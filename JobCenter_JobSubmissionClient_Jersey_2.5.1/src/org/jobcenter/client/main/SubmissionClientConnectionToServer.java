package org.jobcenter.client.main;



import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.jobcenter.client_exceptions.JobcenterSubmissionGeneralErrorException;
import org.jobcenter.client_exceptions.JobcenterSubmissionHTTPErrorException;
import org.jobcenter.client_exceptions.JobcenterSubmissionIOErrorException;
import org.jobcenter.client_exceptions.JobcenterSubmissionMalformedURLErrorException;
import org.jobcenter.client_exceptions.JobcenterSubmissionServerErrorException;
import org.jobcenter.client_exceptions.JobcenterSubmissionXML_JAXBErrorException;
import org.jobcenter.constants.Constants;
import org.jobcenter.constants.WebServiceURLConstants;
import org.jobcenter.coreinterfaces.JobSubmissionInterface;
import org.jobcenter.coreinterfaces.JobSubmissionJobInterface;
import org.jobcenter.request.SubmitJobRequest;
import org.jobcenter.request.SubmitJobsListWithDependenciesRequest;
import org.jobcenter.response.SubmitJobResponse;
import org.jobcenter.response.SubmitJobsListWithDependenciesResponse;
import org.jobcenter.submission.internal.utils.JobSubmissionTransforms;


/**
 *
 *
 */
public class SubmissionClientConnectionToServer implements JobSubmissionInterface {


	private static Logger log = Logger.getLogger(SubmissionClientConnectionToServer.class);


	private static final String CONNECTION_URL_EXTENSION

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.SUBMIT_JOB;
	
	private static final String SUBMIT_JOB_LIST_WITH_DEPENDENCIES_CONNECTION_URL_EXTENSION

		= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.SUBMIT_JOBS_LIST_WITH_DEPENDENCIES;



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


