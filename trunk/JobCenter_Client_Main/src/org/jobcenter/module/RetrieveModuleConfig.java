package org.jobcenter.module;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jobcenter.constants.ClientConstants;
import org.jobcenter.constants.ModuleConfigPropertyFileNames;
import org.jobcenter.util.BuildURLClassLoader;
import org.jobcenter.util.ModuleDirectoryUtil;

/**
 *
 *
 */
public class RetrieveModuleConfig {


	private static Logger log = Logger.getLogger(RetrieveModuleConfig.class);

	private static final String CONFIG_PROPERTY_MODULE_NAME = "module.name";

	private static final String CONFIG_PROPERTY_MODULE_JAVA_CLASS = "module.java.class";

	private static final String CONFIG_PROPERTY_MODULE_VERSION_NUMBER = "module.version.number";


	private static final String CONFIG_PROPERTY_MODULE_DEFAULT_MAX_THREADS_PER_JOB = "module.default.max.threads.per.job";

	private static final String CONFIG_PROPERTY_MODULE_DEFAULT_MIN_THREADS_PER_JOB = "module.default.min.threads.per.job";

	

	private static final String CONFIG_PROPERTY_MODULE_MAX_CONCURRENT_JOBS_PER_CLIENT = "module.max.concurrent.jobs.per.client";

	private static final String CONFIG_PROPERTY_MODULE_MAX_THREADS_PER_JOB = "module.max.threads.per.job";

	private static final String CONFIG_PROPERTY_MODULE_MIN_THREADS_PER_JOB = "module.min.threads.per.job";


	private static final String UNLIMITED_MAX_THREADS_FIRST_CHARACTER_LOWER_CASE = "u";


	private static final int MAX_THREADS_PER_JOB_DEFAULT = 1;



	/**
	 * @return
	 * @throws Throwable
	 */
	public List<ModuleConfigDTO> getConfigAllModules() throws Throwable {

		List<ModuleConfigDTO> configAllModules = new ArrayList<ModuleConfigDTO>();


		File modulesBaseDir = new File( ClientConstants.MODULE_BASE_LOCATION );

		if ( ! modulesBaseDir.exists() ) {

			throw new IllegalArgumentException( "Sub directory '" + ClientConstants.MODULE_BASE_LOCATION  + "' does not exist" );
		}

		if ( ! modulesBaseDir.isDirectory() ) {

			throw new IllegalArgumentException( "Sub directory '" + ClientConstants.MODULE_BASE_LOCATION  + "' is not a directory" );
		}

		File[] moduleDirs = modulesBaseDir.listFiles();

		for ( File moduleDir : moduleDirs ) {

			if ( moduleDir.isDirectory()  ) {

				if ( ! ModuleDirectoryUtil.excludeModuleDirectory( moduleDir.getName() ) ) {

					ModuleConfigDTO configDTOModule = getConfigDTOModuleForModule( moduleDir );

					configAllModules.add( configDTOModule );
				}
			}
		}


		validateConfig( configAllModules );

		logConfig( configAllModules );

		return configAllModules;
	}


	/**
	 * logs the configuration loaded from the modules:
	 *
	 * @param configAllModules
	 */
	private void logConfig( List<ModuleConfigDTO> configAllModules ) throws Throwable {

		if( log.isInfoEnabled() ) {

			log.info( "Successfully loaded the configuration from the modules.  The modules loaded are (in seperate log statements):" );

			for ( ModuleConfigDTO moduleConfigDTO : configAllModules  ) {

				log.info( "               Module name: '" + moduleConfigDTO.getModuleName()
						+ "', subdirectory: '" + moduleConfigDTO.getModuleSubDirectory()
						+ "', Module Java Class: '" + moduleConfigDTO.getModuleJavaClass()
						+ "', Max Number Concurrent Jobs: '" + moduleConfigDTO.getMaxNumberConcurrentJobs()
						+ "', Module Version: '" + moduleConfigDTO.getModuleVersion() );
			}

			log.info( "End Of List: Successfully loaded the configuration from the modules.  The modules loaded are (in seperate log statements):" );
		}
	}




