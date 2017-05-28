package org.jobcenter.processjob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jobcenter.config.ClientConfigDTO;
import org.jobcenter.module.ModuleConfigDTO;
import org.jobcenter.module.ModuleHolder;
import org.jobcenter.request.JobRequest;
import org.jobcenter.request.JobRequestModuleInfo;
import org.jobcenter.response.BaseResponse;
import org.jobcenter.response.JobResponse;
import org.jobcenter.serverinterface.ServerConnection;

/**
 * Singleton for calling server interface
 *
 * uses private class ModuleTracker for tracking how many threads each module is currently in use on.
 *
 */
public class GetJob {


	private static Logger log = Logger.getLogger(GetJob.class);

	private static GetJob instance = new GetJob();


	/**
	 * private constructor
	 */
	private GetJob() {

	}

	/**
	 * @return singleton
	 */
	public static GetJob getInstance()  {

		return instance;
	}



	/**
	 * @return
	 * @throws Throwable
	 */
	public JobResponse getNextJob( int availableThreadCount, int availableJobCount )  throws Throwable {

		log.debug( "getNextJob( ) called." );

		//   TODO    !!!!!!!!!!  for now code for one version per module, enhance later


		Map< String, ModuleTracker > modulesInUse = new HashMap<String,ModuleTracker>();

		//  load map with configuration

		List<ModuleConfigDTO> moduleConfigList = ClientConfigDTO.getSingletonInstance().getModuleConfigList();

		for ( ModuleConfigDTO configDTOModuleInList : moduleConfigList ) {

			String moduleSubDirectory = configDTOModuleInList.getModuleSubDirectory();

			ModuleTracker moduleTracker = new ModuleTracker();

			moduleTracker.setModuleConfigDTO( configDTOModuleInList );

			moduleTracker.setConcurrentJobsAvailableCount( configDTOModuleInList.getMaxNumberConcurrentJobs() );

			modulesInUse.put( moduleSubDirectory, moduleTracker );
		}




		List<JobRunnerThread> jobRunnerThreads = ThreadsHolderSingleton.getInstance().getJobRunnerThreads();


		//  get info on modules currently assigned to worker threads

		for ( JobRunnerThread jobRunnerThread : jobRunnerThreads ) {

			ModuleHolder moduleHolder = jobRunnerThread.getModuleHolder();

			if ( moduleHolder != null ) {

				ModuleConfigDTO moduleConfigDTO = moduleHolder.getModuleConfigDTO();

				String moduleSubDirectory = moduleConfigDTO.getModuleSubDirectory();

				ModuleTracker moduleTracker = modulesInUse.get( moduleSubDirectory );

				if ( moduleTracker == null ) {

//					moduleTracker = new ModuleTracker();
//
//					moduleTracker.setModuleConfigDTO( moduleConfigDTO );
//					moduleTracker.setInUseCount( 1 );

				} else {

					if ( moduleConfigDTO.isMaxNumberConcurrentJobsSet() ) {

						moduleTracker.setConcurrentJobsAvailableCount( moduleTracker.getConcurrentJobsAvailableCount() - 1 );
					}
				}
			}

		}


		return getNextJobInternal( modulesInUse, availableThreadCount, availableJobCount );
	}

