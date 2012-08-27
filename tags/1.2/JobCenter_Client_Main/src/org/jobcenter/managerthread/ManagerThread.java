package org.jobcenter.managerthread;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.clientstatusupdate.ClientStatusUpdateThread;
import org.jobcenter.config.ClientConfigDTO;
import org.jobcenter.constants.ClientStatusUpdateTypeEnum;
import org.jobcenter.constants.Constants;
import org.jobcenter.main.ClientMain;
import org.jobcenter.module.InactiveModulePool;
import org.jobcenter.processjob.GetJob;
import org.jobcenter.processjob.GetJobThread;
import org.jobcenter.processjob.JobRunnerThread;
import org.jobcenter.processjob.ThreadsHolderSingleton;
import org.jobcenter.serverinterface.ServerConnection;
import org.jobcenter.util.LogOpenFiles;
import org.jobcenter.util.SendClientStatusUpdateToServer;
import org.jobcenter.util.SendClientStatusUpdateToServer.PassJobsToServer;

/**
 * Starts the thread "GetJobThread" and the work threads "jobRunnerThread_##"
 * Checks the thread "GetJobThread" and the work threads "jobRunnerThread_##" for thread death and replaces them if they die.
 *
 */
public class ManagerThread extends Thread {

	private static final String className = ManagerThread.class.getSimpleName();

	private static Logger log = Logger.getLogger(ManagerThread.class);



	private static final int WAIT_TIME_FOR_MANAGER_THREAD_TO_EXIT_IN_SECONDS = 10;

	private static final int WAIT_TIME_FOR_CLIENT_STATUS_UPDATE_THREAD_TO_EXIT_IN_SECONDS = 10;

	private static final int WAIT_TIME_FOR_GET_JOB_THREAD_TO_EXIT_IN_SECONDS = 10;

	private static final int WAIT_TIME_FOR_GET_JOB_RUNNER_THREAD_TO_EXIT_IN_SECONDS = 10;



	private static final String GET_JOB_THREAD_NAME = "GetJobThread";

	private volatile boolean keepRunning = true;


	private volatile boolean stopRetrievingJobs = false;


	private volatile boolean stopJobCenterClientProgram = false;;


	private ClientMain jobCenterClientMain;


	private volatile GetJobThread getJobThread;

	private int getJobThreadCounter = 2;  // used if need to replace the thread




	private volatile ClientStatusUpdateThread clientStatusUpdateThread;

	private int clientStatusUpdateThreadCounter = 2;  // used if need to replace the thread



	/**
	 * default Constructor
	 */
	public ManagerThread() {


		//  Set a name for the thread

		String threadName = className;

		setName( threadName );

		init();
	}


	/**
	 * Constructor
	 * @param s
	 */
	public ManagerThread( String s ) {

		super(s);

		init();
	}

	/**
	 *
	 */
	private void init() {


	}


