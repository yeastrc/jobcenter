package org.jobcenter.client_exceptions;

/**
 * Exception thrown when Jobcenter Submission client receives an error from the server
 *
 */
public class JobcenterSubmissionServerErrorException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private int errorCode;
	private String errorCodeDescription;
	private String clientIPAddressAtServer;



	public JobcenterSubmissionServerErrorException() {
		super();
	}
	public JobcenterSubmissionServerErrorException(int errorCode, String errorCodeDescription, String clientIPAddressAtServer, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
		this.errorCodeDescription = errorCodeDescription;
		this.clientIPAddressAtServer = clientIPAddressAtServer;
	}
	public JobcenterSubmissionServerErrorException(int errorCode, String errorCodeDescription, String clientIPAddressAtServer, String message) {
		super(message);
		this.errorCode = errorCode;
		this.errorCodeDescription = errorCodeDescription;
		this.clientIPAddressAtServer = clientIPAddressAtServer;
	}
	public JobcenterSubmissionServerErrorException(Throwable cause) {
		super(cause);
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorCodeDescription() {
		return errorCodeDescription;
	}
	public void setErrorCodeDescription(String errorCodeDescription) {
		this.errorCodeDescription = errorCodeDescription;
	}
	public String getClientIPAddressAtServer() {
		return clientIPAddressAtServer;
	}
	public void setClientIPAddressAtServer(String clientIPAddressAtServer) {
		this.clientIPAddressAtServer = clientIPAddressAtServer;
	}


}
