package org.jobcenter.job_client_module_interface;

import java.util.Map;

import org.jobcenter.exceptions.JobcenterModuleInterfaceInvalidCharactersInStringException;
import org.jobcenter.exceptions.JobcenterSystemErrorException;

/**
 * This interface defines services available to the client
 *
 */
public interface ModuleInterfaceClientServices {


	/**
	 * Using a regex, replace all characters with hex values < x20 that are not allowed with replacementString
	 *  
	 *  	Allowed characters (for values < x20) in XML UTF-8:   #x9 | #xA | #xD |
	 *  	  as referenced in http://www.w3.org/TR/xml/#charsets
	 *  
	 * @param input
	 * @param replacementString
	 * @return
	 */
	public String replaceInvalidCharactersInXMLUTF8InString( String input, String replacementString );
	
	/**
	 * Replace non ASCII characters ( > x7F ) with replacementString  
	 * 
	 * Use with method replaceInvalidCharactersInXMLUTF8InString(...) for full cleaning of invalid characters
	 * 
	 * @param input
	 * @param replacementString
	 * @return
	 */
	public String replaceNonAsciiCharactersInString( String input, String replacementString );

	
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
	 * 
	 * @return requestId - the next assigned id related to the particular requestTypeName.  Will return the passed in requestId if one is provided ( not null )
	 * 
	 * @throws JobcenterModuleInterfaceInvalidCharactersInStringException
	 *             thrown if the message passed in cannot be marshaled and unmarshaled as XML UTF-8
	 *              
	 * @throws JobcenterSystemErrorException - when general internal Jobcenter error
	 * 
	 * @throws Throwable - throws an error if any errors related to submitting the job
	 */
	public int submitJob( String requestTypeName, Integer requestId, String jobTypeName, String submitter, Integer priority, Map<String, String> jobParameters ) 
			 throws JobcenterModuleInterfaceInvalidCharactersInStringException, Throwable;


	/**
	 * @param requestTypeName - the name of the request type
	 * @param requestId - Pass in to relate the submitted job to an existing requestId.  Pass in null otherwise
	 * @param jobTypeName - the name of the job type
	 * @param submitter
	 * 
	 * @param priority - optional, if null, the priority on the job_type table is used
	 * @param requiredExecutionThreads - optional, 
	 *             if not null:
	 *                if the client max threads >= requiredExecutionThreads
	 *                the server will send no job until the client has available threads >= requiredExecutionThreads
	 * 
	 * @param jobParameters
	 * 
	 * @return requestId - the next assigned id related to the particular requestTypeName.  Will return the passed in requestId if one is provided ( not null )
	 * 
	 * @throws JobcenterModuleInterfaceInvalidCharactersInStringException
	 *             thrown if the message passed in cannot be marshaled and unmarshaled as XML UTF-8
	 *              
	 * @throws JobcenterSystemErrorException - when general internal Jobcenter error
	 * 
	 * @throws Throwable - throws an error if any errors related to submitting the job
	 */
	public int submitJob( String requestTypeName, Integer requestId, String jobTypeName, String submitter, Integer priority, Integer requiredExecutionThreads, Map<String, String> jobParameters ) 
			 throws JobcenterModuleInterfaceInvalidCharactersInStringException, Throwable;

}
