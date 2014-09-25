package org.jobcenter.client.main;



import java.io.BufferedInputStream;
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
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

//import org.apache.log4j.Logger;


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
import org.jobcenter.response.BaseResponse;
import org.jobcenter.response.SubmitJobResponse;
import org.jobcenter.response.SubmitJobsListWithDependenciesResponse;
import org.jobcenter.submission.internal.utils.JobSubmissionTransforms;


/**
 *
 *
 */
public class SubmissionClientConnectionToServer implements JobSubmissionInterface {
	
	private static final String XML_ENCODING_CHARACTER_SET = "UTF-8";


//	private static Logger log = Logger.getLogger(SubmissionClientConnectionToServer.class);

	private static final int SUCCESS_HTTP_RETURN_CODE = 200;

	private static final String SUBMIT_JOB_CONNECTION_URL_EXTENSION

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.SUBMIT_JOB;

	private static final String SUBMIT_JOB_LIST_WITH_DEPENDENCIES_CONNECTION_URL_EXTENSION

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.SUBMIT_JOBS_LIST_WITH_DEPENDENCIES;


	private static final String CONTENT_TYPE_SEND_RECEIVE = "application/xml";
	
	
	private boolean doNotSendToServer = false;
	





	private String connectionURL = null;


	private String submissionNodeName = Constants.SUBMISSION_CLIENT_NODE_NAME_DEFAULT;
	
	private JAXBContext jaxbContext;



	public static JobSubmissionInterface getInstance() {

		return new SubmissionClientConnectionToServer();
	}


	@Override
	public void init( String connectionURL ) throws Throwable {

//		log.info( "Entered org.jobcenter.client.main.SubmissionClientConnectionToServer.init() in project JobCenter_JobSubmissionClient" );

		if ( connectionURL == null || connectionURL.isEmpty() ) {

			throw new IllegalArgumentException( "connectionURL cannot be null or empty" );
		}

		this.connectionURL = connectionURL;
		

		try { 
			jaxbContext = JAXBContext.newInstance( SubmitJobResponse.class, SubmitJobRequest.class, SubmitJobsListWithDependenciesResponse.class, SubmitJobsListWithDependenciesRequest.class );
		} catch (JAXBException e) {

			JobcenterSubmissionXML_JAXBErrorException exc = new JobcenterSubmissionXML_JAXBErrorException( "JAXBException Setting up JAXBContext for sending XML to server at URL: " + connectionURL, e );

			exc.setFullConnectionURL( connectionURL );

			throw exc;

		}

	}


