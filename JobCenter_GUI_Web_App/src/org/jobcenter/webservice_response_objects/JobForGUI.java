package org.jobcenter.webservice_response_objects;

import java.util.List;

/**
 * Sent to JS code on browser
 *
 */
public class JobForGUI {

	private int jobId;
	private String jobTypeName;
	private int requestId;
	private String submitDate;
	private String submitter;
	private int priority;
	private Integer requiredExecutionThreads;
	private String status;
	private String statusColor;
	private Integer dbRecordVersionNumber;
	
	private List<JobParameterForGUI> jobParameterList;
	
	private List<RunForGUI> runForGUIList;
	
	private boolean requeueable;
	private boolean cancellable;
	
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	public String getJobTypeName() {
		return jobTypeName;
	}
	public void setJobTypeName(String jobTypeName) {
		this.jobTypeName = jobTypeName;
	}
	public int getRequestId() {
		return requestId;
	}
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	public String getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(String submitDate) {
		this.submitDate = submitDate;
	}
	public String getSubmitter() {
		return submitter;
	}
	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public Integer getRequiredExecutionThreads() {
		return requiredExecutionThreads;
	}
	public void setRequiredExecutionThreads(Integer requiredExecutionThreads) {
		this.requiredExecutionThreads = requiredExecutionThreads;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusColor() {
		return statusColor;
	}
	public void setStatusColor(String statusColor) {
		this.statusColor = statusColor;
	}
	public Integer getDbRecordVersionNumber() {
		return dbRecordVersionNumber;
	}
	public void setDbRecordVersionNumber(Integer dbRecordVersionNumber) {
		this.dbRecordVersionNumber = dbRecordVersionNumber;
	}
	public List<JobParameterForGUI> getJobParameterList() {
		return jobParameterList;
	}
	public void setJobParameterList(List<JobParameterForGUI> jobParameterList) {
		this.jobParameterList = jobParameterList;
	}
	public List<RunForGUI> getRunForGUIList() {
		return runForGUIList;
	}
	public void setRunForGUIList(List<RunForGUI> runForGUIList) {
		this.runForGUIList = runForGUIList;
	}
	public boolean isRequeueable() {
		return requeueable;
	}
	public void setRequeueable(boolean requeueable) {
		this.requeueable = requeueable;
	}
	public boolean isCancellable() {
		return cancellable;
	}
	public void setCancellable(boolean cancellable) {
		this.cancellable = cancellable;
	}
}
