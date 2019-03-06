package org.jobcenter.processjob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.client.timecontrol.TimeControl;
import org.jobcenter.config.ClientConfigDTO;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.constants.RunMessageTypesConstants;
import org.jobcenter.constants.WaitTimesForLoggingAndInterruptConstants;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.JobType;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.dto.RunMessageDTO;
import org.jobcenter.managerthread.ManagerThread;
import org.jobcenter.module.ModuleConfigDTO;
import org.jobcenter.module.ModuleFactory;
import org.jobcenter.module.ModuleHolder;
import org.jobcenter.request.UpdateServerFromJobRunOnClientRequest;
import org.jobcenter.response.BaseResponse;
import org.jobcenter.response.JobResponse;
import org.jobcenter.response.UpdateServerFromJobRunOnClientResponse;
import org.jobcenter.serverinterface.ServerConnection;
import org.jobcenter.util.JobToFile;
import org.jobcenter.util.LogOpenFiles;
import org.jobcenter.util.MarkJobsInProgressAsFailed;

/**
 *  Thread for getting jobs.  Only one thread per client
 *
 *  Get jobs from server, put the job on a worker thread, and activate the worker thread to process the request.
 *
 */
public class GetJobThread extends Thread  {

	private static final String className = GetJobThread.class.getSimpleName();

	private static Logger log = Logger.getLogger(GetJobThread.class);


	private static final boolean WAIT_BECAUSE_NO_WORKER_THREADS_TRUE = true;

	private static final boolean WAIT_BECAUSE_NO_WORKER_THREADS_FALSE = false;

	/**
	 * "parent" thread
	 */
	private ManagerThread managerThread;

	private volatile MarkJobsInProgressAsFailed markJobsInProgressAsFailed = null;


	private volatile boolean keepRunning = true;



	private volatile boolean exittedWaitDueToAwakenNotify = false;
	
	
	private final long currentTimeMillisBeforeCallGetJob_NOTSET = 0;
	
	/**
	 * Set right before GetJob is called and then cleared after
	 */
	private volatile long currentTimeMillisBeforeCallGetJob = currentTimeMillisBeforeCallGetJob_NOTSET;


	/**
	 * This is the size of the inactiveThreadPool at the time the client
	 * last looked for jobs to run.
	 *
	 * If this gets larger before the get job thread goes to sleep,
	 * the get job thread should not sleep but immediately check if it
	 * can get a job to run.
	 */
	private int inactiveJobRunnersCountAtGetJobSearchTime = 0;




	/**
	 * default Constructor
	 */
	public GetJobThread() {


		//  Set a name for the thread

		String threadName = className;

		setName( threadName );

		init();
	}


	/**
	 * Constructor
	 * @param s
	 */
	public GetJobThread( String s ) {

		super(s);

		init();
	}


	/**
	 *
	 */
	private void init() {

//		ClassLoader thisClassLoader = this.getClass().getClassLoader();
//
//		this.setContextClassLoader( thisClassLoader );

	}


	/**
	 * awaken thread to get next job or to complete
	 */
	public void awaken() {

		synchronized (this) {

			exittedWaitDueToAwakenNotify = true;

			notify();
		}

	}
	
	
	
	/**
	 * Call From Manager Thread whenever it wakes up
	 */
	public void checkupFromManagerThread() {
		interruptIfGetJobTimeExceeded();
	}
	
