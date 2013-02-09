package org.jobcenter.constants;

public interface ModuleConfigPropertyFileNames {

	//  Every module must have these properties files or the module will not be loaded.

	//  This properties file is expected to be in the module jar

	public static final String MODULE_CONFIG_PER_MODULE="jobcenter_module_config_per_module.properties";

	//  This properties file is expected to be in the moduledir/config directory and set per client.

	//       A copy of this properties file could be put in the module jar as a default to be read
	//       if this file does not exist in the moduledir/config directory.

	public static final String MODULE_CONFIG_PER_CLIENT="jobcenter_module_config_per_client.properties";

}
