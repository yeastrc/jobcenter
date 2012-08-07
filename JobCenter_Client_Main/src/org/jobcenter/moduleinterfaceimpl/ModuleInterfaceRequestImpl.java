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


	//  get methods visible to the module through interface ModuleInterfaceRequest

	@Override
	public int getRequestId() {

		return requestId;
	}

	@Override
	public Map<String, String> getJobParameters() {

		return jobParameters;
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

}
