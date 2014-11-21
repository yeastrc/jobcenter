package org.jobcenter.service;

import org.jobcenter.request.GetClientsConnectedListRequest;
import org.jobcenter.response.GetClientsConnectedListResponse;

public interface GetClientsFailedToConnectTrackingService {

	/* 
	 * Return list of clients where same node name is active on two different clients
	 */
	/* (non-Javadoc)
	 * @see org.jobcenter.service.GetClientsUsingSameNodeNameService#retrieveClientsConnectedList(org.jobcenter.request.GetClientsConnectedListRequest, java.lang.String)
	 */
	public abstract GetClientsConnectedListResponse retrieveClientsFailedToConnectTrackingList(
			GetClientsConnectedListRequest getClientsConnectedListRequest,
			String remoteHost);

}