	/**
	 * does overall validation of config and throws exceptions if there are problems
	 *
	 * @param configAllModules
	 */
	private void validateConfig( List<ModuleConfigDTO> configAllModules ) throws Throwable {

		//  currently validating that the same module name is not loaded twice

		for ( ModuleConfigDTO moduleConfigDTO : configAllModules  ) {

			for ( ModuleConfigDTO moduleConfigDTOCompare : configAllModules  ) {

				if ( moduleConfigDTO != moduleConfigDTOCompare
						&& moduleConfigDTO.getModuleName().equals( moduleConfigDTOCompare.getModuleName() ) ) {

					String msg = "!!!!!!!!!!  ERROR:  validateConfig(...):  Module name '" + moduleConfigDTO.getModuleName()
						+ "' has been loaded more than once.  One of the subdirectories is '"
						+ moduleConfigDTO.getModuleSubDirectory()
						+ "', another one is '"
						+ moduleConfigDTOCompare.getModuleSubDirectory()
						+ "'.  There may be other duplicates.";

					log.error( msg );
					throw new Exception( msg );
				}

			}
		}
	}


	/**
	 * @param moduleDir
	 * @return
	 * @throws Throwable
	 */
	private ModuleConfigDTO getConfigDTOModuleForModule( File moduleDir ) throws Throwable {

		ModuleConfigDTO moduleConfigDTO = new ModuleConfigDTO();

		String moduleSubDirectory = moduleDir.getName();

		moduleConfigDTO.setModuleSubDirectory( moduleSubDirectory );

		BuildURLClassLoader buildURLClassLoader = new BuildURLClassLoader();

		ClassLoader moduleInterfaceClassLoader = buildURLClassLoader.getClassLoaderForBaseDirectorySystemClassLoaderAsParent( moduleDir.getAbsolutePath() );

		getModuleConfigPerModuleData( moduleConfigDTO, moduleInterfaceClassLoader, moduleDir );

		getModuleConfigPerClientData( moduleConfigDTO, moduleInterfaceClassLoader, moduleDir );


		setModuleConfigDTODefaults( moduleConfigDTO );


		log.warn( "Contents of moduleConfigDTO (after loading config files and setting defaults) = " + moduleConfigDTO );


		return moduleConfigDTO;
	}


	/**
	 * @param moduleConfigDTO
	 */
	private void setModuleConfigDTODefaults( ModuleConfigDTO moduleConfigDTO ) {

		// default max number of threads to "1" if max not set and not set to unlimited

		if ( ! ( moduleConfigDTO.isMaxNumberThreadsPerJobUnlimited()
				 || moduleConfigDTO.isMaxNumberThreadsPerJobSet() ) ) {

			moduleConfigDTO.setMaxNumberThreadsPerJob( MAX_THREADS_PER_JOB_DEFAULT );
			moduleConfigDTO.setMaxNumberThreadsPerJobSet( true );

		}

	}



