package org.jobcenter.request;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "viewJobRequest")

public class ViewJobRequest extends BaseRequest {

	private int jobId;

	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

}