	/**
	 * awaken thread to process request
	 */
	public void awaken() {

		synchronized (this) {

			notify();
		}

	}




	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {


		log.debug( "run() called " );

		//		ClassLoader thisClassLoader = this.getClass().getClassLoader();

		//		this.setContextClassLoader( thisClassLoader );


		try {

			createClientControlFile();


			clientStatusUpdateThread = new ClientStatusUpdateThread();

			clientStatusUpdateThread.start();


			ClientConfigDTO clientConfigDTO = ClientConfigDTO.getSingletonInstance();  //  retrieve singleton


			log.info( "Starting " + clientConfigDTO.getMaxConcurrentJobs() + " JobRunnerThread instances." );


			List<JobRunnerThread> jobRunnerThreads = ThreadsHolderSingleton.getInstance().getJobRunnerThreads();

			for ( int count = 0; count < clientConfigDTO.getMaxConcurrentJobs(); count++ ) {

				JobRunnerThread jobRunnerThread = new JobRunnerThread();

				jobRunnerThread.setManagerThread( this );

				jobRunnerThreads.add( jobRunnerThread );

				jobRunnerThread.start();
			}

			getJobThread = new GetJobThread( GET_JOB_THREAD_NAME /* name */ );

			ThreadsHolderSingleton.getInstance().setGetJobThread( getJobThread );

			getJobThread.start();



			runProcessLoop( );  // Call main processing loop that will run while keepRunning == true




			if ( stopRetrievingJobs ) {

				File clientControlFile = null;

				FileWriter clientControlFileWriter = null;

				try {

					clientControlFile = new File( Constants.CLIENT_RUN_CONTROL_FILENAME );

					log.info( "ClientControlFile: filename = '" + Constants.CLIENT_RUN_CONTROL_FILENAME + "' filepath is = '" + clientControlFile.getAbsolutePath() + "'." );

					clientControlFileWriter = new FileWriter( clientControlFile );

					if ( stopJobCenterClientProgram ) {

						log.info( "ClientControlFile: Changing file contents to: \n" + Constants.CLIENT_RUN_CONTROL_ALL_JOBS_COMPLETE_SHUTDOWN_PROCEEDING );

						clientControlFileWriter.write( Constants.CLIENT_RUN_CONTROL_ALL_JOBS_COMPLETE_SHUTDOWN_PROCEEDING );

					} else {

						log.info( "ClientControlFile: Changing file contents to: \n" + Constants.CLIENT_RUN_CONTROL_ALL_JOBS_COMPLETE_READY_FOR_SHUTDOWN );

						clientControlFileWriter.write( Constants.CLIENT_RUN_CONTROL_ALL_JOBS_COMPLETE_READY_FOR_SHUTDOWN );
					}

				} catch (Throwable e) {

					log.error( "Exception in createClientControlFile(): ", e );

				} finally {

					if ( clientControlFileWriter != null ) {

						try {

							clientControlFileWriter.close();

						} catch (Throwable e) {

							log.error( "Exception in createClientControlFile(): calling clientControlFileWriter.close(); ", e );
						}
					}
				}

				if ( stopJobCenterClientProgram ) {

					jobCenterClientMain.stopMainThread();
				}

			}


		} catch (Throwable e) {

			log.error( "Exception in run(): ", e );
		}


		log.debug( "About to exit run()" );

		LogOpenFiles.logOpenFiles( LogOpenFiles.LIST_FILES_TRUE );




		log.info( "exitting run()" );


	}


	/**
	 * Main Processing loop
	 */
	private void runProcessLoop() {

		while ( keepRunning ) {

//			if ( log.isDebugEnabled() ) {
//
//				log.debug( "Top of loop in 'runProcessLoop()', waitTime in microseconds = " + waitTime );
//
//			}


			try {

				replaceWorkerThreadsIfDead();

				if ( keepRunning && ! stopRetrievingJobs ) {
					checkForStopProcessingJobsRequest();
				}

				if ( keepRunning ) {

					synchronized (this) {

						try {

							int waitTimeInSeconds = ClientConfigDTO.getSingletonInstance().getSleepTimeCheckingControlFile();

							wait( waitTimeInSeconds * 1000 ); //  wait for notify() call or timeout, in milliseconds

						} catch (InterruptedException e) {

							log.warn( "wait( waitTime ) was interrupted." );

						}
					}
				}

				this.getId();

			} catch (Throwable e) {

				log.error( "Exception in runProcessLoop(): ", e );
			}
		}

	}



