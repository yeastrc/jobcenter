package org.jobcenter.config_property_files;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Contents from config file 'jobcenter_gui.properties'
 *
 * Singleton
 */
public class JobcenterGUIConfigPropertyFileContents {
	
	private static final Logger log = Logger.getLogger( JobcenterGUIConfigPropertyFileContents.class );
	

	private static String CONFIG_FILENAME = "jobcenter_gui.properties";

	private static String PROPERTY_NAME__WEBAPP_SECURITY_INSTALLED = "webapp.security.installed";
	private static String PROPERTY_NAME__WEBAPP_SECURITY_INSTALLED__Y = "Y";

	private static String PROPERTY_NAME__SHOW_SIMPLE_HOME_PAGE = "show.simple.home.page";
	private static String PROPERTY_NAME__SHOW_SIMPLE_HOME_PAGE__Y = "Y";

	private static String PROPERTY_NAME__SHOW_SIMPLE_JOB_STATUS_FILTER = "show.simple.job.status.filter";
	private static String PROPERTY_NAME__SHOW_SIMPLE_JOB_STATUS_FILTER__Y = "Y";

	private static String PROPERTY_NAME__REQUEST_TYPES_DISABLE = "request.types.disable";
	private static String PROPERTY_NAME__REQUEST_TYPES_DISABLE__Y = "Y";

	private static String PROPERTY_NAME__JOB_TYPES_ALLOWED = "job.types.allowed";
	private static String PROPERTY_NAME__REQUEST_TYPES_ALLOWED = "request.types.allowed";
	private static String PROPERTY_NAME__TYPES_ALLOWED_DELIMITER_CHARACTER = "delimiter.character";
	
	private static String TYPES_ALLOWED_DELIMITER_CHARACTER_DEFAULT = ",";  // Default to comma
			
	private static final JobcenterGUIConfigPropertyFileContents instance = new JobcenterGUIConfigPropertyFileContents();
	
	public static JobcenterGUIConfigPropertyFileContents getInstance() {
		return instance;
	}
	
	private boolean webappSecurityInstalled = false;

	private boolean simpleHomePage = false;
	
	private boolean simpleJobStatusFilter = false;
	
	private boolean requestTypesDisable = false;
	
	private Set<String> jobTypeNamesAllowed;
	private Set<String> requestTypeNamesAllowed;
	
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

			log.warn( "INFO: Starting reading configuration from file: " + CONFIG_FILENAME );

			Properties configProps = new Properties();
			configProps.load(propertiesFileAsStream);
			String propertyValue = null;
			
