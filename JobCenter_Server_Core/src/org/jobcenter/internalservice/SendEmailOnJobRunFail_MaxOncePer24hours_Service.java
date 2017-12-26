package org.jobcenter.internalservice;

import org.jobcenter.dto.JobType;

/**
 * Send an email on Job Run Fail.  Only One email per 24 hours max
 *
 */
public interface SendEmailOnJobRunFail_MaxOncePer24hours_Service {

	public  void sendEmailOnJobRunFailed( JobType jobTypeOfFailedJob );
}
