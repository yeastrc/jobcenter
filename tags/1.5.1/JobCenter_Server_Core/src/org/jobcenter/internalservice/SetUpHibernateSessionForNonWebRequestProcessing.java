package org.jobcenter.internalservice;

public interface SetUpHibernateSessionForNonWebRequestProcessing {

	/**
	 * Create a Hibernate session on the ThreadLocal
	 *
	 */
	public abstract void openSession();

	/**
	 * Close the Hibernate session on the ThreadLocal
	 *
	 */
	public abstract void closeSession();

}