	/**
	 *
	 */
	private void replaceWorkerThreadsIfDead() {

		if ( keepRunning ) {  //  only do if keep running is true




			//  check health of heartbeatThread, replace thread if dead

			if ( ! clientStatusUpdateThread.isAlive() ) {

				ClientStatusUpdateThread oldHeartbeatThread = clientStatusUpdateThread;

				clientStatusUpdateThread = new ClientStatusUpdateThread(  );

				clientStatusUpdateThread.setName( ClientStatusUpdateThread.className + "_" + clientStatusUpdateThreadCounter );

				clientStatusUpdateThreadCounter++;

				log.error( "HeartbeatThread thread '" + oldHeartbeatThread.getName() + "' is dead.  Replacing it with HeartbeatThread thread '" + clientStatusUpdateThread.getName() + "'."  );

				clientStatusUpdateThread.start();
			}



			//  check health of getJobThread, replace thread if dead

			if ( ! getJobThread.isAlive() ) {

				GetJobThread oldGetJobThread = getJobThread;

				getJobThread = new GetJobThread(  GET_JOB_THREAD_NAME + "_" + getJobThreadCounter /* name */  );

				getJobThreadCounter += 1;

				ThreadsHolderSingleton.getInstance().setGetJobThread( getJobThread );

				log.error( "GetJobThread thread '" + oldGetJobThread.getName() + "' is dead.  Replacing it with GetJobThread thread '" + getJobThread.getName() + "'."  );

				getJobThread.start();
			}


			//  check health of worker threads, replace thread if dead

			List<JobRunnerThread> jobRunnerThreads = ThreadsHolderSingleton.getInstance().getJobRunnerThreads();

			boolean replacedJobRunnerThread = false;

			for ( int index = 0; index < jobRunnerThreads.size(); index++ ) {

				JobRunnerThread jobRunnerThreadInList = jobRunnerThreads.get( index );

				if ( ! jobRunnerThreadInList.isAlive() ) {

					JobRunnerThread newJobRunnerThread = new JobRunnerThread();

					newJobRunnerThread.setDeadThread( jobRunnerThreadInList );

					jobRunnerThreads.set( index, newJobRunnerThread );

					log.error( "Jobrunner thread '" + jobRunnerThreadInList.getName() + "' is dead.  Replacing it with Jobrunner thread '" + newJobRunnerThread.getName() + "'."  );

					newJobRunnerThread.start();

					replacedJobRunnerThread = true;
				}
			}

			//  if replaced any JobRunnerThread, awaken Get job thread to use it.

			if ( replacedJobRunnerThread ) {

				getJobThread.awaken();
			}



		}
	}


	/**
	 *
	 */
	private void createClientControlFile() {

		File clientControlFile = null;

		BufferedWriter clientControlFileWriter = null;

		try {


			clientControlFile = new File( Constants.CLIENT_RUN_CONTROL_FILENAME );

			log.info( "ClientControlFile: filename = '" + Constants.CLIENT_RUN_CONTROL_FILENAME + "' filepath is = '" + clientControlFile.getAbsolutePath() + "'." );

			clientControlFileWriter = new BufferedWriter( new FileWriter( clientControlFile ) );
			
			if ( log.isDebugEnabled() ) {
				
				StringBuilder contentsSB = new StringBuilder( 2000);
				
				for ( String line : Constants.CLIENT_RUN_CONTROL_INITIAL_CONTENTS ) {

					contentsSB.append( line  );

					contentsSB.append( "\n" );
				}


				log.debug( "ClientControlFile: Changing file contents to: \n" + contentsSB );
			}

			for ( String line : Constants.CLIENT_RUN_CONTROL_INITIAL_CONTENTS ) {

				clientControlFileWriter.append( line  );

				clientControlFileWriter.newLine();
			}

		} catch (Throwable e) {

			log.error( "Exception in createClientControlFile(): ", e );

		} finally {

			if ( clientControlFileWriter != null ) {

				try {

					clientControlFileWriter.close();

				} catch (Throwable e) {

					log.error( "Exception in createClientControlFile(): calling clientControlFileWriter.close(); ", e );
				}
			}
		}

	}


