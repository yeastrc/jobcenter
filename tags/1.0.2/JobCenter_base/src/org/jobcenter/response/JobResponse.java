package org.jobcenter.response;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.dto.Job;

/**
 * The response to a get job request
 *
 */
@XmlRootElement(name = "jobResponse")

public class JobResponse extends BaseResponse {
	
	private boolean jobFound;
	
	private Job job;
	
	public boolean isJobFound() {
		return jobFound;
	}

	public void setJobFound(boolean jobFound) {
		this.jobFound = jobFound;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

}
