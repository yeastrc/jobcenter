package org.jobcenter.job_client_module_interface;

/**
 * Call backs for the module to indicate the progress on processing the job
 *
 */
public interface ModuleInterfaceJobProgress {


	/**
	 * Call after completing some unit of work to indicate that the module is still making progress in processing the job
	 *
	 * If calling updatePercentComplete(...) below, then this does not need to be called
	 */
	public void progressPing();

	/**
	 * call if the module supports reporting a percentage complete.
	 */
	public void supportsPercentComplete();


	/**
	 * the percentage the task is complete, if available.  Will only be expected to be called if supportsPercentComplete() is called;
	 *
	 * @param percentComplete - the percentage the task is complete in whole numbers, '100' when fully complete.
	 */
	public void updatePercentComplete( int percentComplete );


}
