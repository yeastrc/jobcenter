package org.jobcenter.internalservice;

import org.jobcenter.dto.NodeClientStatusDTO;
import org.jobcenter.dtoservernondb.NodeClientStatusDTOPrevCurrent;
import org.jobcenter.request.ClientStartupRequest;
import org.jobcenter.request.ClientStatusUpdateRequest;

public interface ClientStatusDBUpdateService {



	/**
	 * @param client
	 */
	void updateDB(NodeClientStatusDTO client);

	/**
	 * @param clientStartupRequest
	 * @param timeUntilNextClientStatusUpdate
	 * @param remoteHost
	 * @return TODO
	 */
	public NodeClientStatusDTOPrevCurrent updateDBWithStatusFromClientStartup( ClientStartupRequest clientStartupRequest, int timeUntilNextClientStatusUpdate, String remoteHost );

	/**
	 * @param clientStatusUpdateRequest
	 * @param remoteHost
	 * @return TODO
	 */
	public NodeClientStatusDTOPrevCurrent updateDBWithStatusFromClient( ClientStatusUpdateRequest clientStatusUpdateRequest, String remoteHost );

}