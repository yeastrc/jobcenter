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
	
	
	/**
	 *  true if the next job found requires more threads to run than available on client 
	 */
	private boolean nextJobRequiresMoreThreads;
	

	public boolean isNextJobRequiresMoreThreads() {
		return nextJobRequiresMoreThreads;
	}

	public void setNextJobRequiresMoreThreads(boolean nextJobRequiresMoreThreads) {
		this.nextJobRequiresMoreThreads = nextJobRequiresMoreThreads;
	}

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
