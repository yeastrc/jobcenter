package org.jobcenter.constants;

/**
 * 
 *
 */
public class WaitTimesForLoggingAndInterruptConstants {

	public static final int CLIENT_WAIT_TO_INTERRUPT_GET_NEXT_JOB_THREAD = 2 * 60 * 1000; // 1 minute
	
	/**
	 * Half of client wait to interrupt get next job thread
	 */
	public static final int SERVER_LOG_GET_NEXT_JOB_EXCEEDED = CLIENT_WAIT_TO_INTERRUPT_GET_NEXT_JOB_THREAD / 2; 
}
