package org.jobcenter.internalservice;

import org.jobcenter.dto.Job;

public interface SaveJobSentToClientService {

	/**
	 * @param job
	 */
	public abstract void saveJobSentToClient(Job job);

}