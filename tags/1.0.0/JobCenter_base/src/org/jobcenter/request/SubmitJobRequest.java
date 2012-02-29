package org.jobcenter.request;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "submitJobRequest")

public class SubmitJobRequest extends BaseRequest {

	private String requestTypeName;
	private Integer requestId;
	private String jobTypeName;
	private String submitter;
	private Integer priority;
	private Map<String, String> jobParameters;


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
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Map<String, String> getJobParameters() {
		return jobParameters;
	}
	public void setJobParameters(Map<String, String> jobParameters) {
		this.jobParameters = jobParameters;
	}
}
