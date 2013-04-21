package org.jobcenter.internalservice;

import org.jobcenter.dtoservernondb.MailConfig;

public interface GetMailConfig {

	/**
	 * @return
	 */
	public abstract MailConfig getClientCheckinMailConfig();

	public abstract MailConfig getClientStatusMailConfig();
}