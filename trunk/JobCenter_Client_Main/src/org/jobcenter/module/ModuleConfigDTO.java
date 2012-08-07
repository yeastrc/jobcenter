package org.jobcenter.module;

public class ModuleConfigDTO {

	private int moduleInstanceCount = 0;

	/**
	 * Primary key to this bean
	 */
	private String moduleSubDirectory;

	/**
	 * Max number of concurrent jobs for this module ( based on the subdirectory for this module )
	 */
	private int maxNumberConcurrentJobs;


	/**
	 * is the max number of concurrent jobs set?
	 */
	private boolean maxNumberConcurrentJobsSet;

	/**
	 * Max number of threads per jobs for this module ( based on the subdirectory for this module )
	 */
	private int maxNumberThreadsPerJob;

	/**
	 * is the Max number of threads per jobs set to unlimited?
	 */
	private boolean maxNumberThreadsPerJobUnlimited;

	/**
	 * is the Max number of threads per jobs set?
	 */
	private boolean maxNumberThreadsPerJobSet;

	/**
	 * min number of threads per jobs for this module ( based on the subdirectory for this module )
	 */
	private int minNumberThreadsPerJob;


	/**
	 * is the min number of threads per jobs set?
	 */
	private boolean minNumberThreadsPerJobSet;



	/**
	 * Read from properties file "jobcentermoduleconfig.properties" in the module
	 */
	private String moduleName;

	/**
	 * Read from properties file "jobcentermoduleconfig.properties" in the module
	 */
	private int moduleVersion;

	/**
	 *   class in the module that is the "launch point" and has implemented the interface "org.jobcenter.job_module_interface.ModuleInterfaceClientMainInterface"
	 */
	private String moduleJavaClass;



	/**
	 * Has the module failed to load or init
	 */
	private boolean moduleFailedToLoadOrInit = false;



	@Override
	public String toString() {
		return "ModuleConfigDTO [maxNumberConcurrentJobs="
				+ maxNumberConcurrentJobs + ", maxNumberConcurrentJobsSet="
				+ maxNumberConcurrentJobsSet + ", maxNumberThreadsPerJob="
				+ maxNumberThreadsPerJob + ", maxNumberThreadsPerJobSet="
				+ maxNumberThreadsPerJobSet
				+ ", maxNumberThreadsPerJobUnlimited="
				+ maxNumberThreadsPerJobUnlimited + ", minNumberThreadsPerJob="
				+ minNumberThreadsPerJob + ", minNumberThreadsPerJobSet="
				+ minNumberThreadsPerJobSet + ", moduleFailedToLoadOrInit="
				+ moduleFailedToLoadOrInit + ", moduleInstanceCount="
				+ moduleInstanceCount + ", moduleJavaClass=" + moduleJavaClass
				+ ", moduleName=" + moduleName + ", moduleSubDirectory="
				+ moduleSubDirectory + ", moduleVersion=" + moduleVersion + "]";
	}


	public String getModuleJavaClass() {
		return moduleJavaClass;
	}

	public void setModuleJavaClass(String moduleJavaClass) {
		this.moduleJavaClass = moduleJavaClass;
	}

	public String getModuleSubDirectory() {
		return moduleSubDirectory;
	}

	public void setModuleSubDirectory(String moduleSubDirectory) {
		this.moduleSubDirectory = moduleSubDirectory;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public int getModuleVersion() {
		return moduleVersion;
	}

	public void setModuleVersion(int moduleVersion) {
		this.moduleVersion = moduleVersion;
	}

	public boolean isModuleFailedToLoadOrInit() {
		return moduleFailedToLoadOrInit;
	}

	public void setModuleFailedToLoadOrInit(boolean moduleFailedToLoadOrInit) {
		this.moduleFailedToLoadOrInit = moduleFailedToLoadOrInit;
	}

	public int getModuleInstanceCount() {
		return moduleInstanceCount;
	}

	public void setModuleInstanceCount(int moduleInstanceCount) {
		this.moduleInstanceCount = moduleInstanceCount;
	}

	public int getMaxNumberConcurrentJobs() {
		return maxNumberConcurrentJobs;
	}

	public void setMaxNumberConcurrentJobs(int maxNumberConcurrentJobs) {
		this.maxNumberConcurrentJobs = maxNumberConcurrentJobs;
	}

	public boolean isMaxNumberConcurrentJobsSet() {
		return maxNumberConcurrentJobsSet;
	}

	public void setMaxNumberConcurrentJobsSet(boolean maxNumberConcurrentJobsSet) {
		this.maxNumberConcurrentJobsSet = maxNumberConcurrentJobsSet;
	}

	public int getMaxNumberThreadsPerJob() {
		return maxNumberThreadsPerJob;
	}

	public void setMaxNumberThreadsPerJob(int maxNumberThreadsPerJob) {
		this.maxNumberThreadsPerJob = maxNumberThreadsPerJob;
	}

	public int getMinNumberThreadsPerJob() {
		return minNumberThreadsPerJob;
	}

	public void setMinNumberThreadsPerJob(int minNumberThreadsPerJob) {
		this.minNumberThreadsPerJob = minNumberThreadsPerJob;
	}

	public boolean isMaxNumberThreadsPerJobSet() {
		return maxNumberThreadsPerJobSet;
	}

	public void setMaxNumberThreadsPerJobSet(boolean maxNumberThreadsPerJobSet) {
		this.maxNumberThreadsPerJobSet = maxNumberThreadsPerJobSet;
	}

	public boolean isMinNumberThreadsPerJobSet() {
		return minNumberThreadsPerJobSet;
	}

	public void setMinNumberThreadsPerJobSet(boolean minNumberThreadsPerJobSet) {
		this.minNumberThreadsPerJobSet = minNumberThreadsPerJobSet;
	}

	public boolean isMaxNumberThreadsPerJobUnlimited() {
		return maxNumberThreadsPerJobUnlimited;
	}

	public void setMaxNumberThreadsPerJobUnlimited(
			boolean maxNumberThreadsPerJobUnlimited) {
		this.maxNumberThreadsPerJobUnlimited = maxNumberThreadsPerJobUnlimited;
	}


}
