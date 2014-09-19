package org.jobcenter.moduleinterfaceimpl;

import java.util.Map;

import org.jobcenter.job_client_module_interface.ModuleInterfaceRequest;

/**
 *
 *
 */
public class ModuleInterfaceRequestImpl implements ModuleInterfaceRequest {

	private int requestId;

	private Map<String, String> jobParameters;

	private int numberOfThreadsForRunningJob;
	
	/**
	 * required_execution_threads in job_type table
	 */
	private Integer jobTypeRequiredExecutionThreads;


	//  get methods visible to the module through interface ModuleInterfaceRequest

	@Override
	public int getRequestId() {

		return requestId;
	}

	@Override
	public Map<String, String> getJobParameters() {

		return jobParameters;
	}
	

	/* (non-Javadoc)
	 * @see org.jobcenter.job_client_module_interface.ModuleInterfaceRequest#getJobTypeRequiredExecutionThreads()
	 * 
	 * the value from required_execution_threads field in job_type table
	 */
	@Override
	public Integer getJobTypeRequiredExecutionThreads() {
		return jobTypeRequiredExecutionThreads;
	}


	@Override
	public int getNumberOfThreadsForRunningJob() {

		return numberOfThreadsForRunningJob;
	}



	//  setter methods not visible to the module

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public void setJobParameters(Map<String, String> jobParameters) {
		this.jobParameters = jobParameters;
	}

	public void setNumberOfThreadsForRunningJob(int numberOfThreadsForRunningJob) {
		this.numberOfThreadsForRunningJob = numberOfThreadsForRunningJob;
	}

	
	public void setJobTypeRequiredExecutionThreads(
			Integer jobTypeRequiredExecutionThreads) {
		this.jobTypeRequiredExecutionThreads = jobTypeRequiredExecutionThreads;
	}
}
