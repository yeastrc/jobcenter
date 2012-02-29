package org.jobcenter.module;

import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.config.ClientConfigDTO;
import org.jobcenter.constants.ClientConstants;
import org.jobcenter.constants.ModuleSystemPropertiesAtLoad;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.JobType;
import org.jobcenter.job_client_module_interface.ModuleInterfaceClientMainInterface;
import org.jobcenter.util.BuildURLClassLoader;

public class ModuleFactory {


	private static Logger log = Logger.getLogger(ModuleFactory.class);

	private static ModuleFactory instance = new ModuleFactory();

	private InactiveModulePool inactiveModulePool = InactiveModulePool.getInstance();

	/**
	 * private constructor
	 */
	private ModuleFactory() {


	}

	public static ModuleFactory getInstance() {

		return instance;
	}



	/**
	 * Load a module for each moduleConfig
	 *
	 * @param clientConfigDTO
	 * @throws Throwable
	 */
	public void loadModulesForAllModuleConfig ( ClientConfigDTO clientConfigDTO ) throws Throwable {

		List<ModuleConfigDTO> configAllModules = clientConfigDTO.getModuleConfigList();

		for ( ModuleConfigDTO moduleConfigDTO : configAllModules ) {

			ModuleHolder moduleHolder = loadModule( moduleConfigDTO );

			inactiveModulePool.addModuleHolder( moduleHolder );
		}

	}



	/**
	 * @param job
	 * @return
	 * @throws Throwable
	 */
	public ModuleHolder getModuleForJob( Job job ) throws Throwable {

		ModuleHolder moduleHolder = null;

		ModuleConfigDTO moduleConfigDTO = getModuleConfigDTOForJob( job );

		//  Should not happen
		if ( moduleConfigDTO == null ) {

			String msg = "No moduleConfigDTO found for job";

			throw new Exception( msg );
		}

		moduleHolder = inactiveModulePool.pollModuleHolder( moduleConfigDTO.getModuleSubDirectory() );

		if ( moduleHolder == null ) {

			if ( moduleConfigDTO.isModuleFailedToLoadOrInit() ) {

				log.info( "Module previously failed to load or initialize, Name = " + moduleConfigDTO.getModuleName() + ", Subdir = " + moduleConfigDTO.getModuleSubDirectory() );

			} else {

				moduleHolder = loadModule( moduleConfigDTO );
			}
		}

		return moduleHolder;
	}


	/**
	 * @param job
	 * @return
	 */
	private ModuleConfigDTO getModuleConfigDTOForJob( Job job ) throws Throwable {


		ModuleConfigDTO moduleConfigDTO = null;

		JobType jobType = job.getJobType();

		if ( jobType == null ) {

			String msg = "JobType is null.  job.getId() = "
				+ job.getId() + ".";

			log.error( msg );

			throw new Exception( msg );
		}

		String jobModuleName = jobType.getModuleName();

		if ( jobModuleName == null ) {

			String msg = "jobModuleName is null.  job.getId() = "
				+ job.getId() + ".";

			log.error( msg );

			throw new Exception( msg );
		}

		List<ModuleConfigDTO> moduleConfigList = ClientConfigDTO.getSingletonInstance().getModuleConfigList();

		for ( ModuleConfigDTO configDTOModuleInList : moduleConfigList ) {

			if ( jobModuleName.equals( configDTOModuleInList.getModuleName() )  ) {

				if ( configDTOModuleInList.getModuleVersion() >= jobType.getMinimumModuleVersion() ) {

					if ( moduleConfigDTO == null || moduleConfigDTO.getModuleVersion() < configDTOModuleInList.getModuleVersion() ) {

						moduleConfigDTO = configDTOModuleInList;
					}
				}
			}
		}

		if ( moduleConfigDTO == null ) {

			String msg = "No moduleConfigDTO object found for job.  getModuleName().getName() = |"
				+ job.getJobType().getModuleName() + "|, jobType.getMinimumModuleVersion() = " + jobType.getMinimumModuleVersion() + ".";

			log.error( msg );

			throw new Exception( msg );
		}


		return moduleConfigDTO;

	}


