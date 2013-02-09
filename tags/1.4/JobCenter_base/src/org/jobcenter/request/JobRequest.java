package org.jobcenter.request;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;





@XmlRootElement(name = "jobRequest")

public class JobRequest extends BaseRequest {

	private List<JobRequestModuleInfo> clientModules;


	public List<JobRequestModuleInfo> getClientModules() {
		return clientModules;
	}

	public void setClientModules(List<JobRequestModuleInfo> clientModules) {
		this.clientModules = clientModules;
	}


}
