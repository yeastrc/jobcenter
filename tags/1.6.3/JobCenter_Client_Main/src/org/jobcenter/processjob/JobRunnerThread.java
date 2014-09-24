package org.jobcenter.processjob;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jobcenter.config.ClientConfigDTO;
import org.jobcenter.config.ConfigFromServer;
import org.jobcenter.constants.JobConstants;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.constants.RunMessageTypesConstants;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.dto.RunMessageDTO;
import org.jobcenter.job_client_module_interface.ModuleInterfaceClientMainInterface;
import org.jobcenter.managerthread.ManagerThread;
import org.jobcenter.module.InactiveModulePool;
import org.jobcenter.module.ModuleHolder;
import org.jobcenter.moduleinterfaceimpl.ModuleInterfaceClientServicesImpl;
import org.jobcenter.moduleinterfaceimpl.ModuleInterfaceModuleJobProgressImpl;
import org.jobcenter.moduleinterfaceimpl.ModuleInterfaceRequestImpl;
import org.jobcenter.moduleinterfaceimpl.ModuleInterfaceModuleResponseImpl;
import org.jobcenter.util.JobToFile;
import org.jobcenter.util.SendJobStatusToServer;

/**
 *
 *
 */
public class JobRunnerThread extends Thread  {

	private static final String className = JobRunnerThread.class.getSimpleName();

	private static Logger log = Logger.getLogger(JobRunnerThread.class);



	private volatile boolean keepRunning = true;




	//  Put here so can be updated when stopRunningAfterProcessingJob() is called

	private volatile ModuleInterfaceClientServicesImpl moduleInterfaceClientServicesImpl = null;


	//  put here so can be accessed when updating status on the server
	private volatile ModuleInterfaceModuleJobProgressImpl moduleInterfaceModuleJobProgressImpl = null;


	private SendJobStatusToServer sendJobStatusToServer = new SendJobStatusToServer();


	/**
	 * "parent" thread
	 */
	private ManagerThread managerThread;


	/**
	 * pass in a dead thread that needs the job status sent to the server.
	 */
	private JobRunnerThread deadThread;


	/**
	 * Job to process
	 */
	private volatile Job job;


	/**
	 * Module to use
	 */
	private volatile ModuleHolder moduleHolder;


	/**
	 * The max number of threads to use to process this job, reset to zero when job is done processing.
	 */
	private volatile int maxNumberThreadsToUseToProcessJob;



	/**
	 * default Constructor
	 */
	public JobRunnerThread() {

		//  Set a name for the thread

		String threadName = className + "-" + JobRunnerThreadPool.getNextJobRunnerThreadCounter();

		setName( threadName );

		init();
	}


	/**
	 * Constructor
	 * @param s
	 */
	public JobRunnerThread( String s ) {

		super(s);

		init();
	}


	/**
	 *
	 */
	private void init() {

		if ( log.isDebugEnabled() ) {
			log.debug( "init() called, thread name = " + this.getName() );
		}


//		ClassLoader thisClassLoader = this.getClass().getClassLoader();
//
//		this.setContextClassLoader( thisClassLoader );

	}


	/**
	 * awaken thread to process request, calls "notify()"
	 */
	public void awaken() {

		if ( log.isDebugEnabled() ) {

			log.debug("awaken() called:  jobRunnerThread.getId() = " + this.getId() + ", jobRunnerThread.getName() = " + this.getName() );
		}

		synchronized (this) {

			notify();
		}

	}