	/**
	 * @param modulesInUse
	 * @param availableThreadCount
	 * @return
	 * @throws Throwable
	 */
	private JobResponse getNextJobInternal( Map<String, ModuleTracker> modulesInUse, int availableThreadCount, int availableJobCount )
	throws Throwable {


		JobRequest jobRequest = new JobRequest();

		String nodeName = ClientConfigDTO.getSingletonInstance().getClientNodeName();
		int totalNumberThreadsConfiguredOnClient = ClientConfigDTO.getSingletonInstance().getMaxThreadsForModules();
		int totalNumberJobsConfiguredOnClient =ClientConfigDTO.getSingletonInstance().getMaxConcurrentJobs(); 
		Integer delayFromJobsubmission = ClientConfigDTO.getSingletonInstance().getDelayFromJobsubmission();
		
		jobRequest.setNodeName( nodeName );
		
		jobRequest.setNumberThreadsAvailableOnClient( availableThreadCount );
		jobRequest.setNumberJobsAvailableOnClient( availableJobCount );
		
		jobRequest.setTotalNumberJobsConfiguredOnClient( totalNumberJobsConfiguredOnClient );
		jobRequest.setTotalNumberThreadsConfiguredOnClient( totalNumberThreadsConfiguredOnClient );
		
		jobRequest.setDelayFromJobsubmission( delayFromJobsubmission );
		

		List<JobRequestModuleInfo> clientModules = new ArrayList<JobRequestModuleInfo>();

		jobRequest.setClientModules( clientModules );

		JobRequestModuleInfo jobRequestModuleInfo = null;

		//  process through map and produce list of modules ( also from a point of max number of threads for the module )
		//        available to process jobs


		for ( Map.Entry<String, ModuleTracker> entry : modulesInUse.entrySet() ) {

			ModuleTracker moduleTracker = entry.getValue();

			ModuleConfigDTO moduleConfigDTO =  moduleTracker.getModuleConfigDTO();

//			boolean notIsModuleFailedToLoadOrInit = ! moduleConfigDTO.isModuleFailedToLoadOrInit();
//
//			boolean notIsMaxNumberConcurrentJobsSet = ! moduleConfigDTO.isMaxNumberConcurrentJobsSet();
//
//			boolean concurrentJobsAvailableCountGTZero = moduleTracker.concurrentJobsAvailableCount > 0;
//
//			boolean conCurrTest = ( ( ! moduleConfigDTO.isMaxNumberConcurrentJobsSet() ) || moduleTracker.concurrentJobsAvailableCount > 0 );
//
//			boolean isMinNumberThreadsPerJobSet = moduleConfigDTO.isMinNumberThreadsPerJobSet();
//
//			boolean MinNumberThreadsPerJobLEAvTC = moduleConfigDTO.getMinNumberThreadsPerJob() <= availableThreadCount;
//
//			boolean MinTtoAvTCtest = ! moduleConfigDTO.isMinNumberThreadsPerJobSet() || moduleConfigDTO.getMinNumberThreadsPerJob() <= availableThreadCount;

			if ( ( ! moduleConfigDTO.isModuleFailedToLoadOrInit() ) // bypass modules that failed to load or init()
					//              if the max number of concurrent jobs is set on the module, the available count must be > zero
					&& ( ( ! moduleConfigDTO.isMaxNumberConcurrentJobsSet() )
							|| moduleTracker.concurrentJobsAvailableCount > 0
						)
						//  If the minimumThreads per Job is set for a given module,
						//  only accept it where the minimumThreads per Job is <= the available threads
					&& ( ( ! moduleConfigDTO.isMinNumberThreadsPerJobSet() )
							|| moduleConfigDTO.getMinNumberThreadsPerJob() <= availableThreadCount
						)

				)
			{

				jobRequestModuleInfo = new JobRequestModuleInfo();

				jobRequestModuleInfo.setModuleName( moduleConfigDTO.getModuleName() );
				jobRequestModuleInfo.setModuleVersion( moduleConfigDTO.getModuleVersion() );

				clientModules.add( jobRequestModuleInfo );

			}
		}

		JobResponse jobResponse = null;

		//  Skip call to server if no modules found that can run on another thread

		if ( ! clientModules.isEmpty() ) {

			logModules( clientModules );

			jobResponse = ServerConnection.getInstance().getNextJob( jobRequest );

		} else {

			log.debug( "Skipping calling the server since no modules are available to run the next job.  Possibly all modules are at max threads." );

			jobResponse = new JobResponse();

			jobResponse.setJobFound( false );

			jobResponse.setErrorCode( BaseResponse.ERROR_CODE_NO_ERRORS );

			jobResponse.setErrorResponse( false );
		}

		return jobResponse;
	}



	/**
	 * logs the configuration loaded from the modules:
	 *
	 * @param configAllModules
	 */
	private void logModules( List<JobRequestModuleInfo> clientModules ) throws Throwable {

		if( log.isDebugEnabled() ) {

			log.debug( "Calling the server to get the next job using the following modules:" );

			for ( JobRequestModuleInfo clientModule : clientModules  ) {

				log.debug( "Module name: '" + clientModule.getModuleName()
						+ "', Module Version: '" + clientModule.getModuleVersion() );
			}
		}
	}



	/**
	 *
	 *
	 */
	public void destroy() {

		log.debug( "destroy() called." );

	}


	/**
	 *
	 *
	 */
	private class ModuleTracker {

		ModuleConfigDTO moduleConfigDTO;

		int concurrentJobsAvailableCount;

		public ModuleConfigDTO getModuleConfigDTO() {
			return moduleConfigDTO;
		}

		public void setModuleConfigDTO(ModuleConfigDTO moduleConfigDTO) {
			this.moduleConfigDTO = moduleConfigDTO;
		}

		public int getConcurrentJobsAvailableCount() {
			return concurrentJobsAvailableCount;
		}

		public void setConcurrentJobsAvailableCount(int concurrentJobsAvailableCount) {
			this.concurrentJobsAvailableCount = concurrentJobsAvailableCount;
		}


	}
}
