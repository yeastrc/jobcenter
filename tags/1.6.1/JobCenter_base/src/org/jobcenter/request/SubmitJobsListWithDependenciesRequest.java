package org.jobcenter.request;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.nondbdto.SubmittedJobAndDependencies;

@XmlRootElement(name = "submitJobsListWithDependenciesRequest")

public class SubmitJobsListWithDependenciesRequest extends BaseRequest {

	private String requestTypeName;
	private Integer requestId;
	private String submitter;

	private List<SubmittedJobAndDependencies> submittedJobAndDependenciesList;


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
	public String getSubmitter() {
		return submitter;
	}
	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}
	public List<SubmittedJobAndDependencies> getSubmittedJobAndDependenciesList() {
		return submittedJobAndDependenciesList;
	}
	public void setSubmittedJobAndDependenciesList(
			List<SubmittedJobAndDependencies> submittedJobAndDependenciesList) {
		this.submittedJobAndDependenciesList = submittedJobAndDependenciesList;
	}
}