	/**
	 * Called on a different thread.
	 * The ManagerThread instance has detected that the user has requested that the JobCenter client stop retrieving jobs.
	 */
	public void stopRunningAfterProcessingJob() {

		if ( log.isInfoEnabled() ) {

			log.info("stopRunningAfterProcessingJob() called:  jobRunnerThread.getId() = " + this.getId() + ", jobRunnerThread.getName() = " + this.getName() );
		}

		synchronized (this) {

			this.keepRunning = false;

		}

		try {

			if ( moduleInterfaceClientServicesImpl != null ) {


			}



		} catch ( Throwable t ) {


			log.error( "Exception calling jobCenterClientServicesImpl = null;");
		}

		try {


			ModuleHolder localModuleHolder = null;

			synchronized (this) {

				localModuleHolder = moduleHolder;
			}

			if ( localModuleHolder != null ) {

				ModuleInterfaceClientMainInterface module = localModuleHolder.getModule();

				if ( module != null ) {

					if ( log.isInfoEnabled() ) {

						try {
							log.info("stopRunningAfterProcessingJob() called: Calling stopRunningAfterProcessingJob on module.  Module name = " + localModuleHolder.getModuleConfigDTO().getModuleName() );
						} catch (Throwable e) {

						}
					}


					ClassLoader threadCurrentContextClassLoader = Thread.currentThread().getContextClassLoader();


					try {
						//  Set context class loader to class loader for module

						if ( log.isInfoEnabled() ) {

							log.info( "Setting current thread context class loader to class loader for module. moduleToRun.getClass().getClassLoader() = " + module.getClass().getClassLoader() );

							log.info( "Setting current thread context class loader to class loader for module.  module sub directory " + moduleHolder.getModuleConfigDTO().getModuleSubDirectory() );
						}

				        Thread.currentThread().setContextClassLoader( module.getClass().getClassLoader() );

						//  pass shutdown request to the module.  Then can only wait for the module to return to process the results and exit the loop.

						module.stopRunningAfterProcessingJob();


					} finally {

						//  Set context class loader back to class loader it was set to before calling the module

						log.info( "Resetting current thread context class loader back to class loader before calling module.stopRunningAfterProcessingJob()" );

				        Thread.currentThread().setContextClassLoader( threadCurrentContextClassLoader );

					}


				}
			}

			sendJobStatusToServer.stopRunningAfterProcessingJob();

		} catch (Throwable e) {

			log.warn( "In stopRunningAfterProcessingJob(): call to module.stopRunningAfterProcessingJob() threw Throwable " + e.toString(), e );
		}

		//  awaken this thread if it is in 'wait' state ( not currently processing a job )


		awaken();

		//  This object/thread will then run until the current job is complete and then will die.
	}

