package org.jobcenter.guiclient;

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;


import org.jobcenter.constants.Constants;
import org.jobcenter.constants.WebServiceURLConstants;
import org.jobcenter.dto.*;
import org.jobcenter.guiclient.response.GUIListJobsResponse;
import org.jobcenter.nondbdto.ClientConnectedDTO;
import org.jobcenter.request.*;
import org.jobcenter.response.*;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class GUIConnectionToServerClient {



	private static Logger log = Logger.getLogger(GUIConnectionToServerClient.class);


	private static final String URL_EXTENSION_LIST_JOBS

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.GUI_LIST_JOBS;

	private static final String URL_EXTENSION_LIST_REQUESTS

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.GUI_LIST_REQUESTS;

	private static final String URL_EXTENSION_VIEW_JOB

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.GUI_VIEW_JOB;

	private static final String URL_EXTENSION_REQUEUE_JOB

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.REQUEUE_JOB;

	private static final String URL_EXTENSION_CANCEL_JOB

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.CANCEL_JOB;

	private static final String URL_EXTENSION_UPDATE_JOB_PRIORITY

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.JOB_CHANGE_PRIORITY;

	private static final String URL_EXTENSION_LIST_JOB_TYPES

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.GUI_LIST_JOB_TYPES;

	private static final String URL_EXTENSION_LIST_REQUEST_TYPES

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.GUI_LIST_REQUEST_TYPES;


	private static final String URL_EXTENSION_GUI_LIST_CLIENTS_STATUS

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.GUI_LIST_CLIENTS_STATUS;

	private static final String URL_EXTENSION_GUI_LIST_CLIENTS_CONNECTED

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.GUI_LIST_CLIENTS_CONNECTED;


	private static final String URL_EXTENSION_GUI_LIST_CLIENTS_USING_SAME_NODE_NAME

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.GUI_LIST_CLIENTS_USING_SAME_NODE_NAME;

	private static final String URL_EXTENSION_GUI_LIST_CLIENTS_FAILED_TO_CONNECT

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.GUI_LIST_CLIENTS_FAILED_TO_CONNECT;




	private String connectionURL = null;


	private String guiNodeName = Constants.GUI_CLIENT_NODE_NAME_DEFAULT;


	private Client jersey_JAX_RS_Client = null;

	private boolean initialized = false;


	public static GUIConnectionToServerClient getInstance() {

		return new GUIConnectionToServerClient();
	}


	/**
	 * @param connectionURL
	 * @throws Throwable
	 */
	public void init( String connectionURL ) throws Throwable {

		log.info( "Entered init() in project JobCenter_GUIClient" );

		if ( connectionURL == null || connectionURL.isEmpty() ) {

			throw new IllegalArgumentException( "connectionURL cannot be null or empty" );
		}



		this.connectionURL = connectionURL;


//		ClassLoader thisClassLoader = this.getClass().getClassLoader();
//
//		InputStream props = thisClassLoader.getResourceAsStream( PROPERTIES_FILE_NAME );
//
//		if ( props == null ) {
//
//			String msg = "JobCenter_Client_Jersey_1.3:  ClientConnectionToServer:    Properties file '" + PROPERTIES_FILE_NAME + "' not found.";
//
//			log.error( msg );
//
//			throw new Exception( msg );
//
//		} else {
//
//			Properties configProps = new Properties();
//
//			configProps.load(props);
//
//			log.info( "JobCenter_Client_Jersey_1.3:  ClientConnectionToServer:  config.location = " + configProps.getProperty("config.location") );
//
//			connectionURL = configProps.getProperty("server.url") + "/services/";
//
//		}

		jersey_JAX_RS_Client = Client.create();

		initialized = true;
	}


	/**
	 *
	 */
	public void destroy()  {

		initialized = false;

		if ( jersey_JAX_RS_Client != null ) {

			jersey_JAX_RS_Client.destroy();

			jersey_JAX_RS_Client = null;
		}

	}


	/**
	 * @param nodeName
	 */
	public void setNodeName(String nodeName) {

		this.guiNodeName = nodeName;
	}





	/**
	 * @param statusIds
	 * @param requestTypeNames - pass null if not used
	 * @param requestId - pass null if not used
	 * @param jobTypeNames - pass null if not used
	 * @param submitter - pass null if not used
	 * @param indexStart - set to null for starting at first record
	 * @param jobsReturnCountMax - pass null if not used
	 * @return
	 * @throws Throwable
	 */
	public GUIListJobsResponse listJobs( Set<Integer> statusIds,
			Set<String> requestTypeNames,
			Integer requestId,
			Set<String> jobTypeNames,
			String submitter,
			Integer indexStart,         //  set to null for starting at first record
			Integer jobsReturnCountMax ) throws Throwable {

		if ( ! initialized ) {

			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}

		String fullConnectionURL = connectionURL + URL_EXTENSION_LIST_JOBS;


		ListJobsRequest listJobsRequest = new ListJobsRequest();

		listJobsRequest.setNodeName( guiNodeName );

		listJobsRequest.setStatusIds( statusIds );

		listJobsRequest.setRequestId( requestId );

		listJobsRequest.setJobTypeNames( jobTypeNames );

		listJobsRequest.setRequestTypeNames( requestTypeNames );

		listJobsRequest.setSubmitter( submitter );

		listJobsRequest.setIndexStart( indexStart );

		listJobsRequest.setJobsReturnCountMax( jobsReturnCountMax );

		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );

		ListJobsResponse listJobsResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( ListJobsResponse.class, listJobsRequest );

		if ( log.isInfoEnabled() ) {

			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  listJobsResponse.isErrorResponse() = " + listJobsResponse.isErrorResponse()
					+ ", listJobsResponse.getErrorCode() = " + listJobsResponse.getErrorCode() );
		}

		if ( listJobsResponse.isErrorResponse() ) {

			String requestTypeNamesString = null;

			if ( requestTypeNames != null && ( ! requestTypeNames.isEmpty() ) ) {

				StringBuilder requestTypeNamesSB = new StringBuilder( 1000 );

				for ( String requestTypeName : requestTypeNames ) {

					requestTypeNamesSB.append( "|" );
					requestTypeNamesSB.append( requestTypeName );
					requestTypeNamesSB.append( "|, " );
				}

				requestTypeNamesString = requestTypeNamesSB.toString();
			}

			String jobTypeNamesString = null;

			if ( jobTypeNames != null && ( ! jobTypeNames.isEmpty() ) ) {

				StringBuilder jobTypeNamesSB = new StringBuilder( 1000 );

				for ( String jobTypeName : jobTypeNames ) {

					jobTypeNamesSB.append( "|" );
					jobTypeNamesSB.append( jobTypeName );
					jobTypeNamesSB.append( "|, " );
				}

				jobTypeNamesString = jobTypeNamesSB.toString();
			}


			String msg = "Retrieval of list of jobs failed.  Error code = " + listJobsResponse.getErrorCode() + ", error code desc = " + listJobsResponse.getErrorCodeDescription()
				+ ", the client's IP address as seen by the server = " + listJobsResponse.getClientIPAddressAtServer()
				+ "\n  requestTypeNames = " + requestTypeNamesString + ", requestId = " + requestId + ", jobTypeName = " + jobTypeNamesString + ".";

			log.error( msg );

			throw new Exception( msg );
		}


		GUIListJobsResponse guiListJobsResponse = new GUIListJobsResponse();

		guiListJobsResponse.setJobCount( listJobsResponse.getJobCount() );

		List<Job> jobs = listJobsResponse.getJobs();

		guiListJobsResponse.setJobs( jobs );

		return guiListJobsResponse;
	}




	/**
	 * @param statusIds
	 * @param requestTypeName
	 * @param requestId
	 * @param jobTypeName
	 * @param submitter
	 * @param indexStart
	 * @param requestsReturnCountMax
	 * @return
	 * @throws Throwable
	 */
	public List<RequestDTO> listRequests( Set<Integer> statusIds,
			String requestTypeName,
			Integer requestId,
			String jobTypeName,
			String submitter,
			Integer indexStart,         //  set to null for starting at first record
			Integer requestsReturnCountMax ) throws Throwable {

		if ( ! initialized ) {

			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}

		List<RequestDTO> requests = null;


		String fullConnectionURL = connectionURL + URL_EXTENSION_LIST_REQUESTS;


		ListRequestsRequest listRequestsRequest = new ListRequestsRequest();

		listRequestsRequest.setNodeName( guiNodeName );

		listRequestsRequest.setStatusIds( statusIds );

		listRequestsRequest.setRequestId( requestId );

		listRequestsRequest.setJobTypeName( jobTypeName );

		listRequestsRequest.setRequestTypeName( requestTypeName );

		listRequestsRequest.setSubmitter( submitter );

		listRequestsRequest.setIndexStart( indexStart );

		listRequestsRequest.setJobsReturnCountMax( requestsReturnCountMax );

		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );

		ListRequestsResponse listRequestsResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( ListRequestsResponse.class, listRequestsRequest );

		if ( log.isInfoEnabled() ) {

			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  listRequestsResponse.isErrorResponse() = " + listRequestsResponse.isErrorResponse()
					+ ", listRequestsResponse.getErrorCode() = " + listRequestsResponse.getErrorCode()
					+ ", the client's IP address as seen by the server = " + listRequestsResponse.getClientIPAddressAtServer() );
		}

		if ( listRequestsResponse.isErrorResponse() ) {

			String msg = "Retrieval of list of requests failed.  Error code = " + listRequestsResponse.getErrorCode() + ", error code desc = " + listRequestsResponse.getErrorCodeDescription()
				+ ", the client's IP address as seen by the server = " + listRequestsResponse.getClientIPAddressAtServer()
				+ "\n  requestTypeName = |" + requestTypeName + "|, requestId = " + requestId + ", jobTypeName = |" + jobTypeName + "|.";

			log.error( msg );

			throw new Exception( msg );
		}

		requests = listRequestsResponse.getRequests();

		return requests;
	}



	/**
	 * @param jobId
	 * @return
	 * @throws Throwable
	 */
	public Job viewJob( int jobId ) throws Throwable {

		if ( ! initialized ) {

			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}

		Job job = null;


		String fullConnectionURL = connectionURL + URL_EXTENSION_VIEW_JOB;


		ViewJobRequest viewJobRequest = new ViewJobRequest();

		viewJobRequest.setNodeName( guiNodeName );

		viewJobRequest.setJobId( jobId );

		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );

		ViewJobResponse viewJobResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( ViewJobResponse.class, viewJobRequest );

		if ( log.isInfoEnabled() ) {

			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  viewJobResponse.isErrorResponse() = " + viewJobResponse.isErrorResponse()
					+ ", viewJobResponse.getErrorCode() = " + viewJobResponse.getErrorCode()
					+ ", the client's IP address as seen by the server = " + viewJobResponse.getClientIPAddressAtServer() );
		}

		if ( viewJobResponse.isErrorResponse() ) {

			String msg = "Retrieval of job failed.  Error code = " + viewJobResponse.getErrorCode() + ", error code desc = " + viewJobResponse.getErrorCodeDescription()
				+ ", the client's IP address as seen by the server = " + viewJobResponse.getClientIPAddressAtServer()
				+ "\n  job id = |" + jobId + "|.";

			log.error( msg );

			throw new Exception( msg );
		}

		job = viewJobResponse.getJob();

		return job;
	}


