package org.jobcenter.exceptions;

/**
 * Thrown for when there are invalid characters in a string passed to Jobcenter.
 * This could be any string that is in the call to Jobcenter
 * 
 * This is required since Jobcenter uses UTF-8 XML to pass data 
 * between the Jobcenter client and the Jobcenter server 
 *
 * Allowed characters (for values < x20) in XML UTF-8:   #x9 | #xA | #xD |
 * as referenced in http://www.w3.org/TR/xml/#charsets
 */
public class JobcenterModuleInterfaceInvalidCharactersInStringException extends Exception {

	// constructors

	/**
	 * default constructor
	 */
	public JobcenterModuleInterfaceInvalidCharactersInStringException( ) {
		super( );
	}


	public JobcenterModuleInterfaceInvalidCharactersInStringException( Throwable ex) {
		super( ex );
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
