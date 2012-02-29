package org.jobcenter.job_client_module_interface;

import java.util.Map;

public interface ModuleInterfaceRequest {

	/**
	 * @return the parameters for the request
	 */
	public  Map<String, String> getJobParameters( );


	/**
	 * @return  the assigned request id for this job.
	 *
	 * This requestId should be passed to submit job for another job that is to be associated with this job
	 */
	public int getRequestId(  );

}