	/**
	 * shutdown was received from the operating system.  This is called on a different thread.
	 */
	public void shutdown() {


		log.info("shutdown() called:  jobRunnerThread.getId() = " + this.getId() + ", jobRunnerThread.getName() = " + this.getName() );


		synchronized (this) {

			this.keepRunning = false;

		}

		try {


			ModuleHolder localModuleHolder = null;

			synchronized (this) {

				localModuleHolder = moduleHolder;
			}

			if ( localModuleHolder != null ) {

				ModuleInterfaceClientMainInterface module = localModuleHolder.getModule();

				if ( module != null ) {

					if ( log.isInfoEnabled() ) {

						try {
							log.info("shutdown() called: Calling shutdown on module.  Module name = " + localModuleHolder.getModuleConfigDTO().getModuleName() );
						} catch (Throwable e) {

						}
					}


					ClassLoader threadCurrentContextClassLoader = Thread.currentThread().getContextClassLoader();

					try {
						//  Set context class loader to class loader for module


						log.info( "Setting current thread context class loader to class loader for module. moduleToRun.getClass().getClassLoader() = " + module.getClass().getClassLoader() );

						log.info( "Setting current thread context class loader to class loader for module.  module sub directory " + moduleHolder.getModuleConfigDTO().getModuleSubDirectory() );

				        Thread.currentThread().setContextClassLoader( module.getClass().getClassLoader() );


						//  pass shutdown request to the module.  Then can only wait for the module to return to process the results and exit the loop.

						module.shutdown();


					} finally {

						//  Set context class loader back to class loader it was set to before calling the module

						log.info( "Resetting current thread context class loader back to class loader before calling module.shutdown()" );

				        Thread.currentThread().setContextClassLoader( threadCurrentContextClassLoader );

					}


				}
			}

			sendJobStatusToServer.shutdown();


		} catch (Throwable e) {

			log.info( "In shutdown(): call to module.shutdown() threw Throwable " + e.toString(), e );
		}

		//  awaken this thread if it is in 'wait' state ( not currently processing a job )

		this.awaken();
	}



	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {


		if ( deadThread != null ) {

			//  pass dead thread info to server


			try {

				Job jobFromDeadThread = deadThread.getJob();

				if ( jobFromDeadThread != null ) {

					log.error( "Thread died while processing job, Job id = " + job.getId() + ", thread = " + deadThread.getName() );

					//  Update job from dead thread with failed status and send to server

					int moduleRunStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;

					List<RunMessageDTO> runMessages = new ArrayList<RunMessageDTO>();

					RunMessageDTO runMessageDTO = new RunMessageDTO();

					runMessageDTO.setType( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR );

					runMessageDTO.setMessage( "Thread died while processing job" );

					runMessages.add( runMessageDTO );

					jobFromDeadThread.setStatusId( moduleRunStatus );

					RunDTO run = jobFromDeadThread.getCurrentRun();

					run.setEndDate( new Date() );

					run.setStatusId( moduleRunStatus );

					run.setRunMessages( runMessages );

					sendJobStatusToServer.sendJobStatusToServer( jobFromDeadThread );

				}


			} catch ( Throwable t ) {

				log.error( "Exception in sending dead thread job status to server: ", t );
			}
		}


		synchronized (this) {

			try {

				job = null;

				//  Add self to inactive thread pool to wait for first job

				JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().add( this );

				log.debug( "before 'while ( keepRunning )', before wait() called" );

				wait();

				if ( log.isDebugEnabled() ) {

					log.debug("before 'while ( keepRunning )', after wait() called:  jobRunnerThread.getId() = " + this.getId() );
				}


			} catch (InterruptedException e) {

				log.info("wait() interrupted with InterruptedException");

			}
		}



		while ( keepRunning ) {

			try {

				List<RunMessageDTO> runMessages = new ArrayList<RunMessageDTO>();

				Map<String, String> runOutputParams = new HashMap<String, String>();

				int moduleRunStatus = JobStatusValuesConstants.JOB_STATUS_NOT_SET;

				if ( moduleHolder == null ) {

					moduleRunStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;

					RunMessageDTO runMessageDTO = new RunMessageDTO();

					runMessageDTO.setType( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR );

					runMessageDTO.setMessage( "JobCenter Client Module is not loaded" );

					runMessages.add( runMessageDTO );


				} else {

					int jobParamsMapSize = job.getJobParameters().size();

					if ( job.getJobParameterCount() != JobConstants.JOB_PARAMETER_COUNT_NOT_SET
						&& jobParamsMapSize != job.getJobParameterCount() ) {

						//  The number of job parameters passed does not match the value jobParameterCount on the Job object


						moduleRunStatus = JobStatusValuesConstants.JOB_STATUS_SOFT_ERROR;

						RunMessageDTO runMessageDTO = new RunMessageDTO();

						runMessageDTO.setType( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR );

						runMessageDTO.setMessage( "Setting soft error to retry the job.  "
								+ "Number of job parameters passed to client does not match jobParameterCount field."
								+ "  Number of parameters passed = " + jobParamsMapSize
								+ ", jobParameterCount = " + job.getJobParameterCount() + ".");

						runMessages.add( runMessageDTO );



					} else {

						moduleRunStatus = processJob( runMessages, runOutputParams );
					}
				}




				//  Update job with current status

				job.setStatusId(moduleRunStatus);

				RunDTO run = job.getCurrentRun();

				run.setEndDate( new Date() );

				run.setStatusId( moduleRunStatus );

				run.setRunMessages( runMessages );

				run.setRunOutputParams( runOutputParams );

				sendJobStatusToServer.sendJobStatusToServer( job );


				try {

					if ( moduleRunStatus == JobStatusValuesConstants.JOB_STATUS_HARD_ERROR
							|| moduleRunStatus == JobStatusValuesConstants.JOB_STATUS_SOFT_ERROR ) {

						if ( log.isInfoEnabled() ) {
							log.info( "Job failed with hard error or soft error so saving to 'failed_jobs' directory, job id = " + job.getId() + ", run id = " + job.getCurrentRunId() );
						}

						JobToFile.saveJobToFailedJobsDirectory( job );

						JobToFile.cleanFailedJobsDirectoryOfFilesOver30DaysOld();
					}

				} catch ( Throwable t ) {

					// exceptions are logged in called method
				}


				//  TODO  TEMP


//				try {
//
//					List<Job> jobsInDirectory = JobToFile.listJobsInJobsInProgressDirectory();
//
//					int z = 0;
//
//				} catch ( Throwable t ) {
//
//					log.error( "Exception from call to listJobsInJobsInProgressDirectory(): ", t );
//				}



				try {

					JobToFile.deleteJobFromJobsInProgressDirectory( job );

				} catch ( Throwable t ) {

					String jobId = "unknown";

					if ( job != null ) {

						jobId = Integer.toString( job.getId() );
					}

					log.error( "Exception from call to deleteJobFromJobsInProgressDirectory(job): job.id = " + jobId, t );
				}

				job = null;


				synchronized (this) {

					moduleHolder = null;
				}

			} catch ( Throwable t ) {

				log.error( "Exception in run(): ", t );


			}  finally {

				synchronized (this) {

					try {

						job = null;

						if ( keepRunning ) {

							// reset to zero

							maxNumberThreadsToUseToProcessJob = 0;

							//  Add self to inactive thread pool to wait for next job

							JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().add( this );

							ThreadsHolderSingleton.getInstance().getGetJobThread().awaken();

							log.debug( "in 'while ( keepRunning )', before wait() called" );

							wait();


							if ( log.isDebugEnabled() ) {

								log.debug("in 'while ( keepRunning )', after wait() called:  jobRunnerThread.getId() = " + this.getId() );
							}

						}

					} catch (InterruptedException e) {

						log.info("wait() interrupted with InterruptedException", e);

					}
				}


			}

		}



		log.info( "Exiting run()" );
	}


