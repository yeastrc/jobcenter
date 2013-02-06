package org.jobcenter.client.main;



import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import org.jobcenter.constants.Constants;
import org.jobcenter.constants.WebServiceURLConstants;
import org.jobcenter.coreinterfaces.JobSubmissionInterface;
import org.jobcenter.request.SubmitJobRequest;
import org.jobcenter.response.SubmitJobResponse;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 *
 *
 */
public class SubmissionClientConnectionToServer implements JobSubmissionInterface {


	private static Logger log = Logger.getLogger(SubmissionClientConnectionToServer.class);


	private static final String CONNECTION_URL_EXTENSION

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.SUBMIT_JOB;


	private String connectionURL = null;


	private String submissionNodeName = Constants.SUBMISSION_CLIENT_NODE_NAME_DEFAULT;


	private Client jersey_JAX_RS_Client = null;


	public static JobSubmissionInterface getInstance() {

		return new SubmissionClientConnectionToServer();
	}


	@Override
	public void init( String connectionURL ) throws Throwable {

		log.info( "Entered org.jobcenter.client.main.SubmissionClientConnectionToServer.init() in project JobCenter_JobSubmissionClient" );

		if ( connectionURL == null || connectionURL.isEmpty() ) {

			throw new IllegalArgumentException( "connectionURL cannot be null or empty" );
		}



		this.connectionURL = connectionURL;


		jersey_JAX_RS_Client = Client.create();

	}


	@Override
	public void destroy()  {


		if ( jersey_JAX_RS_Client != null ) {

			jersey_JAX_RS_Client.destroy();

			jersey_JAX_RS_Client = null;
		}
	}


	@Override
	public void setNodeName(String nodeName) {

		this.submissionNodeName = nodeName;
	}


//	  @see org.jobcenter.coreinterfaces.JobCenterJobSubmissionInterface#submitJob(java.lang.String, java.lang.String, int, java.util.Map)

	/**
	 * @param requestTypeName - the name of the request type
	 * @param requestId - Pass in to relate the submitted job to an existing requestId.  Pass in null otherwise
	 * @param jobTypeName - the name of the job type
	 * @param submitter
	 * @param priority
	 * @param jobParameters
	 * @return requestId - the next assigned id related to the particular requestTypeName.  Will return the passed in requestId if one is provided ( not null )
	 * @throws Throwable - throws an error if any errors related to submitting the job
	 */
	@Override

	public int submitJob( String requestTypeName, Integer requestId, String jobTypeName, String submitter, Integer priority, Map<String, String> jobParameters ) throws Throwable {

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jobTypeName == null ) {

			throw new IllegalArgumentException( "name cannot be null" );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}


		String fullConnectionURL = connectionURL + CONNECTION_URL_EXTENSION;


		SubmitJobRequest submitJobRequest = new SubmitJobRequest();

		submitJobRequest.setNodeName( submissionNodeName );

		submitJobRequest.setRequestTypeName( requestTypeName );
		submitJobRequest.setRequestId( requestId );

		submitJobRequest.setJobTypeName( jobTypeName );

		submitJobRequest.setPriority (priority );
		submitJobRequest.setSubmitter( submitter );
		submitJobRequest.setJobParameters( jobParameters );

		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );

		SubmitJobResponse submitJobResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( SubmitJobResponse.class, submitJobRequest );

		if ( log.isInfoEnabled() ) {


			log.info( "JobCenter_JobSubmissionClient:  SubmissionClientConnectionToServer:  submitJobResponse.isErrorResponse() = " + submitJobResponse.isErrorResponse() );

			log.info( "JobCenter_JobSubmissionClient:  SubmissionClientConnectionToServer:  submitJobResponse.getErrorCode() = " + submitJobResponse.getErrorCode() );
		}

		if ( submitJobResponse.isErrorResponse() ) {

			String msg = "Submission of job failed.  Error code = " + submitJobResponse.getErrorCode() + ", error code desc = " + submitJobResponse.getErrorCodeDescription()
				+ ", the client's IP address as seen by the server = " + submitJobResponse.getClientIPAddressAtServer()
				+ "\n  requestTypeName = |" + requestTypeName + "|, requestId = " + requestId + ", jobTypeName = |" + jobTypeName + "|.";

			log.error( msg );

			throw new Exception( msg );
		}


		return submitJobResponse.getRequestId();
	}




}
