package org.jobcenter.client_exceptions;

/**
 * Exception thrown when Jobcenter Submission client encounters an HTTP error
 *
 */
public class JobcenterSubmissionHTTPErrorException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private int httpErrorCode;

	private byte[] errorStreamContents;

	private String fullConnectionURL;


	public JobcenterSubmissionHTTPErrorException() {
		super();
	}
	public JobcenterSubmissionHTTPErrorException( String message, Throwable cause) {
		super(message, cause);
	}
	public JobcenterSubmissionHTTPErrorException( String message) {
		super(message);
	}
	public JobcenterSubmissionHTTPErrorException(Throwable cause) {
		super(cause);
	}


	public int getHttpErrorCode() {
		return httpErrorCode;
	}
	public void setHttpErrorCode(int httpErrorCode) {
		this.httpErrorCode = httpErrorCode;
	}


	public byte[] getErrorStreamContents() {
		return errorStreamContents;
	}
	public void setErrorStreamContents(byte[] errorStreamContents) {
		this.errorStreamContents = errorStreamContents;
	}
	public String getFullConnectionURL() {
		return fullConnectionURL;
	}
	public void setFullConnectionURL(String fullConnectionURL) {
		this.fullConnectionURL = fullConnectionURL;
	}

}
