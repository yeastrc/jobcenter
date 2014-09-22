package org.jobcenter.dto;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jobcenter.constants.JobConstants;
import org.jobcenter.util.JobUtilities;



/**
 * DTO for job table
 *
 */

@XmlRootElement(name = "job")
@XmlAccessorType(XmlAccessType.FIELD)
public class Job {

	private int id;

	private int jobTypeId;

	private int requestId;

	/**
	 * associated jobType table record, populated by Hibernate
	 */
	private JobType jobType;

	private Date submitDate;

	private String submitter;

	private int priority;
	
	private Integer requiredExecutionThreads;

	private int statusId;

	private String insertComplete = "T";
	
	private int jobParameterCount = JobConstants.JOB_PARAMETER_COUNT_NOT_SET;
	
	private Date delayJobUntil;
	private int paramErrorRetryCount;
	private int softErrorRetryCount;
	

	/**
	 * Current database record version number, for optimistic locking version tracking
	 */
	private Integer dbRecordVersionNumber;

	//	populated by Hibernate
	private StatusDTO status;



	//  retrieved from other tables

	private int currentRunId;


	//   Populated for getJob ( next job to run returned to JobCenter_Client
	private RunDTO currentRun;

	//   Populated for listJob
	private List<RunDTO> allRuns;

	private RequestTypeDTO requestTypeDTO;


	private Map<String, String> jobParameters;
	
	@XmlTransient //  Do not send to client
	private List<Integer> jobDependencies;
	
	


	private int jobParameterCountWhenRetrievedByGetJob = -1;

	private int jobParameterCountComputedInClient = -1;

	
	

	public static class ReverseSortByIdComparator implements Comparator<Job> {

		@Override
		public int compare(Job o1, Job o2) {

			if ( o1 == null || o2 == null ) {
				return 0;
			}

			// "-" in front since reverse
			return  -  ( o1.getId() - o2.getId() );
		}
	}




	/**
	 * Can this job be requeued
	 * @return
	 */
	public boolean isRequeueable() {

		return JobUtilities.isJobRequeueable( this );
	}


	/**
	 * Can this job be canceled
	 * @return
	 */
	public boolean isCancellable() {

		return JobUtilities.isJobCancellable( this );
	}




	/**
	 * Current database record version number, for optimistic locking version tracking
	 *
	 * @return
	 */
	public Integer getDbRecordVersionNumber() {
		return dbRecordVersionNumber;
	}

	/**
	 * Current database record version number, for optimistic locking version tracking
	 *
	 * @param dbRecordVersionNumber
	 */
	public void setDbRecordVersionNumber(Integer dbRecordVersionNumber) {
		this.dbRecordVersionNumber = dbRecordVersionNumber;
	}


	public void setJobType(JobType jobType) {
		this.jobType = jobType;

		if ( jobType != null ) {
			jobTypeId = jobType.getId();
		}
	}


	public int getJobTypeId() {

		int jobTypeId = this.jobTypeId;

		if ( jobType != null ) {

			jobTypeId = jobType.getId();
		}

		return jobTypeId;
	}


	public int getStatusId() {

		int statusId = this.statusId;

		if ( status != null ) {

			statusId = status.getId();
		}

		return statusId;
	}


	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}






	public StatusDTO getStatus() {
		return status;
	}

	public void setStatus(StatusDTO status) {
		this.status = status;

		if ( status != null ) {
			statusId = status.getId();
		}
	}





	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public void setJobTypeId(int jobTypeId) {
		this.jobTypeId = jobTypeId;
	}


	public Date getSubmitDate() {

		return submitDate;
	}

	public void setSubmitDate(Date submitDate) {
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





	public Map<String, String> getJobParameters() {
		return jobParameters;
	}

	public void setJobParameters(Map<String, String> jobParameters) {
		this.jobParameters = jobParameters;
	}

	public JobType getJobType() {
		return jobType;
	}


	public int getCurrentRunId() {
		return currentRunId;
	}

	public void setCurrentRunId(int currentRunId) {
		this.currentRunId = currentRunId;
	}

	public RunDTO getCurrentRun() {
		return currentRun;
	}

	public void setCurrentRun(RunDTO currentRun) {
		this.currentRun = currentRun;
	}



	public int getRequestId() {
		return requestId;
	}



	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}



	public List<RunDTO> getAllRuns() {
		return allRuns;
	}



	public void setAllRuns(List<RunDTO> allRuns) {
		this.allRuns = allRuns;
	}



	public RequestTypeDTO getRequestTypeDTO() {
		return requestTypeDTO;
	}



	public void setRequestTypeDTO(RequestTypeDTO requestTypeDTO) {
		this.requestTypeDTO = requestTypeDTO;
	}


	public String getInsertComplete() {
		return insertComplete;
	}


	public void setInsertComplete(String insertComplete) {
		this.insertComplete = insertComplete;
	}

	public int getJobParameterCount() {
		return jobParameterCount;
	}


	public void setJobParameterCount(int jobParameterCount) {
		this.jobParameterCount = jobParameterCount;
	}


	public int getJobParameterCountWhenRetrievedByGetJob() {
		return jobParameterCountWhenRetrievedByGetJob;
	}


	public void setJobParameterCountWhenRetrievedByGetJob(
			int jobParameterCountWhenRetrievedByGetJob) {
		this.jobParameterCountWhenRetrievedByGetJob = jobParameterCountWhenRetrievedByGetJob;
	}


	public int getJobParameterCountComputedInClient() {
		return jobParameterCountComputedInClient;
	}


	public void setJobParameterCountComputedInClient(
			int jobParameterCountComputedInClient) {
		this.jobParameterCountComputedInClient = jobParameterCountComputedInClient;
	}


	public Date getDelayJobUntil() {
		return delayJobUntil;
	}


	public void setDelayJobUntil(Date delayJobUntil) {
		this.delayJobUntil = delayJobUntil;
	}


	public int getParamErrorRetryCount() {
		return paramErrorRetryCount;
	}


	public void setParamErrorRetryCount(int paramErrorRetryCount) {
		this.paramErrorRetryCount = paramErrorRetryCount;
	}


	public int getSoftErrorRetryCount() {
		return softErrorRetryCount;
	}


	public void setSoftErrorRetryCount(int softErrorRetryCount) {
		this.softErrorRetryCount = softErrorRetryCount;
	}
	

	public List<Integer> getJobDependencies() {
		return jobDependencies;
	}


	public void setJobDependencies(List<Integer> jobDependencies) {
		this.jobDependencies = jobDependencies;
	}

}
