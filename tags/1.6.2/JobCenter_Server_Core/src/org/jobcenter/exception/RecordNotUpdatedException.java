package org.jobcenter.exception;

public class RecordNotUpdatedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 882427522840327768L;

	public RecordNotUpdatedException() {
		
	}
	
	public RecordNotUpdatedException( String s ) {
		super(s);
	}
}
