package org.jobcenter.main;

import org.apache.log4j.Logger;


import org.jobcenter.client.coreinterfaces.ClientMainIF;
import org.jobcenter.config.RetrieveClientConfiguration;
import org.jobcenter.config.RetrieveTimeControlConfig;
import org.jobcenter.managerthread.ManagerThread;

import org.jobcenter.serverinterface.ClientStartupServerConnection;
import org.jobcenter.util.BuildURLClassLoader;
import org.jobcenter.util.JobToFile;


public class ClientMain implements ClientMainIF {


	private static final String className = ClientMain.class.getSimpleName();

	private static Logger log = Logger.getLogger(ClientMain.class);

	private volatile boolean keepRunning = true;

	private volatile Thread currentThread;

	private ManagerThread managerThread;


	/* (non-Javadoc)
	 * @see org.jobcenter.client.coreinterfaces.ClientMainIF#runClientMain(java.lang.ClassLoader)
	 */
	@Override
	public void runClientMain( ClassLoader rootClassLoader ) {

		log.warn( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!runClientMain( ClassLoader rootClassLoader ) start" );

//		createFile();


		//  use to test that emails are being sent for logging to error

//		log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!runClientMain( ClassLoader rootClassLoader ) start" );


		try {

			BuildURLClassLoader.setRootClassLoader( rootClassLoader );
			
			//  Create the jobs directories JobCenter will write to and test write a file in it.
			JobToFile.createJobsDirectoryiesAndTestFiles();


			RetrieveClientConfiguration retrieveClientConfiguration = new RetrieveClientConfiguration();

			retrieveClientConfiguration.loadClientConfiguration();

			retrieveClientConfiguration = null;

			RetrieveTimeControlConfig retrieveTimeControlConfig = new RetrieveTimeControlConfig();

			retrieveTimeControlConfig.loadTimeControlConfig();

			retrieveTimeControlConfig = null;

			ClientStartupServerConnection.connectToServerForStartupAction();

			currentThread = Thread.currentThread();

			managerThread = new ManagerThread( );

			managerThread.setJobCenterClientMain( this );

			managerThread.start();



			while ( keepRunning ) {

				//   TODO  TEMP code

				synchronized (this) {

					try {

						wait();

					} catch (InterruptedException e) {

						log.warn("wait() interrupted with InterruptedException");

					}
				}


			}


		} catch (Throwable e1) {

			String msg = "JobCenterClientMain: Exception: " + e1.toString();

			log.error( msg, e1);


			System.out.println( msg );
			e1.printStackTrace(System.out);

			System.err.println( msg );
			e1.printStackTrace();

		}



	}





	/**
	 * shutdown was received from the operating system on a different thread
	 */
	@Override
	public void shutdown() {

		log.debug( "shutdown() Called" );


		try {


			if ( managerThread != null ) {

				managerThread.shutdown();

				try {

					//  wait for managerThread to exit the run() method

					managerThread.join();

				} catch (InterruptedException e) {

					log.warn( "In shutdown(): call to managerThread.join() threw InterruptedException " + e.toString(), e );
				}
			}


		} catch (Throwable e1) {

			log.error( "JobCenterClientMain: shutdown:  managerThread.shutdown();:  Exception: ", e1);
		}

		keepRunning = false;

		awaken();


		log.info( "Exiting shutdown()" );


	}

	/**
	 * Stop the main thread
	 */
	public void stopMainThread() {


		log.info( "stopMainThread() Called" );

		//  awaken and let main thread die

		keepRunning = false;

		awaken();


		log.info( "Exiting stopMainThread()" );


	}


	/**
	 * awaken thread to process request
	 */
	private void awaken() {

		synchronized (this) {

			notify();
		}

	}



	//  TODO  test stuff

//	private void createFile() {
//
//		try {
//
//			File file = new File( "testOutput.txt" );
//
//			log.info( "filepath of testOutput.txt is " + file.getAbsolutePath() );
//
//		} catch ( Throwable t ) {
//
//			log.error( "createFile()  Exception: " + t.toString(), t );
//		}
//
//
//	}



}
