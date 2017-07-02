package org.jobcenter.guiclient.response;

import java.util.List;

import org.jobcenter.dto.Job;

/**
 * GUI response to list jobs call
 *
 */
public class GUIListJobsResponse {

	private List<Job> jobs;
	
	private int jobCount;
	

	public List<Job> getJobs() {
		return jobs;
	}
	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}
	public int getJobCount() {
		return jobCount;
	}
	public void setJobCount(int jobCount) {
		this.jobCount = jobCount;
	}
}