	/**
	 * Read data from properties file MODULE_CONFIG_PER_MODULE
	 *
	 * @param moduleConfigDTO
	 * @param moduleInterfaceClassLoader
	 * @param moduleDir
	 * @throws Throwable
	 */
	private void getModuleConfigPerModuleData( ModuleConfigDTO moduleConfigDTO, ClassLoader moduleInterfaceClassLoader, File moduleDir ) throws Throwable {


		String configFileName = ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_MODULE;


		InputStream props = moduleInterfaceClassLoader.getResourceAsStream( configFileName );

		if ( props == null ) {

			String msg = "Cannot find configuration of module file " + ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_MODULE + " for module directory " + moduleDir.getAbsolutePath();

			log.error( msg );

			throw new Exception( msg );

		}

		URL propURL = moduleInterfaceClassLoader.getResource( configFileName );


		String configFilePath = "Config file " + configFileName + "  propURL == null";

		if ( propURL != null ) {

			configFilePath = "Config file " + configFileName + "  propURL.getFile() = " + propURL.getFile();

			log.info( configFilePath );
		}


		StringBuilder logLoadedFromConfig = new StringBuilder( 5000 );

		logLoadedFromConfig.append( "INFO:  Loaded from Config file '" );

		logLoadedFromConfig.append(  configFilePath );
		logLoadedFromConfig.append( "' ( will be overrided by values from config file '" );
		logLoadedFromConfig.append( ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_CLIENT );
		logLoadedFromConfig.append( "' ).  " );

		Properties configProps = new Properties();

		configProps.load(props);

		String moduleName =  configProps.getProperty( CONFIG_PROPERTY_MODULE_NAME );

		if ( StringUtils.isEmpty( moduleName ) ) {

			String msg = "Error processing config for module '" + moduleConfigDTO.getModuleName()
			+ "' in subdirectory '" + moduleConfigDTO.getModuleSubDirectory() + "'."
			+ "  property '" + CONFIG_PROPERTY_MODULE_NAME + "' Cannot be empty."
			+ "  " + configFilePath;

			log.error( msg );

			throw new Exception( msg );
		}

		moduleConfigDTO.setModuleName( moduleName );

		logLoadedFromConfig.append( "  Property '" );
		logLoadedFromConfig.append( CONFIG_PROPERTY_MODULE_NAME );
		logLoadedFromConfig.append( "' = '" );
		logLoadedFromConfig.append( moduleName );
		logLoadedFromConfig.append( "'." );

		String moduleJavaClass =  configProps.getProperty( CONFIG_PROPERTY_MODULE_JAVA_CLASS );

		if ( StringUtils.isEmpty( moduleName ) ) {

			String msg = "Error processing config for module '" + moduleConfigDTO.getModuleName()
			+ "' in subdirectory '" + moduleConfigDTO.getModuleSubDirectory() + "'."
			+ "  property '" + CONFIG_PROPERTY_MODULE_JAVA_CLASS + "' Cannot be empty."
			+ "  " + configFilePath;

			log.error( msg );

			throw new Exception( msg );
		}

		moduleConfigDTO.setModuleJavaClass( moduleJavaClass );


		String moduleVersionString = configProps.getProperty( CONFIG_PROPERTY_MODULE_VERSION_NUMBER );

		if ( StringUtils.isEmpty( moduleVersionString ) ) {

			String msg = "Error processing config for module '" + moduleConfigDTO.getModuleName()
			+ "' in subdirectory '" + moduleConfigDTO.getModuleSubDirectory() + "'."
			+ "  property '" + CONFIG_PROPERTY_MODULE_VERSION_NUMBER + "' Cannot be empty."
			+ "  " + configFilePath;

			log.error( msg );

			throw new Exception( msg );
		}

		try {

			int moduleVersion = Integer.parseInt( moduleVersionString );

			moduleConfigDTO.setModuleVersion( moduleVersion );


		} catch ( NumberFormatException ex ) {


			String msg = "Error processing config for module '" + moduleConfigDTO.getModuleName()
			+ "' in subdirectory '" + moduleConfigDTO.getModuleSubDirectory() + "'."
			+ "  Failed to parse number for property '" + CONFIG_PROPERTY_MODULE_VERSION_NUMBER + "'."
			+ "  " + configFilePath;

			log.error( msg, ex );

			throw ex;
		}

		logLoadedFromConfig.append( "  Property '" );
		logLoadedFromConfig.append( CONFIG_PROPERTY_MODULE_VERSION_NUMBER );
		logLoadedFromConfig.append( "' = '" );
		logLoadedFromConfig.append( moduleVersionString );
		logLoadedFromConfig.append( "'." );


		String maxThreadsPerJobString = configProps.getProperty( CONFIG_PROPERTY_MODULE_DEFAULT_MAX_THREADS_PER_JOB );

		if ( ! StringUtils.isEmpty( maxThreadsPerJobString ) ) {

			String maxThreadsPerJobStringToLowerCase = maxThreadsPerJobString.toLowerCase();

			if ( maxThreadsPerJobStringToLowerCase.startsWith( UNLIMITED_MAX_THREADS_FIRST_CHARACTER_LOWER_CASE ) ) {

				moduleConfigDTO.setMaxNumberThreadsPerJobUnlimited( true );
				moduleConfigDTO.setMaxNumberThreadsPerJobSet( false );

			} else {

				try {

					int numValue = Integer.parseInt( maxThreadsPerJobString );

					moduleConfigDTO.setMaxNumberThreadsPerJob( numValue );

					moduleConfigDTO.setMaxNumberThreadsPerJobSet( true );

					moduleConfigDTO.setMaxNumberThreadsPerJobUnlimited( false );

				} catch ( NumberFormatException ex ) {

					String msg = "Error processing config for module '" + moduleConfigDTO.getModuleName()
					+ "' in subdirectory '" + moduleConfigDTO.getModuleSubDirectory() + "'."
					+ "  Failed to parse number for propterty '" + CONFIG_PROPERTY_MODULE_DEFAULT_MAX_THREADS_PER_JOB + "'."
					+ "  " + configFilePath;

					log.error( msg, ex );

					throw ex;
				}

			}

		}

		logLoadedFromConfig.append( "  Property '" );
		logLoadedFromConfig.append( CONFIG_PROPERTY_MODULE_DEFAULT_MAX_THREADS_PER_JOB );
		logLoadedFromConfig.append( "' = '" );
		logLoadedFromConfig.append( maxThreadsPerJobString );
		logLoadedFromConfig.append( "'." );


		String minThreadsPerJobString = configProps.getProperty( CONFIG_PROPERTY_MODULE_DEFAULT_MIN_THREADS_PER_JOB );

		if ( ! StringUtils.isEmpty( minThreadsPerJobString ) ) {

			try {

				int numValue = Integer.parseInt( minThreadsPerJobString );

				moduleConfigDTO.setMinNumberThreadsPerJob( numValue );

				moduleConfigDTO.setMinNumberThreadsPerJobSet( true );

			} catch ( NumberFormatException ex ) {

				String msg = "Error processing config for module '" + moduleConfigDTO.getModuleName()
				+ "' in subdirectory '" + moduleConfigDTO.getModuleSubDirectory() + "'."
				+ "  Failed to parse number for propterty '" + CONFIG_PROPERTY_MODULE_DEFAULT_MIN_THREADS_PER_JOB + "'."
				+ "  " + configFilePath;

				log.error( msg, ex );
			}
		}


		logLoadedFromConfig.append( "  Property '" );
		logLoadedFromConfig.append( CONFIG_PROPERTY_MODULE_DEFAULT_MIN_THREADS_PER_JOB );
		logLoadedFromConfig.append( "' = '" );
		logLoadedFromConfig.append( minThreadsPerJobString );
		logLoadedFromConfig.append( "'." );



		log.warn( logLoadedFromConfig.toString() );


		if ( log.isInfoEnabled() ) {
			log.info( "Contents of moduleConfigDTO after loading config file '" + configFilePath + "' = " + moduleConfigDTO );
		}
		
	}