	/**
	 * Interrupt this thread if Get Job time Exceeded
	 */
	private void interruptIfGetJobTimeExceeded() {
		
		if ( currentTimeMillisBeforeCallGetJob == currentTimeMillisBeforeCallGetJob_NOTSET ) {
			// Not currently in get next job so just exit
			return;  //  EARLY EXIT
		}
		long currentTimeMillis = System.currentTimeMillis();
		long currentTimeMillisDiff = currentTimeMillis - currentTimeMillisBeforeCallGetJob;
		
		if ( currentTimeMillisDiff > WaitTimesForLoggingAndInterruptConstants.CLIENT_WAIT_TO_INTERRUPT_GET_NEXT_JOB_THREAD ) {
			String msg = "Current time minus time before call GetJob exceeds allowed time, calling interrupt on this thread."
					+ "  currentTimeMillis: " + currentTimeMillis + ", currentTimeMillisBeforeCallGetJob: " + currentTimeMillisBeforeCallGetJob
					+ ", currentTimeMillisDiff: " + currentTimeMillisDiff
					+ ", difference limit: " + WaitTimesForLoggingAndInterruptConstants.CLIENT_WAIT_TO_INTERRUPT_GET_NEXT_JOB_THREAD;
			log.error( msg );
			this.interrupt();
		}
	}




	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		log.info( "run() entered" );

		//  extra task for this thread on startup:


		markJobsInProgressAsFailed();




		long loopCounter = 0;

		while ( keepRunning ) {

			LogOpenFiles.logOpenFiles( LogOpenFiles.LIST_FILES_TRUE );

			if ( loopCounter >= Integer.MAX_VALUE ) {

				loopCounter = 0;
			}

			loopCounter++;

			try {

				inactiveJobRunnersCountAtGetJobSearchTime = JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().size();

				if ( JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().isEmpty() ) {

					//  If no worker threads available

					if ( log.isInfoEnabled() ) {

						log.info( "no worker threads available so sleeping, loopCounter = " + loopCounter );
					}

					waitForSleepTime( WAIT_BECAUSE_NO_WORKER_THREADS_TRUE );


				} else  {

					int availableThreadCount = getAvailableThreadCount( );

					if ( availableThreadCount <= 0 ) {

						//  If no worker threads available

						if ( log.isInfoEnabled() ) {

							log.info( "availableThreadCount <= 0 so sleeping , loopCounter = " + loopCounter );
						}

						waitForSleepTime( WAIT_BECAUSE_NO_WORKER_THREADS_TRUE );


					} else {
						
						try {
							int availableJobCount = getAvailableJobCount();

							getJob( availableThreadCount, availableJobCount );
						} finally {

							currentTimeMillisBeforeCallGetJob = currentTimeMillisBeforeCallGetJob_NOTSET;
						}
					}
				}


			} catch ( Throwable t ) {

				log.error( "Exception in run(): ", t );


				//  TODO   handle and/or log exception


			}
		}


