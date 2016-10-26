package org.jobcenter.internal_jdbc_dao_service;

import org.jobcenter.dto.Job;

public interface InsertJobInternalService {

	/**
	 * @param job
	 * @return
	 */
	public abstract int insertJob(Job job);
}
