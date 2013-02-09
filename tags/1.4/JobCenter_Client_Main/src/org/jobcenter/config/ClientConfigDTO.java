package org.jobcenter.config;

import java.util.List;

import org.jobcenter.module.ModuleConfigDTO;

/**
 * Singleton
 *
 */
public class ClientConfigDTO {

	private static ClientConfigDTO instance = new ClientConfigDTO();

	/**
	 * client node name for this instance of the Job Center Client
	 */
	private String clientNodeName;

	/**
	 * Max number of concurrent jobs running on modules
	 */
	private int maxConcurrentJobs;

	/**
	 * Max number of threads running tasks on modules
	 */
	private int maxThreadsForModules;

	/**
	 * Max sleep time to check for a new job in seconds.
	 *       this only applies if there is no new job when a job completes
	 */
	private int sleepTimeCheckingForNewJobs;

	/**
	 * Max sleep time to check for a new job in seconds.
	 *       this only applies if there is no worker threads when the get job thread wakes up
	 */
	private int sleepTimeCheckingForNewJobsNoWorkerThreads;

	/**
	 * Sleep time to check the control file
	 */
	private int sleepTimeCheckingControlFile;


	/**
	 * Load the modules and call "init()" on each when the JobCenter client starts up.
	 *       Defaults to false.
	 */
	private boolean loadModulesOnStartup = false;



	/**
	 * configuration for the modules
	 */
	private List<ModuleConfigDTO> moduleConfigList;





	/**
	 * private constuctor
	 */
	private ClientConfigDTO() {

	}

	/**
	 * @return singleton
	 */
	public static ClientConfigDTO getSingletonInstance() {

		return instance;
	}


	////////////

	public int getMaxThreadsForModules() {
		return maxThreadsForModules;
	}
	public void setMaxThreadsForModules(int maxThreadsForModules) {
		this.maxThreadsForModules = maxThreadsForModules;
	}

	public List<ModuleConfigDTO> getModuleConfigList() {
		return moduleConfigList;
	}
	public void setModuleConfigList(List<ModuleConfigDTO> moduleConfigList) {
		this.moduleConfigList = moduleConfigList;
	}
	public int getSleepTimeCheckingForNewJobs() {
		return sleepTimeCheckingForNewJobs;
	}
	public void setSleepTimeCheckingForNewJobs(int sleepTimeCheckingForNewJobs) {
		this.sleepTimeCheckingForNewJobs = sleepTimeCheckingForNewJobs;
	}

	public int getSleepTimeCheckingForNewJobsNoWorkerThreads() {
		return sleepTimeCheckingForNewJobsNoWorkerThreads;
	}

	public void setSleepTimeCheckingForNewJobsNoWorkerThreads(
			int sleepTimeCheckingForNewJobsNoWorkerThreads) {
		this.sleepTimeCheckingForNewJobsNoWorkerThreads = sleepTimeCheckingForNewJobsNoWorkerThreads;
	}

	public boolean isLoadModulesOnStartup() {
		return loadModulesOnStartup;
	}

	public void setLoadModulesOnStartup(boolean loadModulesOnStartup) {
		this.loadModulesOnStartup = loadModulesOnStartup;
	}

	public int getSleepTimeCheckingControlFile() {
		return sleepTimeCheckingControlFile;
	}

	public void setSleepTimeCheckingControlFile(int sleepTimeCheckingControlFile) {
		this.sleepTimeCheckingControlFile = sleepTimeCheckingControlFile;
	}

	public String getClientNodeName() {
		return clientNodeName;
	}

	public void setClientNodeName(String clientNodeName) {
		this.clientNodeName = clientNodeName;
	}

	public int getMaxConcurrentJobs() {
		return maxConcurrentJobs;
	}

	public void setMaxConcurrentJobs(int maxConcurrentJobs) {
		this.maxConcurrentJobs = maxConcurrentJobs;
	}
}