		jersey_JAX_RS_Client = ClientBuilder.newClient();   // Client.create();

	}


	@Override
	public void destroy()  {


		if ( jersey_JAX_RS_Client != null ) {

			jersey_JAX_RS_Client.close();  // .destroy();

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
	 * @throws JobcenterSubmissionServerErrorException.  Also throws other Runtime Exceptions
	 */
	@Override

	public int submitJob( String requestTypeName, Integer requestId, String jobTypeName, String submitter, Integer priority, Map<String, String> jobParameters ) throws JobcenterSubmissionServerErrorException {

		return submitJob(requestTypeName, requestId, jobTypeName, submitter, priority, null /* requiredExecutionThreads */
				, jobParameters );
	
	}
	

	/* (non-Javadoc)
	 * @see org.jobcenter.coreinterfaces.JobSubmissionInterface#submitJob(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.util.Map)
	 */
	
	/**
	 * @param requestTypeName - the name of the request type
	 * @param requestId - Pass in to relate the submitted job to an existing requestId.  Pass in null otherwise
	 * @param jobTypeName - the name of the job type
	 * @param submitter
	 * @param priority - optional, if null, the priority on the job_type table is used
	 * @param requiredExecutionThreads - optional, 
	 *             if not null:
	 *                if the client max threads >= requiredExecutionThreads
	 *                the server will send no job until the client has available threads >= requiredExecutionThreads
	 *                
	 * @param jobParameters
	 *
	 * @return requestId - the next assigned id related to the particular requestTypeName.  Will return the passed in requestId if one is provided ( not null )
	 *
	 * @throws IllegalStateException - The connectionURL to the server is not configured
	 * @throws IllegalArgumentException - jobTypeName cannot be null
	 *
	 * These Exceptions are only thrown from JobCenter_JobSubmissionClient_Plain_Java
	 *
	 * @throws JobcenterSubmissionGeneralErrorException - An error condition not covered by any of the other exceptions thrown by this method
	 * @throws JobcenterSubmissionServerErrorException - The Jobcenter code on the server has refused this submit request or has experienced an error
	 * @throws JobcenterSubmissionHTTPErrorException - HTTP response code returned as a result of sending this request to the server
	 * @throws JobcenterSubmissionMalformedURLErrorException - The connection URL cannot be processed into a URL object
	 * @throws JobcenterSubmissionIOErrorException - An IOException was thrown
	 * @throws JobcenterSubmissionXML_JAXBErrorException - A JAXB Exception was thrown marshalling or unmarshalling the XML to/from Java object
	 */
	
	@Override
	public int submitJob(String requestTypeName, Integer requestId,	String jobTypeName, String submitter, Integer priority,	Integer requiredExecutionThreads, Map<String, String> jobParameters)
			throws 
			JobcenterSubmissionGeneralErrorException,
			JobcenterSubmissionServerErrorException,
			JobcenterSubmissionHTTPErrorException,
			JobcenterSubmissionMalformedURLErrorException,
			JobcenterSubmissionIOErrorException,
			JobcenterSubmissionXML_JAXBErrorException {
		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jobTypeName == null ) {

			throw new IllegalArgumentException( "name cannot be null" );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}


//		String fullConnectionURL = connectionURL + CONNECTION_URL_EXTENSION;


		SubmitJobRequest submitJobRequest = new SubmitJobRequest();

		submitJobRequest.setNodeName( submissionNodeName );

		submitJobRequest.setRequestTypeName( requestTypeName );
		submitJobRequest.setRequestId( requestId );

		submitJobRequest.setJobTypeName( jobTypeName );

		submitJobRequest.setPriority (priority );
		submitJobRequest.setRequiredExecutionThreads( requiredExecutionThreads );
		submitJobRequest.setSubmitter( submitter );
		submitJobRequest.setJobParameters( jobParameters );

//		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );
//
//		SubmitJobResponse submitJobResponse = r.type(MediaType.APPLICATION_XML)
//					.accept(MediaType.APPLICATION_XML)
//					.post( SubmitJobResponse.class, submitJobRequest );


		WebTarget target = jersey_JAX_RS_Client.target(connectionURL).path(CONNECTION_URL_EXTENSION);

		SubmitJobResponse submitJobResponse =
			target.request(MediaType.APPLICATION_XML)
			    .post( Entity.entity( submitJobRequest, MediaType.APPLICATION_XML ),
			    		SubmitJobResponse.class);



		if ( log.isInfoEnabled() ) {


			log.info( "JobCenter_JobSubmissionClient:  SubmissionClientConnectionToServer:  submitJobResponse.isErrorResponse() = " + submitJobResponse.isErrorResponse() );

			log.info( "JobCenter_JobSubmissionClient:  SubmissionClientConnectionToServer:  submitJobResponse.getErrorCode() = " + submitJobResponse.getErrorCode() );
		}

		if ( submitJobResponse.isErrorResponse() ) {

			String msg = "Submission of job failed.  Error code = " + submitJobResponse.getErrorCode() + ", error code desc = " + submitJobResponse.getErrorCodeDescription()
				+ ", the client's IP address as seen by the server = " + submitJobResponse.getClientIPAddressAtServer()
				+ "\n  requestTypeName = |" + requestTypeName + "|, requestId = " + requestId + ", jobTypeName = |" + jobTypeName + "|.";

			log.error( msg );

			throw new JobcenterSubmissionServerErrorException( submitJobResponse.getErrorCode(),
					submitJobResponse.getErrorCodeDescription(),
					submitJobResponse.getClientIPAddressAtServer(),
					msg );
		}


		return submitJobResponse.getRequestId();
	}


	
	//////////////////////////////////////////////
	
	//////////   Comment out submitJobsWithDependencies(...) since job dependencies is not completely implemented
	
	
//	/* (non-Javadoc)
//	 * @see org.jobcenter.coreinterfaces.JobSubmissionInterface#submitJobsWithDependencies(java.lang.String, java.lang.Integer, java.lang.String, java.util.List)
//	 */
//	@Override
//	public int submitJobsWithDependencies(String requestTypeName,
//			Integer requestId, 
//			String submitter, 
//			List<JobSubmissionJobInterface> jobSubmissionJobsList )
//
//					throws JobcenterSubmissionGeneralErrorException,
//					JobcenterSubmissionServerErrorException,
//					JobcenterSubmissionHTTPErrorException,
//					JobcenterSubmissionMalformedURLErrorException,
//					JobcenterSubmissionIOErrorException,
//					JobcenterSubmissionXML_JAXBErrorException {
//		
//		
//		String fullConnectionURL = connectionURL + SUBMIT_JOB_LIST_WITH_DEPENDENCIES_CONNECTION_URL_EXTENSION;
//
//		SubmitJobsListWithDependenciesRequest submitJobsListWithDependenciesRequest = JobSubmissionTransforms.createSubmitJobsListWithDependenciesRequest( requestTypeName, requestId, submitter, jobSubmissionJobsList, submissionNodeName );
//
//		WebTarget target = jersey_JAX_RS_Client.target(connectionURL).path(CONNECTION_URL_EXTENSION);
//
//		SubmitJobsListWithDependenciesResponse submitJobsListWithDependenciesResponse =
//			target.request(MediaType.APPLICATION_XML)
//			    .post( Entity.entity( submitJobsListWithDependenciesRequest, MediaType.APPLICATION_XML ),
//			    		SubmitJobsListWithDependenciesResponse.class);
//
//		
//		if ( log.isInfoEnabled() ) {
//
//
//			log.info( "JobCenter_JobSubmissionClient:  SubmissionClientConnectionToServer:  submitJobsListWithDependenciesResponse.isErrorResponse() = " + submitJobsListWithDependenciesResponse.isErrorResponse() );
//
//			log.info( "JobCenter_JobSubmissionClient:  SubmissionClientConnectionToServer:  submitJobsListWithDependenciesResponse.getErrorCode() = " + submitJobsListWithDependenciesResponse.getErrorCode() );
//		}
//
//		if ( submitJobsListWithDependenciesResponse.isErrorResponse() ) {
//
//			String msg = "Submission of jobs failed.  Error code = " + submitJobsListWithDependenciesResponse.getErrorCode() + ", error code desc = " + submitJobsListWithDependenciesResponse.getErrorCodeDescription()
//				+ ", the client's IP address as seen by the server = " + submitJobsListWithDependenciesResponse.getClientIPAddressAtServer()
//				+ "\n  requestTypeName = |" + requestTypeName + "|, requestId = " + requestId + ".";
//
//			log.error( msg );
//
//			throw new JobcenterSubmissionServerErrorException( submitJobsListWithDependenciesResponse.getErrorCode(),
//					submitJobsListWithDependenciesResponse.getErrorCodeDescription(),
//					submitJobsListWithDependenciesResponse.getClientIPAddressAtServer(),
//					msg );
//		}
//
//
//		return submitJobsListWithDependenciesResponse.getRequestId();
//		
//
//	}



}
