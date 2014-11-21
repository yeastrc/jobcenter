package org.jobcenter.service;

import org.jobcenter.request.ListRequestsRequest;
import org.jobcenter.response.ListRequestsResponse;

/**
 * 
 *
 */
public interface GetRequestListService {

	/**
	 * @param listRequestsRequest
	 * @param remoteHost
	 * @return
	 */
	public abstract ListRequestsResponse retrieveRequestsList( ListRequestsRequest listRequestsRequest, String remoteHost );

}