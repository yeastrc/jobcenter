package org.jobcenter.service;

import org.jobcenter.request.GetClientsConnectedListRequest;
import org.jobcenter.response.GetClientsConnectedListResponse;

public interface GetClientsUsingSameNodeNameService {

	/* 
	 * Return list of clients where same node name is active on two different clients
	 */
	public abstract GetClientsConnectedListResponse retrieveClientsUsingSameNodeNameList(
			GetClientsConnectedListRequest getClientsConnectedListRequest,
			String remoteHost);

}