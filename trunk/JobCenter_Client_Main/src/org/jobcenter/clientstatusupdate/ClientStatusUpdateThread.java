package org.jobcenter.clientstatusupdate;



import org.apache.log4j.Logger;
import org.jobcenter.config.ConfigFromServer;
import org.jobcenter.constants.ClientStatusUpdateTypeEnum;
import org.jobcenter.constants.Constants;
import org.jobcenter.util.SendClientStatusUpdateToServer;
import org.jobcenter.util.SendClientStatusUpdateToServer.PassJobsToServer;

/**
 * sends the client status update to the server
 *
 */
public class ClientStatusUpdateThread extends Thread {

	public static final String className = ClientStatusUpdateThread.class.getSimpleName();

	private static Logger log = Logger.getLogger(ClientStatusUpdateThread.class);


	private volatile boolean keepRunning = true;
	
	
	private volatile String stopRequestType = null;


	private int waitTimeForNextClientCheckin;

	

	private Integer waitTimeForNextClientCheckinFromLastServerResponse;


	/**
	 * default Constructor
	 */
	public ClientStatusUpdateThread() {


		//  Set a name for the thread

		String threadName = className;

		setName( threadName );

		init();
	}


	/**
	 * Constructor
	 * @param s
	 */
	public ClientStatusUpdateThread( String s ) {

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

			Integer waitTimeForNextClientCheckinConfig = ConfigFromServer.getInstance().getWaitTimeForNextClientCheckin();

			if ( waitTimeForNextClientCheckinConfig == null ) {

				
				//  didn't receive config from server for this so put thread to sleep forever, until shutdown.

				synchronized (this) {

					try {

						wait( ); //  wait indefinitely for notify() call

					} catch (InterruptedException e) {

						log.warn( "wait( ) was interrupted." );
					}
				}

			} else {

				waitTimeForNextClientCheckin = waitTimeForNextClientCheckinConfig;

				runProcessLoop( );  // Call main processing loop that will run while keepRunning == true
			}


		} catch (Throwable e) {

			log.error( "Exception in run(): ", e );
		}
		
		
		if ( stopRequestType != null ) {

			//  notify server attempting to shut down

			ClientStatusUpdateTypeEnum updateType = ClientStatusUpdateTypeEnum.CLIENT_STOP_RETRIEVING_JOBS_AND_PAUSE_REQUESTED;

			if ( Constants.CLIENT_RUN_CONTROL_STOP_JOBS_TEXT.equals( stopRequestType ) ) {

				updateType = ClientStatusUpdateTypeEnum.CLIENT_STOP_RETRIEVING_JOBS_AND_PAUSE_REQUESTED;

			} else if ( Constants.CLIENT_RUN_CONTROL_STOP_RUN_TEXT.equals( stopRequestType ) ) {

				updateType = ClientStatusUpdateTypeEnum.CLIENT_STOP_RETRIEVING_JOBS_AND_SHUTDOWN_REQUESTED;
			}

			try {
				SendClientStatusUpdateToServer.sendClientStatusUpdateToServer( updateType, PassJobsToServer.PASS_JOBS_TO_SERVER_NO );

			} catch (Throwable t) {

				log.warn( "In run(); call to SendClientStatusUpdateToServer.sendClientStatusUpdateToServer( ClientStatusUpdateTypeEnum.CLIENT_ABOUT_TO_EXIT ) threw Throwable " + t.toString(), t );
			}

		} else {
			
			try {
				SendClientStatusUpdateToServer.sendClientStatusUpdateToServer( ClientStatusUpdateTypeEnum.CLIENT_SHUTDOWN_REQUESTED, PassJobsToServer.PASS_JOBS_TO_SERVER_NO );

			} catch (Throwable t) {

				log.warn( "In shutdown(): call to SendClientStatusUpdateToServer.sendClientStatusUpdateToServer( ClientStatusUpdateTypeEnum.CLIENT_ABOUT_TO_EXIT ) threw Throwable " + t.toString(), t );
			}
		}

		log.debug( "exitting run()" );
	}


	/**
	 * Main Processing loop
	 */
	private void runProcessLoop() {

		while ( keepRunning ) {

//			if ( log.isDebugEnabled() ) {
//
//				log.debug( "Top of loop in 'runProcessLoop()', waitTime in milliseconds = " + waitTime );
//
//			}


			try {

				if ( keepRunning ) {  //  only do if keep running is true

					sendClientStatusUpdateClientUp();
				}

				if ( keepRunning ) {


					int waitTimeInSeconds = waitTimeForNextClientCheckin;
					
					if ( waitTimeForNextClientCheckinFromLastServerResponse != null ) {
						
						if ( log.isDebugEnabled() ) {
							
							log.debug( "waitTimeForNextClientCheckinFromLastServerResponse != null so using it, is = " 
									+ waitTimeForNextClientCheckinFromLastServerResponse );
						}
						
						waitTimeInSeconds = waitTimeForNextClientCheckinFromLastServerResponse;
					
					} else {
						
						if ( log.isDebugEnabled() ) {
							
							log.debug( "waitTimeForNextClientCheckinFromLastServerResponse == null so using waitTimeForNextClientCheckin from client startup, is = " 
									+ waitTimeForNextClientCheckin );
						}
					}


					synchronized (this) {

						try {

							wait( waitTimeInSeconds * 1000 ); //  wait for notify() call or timeout, in milliseconds

						} catch (InterruptedException e) {

							log.warn( "wait( waitTime ) was interrupted." );

						}
					}
				}
				

			} catch (Throwable e) {

				log.error( "Exception in runProcessLoop(): ", e );
			}
		}
		

		try {

			sendClientStatusUpdateClientUp();


		} catch (Throwable e) {

			log.error( "Exception in runProcessLoop(): ", e );
		}

	}



	/**
	 *
	 */
	private void sendClientStatusUpdateClientUp() throws Throwable {

		waitTimeForNextClientCheckinFromLastServerResponse = SendClientStatusUpdateToServer.sendClientStatusUpdateToServer( ClientStatusUpdateTypeEnum.CLIENT_UP, PassJobsToServer.PASS_JOBS_TO_SERVER_YES );
	}


	
	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {


		log.debug( "shutdown() called, setting keepRunning = false, calling awaken() " );

		keepRunning = false;

		awaken();

		log.debug( "Exiting shutdown()" );
	}


	public String getStopRequestType() {
		return stopRequestType;
	}


	public void setStopRequestType(String stopRequestType) {
		this.stopRequestType = stopRequestType;
	}




}