	/**
	 * @param runMessages
	 * @param runOutputParams
	 * @return moduleRunStatus
	 */
	private int processJob( List<RunMessageDTO> runMessages, Map<String, String> runOutputParams ) {


		
		

		//    process the job

		ModuleInterfaceClientMainInterface moduleToRun = moduleHolder.getModule();

		int moduleRunStatus = JobStatusValuesConstants.JOB_STATUS_NOT_SET;

		Map<String, String> parameters = job.getJobParameters();

		ModuleInterfaceRequestImpl moduleInterfaceRequestImpl = new ModuleInterfaceRequestImpl();

		ModuleInterfaceModuleResponseImpl moduleInterfaceModuleResponseImpl = new ModuleInterfaceModuleResponseImpl();

		moduleInterfaceClientServicesImpl = new ModuleInterfaceClientServicesImpl();

		try {

			moduleInterfaceClientServicesImpl.init();

			moduleInterfaceRequestImpl.setNumberOfThreadsForRunningJob( maxNumberThreadsToUseToProcessJob );

			moduleInterfaceRequestImpl.setJobParameters( parameters );

			moduleInterfaceRequestImpl.setRequestId( job.getRequestId() );
			
			moduleInterfaceRequestImpl.setJobRequiredExecutionThreads( job.getRequiredExecutionThreads() );
			
			moduleInterfaceRequestImpl.setJobcenterClientNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );

			moduleInterfaceModuleResponseImpl.setRunOutputParams( runOutputParams );
			moduleInterfaceModuleResponseImpl.setRunMessages( runMessages );

			moduleInterfaceClientServicesImpl.setCurrentJob( job );

			RunDTO run = job.getCurrentRun();

			moduleInterfaceClientServicesImpl.setCurrentRun( run );

			if ( log.isDebugEnabled() ) {

				log.debug("Calling moduleToRun.processRequest():  jobRunnerThread.getId() = " + this.getId() );
			}


			ClassLoader threadCurrentContextClassLoader = Thread.currentThread().getContextClassLoader();


			try {
				//  Set context class loader to class loader for module


				moduleInterfaceModuleJobProgressImpl = new ModuleInterfaceModuleJobProgressImpl();


				log.info( "Setting current thread context class loader to class loader for module. moduleToRun.getClass().getClassLoader() = " + moduleToRun.getClass().getClassLoader() );

				log.info( "Setting current thread context class loader to class loader for module.  module sub directory " + moduleHolder.getModuleConfigDTO().getModuleSubDirectory() );

		        Thread.currentThread().setContextClassLoader( moduleToRun.getClass().getClassLoader() );

				log.info( "In JobRunnerThread, about to call moduleToRun.processRequest(...): Current class loader: "
						+ this.getClass().getClassLoader()
						+ "; this.getContextClassLoader(): " + this.getContextClassLoader() );

				moduleToRun.processRequest( moduleInterfaceRequestImpl, moduleInterfaceModuleResponseImpl, moduleInterfaceModuleJobProgressImpl, moduleInterfaceClientServicesImpl );

			} finally {

				//  Set context class loader back to class loader it was set to before calling the module

				log.info( "Resetting current thread context class loader back to class loader after calling module.processRequest(...)" );

		        Thread.currentThread().setContextClassLoader( threadCurrentContextClassLoader );

		        //  clear the In progress tracking
		        moduleInterfaceModuleJobProgressImpl = null;
			}


			moduleRunStatus = moduleInterfaceModuleResponseImpl.getStatusCode();

		} catch ( Throwable t ) {

			log.error( "Exception in call to moduleToRun.processRequest(): Module name: '" + moduleHolder.getModuleConfigDTO().getModuleName()
					+ "', Module subdirectory: '" + moduleHolder.getModuleConfigDTO().getModuleSubDirectory()
					+ "', Module version: " + moduleHolder.getModuleConfigDTO().getModuleVersion()
					+ ", Exception:" +  t.toString(), t );

			moduleRunStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;

			RunMessageDTO runMessageDTO = new RunMessageDTO();

			runMessageDTO.setType( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR );

			runMessageDTO.setMessage( "Module threw exception, Exception type is '" + t.getClass().getName()
					+ "', exception string is: " + t.toString() );

			runMessages.add( runMessageDTO );

		} finally {

			try {

				moduleInterfaceClientServicesImpl.destroy();

			} catch ( Throwable t ) {

				log.error( "Exception in call to jobCenterClientServicesImpl.destroy()"
						+ ", Exception:" +  t.toString(), t );
			}

			//  Must do since doesn't go out of scope
			moduleInterfaceClientServicesImpl = null;
		}



