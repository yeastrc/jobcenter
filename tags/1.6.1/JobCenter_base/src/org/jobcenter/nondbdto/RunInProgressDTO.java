package org.jobcenter.nondbdto;

/**
 * Information on a job run in progress
 *
 */
public class RunInProgressDTO {

	private Integer jobId;
	private Integer runId;

	private Long lastStatusTimeStampFromModule;
	private Integer percentageComplete;


	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	public Integer getRunId() {
		return runId;
	}
	public void setRunId(Integer runId) {
		this.runId = runId;
	}
	public Integer getPercentageComplete() {
		return percentageComplete;
	}
	public void setPercentageComplete(Integer percentageComplete) {
		this.percentageComplete = percentageComplete;
	}
	public Long getLastStatusTimeStampFromModule() {
		return lastStatusTimeStampFromModule;
	}
	public void setLastStatusTimeStampFromModule(Long lastStatusTimeStampFromModule) {
		this.lastStatusTimeStampFromModule = lastStatusTimeStampFromModule;
	}
}
