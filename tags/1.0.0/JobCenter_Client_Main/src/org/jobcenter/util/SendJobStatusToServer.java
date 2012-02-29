package org.jobcenter.util;

import org.apache.log4j.Logger;
import org.jobcenter.config.ClientConfigDTO;
import org.jobcenter.dto.Job;
import org.jobcenter.request.UpdateServerFromJobRunOnClientRequest;
import org.jobcenter.response.UpdateServerFromJobRunOnClientResponse;
import org.jobcenter.serverinterface.ServerConnection;

public class SendJobStatusToServer {


	private static final String className = SendJobStatusToServer.class.getSimpleName();

	private static Logger log = Logger.getLogger(SendJobStatusToServer.class);


	private static final int WAIT_TIME_FOR_RETRY_UPDATE_JOB_STATUS = 10 * 1000;  //  10 seconds


	private volatile boolean keepRunning = true;



	/**
	 * awaken thread to process request, calls "notify()"
	 */
	public void awaken() {

		if ( log.isDebugEnabled() ) {

			log.debug( "awaken() called:" );
		}

		synchronized (this) {

			notify();
		}

	}

	/**
	 * Called on a different thread.
	 * The ManagerThread instance has detected that the user has requested that the Jobmanager client stop retrieving jobs.
	 */
	public void stopRunningAfterProcessingJob() {

		synchronized (this) {

			this.keepRunning = false;

		}

		//  awaken this thread if it is in 'wait' state ( not currently processing a job )


		awaken();

		//  This object/thread will then run until the current job is complete and then will die.
	}


	/**
	 * shutdown was received from the operating system.  This is called on a different thread.
	 */
	public void shutdown() {


		log.info("shutdown() called: " );


		synchronized (this) {

			this.keepRunning = false;

		}

		//  awaken this thread if it is in 'wait' state ( not currently processing a job )

		this.awaken();
	}



	/**
	 * @param moduleRunStatus
	 * @param moduleCompletionMessage
	 * @throws Throwable
	 */
	public void sendJobStatusToServer( Job job )
	throws Throwable {
		
		UpdateServerFromJobRunOnClientRequest updateServerFromJobRunOnClientRequest = new UpdateServerFromJobRunOnClientRequest();

		updateServerFromJobRunOnClientRequest.setJob( job );

		updateServerFromJobRunOnClientRequest.setIgnoreJobdbRecordVersionNumber( UpdateServerFromJobRunOnClientRequest.IGNORE_JOB_DB_RECORD_VERSION_NUMBER_TRUE );

		updateServerFromJobRunOnClientRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );

		UpdateServerFromJobRunOnClientResponse updateJobStatusResponse = null;

		boolean sendResponseSuccessful = false;

		while ( ! sendResponseSuccessful ) {

			if ( log.isInfoEnabled() ) {
				log.info( "Sending status to server for job id " + job.getId() + ", job status = " + job.getStatusId() );
			}


			try {
				updateJobStatusResponse = ServerConnection.getInstance().updateServerFromJobRunOnClient( updateServerFromJobRunOnClientRequest );

				sendResponseSuccessful = true;   //  call did not throw exception so exit loop

			} catch ( Throwable t ) {

				String jobIdString = "'job' is null";

				if ( job != null ) {

					jobIdString = Integer.toString( job.getId() );
				}

				log.error( "Exception in sendJobStatusToServer( job): job id " + jobIdString + ", exception = " + t.toString(), t );

				try {

					if ( ! keepRunning ) {  // time to exit rethrowing the exception

						throw new Exception( "Giving up retry of update status on server since keepRunning is false", t );
					}

					synchronized ( this ) {

						wait( WAIT_TIME_FOR_RETRY_UPDATE_JOB_STATUS );
					}

				} catch (InterruptedException e) {

					log.error("InterruptedException wait( WAIT_TIME_FOR_RETRY_SUBMIT_JOB ), while sleeping before re-try update job status.  Exception = " + t.toString(), t );
				}
			}
		}

		if ( updateJobStatusResponse.isErrorResponse() ) {

			updateJobStatusResponse.getErrorCode();

			updateJobStatusResponse.getErrorCodeDescription();

			throw new Exception( "Failed to update status on server.  Error code = " + updateJobStatusResponse.getErrorCode()
					+ ", Error Code Description = " + updateJobStatusResponse.getErrorCodeDescription() );
		}
	}





}
