package org.jobcenter.util;

import org.jobcenter.constants.JobStatusValuesConstantsCopy;
import org.jobcenter.dto.Job;

public class JobUtilities {

	public static boolean isJobRequeueable( Job job ) {
		
//		System.out.println( "JobUtilities::isJobRequeueable, job status = " + job.getStatusId() + ", job id = " + job.getId() );
		
		if ( job.getStatusId() == JobStatusValuesConstantsCopy.JOB_STATUS_CANCELED
				|| job.getStatusId() == JobStatusValuesConstantsCopy.JOB_STATUS_HARD_ERROR ) {
			
			return true;
		}
		
		return false;
	}
	
	public static boolean isJobCancellable( Job job ) {
		
//		System.out.println( "JobUtilities::isJobCancellable, job status = " + job.getStatusId() + ", job id = " + job.getId() );
		
		if ( job.getStatusId() == JobStatusValuesConstantsCopy.JOB_STATUS_SUBMITTED
				|| job.getStatusId() == JobStatusValuesConstantsCopy.JOB_STATUS_HARD_ERROR 
				|| job.getStatusId() == JobStatusValuesConstantsCopy.JOB_STATUS_REQUEUED
				) {
			
			return true;
		}
		
		return false;
	}

}