	@Override
	public void destroy()  {




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
	 *
	 * @return requestId - the next assigned id related to the particular requestTypeName.  Will return the passed in requestId if one is provided ( not null )
	 *
	 * @throws IllegalStateException - The connectionURL to the server is not configured
	 * @throws IllegalArgumentException - jobTypeName cannot be null
	 * @throws JobcenterSubmissionGeneralErrorException - An error condition not covered by any of the other exceptions thrown by this method
	 * @throws JobcenterSubmissionServerErrorException - The Jobcenter code on the server has refused this submit request or has experienced an error
	 * @throws JobcenterSubmissionHTTPErrorException - HTTP response code returned as a result of sending this request to the server
	 * @throws JobcenterSubmissionMalformedURLErrorException - The connection URL cannot be processed into a URL object
	 * @throws JobcenterSubmissionIOErrorException - An IOException was thrown
	 * @throws JobcenterSubmissionXML_JAXBErrorException - A JAXB Exception was thrown marshalling or unmarshalling the XML to/from Java object
	 */
	@Override

	public int submitJob( String requestTypeName, Integer requestId, String jobTypeName, String submitter, Integer priority, Map<String, String> jobParameters )
	throws
	IllegalStateException,
	IllegalArgumentException,
	JobcenterSubmissionGeneralErrorException,
	JobcenterSubmissionServerErrorException,
	JobcenterSubmissionHTTPErrorException,
	JobcenterSubmissionMalformedURLErrorException,
	JobcenterSubmissionIOErrorException,
	JobcenterSubmissionXML_JAXBErrorException

	{
		
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

		String fullConnectionURL = connectionURL + SUBMIT_JOB_CONNECTION_URL_EXTENSION;



		SubmitJobRequest submitJobRequest = JobSubmissionTransforms.createSubmitJobRequest( requestTypeName, requestId, jobTypeName, submitter, priority, requiredExecutionThreads, jobParameters, submissionNodeName );

		byte[] submitJobRequestMarshalled = null;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream( 1000 );
		
		Marshaller marshaller = null;
		Unmarshaller unmarshaller = null;
		
		try {
			
			marshaller = jaxbContext.createMarshaller();
			unmarshaller = jaxbContext.createUnmarshaller();
			
			marshaller.setProperty( Marshaller.JAXB_ENCODING, XML_ENCODING_CHARACTER_SET );

		} catch (JAXBException e) {
			
			JobcenterSubmissionXML_JAXBErrorException exc = new JobcenterSubmissionXML_JAXBErrorException( "JAXBException creating XML to send to server at URL: " + fullConnectionURL, e );

			exc.setFullConnectionURL( fullConnectionURL );

			throw exc;
		}
		
		try {
			marshaller.marshal( submitJobRequest, baos ) ;
			
			submitJobRequestMarshalled = baos.toByteArray();

			ByteArrayInputStream bais = new ByteArrayInputStream( submitJobRequestMarshalled );

			Object unmarshalledObject = unmarshaller.unmarshal( bais );

		} catch (JAXBException e) {

			JobcenterSubmissionXML_JAXBErrorException exc = new JobcenterSubmissionXML_JAXBErrorException( "JAXBException creating XML to send to server at URL: " + fullConnectionURL, e );

			exc.setFullConnectionURL( fullConnectionURL );

			throw exc;
		}
		
		if ( doNotSendToServer ) {
			
			return -1;
		}
		
		BaseResponse baseResponse = sendRequestToServerGetRequestId(requestTypeName, requestId, fullConnectionURL, submitJobRequestMarshalled, jobTypeName );

		if ( ! ( baseResponse instanceof SubmitJobResponse ) ) {
			
			throw new JobcenterSubmissionGeneralErrorException("Processing Error: Server returned XML that deserialed to unexpected type");
		}
		
		SubmitJobResponse submitJobResponse = (SubmitJobResponse) baseResponse;
		
		return submitJobResponse.getRequestId();

	}
	
	
	
	
	
	//////////////////////////////////////////////
	
	//////////   Comment out submitJobsWithDependencies(...) since job dependencies is not completely implemented
	
	//   The method it calls has been modified so this will need modification if it is uncommented
	
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
//		
//		BaseResponse baseResponse = sendRequestToServerGetRequestId(requestTypeName, requestId, fullConnectionURL, submitJobsListWithDependenciesRequest, null /* jobTypeName */ );
//
//		if ( ! ( baseResponse instanceof SubmitJobsListWithDependenciesResponse ) ) {
//			
//			throw new JobcenterSubmissionGeneralErrorException("Processing Error: Server returned XML that deserialed to unexpected type");
//		}
//		
//		SubmitJobsListWithDependenciesResponse submitJobsListWithDependenciesResponse = (SubmitJobsListWithDependenciesResponse) baseResponse;
//		
//		return submitJobsListWithDependenciesResponse.getRequestId();
//
//	}



	/**
	 * @param requestTypeName
	 * @param requestId
	 * @param fullConnectionURL
	 * @param requestObject - submitJobRequest or 
	 * @param jobTypeName - for error message
	 * @param firstJobTypeName
	 * @return
	 */
	private BaseResponse sendRequestToServerGetRequestId(String requestTypeName,
			Integer requestId, 
			String fullConnectionURL,
//			Object requestObject, 
			byte[] requestObjectMarshalled, 
			String jobTypeName ) {
		
		URL urlObject;
		try {
			urlObject = new URL( fullConnectionURL );

		} catch (MalformedURLException e) {

			JobcenterSubmissionMalformedURLErrorException jsmee = new JobcenterSubmissionMalformedURLErrorException( "Exception creating URL object to connect to server.  URL: " + fullConnectionURL, e );

			jsmee.setFullConnectionURL( fullConnectionURL );

			throw jsmee;
		}

		URLConnection urlConnection;
		try {
			urlConnection = urlObject.openConnection();

		} catch (IOException e) {

			throw new JobcenterSubmissionIOErrorException( "Exception calling openConnection() on URL object to connect to server.  URL: " + fullConnectionURL, e );
		}

		if ( ! ( urlConnection instanceof HttpURLConnection ) ) {

			throw new JobcenterSubmissionGeneralErrorException("Processing Error: Cannot cast URLConnection to HttpURLConnection");
		}

		HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;


		httpURLConnection.setRequestProperty( "Accept", CONTENT_TYPE_SEND_RECEIVE );
		httpURLConnection.setRequestProperty( "Content-Type", CONTENT_TYPE_SEND_RECEIVE );

		// Send post request
		httpURLConnection.setDoOutput(true);


		try {  //  Overall try/catch block to put "httpURLConnection.disconnect();" in the finally block



			try {

				httpURLConnection.connect();

			} catch ( IOException e ) {

				throw new JobcenterSubmissionIOErrorException( "Exception connecting to server at URL: " + fullConnectionURL, e );
			}


			BufferedOutputStream bufferedOutputStream = null;

			try {

				OutputStream outputStream = httpURLConnection.getOutputStream();

				bufferedOutputStream = new BufferedOutputStream( outputStream );
				
				//  instead of marshalling here, just send the byte array
				
				bufferedOutputStream.write( requestObjectMarshalled );


//				Marshaller marshaller = jaxbContext.createMarshaller();
//
//				marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );
//
//				marshaller.marshal( requestObject, bufferedOutputStream ) ;
//
//			} catch ( JAXBException e ) {
//
//				JobcenterSubmissionXML_JAXBErrorException exc = new JobcenterSubmissionXML_JAXBErrorException( "JAXBException creating XML to send to server at URL: " + fullConnectionURL, e );
//
//				exc.setFullConnectionURL( fullConnectionURL );
//
//				throw exc;

			} catch ( IOException e ) {


				byte[] errorStreamContents = null;

				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection );
				} catch ( Exception ex ) {

				}

				JobcenterSubmissionIOErrorException exc = new JobcenterSubmissionIOErrorException( "IOException sending XML to server at URL: " + fullConnectionURL, e );

				exc.setFullConnectionURL( fullConnectionURL );
				exc.setErrorStreamContents( errorStreamContents );

				throw exc;

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

						JobcenterSubmissionIOErrorException exc = new JobcenterSubmissionIOErrorException( "IOException closing output Stream to server at URL: " + fullConnectionURL, e );

						exc.setFullConnectionURL( fullConnectionURL );
						exc.setErrorStreamContents( errorStreamContents );

						throw exc;

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

					JobcenterSubmissionHTTPErrorException exc = new JobcenterSubmissionHTTPErrorException( "Unsuccessful HTTP response code of " + httpResponseCode
							+ " connecting to server at URL: " + fullConnectionURL );

					exc.setFullConnectionURL( fullConnectionURL );
					exc.setHttpErrorCode( httpResponseCode );
					exc.setErrorStreamContents( errorStreamContents );

					throw exc;
				}

			} catch ( IOException e ) {

				byte[] errorStreamContents = null;

				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection );
				} catch ( Exception ex ) {

				}
				JobcenterSubmissionIOErrorException exc = new JobcenterSubmissionIOErrorException( "IOException getting HTTP response code from server at URL: " + fullConnectionURL, e );

				exc.setFullConnectionURL( fullConnectionURL );
				exc.setErrorStreamContents( errorStreamContents );

				throw exc;

			}

			BaseResponse baseResponse = null;

			BufferedInputStream bufferedInputStream = null;

			try {

				InputStream inputStream = httpURLConnection.getInputStream();

				bufferedInputStream = new BufferedInputStream( inputStream );

				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

				baseResponse = (BaseResponse) unmarshaller.unmarshal( bufferedInputStream );

			} catch ( JAXBException e ) {

				JobcenterSubmissionXML_JAXBErrorException exc = new JobcenterSubmissionXML_JAXBErrorException( "JAXBException unmarshalling XML received from server at URL: " + fullConnectionURL, e );

				exc.setFullConnectionURL( fullConnectionURL );

				throw exc;

			} catch ( IOException e ) {


				byte[] errorStreamContents = null;

				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection );
				} catch ( Exception ex ) {

				}
				JobcenterSubmissionIOErrorException exc = new JobcenterSubmissionIOErrorException( "IOException receiving XML from server at URL: " + fullConnectionURL, e );

				exc.setFullConnectionURL( fullConnectionURL );
				exc.setErrorStreamContents( errorStreamContents );

				throw exc;


			} finally {

				if ( bufferedInputStream != null ) {

					try {
						bufferedInputStream.close();
					} catch ( IOException e ) {


						byte[] errorStreamContents = null;

						try {
							errorStreamContents= getErrorStreamContents( httpURLConnection );
						} catch ( Exception ex ) {

						}
						JobcenterSubmissionIOErrorException exc = new JobcenterSubmissionIOErrorException( "IOException closing input Stream from server at URL: " + fullConnectionURL, e );

						exc.setFullConnectionURL( fullConnectionURL );
						exc.setErrorStreamContents( errorStreamContents );

						throw exc;
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
					JobcenterSubmissionHTTPErrorException exc = new JobcenterSubmissionHTTPErrorException( "Unsuccessful HTTP response code of " + httpResponseCode
							+ " connecting to server at URL: " + fullConnectionURL );

					exc.setFullConnectionURL( fullConnectionURL );
					exc.setHttpErrorCode( httpResponseCode );
					exc.setErrorStreamContents( errorStreamContents );

					throw exc;

				}


			} catch ( IOException e ) {

				byte[] errorStreamContents = null;

				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection );
				} catch ( Exception ex ) {

				}
				JobcenterSubmissionIOErrorException exc = new JobcenterSubmissionIOErrorException( "IOException getting HTTP response code from server at URL: " + fullConnectionURL, e );

				exc.setFullConnectionURL( fullConnectionURL );
				exc.setErrorStreamContents( errorStreamContents );

				throw exc;
			}


			if ( baseResponse.isErrorResponse() ) {
				
				StringBuilder msgSB = new StringBuilder( 1000 );

				msgSB.append( "Submission of job failed.  Error code = " ); 
				msgSB.append( baseResponse.getErrorCode()  ); 
				msgSB.append(  ", error code desc = "  ); 
				msgSB.append( baseResponse.getErrorCodeDescription() ); 
				msgSB.append(  ", the client's IP address as seen by the server = "  ); 
				msgSB.append( baseResponse.getClientIPAddressAtServer() ); 
				msgSB.append( "\n  requestTypeName = |"  );  
				msgSB.append( requestTypeName  ); 
				msgSB.append( "|, requestId = "  ); 
				msgSB.append( requestId );  
				
				if ( jobTypeName != null ) {
					
					msgSB.append( ", jobTypeName = |"  ); 
					msgSB.append( jobTypeName ); 
					msgSB.append( "|" );
				}
				

				msgSB.append( "." );
				
				String msg = msgSB.toString();

				throw new JobcenterSubmissionServerErrorException( baseResponse.getErrorCode(), baseResponse.getErrorCodeDescription(), baseResponse.getClientIPAddressAtServer(), msg );
			}


			return baseResponse; 

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

		System.out.println( "Data from httpURLConnection.getErrorStream(): " + baos.toString() );

		return baos.toByteArray();
	}





	/**
	 * !!!!!!  Only set to true for unit testing
	 * 
	 * @param doNotSendToServer
	 */
	public void setDoNotSendToServer(boolean doNotSendToServer) {
		this.doNotSendToServer = doNotSendToServer;
	}





}
