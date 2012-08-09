package org.jobcenter.listener;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jobcenter.constants.ServerConfigKeyValues;
import org.jobcenter.constants.ServerJerseyConstants;
import org.jobcenter.internalservice.GetValueFromConfigService;
import org.jobcenter.internalservice.GetValueFromConfigServiceImpl;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This class is loaded and the method "contextInitialized" is called when the web application is first loaded by the container
 *
 */
public class ServletContextAppListener extends HttpServlet implements
		ServletContextListener {

	private static final long serialVersionUID = 8422034822219270825L;


	private static Logger log = Logger.getLogger(ServletContextAppListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent sce) {


	}

	@Override
	public void contextInitialized(ServletContextEvent event) {


		log.info( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  Startup of JobCenter application, in Context Listener 'ServletContextAppListener'. !!!!!!!!!!!!!!!!!!!!!!!!" );

		log.warn( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  Startup of JobCenter application, in Context Listener 'ServletContextAppListener'. !!!!!!!!!!!!!!!!!!!!!!!!" );


		ServletContext context = event.getServletContext();

		SessionFactory hibernateSessionFactory = null;

		Session session = null;

		try {

			// START: The following block of code was copied from "OpenSessionInViewFilter" in Spring

			//  These two blocks of code are here so that the Hibernate session is created and put on the ThreadLocal
			//    so that all the beans configured in the Spring "applicationContext.xml" file will also work here.

			WebApplicationContext springCTX = WebApplicationContextUtils.getWebApplicationContext( context );

			if ( springCTX == null ) {

				throw new RuntimeException( "WebApplicationContext springCTX returned == null" );
			}

			hibernateSessionFactory = springCTX.getBean( ServerJerseyConstants.SESSION_FACTORY_ID_IN_SPRING_CONTEXT, SessionFactory.class );

			session = SessionFactoryUtils.getSession(hibernateSessionFactory, true);
			TransactionSynchronizationManager.bindResource(hibernateSessionFactory, new SessionHolder(session));

			// END: The following block of code was copied from "OpenSessionInViewFilter" in Spring


			GetValueFromConfigService getValueFromConfigService = GetValueFromConfigServiceImpl.getFromApplicationContext( springCTX );

			String fromEmailAddress = getValueFromConfigService.getConfigValueAsString( ServerConfigKeyValues.CLIENT_CHECKIN_NOTIFICATION_FROM_EMAIL_ADDRESS );

			String toEmailAddressList = getValueFromConfigService.getConfigValueAsString( ServerConfigKeyValues.CLIENT_CHECKIN_NOTIFICATION_TO_EMAIL_ADDRESS_LIST );

			String smtpAddress = getValueFromConfigService.getConfigValueAsString( ServerConfigKeyValues.CLIENT_CHECKIN_NOTIFICATION_SMTP_EMAIL_HOST );

			log.info( "fromEmailAddress = '" + fromEmailAddress + "', toEmailAddressList = '" + toEmailAddressList + "', smtpAddress = '" + smtpAddress + "'." );

		} catch (RuntimeException re) {

			log.error( "Exception thrown reading from config.  Exception = " + re.toString(), re );

			throw re;

		} finally {

			// START: The following block of code was copied from "OpenSessionInViewFilter" in Spring

			if ( hibernateSessionFactory != null ) {

				SessionHolder sessionHolder =
					(SessionHolder) TransactionSynchronizationManager.unbindResource( hibernateSessionFactory );

				SessionFactoryUtils.closeSession( sessionHolder.getSession() );
			}

			// END: The following block of code was copied from "OpenSessionInViewFilter" in Spring


		}

	}

}
