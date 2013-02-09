package org.jobcenter.service;

import org.jobcenter.request.JobChangePriorityRequest;
import org.jobcenter.response.JobChangePriorityResponse;

public interface JobChangePriorityService {

	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 */
	//	@Override
	public abstract JobChangePriorityResponse jobChangePriority(
			JobChangePriorityRequest jobChangePriorityRequest, String remoteHost);

}