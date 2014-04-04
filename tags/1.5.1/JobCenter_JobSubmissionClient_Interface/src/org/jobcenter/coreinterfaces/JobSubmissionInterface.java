package org.jobcenter.coreinterfaces;

import java.util.Map;

import org.jobcenter.client_exceptions.JobcenterSubmissionGeneralErrorException;
import org.jobcenter.client_exceptions.JobcenterSubmissionHTTPErrorException;
import org.jobcenter.client_exceptions.JobcenterSubmissionIOErrorException;
import org.jobcenter.client_exceptions.JobcenterSubmissionMalformedURLErrorException;
import org.jobcenter.client_exceptions.JobcenterSubmissionServerErrorException;
import org.jobcenter.client_exceptions.JobcenterSubmissionXML_JAXBErrorException;

public interface JobSubmissionInterface {

	/**
	 * @param connectionURL - Must only contain IP address ( and optional port ) and context and end in "/"
	 * @throws Throwable
	 */
	public void init( String connectionURL ) throws Throwable;

	/**
	 *
	 */
	public void destroy();

	/**
	 * Set the name in the node table to be used for this submit job,
	 *   or use the default specified in project JobCenter_Base class org.jobcenter.constants.Constants.SUBMISSION_CLIENT_NODE_NAME_DEFAULT
	 *
	 * @param nodeName
	 */
	public void setNodeName( String nodeName );

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
	public int submitJob( String requestTypeName, Integer requestId, String jobTypeName, String submitter, Integer priority, Map<String, String> jobParameters )
	throws
	JobcenterSubmissionGeneralErrorException,
	JobcenterSubmissionServerErrorException,
	JobcenterSubmissionHTTPErrorException,
	JobcenterSubmissionMalformedURLErrorException,
	JobcenterSubmissionIOErrorException,
	JobcenterSubmissionXML_JAXBErrorException;

}
