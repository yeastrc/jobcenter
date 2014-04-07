package org.jobcenter.client.main;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import org.jobcenter.request.SubmitJobRequest;
import org.jobcenter.response.SubmitJobResponse;


/**
 *
 *
 */
public class SubmissionClientConnectionToServer implements JobSubmissionInterface {


//	private static Logger log = Logger.getLogger(SubmissionClientConnectionToServer.class);

	private static final int SUCCESS_HTTP_RETURN_CODE = 200;

	private static final String CONNECTION_URL_EXTENSION

			= WebServiceURLConstants.WEB_SERVICE_URL_BASE_POST_CONTEXT + WebServiceURLConstants.SUBMIT_JOB;


	private static final String CONTENT_TYPE_SEND_RECEIVE = "application/xml";


	private String connectionURL = null;


	private String submissionNodeName = Constants.SUBMISSION_CLIENT_NODE_NAME_DEFAULT;



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

		if ( connectionURL == null ) {

			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}

		if ( jobTypeName == null ) {

			throw new IllegalArgumentException( "jobTypeName cannot be null" );
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



		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance( SubmitJobResponse.class, SubmitJobRequest.class );
		} catch (JAXBException e) {

			JobcenterSubmissionXML_JAXBErrorException exc = new JobcenterSubmissionXML_JAXBErrorException( "JAXBException Setting up JAXBContext for sending XML to server at URL: " + fullConnectionURL, e );

			exc.setFullConnectionURL( fullConnectionURL );

			throw exc;

		}



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


				Marshaller marshaller = jaxbContext.createMarshaller();

				marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );

				marshaller.marshal( submitJobRequest, bufferedOutputStream ) ;

			} catch ( JAXBException e ) {

				JobcenterSubmissionXML_JAXBErrorException exc = new JobcenterSubmissionXML_JAXBErrorException( "JAXBException creating XML to send to server at URL: " + fullConnectionURL, e );

				exc.setFullConnectionURL( fullConnectionURL );

				throw exc;

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

			SubmitJobResponse submitJobResponse = null;

			BufferedInputStream bufferedInputStream = null;

			try {

				InputStream inputStream = httpURLConnection.getInputStream();

				bufferedInputStream = new BufferedInputStream( inputStream );

				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

				submitJobResponse = (SubmitJobResponse) unmarshaller.unmarshal( bufferedInputStream );

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


			if ( submitJobResponse.isErrorResponse() ) {

				String msg = "Submission of job failed.  Error code = " + submitJobResponse.getErrorCode() + ", error code desc = " + submitJobResponse.getErrorCodeDescription()
				+ ", the client's IP address as seen by the server = " + submitJobResponse.getClientIPAddressAtServer()
				+ "\n  requestTypeName = |" + requestTypeName + "|, requestId = " + requestId + ", jobTypeName = |" + jobTypeName + "|.";

				throw new JobcenterSubmissionServerErrorException( submitJobResponse.getErrorCode(), submitJobResponse.getErrorCodeDescription(), submitJobResponse.getClientIPAddressAtServer(), msg );
			}


			return submitJobResponse.getRequestId();

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




}
