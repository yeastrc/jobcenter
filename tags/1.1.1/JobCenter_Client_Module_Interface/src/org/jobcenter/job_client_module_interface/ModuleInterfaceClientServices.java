package org.jobcenter.job_client_module_interface;

import java.util.Map;

/**
 * This interface defines services available to the client
 *
 */
public interface ModuleInterfaceClientServices {


	/**
	 * Returns the Run Output Params from the previous run for the current job.
	 * Returns null if no previous run for the current job.
	 * @return
	 * @throws Throwable
	 */
	public Map<String, String> getPreviousRunOutputParams() throws Throwable;

	/**
	 * Returns the Run Output Params from the first run ( not including the current run ) for the current job.
	 * Returns null if no first run ( not including the current run ) run for the current job.
	 * @return
	 * @throws Throwable
	 */
	public Map<String, String> getFirstRunOutputParams() throws Throwable;

	/**
	 * Provides a count of the runs for the job being processed.  Only the runs before the current run are included.
	 * @return
	 * @throws Throwable
	 */
	public int getRunCount() throws Throwable;

	/**
	 * Returns the Run Output Params from the run based on the index ( not including the current run ) for the current job.
	 * Returns null if no run based on the index exists ( not including the current run ) for the current job.
	 * @return
	 * @throws Throwable
	 */
	public Map<String, String> getRunOutputParamsUsingIndex( int index ) throws Throwable;

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
