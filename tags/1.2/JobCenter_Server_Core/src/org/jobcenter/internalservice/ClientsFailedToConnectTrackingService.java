package org.jobcenter.internalservice;

import java.util.List;

import org.jobcenter.nondbdto.ClientConnectedDTO;

public interface ClientsFailedToConnectTrackingService {

	/**
	 * @return
	 */
	public abstract List<ClientConnectedDTO> retrieveClientsFailedToConnectList();

	/**
	 * @param clientConnectedDTO
	 */
	public abstract void addFailedNodeName(String nodeName,
			String remoteIPAddress);

}