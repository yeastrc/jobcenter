package org.jobcenter.client.main;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.jobcenter.constants.WebServiceURLConstants;
import org.jobcenter.request.JobRequest;
import org.jobcenter.response.JobResponse;

/**
 * Not called by code outside of ClientConnectionToServer
 * 
 * Called from methods in ClientConnectionToServer for methods provided here:
 * 
 * 
 * Package Private (at least for now)
 */
class ClientConnectionToServer_PlainJava {

	private static Logger log = Logger.getLogger(ClientConnectionToServer_PlainJava.class);

	private static final String XML_ENCODING_CHARACTER_SET = StandardCharsets.UTF_8.toString();
	private static final int SUCCESS_HTTP_RETURN_CODE = 200;
	private static final String CONTENT_TYPE_SEND_RECEIVE = "application/xml";

	private static final int CONNECT_TIMEOUT_HttpURLConnection_SECONDS = 15;
	private static final int CONNECT_TIMEOUT_HttpURLConnection_MILLISECONDS = CONNECT_TIMEOUT_HttpURLConnection_SECONDS * 1000;

	private static final int READ_TIMEOUT_HttpURLConnection_SECONDS = 15;
	private static final int READ_TIMEOUT_HttpURLConnection_MILLISECONDS = READ_TIMEOUT_HttpURLConnection_SECONDS * 1000;
	
	//  Total Time allowed from Connection Open to Connection closed by server (send from server is complete)
	private static final int TOTAL_CONNECTION_TIMEOUT_HttpURLConnection_SECONDS = 60;
	private static final int TOTAL_CONNECTION_TIMEOUT_HttpURLConnection_MILLISECONDS = TOTAL_CONNECTION_TIMEOUT_HttpURLConnection_SECONDS * 1000;
	
	private volatile JAXBContext jaxbContext;
	private volatile boolean instanceInitialized;

//	private AtomicLong max_timeToRunMethod_MilliSeconds_From_callActualWebserviceOnServer = new AtomicLong();
//
//	private AtomicLong max_timeToRunMethod_MilliSeconds_From_callLoginAccountWebservice = new AtomicLong();

	private String connectionURL;
	
	private boolean checkConnectTimeoutApplied = true; // Only do once
	private boolean checkReadTimeoutApplied = true; // Only do once
	
	/* 
	 * If not fully configured, throws exception  
	 * 
	 * 
	 * 
	 */
	public void init( String connectionURL ) throws Exception {
		
		this.connectionURL = connectionURL;
		
		jaxbContext = 
				JAXBContext.newInstance( 
						JobRequest.class, 
						JobResponse.class );
		instanceInitialized = true;
	}
	
	/**
	 * @param jobRequest
	 * @return
	 * @throws Throwable
	 */
	public JobResponse getNextJobToProcess(JobRequest jobRequest) throws Throwable {
		
		log.info("getNextJobToProcess(...) called");

		if ( connectionURL == null ) {
			throw new IllegalStateException( "The connectionURL to the server is not configured." );
		}
		if ( jobRequest == null ) {
			throw new IllegalArgumentException( "jobRequest cannot be null" );
		}
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		final String webserviceURL = connectionURL + WebServiceURLConstants.GET_NEXT_JOB_FOR_CLIENT_TO_PROCESS;
		Object webserviceResponseAsObject = callActualWebserviceOnServer( jobRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof JobResponse ) ) {
			String msg = "Response unmarshaled to class other than JobResponse.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			throw new Exception( msg );
		}
		JobResponse webserviceResponse = null;
		try {
			webserviceResponse = (JobResponse) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as JobResponse: "
					+ e.toString();
			throw new Exception( msg );
		}
		return webserviceResponse;
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
		
		long methodTracking_Start = System.nanoTime();
		
		Date methodStartDate_Now = new Date();
		
		if ( log.isInfoEnabled() ) {
	
			log.info( "method callActualWebserviceOnServer(...) entered" );
		}

