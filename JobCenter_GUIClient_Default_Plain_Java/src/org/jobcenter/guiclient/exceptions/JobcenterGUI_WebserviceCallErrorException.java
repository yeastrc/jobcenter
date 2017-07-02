package org.jobcenter.guiclient.exceptions;

/**
 * Error calling Jobcenter GUI Webservice
 *
 */
public class JobcenterGUI_WebserviceCallErrorException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private boolean badHTTPStatusCode;
	private boolean serverURLError;
	private boolean serverSendReceiveDataError;
	private boolean connectToServerError;
	private boolean failToEncodeDataToSendToServer;
	private boolean failToDecodeDataReceivedFromServer;
	private boolean serverAppCodeReturnedErrorResponse;
	
	public boolean isServerAppCodeReturnedErrorResponse() {
		return serverAppCodeReturnedErrorResponse;
	}

	public void setServerAppCodeReturnedErrorResponse(boolean serverAppCodeReturnedErrorResponse) {
		this.serverAppCodeReturnedErrorResponse = serverAppCodeReturnedErrorResponse;
	}

	private Integer httpStatusCode;
	private String webserviceURL;
	
	private byte[] errorStreamContents;

	public JobcenterGUI_WebserviceCallErrorException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JobcenterGUI_WebserviceCallErrorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public JobcenterGUI_WebserviceCallErrorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public JobcenterGUI_WebserviceCallErrorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public JobcenterGUI_WebserviceCallErrorException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}


	public boolean isBadHTTPStatusCode() {
		return badHTTPStatusCode;
	}

	public void setBadHTTPStatusCode(boolean badHTTPStatusCode) {
		this.badHTTPStatusCode = badHTTPStatusCode;
	}

	public Integer getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(Integer httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	public boolean isFailToEncodeDataToSendToServer() {
		return failToEncodeDataToSendToServer;
	}

	public void setFailToEncodeDataToSendToServer(boolean failToEncodeDataToSendToServer) {
		this.failToEncodeDataToSendToServer = failToEncodeDataToSendToServer;
	}

	public boolean isFailToDecodeDataReceivedFromServer() {
		return failToDecodeDataReceivedFromServer;
	}

	public void setFailToDecodeDataReceivedFromServer(boolean failToDecodeDataReceivedFromServer) {
		this.failToDecodeDataReceivedFromServer = failToDecodeDataReceivedFromServer;
	}

	public boolean isServerURLError() {
		return serverURLError;
	}

	public void setServerURLError(boolean serverURLError) {
		this.serverURLError = serverURLError;
	}


	public String getWebserviceURL() {
		return webserviceURL;
	}

	public void setWebserviceURL(String webserviceURL) {
		this.webserviceURL = webserviceURL;
	}
	public boolean isConnectToServerError() {
		return connectToServerError;
	}

	public void setConnectToServerError(boolean connectToServerError) {
		this.connectToServerError = connectToServerError;
	}
	public byte[] getErrorStreamContents() {
		return errorStreamContents;
	}

	public void setErrorStreamContents(byte[] errorStreamContents) {
		this.errorStreamContents = errorStreamContents;
	}

	public boolean isServerSendReceiveDataError() {
		return serverSendReceiveDataError;
	}

	public void setServerSendReceiveDataError(boolean serverSendReceiveDataError) {
		this.serverSendReceiveDataError = serverSendReceiveDataError;
	}



}
