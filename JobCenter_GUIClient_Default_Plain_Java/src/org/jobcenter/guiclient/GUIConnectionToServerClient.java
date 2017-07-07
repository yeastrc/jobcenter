package org.jobcenter.guiclient;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jobcenter.client_exceptions.JobcenterSubmissionXML_JAXBErrorException;
import org.jobcenter.constants.Constants;
import org.jobcenter.constants.WebServiceURLConstants;
import org.jobcenter.dto.*;
import org.jobcenter.guiclient.exceptions.JobcenterGUI_WebserviceCallErrorException;
import org.jobcenter.guiclient.response.GUIListJobsResponse;
import org.jobcenter.nondbdto.ClientConnectedDTO;
import org.jobcenter.request.*;
import org.jobcenter.response.*;

/**
 * Class for connecting to server, requires Java 8+
 * 
 * Warning:  Update jaxbContext = JAXBContext.newInstance(  for any classes sent or received )
 *
 */
public class GUIConnectionToServerClient {

	private static final String XML_ENCODING_CHARACTER_SET = StandardCharsets.UTF_8.toString();
	private static final int SUCCESS_HTTP_RETURN_CODE = 200;
	private static final String CONTENT_TYPE_SEND_RECEIVE = "application/xml";
	
	
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


	private JAXBContext jaxbContext;

	
//	private volatile byte[] requestMarshalledLastSent;


	private boolean initialized = false;


	public static GUIConnectionToServerClient getInstance() {

		return new GUIConnectionToServerClient();
	}


	/**
	 * @param connectionURL
	 * @throws Exception
	 */
	public void init( String connectionURL ) throws Exception {

//		log.info( "Entered init() in project JobCenter_GUIClient" );

		if ( connectionURL == null || connectionURL.isEmpty() ) {

			throw new IllegalArgumentException( "connectionURL cannot be null or empty" );
		}


		this.connectionURL = connectionURL;

		try { 
			jaxbContext = JAXBContext.newInstance( 

					ListJobsRequest.class,
					ListRequestsRequest.class,
					ViewJobRequest.class,
					RequeueJobRequest.class,
					CancelJobRequest.class,
					JobChangePriorityRequest.class,
					ListJobTypesRequest.class,
					ListRequestTypesRequest.class,
					ListClientsStatusRequest.class,
					GetClientsConnectedListRequest.class,
					GetClientsConnectedListRequest.class,
					GetClientsConnectedListRequest.class,
					
					ListJobsResponse.class,
					ListRequestsResponse.class,
					ViewJobResponse.class,
					RequeueJobResponse.class,
					CancelJobResponse.class,
					JobChangePriorityResponse.class,
					ListJobTypesResponse.class,
					ListRequestTypesResponse.class,
					ListClientsStatusResponse.class,
					GetClientsConnectedListResponse.class,
					GetClientsConnectedListResponse.class,
					GetClientsConnectedListResponse.class );
			
		} catch (JAXBException e) {

			JobcenterSubmissionXML_JAXBErrorException exc = new JobcenterSubmissionXML_JAXBErrorException( "JAXBException Setting up JAXBContext for sending XML to server at URL: " + connectionURL, e );

			exc.setFullConnectionURL( connectionURL );

			throw exc;

		}

		initialized = true;
	}


