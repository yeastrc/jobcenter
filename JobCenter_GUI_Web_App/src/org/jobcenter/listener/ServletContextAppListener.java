package org.jobcenter.listener;

import javax.servlet.*;
import javax.servlet.http.*;

import org.jobcenter.config_property_files.JobcenterGUIConfigPropertyFileContents;
import org.jobcenter.config_property_files.ServerConnectionConfigPropertyFileContents;
import org.jobcenter.constants.WebConstants;
import org.jobcenter.gui_connection_to_server_client_factory.GUIConnectionToServerClientFactory;


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

		context.setAttribute( WebConstants.APP_CONTEXT_CONTEXT_PATH, contextPath );
		
		
		JobcenterGUIConfigPropertyFileContents jobcenterGUIConfigPropertyFileContents = 
				JobcenterGUIConfigPropertyFileContents.getInstance();
		try {
			jobcenterGUIConfigPropertyFileContents.init();
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}
		
		ServerConnectionConfigPropertyFileContents serverConnectionConfigPropertyFileContents =
				ServerConnectionConfigPropertyFileContents.getInstance();
		
		try {
			serverConnectionConfigPropertyFileContents.init();
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}
		
		try {
			GUIConnectionToServerClientFactory.getInstance().init();
		} catch ( Exception e ) {
			throw new RuntimeException( e );
		}

		context.setAttribute( WebConstants.APP_CONTEXT_SERVER_URL, serverConnectionConfigPropertyFileContents.getJobcenterServerURL() );
		
		context.setAttribute( WebConstants.APP_CONTEXT_CONFIG_OBJECT, jobcenterGUIConfigPropertyFileContents );
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {

		ServletContext context = event.getServletContext();

		GUIConnectionToServerClientFactory.getInstance().destroyGUIConnectionToServerClient();

	}
}
