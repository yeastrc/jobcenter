package org.jobcenter.gui_connection_to_server_client_factory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jobcenter.config_property_files.ServerConnectionConfigPropertyFileContents;
import org.jobcenter.constants.GUIWebAppConstants;
import org.jobcenter.guiclient.GUIConnectionToServerClient;

/**
 * Creates all instances of GUIConnectionToServerClient used in web app
 *
 * Singleton
 */
public class GUIConnectionToServerClientFactory {

	private static final Logger log = Logger.getLogger( GUIConnectionToServerClientFactory.class );
	
	private static final GUIConnectionToServerClientFactory instance = new GUIConnectionToServerClientFactory();
	
	public static GUIConnectionToServerClientFactory getInstance() {
		return instance;
	}
	
	private GUIConnectionToServerClient guiConnectionToServerClient = null;
	
	/**
	 * @throws Exception
	 */
	public void init() throws Exception {
		try {
			String jobcenterServerURL = 
					ServerConnectionConfigPropertyFileContents.getInstance().getJobcenterServerURL();
			if ( StringUtils.isEmpty( jobcenterServerURL ) ) {
				//  Not set in config file so use default
				jobcenterServerURL = GUIWebAppConstants.DEFAULT_URL_TO_SERVER;
				log.warn( "INFO:  No Jobcenter Server URL in config file or no config file so using default URL: " + jobcenterServerURL );
			} else {
				log.warn( "INFO:  Jobcenter Server URL in config file: " + jobcenterServerURL );
			}
			guiConnectionToServerClient = new GUIConnectionToServerClient();
			guiConnectionToServerClient.init( jobcenterServerURL );
		} catch ( Exception e ) {
			String msg = "Failed to init GUIConnectionToServerClient.";
			log.error( msg, e );
			throw e;
		}
	}
	
	
	/**
	 * @return
	 * @throws Exception
	 */
	public GUIConnectionToServerClient getGUIConnectionToServerClient() throws Exception {

		return guiConnectionToServerClient;
	}
	
	/**
	 * 
	 */
	public void destroyGUIConnectionToServerClient() {
		if ( guiConnectionToServerClient != null ) {
			try {
				guiConnectionToServerClient.destroy();
			} catch ( Exception e ) {
				String msg = "Failed to destroy GUIConnectionToServerClient.";
				log.error( msg, e );
				throw e;
			}
		}
	}
	
}
