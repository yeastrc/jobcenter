package org.jobcenter.internalservice;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * This class contains code to set up a Hibernate session so that code that does not
 * run as part of a web request can access the database.
 * It opens a Hibernate session and puts it on ThreadLocal so that it is available
 * to all the database access DAO code.
 *
 * Code that runs as part of a web request gets it's
 * Hibernate session set up and put on the ThreadLocal
 * by a Servlet filter in the Spring jar called "OpenSessionInViewFilter".
 *
 *
 *
 * It appears that this is not needed as long as a class is called that is marked "@Transactional".
 *
 */
public class SetUpHibernateSessionForNonWebRequestProcessingImpl implements SetUpHibernateSessionForNonWebRequestProcessing {

	private static Logger log = Logger.getLogger(SetUpHibernateSessionForNonWebRequestProcessingImpl.class);



//	public static GetValueFromConfigService getFromApplicationContext(ApplicationContext ctx) {
//		return (GetValueFromConfigService) ctx.getBean("getValueFromConfigService");
//	}


	private SessionFactory sessionFactory;

	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.SetUpHibernateSessionForNonWebRequestProcessing#openSession()
	 */
	public void openSession() {

		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.SetUpHibernateSessionForNonWebRequestProcessing#closeSession()
	 */
	public void closeSession( ) {

		if ( sessionFactory != null ) {

			SessionHolder sessionHolder =
				(SessionHolder) TransactionSynchronizationManager.unbindResource( sessionFactory );

			SessionFactoryUtils.closeSession( sessionHolder.getSession() );
		}
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
