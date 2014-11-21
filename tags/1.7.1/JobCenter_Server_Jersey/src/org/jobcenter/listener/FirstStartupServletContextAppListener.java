package org.jobcenter.listener;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

/**
 * This class is loaded and the method "contextInitialized" is called when the web application is first loaded by the container
 *
 *	This class was creates just for getting the logging in at the beginning of web app startup
 */
public class FirstStartupServletContextAppListener extends HttpServlet implements
		ServletContextListener {

	private static final long serialVersionUID = 1L;


	private static Logger log = Logger.getLogger(FirstStartupServletContextAppListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent sce) {


	}

	@Override
	public void contextInitialized(ServletContextEvent event) {


		log.info( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  Startup of JobCenter application, in Context Listener 'FirstStartupServletContextAppListener'. !!!!!!!!!!!!!!!!!!!!!!!!" );

		log.warn( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  Startup of JobCenter application, in Context Listener 'FirstStartupServletContextAppListener'. !!!!!!!!!!!!!!!!!!!!!!!!" );


		ServletContext context = event.getServletContext();


	}

}
