package org.jobcenter.response;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.dto.Job;

/**
 * The response to a list jobs request
 *
 */
@XmlRootElement(name = "listJobsResponse")

public class ListJobsResponse extends BaseResponse {

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