	/**
	 * Check if the control file has been updated to indicate that a "stop" has been requested
	 */
	private void checkForStopProcessingJobsRequest() {


		File clientControlFile = null;

		BufferedReader clientControlFileReader = null;

		BufferedWriter clientControlFileWriter = null;

		try {

			clientControlFile = new File( Constants.CLIENT_RUN_CONTROL_FILENAME );

			clientControlFileReader = new BufferedReader( new FileReader( clientControlFile ) );

			String inputLine = clientControlFileReader.readLine();

			if ( inputLine != null ) {

				boolean stopRequestedLocal = false;

				String stopRequestType = null;

				if ( inputLine.startsWith(  Constants.CLIENT_RUN_CONTROL_STOP_JOBS_TEXT ) ) {

					stopRequestedLocal = true;

					stopRequestType = Constants.CLIENT_RUN_CONTROL_STOP_JOBS_TEXT;

					log.info(  "File '" + Constants.CLIENT_RUN_CONTROL_FILENAME
							+ "' has been changed to specify to stop retrieving new jobs and keep running when all jobs are complete."
							+ "  All threads except for the main thread will be dead when all jobs are complete." );

				} else if ( inputLine.startsWith(  Constants.CLIENT_RUN_CONTROL_STOP_RUN_TEXT ) ) {

					stopJobCenterClientProgram = true;
					stopRequestedLocal = true;

					stopRequestType = Constants.CLIENT_RUN_CONTROL_STOP_RUN_TEXT;


					log.info(  "File '" + Constants.CLIENT_RUN_CONTROL_FILENAME
							+ "' has been changed to specify to stop retrieving new jobs and exit when all jobs are complete." );
				}

				if ( stopRequestedLocal ) {

					stopRetrievingJobs = true;

					processStopRetrievingJobsRequest( stopRequestType );


					log.info( "ClientControlFile: Adding to file contents : \n" + Constants.CLIENT_RUN_CONTROL_STOP_REQUEST_ACCEPTED );

					log.info( "ClientControlFile: filename = '" + Constants.CLIENT_RUN_CONTROL_FILENAME + "' filepath is = '" + clientControlFile.getAbsolutePath() + "'." );

					clientControlFileWriter = new BufferedWriter( new FileWriter( clientControlFile, true /* append */ ) );

					for ( String line : Constants.CLIENT_RUN_CONTROL_STOP_REQUEST_ACCEPTED ) {

						clientControlFileWriter.append( line  );

						clientControlFileWriter.newLine();
					}

				}

			}

		} catch (Throwable e) {

			log.error( "Exception in checkForStopProcessingJobsRequest(): ", e );

		} finally {

			if ( clientControlFileReader != null ) {

				try {

					clientControlFileReader.close();

				} catch (Throwable e) {

					log.error( "Exception in checkForStopProcessingJobsRequest(): calling clientControlFileReader.close(); ", e );
				}
			}

			if ( clientControlFileWriter != null ) {

				try {

					clientControlFileWriter.close();

				} catch (Throwable e) {

					log.error( "Exception in checkForStopProcessingJobsRequest(): calling clientControlFileWriter.close(); ", e );
				}
			}
		}

	}


