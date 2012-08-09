package org.jobcenter.internalservice;

import org.jobcenter.dto.NodeClientStatusDTO;
import org.jobcenter.request.ClientStatusUpdateRequest;

public interface ClientStatusDBUpdateService {



	/**
	 * @param client
	 */
	void updateDB(NodeClientStatusDTO client);


	/**
	 * @param clientStatusUpdateRequest
	 * @param remoteHost
	 */
	public void updateDBWithStatusFromClient( ClientStatusUpdateRequest clientStatusUpdateRequest, String remoteHost );

}