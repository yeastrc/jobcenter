package org.jobcenter.response;


import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.dto.Job;

/**
 * The response to a view job request
 *
 */
@XmlRootElement(name = "viewJobResponse")

public class ViewJobResponse extends BaseResponse {

	private Job job;

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}


}
