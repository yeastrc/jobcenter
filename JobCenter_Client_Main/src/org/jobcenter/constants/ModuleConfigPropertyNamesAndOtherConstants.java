package org.jobcenter.constants;

public class ModuleConfigPropertyNamesAndOtherConstants {
	
	/////////////////////
	
	///   Properties read from config file ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_MODULE
	
	//          These are all required
	
	public static final String CONFIG_PROPERTY_MODULE_NAME = "module.name";

	public static final String CONFIG_PROPERTY_MODULE_JAVA_CLASS = "module.java.class";

	public static final String CONFIG_PROPERTY_MODULE_VERSION_NUMBER = "module.version.number";


	public static final String CONFIG_PROPERTY_MODULE_DEFAULT_MAX_THREADS_PER_JOB = "module.default.max.threads.per.job";

	public static final String CONFIG_PROPERTY_MODULE_DEFAULT_MIN_THREADS_PER_JOB = "module.default.min.threads.per.job";

	
	/////////////////////

	///   Properties read from config file ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_CLIENT

	public static final String CONFIG_PROPERTY_MODULE_MAX_CONCURRENT_JOBS_PER_CLIENT = "module.max.concurrent.jobs.per.client";

	public static final String CONFIG_PROPERTY_MODULE_MAX_THREADS_PER_JOB = "module.max.threads.per.job";

	public static final String CONFIG_PROPERTY_MODULE_MIN_THREADS_PER_JOB = "module.min.threads.per.job";


	//////////////////////////////
	
	//   general constants
	
	//   character used for testing for unlimited max threads.  The string is first converted to lower case
	
	public static final String UNLIMITED_MAX_THREADS_FIRST_CHARACTER_LOWER_CASE = "u";
}
