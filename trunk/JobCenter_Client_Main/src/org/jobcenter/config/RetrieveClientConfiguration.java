package org.jobcenter.config;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jobcenter.constants.ClientConstants;
import org.jobcenter.module.ModuleConfigDTO;
import org.jobcenter.module.ModuleFactory;
import org.jobcenter.module.RetrieveModuleConfig;
import org.jobcenter.util.BuildURLClassLoader;


/**
 *
 *
 */
public class RetrieveClientConfiguration {

	private static Logger log = Logger.getLogger(RetrieveClientConfiguration.class);

	private static final String CONFIG_DIR_PROPERTIES_FILE = "config_client_main.properties";

	private static final String client_main_file_path = "jobcenter_client_code/client_main";

	private static final String CONFIG_CLIENT_NODE_NAME  = "client.node.name";

	private static final String CONFIG_MAX_NUMBER_CONCURRENT_JOBS  = "max.number.concurrent.jobs";

	private static final String CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS  = "sleep.time.checking.for.new.jobs";

	private static final String CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS_NO_WORKER_THREADS = "sleep.time.checking.for.new.jobs.no.worker.threads";

	private static final String CONFIG_SLEEP_TIME_CHECKING_CONTROL_FILE  = "sleep.time.checking.control.file";

	private static final String CONFIG_LOAD_MODULES_ON_STARTUP  = "load.all.modules.on.startup";

