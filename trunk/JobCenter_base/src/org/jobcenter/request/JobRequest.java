package org.jobcenter.request;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;





/**
 * Passed from Jobcenter client to Jobcenter server to request the next job for the client to run
 *
 */
@XmlRootElement(name = "jobRequest")

public class JobRequest extends BaseRequest {

	private List<JobRequestModuleInfo> clientModules;
	
	private int numberThreadsAvailableOnClient;

	private int numberJobsAvailableOnClient;

	
	private int totalNumberThreadsConfiguredOnClient;

	private int totalNumberJobsConfiguredOnClient;


	public int getTotalNumberThreadsConfiguredOnClient() {
		return totalNumberThreadsConfiguredOnClient;
	}

	public void setTotalNumberThreadsConfiguredOnClient(
			int totalNumberThreadsConfiguredOnClient) {
		this.totalNumberThreadsConfiguredOnClient = totalNumberThreadsConfiguredOnClient;
	}

	public int getTotalNumberJobsConfiguredOnClient() {
		return totalNumberJobsConfiguredOnClient;
	}

	public void setTotalNumberJobsConfiguredOnClient(
			int totalNumberJobsConfiguredOnClient) {
		this.totalNumberJobsConfiguredOnClient = totalNumberJobsConfiguredOnClient;
	}

	public int getNumberThreadsAvailableOnClient() {
		return numberThreadsAvailableOnClient;
	}

	public void setNumberThreadsAvailableOnClient(int numberThreadsAvailableOnClient) {
		this.numberThreadsAvailableOnClient = numberThreadsAvailableOnClient;
	}

	public int getNumberJobsAvailableOnClient() {
		return numberJobsAvailableOnClient;
	}

	public void setNumberJobsAvailableOnClient(int numberJobsAvailableOnClient) {
		this.numberJobsAvailableOnClient = numberJobsAvailableOnClient;
	}

	public List<JobRequestModuleInfo> getClientModules() {
		return clientModules;
	}

	public void setClientModules(List<JobRequestModuleInfo> clientModules) {
		this.clientModules = clientModules;
	}


}
