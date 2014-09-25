package org.jobcenter.request;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "getRunIdListRequest")

public class GetRunIdListRequest extends BaseRequest {

	
	private int jobId;

	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

}