	/**
	 * load configuration for client and modules into singleton of ClientConfigDTO
	 * @throws Throwable
	 */
	public void loadClientConfiguration() throws Throwable {

		//  retrieve singleton
		ClientConfigDTO clientConfigDTO = ClientConfigDTO.getSingletonInstance();  //  retrieve singleton


		BuildURLClassLoader buildURLClassLoader = new BuildURLClassLoader();

		ClassLoader mainClassLoader = buildURLClassLoader.getClassLoaderForBaseDirectorySystemClassLoaderAsParent( client_main_file_path );

		URL configPropFile = mainClassLoader.getResource( CONFIG_DIR_PROPERTIES_FILE );


		if ( configPropFile == null ) {

			log.error( "Properties file '" + CONFIG_DIR_PROPERTIES_FILE + "' not found " );
		} else {

			log.info( "Properties file '" + CONFIG_DIR_PROPERTIES_FILE + "' load path = " + configPropFile.getFile() + ", classLoader path = " + client_main_file_path );
		}

		InputStream props = mainClassLoader.getResourceAsStream( CONFIG_DIR_PROPERTIES_FILE );

		if ( props == null ) {

			log.error( "file config_client_main.properties  not found " );

		} else {

			URL propURL = mainClassLoader.getResource( CONFIG_DIR_PROPERTIES_FILE );

			if ( propURL != null ) {

				log.info( "Config Dir file " + CONFIG_DIR_PROPERTIES_FILE + "  propURL.getFile() = " + propURL.getFile() );

//				System.out.println( "Config Dir file " + CONFIG_DIR_PROPERTIES_FILE + "  propURL.getPath() = " + propURL.getPath() );
//
//				System.out.println( "Config Dir file " + CONFIG_DIR_PROPERTIES_FILE + "  propURL.getContent() = " + propURL.getContent() );
			}

			Properties configProps = new Properties();

			configProps.load(props);


			String clientNodeName = configProps.getProperty( CONFIG_CLIENT_NODE_NAME );

			if (  clientNodeName == null || clientNodeName.isEmpty() ) {

				String msg = "Exception:  configProps.getProperty(\"" + CONFIG_CLIENT_NODE_NAME + "\") = |" + clientNodeName + "|.";

				log.error( msg );

				System.out.println( msg );

				throw new Exception( msg );
			}

			clientConfigDTO.setClientNodeName( clientNodeName );



			String maxThreadsForModulesString = "";

			try {

				maxThreadsForModulesString = configProps.getProperty( CONFIG_MAX_NUMBER_CONCURRENT_JOBS );


			} catch ( Exception ex ) {

				String msg = "Exception:  configProps.getProperty(\"" + CONFIG_MAX_NUMBER_CONCURRENT_JOBS + "\") = " + maxThreadsForModulesString;

				log.error( msg, ex );

				System.out.println( msg );

				throw new Exception( msg, ex );

			}

			if ( maxThreadsForModulesString == null || maxThreadsForModulesString.isEmpty() ) {

				String msg = "This property needs a value:\"" + CONFIG_MAX_NUMBER_CONCURRENT_JOBS + "\"";

				log.error( msg );

				System.out.println( msg );

				throw new Exception( msg );

			}

			try {
				int maxThreadsForModules = Integer.parseInt( maxThreadsForModulesString );

				clientConfigDTO.setMaxThreadsForModules( maxThreadsForModules );

			} catch ( Exception ex ) {

				String msg = "Exception:  configProps.getProperty(\"" + CONFIG_MAX_NUMBER_CONCURRENT_JOBS + "\") = " + maxThreadsForModulesString;

				log.error( msg, ex );

				System.out.println( msg );

				throw new Exception( msg, ex );

			}


			String sleepTimeCheckingForNewJobsString = configProps.getProperty( CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS );

			try {

				int sleepTimeCheckingForNewJobs = Integer.parseInt( sleepTimeCheckingForNewJobsString );

				if ( sleepTimeCheckingForNewJobs < 1 ) {

					String msg = "Configuration error: Property \"" + CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS + "\") cannot be less than '1', it is = " + sleepTimeCheckingForNewJobsString;

					log.error( msg );

					System.out.println( msg );

					throw new Exception( msg );
				}

				clientConfigDTO.setSleepTimeCheckingForNewJobs( sleepTimeCheckingForNewJobs );

			} catch ( Exception ex ) {


				String msg = "Exception:  configProps.getProperty(\"" + CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS + "\") = " + sleepTimeCheckingForNewJobsString;

				log.error( msg, ex );

				System.out.println( msg );

				throw new Exception( msg, ex );
			}


			boolean sleepTimeCheckingForNewJobsNoWorkerThreadsStringSet = false;

			String sleepTimeCheckingForNewJobsNoWorkerThreadsString = configProps.getProperty( CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS_NO_WORKER_THREADS );

			if ( sleepTimeCheckingForNewJobsNoWorkerThreadsString != null && ! sleepTimeCheckingForNewJobsNoWorkerThreadsString.isEmpty() ) {
				try {

					int sleepTimeCheckingForNewJobsNoWorkerThreads = Integer.parseInt( sleepTimeCheckingForNewJobsNoWorkerThreadsString );

					if ( sleepTimeCheckingForNewJobsNoWorkerThreads < 1 ) {

						String msg = "Configuration error: Property \"" + CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS_NO_WORKER_THREADS + "\") cannot be less than '1', it is = " + sleepTimeCheckingForNewJobsNoWorkerThreadsString;

						log.error( msg );

						System.out.println( msg );

						throw new Exception( msg );
					} else {

						sleepTimeCheckingForNewJobsNoWorkerThreadsStringSet = true;

						clientConfigDTO.setSleepTimeCheckingForNewJobsNoWorkerThreads( sleepTimeCheckingForNewJobsNoWorkerThreads );
					}

				} catch ( Exception ex ) {


					String msg = "The provided value for config entry \"" + CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS_NO_WORKER_THREADS + "\".   cannot be parsed to integer.  Value in file = " + sleepTimeCheckingForNewJobsNoWorkerThreadsString;

					log.error( msg, ex );

					System.out.println( msg );

					throw new Exception( msg, ex );
				}
			}

			if ( ! sleepTimeCheckingForNewJobsNoWorkerThreadsStringSet ) {

				int sleepTimeCheckingForNewJobs = clientConfigDTO.getSleepTimeCheckingForNewJobs();

				int sleepTimeCheckingForNewJobsNoWorkerThreads
						= sleepTimeCheckingForNewJobs * ClientConstants.MULTIPLE_FOR_COMPUTING_TIME_CHECKING_WITH_NO_WORKER_THREADS;


				String msg = "Config entry \"" + CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS_NO_WORKER_THREADS + "\" not set, using default of " + sleepTimeCheckingForNewJobsNoWorkerThreads;

				log.info( msg );

				clientConfigDTO.setSleepTimeCheckingForNewJobsNoWorkerThreads( sleepTimeCheckingForNewJobsNoWorkerThreads );
			}




			String sleepTimeCheckingControlFileString = configProps.getProperty( CONFIG_SLEEP_TIME_CHECKING_CONTROL_FILE );

			try {

				int sleepTimeCheckingControlFile = Integer.parseInt( sleepTimeCheckingControlFileString );

				if ( sleepTimeCheckingControlFile < 1 ) {

					String msg = "Configuration error: Property \"" + CONFIG_SLEEP_TIME_CHECKING_CONTROL_FILE + "\") cannot be less than '1', it is = " + sleepTimeCheckingControlFileString;

					log.error( msg );

					System.out.println( msg );

					throw new Exception( msg );
				}


				clientConfigDTO.setSleepTimeCheckingControlFile( sleepTimeCheckingControlFile );

			} catch ( Exception ex ) {


				String msg = "Exception:  configProps.getProperty(\"" + CONFIG_SLEEP_TIME_CHECKING_CONTROL_FILE + "\") = " + sleepTimeCheckingForNewJobsString;

				log.error( msg, ex );

				System.out.println( msg );

				throw new Exception( msg, ex );
			}







			String loadModulesOnStartupString = configProps.getProperty( CONFIG_LOAD_MODULES_ON_STARTUP );

			boolean loadModulesOnStartup = false;  // default to false

			if ( "true".equalsIgnoreCase( loadModulesOnStartupString ) ) {

				loadModulesOnStartup = true;
			}

			clientConfigDTO.setLoadModulesOnStartup( loadModulesOnStartup );


		}

		RetrieveModuleConfig retrieveModuleConfig = new RetrieveModuleConfig();

		List<ModuleConfigDTO> configAllModules = retrieveModuleConfig.getConfigAllModules();

		clientConfigDTO.setModuleConfigList( configAllModules );

		if ( clientConfigDTO.isLoadModulesOnStartup() ) {

			ModuleFactory.getInstance().loadModulesForAllModuleConfig( clientConfigDTO );
		}
	}


}
