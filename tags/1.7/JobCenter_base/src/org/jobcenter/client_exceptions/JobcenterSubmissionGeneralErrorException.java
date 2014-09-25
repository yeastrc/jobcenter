package org.jobcenter.client_exceptions;

/**
 * Exception thrown when Jobcenter Submission client encounters an General error
 *
 */
public class JobcenterSubmissionGeneralErrorException extends RuntimeException {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	public JobcenterSubmissionGeneralErrorException() {
		super();
	}
	public JobcenterSubmissionGeneralErrorException(String message, Throwable cause) {
		super(message, cause);
	}
	public JobcenterSubmissionGeneralErrorException(String message) {
		super(message);
	}
	public JobcenterSubmissionGeneralErrorException(Throwable cause) {
		super(cause);
	}
}
