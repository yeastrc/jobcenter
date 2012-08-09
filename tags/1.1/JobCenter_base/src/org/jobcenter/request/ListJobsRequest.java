package org.jobcenter.request;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlRootElement(name = "listJobsRequest")

public class ListJobsRequest extends BaseRequest {

	private Set<String> requestTypeNames;

	private Integer requestId;
	private Set<String> jobTypeNames;
	private String submitter;
	private Set<Integer> statusIds;

	private Integer indexStart;         //  set to null for starting at first record
	private Integer jobsReturnCountMax; // set to null to get defined max
	
	
	
	public Set<String> getRequestTypeNames() {
		return requestTypeNames;
	}
	public void setRequestTypeNames(Set<String> requestTypeNames) {
		this.requestTypeNames = requestTypeNames;
	}
	public Integer getRequestId() {
		return requestId;
	}
	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}
	public Set<String> getJobTypeNames() {
		return jobTypeNames;
	}
	public void setJobTypeNames(Set<String> jobTypeNames) {
		this.jobTypeNames = jobTypeNames;
	}
	public String getSubmitter() {
		return submitter;
	}
	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}
	public Set<Integer> getStatusIds() {
		return statusIds;
	}
	public void setStatusIds(Set<Integer> statusIds) {
		this.statusIds = statusIds;
	}
	public Integer getIndexStart() {
		return indexStart;
	}
	public void setIndexStart(Integer indexStart) {
		this.indexStart = indexStart;
	}
	public Integer getJobsReturnCountMax() {
		return jobsReturnCountMax;
	}
	public void setJobsReturnCountMax(Integer jobsReturnCountMax) {
		this.jobsReturnCountMax = jobsReturnCountMax;
	}

	
	
	

	
	
}
