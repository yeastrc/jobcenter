package org.jobcenter.service;

import java.util.List;

import org.jobcenter.dto.NodeClientStatusDTO;
import org.jobcenter.request.ListClientsStatusRequest;
import org.jobcenter.response.ListClientsStatusResponse;

public interface GetClientsStatusListService {

	/**
	 * @param listJobsRequest
	 * @param remoteHost
	 * @return
	 */
	public abstract ListClientsStatusResponse retrieveClientsStatusList(
			ListClientsStatusRequest listClientsStatusRequest, String remoteHost);

	/**
	 * @return
	 */
	public  List<NodeClientStatusDTO>  retrieveClientsLateForCheckinList();
}