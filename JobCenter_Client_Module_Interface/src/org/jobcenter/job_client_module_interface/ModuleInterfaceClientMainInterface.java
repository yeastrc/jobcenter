package org.jobcenter.job_client_module_interface;

public interface ModuleInterfaceClientMainInterface {



	/**
	 * Called when the object is instantiated.
	 * The object will not be used to process requests if this method throws an exception
	 *
	 * @param moduleInstanceNumber - The instance number of this module in this client,
	 *         incremented for each time the module instantiated while the client is running,
	 *         reset to 1 when the client is restarted.
	 *         This can be useful for separating logging or other file resources between
	 *         instances of the same module in the same client.
	 * @throws Throwable
	 */
	public void init( int moduleInstanceNumber ) throws Throwable;


	/**
	 * Called when the object will no longer be used
	 */
	public void destroy();



	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 *   and the job will be left in the state of "Running".
	 */
	public void shutdown();


	/**
	 * Called on a separate thread when a request comes from the user via updating the control file.
	 * The user has indicated that they want the job manager client to to stop processing new jobs
	 * and complete the jobs that are running.
	 *
	 * The module needs this information since if it is in an endless retry mode to submit the next job or
	 *   other efforts, it needs to fail the job and return.
	 */
	public void stopRunningAfterProcessingJob();



	/**
	 * called before and possibly after each request is processed to clear input parameters and stored errors
	 */
	public void reset() throws Throwable;

	/**
	 * Process the request
	 */
	public void processRequest( ModuleInterfaceRequest moduleInterfaceRequest, ModuleInterfaceResponse moduleInterfaceResponse, ModuleInterfaceJobProgress moduleInterfaceJobProgress, ModuleInterfaceClientServices moduleInterfaceClientServices ) throws Throwable;

}
