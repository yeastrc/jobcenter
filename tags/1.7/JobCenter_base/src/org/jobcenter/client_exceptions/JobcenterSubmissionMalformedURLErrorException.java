package org.jobcenter.client_exceptions;

/**
 * Exception thrown when Jobcenter Submission client encounters an Malformed URL error
 *
 */
public class JobcenterSubmissionMalformedURLErrorException extends RuntimeException {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String fullConnectionURL;


	public JobcenterSubmissionMalformedURLErrorException() {
		super();
	}
	public JobcenterSubmissionMalformedURLErrorException(String message, Throwable cause) {
		super(message, cause);
	}
	public JobcenterSubmissionMalformedURLErrorException(String message) {
		super(message);
	}
	public JobcenterSubmissionMalformedURLErrorException(Throwable cause) {
		super(cause);
	}

	public String getFullConnectionURL() {
		return fullConnectionURL;
	}
	public void setFullConnectionURL(String fullConnectionURL) {
		this.fullConnectionURL = fullConnectionURL;
	}
}
