package org.jobcenter.service;

import org.jobcenter.request.GetRunRequest;
import org.jobcenter.response.GetRunResponse;

public interface GetRunService {

	/**
	 * @param getRunRequest
	 * @param remoteHost
	 * @return
	 */
	public abstract GetRunResponse getRunFromJob(GetRunRequest getRunRequest,
			String remoteHost);

}