		try {
			
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
			
			{
				//  Confirm that the generated XML can be parsed.
				ByteArrayInputStream bais = new ByteArrayInputStream( requestXMLToSend );
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				XMLInputFactory xmlInputFactory = xmlInputFactory_XXE_Safe_Creator();
				XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new StreamSource( bais ) );
				@SuppressWarnings("unused")
				Object unmarshalledObject = unmarshaller.unmarshal( xmlStreamReader );
			}

		} catch ( Exception e ) {
			String msg = "Error. Fail to encode request to send to server: " + e.toString();
			throw new Exception(msg);
		}
		//   Create object for connecting to server
		URL urlObject;
		try {
			urlObject = new URL( webserviceURL );
		} catch (MalformedURLException e) {
			throw e;
		}
		//   Open connection to server
		URLConnection urlConnection;
		try {
			urlConnection = urlObject.openConnection();
		} catch (IOException e) {
			throw e;
		}
		// Downcast URLConnection to HttpURLConnection to allow setting of HTTP parameters 
		if ( ! ( urlConnection instanceof HttpURLConnection ) ) {
			throw new Exception( "Failed Downcast URLConnection to HttpURLConnection:  ! ( urlConnection instanceof HttpURLConnection )  " );
		}
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) urlConnection;
		} catch (Exception e) {
			throw new Exception( "Failed Downcast URLConnection to HttpURLConnection: ", e );
		}

		//  Set HttpURLConnection properties
		
		httpURLConnection.setConnectTimeout( CONNECT_TIMEOUT_HttpURLConnection_MILLISECONDS );
		httpURLConnection.setReadTimeout( READ_TIMEOUT_HttpURLConnection_MILLISECONDS );
		

		if ( this.checkConnectTimeoutApplied ){
			this.checkConnectTimeoutApplied = false;
			int connectTimeout = httpURLConnection.getConnectTimeout();
			if ( connectTimeout != CONNECT_TIMEOUT_HttpURLConnection_MILLISECONDS ) {
				log.warn( "httpURLConnection.setConnectTimeout(...) failed to set connect timeout.  httpURLConnection.getConnectTimeout() returned: "
						+ connectTimeout 
						+ ", httpURLConnection.setConnectTimeout(...) was passed: "
						+ CONNECT_TIMEOUT_HttpURLConnection_MILLISECONDS );
			}
		}
		if ( this.checkReadTimeoutApplied ){
			this.checkReadTimeoutApplied = false;
			int readTimeout = httpURLConnection.getReadTimeout();
			if ( readTimeout != READ_TIMEOUT_HttpURLConnection_MILLISECONDS ) {
				log.warn( "httpURLConnection.setReadTimeout(...) failed to set read timeout.  httpURLConnection.getReadTimeout() returned: "
						+ readTimeout 
						+ ", httpURLConnection.setReadTimeout(...) was passed: "
						+ READ_TIMEOUT_HttpURLConnection_MILLISECONDS );
			}
		}
		
		long currentTimeBeforeOpenHTTPConnection = System.currentTimeMillis();
		
		httpURLConnection.setRequestProperty( "Accept", CONTENT_TYPE_SEND_RECEIVE );
		httpURLConnection.setRequestProperty( "Content-Type", CONTENT_TYPE_SEND_RECEIVE );
		httpURLConnection.setDoOutput(true);
		// Send post request to server
		try {  //  Overall try/catch block to put "httpURLConnection.disconnect();" in the finally block
			try {
				httpURLConnection.connect();

			} catch ( SocketTimeoutException e ) {
				
				//  Connection Time out
			
				String msg = "SocketTimeoutException opening connection to server. CONNECT_TIMEOUT_HttpURLConnection_MILLISECONDS: " + CONNECT_TIMEOUT_HttpURLConnection_MILLISECONDS
						+ ", callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now
						+ ", webserviceURL: " + webserviceURL;
				log.error( msg );
				throw new Exception( msg, e );
				
			} catch ( IOException e ) {
				throw e;
			}

			{
				long currentTime = System.currentTimeMillis();
				long timeDiff = currentTime - currentTimeBeforeOpenHTTPConnection;
				if ( timeDiff > 5000 ) {
					String msg = "Time taken for open connection exceeds 5000 milliseconds. Time taken for open connection: " 
							+ timeDiff
							+ ".  NOT throwing Exception, allowing processing to continue."
							+ " callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now
							+ " webserviceURL: " + webserviceURL;
					log.error( msg );
				}
			}
			
			//  Send XML to server
			
			long currentTimeBeforeSendToServer = System.currentTimeMillis();
			
			BufferedOutputStream bufferedOutputStream = null;
			try {
				OutputStream outputStream = httpURLConnection.getOutputStream();
				bufferedOutputStream = new BufferedOutputStream( outputStream );
				bufferedOutputStream.write( requestXMLToSend );

			} catch ( SocketTimeoutException e ) {
				
				//  Connection Time out
				
				String msg = "SocketTimeoutException sending request to server. READ_TIMEOUT_HttpURLConnection_MILLISECONDS: " + READ_TIMEOUT_HttpURLConnection_MILLISECONDS
						+ ", callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now
						+ ", webserviceURL: " + webserviceURL;
				log.error( msg );
				throw new Exception( msg, e );
				
			} catch ( IOException e ) {
				byte[] errorStreamContents = null;
				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection );
				} catch ( Exception ex ) {
				}
				log.error("Failed send to server. "
						+ " callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now
						+ ", errorStreamContents: " + errorStreamContents, e );
				throw e;
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
						log.error("Failed send to server (Failed to close connection after failed send).  "
								+ " callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now
								+ ", errorStreamContents: " 
								+ errorStreamContents, e );
					}
				}
			}

			{
				long currentTime = System.currentTimeMillis();
				long timeDiff = currentTime - currentTimeBeforeSendToServer;
				if ( timeDiff > 5000 ) {
					String msg = "Time take for send request exceeds 5000 milliseconds. Time taken for send: " 
							+ timeDiff
							+ ".  NOT throwing Exception, allowing processing to continue."
							+ " callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now
							+ ", webserviceURL: " + webserviceURL;
					log.error( msg );
				}
			}
			
			{
				long currentTime = System.currentTimeMillis();
				long timeDiff = currentTime - currentTimeBeforeOpenHTTPConnection;
				if ( timeDiff > TOTAL_CONNECTION_TIMEOUT_HttpURLConnection_MILLISECONDS ) {
					String msg = "Time after send request exceeds total allowed connection time.  Time allowed, in milliseconds: "
							+ TOTAL_CONNECTION_TIMEOUT_HttpURLConnection_MILLISECONDS
							+ ", Time taken so far: " 
							+ timeDiff
							+ ", callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now;
					log.error( msg );
					throw new Exception( msg );
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
					String msg = ( "HTTP response code not '" + SUCCESS_HTTP_RETURN_CODE + "', is: " + httpResponseCode 
							+ ", callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now
							+ ", errorStreamContents: " + errorStreamContents );
					log.error( msg );
					throw new Exception(msg);
				}

			} catch ( SocketTimeoutException e ) {
				
				//  Connection Time out
				
				String msg = "SocketTimeoutException reading HTTP Status Code from server. READ_TIMEOUT_HttpURLConnection_MILLISECONDS: " + READ_TIMEOUT_HttpURLConnection_MILLISECONDS
						+ ", callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now
						+ ", webserviceURL: " + webserviceURL;
				log.error( msg );
				throw new Exception( msg, e );
				
			} catch ( IOException e ) {
				byte[] errorStreamContents = null;
				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection );
				} catch ( Exception ex ) {
				}
				String msg = ( "IOException getting HTTP response code from server at URL: " + webserviceURL
						+ ", callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now
						+ ", errorStreamContents: " + errorStreamContents  );
				log.error( msg );
				throw new Exception(msg, e);
			}
			{
				long currentTime = System.currentTimeMillis();
				long timeDiff = currentTime - currentTimeBeforeOpenHTTPConnection;
				if ( timeDiff > TOTAL_CONNECTION_TIMEOUT_HttpURLConnection_MILLISECONDS ) {
					String msg = "Time after Get HTTP Status Code exceeds total allowed connection time.  Time allowed, in milliseconds: "
							+ TOTAL_CONNECTION_TIMEOUT_HttpURLConnection_MILLISECONDS
							+ ", Time taken so far: " 
							+ timeDiff
							+ ", callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now;
					log.error( msg );
					throw new Exception( msg );
				}
			}
			//  Get response XML from server
			ByteArrayOutputStream outputStreamBufferOfServerResponse = new ByteArrayOutputStream( 1000000 );
			InputStream inputStream = null;
			try {
				inputStream = httpURLConnection.getInputStream();
				int readCount = 0;
				int readZeroLengthCount = 0;
				int nRead;
				byte[] data = new byte[ 16384 ];
				while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
					readCount++;
					if ( nRead == 0 ) {
						readZeroLengthCount++;
					}
					outputStreamBufferOfServerResponse.write(data, 0, nRead);
					{
						long currentTime = System.currentTimeMillis();
						long timeDiff = currentTime - currentTimeBeforeOpenHTTPConnection;
						if ( timeDiff > TOTAL_CONNECTION_TIMEOUT_HttpURLConnection_MILLISECONDS ) {
							String msg = "Time after Single Read from server exceeds total allowed connection time.  Time allowed, in milliseconds: "
									+ TOTAL_CONNECTION_TIMEOUT_HttpURLConnection_MILLISECONDS
									+ ", Time taken so far: " 
									+ timeDiff
									+ ", readCount: " + readCount
									+ ", readZeroLengthCount (# bytes read was zero): " + readZeroLengthCount
									+ ", callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now
									+ ", webserviceURL: " + webserviceURL;
							log.error( msg );
							throw new Exception( msg );
						}
					}
				}

			} catch ( SocketTimeoutException e ) {
				
				//  Connection Time out
				
				String msg = "SocketTimeoutException reading response from server. READ_TIMEOUT_HttpURLConnection_MILLISECONDS: " + READ_TIMEOUT_HttpURLConnection_MILLISECONDS
						+ ", callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now
						+ ", webserviceURL: " + webserviceURL;
				log.error( msg );
				throw new Exception( msg, e );
				
			} catch ( IOException e ) {
				byte[] errorStreamContents = null;
				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection );
				} catch ( Exception ex ) {
				}
				String msg = ( "IOException receiving XML from server at URL: " + webserviceURL
						+ ", callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now
						+ ", errorStreamContents: " + errorStreamContents );
				log.error( msg );
				throw new Exception(msg, e);
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
						String msg = ( "IOException closing input Stream from server at URL: " + webserviceURL
								+ ", callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now
								+ ", errorStreamContents: " + errorStreamContents  );
						log.error( msg );
						throw new Exception(msg, e);
					}
				}
			}
			byte[] serverResponseByteArrayFromServer = outputStreamBufferOfServerResponse.toByteArray();
			
			byte[] serverResponseByteArray = serverResponseByteArrayFromServer;

			ByteArrayInputStream inputStreamBufferOfServerResponse = new ByteArrayInputStream( serverResponseByteArray );
			// Unmarshal received XML into Java objects
			try {
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				XMLInputFactory xmlInputFactory = xmlInputFactory_XXE_Safe_Creator();
				XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new StreamSource( inputStreamBufferOfServerResponse ) );
				webserviceResponseAsObject = unmarshaller.unmarshal( xmlStreamReader );
			} catch ( JAXBException e ) {
				String msg = ( "JAXBException unmarshalling XML received from server at URL: " + webserviceURL
						+ ", callActualWebserviceOnServer(...):Method Enter Date/Time: " + methodStartDate_Now );
				String logMsg = msg;
				try {
					logMsg += ", serverResponseByteArray as String " + new String( serverResponseByteArray, XML_ENCODING_CHARACTER_SET ); 
				} catch ( Throwable t ) {
					//  Eat Exception
				}
				log.error( logMsg );
				throw new Exception(msg, e);
			}
			
			{
				long methodTracking_End = System.nanoTime();

				long timeToRunMethod_NanoSeconds = methodTracking_End - methodTracking_Start;
				long timeToRunMethod_MilliSeconds = (long) ( timeToRunMethod_NanoSeconds / 1000000 );
				
				
//				if ( log.isInfoEnabled() ) {
//			
//					process_timeToRunMethod_MilliSeconds_From_callActualWebserviceOnServer( timeToRunMethod_MilliSeconds );
//				}
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
		
		try ( InputStream inputStream = httpURLConnection.getErrorStream() ) {
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
	

	/**
	 * @return Create XMLInputFactory object that is XXE safe
	 */
	private XMLInputFactory xmlInputFactory_XXE_Safe_Creator() {
		
		XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
		xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		
		return xmlInputFactory;
	}
	
	
}