			propertyValue = configProps.getProperty( PROPERTY_NAME__WEBAPP_SECURITY_INSTALLED );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				if ( PROPERTY_NAME__WEBAPP_SECURITY_INSTALLED__Y.equals( propertyValue ) ) {
					webappSecurityInstalled = true;
					log.warn( "INFO:  '" + PROPERTY_NAME__WEBAPP_SECURITY_INSTALLED 
							+ "' is '" + PROPERTY_NAME__WEBAPP_SECURITY_INSTALLED__Y 
							+ "' so hiding 'no security' message");
				} else {
					log.warn( "INFO:  '" + PROPERTY_NAME__WEBAPP_SECURITY_INSTALLED 
							+ "' is NOT '" + PROPERTY_NAME__WEBAPP_SECURITY_INSTALLED__Y 
							+ "' so NOT hiding 'no security' message");
				}
			} else {
				log.warn( "INFO:  '" + PROPERTY_NAME__WEBAPP_SECURITY_INSTALLED 
							+ "' is NOT '" + PROPERTY_NAME__WEBAPP_SECURITY_INSTALLED__Y 
						+ "' so NOT hiding 'no security' message");
			}
			
			propertyValue = configProps.getProperty( PROPERTY_NAME__SHOW_SIMPLE_HOME_PAGE );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				if ( PROPERTY_NAME__SHOW_SIMPLE_HOME_PAGE__Y.equals( propertyValue ) ) {
					simpleHomePage = true;
					log.warn( "INFO:  '" + PROPERTY_NAME__SHOW_SIMPLE_HOME_PAGE 
							+ "' is '" + PROPERTY_NAME__SHOW_SIMPLE_HOME_PAGE__Y 
							+ "' so hiding 'no security' message");
				} else {
					log.warn( "INFO:  '" + PROPERTY_NAME__SHOW_SIMPLE_HOME_PAGE 
							+ "' is NOT '" + PROPERTY_NAME__SHOW_SIMPLE_HOME_PAGE__Y 
							+ "' so NOT hiding 'no security' message");
				}
			} else {
				log.warn( "INFO:  '" + PROPERTY_NAME__SHOW_SIMPLE_HOME_PAGE 
							+ "' is NOT '" + PROPERTY_NAME__SHOW_SIMPLE_HOME_PAGE__Y 
						+ "' so NOT hiding 'no security' message");
			}
			
			propertyValue = configProps.getProperty( PROPERTY_NAME__SHOW_SIMPLE_JOB_STATUS_FILTER );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				if ( PROPERTY_NAME__SHOW_SIMPLE_JOB_STATUS_FILTER__Y.equals( propertyValue ) ) {
					simpleJobStatusFilter = true;
					log.warn( "INFO:  '" + PROPERTY_NAME__SHOW_SIMPLE_JOB_STATUS_FILTER 
							+ "' is '" + PROPERTY_NAME__SHOW_SIMPLE_JOB_STATUS_FILTER__Y 
							+ "' so hiding 'no security' message");
				} else {
					log.warn( "INFO:  '" + PROPERTY_NAME__SHOW_SIMPLE_JOB_STATUS_FILTER 
							+ "' is NOT '" + PROPERTY_NAME__SHOW_SIMPLE_JOB_STATUS_FILTER__Y 
							+ "' so NOT hiding 'no security' message");
				}
			} else {
				log.warn( "INFO:  '" + PROPERTY_NAME__SHOW_SIMPLE_JOB_STATUS_FILTER 
							+ "' is NOT '" + PROPERTY_NAME__SHOW_SIMPLE_JOB_STATUS_FILTER__Y 
						+ "' so NOT hiding 'no security' message");
			}
			
			propertyValue = configProps.getProperty( PROPERTY_NAME__REQUEST_TYPES_DISABLE );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				if ( PROPERTY_NAME__REQUEST_TYPES_DISABLE__Y.equals( propertyValue ) ) {
					requestTypesDisable = true;
					log.warn( "INFO:  '" + PROPERTY_NAME__REQUEST_TYPES_DISABLE 
							+ "' is '" + PROPERTY_NAME__REQUEST_TYPES_DISABLE__Y 
							+ "' so disabling all request type processing");
				} else {
					log.warn( "INFO:  '" + PROPERTY_NAME__REQUEST_TYPES_DISABLE 
							+ "' is NOT '" + PROPERTY_NAME__REQUEST_TYPES_DISABLE__Y 
							+ "' so NOT disabling all request type processing");
				}
			} else {
				log.warn( "INFO:  '" + PROPERTY_NAME__REQUEST_TYPES_DISABLE 
							+ "' is NOT '" + PROPERTY_NAME__REQUEST_TYPES_DISABLE__Y 
						+ "' so NOT disabling all request type processing");
			}
			
			String delimiter = TYPES_ALLOWED_DELIMITER_CHARACTER_DEFAULT;
			propertyValue = configProps.getProperty( PROPERTY_NAME__TYPES_ALLOWED_DELIMITER_CHARACTER );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				delimiter = propertyValue;
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__JOB_TYPES_ALLOWED );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				String[] jobTypesAllowedSingleStringSplit = propertyValue.split( delimiter );
				Set<String> jobTypesAllowedLocal = new HashSet<>( jobTypesAllowedSingleStringSplit.length );
				for ( String jobTypesAllowedSingleStringEntry : jobTypesAllowedSingleStringSplit ) {
					jobTypesAllowedLocal.add( jobTypesAllowedSingleStringEntry );
				}
				jobTypeNamesAllowed = Collections.unmodifiableSet( jobTypesAllowedLocal );
				
				log.warn( "INFO: Job Types allowed: " + jobTypeNamesAllowed );
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__REQUEST_TYPES_ALLOWED );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				String[] requestTypesAllowedSingleStringSplit = propertyValue.split( delimiter );
				Set<String> requestTypesAllowedLocal = new HashSet<>( requestTypesAllowedSingleStringSplit.length );
				for ( String jobTypesAllowedSingleStringEntry : requestTypesAllowedSingleStringSplit ) {
					requestTypesAllowedLocal.add( jobTypesAllowedSingleStringEntry );
				}
				requestTypeNamesAllowed = Collections.unmodifiableSet( requestTypesAllowedLocal );

				log.warn( "INFO: Request Types allowed: " + requestTypeNamesAllowed );
			}

			log.warn( "INFO: Finished reading configuration from file: " + CONFIG_FILENAME );
					
		} catch ( RuntimeException e ) {
			log.error( "Error processing Properties file '" + CONFIG_FILENAME + "', exception: " + e.toString(), e );
			throw e;
		} finally {
			if ( propertiesFileAsStream != null ) {
				propertiesFileAsStream.close();
			}
		}
		
	}

	public boolean isRequestTypesDisable() {
		return requestTypesDisable;
	}

	public Set<String> getJobTypeNamesAllowed() {
		return jobTypeNamesAllowed;
	}

	public Set<String> getRequestTypeNamesAllowed() {
		return requestTypeNamesAllowed;
	}

	public boolean isWebappSecurityInstalled() {
		return webappSecurityInstalled;
	}

	public boolean isSimpleJobStatusFilter() {
		return simpleJobStatusFilter;
	}

	public boolean isSimpleHomePage() {
		return simpleHomePage;
	}
	
}
