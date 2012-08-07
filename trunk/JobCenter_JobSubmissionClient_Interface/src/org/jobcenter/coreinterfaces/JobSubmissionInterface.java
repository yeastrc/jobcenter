package org.jobcenter.coreinterfaces;

import java.util.Map;

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
	 * @param priority - Pass null to use the priority in the Job_type table configuration
	 * @param jobParameters
	 * @return requestId - the next assigned id related to the particular requestTypeName.  Will return the passed in requestId if one is provided ( not null )
	 * @throws Throwable - throws an error if any errors related to submitting the job
	 */
	public int submitJob( String requestTypeName, Integer requestId, String jobTypeName, String submitter, Integer priority, Map<String, String> jobParameters ) throws Throwable;

}
