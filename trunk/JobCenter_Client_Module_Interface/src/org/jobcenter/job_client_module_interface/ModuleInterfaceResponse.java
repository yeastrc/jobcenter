package org.jobcenter.job_client_module_interface;

import org.jobcenter.exceptions.JobcenterModuleInterfaceInvalidCharactersInStringException;
import org.jobcenter.exceptions.JobcenterSystemErrorException;

/**
 * Interface for passing the response of running the job to the Jobcenter client
 *
 */
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
	 * @param messageType - a value from org.jobcenter.constants.RunMessageTypesConstants
	 * @param message
	 * 
	 * @throws JobcenterModuleInterfaceInvalidCharactersInStringException
	 *             thrown if the message passed in cannot be marshaled and unmarshaled as XML UTF-8 
	 *             
	 * @throws JobcenterSystemErrorException - when general internal Jobcenter error
	 */
	public void addRunMessage( int messageType, String message ) throws JobcenterModuleInterfaceInvalidCharactersInStringException, JobcenterSystemErrorException;


	/**
	 * Add a run message that is the result of executing the job
	 * 
	 * If the message contains invalid XML characters, it will be cleaned using replacementStringForInvalidXMLChars
	 * to replace the invalid characters.
	 *
	 * @param messageType - a value from org.jobcenter.constants.RunMessageTypesConstants
	 * @param message
	 * 
	 * @param replacementStringForInvalidXMLChars - a string that will replace each and every character with
	 *         hex values < x20 that are not valid XML characters 
	 * 
	 * @throws JobcenterModuleInterfaceInvalidCharactersInStringException
	 *             thrown if the message passed in cannot be marshaled and unmarshaled as XML UTF-8 after cleaning
	 *             
	 * @throws JobcenterSystemErrorException - when general internal Jobcenter error
	 */
	public void addRunMessageAutoCleanString( int messageType, String message, String replacementStringForInvalidXMLChars ) throws JobcenterModuleInterfaceInvalidCharactersInStringException, JobcenterSystemErrorException;


	/**
	 * Add a run message that is the result of executing the job
	 * 
	 * If the message contains characters with values < x20 that are invalid XML characters
	 *   or non-ascii characters with values > x7F,  
	 *   it will be cleaned using replacementStringForInvalidXMLChars
	 *   to replace the invalid characters.
	 *
	 * @param messageType - a value from org.jobcenter.constants.RunMessageTypesConstants
	 * @param message
	 * 
	 * @param replacementStringForInvalidChars - a string that will replace every character with
	 *         hex values < x20 that are not valid XML characters 
	 *         and every non-ASCII character with values > x7F
	 * 
	 * @throws JobcenterModuleInterfaceInvalidCharactersInStringException
	 *             thrown if the message passed in cannot be marshaled and unmarshaled as XML UTF-8 after cleaning
	 *             
	 * @throws JobcenterSystemErrorException - when general internal Jobcenter error
	 */
	public void addRunMessageAutoCleanStringToASCII( int messageType, String message, String replacementStringForInvalidChars ) throws JobcenterModuleInterfaceInvalidCharactersInStringException, JobcenterSystemErrorException;


	/**
	 * Add a value to the Output Params saved as part of this run
	 *
	 * @param key
	 * @param value
	 * @throws JobcenterModuleInterfaceInvalidCharactersInStringException
	 *             thrown if the key or value passed in cannot be marshaled and unmarshaled as XML UTF-8
	 *             
	 * @throws JobcenterSystemErrorException - when general internal Jobcenter error
	 */
	public void addRunOutputParam( String key, String value ) throws JobcenterModuleInterfaceInvalidCharactersInStringException, JobcenterSystemErrorException;
	

	/**
	 * Add a value to the Output Params saved as part of this run
	 *
	 * If the value parameter contains characters with values < x20 that are invalid XML characters
	 *   or non-ascii characters with values > x7F,  
	 *   it will be cleaned using replacementStringForInvalidXMLChars
	 *   to replace the invalid characters.
	 * 
	 * The key will never be altered
	 * 
	 *
	 *  Should not use allow cleaning of invalid for XML Chars and to ASCII if saving data for a retry 
	 *    that needs the data un-modified.
	 *  In that case, consider using base-64 encoding for the data to ensure it is not a problem,
	 *  Otherwise, fail the job since unable to pass data to a retry of the job,
	 *     and put a plain text string in RunOutputParam that such problem was encountered so that 
	 *     retry knows there is data missing from RunOutputParam
	 * 
	 * @param key
	 * @param value
	 * 
	 * @param replacementStringForInvalidXMLChars - a string that will replace each and every character with
	 *         hex values < x20 that are not valid XML characters 
	 * 
	 * @throws JobcenterModuleInterfaceInvalidCharactersInStringException
	 *             thrown if the key or value passed in cannot be marshaled and unmarshaled as XML UTF-8 after cleaning
	 *             
	 * @throws JobcenterSystemErrorException - when general internal Jobcenter error
	 */
	public void addRunOutputParamAutoCleanString( String key, String value, String replacementStringForInvalidXMLChars ) throws JobcenterModuleInterfaceInvalidCharactersInStringException, JobcenterSystemErrorException;
	
	

	/**
	 * Add a value to the Output Params saved as part of this run
	 *
	 * If the value contains invalid XML characters, it will be cleaned using replacementStringForInvalidXMLChars
	 * to replace the invalid characters.
	 * 
	 * The key will never be altered
	 * 
	 *
	 *  Should not use allow cleaning of invalid for XML Chars and to ASCII if saving data for a retry 
	 *    that needs the data un-modified.
	 *  In that case, consider using base-64 encoding for the data to ensure it is not a problem,
	 *  Otherwise, fail the job since unable to pass data to a retry of the job,
	 *     and put a plain text string in RunOutputParam that such problem was encountered so that 
	 *     retry knows there is data missing from RunOutputParam
	 *     
	 *     
	 * @param key
	 * @param value
	 * 
	 * @param replacementStringForInvalidChars - a string that will replace every character with
	 *         hex values < x20 that are not valid XML characters 
	 *         and every non-ASCII character with values > x7F
	 * 
	 * @throws JobcenterModuleInterfaceInvalidCharactersInStringException
	 *             thrown if the key or value passed in cannot be marshaled and unmarshaled as XML UTF-8 after cleaning
	 *             
	 * @throws JobcenterSystemErrorException - when general internal Jobcenter error
	 */
	public void addRunOutputParamAutoCleanStringToASCII( String key, String value, String replacementStringForInvalidXMLChars ) throws JobcenterModuleInterfaceInvalidCharactersInStringException, JobcenterSystemErrorException;
	

}
