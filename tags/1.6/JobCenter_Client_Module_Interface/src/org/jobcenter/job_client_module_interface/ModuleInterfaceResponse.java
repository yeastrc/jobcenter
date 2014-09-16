package org.jobcenter.job_client_module_interface;

public interface ModuleInterfaceResponse {

	/**
	 * Set the status code that is the result of executing the job
	 *
	 * @param statusCode - a value from org.jobcenter.constants.JobStatusValuesConstants
	 */
	public void setStatusCode( int statusCode );


	/**
	 * Add a run message that is the result of executing the job
	 *
	 * @param type - a value from org.jobcenter.constants.RunMessageTypesConstants
	 * @param message
	 */
	public void addRunMessage( int type, String message );


	/**
	 * Add a value to the Output Params saved as part of this run
	 *
	 * @param key
	 * @param value
	 */
	public void addRunOutputParam( String key, String value );
}
