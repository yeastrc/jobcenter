package org.jobcenter.request;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;





@XmlRootElement(name = "clientStartupRequest")

public class ClientStartupRequest extends BaseRequest {

	//  nodeName is in BaseRequest


	private List<JobRequestModuleInfo> clientModules;
	
	private int numberThreadsClientConfig;
	private int numberJobsClientConfig;


	public int getNumberThreadsClientConfig() {
		return numberThreadsClientConfig;
	}

	public void setNumberThreadsClientConfig(int numberThreadsClientConfig) {
		this.numberThreadsClientConfig = numberThreadsClientConfig;
	}

	public int getNumberJobsClientConfig() {
		return numberJobsClientConfig;
	}

	public void setNumberJobsClientConfig(int numberJobsClientConfig) {
		this.numberJobsClientConfig = numberJobsClientConfig;
	}

	public List<JobRequestModuleInfo> getClientModules() {
		return clientModules;
	}

	public void setClientModules(List<JobRequestModuleInfo> clientModules) {
		this.clientModules = clientModules;
	}


}
