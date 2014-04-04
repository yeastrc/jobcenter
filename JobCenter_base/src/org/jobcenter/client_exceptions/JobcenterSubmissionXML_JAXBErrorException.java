package org.jobcenter.client_exceptions;

/**
 * Exception thrown when Jobcenter Submission client encounters an error related to XML/JAXB processing
 * turning the request Java object into XML to send to the server
 * or converting the XML sent by the server into a Java object
 *
 */
public class JobcenterSubmissionXML_JAXBErrorException extends RuntimeException {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String fullConnectionURL;

	public JobcenterSubmissionXML_JAXBErrorException() {
		super();
	}
	public JobcenterSubmissionXML_JAXBErrorException(String message, Throwable cause) {
		super(message, cause);
	}
	public JobcenterSubmissionXML_JAXBErrorException(String message) {
		super(message);
	}
	public JobcenterSubmissionXML_JAXBErrorException(Throwable cause) {
		super(cause);
	}
	public String getFullConnectionURL() {
		return fullConnectionURL;
	}
	public void setFullConnectionURL(String fullConnectionURL) {
		this.fullConnectionURL = fullConnectionURL;
	}
}
