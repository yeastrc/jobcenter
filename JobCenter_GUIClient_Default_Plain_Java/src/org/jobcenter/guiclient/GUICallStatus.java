package org.jobcenter.guiclient;

public enum GUICallStatus {

	    SUCCESS,
	    FAILED_VERSION_NOT_MATCH_DB,
	    FAILED_JOB_NOT_FOUND,
	    FAILED_JOB_NO_LONGER_REQUEUEABLE, //The job is no longer in a status where it is requeueuable.
	    FAILED_JOB_NO_LONGER_CANCELABLE
}
