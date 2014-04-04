package org.jobcenter.request;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlRootElement(name = "listRequestsRequest")

public class ListRequestsRequest extends BaseRequest {

	private String requestTypeName;

	private Integer requestId;
	private String jobTypeName;
	private String submitter;
	private Set<Integer> statusIds;

	private Integer indexStart;         //  set to null for starting at first record
	private Integer jobsReturnCountMax; // set to null to get defined max

	
	
	

	
	public String getRequestTypeName() {
		return requestTypeName;
	}
	public void setRequestTypeName(String requestTypeName) {
		this.requestTypeName = requestTypeName;
	}
	public Integer getRequestId() {
		return requestId;
	}
	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}
	public String getJobTypeName() {
		return jobTypeName;
	}
	public void setJobTypeName(String jobTypeName) {
		this.jobTypeName = jobTypeName;
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
