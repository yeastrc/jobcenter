package org.jobcenter.module;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
			log.info( "Successfully loaded the configuration from the modules.  The modules loaded are:" );

			for ( ModuleConfigDTO moduleConfigDTO : configAllModules  ) {

				log.info( "Module name: '" + moduleConfigDTO.getModuleName()
						+ "', subdirectory: '" + moduleConfigDTO.getModuleSubDirectory()
						+ "', Module Java Class: '" + moduleConfigDTO.getModuleJavaClass()
						+ "', Max Number Threads: '" + moduleConfigDTO.getMaxNumberThreads()
						+ "', Module Version: '" + moduleConfigDTO.getModuleVersion() );
			}
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

		ModuleConfigDTO configDTOModule = new ModuleConfigDTO();

		String moduleSubDirectory = moduleDir.getName();

		configDTOModule.setModuleSubDirectory( moduleSubDirectory );

		BuildURLClassLoader buildURLClassLoader = new BuildURLClassLoader();

		ClassLoader moduleInterfaceClassLoader = buildURLClassLoader.getClassLoaderForBaseDirectorySystemClassLoaderAsParent( moduleDir.getAbsolutePath() );

		getModuleConfigPerModuleData( configDTOModule, moduleInterfaceClassLoader, moduleDir );

		getModuleConfigPerClientData( configDTOModule, moduleInterfaceClassLoader, moduleDir );

		return configDTOModule;
	}


	/**
	 * Read data from properties file MODULE_CONFIG_PER_MODULE
	 *
	 * @param configDTOModule
	 * @param moduleInterfaceClassLoader
	 * @param moduleDir
	 * @throws Throwable
	 */
	private void getModuleConfigPerModuleData( ModuleConfigDTO configDTOModule, ClassLoader moduleInterfaceClassLoader, File moduleDir ) throws Throwable {


		String configFileName = ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_MODULE;


		InputStream props = moduleInterfaceClassLoader.getResourceAsStream( configFileName );

		if ( props == null ) {

			String msg = "Cannot find configuration of module file " + ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_MODULE + " for module directory " + moduleDir.getAbsolutePath();

			log.error( msg );

			throw new Exception( msg );

		} else {

			URL propURL = moduleInterfaceClassLoader.getResource( configFileName );

			if ( propURL != null ) {

				log.info( "Config Dir file " + configFileName + "  propURL.getFile() = " + propURL.getFile() );
			}

			Properties configProps = new Properties();

			configProps.load(props);

			String moduleName =  configProps.getProperty("module.name");

			configDTOModule.setModuleName( moduleName );

			String moduleJavaClass =  configProps.getProperty("module.java.class");

			configDTOModule.setModuleJavaClass( moduleJavaClass );


			String moduleVersionString = configProps.getProperty("module.version.number");

			try {

				int moduleVersion = Integer.parseInt( moduleVersionString );

				configDTOModule.setModuleVersion( moduleVersion );


			} catch ( NumberFormatException ex ) {


			}


		}
	}



	/**
	 * Read data from properties file MODULE_CONFIG_PER_CLIENT
	 *
	 * @param configDTOModule
	 * @param moduleInterfaceClassLoader
	 * @param moduleDir
	 * @throws Throwable
	 */
	private void getModuleConfigPerClientData( ModuleConfigDTO configDTOModule, ClassLoader moduleInterfaceClassLoader, File moduleDir ) throws Throwable {

		String configFileName = ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_CLIENT;


		InputStream props = moduleInterfaceClassLoader.getResourceAsStream( configFileName );


		if ( props == null ) {

			String msg = "Cannot find configuration of module file " + ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_CLIENT + " for module directory " + moduleDir.getAbsolutePath();

			log.error( msg );

			throw new Exception( msg );


		} else {

			URL propURL = moduleInterfaceClassLoader.getResource(  configFileName );

			if ( propURL != null ) {

				log.info( "Config Dir file " + configFileName + "  propURL.getFile() = " + propURL.getFile() );
			}

			Properties configProps = new Properties();

			configProps.load(props);

			String maxNumberThreadsString = configProps.getProperty("module.max.threads.per.job.manager.client");

			try {

				int maxNumberThreads = Integer.parseInt( maxNumberThreadsString );

				configDTOModule.setMaxNumberThreads( maxNumberThreads );

				configDTOModule.setMaxNumberThreadsSet( true );

			} catch ( NumberFormatException ex ) {


			}

		}
	}
}