	/**
	 * Process the "stop" request from the control file.
	 */
	private void processStopRetrievingJobsRequest( String stopRequestType ) {

		keepRunning = false;  // Set thread of the current object to exit main processing loop.


		//  call clientStatusUpdateThread


		if ( clientStatusUpdateThread != null ) {

			clientStatusUpdateThread.setStopRequestType( stopRequestType );

			clientStatusUpdateThread.shutdown();
		}



		//  call getJobThread.shutdown();

		if ( getJobThread != null ) {

			try {
				getJobThread.shutdown();
			} catch (Throwable e) {

				log.error( "In processStopRetrievingJobsRequest(): call to getJobThread.shutdown() threw Throwable " + e.toString(), e );
			}

		} else {

			log.info( "In processStopRetrievingJobsRequest(): getJobThread == null" );
		}

		waitForGetJobThreadToComplete();

		//  call jobRunnerThread.stopRunningAfterProcessingJob();

		List<JobRunnerThread> jobRunnerThreads = ThreadsHolderSingleton.getInstance().getJobRunnerThreads();

		for ( JobRunnerThread jobRunnerThread : jobRunnerThreads ) {

			if ( jobRunnerThread != null ) {

				try {
					jobRunnerThread.stopRunningAfterProcessingJob();
				} catch (Throwable e) {

					log.info( "In processStopRetrievingJobsRequest(): call to jobRunnerThread.stopRunningAfterProcessingJob() threw Throwable " + e.toString(), e );
				}
			} else {

				log.info( "In processStopRetrievingJobsRequest(): jobRunnerThread == null" );
			}
		}

		waitForWorkerThreadsToComplete();



		waitForClientStatusUpdateThreadToComplete();



		//  notify server attempting to shut down

		ClientStatusUpdateTypeEnum updateType = ClientStatusUpdateTypeEnum.CLIENT_STOP_RETRIEVING_JOBS_AND_PAUSE_REQUESTED;

		if ( Constants.CLIENT_RUN_CONTROL_STOP_JOBS_TEXT.equals( stopRequestType ) ) {

			updateType = ClientStatusUpdateTypeEnum.CLIENT_STOP_RETRIEVING_JOBS_AND_PAUSE_COMPLETED;

		} else if ( Constants.CLIENT_RUN_CONTROL_STOP_RUN_TEXT.equals( stopRequestType ) ) {

			updateType = ClientStatusUpdateTypeEnum.CLIENT_STOP_RETRIEVING_JOBS_AND_SHUTDOWN_COMPLETED;

		}

		try {

			SendClientStatusUpdateToServer.sendClientStatusUpdateToServer( updateType, PassJobsToServer.PASS_JOBS_TO_SERVER_NO );

		} catch (Throwable t) {

			log.info( "In processStopRetrievingJobsRequest(): call to SendClientStatusUpdateToServer.sendClientStatusUpdateToServer( ClientStatusUpdateTypeEnum.CLIENT_ABOUT_TO_EXIT ) threw Throwable " + t.toString(), t );
		}

	}


	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {


		log.debug( "shutdown() called " );

		keepRunning = false;  // Set thread of the current object to exit main processing loop.


		//  Call to server that client has received shut down request.


		if ( clientStatusUpdateThread != null ) {

			clientStatusUpdateThread.shutdown();
		}

		awaken();  // send notify() to to the thread of the current object to start it so it will exit.


		boolean managerThreadExited = false;

		while ( ! managerThreadExited ) {


			try {  // wait for thread of the current object to die, so it won't start any threads to replace the threads that will be setup to die next.
				this.join( WAIT_TIME_FOR_MANAGER_THREAD_TO_EXIT_IN_SECONDS * 1000 );

			} catch (Throwable e) {

				log.error( "In processStopRetrievingJobsRequest(): call to this.shutdown() threw Throwable " + e.toString(), e );
			}

			if ( this.isAlive() ) {

				log.warn( "The thread 'managerThread' has not exited in the allocated time of "
						+ WAIT_TIME_FOR_MANAGER_THREAD_TO_EXIT_IN_SECONDS
						+ " seconds.  The wait for 'managerThread' to exit will be repeated with the same wait time." );

			} else {

				managerThreadExited = true;
			}

		}


		shutdownWorkerTheads();  // call shutdown() on worker threads




		waitForClientStatusUpdateThreadToComplete();


		//  Call to server that client is shutting down.

		try {

			SendClientStatusUpdateToServer.sendClientStatusUpdateToServer( ClientStatusUpdateTypeEnum.CLIENT_ABOUT_TO_EXIT, PassJobsToServer.PASS_JOBS_TO_SERVER_NO );

		} catch (Throwable t) {

			log.error( "In shutdown(): call to SendClientStatusUpdateToServer.sendClientStatusUpdateToServer( ClientStatusUpdateTypeEnum.CLIENT_ABOUT_TO_EXIT ) threw Throwable " + t.toString(), t );
		}




		//  Destroy GetJob and ServerConnection

		GetJob getJob = null;

		try {
			getJob = GetJob.getInstance();
		} catch (Throwable e) {

			log.error( "In shutdown(): call to GetJob.getInstance() threw Throwable " + e.toString(), e );
		}

		log.info( "shutdown():  call to getJob.destroy() " );

		if ( getJob != null ) {
			try {
				getJob.destroy();
			} catch (Throwable e) {

				log.error( "In shutdown(): call to getJob.destroy() threw Throwable " + e.toString(), e );
			}
		}

		log.info( "shutdown():  call to ServerConnection.getInstance().destroy() " );


		try {
			ServerConnection.getInstance().destroy();
		} catch (Throwable e) {

			log.error( "In shutdown(): call to ServerConnection.getInstance().destroy() threw Throwable " + e.toString(), e );
		}


		//  Destroy loaded modules

		destroyModules();

		log.info( "Exiting shutdown()" );


	}



