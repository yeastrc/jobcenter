package org.jobcenter.exceptions;

/**
 * Jobcenter has encountered an internal system error.
 *
 */
public class JobcenterSystemErrorException extends Exception {

	// constructors

	/**
	 * default constructor
	 */
	public JobcenterSystemErrorException( ) {
		super( );
	}


	public JobcenterSystemErrorException( Throwable ex) {
		super( ex );
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
