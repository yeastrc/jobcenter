package org.jobcenter.constants;

/**
 * These are the valid values to be returned in the method getStatus() in the
 * interface org.jobcenter.job_module_interface.ModuleInterfaceClientMainInterface
 *
 * These values must be kept in sync with the values in the table "status"
 *
 * These values must be kept in sync with the values in the interface JobStatusValuesConstantsCopy in the project JobCenter_base
 */
public interface JobStatusValuesConstants {

	public final static int JOB_STATUS_NOT_SET    = 0;

	public final static int JOB_STATUS_SUBMITTED  = 1;
	public final static int JOB_STATUS_RUNNING    = 2;
	public final static int JOB_STATUS_STALLED    = 3;
	public final static int JOB_STATUS_FINISHED   = 4;
	public final static int JOB_STATUS_FINISHED_WITH_WARNINGS = 5;
	public final static int JOB_STATUS_HARD_ERROR = 6;  // Don't retry
	public final static int JOB_STATUS_SOFT_ERROR = 7;  // Can retry
	public final static int JOB_STATUS_CANCELED   = 8;
	public final static int JOB_STATUS_REQUEUED   = 9;  //  Job has been requeued

	//  TODO   Need status for failed to load module.  Will be HARD_ERROR for now.
}
