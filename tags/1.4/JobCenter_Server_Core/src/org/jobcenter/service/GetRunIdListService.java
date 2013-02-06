package org.jobcenter.service;

import org.jobcenter.request.GetRunIdListRequest;
import org.jobcenter.response.GetRunIdListResponse;

public interface GetRunIdListService {

	public abstract GetRunIdListResponse getRunIdListFromJob(
			GetRunIdListRequest getRunIdListRequest, String remoteHost);

}