	/**
	 * Called from shutdown() in this object.
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * Call destroy() on loaded modules
	 */
	private void destroyModules() {


		log.info( "destroyModules(): call module.shutdown() " );


		InactiveModulePool.getInstance().destroyModules();
	}




	/**
	 * Called from shutdown() in this object.
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * Call shutdown() on worker threads
	 * Wait for worker threads to finish
	 */
	private void shutdownWorkerTheads() {


		log.info( "shutdownWorkerTheads(): call getJobThread.shutdown() " );


		//  call getJobThread.shutdown();

		if ( getJobThread != null ) {

			try {
				getJobThread.shutdown();
			} catch (Throwable e) {

				log.info( "In shutdownWorkerTheads(): call to getJobThread.shutdown() threw Throwable " + e.toString(), e );
			}

		} else {

			log.info( "In shutdownWorkerTheads(): getJobThread == null" );
		}

		waitForGetJobThreadToComplete();


		log.info( "shutdownWorkerTheads(): call jobRunnerThread.shutdown() on all threads " );

		//  call jobRunnerThread.shutdown();

		List<JobRunnerThread> jobRunnerThreads = ThreadsHolderSingleton.getInstance().getJobRunnerThreads();

		for ( JobRunnerThread jobRunnerThread : jobRunnerThreads ) {

			if ( jobRunnerThread != null ) {

				try {
					jobRunnerThread.shutdown();
				} catch (Throwable e) {

					log.info( "In shutdownWorkerTheads(): call to jobRunnerThread.shutdown() threw Throwable " + e.toString(), e );
				}
			} else {

				log.info( "In shutdownWorkerTheads(): jobRunnerThread == null" );
			}
		}

		waitForWorkerThreadsToComplete();

	}



	/**
	 *
	 */
	private void waitForWorkerThreadsToComplete() {

		log.info( "waitForWorkerThreadsToComplete(): wait for jobRunnerThreads to complete, call jobRunnerThread.join() on all threads " );

		// wait for jobRunnerThreads to complete

		List<JobRunnerThread> jobRunnerThreads = ThreadsHolderSingleton.getInstance().getJobRunnerThreads();

		for ( JobRunnerThread jobRunnerThread : jobRunnerThreads ) {

			if ( jobRunnerThread != null ) {


				boolean jobRunnerThreadExited = false;

				while ( ! jobRunnerThreadExited ) {

					try {

						jobRunnerThread.join( WAIT_TIME_FOR_GET_JOB_RUNNER_THREAD_TO_EXIT_IN_SECONDS * 1000 );

					} catch (InterruptedException e) {

						log.info( "In waitForWorkerThreadsToComplete(): call to jobRunnerThread.join() for name " + jobRunnerThread.getName() + " threw Exception " + e.toString(), e );
					}

					if ( jobRunnerThread.isAlive() ) {

						log.warn( "The thread 'jobRunnerThread' named '"
								+ jobRunnerThread.getName()
								+ "' has not exited in the allocated time of "
								+ WAIT_TIME_FOR_GET_JOB_RUNNER_THREAD_TO_EXIT_IN_SECONDS
								+ " seconds.  The module probably hasn't exited the call to 'processRequest(...)' yet.  "
								+ "The wait for 'jobRunnerThread' to exit will be repeated with the same wait time." );

					} else {

						jobRunnerThreadExited = true;
					}

				}

			} else {

				log.info( "In waitForWorkerThreadsToComplete(): jobRunnerThread == null" );
			}
		}

		log.info( "waitForWorkerThreadsToComplete(): All jobRunnerThreads ARE complete, called jobRunnerThread.join() on all threads " );


	}


