package org.jobcenter.request;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.dto.Job;



@XmlRootElement(name = "updateJobStatusRequest")

public class UpdateServerFromJobRunOnClientRequest extends BaseRequest {

	public static final boolean IGNORE_JOB_DB_RECORD_VERSION_NUMBER_TRUE = true;
	public static final boolean IGNORE_JOB_DB_RECORD_VERSION_NUMBER_FALSE = false;
	
	private Job job;
	
	private boolean ignoreJobdbRecordVersionNumber = false;
	
	public boolean isIgnoreJobdbRecordVersionNumber() {
		return ignoreJobdbRecordVersionNumber;
	}
	public void setIgnoreJobdbRecordVersionNumber(
			boolean ignoreJobdbRecordVersionNumber) {
		this.ignoreJobdbRecordVersionNumber = ignoreJobdbRecordVersionNumber;
	}
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
}
