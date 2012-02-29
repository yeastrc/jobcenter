package org.jobcenter.module;

public class ModuleConfigDTO {

	private int moduleInstanceCount = 0;

	/**
	 * Primary key to this bean
	 */
	private String moduleSubDirectory;

	/**
	 * Max number of threads executing tasks for this module ( based on the subdirectory for this module )
	 */
	private int maxNumberThreads;


	/**
	 * is the max number of theads set?
	 */
	private boolean maxNumberThreadsSet;


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

	public int getMaxNumberThreads() {
		return maxNumberThreads;
	}

	public void setMaxNumberThreads(int maxNumberThreads) {
		this.maxNumberThreads = maxNumberThreads;
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

	public boolean isMaxNumberThreadsSet() {
		return maxNumberThreadsSet;
	}

	public void setMaxNumberThreadsSet(boolean maxNumberThreadsSet) {
		this.maxNumberThreadsSet = maxNumberThreadsSet;
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


}
