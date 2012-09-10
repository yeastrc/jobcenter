package org.jobcenter.request;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.dto.Job;



@XmlRootElement(name = "cancelJobRequest")

public class CancelJobRequest extends BaseRequest {

	Job job;
	
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
}
