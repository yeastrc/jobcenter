package org.jobcenter.client_exceptions;

/**
 * Exception thrown when Jobcenter Submission client encounters an IO error
 *
 */
public class JobcenterSubmissionIOErrorException extends RuntimeException {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private byte[] errorStreamContents;

	private String fullConnectionURL;


	public JobcenterSubmissionIOErrorException() {
		super();
	}

	public JobcenterSubmissionIOErrorException( String message, Throwable cause) {
		super(message, cause);
	}
	public JobcenterSubmissionIOErrorException( String message) {
		super(message);
	}


	public JobcenterSubmissionIOErrorException(Throwable cause) {
		super(cause);
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
