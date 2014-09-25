package org.jobcenter.moduleinterfaceimpl;

import java.util.List;
import java.util.Map;

import org.jobcenter.base_exceptions.TestMarshalThenUnMarshalObjectException;
import org.jobcenter.dto.RunMessageDTO;
import org.jobcenter.exceptions.JobcenterModuleInterfaceInvalidCharactersInStringException;
import org.jobcenter.exceptions.JobcenterSystemErrorException;
import org.jobcenter.job_client_module_interface.ModuleInterfaceResponse;
import org.jobcenter.test_marshal_then_unmarshal_object.TestMarshalThenUnMarshalObject;
import org.jobcenter.util.CleanInvalidCharactersFromString;

/**
 * 
 *
 */
/**
 * 
 *
 */
public class ModuleInterfaceModuleResponseImpl implements ModuleInterfaceResponse {


	private int statusCode;

	private List<RunMessageDTO> runMessages;

	private Map<String, String> runOutputParams;


	@Override
	public void setStatusCode(int statusCode) {

		this.statusCode = statusCode;
	}

	
	@Override
	public void addRunMessage( int messageType, String message ) throws JobcenterModuleInterfaceInvalidCharactersInStringException, JobcenterSystemErrorException {
		
		addRunMessageAutoCleanStringIfSpecified( false /* doAutoCleaningOfString */, false /* doASCIIAutoCleaningOfString */,
				messageType, message, null /* replacementString */ );
	}

	@Override
	public void addRunMessageAutoCleanString( int messageType, String message, String replacementString )
			throws JobcenterModuleInterfaceInvalidCharactersInStringException,JobcenterSystemErrorException {

		addRunMessageAutoCleanStringIfSpecified( true /* doAutoCleaningOfString */, false /* doASCIIAutoCleaningOfString */,
				messageType, message, replacementString );
	}
	

	@Override
	public void addRunMessageAutoCleanStringToASCII(int messageType, String message, String replacementStringForInvalidChars)
			throws JobcenterModuleInterfaceInvalidCharactersInStringException,
			JobcenterSystemErrorException {

		addRunMessageAutoCleanStringIfSpecified( true /* doAutoCleaningOfString */, true /* doASCIIAutoCleaningOfString */,
				messageType, message, replacementStringForInvalidChars );

		
	}

	/**
	 * @param doAutoCleaningOfString
	 * @param messageType
	 * @param message
	 * @param replacementString
	 * @throws JobcenterModuleInterfaceInvalidCharactersInStringException
	 * @throws JobcenterSystemErrorException
	 */
	private void addRunMessageAutoCleanStringIfSpecified( 
			boolean doAutoCleaningOfString, 
			boolean doASCIIAutoCleaningOfString,
			int messageType, 
			String message, 
			String replacementString)
			throws JobcenterModuleInterfaceInvalidCharactersInStringException,JobcenterSystemErrorException {


		
		TestMarshalThenUnMarshalObject testMarshalThenUnMarshalObject = TestMarshalThenUnMarshalObject.getInstance();


		try {

			if ( doAutoCleaningOfString ) {
				
				//  if invalid, do XML character cleaning

				message = CleanInvalidCharactersFromString.cleanInvalid_XML_CharactersFromString( message, replacementString );

			} else if ( doASCIIAutoCleaningOfString ) {

				//  if invalid, do XML character cleaning, if still invalid, do non-ASCII cleaning
				
				message = CleanInvalidCharactersFromString.cleanInvalid_XML_Characters_AndNonASCII_FromString( message, replacementString );

			} else {
				
				//  Only test for invalid

				testMarshalThenUnMarshalObject.testMarshalThenUnMarshalSingleString( message );
			}

		} catch ( TestMarshalThenUnMarshalObjectException ex ) {

			throw new JobcenterModuleInterfaceInvalidCharactersInStringException( ex.getCause() );
		} catch ( Exception e ) {

			throw new JobcenterSystemErrorException( e );
		}


		RunMessageDTO runMessageDTO = new RunMessageDTO();

		runMessageDTO.setType( messageType );
		runMessageDTO.setMessage( message );

		runMessages.add( runMessageDTO );
	}