	/**
	 * wait For GetJobThread To Complete
	 */
	private void waitForGetJobThreadToComplete () {

		log.info( "waitForGetJobThreadToComplete(): wait for getJobThread to complete, call getJobThread.join() " );

		// wait for getJobThread to complete

		if ( getJobThread != null ) {

			boolean getJobThreadExited = false;

			while ( ! getJobThreadExited ) {
				try {
					getJobThread.join( WAIT_TIME_FOR_GET_JOB_THREAD_TO_EXIT_IN_SECONDS * 1000 );
				} catch (InterruptedException e) {

					log.info( "In waitForGetJobThreadToComplete(): call to getJobThread.join() threw InterruptedException " + e.toString(), e );
				}

				if ( getJobThread.isAlive() ) {

					log.error( "The thread 'getJobThread' has not exited in the allocated time of "
							+ WAIT_TIME_FOR_GET_JOB_THREAD_TO_EXIT_IN_SECONDS
							+ " seconds.  The wait for 'getJobThread' to exit will be repeated with the same wait time." );

				} else {

					getJobThreadExited = true;
				}
			}

		} else {

			log.info( "In waitForGetJobThreadToComplete(): getJobThread == null" );
		}

		log.info( "waitForGetJobThreadToComplete():  getJobThread IS complete, called getJobThread.join() " );

	}


	/**
	 * wait For ClientStatusUpdateThread To Complete
	 */
	private void waitForClientStatusUpdateThreadToComplete () {

		log.info( "waitForClientStatusUpdateThreadToComplete(): wait for ClientStatusUpdateThread to complete, call clientStatusUpdateThread.join() " );

		// wait for clientStatusUpdateThread to complete

		if ( clientStatusUpdateThread != null ) {


			boolean clientStatusUpdateThreadExited = false;

			while ( ! clientStatusUpdateThreadExited ) {


				try {
					clientStatusUpdateThread.join( WAIT_TIME_FOR_CLIENT_STATUS_UPDATE_THREAD_TO_EXIT_IN_SECONDS * 1000 );
				} catch (InterruptedException e) {

					log.info( "In waitForClientStatusUpdateThreadToComplete(): call to clientStatusUpdateThread.join() threw InterruptedException " + e.toString(), e );
				}


				if ( clientStatusUpdateThread.isAlive() ) {

					log.info( "The thread 'clientStatusUpdateThread' has not exited in the allocated time of "
							+ WAIT_TIME_FOR_CLIENT_STATUS_UPDATE_THREAD_TO_EXIT_IN_SECONDS
							+ " seconds.  The wait for 'clientStatusUpdateThread' to exit will be repeated with the same wait time." );

				} else {

					clientStatusUpdateThreadExited = true;
				}

			}

		} else {

			log.info( "In waitForClientStatusUpdateThreadToComplete(): clientStatusUpdateThread == null" );
		}

		log.info( "waitForClientStatusUpdateThreadToComplete():  clientStatusUpdateThread IS complete, called clientStatusUpdateThread.join() " );

	}


	public ClientMain getJobCenterClientMain() {
		return jobCenterClientMain;
	}


	public void setJobCenterClientMain(ClientMain jobCenterClientMain) {
		this.jobCenterClientMain = jobCenterClientMain;
	}


}
