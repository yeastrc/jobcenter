package org.jobcenter.constants;

/**
 * The system properties that are set before the module is loaded
 *
 */
public interface ModuleSystemPropertiesAtLoad {

	
	/**
	 * This is used so that each loaded instance of a module running in a client writes to 
	 * a separate log file.  This separation makes it easier to track what each instance 
	 * is doing since the log statements are not intertwined. 
	 * 
	 * put the following in the log4j file to add the instance number to the logging filename:
	 * 
	 *    ${module.instance.count}
	 *    
	 *  example:
	 *  
	 *   <rollingPolicy class="org.apache.log4j.rolling.TimeBasedRollingPolicy">
	 *      <param name="FileNamePattern" 
	 *        value="logs/Philius_Create_Response_To_Submitter_instance_${module.instance.count}.%d.log" />
	 *   </rollingPolicy>
	 */
	public static final String MODULE_INSTANCE_COUNT = "module.instance.count";
	
	
	
}
