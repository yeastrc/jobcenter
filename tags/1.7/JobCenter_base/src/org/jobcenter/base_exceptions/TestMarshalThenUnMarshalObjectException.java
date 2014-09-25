package org.jobcenter.base_exceptions;

/**
 * Thrown by TestMarshalThenUnMarshalObject when the marshal or unmarshal fail
 *
 */
public class TestMarshalThenUnMarshalObjectException extends Exception {

	// constructors

	/**
	 * default constructor
	 */
	public TestMarshalThenUnMarshalObjectException( ) {
		super( );
	}


	public TestMarshalThenUnMarshalObjectException( Throwable ex) {
		super( ex );
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