	/**
	 * @param moduleConfigDTO
	 * @return
	 * @throws Throwable
	 */
	private ModuleHolder loadModule( ModuleConfigDTO moduleConfigDTO ) throws Throwable {

		String classToLaunch = moduleConfigDTO.getModuleJavaClass();


		String moduleBaseDirectory = ClientConstants.MODULE_BASE_LOCATION + "/" + moduleConfigDTO.getModuleSubDirectory();

		if ( log.isInfoEnabled() ) {

			log.info( "Loading module.  Name = '" + moduleConfigDTO.getModuleName() + "', subdir = '" + moduleConfigDTO.getModuleSubDirectory() + "', "
					+ "full path to subdir = '" + moduleBaseDirectory + "'.");
		}


		BuildURLClassLoader buildURLClassLoader = new BuildURLClassLoader();

		ClassLoader moduleInterfaceClassLoader = buildURLClassLoader.getClassLoaderForBaseDirectorySystemClassLoaderAsParent( moduleBaseDirectory );


		if ( log.isInfoEnabled() ) {
			log.info( "In ModuleFactory, about to load module classToLaunch: "
					+ " moduleInterfaceClassLoader = " + moduleInterfaceClassLoader
					+ ";  Current class loader: "
					+ this.getClass().getClassLoader()
					+ ";  Thread.currentThread().getContextClassLoader(): " + Thread.currentThread().getContextClassLoader() );
		}

		@SuppressWarnings("unchecked")
		Class<ModuleInterfaceClientMainInterface> classToRun  = (Class<ModuleInterfaceClientMainInterface>) moduleInterfaceClassLoader.loadClass( classToLaunch );


		ModuleInterfaceClientMainInterface moduleToRun = null;



		int moduleInstanceCount = moduleConfigDTO.getModuleInstanceCount();

		moduleInstanceCount += 1;


		try {


			System.setProperty( ModuleSystemPropertiesAtLoad.MODULE_INSTANCE_COUNT, Integer.toString( moduleInstanceCount ) );

		} catch ( Throwable t ) {

			String msg = "ModuleFactory:loadModule(...): Failed to set system property " + ModuleSystemPropertiesAtLoad.MODULE_INSTANCE_COUNT ;

			log.error( msg, t );

//			throw new Exception( msg, t );
		}


		try {

			moduleToRun = classToRun.newInstance();

		} catch ( Throwable t ) {

			moduleConfigDTO.setModuleFailedToLoadOrInit( true );

			String msg = "ModuleFactory:loadModule(...): Module name = '" + moduleConfigDTO.getModuleName()
			+ "', subdir = '" + moduleConfigDTO.getModuleSubDirectory()
			+ "', moduleBaseDirectory = '" + moduleBaseDirectory + "'."
			+ "' : Exception loading module class:  in call to classToRun.newInstance(): ";

			log.error( msg, t );

			throw new Exception( msg, t );
		}


		try {

			moduleConfigDTO.setModuleInstanceCount( moduleInstanceCount );

			ClassLoader threadCurrentContextClassLoader = Thread.currentThread().getContextClassLoader();


			try {
				//  Set context class loader to class loader for module


				log.info( "Setting current thread context class loader to class loader for module. moduleToRun.getClass().getClassLoader() = " + moduleToRun.getClass().getClassLoader() );

				log.info( "Setting current thread context class loader to class loader for module.  module sub directory " + moduleConfigDTO.getModuleSubDirectory() );

		        Thread.currentThread().setContextClassLoader( moduleToRun.getClass().getClassLoader() );

				if ( log.isInfoEnabled() ) {
					log.info( "In ModuleFactory, about to call init(...): Current class loader: "
							+ this.getClass().getClassLoader()
							+ "; Thread.currentThread().getContextClassLoader(): " + Thread.currentThread().getContextClassLoader() );
				}

		        moduleToRun.init( moduleInstanceCount );

			} finally {

				//  Set context class loader back to class loader for this class

				log.info( "Resetting current thread context class loader back to class loader before calling module.init(...)" );

		        Thread.currentThread().setContextClassLoader( threadCurrentContextClassLoader );

			}




		} catch ( Throwable t ) {

			moduleConfigDTO.setModuleFailedToLoadOrInit( true );

			String msg ="ModuleFactory:loadModule(...): Module name = '" + moduleConfigDTO.getModuleName()
					+ "', subdir = '" + moduleConfigDTO.getModuleSubDirectory()
					+ "', moduleBaseDirectory = '" + moduleBaseDirectory + "'."
					+ "' : Exception in call to moduleToRun.init(): ";

			log.error( msg, t );

			throw new Exception( msg, t );
		}

		try {

			moduleToRun.reset();

		} catch ( Throwable t ) {

			log.error( "ModuleFactory:loadModule(...):  Module subdir = '" + moduleConfigDTO.getModuleSubDirectory()
					+ "' : Exception in call to moduleToRun.init(): ", t );

			throw t;
		}



		ModuleHolder moduleHolder = new ModuleHolder();

		moduleHolder.setModuleClassLoader( moduleInterfaceClassLoader );


		moduleHolder.setModule( moduleToRun );

		moduleHolder.setModuleConfigDTO( moduleConfigDTO );

		return moduleHolder;

	}
}
