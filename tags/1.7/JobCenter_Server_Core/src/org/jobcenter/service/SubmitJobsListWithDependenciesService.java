package org.jobcenter.service;

import org.jobcenter.request.SubmitJobsListWithDependenciesRequest;
import org.jobcenter.response.SubmitJobsListWithDependenciesResponse;

public interface SubmitJobsListWithDependenciesService {

	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 */
	public abstract SubmitJobsListWithDependenciesResponse submitJobsListWithDependencies(
			SubmitJobsListWithDependenciesRequest submitJobsListWithDependenciesRequest,
			String remoteHost);

}