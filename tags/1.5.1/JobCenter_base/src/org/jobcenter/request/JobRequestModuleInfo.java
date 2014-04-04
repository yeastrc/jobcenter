package org.jobcenter.request;

/**
 * information about a module on the client available to do work
 *
 */
public class JobRequestModuleInfo {

	private String moduleName;
	private int moduleVersion;


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
}
