package org.jobcenter.service;

import org.jobcenter.request.GetClientsConnectedListRequest;
import org.jobcenter.response.GetClientsConnectedListResponse;

public interface GetClientsConnectedListService {

	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 */
	public abstract GetClientsConnectedListResponse retrieveClientsConnectedList(
			GetClientsConnectedListRequest getClientsConnectedListRequest,
			String remoteHost);

}