		log.info( "Exiting run()" );
	}


	/**
	 * @return
	 */
	private int getAvailableThreadCount( ) {

		int threadInUseCount = 0;

		List<JobRunnerThread> jobRunnerThreads = ThreadsHolderSingleton.getInstance().getJobRunnerThreads();

		for ( JobRunnerThread jobRunnerThread : jobRunnerThreads ) {

			int jobRunnerThreadMaxNumberThreadsToUseToProcessJob = jobRunnerThread.getMaxNumberThreadsToUseToProcessJob();

			threadInUseCount += jobRunnerThreadMaxNumberThreadsToUseToProcessJob;
		}

		ClientConfigDTO clientConfigDTO = ClientConfigDTO.getSingletonInstance();  //  retrieve singleton

		int maxThreadsForModules = clientConfigDTO.getMaxThreadsForModules();

		int availableThreadCount = maxThreadsForModules - threadInUseCount;

		return availableThreadCount;
	}
	
	
	/**
	 * @return
	 */
	private int getAvailableJobCount( ) {

		int jobsInUseCount = 0;

		List<JobRunnerThread> jobRunnerThreads = ThreadsHolderSingleton.getInstance().getJobRunnerThreads();

		for ( JobRunnerThread jobRunnerThread : jobRunnerThreads ) {

			Job job = jobRunnerThread.getJob();

			if ( job != null ) {
				jobsInUseCount += 1;
			}
		}

		ClientConfigDTO clientConfigDTO = ClientConfigDTO.getSingletonInstance();  //  retrieve singleton

		int maxJobsForModules = clientConfigDTO.getMaxConcurrentJobs();

		int availableJobsCount = maxJobsForModules - jobsInUseCount;

		return availableJobsCount;
	}


	/**
	 *
	 */
	private void getJob( int availableThreadCount, int availableJobCount ) throws Throwable {

		log.info( "getJob(): getting a job" );

		currentTimeMillisBeforeCallGetJob = System.currentTimeMillis();

		//  get job

		JobResponse jobResponse = null;

		try {
			jobResponse = GetJob.getInstance().getNextJob( availableThreadCount, availableJobCount );

		} catch ( Throwable t ) {

			log.error( "Exception in getJob(), calling GetJob.getInstance().getNextJob( ), setting jobResponse to null, Exception: " + t.toString(), t );

			jobResponse = null;

		} finally {

			long currentTimeMillis = System.currentTimeMillis();
			long currentTimeMillisDiff = currentTimeMillis - currentTimeMillisBeforeCallGetJob;
			
			try {
				if ( currentTimeMillisDiff > WaitTimesForLoggingAndInterruptConstants.CLIENT_WAIT_TO_INTERRUPT_GET_NEXT_JOB_THREAD ) {
					String msg = "Time taken to call GetJob.getInstance().getNextJob(...) exceeded allowed time."
							+ "  currentTimeMillis: " + currentTimeMillis + ", currentTimeMillisBeforeCallGetJob: " + currentTimeMillisBeforeCallGetJob
							+ ", currentTimeMillisDiff: " + currentTimeMillisDiff
							+ ", difference limit: " + WaitTimesForLoggingAndInterruptConstants.CLIENT_WAIT_TO_INTERRUPT_GET_NEXT_JOB_THREAD;
					log.warn( msg );
				}
			} catch ( Throwable t ) {
				log.error( "Error tracking time to call GetJob.getInstance().getNextJob(...).", t );
			}
			
			currentTimeMillisBeforeCallGetJob = currentTimeMillisBeforeCallGetJob_NOTSET;
		}

		if ( jobResponse == null ) {

			log.error( "GetJobThread : getJob() Error:  'jobResponse == null'." );

			waitForSleepTime( WAIT_BECAUSE_NO_WORKER_THREADS_FALSE );

		} else if ( jobResponse.isErrorResponse() || jobResponse.getErrorCode() != BaseResponse.ERROR_CODE_NO_ERRORS ) {

			log.error( "GetJobThread : run() Error:  'jobResponse.isErrorResponse() || jobResponse.getErrorCode() != BaseResponse.ERROR_CODE_NO_ERRORS':  Error code = " + jobResponse.getErrorCode()
					+  " Error description = " + jobResponse.getErrorCodeDescription() + "." );

			waitForSleepTime( WAIT_BECAUSE_NO_WORKER_THREADS_FALSE );

		} else if ( ! jobResponse.isJobFound() ) {
			
			if ( jobResponse.isNextJobRequiresMoreThreads() ) {
				
				log.info( "getJob(): no job found. Next job requires more threads than what is available.  Go to sleep." );
				
			} else {

				log.info( "getJob(): no job found. Go to sleep." );
			}

			waitForSleepTime( WAIT_BECAUSE_NO_WORKER_THREADS_FALSE );

		} else {

			processJobResponse( jobResponse, availableThreadCount );
		}
	}


	/**
	 * @param jobResponse
	 * @param jobRunnerThread
	 * @throws Throwable
	 */
	private void processJobResponse( JobResponse jobResponse, int availableThreadCount ) throws Throwable {

		Job job = jobResponse.getJob();

		if ( job == null ) {

			waitForSleepTime( WAIT_BECAUSE_NO_WORKER_THREADS_FALSE );

		} else {

			//  if found job, put job on thread and awaken thread to process it.

			try {

				JobToFile.saveJobToJobsInProgressDirectory( job );

			} catch ( Throwable t ) {


				log.error( "Exception in saving job to Jobs In Progress Directory error: " + t.toString(), t );

				sendJobResponseOnError( job, "", t );
				
				throw t;
			}

			String moduleName = "";

			if ( job != null && job.getJobType() != null && job.getJobType().getModuleName() != null ) {

				moduleName = job.getJobType().getModuleName();
			}


			ModuleHolder moduleHolder = null;

			try {
				moduleHolder = ModuleFactory.getInstance().getModuleForJob( job );

			} catch ( Throwable t ) {


				log.error( "Exception in loading module error: " + t.toString(), t );

				sendJobResponseOnError( job, moduleName, t );
			}

			if ( moduleHolder != null ) {

				try {

					processJob( job, moduleHolder, availableThreadCount );

				} catch ( Throwable t ) {


					log.error( "Exception in processJob(), trying to get moduleHolder: moduleName on job = " + moduleName, t );


					sendJobResponseOnError( job, moduleName, t );


					//			throw t;

				} finally {




				}
			}
		}
	}



	/**
	 * @param job
	 * @param moduleName
	 * @param jobRunnerThread
	 * @throws Throwable
	 */
	private void processJob( Job job, ModuleHolder moduleHolder, int availableThreadCount  ) throws Throwable {


		JobRunnerThread jobRunnerThread = JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().poll();

		if ( jobRunnerThread == null ) {

			String msg = "processJob( ... ):  jobRunnerThread == null, should not have gotten here if there were no entries in JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance()";

			log.error( msg );

			throw new Exception( msg );
		}
		
		
		JobType jobTypeForThisJob = job.getJobType();
		
		if ( job.getRequiredExecutionThreads() != null && 
				job.getRequiredExecutionThreads() > availableThreadCount ) {
			

			String msg = "processJob( ... ):  jobTypeForThisJob.getRequiredExecutionThreads() > availableThreadCount."
					+ "  job id: " + job.getId() + ", job type id: " + jobTypeForThisJob.getId() ;

			log.error( msg );

			throw new Exception( msg );
		}
		

		int maxNumberThreadsToUseToProcessJob = -1;
		
		if ( job.getRequiredExecutionThreads() != null ) {

			//  If required Execution threads is provided, use that
			
			maxNumberThreadsToUseToProcessJob = job.getRequiredExecutionThreads();
			
		} else {
			
			//  initially set to available thread count
			maxNumberThreadsToUseToProcessJob = availableThreadCount;			

			ModuleConfigDTO moduleConfigDTO = moduleHolder.getModuleConfigDTO();

			boolean moduleMaxNumberThreadsPerJobSet = moduleConfigDTO.isMaxNumberThreadsPerJobSet();

			int moduleMaxNumberThreadsPerJob = moduleConfigDTO.getMaxNumberThreadsPerJob();

			if ( moduleMaxNumberThreadsPerJobSet && moduleMaxNumberThreadsPerJob < maxNumberThreadsToUseToProcessJob ) {

				//  "module max threads per job" is set and is < current maxNumberThreadsToUseToProcessJob (availableThreadCount)
				//     so lower maxNumberThreadsToUseToProcessJob to "module max threads per job"
				
				maxNumberThreadsToUseToProcessJob = moduleMaxNumberThreadsPerJob;

				if ( log.isDebugEnabled() ) {

					log.debug( "Using max number of threads per job from the module, = " + moduleMaxNumberThreadsPerJob );
				}
			}

			if ( log.isDebugEnabled() ) {

				log.debug( "maxNumberThreadsToUseToProcessJob = " + maxNumberThreadsToUseToProcessJob );
			}
			
		}

		jobRunnerThread.setMaxNumberThreadsToUseToProcessJob( maxNumberThreadsToUseToProcessJob );
		
		jobRunnerThread.setModuleHolder( moduleHolder );

		try {

			jobRunnerThread.setJob( job );

			if ( log.isDebugEnabled() ) {

				log.debug("Calling jobRunnerThread.awaken():  jobRunnerThread.getId() = " + jobRunnerThread.getId()
						+ ", jobRunnerThread.getName() = " + jobRunnerThread.getName() );
			}

			jobRunnerThread.awaken();

		} catch ( IllegalMonitorStateException ex ) {

			//  This exception is thrown if this thread does not have the proper lock on the monitor when "notify()" is called.
			//            This is really only a logic error and should never occur.

			log.error( "Exception in processJob(), trying to call jobRunnerThread.awaken(), which calls notify() : ", ex );

			throw ex;

			//  TODO  properly handle this
		}
	}

	/**
	 * @param job
	 * @param moduleName
	 * @param t
	 */
	private void sendJobResponseOnError( Job job, String moduleName, Throwable t )  {


		List<RunMessageDTO> runMessages = new ArrayList<RunMessageDTO>();

		int moduleRunStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;

		RunMessageDTO runMessageDTO = new RunMessageDTO();

		runMessageDTO.setType( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR );

		runMessageDTO.setMessage( "Loading Module threw exception, Exception type is '" + t.getClass().getName()
				+ "', exception string is: " + t.toString() );

		runMessages.add( runMessageDTO );


		//  Update job with current status

		job.setStatusId(moduleRunStatus);

		RunDTO run = job.getCurrentRun();

		run.setEndDate( new Date() );

		run.setStatusId( moduleRunStatus );

		run.setRunMessages( runMessages );

		try {
			sendJobStatusToServer( job);

		} catch (Throwable e) {


			log.error( "Exception in sending error response to server due to loading module error: " + e.toString(), e );
		}



	}


	/**
	 * @param moduleRunStatus
	 * @param moduleCompletionMessage
	 * @throws Throwable
	 */
	private void sendJobStatusToServer( Job job )
	throws Throwable {

		UpdateServerFromJobRunOnClientRequest updateServerFromJobRunOnClientRequest = new UpdateServerFromJobRunOnClientRequest();

		updateServerFromJobRunOnClientRequest.setJob( job );

		updateServerFromJobRunOnClientRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );

		UpdateServerFromJobRunOnClientResponse response = ServerConnection.getInstance().updateServerFromJobRunOnClient( updateServerFromJobRunOnClientRequest );

		if ( response.isErrorResponse() ) {

			String msg =  "Update of job status to server failed.";

			if ( job != null ) {

				msg += "  Job id = " + job.getId();

				if ( job.getCurrentRun() != null ) {

					msg += ".  run id = " + job.getCurrentRun().getId();
				} else {

					msg += ".  run is null ";
				}
			} else {

				msg += "  job reference is null.";
			}

			log.error( msg );


			throw new Exception( msg );
		}
	}




	/**
	 * @param waitBecauseNoWorkerThreads - Was this wait because there are no worker threads
	 *
	 */
	private void waitForSleepTime( boolean waitBecauseNoWorkerThreads ) {

		if ( log.isDebugEnabled() ) {
			log.debug( "waitForSleepTime() entered, waitBecauseNoWorkerThreads " + waitBecauseNoWorkerThreads );
		}

//		int waitTimeInSeconds = ClientConfigDTO.getSingletonInstance().getSleepTimeCheckingForNewJobs();

		int waitTimeInSeconds = 0;

		boolean returnFromFunction = false;



		while ( keepRunning && ( ! returnFromFunction )  ) {

			boolean inATimeWhenCanRetrieveJobs = TimeControl.inATimeWhenCanRetrieveJobs();

			waitTimeInSeconds = TimeControl.timeUntilCanRetrieveJob();

			synchronized (this) {

				try {

					//  If waiting due to no worker threads or no available thread count,
					//  make one last check here for worker threads before waiting

					if ( inATimeWhenCanRetrieveJobs
							&& waitBecauseNoWorkerThreads
							&& ( JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().size() > inactiveJobRunnersCountAtGetJobSearchTime ) )
					{

						//  The number of inactive threads went up so loop around and check for a job

						log.info( "waitForSleepTime(): waitBecauseNoWorkerThreads == true and The number of inactive threads went up so loop around and check for a job. " );

					} else {

						if ( waitBecauseNoWorkerThreads ) {

							int waitBecauseNoWorkerThreadsTimeInSeconds = ClientConfigDTO.getSingletonInstance().getSleepTimeCheckingForNewJobsNoWorkerThreads();

							if ( waitBecauseNoWorkerThreadsTimeInSeconds > waitTimeInSeconds ) {

								waitTimeInSeconds = waitBecauseNoWorkerThreadsTimeInSeconds;
							}

						}

						if ( log.isInfoEnabled() ) {
							log.info( "waitForSleepTime():  Get Job waiting ( calling wait(...) ) for " + waitTimeInSeconds + " seconds." );
						}

						exittedWaitDueToAwakenNotify = false;

						wait( ( (long) waitTimeInSeconds ) * 1000 ); //  wait for notify() call or timeout, in milliseconds

					}

				} catch (InterruptedException e) {

					log.info("waitForSleepTime():  wait() interrupted with InterruptedException");

				}
			}

			if ( exittedWaitDueToAwakenNotify ) {

				if ( waitBecauseNoWorkerThreads ) {

					log.info("waitForSleepTime():  wait(...) exitted due to notify() call.  Was waiting because of no worker threads.");

				} else {

					log.info("waitForSleepTime():  wait(...) exitted due to notify() call.  Was NOT waiting because of no worker threads.");
				}

			} else {

				if ( waitBecauseNoWorkerThreads ) {

					log.info("waitForSleepTime():  wait(...) exitted NOT due to notify() call.  Was waiting because of no worker threads.");

				} else {

					log.info("waitForSleepTime():  wait(...) exitted NOT due to notify() call.  Was NOT waiting because of no worker threads.");
				}
			}

			if ( log.isInfoEnabled() ) {
				log.info( "waitForSleepTime():  wait() call exited, was:  waitBecauseNoWorkerThreads = " + waitBecauseNoWorkerThreads + ", waitTimeInSeconds = " + waitTimeInSeconds + ", keepRunning = " + keepRunning );
			}

			if ( ! keepRunning ) {

				returnFromFunction = true;

			} else {

				if ( TimeControl.inATimeWhenCanRetrieveJobs() ) {

					returnFromFunction = true;
				} else {

					log.info( "waitForSleepTime(): TimeControl.inATimeWhenCanRetrieveJobs() returned false so performing another wait" );
				}
			}
		}

		if ( log.isInfoEnabled() ) {
			log.info( "waitForSleepTime() exitted, waitBecauseNoWorkerThreads = " + waitBecauseNoWorkerThreads + ", waitTimeInSeconds = " + waitTimeInSeconds + ", keepRunning = " + keepRunning );
		}

	}



	/**
	 * run on startup.  calls MarkJobsInProgressAsFailed.markJobsInProgressAsFailed()
	 */
	private void markJobsInProgressAsFailed() {

		markJobsInProgressAsFailed = new MarkJobsInProgressAsFailed();

		markJobsInProgressAsFailed.markJobsInProgressAsFailed();


		markJobsInProgressAsFailed = null;
	}





	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {


		log.warn( "shutdown() called, setting keepRunning = false, calling awaken() " );

		keepRunning = false;

		synchronized ( this ) {

			if ( markJobsInProgressAsFailed != null ) {

				markJobsInProgressAsFailed.shutdown();
			}
		}

		awaken();

		log.warn( "Exiting shutdown()" );
	}


	public boolean isKeepRunning() {
		return keepRunning;
	}



	public ManagerThread getManagerThread() {
		return managerThread;
	}


	public void setManagerThread(ManagerThread managerThread) {
		this.managerThread = managerThread;
	}


}
