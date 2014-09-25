package org.jobcenter.util;

import org.jobcenter.base_exceptions.TestMarshalThenUnMarshalObjectException;
import org.jobcenter.test_marshal_then_unmarshal_object.TestMarshalThenUnMarshalObject;

/**
 * 
 *
 */
public class CleanInvalidCharactersFromString {

	
	/**
	 * @param value
	 * @param replacementString
	 * @return
	 * @throws TestMarshalThenUnMarshalObjectException
	 * @throws Exception
	 */
	public static String cleanInvalid_XML_CharactersFromString( String value, String replacementString ) 
			throws TestMarshalThenUnMarshalObjectException, Exception {
		
		return cleanInvalidCharactersFromString( value, replacementString, false /* doASCIIAutoCleaningOfString */ );
	}
	
	/**
	 * @param value
	 * @param replacementString
	 * @return
	 * @throws TestMarshalThenUnMarshalObjectException
	 * @throws Exception
	 */
	public static String cleanInvalid_XML_Characters_AndNonASCII_FromString( String value, String replacementString ) 
			throws TestMarshalThenUnMarshalObjectException, Exception {
		
		return cleanInvalidCharactersFromString( value, replacementString, true /* doASCIIAutoCleaningOfString */ );
	}
	
	
	/**
	 * @param value
	 * @param replacementString
	 * @param doASCIIAutoCleaningOfString
	 * @return
	 * @throws TestMarshalThenUnMarshalObjectException
	 * @throws Exception
	 */
	private static String cleanInvalidCharactersFromString( String value, String replacementString, boolean doASCIIAutoCleaningOfString )
			throws TestMarshalThenUnMarshalObjectException, Exception {
		
		

		TestMarshalThenUnMarshalObject testMarshalThenUnMarshalObject = TestMarshalThenUnMarshalObject.getInstance();


		try {
			testMarshalThenUnMarshalObject.testMarshalThenUnMarshalSingleString( value );

		} catch ( TestMarshalThenUnMarshalObjectException ex ) {

			value = ReplaceInvalidCharactersInXMLUTF8InString.replaceInvalidCharactersInXMLUTF8InString( value, replacementString );

			try {
				testMarshalThenUnMarshalObject.testMarshalThenUnMarshalSingleString( value );

			} catch ( TestMarshalThenUnMarshalObjectException ex2 ) {

				if ( doASCIIAutoCleaningOfString ) {

						value = ReplaceInvalidCharactersInXMLUTF8InString.replaceNonAsciiCharactersInString( value, replacementString );

						testMarshalThenUnMarshalObject.testMarshalThenUnMarshalSingleString( value );
				} else {

					throw ex2;
				}
			}
		}
		
		return value;
	}
}
