package org.jobcenter.processjob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import org.jobcenter.client.timecontrol.TimeControl;
import org.jobcenter.config.ClientConfigDTO;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.constants.RunMessageTypesConstants;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.dto.RunMessageDTO;
import org.jobcenter.managerthread.ManagerThread;
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
import org.jobcenter.util.SendJobStatusToServer;

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

				JobRunnerThread jobRunnerThread  = JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().poll();

				if ( jobRunnerThread == null ) {


					//  If no worker threads available

					log.info( "no worker threads available, jobRunnerThread == null so sleeping, loopCounter = " + loopCounter );

					waitForSleepTime( WAIT_BECAUSE_NO_WORKER_THREADS_TRUE );


				} else  {

					getJob( jobRunnerThread );
				}


			} catch ( Throwable t ) {

				log.error( "Exception in run(): ", t );


				//  TODO   handle and/or log exception


			}
		}


		log.info( "Exiting run()" );
	}




	/**
	 *
	 */
	private void getJob( JobRunnerThread jobRunnerThread ) throws Throwable {

		log.info( "getJob(): getting a job" );


		//  get job

		JobResponse jobResponse = null;

		try {
			jobResponse = GetJob.getInstance().getNextJob( );

		} catch ( Throwable t ) {

			log.error( "Exception in getJob(), calling GetJob.getInstance().getNextJob( ), setting jobResponse to null, Exception: " + t.toString(), t );

			jobResponse = null;
		}

		if ( jobResponse == null ) {


			//    Problem getting a job
			//            put worker thread back on inactive queue

			JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().add( jobRunnerThread );

			log.error( "GetJobThread : run() Error:  'jobResponse == null'." );

			waitForSleepTime( WAIT_BECAUSE_NO_WORKER_THREADS_FALSE );

		} else if ( jobResponse.isErrorResponse() || jobResponse.getErrorCode() != BaseResponse.ERROR_CODE_NO_ERRORS ) {


			//    Problem getting a job
			//            put worker thread back on inactive queue

			JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().add( jobRunnerThread );

			log.error( "GetJobThread : run() Error:  'jobResponse.isErrorResponse() || jobResponse.getErrorCode() != BaseResponse.ERROR_CODE_NO_ERRORS':  Error code = " + jobResponse.getErrorCode()
					+  " Error description = " + jobResponse.getErrorCodeDescription() + "." );

			waitForSleepTime( WAIT_BECAUSE_NO_WORKER_THREADS_FALSE );

		} else if ( ! jobResponse.isJobFound() ) {

			log.info( "getJob(): no job found, put worker thread back on inactive queue and go to sleep" );

			//    if no job found
			//            put worker thread back on inactive queue

			JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().add( jobRunnerThread );

			waitForSleepTime( WAIT_BECAUSE_NO_WORKER_THREADS_FALSE );

		} else {

			processJobResponse( jobResponse, jobRunnerThread );
		}
	}



	/**
	 * @param jobResponse
	 * @param jobRunnerThread
	 * @throws Throwable
	 */
	private void processJobResponse( JobResponse jobResponse, JobRunnerThread jobRunnerThread ) throws Throwable {

		Job job = jobResponse.getJob();

		if ( job == null ) {

			//    Problem getting a job
			//            put worker thread back on inactive queue

			JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().add( jobRunnerThread );

			waitForSleepTime( WAIT_BECAUSE_NO_WORKER_THREADS_FALSE );

		} else {

			//  if found job, put job on thread and awaken thread to process it.

			JobToFile.saveJobToJobsInProgressDirectory( job );


			String moduleName = "";

			if ( job != null && job.getJobType() != null && job.getJobType().getModuleName() != null ) {

				moduleName = job.getJobType().getModuleName();
			}


			try {

				 processJob( job, moduleName, jobRunnerThread );

			} catch ( Throwable t ) {


				log.error( "Exception in processJobResponse(), trying to get moduleHolder: moduleName on job = " + moduleName, t );


				//			ModuleHolder moduleHolder = jobRunnerThread.getModuleHolder();
				//
				//			if ( moduleHolder == null ) {

				jobRunnerThread.setModuleHolder( null );

				jobRunnerThread.setJob( null );

				JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().add( jobRunnerThread );
				//			}

				//			throw t;

			} finally {




			}
		}
	}




	/**
	 * @param job
	 * @param moduleName
	 * @param jobRunnerThread
	 * @throws Throwable
	 */
	private void processJob( Job job, String moduleName, JobRunnerThread jobRunnerThread ) throws Throwable {

		jobRunnerThread.setModuleHolder( null ); // clear out moduleHolder

		ModuleHolder moduleHolder = null;

		try {
			moduleHolder = ModuleFactory.getInstance().getModuleForJob( job );

			jobRunnerThread.setModuleHolder( moduleHolder );


		} catch ( Throwable t ) {


			log.error( "Exception in loading module error: " + t.toString(), t );

			sendJobResponseOnError( job, moduleName, t );


			jobRunnerThread.setModuleHolder( null );

			jobRunnerThread.setJob( null );

			JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().add( jobRunnerThread );
		}

		try {

			if ( moduleHolder != null ) {

				if ( log.isDebugEnabled() ) {

					log.debug("Calling jobRunnerThread.awaken():  jobRunnerThread.getId() = " + jobRunnerThread.getId()
							+ ", jobRunnerThread.getName() = " + jobRunnerThread.getName() );
				}

				jobRunnerThread.setJob( job );

				jobRunnerThread.awaken();
			}

		} catch ( IllegalMonitorStateException ex ) {

			//  This exception is thrown if this thread does not have the proper lock on the monitor when "notify()" is called.
			//            This is really only a logic error and should never occur.

			log.error( "Exception in processJob(), trying to call jobRunnerThread.awaken(), which calls notify() : ", ex );


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

		log.debug( "waitForSleepTime() entered, waitBecauseNoWorkerThreads " + waitBecauseNoWorkerThreads );

//		int waitTimeInSeconds = ClientConfigDTO.getSingletonInstance().getSleepTimeCheckingForNewJobs();

		int waitTimeInSeconds = 0;

		boolean returnFromFunction = false;



		while ( keepRunning && ( ! returnFromFunction )  ) {

			boolean inATimeWhenCanRetrieveJobs = TimeControl.inATimeWhenCanRetrieveJobs();

			waitTimeInSeconds = TimeControl.timeUntilCanRetrieveJob();

			synchronized (this) {

				try {

					//  If waiting due to no worker threads, make one last check here for worker threads before waiting

					if ( inATimeWhenCanRetrieveJobs && waitBecauseNoWorkerThreads && ( ! JobRunnerThreadPool.getInactiveJobRunnerThreadsInstance().isEmpty() ) ) {

						//  A worker thread is now found so don't wait but rather loop around and go get it

						log.info( "waitForSleepTime(): waitBecauseNoWorkerThreads == true and now there is a thread available so not waiting. " );

					} else {

						if ( waitBecauseNoWorkerThreads ) {

							int waitBecauseNoWorkerThreadsTimeInSeconds = ClientConfigDTO.getSingletonInstance().getSleepTimeCheckingForNewJobsNoWorkerThreads();

							if ( waitBecauseNoWorkerThreadsTimeInSeconds > waitTimeInSeconds ) {

								waitTimeInSeconds = waitBecauseNoWorkerThreadsTimeInSeconds;
							}

						}

						log.info( "waitForSleepTime():  Get Job waiting ( calling wait(...) ) for " + waitTimeInSeconds + " seconds." );

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


			log.info( "waitForSleepTime():  wait() call exited, was:  waitBecauseNoWorkerThreads = " + waitBecauseNoWorkerThreads + ", waitTimeInSeconds = " + waitTimeInSeconds + ", keepRunning = " + keepRunning );

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

		log.info( "waitForSleepTime() exitted, waitBecauseNoWorkerThreads = " + waitBecauseNoWorkerThreads + ", waitTimeInSeconds = " + waitTimeInSeconds + ", keepRunning = " + keepRunning );

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


		log.info( "shutdown() called, setting keepRunning = false, calling awaken() " );

		keepRunning = false;

		synchronized ( this ) {

			if ( markJobsInProgressAsFailed != null ) {

				markJobsInProgressAsFailed.shutdown();
			}
		}

		awaken();

		log.info( "Exiting shutdown()" );
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