	@Override
	public void addRunOutputParam(String key, String value) throws JobcenterModuleInterfaceInvalidCharactersInStringException, JobcenterSystemErrorException {

		addRunOutputParamAutoCleanStringIfSpecified( false /* doAutoCleaningOfString */, false /* doASCIIAutoCleaningOfString */, 
				key, value, null /* replacementString */ );
	}
	

	@Override
	public void addRunOutputParamAutoCleanString(String key, String value,
			String replacementString)
			throws JobcenterModuleInterfaceInvalidCharactersInStringException,
			JobcenterSystemErrorException {

		addRunOutputParamAutoCleanStringIfSpecified( true /* doAutoCleaningOfString */, false /* doASCIIAutoCleaningOfString */, 
				key, value, replacementString );
	}
	
	/**
	 * Add a value to the Output Params saved as part of this run
	 *
	 * If the value contains invalid XML characters, it will be cleaned using replacementString
	 * to replace the invalid characters.
	 * 
	 * The key will never be altered
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
	public void addRunOutputParamAutoCleanStringToASCII( String key, String value, String replacementString ) throws JobcenterModuleInterfaceInvalidCharactersInStringException, JobcenterSystemErrorException {
		
		addRunOutputParamAutoCleanStringIfSpecified( true /* doAutoCleaningOfString */, true /* doASCIIAutoCleaningOfString */, 
				key, value, replacementString );
	}
	

		
	
	
	/**
	 * @param doAutoCleaningOfString
	 * @param doASCIIAutoCleaningOfString
	 * @param key
	 * @param value
	 * @param replacementString
	 * @throws JobcenterModuleInterfaceInvalidCharactersInStringException
	 * @throws JobcenterSystemErrorException
	 */
	private void addRunOutputParamAutoCleanStringIfSpecified( 
			boolean doAutoCleaningOfString,
			boolean doASCIIAutoCleaningOfString, 
			String key, 
			String value,
			String replacementString)
			throws JobcenterModuleInterfaceInvalidCharactersInStringException,
			JobcenterSystemErrorException {
		
		
		TestMarshalThenUnMarshalObject testMarshalThenUnMarshalObject = TestMarshalThenUnMarshalObject.getInstance();



		//  not altering key so only test it once

		try {
			testMarshalThenUnMarshalObject.testMarshalThenUnMarshalSingleString( key );

		} catch ( TestMarshalThenUnMarshalObjectException ex ) {

			throw new JobcenterModuleInterfaceInvalidCharactersInStringException( ex.getCause() );
		} catch ( Exception e ) {

			throw new JobcenterSystemErrorException( e );
		}

		//  Now test the value


		try {

			if ( doAutoCleaningOfString ) {
				
				//  if invalid, do XML character cleaning

				value = CleanInvalidCharactersFromString.cleanInvalid_XML_CharactersFromString( value, replacementString );

			} else if ( doASCIIAutoCleaningOfString ) {

				//  if invalid, do XML character cleaning, if still invalid, do non-ASCII cleaning
				
				value = CleanInvalidCharactersFromString.cleanInvalid_XML_Characters_AndNonASCII_FromString( value, replacementString );

			} else {
				
				//  Only test for invalid

				testMarshalThenUnMarshalObject.testMarshalThenUnMarshalSingleString( value );
			}

		} catch ( TestMarshalThenUnMarshalObjectException ex ) {

			throw new JobcenterModuleInterfaceInvalidCharactersInStringException( ex.getCause() );
		} catch ( Exception e ) {

			throw new JobcenterSystemErrorException( e );
		}


		runOutputParams.put( key, value );
	}


	
	
	
	public int getStatusCode() {
		return statusCode;
	}




	public List<RunMessageDTO> getRunMessages() {
		return runMessages;
	}


	public void setRunMessages(List<RunMessageDTO> runMessages) {
		this.runMessages = runMessages;
	}


	public Map<String, String> getRunOutputParams() {
		return runOutputParams;
	}


	public void setRunOutputParams(Map<String, String> runOutputParams) {
		this.runOutputParams = runOutputParams;
	}




}
