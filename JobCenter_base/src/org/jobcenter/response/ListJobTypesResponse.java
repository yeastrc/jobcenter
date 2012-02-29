package org.jobcenter.response;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.dto.*;

/**
 * The response to a list job types request
 *
 */
@XmlRootElement(name = "listJobTypesResponse")

public class ListJobTypesResponse extends BaseResponse {

	private List<JobType> jobTypes;

	public List<JobType> getJobTypes() {
		return jobTypes;
	}

	public void setJobTypes(List<JobType> jobTypes) {
		this.jobTypes = jobTypes;
	}


}
