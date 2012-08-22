package org.jobcenter.internalservice;

import java.util.List;

import org.jobcenter.dto.NodeClientStatusDTO;

public interface ClientsLateCheckinRetrieveAndSendEmailService {

	/**
	 * @return - list of clients that are late to be updated in the DB
	 */
	public abstract List<NodeClientStatusDTO> sendClientsCheckinLateNotification();

}