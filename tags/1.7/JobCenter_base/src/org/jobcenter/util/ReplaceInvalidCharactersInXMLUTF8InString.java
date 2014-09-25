package org.jobcenter.util;

import org.jobcenter.constants.InvalidCharactersInXMLUTF8Constants;



/**
 * 
 *
 */
public class ReplaceInvalidCharactersInXMLUTF8InString {
	

	private static final char MAX_ASCII_VALUE = '\u007F';
	

	/**
	 * Replace characters < x20 that are invalid in XML UTF-8 with replacementString  
	 * 
	 * Regex in InvalidCharactersInXMLUTF8Constants.InvalidCharactersInXMLUTF8Regex
	 * 
	 * @param input
	 * @param replacementString
	 * @return
	 */
	public static String replaceInvalidCharactersInXMLUTF8InString( String input, String replacementString ) {
		
		if ( input == null ) {
			
			throw new IllegalArgumentException( "input cannot be null" );
		}
		if ( replacementString == null ) {
			
			throw new IllegalArgumentException( "replacementString cannot be null" );
		}
		
		String output = input.replaceAll( InvalidCharactersInXMLUTF8Constants.InvalidCharactersInXMLUTF8Regex, replacementString );
		
		
		return output;
	}
	

	/**
	 * Replace characters >= x7F with replacementString  
	 * 
	 * @param input
	 * @param replacementString
	 * @return
	 */
	public static String replaceNonAsciiCharactersInString( String input, String replacementString ) {
		
		if ( input == null ) {
			
			throw new IllegalArgumentException( "input cannot be null" );
		}
		if ( replacementString == null ) {
			
			throw new IllegalArgumentException( "replacementString cannot be null" );
		}
		
		StringBuilder outputSB = new StringBuilder( input.length() + 10 );
		
		for ( int index = 0; index < input.length(); index++ ) {

			char inputChar = outputSB.charAt(index);
		
			if ( inputChar <= MAX_ASCII_VALUE ) {

				outputSB.append( inputChar );
			} else {
				
				outputSB.append( replacementString );
			}
		}
		
		String output = outputSB.toString();
		
		return output;
	}
}
