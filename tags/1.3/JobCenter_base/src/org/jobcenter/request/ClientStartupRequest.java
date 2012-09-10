package org.jobcenter.request;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;





@XmlRootElement(name = "clientStartupRequest")

public class ClientStartupRequest extends BaseRequest {

	//  nodeName is in BaseRequest


	private List<JobRequestModuleInfo> clientModules;


	public List<JobRequestModuleInfo> getClientModules() {
		return clientModules;
	}

	public void setClientModules(List<JobRequestModuleInfo> clientModules) {
		this.clientModules = clientModules;
	}


}
