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

	@Override
	public int getRequestId() {

		return requestId;
	}

	@Override
	public Map<String, String> getJobParameters() {

		return jobParameters;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public void setJobParameters(Map<String, String> jobParameters) {
		this.jobParameters = jobParameters;
	}

}
