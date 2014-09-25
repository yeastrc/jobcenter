package org.jobcenter.request;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "getRunRequest")

public class GetRunRequest extends BaseRequest {

	public static enum GetRunRequestType {
		
		GET_FIRST_RUN,
		GET_PREVIOUS_RUN,
		GET_RUN_FROM_INDEX
		
	}
	
	private GetRunRequestType getRunRequestType;
	
	private int jobId;
	
	private Integer currentRunId;
	
	/**
	 * index is zero based
	 */
	private int runIndex;


	public GetRunRequestType getGetRunRequestType() {
		return getRunRequestType;
	}
	public void setGetRunRequestType(GetRunRequestType getRunRequestType) {
		this.getRunRequestType = getRunRequestType;
	}
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public Integer getCurrentRunId() {
		return currentRunId;
	}
	public void setCurrentRunId(Integer currentRunId) {
		this.currentRunId = currentRunId;
	}
	public int getRunIndex() {
		return runIndex;
	}
	public void setRunIndex(int runIndex) {
		this.runIndex = runIndex;
	}
}
