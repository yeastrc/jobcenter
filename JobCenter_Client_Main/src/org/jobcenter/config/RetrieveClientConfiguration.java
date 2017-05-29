package org.jobcenter.config;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jobcenter.constants.ClientConfigPropertyNamesAndOtherConstants;
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

	/**
	 * load configuration for client and modules into singleton of ClientConfigDTO
	 * @throws Throwable
	 */
	public void loadClientConfiguration() throws Throwable {

		//  retrieve singleton
		ClientConfigDTO clientConfigDTO = ClientConfigDTO.getSingletonInstance();  //  retrieve singleton


		BuildURLClassLoader buildURLClassLoader = new BuildURLClassLoader();

		ClassLoader mainClassLoader = buildURLClassLoader.getClassLoaderForBaseDirectorySystemClassLoaderAsParent( ClientConfigPropertyNamesAndOtherConstants.CLIENT_MAIN_FILE_PATH );

		URL configPropFile = mainClassLoader.getResource( ClientConfigPropertyNamesAndOtherConstants.CONFIG_DIR_PROPERTIES_FILE );


		if ( configPropFile == null ) {

			log.error( "Properties file '" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_DIR_PROPERTIES_FILE + "' not found " );
		} else {

			log.info( "Properties file '" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_DIR_PROPERTIES_FILE + "' load path = " + configPropFile.getFile() + ", classLoader path = " + ClientConfigPropertyNamesAndOtherConstants.CLIENT_MAIN_FILE_PATH );
		}

		InputStream props = mainClassLoader.getResourceAsStream( ClientConfigPropertyNamesAndOtherConstants.CONFIG_DIR_PROPERTIES_FILE );

		if ( props == null ) {

			log.error( "file config_client_main.properties  not found " );

		} else {

			URL propURL = mainClassLoader.getResource( ClientConfigPropertyNamesAndOtherConstants.CONFIG_DIR_PROPERTIES_FILE );

			if ( propURL != null ) {

				log.info( "Config Dir file " + ClientConfigPropertyNamesAndOtherConstants.CONFIG_DIR_PROPERTIES_FILE + "  propURL.getFile() = " + propURL.getFile() );

//				System.out.println( "Config Dir file " + ClientConfigPropertyNamesAndOtherConstants.CONFIG_DIR_PROPERTIES_FILE + "  propURL.getPath() = " + propURL.getPath() );
//
//				System.out.println( "Config Dir file " + ClientConfigPropertyNamesAndOtherConstants.CONFIG_DIR_PROPERTIES_FILE + "  propURL.getContent() = " + propURL.getContent() );
			}

			Properties configProps = new Properties();

			configProps.load(props);


			String clientNodeName = configProps.getProperty( ClientConfigPropertyNamesAndOtherConstants.CONFIG_CLIENT_NODE_NAME );

			if (  clientNodeName == null || clientNodeName.isEmpty() ) {

				String msg = "Exception:  configProps.getProperty(\"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_CLIENT_NODE_NAME + "\") = |" + clientNodeName + "|.";

				log.error( msg );

				System.out.println( msg );

				throw new Exception( msg );
			}

			clientConfigDTO.setClientNodeName( clientNodeName );

/////////////////////////////

			String maxConcurrentJobsForModulesString = "";

			try {

				maxConcurrentJobsForModulesString = configProps.getProperty( ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_CONCURRENT_JOBS );


			} catch ( Exception ex ) {

				String msg = "Exception:  configProps.getProperty(\"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_CONCURRENT_JOBS + "\") = " + maxConcurrentJobsForModulesString;

				log.error( msg, ex );

				System.out.println( msg );

				throw new Exception( msg, ex );

			}

			String maxThreadsForModulesString = "";

			try {

				maxThreadsForModulesString = configProps.getProperty( ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_JOB_THREADS );


			} catch ( Exception ex ) {

				String msg = "Exception:  configProps.getProperty(\"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_JOB_THREADS + "\") = " + maxThreadsForModulesString;

				log.error( msg, ex );

				System.out.println( msg );

				throw new Exception( msg, ex );

			}



			if ( StringUtils.isEmpty( maxConcurrentJobsForModulesString ) && StringUtils.isEmpty( maxThreadsForModulesString ) ) {

				String msg = "One of these two properties needs a value:\"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_CONCURRENT_JOBS
				+ "\" or  \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_JOB_THREADS + "\"" + ".  Both properties can be set.";

				log.error( msg );

				System.out.println( msg );

				throw new Exception( msg );

			}


			if ( ! StringUtils.isEmpty( maxThreadsForModulesString ) ) {

				try {
					int maxThreadsForModules = Integer.parseInt( maxThreadsForModulesString );

					clientConfigDTO.setMaxThreadsForModules( maxThreadsForModules );


				} catch ( Exception ex ) {

					String msg = "Exception:  configProps.getProperty(\"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_JOB_THREADS + "\") = " + maxThreadsForModulesString;

					log.error( msg, ex );

					System.out.println( msg );

					throw new Exception( msg, ex );

				}
			}



			if ( ! StringUtils.isEmpty( maxConcurrentJobsForModulesString ) ) {

				try {
					int maxConcurrentJobsForModules = Integer.parseInt( maxConcurrentJobsForModulesString );

					clientConfigDTO.setMaxConcurrentJobs( maxConcurrentJobsForModules );

				} catch ( Exception ex ) {

					String msg = "Exception:  configProps.getProperty(\"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_CONCURRENT_JOBS + "\") = " + maxConcurrentJobsForModulesString;

					log.error( msg, ex );

					System.out.println( msg );

					throw new Exception( msg, ex );
				}
			}



			if ( StringUtils.isEmpty( maxConcurrentJobsForModulesString ) ) {

				log.warn( "Using the value of \"" + clientConfigDTO.getMaxThreadsForModules()
						+ "\"  in \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_JOB_THREADS
						+ "\" for the value for \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_CONCURRENT_JOBS
						+ "\" since the config param \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_CONCURRENT_JOBS
						+ "\" was not set in the configuration file." );

				//  copy the value to here since maxConcurrentJobsForModules is not set in the configuration
				clientConfigDTO.setMaxConcurrentJobs( clientConfigDTO.getMaxThreadsForModules() );
			}

			if ( StringUtils.isEmpty( maxThreadsForModulesString ) ) {

				log.warn( "Using the value of \"" + clientConfigDTO.getMaxConcurrentJobs()
						+ "\" in \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_CONCURRENT_JOBS
						+ "\" for the value for \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_JOB_THREADS
						+ "\" since the config param \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_JOB_THREADS
						+ "\" was not set in the configuration file." );


				//  copy the value to here since maxThreadsForModules is not set in the configuration
				clientConfigDTO.setMaxThreadsForModules( clientConfigDTO.getMaxConcurrentJobs() );
			}




			if ( clientConfigDTO.getMaxConcurrentJobs() > clientConfigDTO.getMaxThreadsForModules() ) {

				String msg = "ERROR:  The value of \"" + clientConfigDTO.getMaxConcurrentJobs()
						+ "\"  in \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_CONCURRENT_JOBS
						+ "\" is larger than the value of \"" + clientConfigDTO.getMaxThreadsForModules()
						+ "\" in \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_MAX_NUMBER_JOB_THREADS
						+ "\".";

				log.error( msg );

				System.out.println( msg );

				throw new Exception( msg );
			}



//////////////////////////////////////////////////

			String sleepTimeCheckingForNewJobsString = configProps.getProperty( ClientConfigPropertyNamesAndOtherConstants.CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS );

			try {

				int sleepTimeCheckingForNewJobs = Integer.parseInt( sleepTimeCheckingForNewJobsString );

				if ( sleepTimeCheckingForNewJobs < 1 ) {

					String msg = "Configuration error: Property \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS + "\") cannot be less than '1', it is = " + sleepTimeCheckingForNewJobsString;

					log.error( msg );

					System.out.println( msg );

					throw new Exception( msg );
				}

				clientConfigDTO.setSleepTimeCheckingForNewJobs( sleepTimeCheckingForNewJobs );

			} catch ( Exception ex ) {


				String msg = "Exception:  configProps.getProperty(\"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS + "\") = " + sleepTimeCheckingForNewJobsString;

				log.error( msg, ex );

				System.out.println( msg );

				throw new Exception( msg, ex );
			}


			boolean sleepTimeCheckingForNewJobsNoWorkerThreadsStringSet = false;

			String sleepTimeCheckingForNewJobsNoWorkerThreadsString = configProps.getProperty( ClientConfigPropertyNamesAndOtherConstants.CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS_NO_WORKER_THREADS );

			if ( sleepTimeCheckingForNewJobsNoWorkerThreadsString != null && ! sleepTimeCheckingForNewJobsNoWorkerThreadsString.isEmpty() ) {
				try {

					int sleepTimeCheckingForNewJobsNoWorkerThreads = Integer.parseInt( sleepTimeCheckingForNewJobsNoWorkerThreadsString );

					if ( sleepTimeCheckingForNewJobsNoWorkerThreads < 1 ) {

						String msg = "Configuration error: Property \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS_NO_WORKER_THREADS + "\") cannot be less than '1', it is = " + sleepTimeCheckingForNewJobsNoWorkerThreadsString;

						log.error( msg );

						System.out.println( msg );

						throw new Exception( msg );
					} else {

						sleepTimeCheckingForNewJobsNoWorkerThreadsStringSet = true;

						clientConfigDTO.setSleepTimeCheckingForNewJobsNoWorkerThreads( sleepTimeCheckingForNewJobsNoWorkerThreads );
					}

				} catch ( Exception ex ) {


					String msg = "The provided value for config entry \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS_NO_WORKER_THREADS + "\".   cannot be parsed to integer.  Value in file = " + sleepTimeCheckingForNewJobsNoWorkerThreadsString;

					log.error( msg, ex );

					System.out.println( msg );

					throw new Exception( msg, ex );
				}
			}

			if ( ! sleepTimeCheckingForNewJobsNoWorkerThreadsStringSet ) {

				int sleepTimeCheckingForNewJobs = clientConfigDTO.getSleepTimeCheckingForNewJobs();

				int sleepTimeCheckingForNewJobsNoWorkerThreads
						= sleepTimeCheckingForNewJobs * ClientConstants.MULTIPLE_FOR_COMPUTING_TIME_CHECKING_WITH_NO_WORKER_THREADS;


				String msg = "Config entry \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_SLEEP_TIME_CHECKING_FOR_NEW_JOBS_NO_WORKER_THREADS + "\" not set, using default of " + sleepTimeCheckingForNewJobsNoWorkerThreads;

				log.info( msg );

				clientConfigDTO.setSleepTimeCheckingForNewJobsNoWorkerThreads( sleepTimeCheckingForNewJobsNoWorkerThreads );
			}

			String delayFromJobsubmissionString = configProps.getProperty( ClientConfigPropertyNamesAndOtherConstants.CONFIG_DELAY_FROM_JOB_SUBMISSION_TIMESTAMP );
					
			if ( StringUtils.isNotEmpty( delayFromJobsubmissionString) ) {
				try {
					Integer delayFromJobsubmission = Integer.parseInt( delayFromJobsubmissionString );
					clientConfigDTO.setDelayFromJobsubmission( delayFromJobsubmission );
					
				} catch ( Exception ex ) {
					String msg = "Exception:  configProps.getProperty(\"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_DELAY_FROM_JOB_SUBMISSION_TIMESTAMP + "\") = " 
							+ delayFromJobsubmissionString;
					log.error( msg, ex );
					System.out.println( msg );
					throw new Exception( msg, ex );
				}

			}


			String sleepTimeCheckingControlFileString = configProps.getProperty( ClientConfigPropertyNamesAndOtherConstants.CONFIG_SLEEP_TIME_CHECKING_CONTROL_FILE );

			try {

				int sleepTimeCheckingControlFile = Integer.parseInt( sleepTimeCheckingControlFileString );

				if ( sleepTimeCheckingControlFile < 1 ) {

					String msg = "Configuration error: Property \"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_SLEEP_TIME_CHECKING_CONTROL_FILE + "\") cannot be less than '1', it is = " + sleepTimeCheckingControlFileString;

					log.error( msg );

					System.out.println( msg );

					throw new Exception( msg );
				}


				clientConfigDTO.setSleepTimeCheckingControlFile( sleepTimeCheckingControlFile );

			} catch ( Exception ex ) {


				String msg = "Exception:  configProps.getProperty(\"" + ClientConfigPropertyNamesAndOtherConstants.CONFIG_SLEEP_TIME_CHECKING_CONTROL_FILE + "\") = " + sleepTimeCheckingForNewJobsString;

				log.error( msg, ex );

				System.out.println( msg );

				throw new Exception( msg, ex );
			}







			String loadModulesOnStartupString = configProps.getProperty( ClientConfigPropertyNamesAndOtherConstants.CONFIG_LOAD_MODULES_ON_STARTUP );

			boolean loadModulesOnStartup = false;  // default to false

			if ( "true".equalsIgnoreCase( loadModulesOnStartupString ) ) {

				loadModulesOnStartup = true;
			}

			clientConfigDTO.setLoadModulesOnStartup( loadModulesOnStartup );


		}
		
		log.warn( "Client Main Config load complete:"
				+ "\n ClientNodeName: " + clientConfigDTO.getClientNodeName()
				+ "\n maxConcurrentJobs: " + clientConfigDTO.getMaxConcurrentJobs()
				+ "\n maxThreadsForModules: " + clientConfigDTO.getMaxThreadsForModules()
				+ "\n sleepTimeCheckingForNewJobs: " + clientConfigDTO.getSleepTimeCheckingForNewJobs()
				+ "\n sleepTimeCheckingForNewJobsNoWorkerThreads: " + clientConfigDTO.getSleepTimeCheckingForNewJobsNoWorkerThreads()
				+ "\n delayFromJobsubmission: " + clientConfigDTO.getDelayFromJobsubmission()
				+ "\n sleepTimeCheckingControlFile: " + clientConfigDTO.getSleepTimeCheckingControlFile()
				+ "\n loadModulesOnStartup: " + clientConfigDTO.isLoadModulesOnStartup()
				);
		

		RetrieveModuleConfig retrieveModuleConfig = new RetrieveModuleConfig();

		List<ModuleConfigDTO> configAllModules = retrieveModuleConfig.getConfigAllModules();

		clientConfigDTO.setModuleConfigList( configAllModules );

		if ( clientConfigDTO.isLoadModulesOnStartup() ) {

			ModuleFactory.getInstance().loadModulesForAllModuleConfig( clientConfigDTO );
		}
	}


}
