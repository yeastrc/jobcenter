package org.jobcenter.module;

import org.apache.log4j.Logger;
import org.jobcenter.job_client_module_interface.ModuleInterfaceClientMainInterface;

/**
 * This will hold a module and its class loader
 *
 */
public class ModuleHolder {


	private static Logger log = Logger.getLogger(ModuleHolder.class);




	/**
	 * The module
	 */
	private ModuleInterfaceClientMainInterface module;

	/**
	 * The subdirectory the module is in is available in the moduleConfigDTO object
	 */

	/**
	 * The configuration object for the module
	 */
	private ModuleConfigDTO moduleConfigDTO;

	/**
	 * The class loader for the module
	 */
	private ClassLoader moduleClassLoader;






	/**
	 * default constuctor
	 */
	public ModuleHolder() {


	}




	public ModuleInterfaceClientMainInterface getModule() {
		return module;
	}

	public void setModule(ModuleInterfaceClientMainInterface module) {
		this.module = module;
	}

	public ClassLoader getModuleClassLoader() {
		return moduleClassLoader;
	}

	public void setModuleClassLoader(ClassLoader moduleClassLoader) {
		this.moduleClassLoader = moduleClassLoader;
	}

	public ModuleConfigDTO getModuleConfigDTO() {
		return moduleConfigDTO;
	}

	public void setModuleConfigDTO(ModuleConfigDTO moduleConfigDTO) {
		this.moduleConfigDTO = moduleConfigDTO;
	}

}