	/**
	 *
	 */
	public void destroy()  {

		initialized = false;

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
	 * @throws Exception 
	 * @throws Exception
	 */
	public GUIListJobsResponse listJobs( Set<Integer> statusIds,
			Set<String> requestTypeNames,
			Integer requestId,
			Set<String> jobTypeNames,
			String submitter,
			Integer indexStart,         //  set to null for starting at first record
			Integer jobsReturnCountMax ) throws Exception {

		if ( ! initialized ) {
			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {
			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		String webserviceURL = connectionURL + URL_EXTENSION_LIST_JOBS;

		ListJobsRequest webserviceRequest = new ListJobsRequest();
		webserviceRequest.setNodeName( guiNodeName );
		webserviceRequest.setStatusIds( statusIds );
		webserviceRequest.setRequestId( requestId );
		webserviceRequest.setJobTypeNames( jobTypeNames );
		webserviceRequest.setRequestTypeNames( requestTypeNames );
		webserviceRequest.setSubmitter( submitter );
		webserviceRequest.setIndexStart( indexStart );
		webserviceRequest.setJobsReturnCountMax( jobsReturnCountMax );

		Object webserviceResponseAsObject = callActualWebserviceOnServer( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof ListJobsResponse ) ) {
			String msg = "Response unmarshaled to class other than ListJobsResponse.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		ListJobsResponse webserviceResponse = null;
		try {
			webserviceResponse = (ListJobsResponse) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as ListJobsResponse: "
					+ e.toString();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}

//		if ( log.isInfoEnabled() ) {
//
//			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  listJobsResponse.isErrorResponse() = " + listJobsResponse.isErrorResponse()
//					+ ", listJobsResponse.getErrorCode() = " + listJobsResponse.getErrorCode() );
//		}
		
		ListJobsResponse listJobsResponse = webserviceResponse;

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
				+ ", the node name sent to the server: " + guiNodeName
				+ "\n  requestTypeNames = " + requestTypeNamesString + ", requestId = " + requestId + ", jobTypeName = " + jobTypeNamesString + ".";

//			log.error( msg );

			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setServerAppCodeReturnedErrorResponse(true);
			throw exception;
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
	 * @throws Exception 
	 * @throws Exception
	 */
	public List<RequestDTO> listRequests( Set<Integer> statusIds,
			String requestTypeName,
			Integer requestId,
			String jobTypeName,
			String submitter,
			Integer indexStart,         //  set to null for starting at first record
			Integer requestsReturnCountMax ) throws Exception {

		if ( ! initialized ) {

			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		List<RequestDTO> requests = null;


		String webserviceURL = connectionURL + URL_EXTENSION_LIST_REQUESTS;


		ListRequestsRequest webserviceRequest = new ListRequestsRequest();

		webserviceRequest.setNodeName( guiNodeName );

		webserviceRequest.setStatusIds( statusIds );

		webserviceRequest.setRequestId( requestId );

		webserviceRequest.setJobTypeName( jobTypeName );

		webserviceRequest.setRequestTypeName( requestTypeName );

		webserviceRequest.setSubmitter( submitter );

		webserviceRequest.setIndexStart( indexStart );

		webserviceRequest.setJobsReturnCountMax( requestsReturnCountMax );

		Object webserviceResponseAsObject = callActualWebserviceOnServer( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof ListRequestsResponse ) ) {
			String msg = "Response unmarshaled to class other than ListRequestsResponse.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		ListRequestsResponse webserviceResponse = null;
		try {
			webserviceResponse = (ListRequestsResponse) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as ListRequestsResponse: "
					+ e.toString();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		ListRequestsResponse listRequestsResponse = webserviceResponse;

//		if ( log.isInfoEnabled() ) {
//			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  listRequestsResponse.isErrorResponse() = " + listRequestsResponse.isErrorResponse()
//					+ ", listRequestsResponse.getErrorCode() = " + listRequestsResponse.getErrorCode()
//					+ ", the client's IP address as seen by the server = " + listRequestsResponse.getClientIPAddressAtServer() );
//		}

		if ( listRequestsResponse.isErrorResponse() ) {

			String msg = "Retrieval of list of requests failed.  Error code = " + listRequestsResponse.getErrorCode() + ", error code desc = " + listRequestsResponse.getErrorCodeDescription()
				+ ", the client's IP address as seen by the server = " + listRequestsResponse.getClientIPAddressAtServer()
				+ ", the node name sent to the server: " + guiNodeName
				+ "\n  requestTypeName = |" + requestTypeName + "|, requestId = " + requestId + ", jobTypeName = |" + jobTypeName + "|.";

//			log.error( msg );

			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setServerAppCodeReturnedErrorResponse(true);
			throw exception;
		}

		requests = listRequestsResponse.getRequests();

		return requests;
	}



	/**
	 * @param jobId
	 * @return
	 * @throws Exception
	 */
	public Job viewJob( int jobId ) throws Exception {

		if ( ! initialized ) {
			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {
			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		Job job = null;

		String webserviceURL = connectionURL + URL_EXTENSION_VIEW_JOB;

		ViewJobRequest webserviceRequest = new ViewJobRequest();
		webserviceRequest.setNodeName( guiNodeName );
		webserviceRequest.setJobId( jobId );

		Object webserviceResponseAsObject = callActualWebserviceOnServer( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof ViewJobResponse ) ) {
			String msg = "Response unmarshaled to class other than ViewJobResponse.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		ViewJobResponse webserviceResponse = null;
		try {
			webserviceResponse = (ViewJobResponse) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as ViewJobResponse: "
					+ e.toString();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}

		ViewJobResponse viewJobResponse = webserviceResponse;

//		if ( log.isInfoEnabled() ) {
//
//			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  viewJobResponse.isErrorResponse() = " + viewJobResponse.isErrorResponse()
//					+ ", viewJobResponse.getErrorCode() = " + viewJobResponse.getErrorCode()
//					+ ", the client's IP address as seen by the server = " + viewJobResponse.getClientIPAddressAtServer() );
//		}

		if ( viewJobResponse.isErrorResponse() ) {

			String msg = "Retrieval of job failed.  Error code = " + viewJobResponse.getErrorCode() + ", error code desc = " + viewJobResponse.getErrorCodeDescription()
				+ ", the client's IP address as seen by the server = " + viewJobResponse.getClientIPAddressAtServer()
				+ ", the node name sent to the server: " + guiNodeName
				+ "\n  job id = |" + jobId + "|.";

//			log.error( msg );

			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setServerAppCodeReturnedErrorResponse(true);
			throw exception;
		}

		job = viewJobResponse.getJob();

		return job;
	}


//
//	/**
//	
//						WARNING:  Out of date, does not not use method callActualWebserviceOnServer
//	 * @param jobId
//	 * @param statusId
//	 * @param dbRecordVersionNumber
//	 * @throws Exception
//	 */
//	public void updateJobStatus( int jobId, int statusId, int dbRecordVersionNumber ) throws Exception {
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
	 * @throws Exception
	 */
	public GUICallStatus requeueJob( int jobId, Integer dbRecordVersionNumber ) throws Exception {

		//  TODO  return different things if there is a general failure compared to the version being different

		if ( ! initialized ) {
			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {
			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		String webserviceURL = connectionURL + URL_EXTENSION_REQUEUE_JOB;

		RequeueJobRequest webserviceRequest = new RequeueJobRequest();
		webserviceRequest.setNodeName( guiNodeName );
		Job job = new Job();
		webserviceRequest.setJob( job );
		job.setId( jobId );

		if ( dbRecordVersionNumber != null ) {
			job.setDbRecordVersionNumber( dbRecordVersionNumber );
		}

		Object webserviceResponseAsObject = callActualWebserviceOnServer( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof RequeueJobResponse ) ) {
			String msg = "Response unmarshaled to class other than RequeueJobResponse.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		RequeueJobResponse webserviceResponse = null;
		try {
			webserviceResponse = (RequeueJobResponse) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as RequeueJobResponse: "
					+ e.toString();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}

		RequeueJobResponse requeueJobResponse = webserviceResponse;

//		if ( log.isInfoEnabled() ) {
//
//			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  requeueJobResponse.isErrorResponse() = " + requeueJobResponse.isErrorResponse()
//					+ ", requeueJobResponse.getErrorCode() = " + requeueJobResponse.getErrorCode()
//					+ ", the client's IP address as seen by the server = " + requeueJobResponse.getClientIPAddressAtServer() );
//		}

		if ( requeueJobResponse.isErrorResponse() ) {

			if ( requeueJobResponse.getRequeueJobResponseErrorCode() ==  RequeueJobResponse.REQUEUE_ERROR_DB_RECORD_VERSION_NUMBER_OUT_OF_SYNC ) {

				return GUICallStatus.FAILED_VERSION_NOT_MATCH_DB;

			} else if ( requeueJobResponse.getRequeueJobResponseErrorCode() ==  RequeueJobResponse.REQUEUE_ERROR_JOB_NO_LONGER_REQUEUABLE ) {

				return GUICallStatus.FAILED_JOB_NO_LONGER_REQUEUEABLE;

			} else if ( requeueJobResponse.getRequeueJobResponseErrorCode() ==  RequeueJobResponse.REQUEUE_ERROR_JOB_NOT_FOUND_IN_DB ) {

				return GUICallStatus.FAILED_JOB_NOT_FOUND;

			} else {
				String msg = "Requeue of job failed.  Error code = " + requeueJobResponse.getErrorCode() + ", error code desc = " + requeueJobResponse.getErrorCodeDescription()
					+ ", the client's IP address as seen by the server = " + requeueJobResponse.getClientIPAddressAtServer()
					+ ", the node name sent to the server: " + guiNodeName
				+ "\n  job id = |" + jobId + "|.";

//				log.error( msg );

				JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
				exception.setServerAppCodeReturnedErrorResponse(true);
				throw exception;
			}

		}

		return GUICallStatus.SUCCESS;
	}





	/**
	 * @param jobId
	 * @param dbRecordVersionNumber - Pass "null" if not enforce
	 * @return
	 * @throws Exception
	 */
	public GUICallStatus cancelJob( int jobId, Integer dbRecordVersionNumber ) throws Exception {

		//  TODO  return different things if there is a general failure compared to the version being different

		if ( ! initialized ) {
			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {
			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		String webserviceURL = connectionURL + URL_EXTENSION_CANCEL_JOB;

		CancelJobRequest webserviceRequest = new CancelJobRequest();
		webserviceRequest.setNodeName( guiNodeName );
		Job job = new Job();
		webserviceRequest.setJob( job );
		job.setId( jobId );

		if ( dbRecordVersionNumber != null ) {
			job.setDbRecordVersionNumber( dbRecordVersionNumber );
		}

		Object webserviceResponseAsObject = callActualWebserviceOnServer( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof CancelJobResponse ) ) {
			String msg = "Response unmarshaled to class other than CancelJobResponse.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		CancelJobResponse webserviceResponse = null;
		try {
			webserviceResponse = (CancelJobResponse) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as CancelJobResponse: "
					+ e.toString();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}

		CancelJobResponse cancelJobResponse = webserviceResponse;

//		if ( log.isInfoEnabled() ) {
//
//			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  cancelJobResponse.isErrorResponse() = " + cancelJobResponse.isErrorResponse()
//					+ ", cancelJobResponse.getErrorCode() = " + cancelJobResponse.getErrorCode()
//					+ ", the client's IP address as seen by the server = " + cancelJobResponse.getClientIPAddressAtServer() );
//		}


		if ( cancelJobResponse.isErrorResponse() ) {

			if ( cancelJobResponse.getCancelJobResponseErrorCode() ==  CancelJobResponse.CANCEL_ERROR_DB_RECORD_VERSION_NUMBER_OUT_OF_SYNC ) {

				return GUICallStatus.FAILED_VERSION_NOT_MATCH_DB;

			} else if ( cancelJobResponse.getCancelJobResponseErrorCode() ==  CancelJobResponse.CANCEL_ERROR_JOB_NOT_CANCELABLE ) {

				return GUICallStatus.FAILED_JOB_NO_LONGER_CANCELABLE;

			} else if ( cancelJobResponse.getCancelJobResponseErrorCode() ==  CancelJobResponse.CANCEL_ERROR_JOB_NOT_FOUND_IN_DB ) {

				return GUICallStatus.FAILED_JOB_NOT_FOUND;

			} else {
				String msg = "Cancel of job failed.  Error code = " + cancelJobResponse.getErrorCode() + ", error code desc = " + cancelJobResponse.getErrorCodeDescription()
					+ ", the client's IP address as seen by the server = " + cancelJobResponse.getClientIPAddressAtServer()
					+ ", the node name sent to the server: " + guiNodeName
					+ "\n  job id = |" + jobId + "|.";

//				log.error( msg );

				JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
				exception.setServerAppCodeReturnedErrorResponse(true);
				throw exception;
			}

		}

		return GUICallStatus.SUCCESS;
	}




	/**
	 * @param newPriority
	 * @param jobId
	 * @param dbRecordVersionNumber - Pass "null" if not enforce
	 * @return
	 * @throws Exception
	 */
	public GUICallStatus changeJobPriority( int newPriority, int jobId, Integer dbRecordVersionNumber ) throws Exception {

		//  TODO  return different things if there is a general failure compared to the version being different

		if ( ! initialized ) {
			throw new IllegalStateException( "not initialized.  init() was never called." );
		}

		if ( connectionURL == null ) {
			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		String webserviceURL = connectionURL + URL_EXTENSION_UPDATE_JOB_PRIORITY;

		JobChangePriorityRequest webserviceRequest = new JobChangePriorityRequest();
		webserviceRequest.setNodeName( guiNodeName );
		Job job = new Job();
		webserviceRequest.setJob( job );
		job.setId( jobId );
		job.setPriority( newPriority );

		if ( dbRecordVersionNumber != null ) {
			job.setDbRecordVersionNumber( dbRecordVersionNumber );
		}

		Object webserviceResponseAsObject = callActualWebserviceOnServer( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof JobChangePriorityResponse ) ) {
			String msg = "Response unmarshaled to class other than JobChangePriorityResponse.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		JobChangePriorityResponse webserviceResponse = null;
		try {
			webserviceResponse = (JobChangePriorityResponse) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as JobChangePriorityResponse: "
					+ e.toString();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}

		JobChangePriorityResponse jobChangePriorityResponse = webserviceResponse;

//		if ( log.isInfoEnabled() ) {
//
//			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  JobChangePriorityResponse.isErrorResponse() = " + JobChangePriorityResponse.isErrorResponse()
//					+ ", JobChangePriorityResponse.getErrorCode() = " + JobChangePriorityResponse.getErrorCode()
//					+ ", the client's IP address as seen by the server = " + JobChangePriorityResponse.getClientIPAddressAtServer() );
//		}

		if ( jobChangePriorityResponse.isErrorResponse() ) {

			if ( jobChangePriorityResponse.getJobChangePriorityResponseErrorCode() ==  JobChangePriorityResponse.CHANGE_PRIORITY_ERROR_DB_RECORD_VERSION_NUMBER_OUT_OF_SYNC ) {

				return GUICallStatus.FAILED_VERSION_NOT_MATCH_DB;

			} else if ( jobChangePriorityResponse.getJobChangePriorityResponseErrorCode() ==  JobChangePriorityResponse.CHANGE_PRIORITY_ERROR_JOB_NOT_FOUND_IN_DB ) {

				return GUICallStatus.FAILED_JOB_NOT_FOUND;

			} else {

				String msg = "Update of job priority failed.  Error code = " + jobChangePriorityResponse.getErrorCode() + ", error code desc = " + jobChangePriorityResponse.getErrorCodeDescription()
					+ ", the client's IP address as seen by the server = " + jobChangePriorityResponse.getClientIPAddressAtServer()
					+ ", the node name sent to the server: " + guiNodeName
				+ "\n  job id = |" + jobId + "|.";

//				log.error( msg );

				JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
				exception.setServerAppCodeReturnedErrorResponse(true);
				throw exception;
			}
		}

		return GUICallStatus.SUCCESS;

	}

	/**
	 * @return
	 * @throws Exception
	 */
	public List<JobType>  listJobTypes(  ) throws Exception {

		return listJobTypes( null );
		
	}
	

	/**
	 * @return
	 * @throws Exception
	 */
	public List<JobType>  listJobTypes( List<String> jobTypeNames ) throws Exception {

		if ( ! initialized ) {
			throw new IllegalStateException( "not initialized.  init() was never called." );
		}
		if ( connectionURL == null ) {
			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		List<JobType> jobTypes = null;

		String webserviceURL = connectionURL + URL_EXTENSION_LIST_JOB_TYPES;

		ListJobTypesRequest webserviceRequest = new ListJobTypesRequest();
		webserviceRequest.setNodeName( guiNodeName );
		webserviceRequest.setJobTypeNames( jobTypeNames );

		Object webserviceResponseAsObject = callActualWebserviceOnServer( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof ListJobTypesResponse ) ) {
			String msg = "Response unmarshaled to class other than ListJobTypesResponse.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		ListJobTypesResponse webserviceResponse = null;
		try {
			webserviceResponse = (ListJobTypesResponse) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as ListJobTypesResponse: "
					+ e.toString();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}

		ListJobTypesResponse listJobTypesResponse = webserviceResponse;

//		if ( log.isInfoEnabled() ) {
//			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  listJobTypesResponse.isErrorResponse() = " + listJobTypesResponse.isErrorResponse()
//					+ ", listJobTypesResponse.getErrorCode() = " + listJobTypesResponse.getErrorCode()
//					+ ", the client's IP address as seen by the server = " + listJobTypesResponse.getClientIPAddressAtServer() );
//		}

		if ( listJobTypesResponse.isErrorResponse() ) {

			String msg = "Retrieval of job types failed.  Error code = " + listJobTypesResponse.getErrorCode() + ", error code desc = " + listJobTypesResponse.getErrorCodeDescription()
			+ ", the client's IP address as seen by the server = " + listJobTypesResponse.getClientIPAddressAtServer()
			+ ", the node name sent to the server: " + guiNodeName;

//			log.error( msg );

			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setServerAppCodeReturnedErrorResponse(true);
			throw exception;
		}

		jobTypes = listJobTypesResponse.getJobTypes();

		return jobTypes;
	}



	/**
	 * @return
	 * @throws Exception
	 */
	public List<RequestTypeDTO>  listRequestTypes(  ) throws Exception {

		if ( ! initialized ) {
			throw new IllegalStateException( "not initialized.  init() was never called." );
		}
		if ( connectionURL == null ) {
			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		List<RequestTypeDTO> requestTypes = null;

		String webserviceURL = connectionURL + URL_EXTENSION_LIST_REQUEST_TYPES;

		ListRequestTypesRequest webserviceRequest = new ListRequestTypesRequest();
		webserviceRequest.setNodeName( guiNodeName );

		Object webserviceResponseAsObject = callActualWebserviceOnServer( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof ListRequestTypesResponse ) ) {
			String msg = "Response unmarshaled to class other than ListRequestTypesResponse.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		ListRequestTypesResponse webserviceResponse = null;
		try {
			webserviceResponse = (ListRequestTypesResponse) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as ListRequestTypesResponse: "
					+ e.toString();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		ListRequestTypesResponse listRequestTypesResponse = webserviceResponse;

//		if ( log.isInfoEnabled() ) {
//			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  listRequestTypesResponse.isErrorResponse() = " + listRequestTypesResponse.isErrorResponse()
//					+ ", listRequestTypesResponse.getErrorCode() = " + listRequestTypesResponse.getErrorCode()
//					+ ", the client's IP address as seen by the server = " + listRequestTypesResponse.getClientIPAddressAtServer() );
//		}

		if ( listRequestTypesResponse.isErrorResponse() ) {

			String msg = "Retrieval of request types failed.  Error code = " + listRequestTypesResponse.getErrorCode() + ", error code desc = " + listRequestTypesResponse.getErrorCodeDescription()
							+ ", the client's IP address as seen by the server = " + listRequestTypesResponse.getClientIPAddressAtServer()
							+ ", the node name sent to the server: " + guiNodeName;

//			log.error( msg );

			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setServerAppCodeReturnedErrorResponse(true);
			throw exception;
		}

		requestTypes = listRequestTypesResponse.getRequestTypes();

		return requestTypes;
	}



	/**
	 * @return
	 * @throws Exception
	 */
	public List<NodeClientStatusDTO>  listClientsStatus(  ) throws Exception {

		if ( ! initialized ) {
			throw new IllegalStateException( "not initialized.  init() was never called." );
		}
		if ( connectionURL == null ) {
			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		List<NodeClientStatusDTO> clientsStatusList = null;

		String webserviceURL = connectionURL + URL_EXTENSION_GUI_LIST_CLIENTS_STATUS;

		ListClientsStatusRequest webserviceRequest = new ListClientsStatusRequest();
		webserviceRequest.setNodeName( guiNodeName );

		Object webserviceResponseAsObject = callActualWebserviceOnServer( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof ListClientsStatusResponse ) ) {
			String msg = "Response unmarshaled to class other than ListClientsStatusResponse.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		ListClientsStatusResponse webserviceResponse = null;
		try {
			webserviceResponse = (ListClientsStatusResponse) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as ListClientsStatusResponse: "
					+ e.toString();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}

		ListClientsStatusResponse listClientsStatusResponse = webserviceResponse;

//		if ( log.isInfoEnabled() ) {
//			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  listClientsStatusResponse.isErrorResponse() = " + listClientsStatusResponse.isErrorResponse()
//					+ ", listClientsStatusResponse.getErrorCode() = " + listClientsStatusResponse.getErrorCode()
//					+ ", the client's IP address as seen by the server = " + listClientsStatusResponse.getClientIPAddressAtServer() );
//		}

		if ( listClientsStatusResponse.isErrorResponse() ) {

			String msg = "Retrieval of clients status failed.  Error code = " + listClientsStatusResponse.getErrorCode() + ", error code desc = " + listClientsStatusResponse.getErrorCodeDescription()
							+ ", the client's IP address as seen by the server = " + listClientsStatusResponse.getClientIPAddressAtServer()
							+ ", the node name sent to the server: " + guiNodeName;

//			log.error( msg );

			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setServerAppCodeReturnedErrorResponse(true);
			throw exception;
		}

		clientsStatusList = listClientsStatusResponse.getClients();

		return clientsStatusList;
	}



	/**
	 * @return
	 * @throws Exception
	 */
	public List<ClientConnectedDTO>  retrieveClientsConnectedList(  ) throws Exception {

		if ( ! initialized ) {
			throw new IllegalStateException( "not initialized.  init() was never called." );
		}
		if ( connectionURL == null ) {
			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		List<ClientConnectedDTO> clientConnectedDTOList = null;

		String webserviceURL = connectionURL + URL_EXTENSION_GUI_LIST_CLIENTS_CONNECTED;

		GetClientsConnectedListRequest webserviceRequest = new GetClientsConnectedListRequest();
		webserviceRequest.setNodeName( guiNodeName );

		Object webserviceResponseAsObject = callActualWebserviceOnServer( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof GetClientsConnectedListResponse ) ) {
			String msg = "Response unmarshaled to class other than GetClientsConnectedListResponse.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		GetClientsConnectedListResponse webserviceResponse = null;
		try {
			webserviceResponse = (GetClientsConnectedListResponse) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as GetClientsConnectedListResponse: "
					+ e.toString();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}

		GetClientsConnectedListResponse getClientsConnectedListResponse = webserviceResponse;

//		if ( log.isInfoEnabled() ) {
//			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient:  getClientsConnectedListResponse.isErrorResponse() = " + getClientsConnectedListResponse.isErrorResponse()
//					+ ", getClientsConnectedListResponse.getErrorCode() = " + getClientsConnectedListResponse.getErrorCode()
//					+ ", the client's IP address as seen by the server = " + getClientsConnectedListResponse.getClientIPAddressAtServer() );
//		}

		if ( getClientsConnectedListResponse.isErrorResponse() ) {

			String msg = "Retrieval of clients connected failed.  Error code = " + getClientsConnectedListResponse.getErrorCode() + ", error code desc = " + getClientsConnectedListResponse.getErrorCodeDescription()
							+ ", the client's IP address as seen by the server = " + getClientsConnectedListResponse.getClientIPAddressAtServer()
							+ ", the node name sent to the server: " + guiNodeName ;

//			log.error( msg );

			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setServerAppCodeReturnedErrorResponse(true);
			throw exception;
		}

		clientConnectedDTOList = getClientsConnectedListResponse.getClientConnectedDTOList();

		return clientConnectedDTOList;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public List<ClientConnectedDTO>  retrieveClientsUsingSameNodeNameList(  ) throws Exception {

		if ( ! initialized ) {
			throw new IllegalStateException( "not initialized.  init() was never called." );
		}
		if ( connectionURL == null ) {
			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		List<ClientConnectedDTO> clientConnectedDTOList = null;

		String webserviceURL = connectionURL + URL_EXTENSION_GUI_LIST_CLIENTS_USING_SAME_NODE_NAME;

		GetClientsConnectedListRequest webserviceRequest = new GetClientsConnectedListRequest();
		webserviceRequest.setNodeName( guiNodeName );

		Object webserviceResponseAsObject = callActualWebserviceOnServer( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof GetClientsConnectedListResponse ) ) {
			String msg = "Response unmarshaled to class other than GetClientsConnectedListResponse.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		GetClientsConnectedListResponse webserviceResponse = null;
		try {
			webserviceResponse = (GetClientsConnectedListResponse) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as GetClientsConnectedListResponse: "
					+ e.toString();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}

		GetClientsConnectedListResponse getClientsConnectedListResponse = webserviceResponse;

//		if ( log.isInfoEnabled() ) {
//			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient: retrieveClientsUsingSameNodeNameList():  getClientsConnectedListResponse.isErrorResponse() = " + getClientsConnectedListResponse.isErrorResponse()
//					+ ", getClientsConnectedListResponse.getErrorCode() = " + getClientsConnectedListResponse.getErrorCode()
//					+ ", the client's IP address as seen by the server = " + getClientsConnectedListResponse.getClientIPAddressAtServer() );
//		}

		if ( getClientsConnectedListResponse.isErrorResponse() ) {

			String msg = "Retrieval of clients connected failed.  Error code = " + getClientsConnectedListResponse.getErrorCode() + ", error code desc = " + getClientsConnectedListResponse.getErrorCodeDescription()
							+ ", the client's IP address as seen by the server = " + getClientsConnectedListResponse.getClientIPAddressAtServer()
							+ ", the node name sent to the server: " + guiNodeName;

//			log.error( msg );

			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setServerAppCodeReturnedErrorResponse(true);
			throw exception;
		}

		clientConnectedDTOList = getClientsConnectedListResponse.getClientConnectedDTOList();

		return clientConnectedDTOList;
	}



	/**
	 * @return
	 * @throws Exception
	 */
	public List<ClientConnectedDTO>  retrieveClientsFailedToConnectTrackingList(  ) throws Exception {

		if ( ! initialized ) {
			throw new IllegalStateException( "not initialized.  init() was never called." );
		}
		if ( connectionURL == null ) {
			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		List<ClientConnectedDTO> clientConnectedDTOList = null;

		String webserviceURL = connectionURL + URL_EXTENSION_GUI_LIST_CLIENTS_FAILED_TO_CONNECT;

		GetClientsConnectedListRequest webserviceRequest = new GetClientsConnectedListRequest();
		webserviceRequest.setNodeName( guiNodeName );

		Object webserviceResponseAsObject = callActualWebserviceOnServer( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof GetClientsConnectedListResponse ) ) {
			String msg = "Response unmarshaled to class other than GetClientsConnectedListResponse.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		GetClientsConnectedListResponse webserviceResponse = null;
		try {
			webserviceResponse = (GetClientsConnectedListResponse) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as GetClientsConnectedListResponse: "
					+ e.toString();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}

		GetClientsConnectedListResponse getClientsConnectedListResponse = webserviceResponse;

//		if ( log.isInfoEnabled() ) {
//			log.info( "JobCenter_GUIClient:  GUIConnectionToServerClient: retrieveClientsFailedToConnectTrackingList():  getClientsConnectedListResponse.isErrorResponse() = " + getClientsConnectedListResponse.isErrorResponse()
//					+ ", getClientsConnectedListResponse.getErrorCode() = " + getClientsConnectedListResponse.getErrorCode()
//					+ ", the client's IP address as seen by the server = " + getClientsConnectedListResponse.getClientIPAddressAtServer() );
//		}

		if ( getClientsConnectedListResponse.isErrorResponse() ) {

			String msg = "Retrieval of clients connected failed.  Error code = " + getClientsConnectedListResponse.getErrorCode() + ", error code desc = " + getClientsConnectedListResponse.getErrorCodeDescription()
							+ ", the client's IP address as seen by the server = " + getClientsConnectedListResponse.getClientIPAddressAtServer()
							+ ", the node name sent to the server: " + guiNodeName;

//			log.error( msg );

			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg );
			exception.setServerAppCodeReturnedErrorResponse(true);
			throw exception;
		}

		clientConnectedDTOList = getClientsConnectedListResponse.getClientConnectedDTOList();

		return clientConnectedDTOList;
	}




	//////////////////////////////////////////////////////////////////
	//    Internal Method
	/**
	 * @param webserviceRequest
	 * @param webserviceURL
	 * @return
	 * @throws Exception
	 */
	private Object callActualWebserviceOnServer( 
			Object webserviceRequest,
			String webserviceURL ) throws Exception {
		Object webserviceResponseAsObject = null;
		byte[] requestXMLToSend = null;
		try {
			//  Jackson JSON code for JSON testing
			//  JSON using Jackson
//			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
//			requestXMLToSend = mapper.writeValueAsBytes( webserviceRequest );
			
			//  Marshal (write) the object to the byte array as XML
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			marshaller.setProperty( Marshaller.JAXB_ENCODING, XML_ENCODING_CHARACTER_SET );
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100000);
			try {
				marshaller.marshal( webserviceRequest, outputStream );
			} catch ( Exception e ) {
				throw e;
			} finally {
				if ( outputStream != null ) {
					outputStream.close();
				}
			}
			requestXMLToSend = outputStream.toByteArray();
			//  Confirm that the generated XML can be parsed.
			ByteArrayInputStream bais = new ByteArrayInputStream( requestXMLToSend );
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			@SuppressWarnings("unused")
			Object unmarshalledObject = unmarshaller.unmarshal( bais );

		} catch ( Exception e ) {
			String msg = "Error. Fail to encode request to send to server: "
					+ e.toString();
			JobcenterGUI_WebserviceCallErrorException exception = new JobcenterGUI_WebserviceCallErrorException( msg, e );
			exception.setFailToEncodeDataToSendToServer(true);
			throw exception;
		}
		//   Create object for connecting to server
		URL urlObject;
		try {
			urlObject = new URL( webserviceURL );
		} catch (MalformedURLException e) {
			JobcenterGUI_WebserviceCallErrorException wcee = new JobcenterGUI_WebserviceCallErrorException( "Exception creating URL object to connect to server.  URL: " + webserviceURL, e );
			wcee.setServerURLError(true);
			wcee.setWebserviceURL( webserviceURL );
			throw wcee;
		}
		//   Open connection to server
		URLConnection urlConnection;
		try {
			urlConnection = urlObject.openConnection();
		} catch (IOException e) {
			JobcenterGUI_WebserviceCallErrorException wcee = new JobcenterGUI_WebserviceCallErrorException( "Exception calling openConnection() on URL object to connect to server.  URL: " + webserviceURL, e );
			wcee.setServerURLError(true);
			wcee.setWebserviceURL( webserviceURL );
			throw wcee;
		}
		// Downcast URLConnection to HttpURLConnection to allow setting of HTTP parameters 
		if ( ! ( urlConnection instanceof HttpURLConnection ) ) {
			JobcenterGUI_WebserviceCallErrorException wcee = new JobcenterGUI_WebserviceCallErrorException( "Processing Error: Cannot cast URLConnection to HttpURLConnection" );
			wcee.setServerURLError(true);
			wcee.setWebserviceURL( webserviceURL );
			throw wcee;
		}
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) urlConnection;
		} catch (Exception e) {
			JobcenterGUI_WebserviceCallErrorException wcee = new JobcenterGUI_WebserviceCallErrorException( "Processing Error: Cannot cast URLConnection to HttpURLConnection" );
			wcee.setServerURLError(true);
			wcee.setWebserviceURL( webserviceURL );
			throw wcee;
		}
		//  Set HttpURLConnection properties
		httpURLConnection.setRequestProperty( "Accept", CONTENT_TYPE_SEND_RECEIVE );
		httpURLConnection.setRequestProperty( "Content-Type", CONTENT_TYPE_SEND_RECEIVE );
		httpURLConnection.setDoOutput(true);
		// Send post request to server
		try {  //  Overall try/catch block to put "httpURLConnection.disconnect();" in the finally block
			try {
				httpURLConnection.connect();
			} catch ( IOException e ) {
				JobcenterGUI_WebserviceCallErrorException wcee = new JobcenterGUI_WebserviceCallErrorException( "Exception connecting to server at URL: " + webserviceURL, e );
				wcee.setServerURLError(true);
				wcee.setWebserviceURL( webserviceURL );
				throw wcee;
			}
			//  Send XML to server
			BufferedOutputStream bufferedOutputStream = null;
			try {
				OutputStream outputStream = httpURLConnection.getOutputStream();
				bufferedOutputStream = new BufferedOutputStream( outputStream );
				bufferedOutputStream.write( requestXMLToSend );
			} catch ( IOException e ) {
				byte[] errorStreamContents = null;
				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection );
				} catch ( Exception ex ) {
				}
				JobcenterGUI_WebserviceCallErrorException wcee = new JobcenterGUI_WebserviceCallErrorException( "IOException sending XML to server at URL: " + webserviceURL, e );
				wcee.setServerURLError(true);
				wcee.setWebserviceURL( webserviceURL );
				wcee.setErrorStreamContents( errorStreamContents );
				throw wcee;
			} finally {
				if ( bufferedOutputStream != null ) {
					try {
						bufferedOutputStream.close();
					} catch ( IOException e ) {
						byte[] errorStreamContents = null;
						try {
							errorStreamContents= getErrorStreamContents( httpURLConnection );
						} catch ( Exception ex ) {
						}
						JobcenterGUI_WebserviceCallErrorException wcee = new JobcenterGUI_WebserviceCallErrorException( "IOException closing output Stream to server at URL: " + webserviceURL, e );
						wcee.setServerURLError(true);
						wcee.setWebserviceURL( webserviceURL );
						wcee.setErrorStreamContents( errorStreamContents );
						throw wcee;
					}
				}
			}
			try {
				int httpResponseCode = httpURLConnection.getResponseCode();
				if ( httpResponseCode != SUCCESS_HTTP_RETURN_CODE ) {
					byte[] errorStreamContents = null;
					try {
						errorStreamContents= getErrorStreamContents( httpURLConnection );
					} catch ( Exception ex ) {
					}
					JobcenterGUI_WebserviceCallErrorException wcee = 
							new JobcenterGUI_WebserviceCallErrorException( "Unsuccessful HTTP response code of " + httpResponseCode
									+ " connecting to server at URL: " + webserviceURL );
					wcee.setBadHTTPStatusCode(true);
					wcee.setHttpStatusCode( httpResponseCode );
					wcee.setWebserviceURL( webserviceURL );
					wcee.setErrorStreamContents( errorStreamContents );
					throw wcee;
				}
			} catch ( IOException e ) {
				byte[] errorStreamContents = null;
				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection );
				} catch ( Exception ex ) {
				}
				JobcenterGUI_WebserviceCallErrorException wcee = 
						new JobcenterGUI_WebserviceCallErrorException( "IOException getting HTTP response code from server at URL: " + webserviceURL, e );
				wcee.setServerSendReceiveDataError(true);
				wcee.setWebserviceURL( webserviceURL );
				wcee.setErrorStreamContents( errorStreamContents );
				throw wcee;
			}
			//  Get response XML from server
			ByteArrayOutputStream outputStreamBufferOfServerResponse = new ByteArrayOutputStream( 1000000 );
			InputStream inputStream = null;
			try {
				inputStream = httpURLConnection.getInputStream();
				int nRead;
				byte[] data = new byte[ 16384 ];
				while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
					outputStreamBufferOfServerResponse.write(data, 0, nRead);
				}
			} catch ( IOException e ) {
				byte[] errorStreamContents = null;
				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection );
				} catch ( Exception ex ) {
				}
				JobcenterGUI_WebserviceCallErrorException wcee = 
						new JobcenterGUI_WebserviceCallErrorException( "IOException receiving XML from server at URL: " + webserviceURL, e );
				wcee.setServerSendReceiveDataError(true);
				wcee.setWebserviceURL( webserviceURL );
				wcee.setErrorStreamContents( errorStreamContents );
				throw wcee;
			} finally {
				if ( inputStream != null ) {
					try {
						inputStream.close();
					} catch ( IOException e ) {
						byte[] errorStreamContents = null;
						try {
							errorStreamContents= getErrorStreamContents( httpURLConnection );
						} catch ( Exception ex ) {
						}
						JobcenterGUI_WebserviceCallErrorException wcee = 
								new JobcenterGUI_WebserviceCallErrorException( "IOException closing input Stream from server at URL: " + webserviceURL, e );
						wcee.setServerSendReceiveDataError(true);
						wcee.setWebserviceURL( webserviceURL );
						wcee.setErrorStreamContents( errorStreamContents );
						throw wcee;
					}
				}
			}
			byte[] serverResponseByteArrayFromServer = outputStreamBufferOfServerResponse.toByteArray();
			byte[] serverResponseByteArray = serverResponseByteArrayFromServer;

			ByteArrayInputStream inputStreamBufferOfServerResponse = 
					new ByteArrayInputStream( serverResponseByteArray );
			// Unmarshal received XML into Java objects
			try {
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				webserviceResponseAsObject = unmarshaller.unmarshal( inputStreamBufferOfServerResponse );
			} catch ( JAXBException e ) {
				JobcenterGUI_WebserviceCallErrorException wcee = 
						new JobcenterGUI_WebserviceCallErrorException( "JAXBException unmarshalling XML received from server at URL: " + webserviceURL, e );
				wcee.setFailToDecodeDataReceivedFromServer(true);
				wcee.setWebserviceURL( webserviceURL );
				throw wcee;
			}
			return webserviceResponseAsObject; 
		} finally {
//			httpURLConnection.disconnect();
		}
	}
	
	/**
	 * @param httpURLConnection
	 * @return
	 * @throws IOException
	 */
	private byte[] getErrorStreamContents(HttpURLConnection httpURLConnection) throws IOException {
		
		InputStream inputStream = httpURLConnection.getErrorStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int byteArraySize = 5000;
		byte[] data = new byte[ byteArraySize ];
		while (true) {
			int bytesRead = inputStream.read( data );
			if ( bytesRead == -1 ) {  // end of input
				break;
			}
			if ( bytesRead > 0 ) {
				baos.write( data, 0, bytesRead );
			}
		}
		return baos.toByteArray();
	}




}
