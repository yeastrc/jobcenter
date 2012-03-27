package org.jobcenter.jdbc;

import java.util.List;

import org.jobcenter.request.ListRequestsRequest;

public interface RequestJDBCDAO {

	/**
	 * @param listRequestsRequest
	 * @return
	 * @throws Exception
	 */
	public abstract List<Integer> getRequestIdList(
			ListRequestsRequest listRequestsRequest);

}