//
//	/**
//	 * @param jobId
//	 * @param statusId
//	 * @param dbRecordVersionNumber
//	 * @throws Throwable
//	 */
//	public void updateJobStatus( int jobId, int statusId, int dbRecordVersionNumber ) throws Throwable {
//
//		//  TODO  return different things if there is a general failure compared to the version being different
//
//		if ( ! initialized ) {
//
//			throw new IllegalStateException( "not initialized.  init() was never called." );
//		}
//
//		if ( connectionURL == null ) {
//
//			throw new IllegalStateException( "The connectionURL to the server is not configured." );
//		}
//
//		if ( jersey_JAX_RS_Client == null ) {
//
//			throw new IllegalStateException( "jerseyClient == null" );
//		}
//
//
//		String fullConnectionURL = connectionURL + URL_EXTENSION_UPDATE_JOB_STATUS;
//
//		UpdateJobStatusRequest updateJobStatusRequest = new UpdateJobStatusRequest();
//
//		updateJobStatusRequest.setNodeName( guiNodeName );
//
//		Job job = new Job();
//
//		updateJobStatusRequest.setJob( job );
//
//		job.setId( jobId );
//
//		job.setStatusId( statusId );
//
//		job.setDbRecordVersionNumber( dbRecordVersionNumber );
//
//		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );
//
//		UpdateJobStatusResponse updateJobStatusResponse = r.type(MediaType.APPLICATION_XML)
//					.accept(MediaType.APPLICATION_XML)
//					.post( UpdateJobStatusResponse.class, updateJobStatusRequest );
//
//		if ( log.isInfoEnabled() ) {
//
//			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  updateJobStatusResponse.isErrorResponse() = " + updateJobStatusResponse.isErrorResponse()
//					+ ", updateJobStatusResponse.getErrorCode() = " + updateJobStatusResponse.getErrorCode()
//					+ ", the client's IP address as seen by the server = " + updateJobStatusResponse.getClientIPAddressAtServer() );
//		}
//
//		if ( updateJobStatusResponse.isErrorResponse() ) {
//
//			String msg = "Update of job status failed.  Error code = " + updateJobStatusResponse.getErrorCode() + ", error code desc = " + updateJobStatusResponse.getErrorCodeDescription()
//				+ ", the client's IP address as seen by the server = " + updateJobStatusResponse.getClientIPAddressAtServer()
//				+ "\n  job id = |" + jobId + "|.";
//
//			log.error( msg );
//
//			throw new Exception( msg );
//		}
//	}






	/**
	 * @param jobId
	 * @param dbRecordVersionNumber - Pass "null" if not enforce
	 * @return
	 * @throws Throwable
	 */
	public GUICallStatus requeueJob( int jobId, Integer dbRecordVersionNumber ) throws Throwable {

		//  TODO  return different things if there is a general failure compared to the version being different

		if ( ! initialized ) {

			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}


		String fullConnectionURL = connectionURL + URL_EXTENSION_REQUEUE_JOB;

		RequeueJobRequest requeueJobRequest = new RequeueJobRequest();

		requeueJobRequest.setNodeName( guiNodeName );

		Job job = new Job();

		requeueJobRequest.setJob( job );

		job.setId( jobId );

		if ( dbRecordVersionNumber != null ) {

			job.setDbRecordVersionNumber( dbRecordVersionNumber );
		}

		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );

		RequeueJobResponse requeueJobResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( RequeueJobResponse.class, requeueJobRequest );

		if ( log.isInfoEnabled() ) {

			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  requeueJobResponse.isErrorResponse() = " + requeueJobResponse.isErrorResponse()
					+ ", requeueJobResponse.getErrorCode() = " + requeueJobResponse.getErrorCode()
					+ ", the client's IP address as seen by the server = " + requeueJobResponse.getClientIPAddressAtServer() );
		}

		if ( requeueJobResponse.isErrorResponse() ) {

			if ( requeueJobResponse.getRequeueJobResponseErrorCode() ==  requeueJobResponse.REQUEUE_ERROR_DB_RECORD_VERSION_NUMBER_OUT_OF_SYNC ) {

				return GUICallStatus.FAILED_VERSION_NOT_MATCH_DB;

			} else if ( requeueJobResponse.getRequeueJobResponseErrorCode() ==  requeueJobResponse.REQUEUE_ERROR_JOB_NO_LONGER_REQUEUABLE ) {

				return GUICallStatus.FAILED_JOB_NO_LONGER_REQUEUEABLE;

			} else if ( requeueJobResponse.getRequeueJobResponseErrorCode() ==  requeueJobResponse.REQUEUE_ERROR_JOB_NOT_FOUND_IN_DB ) {

				return GUICallStatus.FAILED_JOB_NOT_FOUND;

			} else {
				String msg = "Requeue of job failed.  Error code = " + requeueJobResponse.getErrorCode() + ", error code desc = " + requeueJobResponse.getErrorCodeDescription()
					+ ", the client's IP address as seen by the server = " + requeueJobResponse.getClientIPAddressAtServer()
				+ "\n  job id = |" + jobId + "|.";

				log.error( msg );

				throw new Exception( msg );
			}

		}

		return GUICallStatus.SUCCESS;
	}





	/**
	 * @param jobId
	 * @param dbRecordVersionNumber - Pass "null" if not enforce
	 * @return
	 * @throws Throwable
	 */
	public GUICallStatus cancelJob( int jobId, Integer dbRecordVersionNumber ) throws Throwable {

		//  TODO  return different things if there is a general failure compared to the version being different

		if ( ! initialized ) {

			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}


		String fullConnectionURL = connectionURL + URL_EXTENSION_CANCEL_JOB;

		CancelJobRequest cancelJobRequest = new CancelJobRequest();

		cancelJobRequest.setNodeName( guiNodeName );

		Job job = new Job();

		cancelJobRequest.setJob( job );

		job.setId( jobId );

		if ( dbRecordVersionNumber != null ) {

			job.setDbRecordVersionNumber( dbRecordVersionNumber );
		}


		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );

		CancelJobResponse cancelJobResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( CancelJobResponse.class, cancelJobRequest );

		if ( log.isInfoEnabled() ) {

			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  cancelJobResponse.isErrorResponse() = " + cancelJobResponse.isErrorResponse()
					+ ", cancelJobResponse.getErrorCode() = " + cancelJobResponse.getErrorCode()
					+ ", the client's IP address as seen by the server = " + cancelJobResponse.getClientIPAddressAtServer() );
		}


		if ( cancelJobResponse.isErrorResponse() ) {

			if ( cancelJobResponse.getCancelJobResponseErrorCode() ==  cancelJobResponse.CANCEL_ERROR_DB_RECORD_VERSION_NUMBER_OUT_OF_SYNC ) {

				return GUICallStatus.FAILED_VERSION_NOT_MATCH_DB;

			} else if ( cancelJobResponse.getCancelJobResponseErrorCode() ==  cancelJobResponse.CANCEL_ERROR_JOB_NOT_CANCELABLE ) {

				return GUICallStatus.FAILED_JOB_NO_LONGER_CANCELABLE;

			} else if ( cancelJobResponse.getCancelJobResponseErrorCode() ==  cancelJobResponse.CANCEL_ERROR_JOB_NOT_FOUND_IN_DB ) {

				return GUICallStatus.FAILED_JOB_NOT_FOUND;

			} else {
				String msg = "Cancel of job failed.  Error code = " + cancelJobResponse.getErrorCode() + ", error code desc = " + cancelJobResponse.getErrorCodeDescription()
					+ ", the client's IP address as seen by the server = " + cancelJobResponse.getClientIPAddressAtServer()
					+ "\n  job id = |" + jobId + "|.";

				log.error( msg );

				throw new Exception( msg );
			}

		}

		return GUICallStatus.SUCCESS;
	}




	/**
	 * @param newPriority
	 * @param jobId
	 * @param dbRecordVersionNumber - Pass "null" if not enforce
	 * @return
	 * @throws Throwable
	 */
	public GUICallStatus changeJobPriority( int newPriority, int jobId, Integer dbRecordVersionNumber ) throws Throwable {

		//  TODO  return different things if there is a general failure compared to the version being different

		if ( ! initialized ) {

			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}


		String fullConnectionURL = connectionURL + URL_EXTENSION_UPDATE_JOB_PRIORITY;

		JobChangePriorityRequest JobChangePriorityRequest = new JobChangePriorityRequest();

		JobChangePriorityRequest.setNodeName( guiNodeName );

		Job job = new Job();

		JobChangePriorityRequest.setJob( job );

		job.setId( jobId );

		job.setPriority( newPriority );

		if ( dbRecordVersionNumber != null ) {

			job.setDbRecordVersionNumber( dbRecordVersionNumber );
		}


		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );

		JobChangePriorityResponse JobChangePriorityResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( JobChangePriorityResponse.class, JobChangePriorityRequest );

		if ( log.isInfoEnabled() ) {

			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  JobChangePriorityResponse.isErrorResponse() = " + JobChangePriorityResponse.isErrorResponse()
					+ ", JobChangePriorityResponse.getErrorCode() = " + JobChangePriorityResponse.getErrorCode()
					+ ", the client's IP address as seen by the server = " + JobChangePriorityResponse.getClientIPAddressAtServer() );
		}

		if ( JobChangePriorityResponse.isErrorResponse() ) {

			if ( JobChangePriorityResponse.getJobChangePriorityResponseErrorCode() ==  JobChangePriorityResponse.CHANGE_PRIORITY_ERROR_DB_RECORD_VERSION_NUMBER_OUT_OF_SYNC ) {

				return GUICallStatus.FAILED_VERSION_NOT_MATCH_DB;

			} else if ( JobChangePriorityResponse.getJobChangePriorityResponseErrorCode() ==  JobChangePriorityResponse.CHANGE_PRIORITY_ERROR_JOB_NOT_FOUND_IN_DB ) {

				return GUICallStatus.FAILED_JOB_NOT_FOUND;

			} else {

				String msg = "Update of job priority failed.  Error code = " + JobChangePriorityResponse.getErrorCode() + ", error code desc = " + JobChangePriorityResponse.getErrorCodeDescription()
					+ ", the client's IP address as seen by the server = " + JobChangePriorityResponse.getClientIPAddressAtServer()
				+ "\n  job id = |" + jobId + "|.";

				log.error( msg );

				throw new Exception( msg );
			}
		}

		return GUICallStatus.SUCCESS;

	}

	/**
	 * @return
	 * @throws Throwable
	 */
	public List<JobType>  listJobTypes(  ) throws Throwable {

		return listJobTypes( null );
		
	}
	

	/**
	 * @return
	 * @throws Throwable
	 */
	public List<JobType>  listJobTypes( List<String> jobTypeNames ) throws Throwable {

		if ( ! initialized ) {

			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}

		List<JobType> jobTypes = null;


		String fullConnectionURL = connectionURL + URL_EXTENSION_LIST_JOB_TYPES;


		ListJobTypesRequest listJobTypesRequest = new ListJobTypesRequest();

		listJobTypesRequest.setNodeName( guiNodeName );
		
		listJobTypesRequest.setJobTypeNames( jobTypeNames );


		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );

		ListJobTypesResponse listJobTypesResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( ListJobTypesResponse.class, listJobTypesRequest );

		if ( log.isInfoEnabled() ) {

			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  listJobTypesResponse.isErrorResponse() = " + listJobTypesResponse.isErrorResponse()
					+ ", listJobTypesResponse.getErrorCode() = " + listJobTypesResponse.getErrorCode()
					+ ", the client's IP address as seen by the server = " + listJobTypesResponse.getClientIPAddressAtServer() );
		}

		if ( listJobTypesResponse.isErrorResponse() ) {

			String msg = "Retrieval of job types failed.  Error code = " + listJobTypesResponse.getErrorCode() + ", error code desc = " + listJobTypesResponse.getErrorCodeDescription()
			+ ", the client's IP address as seen by the server = " + listJobTypesResponse.getClientIPAddressAtServer();

			log.error( msg );

			throw new Exception( msg );
		}

		jobTypes = listJobTypesResponse.getJobTypes();

		return jobTypes;
	}



	/**
	 * @return
	 * @throws Throwable
	 */
	public List<RequestTypeDTO>  listRequestTypes(  ) throws Throwable {

		if ( ! initialized ) {

			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}

		List<RequestTypeDTO> requestTypes = null;


		String fullConnectionURL = connectionURL + URL_EXTENSION_LIST_REQUEST_TYPES;


		ListRequestTypesRequest listRequestTypesRequest = new ListRequestTypesRequest();

		listRequestTypesRequest.setNodeName( guiNodeName );


		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );

		ListRequestTypesResponse listRequestTypesResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( ListRequestTypesResponse.class, listRequestTypesRequest );

		if ( log.isInfoEnabled() ) {

			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  listRequestTypesResponse.isErrorResponse() = " + listRequestTypesResponse.isErrorResponse()
					+ ", listRequestTypesResponse.getErrorCode() = " + listRequestTypesResponse.getErrorCode()
					+ ", the client's IP address as seen by the server = " + listRequestTypesResponse.getClientIPAddressAtServer() );
		}

		if ( listRequestTypesResponse.isErrorResponse() ) {

			String msg = "Retrieval of request types failed.  Error code = " + listRequestTypesResponse.getErrorCode() + ", error code desc = " + listRequestTypesResponse.getErrorCodeDescription()
							+ ", the client's IP address as seen by the server = " + listRequestTypesResponse.getClientIPAddressAtServer();

			log.error( msg );

			throw new Exception( msg );
		}

		requestTypes = listRequestTypesResponse.getRequestTypes();

		return requestTypes;
	}



	/**
	 * @return
	 * @throws Throwable
	 */
	public List<NodeClientStatusDTO>  listClientsStatus(  ) throws Throwable {

		if ( ! initialized ) {

			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}

		List<NodeClientStatusDTO> clientsStatusList = null;


		String fullConnectionURL = connectionURL + URL_EXTENSION_GUI_LIST_CLIENTS_STATUS;


		ListClientsStatusRequest listClientsStatusRequest = new ListClientsStatusRequest();

		listClientsStatusRequest.setNodeName( guiNodeName );


		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );

		ListClientsStatusResponse listClientsStatusResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( ListClientsStatusResponse.class, listClientsStatusRequest );

		if ( log.isInfoEnabled() ) {

			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  listClientsStatusResponse.isErrorResponse() = " + listClientsStatusResponse.isErrorResponse()
					+ ", listClientsStatusResponse.getErrorCode() = " + listClientsStatusResponse.getErrorCode()
					+ ", the client's IP address as seen by the server = " + listClientsStatusResponse.getClientIPAddressAtServer() );
		}

		if ( listClientsStatusResponse.isErrorResponse() ) {

			String msg = "Retrieval of clients status failed.  Error code = " + listClientsStatusResponse.getErrorCode() + ", error code desc = " + listClientsStatusResponse.getErrorCodeDescription()
							+ ", the client's IP address as seen by the server = " + listClientsStatusResponse.getClientIPAddressAtServer();

			log.error( msg );

			throw new Exception( msg );
		}

		clientsStatusList = listClientsStatusResponse.getClients();

		return clientsStatusList;
	}



	/**
	 * @return
	 * @throws Throwable
	 */
	public List<ClientConnectedDTO>  retrieveClientsConnectedList(  ) throws Throwable {

		if ( ! initialized ) {

			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}

		List<ClientConnectedDTO> clientConnectedDTOList = null;


		String fullConnectionURL = connectionURL + URL_EXTENSION_GUI_LIST_CLIENTS_CONNECTED;


		GetClientsConnectedListRequest getClientsConnectedListRequest = new GetClientsConnectedListRequest();

		getClientsConnectedListRequest.setNodeName( guiNodeName );


		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );

		GetClientsConnectedListResponse getClientsConnectedListResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( GetClientsConnectedListResponse.class, getClientsConnectedListRequest );

		if ( log.isInfoEnabled() ) {

			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  getClientsConnectedListResponse.isErrorResponse() = " + getClientsConnectedListResponse.isErrorResponse()
					+ ", getClientsConnectedListResponse.getErrorCode() = " + getClientsConnectedListResponse.getErrorCode()
					+ ", the client's IP address as seen by the server = " + getClientsConnectedListResponse.getClientIPAddressAtServer() );
		}

		if ( getClientsConnectedListResponse.isErrorResponse() ) {

			String msg = "Retrieval of clients connected failed.  Error code = " + getClientsConnectedListResponse.getErrorCode() + ", error code desc = " + getClientsConnectedListResponse.getErrorCodeDescription()
							+ ", the client's IP address as seen by the server = " + getClientsConnectedListResponse.getClientIPAddressAtServer() ;

			log.error( msg );

			throw new Exception( msg );
		}

		clientConnectedDTOList = getClientsConnectedListResponse.getClientConnectedDTOList();

		return clientConnectedDTOList;
	}

	/**
	 * @return
	 * @throws Throwable
	 */
	public List<ClientConnectedDTO>  retrieveClientsUsingSameNodeNameList(  ) throws Throwable {

		if ( ! initialized ) {

			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}

		List<ClientConnectedDTO> clientConnectedDTOList = null;


		String fullConnectionURL = connectionURL + URL_EXTENSION_GUI_LIST_CLIENTS_USING_SAME_NODE_NAME;


		GetClientsConnectedListRequest getClientsConnectedListRequest = new GetClientsConnectedListRequest();

		getClientsConnectedListRequest.setNodeName( guiNodeName );


		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );

		GetClientsConnectedListResponse getClientsConnectedListResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( GetClientsConnectedListResponse.class, getClientsConnectedListRequest );

		if ( log.isInfoEnabled() ) {

			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient: retrieveClientsUsingSameNodeNameList():  getClientsConnectedListResponse.isErrorResponse() = " + getClientsConnectedListResponse.isErrorResponse()
					+ ", getClientsConnectedListResponse.getErrorCode() = " + getClientsConnectedListResponse.getErrorCode()
					+ ", the client's IP address as seen by the server = " + getClientsConnectedListResponse.getClientIPAddressAtServer() );
		}

		if ( getClientsConnectedListResponse.isErrorResponse() ) {

			String msg = "Retrieval of clients connected failed.  Error code = " + getClientsConnectedListResponse.getErrorCode() + ", error code desc = " + getClientsConnectedListResponse.getErrorCodeDescription()
							+ ", the client's IP address as seen by the server = " + getClientsConnectedListResponse.getClientIPAddressAtServer();

			log.error( msg );

			throw new Exception( msg );
		}

		clientConnectedDTOList = getClientsConnectedListResponse.getClientConnectedDTOList();

		return clientConnectedDTOList;
	}



	/**
	 * @return
	 * @throws Throwable
	 */
	public List<ClientConnectedDTO>  retrieveClientsFailedToConnectTrackingList(  ) throws Throwable {

		if ( ! initialized ) {

			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jersey_JAX_RS_Client == null ) {

			throw new IllegalStateException( "jerseyClient == null" );
		}

		List<ClientConnectedDTO> clientConnectedDTOList = null;


		String fullConnectionURL = connectionURL + URL_EXTENSION_GUI_LIST_CLIENTS_FAILED_TO_CONNECT;


		GetClientsConnectedListRequest getClientsConnectedListRequest = new GetClientsConnectedListRequest();

		getClientsConnectedListRequest.setNodeName( guiNodeName );


		WebResource r = jersey_JAX_RS_Client.resource( fullConnectionURL );

		GetClientsConnectedListResponse getClientsConnectedListResponse = r.type(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post( GetClientsConnectedListResponse.class, getClientsConnectedListRequest );

		if ( log.isInfoEnabled() ) {

			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient: retrieveClientsFailedToConnectTrackingList():  getClientsConnectedListResponse.isErrorResponse() = " + getClientsConnectedListResponse.isErrorResponse()
					+ ", getClientsConnectedListResponse.getErrorCode() = " + getClientsConnectedListResponse.getErrorCode()
					+ ", the client's IP address as seen by the server = " + getClientsConnectedListResponse.getClientIPAddressAtServer() );
		}

		if ( getClientsConnectedListResponse.isErrorResponse() ) {

			String msg = "Retrieval of clients connected failed.  Error code = " + getClientsConnectedListResponse.getErrorCode() + ", error code desc = " + getClientsConnectedListResponse.getErrorCodeDescription()
							+ ", the client's IP address as seen by the server = " + getClientsConnectedListResponse.getClientIPAddressAtServer();

			log.error( msg );

			throw new Exception( msg );
		}

		clientConnectedDTOList = getClientsConnectedListResponse.getClientConnectedDTOList();

		return clientConnectedDTOList;
	}







}
