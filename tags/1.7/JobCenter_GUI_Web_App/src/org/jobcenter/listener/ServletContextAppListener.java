package org.jobcenter.listener;

import javax.servlet.*;
import javax.servlet.http.*;

import org.jobcenter.constants.GUIWebAppConstants;


public class ServletContextAppListener extends HttpServlet implements ServletContextListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {

		ServletContext context = event.getServletContext();

		String contextPath = context.getContextPath();

		context.setAttribute( "contextPath", contextPath );
		
		context.setAttribute( "serverURL", GUIWebAppConstants.URL_TO_SERVER );
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {

		ServletContext context = event.getServletContext();


	}
}
