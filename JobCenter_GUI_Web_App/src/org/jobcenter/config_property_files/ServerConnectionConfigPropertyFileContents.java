package org.jobcenter.config_property_files;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Contents from config file 'server_connection_config.properties'
 *
 * Singleton
 */
public class ServerConnectionConfigPropertyFileContents {
	
	private static final Logger log = Logger.getLogger( ServerConnectionConfigPropertyFileContents.class );
	

	private static String CONFIG_FILENAME = "server_connection_config.properties";

	private static String PROPERTY_NAME__JOBCENTER_SERVER_URL = "jobcenter.server.url";

	
	private static final ServerConnectionConfigPropertyFileContents instance = new ServerConnectionConfigPropertyFileContents();
	
	public static ServerConnectionConfigPropertyFileContents getInstance() {
		return instance;
	}
	
	private String jobcenterServerURL;
	
	/**
	 * @throws Exception
	 */
	public void init() throws Exception {

		InputStream propertiesFileAsStream = null;
		try {
			//  Get config file from class path
			ClassLoader thisClassLoader = this.getClass().getClassLoader();
			URL configPropFile = thisClassLoader.getResource( CONFIG_FILENAME );
			if ( configPropFile == null ) {
				//  No properties file
				return;  //  EARLY EXIT
//				String msg = "Properties file '" + DB_CONFIG_FILENAME + "' not found in class path.";
//				log.error( msg );
//				throw new Exception( msg );
			} else {
				log.info( "Properties file '" + CONFIG_FILENAME + "' found, load path = " + configPropFile.getFile() );
			}
			propertiesFileAsStream = thisClassLoader.getResourceAsStream( CONFIG_FILENAME );
			if ( propertiesFileAsStream == null ) {
				//  No properties file
				return;  //  EARLY EXIT
//				String msg = "Properties file '" + CONFIG_FILENAME + "' not found in class path.";
//				log.error( msg );
//				throw new JobcenterGUIConfigException( msg );
			}
			Properties configProps = new Properties();
			configProps.load(propertiesFileAsStream);
			String propertyValue = null;
			propertyValue = configProps.getProperty( PROPERTY_NAME__JOBCENTER_SERVER_URL );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				jobcenterServerURL = propertyValue;
			}
		} catch ( RuntimeException e ) {
			log.error( "Error processing Properties file '" + CONFIG_FILENAME + "', exception: " + e.toString(), e );
			throw e;
		} finally {
			if ( propertiesFileAsStream != null ) {
				propertiesFileAsStream.close();
			}
		}
		
	}

	public String getJobcenterServerURL() {
		return jobcenterServerURL;
	}
	
}
