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
	 * required_execution_threads in job table
	 */
	private Integer jobRequiredExecutionThreads;
	
	/**
	 * 
	 */
	private String jobcenterClientNodeName;


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
	 * @see org.jobcenter.job_client_module_interface.ModuleInterfaceRequest#getJobRequiredExecutionThreads()
	 * 
	 * the value from required_execution_threads field in job table
	 */
	@Override
	public Integer getJobRequiredExecutionThreads() {
		return jobRequiredExecutionThreads;
	}


	@Override
	public int getNumberOfThreadsForRunningJob() {

		return numberOfThreadsForRunningJob;
	}


	@Override
	public String getJobcenterClientNodeName() {

		return jobcenterClientNodeName;
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

	
	public void setJobRequiredExecutionThreads(
			Integer jobRequiredExecutionThreads) {
		this.jobRequiredExecutionThreads = jobRequiredExecutionThreads;
	}
	

	public void setJobcenterClientNodeName(String jobcenterClientNodeName) {
		this.jobcenterClientNodeName = jobcenterClientNodeName;
	}


}