		//   TODO  consider destroying module if had hard errors or if module requests reload for each request


		try {

			ClassLoader threadCurrentContextClassLoader = Thread.currentThread().getContextClassLoader();


			try {
				//  Set context class loader to class loader for module


				log.info( "Setting current thread context class loader to class loader for module. moduleToRun.getClass().getClassLoader() = " + moduleToRun.getClass().getClassLoader() );

				log.info( "Setting current thread context class loader to class loader for module.  module sub directory " + moduleHolder.getModuleConfigDTO().getModuleSubDirectory() );

		        Thread.currentThread().setContextClassLoader( moduleToRun.getClass().getClassLoader() );

				moduleToRun.reset();

			} finally {

				//  Set context class loader back to class loader before the module was called

				log.info( "Resetting current thread context class loader back to class loader before calling module.reset()" );

		        Thread.currentThread().setContextClassLoader( threadCurrentContextClassLoader );

			}


			//    Park module

			InactiveModulePool.getInstance().addModuleHolder( moduleHolder );

		} catch ( Throwable t ) {


			log.error( "Exception in call to moduleToRun.reset(): Module name: '" + moduleHolder.getModuleConfigDTO().getModuleName()
					+ "', Module subdirectory: '" + moduleHolder.getModuleConfigDTO().getModuleSubDirectory()
					+ "', Module version: " + moduleHolder.getModuleConfigDTO().getModuleVersion()
					+ ", Exception:" + t.toString(), t );

			//  try to destroy module since it failed to reset.

			ClassLoader threadCurrentContextClassLoader = Thread.currentThread().getContextClassLoader();

			try {

				log.info( "Setting current thread context class loader to class loader for module. moduleToRun.getClass().getClassLoader() = " + moduleToRun.getClass().getClassLoader() );

				log.info( "Setting current thread context class loader to class loader for module.  module sub directory " + moduleHolder.getModuleConfigDTO().getModuleSubDirectory() );

		        Thread.currentThread().setContextClassLoader( moduleToRun.getClass().getClassLoader() );

				moduleToRun.destroy();

			} catch ( Throwable tt ) {

				log.error( "Exception in call to moduleToRun.destroy() [ called after moduleToRun.reset() returned exception ]: Module name: '" + moduleHolder.getModuleConfigDTO().getModuleName()
					+ "', Module subdirectory: '" + moduleHolder.getModuleConfigDTO().getModuleSubDirectory()
					+ "', Module version: " + moduleHolder.getModuleConfigDTO().getModuleVersion()
					+ ", Exception:" + tt.toString(), tt );

			} finally {
				//  Set context class loader back to class loader before the module was called

				log.info( "Resetting current thread context class loader back to class loader before calling module.destroy()" );

				Thread.currentThread().setContextClassLoader( threadCurrentContextClassLoader );

			}
		}

		return moduleRunStatus;
	}





	public boolean isKeepRunning() {
		return keepRunning;
	}

	public void setKeepRunning(boolean keepRunning) {
		this.keepRunning = keepRunning;
	}


	public JobRunnerThread getDeadThread() {
		return deadThread;
	}


	public void setDeadThread(JobRunnerThread deadThread) {
		this.deadThread = deadThread;
	}


	public Job getJob() {
		return job;
	}


	public void setJob(Job job) {
		this.job = job;
	}

	public int getMaxNumberThreadsToUseToProcessJob() {
		return maxNumberThreadsToUseToProcessJob;
	}

	public void setMaxNumberThreadsToUseToProcessJob(
			int maxNumberThreadsToUseToProcessJob) {
		this.maxNumberThreadsToUseToProcessJob = maxNumberThreadsToUseToProcessJob;
	}


	public ManagerThread getManagerThread() {
		return managerThread;
	}


	public void setManagerThread(ManagerThread managerThread) {
		this.managerThread = managerThread;
	}


	public ModuleHolder getModuleHolder() {
		return moduleHolder;
	}


	/**
	 * synchronized to coordinate setting and checking this field
	 * @param moduleHolder
	 */
	public synchronized void setModuleHolder(ModuleHolder moduleHolder) {
		this.moduleHolder = moduleHolder;
	}


	public ModuleInterfaceModuleJobProgressImpl getModuleInterfaceModuleJobProgressImpl() {
		return moduleInterfaceModuleJobProgressImpl;
	}


}
