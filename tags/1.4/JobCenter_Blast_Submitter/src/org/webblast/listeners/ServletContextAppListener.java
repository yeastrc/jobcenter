package org.webblast.listeners;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;


/**
 * This class is loaded and the method "contextInitialized" is called when the web application is first loaded by the container
 *
 */
public class ServletContextAppListener extends HttpServlet implements ServletContextListener {

	private static Logger log = Logger.getLogger( ServletContextAppListener.class );
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {

		//  The servlet context is accessible by the expression language in the JSPs

		ServletContext context = event.getServletContext();

		String contextPath = context.getContextPath();

		context.setAttribute( "contextPath", contextPath );
		
		System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!   Start up of blast web app  !!!!!!!!!!!!!!!!! to System.out.println" );

		log.info( "!!!!!!!!!!!!!!!!!!!!!!!   Start up of blast web app  !!!!!!!!!!!!!!!!!" );


	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {

		//ServletContext context = event.getServletContext();


	}
	
	
		
}
