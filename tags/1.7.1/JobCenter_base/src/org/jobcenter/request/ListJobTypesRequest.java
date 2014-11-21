package org.jobcenter.request;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "listJobTypesRequest")

public class ListJobTypesRequest extends BaseRequest {

	private List<String> jobTypeNames;

	public List<String> getJobTypeNames() {
		return jobTypeNames;
	}

	public void setJobTypeNames(List<String> jobTypeNames) {
		this.jobTypeNames = jobTypeNames;
	}
}
