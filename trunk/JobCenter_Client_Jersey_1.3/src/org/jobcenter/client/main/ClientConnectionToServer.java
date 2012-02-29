package org.jobcenter.client.main;


import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.jobcenter.constants.WebServiceURLConstants;
import org.jobcenter.coreinterfaces.ClientConnectionToServerIF;
import org.jobcenter.dto.Job;
import org.jobcenter.request.ClientStatusUpdateRequest;
import org.jobcenter.request.ClientStartupRequest;
import org.jobcenter.request.GetRunIdListRequest;
import org.jobcenter.request.GetRunRequest;
import org.jobcenter.request.JobRequest;
import org.jobcenter.request.SubmitJobRequest;
import org.jobcenter.request.UpdateServerFromJobRunOnClientRequest;
import org.jobcenter.response.ClientStatusUpdateResponse;
import org.jobcenter.response.ClientStartupResponse;
import org.jobcenter.response.GetRunIdListResponse;
import org.jobcenter.response.GetRunResponse;
import org.jobcenter.response.JobResponse;
import org.jobcenter.response.SubmitJobResponse;
import org.jobcenter.response.UpdateServerFromJobRunOnClientResponse;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class ClientConnectionToServer implements ClientConnectionToServerIF {


	private static final String PROPERTIES_FILE_NAME = "config_server_connection.properties";

	//  These are used for a quick retry attempt

	private static final int RETRY_SLEEP_TIME = 1000; // 1 second

	private static final int RETRY_COUNT_MAX = 2; // max retries before rethrow exception


	private static Logger log = Logger.getLogger(ClientConnectionToServer.class);


	private static String connectionURL = null;


	private Client jersey_JAX_RS_Client = null;



	@Override
	public void init() throws Throwable {

		log.info( "Entered init() in project JobManager_Client_Jersey_1.3" );

		try {

			ClassLoader thisClassLoader = this.getClass().getClassLoader();

			InputStream props = thisClassLoader.getResourceAsStream( PROPERTIES_FILE_NAME );

			if ( props == null ) {

				String msg = "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:    Properties file '" + PROPERTIES_FILE_NAME + "' not found.";

				log.error( msg );

				throw new Exception( msg );

			} else {

				Properties configProps = new Properties();

				configProps.load(props);

//				log.info( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  config.location = " + configProps.getProperty("config.location") );

				connectionURL = configProps.getProperty("server.url") + "/" + WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT;
			}

			jersey_JAX_RS_Client = Client.create();

		} catch ( RuntimeException e ) {

			log.info( "In init(),   Properties file '" + PROPERTIES_FILE_NAME + "', exception: " + e.toString(), e );

			throw e;
		}
	}


	@Override
	public void destroy(){

		try {
			if ( jersey_JAX_RS_Client != null ) {

				jersey_JAX_RS_Client.destroy();

				jersey_JAX_RS_Client = null;
			}

		} catch ( RuntimeException e ) {

			log.info( "In destroy(), exception: " + e.toString(), e );

			throw e;
		}
	}




	@Override
	public ClientStartupResponse clientStartup( ClientStartupRequest clientStartupRequest ) throws Throwable {


		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( clientStartupRequest == null ) {

			throw new IllegalArgumentException( "clientStartupRequest cannot be null" );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}


		String fullConnectionURL = connectionURL + WebServiceURLConstants.CLIENT_STARTUP;

		/////////////////////

		if ( log.isDebugEnabled() ) {

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  ClientConnectionToServer in project JobManager_Client_Jersey_1.3" );

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  fullConnectionURL = " + fullConnectionURL );
		}

		/////////////////////////////



		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );


		boolean successfulCallToServer = false;

		int retryCount = 0;

		ClientStartupResponse clientStartupResponse = null;

		while ( ( ! successfulCallToServer ) ) {  //  exit loop when call to server not throw exception or retryCount >= RETRY_COUNT_MAX

			try {
				clientStartupResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( ClientStartupResponse.class, clientStartupRequest );

				successfulCallToServer = true;

			} catch ( Throwable t ) {

				log.info( "Call to server from clientStartup() threw exception, retryCount = " + retryCount + ", RETRY_COUNT_MAX = " + RETRY_COUNT_MAX, t );


				retryCount++;

				if ( retryCount >= RETRY_COUNT_MAX ) {

					throw new Exception( "\n\n!!!!!!!!!!!   Exception connecting to the server for the first time.  \n\n"
							+ "Trying to connect using the URL:  " + fullConnectionURL + "\n\n", t );

				}

				try {
					Thread.sleep( RETRY_SLEEP_TIME );

				} catch (InterruptedException e) {

					log.info( "Sleep waiting for retry interrupted with InterruptedException", e );
				}
			}
		}


		if ( log.isDebugEnabled() ) {

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  clientStartupResponse.isErrorResponse() = " + clientStartupResponse.isErrorResponse()
						+ ", clientStartupResponse.getErrorCode() = " + clientStartupResponse.getErrorCode()
//						+ ",  clientStartupResponse.getNodeIndentifier() = " + clientStartupResponse.getNodeIndentifier()
						);

		}

		/////////////////////////////////////

		return clientStartupResponse;
	}



	@Override
	public ClientStatusUpdateResponse clientStatusUpdate(	ClientStatusUpdateRequest clientStatusUpdateRequest) throws Throwable {

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( clientStatusUpdateRequest == null ) {

			throw new IllegalArgumentException( "clientStatusUpdateRequest cannot be null" );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}


		String fullConnectionURL = connectionURL + WebServiceURLConstants.CLIENT_UPDATE_STATUS;

		/////////////////////

		if ( log.isDebugEnabled() ) {

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  ClientConnectionToServer in project JobManager_Client_Jersey_1.3" );

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  fullConnectionURL = " + fullConnectionURL );
		}

		/////////////////////////////



		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );


		boolean successfulCallToServer = false;

		int retryCount = 0;

		ClientStatusUpdateResponse response = null;

		while ( ( ! successfulCallToServer ) ) {  //  exit loop when call to server not throw exception or retryCount >= RETRY_COUNT_MAX

			try {
				response = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( ClientStatusUpdateResponse.class, clientStatusUpdateRequest );

				successfulCallToServer = true;

			} catch ( Throwable t ) {

				log.info( "Call to server from clientStatusUpdate() threw exception, retryCount = " + retryCount + ", RETRY_COUNT_MAX = " + RETRY_COUNT_MAX, t );


				retryCount++;

				if ( retryCount >= RETRY_COUNT_MAX ) {

					throw new Exception( "!!!!!!!!!!!   Exception connecting to the server.  "
							+ "In clientStatusUpdate(), trying to connect using the URL:  " + fullConnectionURL, t );

//					throw t; //////   exit by rethrowing the exception if the retry count has been exceeded
				}

				try {
					Thread.sleep( RETRY_SLEEP_TIME );

				} catch (InterruptedException e) {

					log.info( "Sleep waiting for retry interrupted with InterruptedException", e );
				}
			}
		}


		if ( log.isDebugEnabled() ) {

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer: clientStatusUpdate(...):  response.isErrorResponse() = " + response.isErrorResponse()
						+ ", response.getErrorCode() = " + response.getErrorCode()
//						+ ",  clientHeartbeatResponse.getNodeIndentifier() = " + clientHeartbeatResponse.getNodeIndentifier()
						);

		}

		/////////////////////////////////////

		return response;
	}



	/* (non-Javadoc)
	 * @see org.jobcenter.coreinterfaces.ClientConnectionToServerIF#getNextJobToProcess(org.jobcenter.dto.JobRequest)
	 */
	@Override
	public JobResponse getNextJobToProcess(JobRequest jobRequest) throws Throwable {

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jobRequest == null ) {

			throw new IllegalArgumentException( "jobRequest cannot be null" );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}


		String fullConnectionURL = connectionURL + WebServiceURLConstants.GET_NEXT_JOB_FOR_CLIENT_TO_PROCESS;

		/////////////////////


		if ( log.isDebugEnabled() ) {

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  ClientConnectionToServer in project JobManager_Client_Jersey_1.3" );

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  fullConnectionURL = " + fullConnectionURL );
		}

		/////////////////////////////



		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );


		boolean successfulCallToServer = false;

		int retryCount = 0;

		JobResponse jobResponse = null;

		while ( ( ! successfulCallToServer ) ) {  //  exit loop when call to server not throw exception or retryCount >= RETRY_COUNT_MAX

			try {
				jobResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( JobResponse.class, jobRequest );

				successfulCallToServer = true;

			} catch ( Throwable t ) {

				log.info( "Call to server with jobRequest threw exception, retryCount = " + retryCount + ", RETRY_COUNT_MAX = " + RETRY_COUNT_MAX, t );

				retryCount++;

				if ( retryCount >= RETRY_COUNT_MAX ) {

					throw new Exception( "!!!!!!!!!!!   Exception connecting to the server.  "
							+ "In getNextJobToProcess(), trying to connect using the URL:  " + fullConnectionURL, t );

//					throw t;
				}

				try {
					Thread.sleep( RETRY_SLEEP_TIME );

				} catch (InterruptedException e) {

					log.info( "Sleep waiting for retry interrupted with InterruptedException", e );
				}
			}
		}



		if ( log.isDebugEnabled() ) {


			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  jobResponse.isErrorResponse() = " + jobResponse.isErrorResponse()
						+ ", jobResponse.getErrorCode() = " + jobResponse.getErrorCode()
						+ ",  jobResponse.isJobFound() = " + jobResponse.isJobFound() );

			////////////////////////////////////////

		}


		if ( log.isInfoEnabled() ) {

			Job job = jobResponse.getJob();

			if ( job != null ) {

				log.info( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  fullConnectionURL = " + fullConnectionURL );

				log.info( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  jobResponse.getJob().getId() =  " + job.getId()
					+ ", getJobTypeId() =  " + job.getJobTypeId()
					+ ", getPriority() =  " + job.getPriority()
					+ ", getJob().getgetStatusId() =  " + job.getStatusId()
					+ ", jobResponse.getJob().getSubmitter() =  " + job.getSubmitter()
					+ ", jobResponse.getJob().getSubmitDate() =  " + job.getSubmitDate() );

				if ( job.getJobParameters() == null ) {

					log.info( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  jobResponse.getJobParameters() == null " );

				} else {

					for ( Map.Entry<String, String> entry : job.getJobParameters().entrySet() ) {

						log.info( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  jobResponse.getJob().getJobParameters() entry.getKey() =  |"
								+ entry.getKey()
								+ "|, entry.getValue() =  |" + entry.getValue() + "|." );

					}
				}
			}

		}

		/////////////////////////////////////

		return jobResponse;
	}

	@Override
	public UpdateServerFromJobRunOnClientResponse updateServerFromJobRunOnClient( UpdateServerFromJobRunOnClientRequest updateServerFromJobRunOnClientRequest )
	throws Throwable {


		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( updateServerFromJobRunOnClientRequest == null ) {

			throw new IllegalArgumentException( "updateServerFromJobRunOnClientRequest cannot be null" );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}

		String fullConnectionURL = connectionURL + WebServiceURLConstants.UPDATE_SERVER_FROM_JOB_RUN_ON_CLIENT_SERVICE;

		/////////////////////


		if ( log.isDebugEnabled() ) {

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  ClientConnectionToServer in project JobManager_Client_Jersey_1.3" );

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  fullConnectionURL = " + fullConnectionURL );
		}

		/////////////////////////////



		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );


		boolean successfulCallToServer = false;

		int retryCount = 0;

		UpdateServerFromJobRunOnClientResponse updateServerFromJobRunOnClientResponse = null;

		while ( ( ! successfulCallToServer ) ) {  //  exit loop when call to server not throw exception or retryCount >= RETRY_COUNT_MAX

			try {
				updateServerFromJobRunOnClientResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( UpdateServerFromJobRunOnClientResponse.class, updateServerFromJobRunOnClientRequest );

				successfulCallToServer = true;

			} catch ( Throwable t ) {


				log.info( "Call to server from updateServerFromJobRunOnClient() threw exception, retryCount = " + retryCount + ", RETRY_COUNT_MAX = " + RETRY_COUNT_MAX, t );

				retryCount++;

				if ( retryCount >= RETRY_COUNT_MAX ) {

					throw new Exception( "!!!!!!!!!!!   Exception connecting to the server.  "
							+ "In updateServerFromJobRunOnClient(), trying to connect using the URL:  " + fullConnectionURL, t );

//					throw t;
				}

				try {
					Thread.sleep( RETRY_SLEEP_TIME );

				} catch (InterruptedException e) {

					log.info( "Sleep waiting for retry interrupted with InterruptedException", e );
				}
			}
		}


		if ( log.isDebugEnabled() ) {

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  updateServerFromJobRunOnClientResponse.isErrorResponse() = " + updateServerFromJobRunOnClientResponse.isErrorResponse() );

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  updateServerFromJobRunOnClientResponse.getErrorCode() = " + updateServerFromJobRunOnClientResponse.getErrorCode() );
		}

		return updateServerFromJobRunOnClientResponse;
	}


	/**
	 * Get a particular run object
	 *
	 * @param getRunRequest
	 * @return
	 * @throws Throwable
	 */
	@Override
	public GetRunResponse getRunRequest( GetRunRequest getRunRequest ) throws Throwable {



		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( getRunRequest == null ) {

			throw new IllegalArgumentException( "getRunRequest cannot be null" );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}

		String fullConnectionURL = connectionURL + WebServiceURLConstants.GET_RUN_SERVICE;

		/////////////////////


		if ( log.isDebugEnabled() ) {

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  ClientConnectionToServer in project JobManager_Client_Jersey_1.3" );

			log.debug( "Entered getRunRequest( GetRunRequest getRunRequest ). Job id = " + getRunRequest.getJobId() + ", Run id = " + getRunRequest.getCurrentRunId() );

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  fullConnectionURL = " + fullConnectionURL );
		}

		/////////////////////////////



		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );


		boolean successfulCallToServer = false;

		int retryCount = 0;

		GetRunResponse getRunResponse = null;

		while ( ( ! successfulCallToServer ) ) {  //  exit loop when call to server not throw exception or retryCount >= RETRY_COUNT_MAX

			try {
				getRunResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( GetRunResponse.class, getRunRequest );

				successfulCallToServer = true;

			} catch ( Throwable t ) {

				log.info( "Call to server from getRunRequest() threw exception, retryCount = " + retryCount + ", RETRY_COUNT_MAX = " + RETRY_COUNT_MAX, t );

				retryCount++;

				if ( retryCount >= RETRY_COUNT_MAX ) {

					throw new Exception( "!!!!!!!!!!!   Exception connecting to the server.  "
							+ "In getRunRequest(), trying to connect using the URL:  " + fullConnectionURL, t );

//					throw t;
				}

				try {
					Thread.sleep( RETRY_SLEEP_TIME );

				} catch (InterruptedException e) {

					log.info( "Sleep waiting for retry interrupted with InterruptedException", e );
				}
			}
		}

		if ( log.isDebugEnabled() ) {

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:   getRunRequest( GetRunRequest getRunRequest ):  getRunResponse.isErrorResponse() = " + getRunResponse.isErrorResponse() );

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:   getRunRequest( GetRunRequest getRunRequest ):  getRunResponse.getErrorCode() = " + getRunResponse.getErrorCode() );
		}

		return getRunResponse;

	}





	/**
	 * Get a list of run ids for a job id
	 *
	 * @param getRunIdListRequest
	 * @return
	 * @throws Throwable
	 */
	@Override
	public GetRunIdListResponse getRunIdListRequest( GetRunIdListRequest getRunIdListRequest ) throws Throwable {



		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( getRunIdListRequest == null ) {

			throw new IllegalArgumentException( "getRunIdListRequest cannot be null" );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}

		String fullConnectionURL = connectionURL + WebServiceURLConstants.GET_RUN_ID_LIST_SERVICE;

		/////////////////////


		if ( log.isDebugEnabled() ) {

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  ClientConnectionToServer in project JobManager_Client_Jersey_1.3" );

			log.debug( "Entered getRunIdListRequest( GetRunRequest getRunIdListRequest ). Job id = " + getRunIdListRequest.getJobId() );

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:  fullConnectionURL = " + fullConnectionURL );
		}

		/////////////////////////////



		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );


		boolean successfulCallToServer = false;

		int retryCount = 0;

		GetRunIdListResponse getRunIdListResponse = null;

		while ( ( ! successfulCallToServer ) ) {  //  exit loop when call to server not throw exception or retryCount >= RETRY_COUNT_MAX

			try {
				getRunIdListResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( GetRunIdListResponse.class, getRunIdListRequest );

				successfulCallToServer = true;

			} catch ( Throwable t ) {

				log.info( "Call to server from getRunIdListRequest() threw exception, retryCount = " + retryCount + ", RETRY_COUNT_MAX = " + RETRY_COUNT_MAX, t );

				retryCount++;

				if ( retryCount >= RETRY_COUNT_MAX ) {

					throw new Exception( "!!!!!!!!!!!   Exception connecting to the server.  "
							+ "In getRunIdListRequest(), trying to connect using the URL:  " + fullConnectionURL, t );

//					throw t;
				}

				try {
					Thread.sleep( RETRY_SLEEP_TIME );

				} catch (InterruptedException e) {

					log.info( "Sleep waiting for retry interrupted with InterruptedException", e );
				}
			}
		}


		if ( log.isDebugEnabled() ) {

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:   getRunIdListRequest( GetRunIdListRequest getRunIdListRequest ):  getRunIdListResponse.isErrorResponse() = " + getRunIdListResponse.isErrorResponse() );

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:   getRunIdListRequest( GetRunIdListRequest getRunIdListRequest ):  getRunIdListResponse.getErrorCode() = " + getRunIdListResponse.getErrorCode() );
		}

		return getRunIdListResponse;

	}


	/**
	 * Submit a job to the server
	 *
	 * @param submitJobRequest
	 * @return SubmitJobResponse
	 * @throws Throwable - throws an error if any errors related to submitting the job
	 */
	@Override

	public SubmitJobResponse submitJob( SubmitJobRequest submitJobRequest ) throws Throwable {

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( submitJobRequest == null ) {

			throw new IllegalArgumentException( "submitJobRequest cannot be null" );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}



		String fullConnectionURL = connectionURL + WebServiceURLConstants.SUBMIT_JOB;



		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );


		boolean successfulCallToServer = false;

		int retryCount = 0;

		SubmitJobResponse submitJobResponse = null;

		while ( ( ! successfulCallToServer ) ) {  //  exit loop when call to server not throw exception or retryCount >= RETRY_COUNT_MAX

			try {
				submitJobResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( SubmitJobResponse.class, submitJobRequest );

				successfulCallToServer = true;

			} catch ( Throwable t ) {

				log.info( "Call to server from submitJob() threw exception, retryCount = " + retryCount + ", RETRY_COUNT_MAX = " + RETRY_COUNT_MAX, t );

				retryCount++;

				if ( retryCount >= RETRY_COUNT_MAX ) {

					throw new Exception( "!!!!!!!!!!!   Exception connecting to the server.  "
							+ "In submitJob(), trying to connect using the URL:  " + fullConnectionURL, t );

//					throw t;
				}

				try {
					Thread.sleep( RETRY_SLEEP_TIME );

				} catch (InterruptedException e) {

					log.info( "Sleep waiting for retry interrupted with InterruptedException", e );
				}
			}
		}



		if ( log.isDebugEnabled() ) {

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:   submitJob( SubmitJobRequest submitJobRequest )):  submitJobResponse.isErrorResponse() = " + submitJobResponse.isErrorResponse() );

			log.debug( "JobManager_Client_Jersey_1.3:  ClientConnectionToServer:   submitJob( SubmitJobRequest submitJobRequest ):  submitJobResponse.getErrorCode() = " + submitJobResponse.getErrorCode() );
		}



		return submitJobResponse;
	}


}