	/**
	 * Read data from properties file MODULE_CONFIG_PER_CLIENT
	 *
	 * @param moduleConfigDTO
	 * @param moduleInterfaceClassLoader
	 * @param moduleDir
	 * @throws Throwable
	 */
	private void getModuleConfigPerClientData( ModuleConfigDTO moduleConfigDTO, ClassLoader moduleInterfaceClassLoader, File moduleDir ) throws Throwable {

		String configFileName = ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_CLIENT;


		InputStream props = moduleInterfaceClassLoader.getResourceAsStream( configFileName );


		if ( props == null ) {

			String msg = "Cannot find configuration of module file " + ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_CLIENT + " for module directory " + moduleDir.getAbsolutePath();

			log.error( msg );

			throw new Exception( msg );
		}



		URL propURL = moduleInterfaceClassLoader.getResource(  configFileName );

		String configFilePath = "Config file " + configFileName + "  propURL == null";

		if ( propURL != null ) {

			configFilePath = "Config file " + configFileName + "  propURL.getFile() = " + propURL.getFile();

			log.info( configFilePath );
		}


		StringBuilder logLoadedFromConfig = new StringBuilder( 5000 );

		logLoadedFromConfig.append( "INFO:  Loaded from Config file '" );

		logLoadedFromConfig.append(  configFilePath );
		logLoadedFromConfig.append( "' ( will override values in config file '" );
		logLoadedFromConfig.append( ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_MODULE );
		logLoadedFromConfig.append( "' ).  " );

		Properties configProps = new Properties();

		configProps.load(props);

		String maxNumberConcurrentJobsString = configProps.getProperty( CONFIG_PROPERTY_MODULE_MAX_CONCURRENT_JOBS_PER_CLIENT );

		if ( ! StringUtils.isEmpty( maxNumberConcurrentJobsString ) ) {

			try {

				int maxNumberConcurrentJobs = Integer.parseInt( maxNumberConcurrentJobsString );

				moduleConfigDTO.setMaxNumberConcurrentJobs( maxNumberConcurrentJobs );

				moduleConfigDTO.setMaxNumberConcurrentJobsSet( true );

			} catch ( NumberFormatException ex ) {

				String msg = "Error processing config for module '" + moduleConfigDTO.getModuleName()
				+ "' in subdirectory '" + moduleConfigDTO.getModuleSubDirectory() + "'."
				+ "  Failed to parse number for property '" + CONFIG_PROPERTY_MODULE_MAX_CONCURRENT_JOBS_PER_CLIENT + "'."
				+ "  " + configFilePath;

				log.error( msg, ex );

				throw ex;
			}
		}

		logLoadedFromConfig.append( "  Property '" );
		logLoadedFromConfig.append( CONFIG_PROPERTY_MODULE_MAX_CONCURRENT_JOBS_PER_CLIENT );
		logLoadedFromConfig.append( "' = '" );
		logLoadedFromConfig.append( maxNumberConcurrentJobsString );
		logLoadedFromConfig.append( "'." );




		String maxThreadsPerJobString = configProps.getProperty( CONFIG_PROPERTY_MODULE_MAX_THREADS_PER_JOB );

		if ( ! StringUtils.isEmpty( maxThreadsPerJobString ) ) {

			String maxThreadsPerJobStringToLowerCase = maxThreadsPerJobString.toLowerCase();

			if ( maxThreadsPerJobStringToLowerCase.startsWith( UNLIMITED_MAX_THREADS_FIRST_CHARACTER_LOWER_CASE ) ) {

				moduleConfigDTO.setMaxNumberThreadsPerJobUnlimited( true );
				moduleConfigDTO.setMaxNumberThreadsPerJobSet( false );

			} else {

				try {

					int numValue = Integer.parseInt( maxThreadsPerJobString );

					moduleConfigDTO.setMaxNumberThreadsPerJob( numValue );

					moduleConfigDTO.setMaxNumberThreadsPerJobSet( true );

					moduleConfigDTO.setMaxNumberThreadsPerJobUnlimited( false );

				} catch ( NumberFormatException ex ) {

					String msg = "Error processing config for module '" + moduleConfigDTO.getModuleName()
					+ "' in subdirectory '" + moduleConfigDTO.getModuleSubDirectory() + "'."
					+ "  Failed to parse number for propterty '" + CONFIG_PROPERTY_MODULE_MAX_THREADS_PER_JOB + "'."
					+ "  " + configFilePath;

					log.error( msg, ex );

					throw ex;
				}

			}

		}

		logLoadedFromConfig.append( "  Property '" );
		logLoadedFromConfig.append( CONFIG_PROPERTY_MODULE_MAX_THREADS_PER_JOB );
		logLoadedFromConfig.append( "' = '" );
		logLoadedFromConfig.append( maxThreadsPerJobString );
		logLoadedFromConfig.append( "'." );


		String minThreadsPerJobString = configProps.getProperty( CONFIG_PROPERTY_MODULE_MIN_THREADS_PER_JOB );

		if ( ! StringUtils.isEmpty( minThreadsPerJobString ) ) {

			try {

				int numValue = Integer.parseInt( minThreadsPerJobString );

				moduleConfigDTO.setMinNumberThreadsPerJob( numValue );

				moduleConfigDTO.setMinNumberThreadsPerJobSet( true );

			} catch ( NumberFormatException ex ) {

				String msg = "Error processing config for module '" + moduleConfigDTO.getModuleName()
				+ "' in subdirectory '" + moduleConfigDTO.getModuleSubDirectory() + "'."
				+ "  Failed to parse number for propterty '" + CONFIG_PROPERTY_MODULE_MIN_THREADS_PER_JOB + "'."
				+ "  " + configFilePath;

				log.error( msg, ex );
			}
		}


		logLoadedFromConfig.append( "  Property '" );
		logLoadedFromConfig.append( CONFIG_PROPERTY_MODULE_MIN_THREADS_PER_JOB );
		logLoadedFromConfig.append( "' = '" );
		logLoadedFromConfig.append( minThreadsPerJobString );
		logLoadedFromConfig.append( "'." );


		log.warn( logLoadedFromConfig.toString() );

		if ( log.isInfoEnabled() ) {
			log.info( "Contents of moduleConfigDTO after loading config file '" + configFilePath + "' = " + moduleConfigDTO );
		}
	}





}
