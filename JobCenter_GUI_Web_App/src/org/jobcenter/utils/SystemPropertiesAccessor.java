package org.jobcenter.utils;

import java.util.Properties;

import org.apache.log4j.Logger;

public class SystemPropertiesAccessor {

	private static Logger log = Logger.getLogger(SystemPropertiesAccessor.class);


	private static boolean isDevEnv = false;

	private static boolean isTestEnv = false;


	static {

		Properties prop = System.getProperties();


		String devEnv = prop.getProperty("devEnv");

		if ( "Y".equals(devEnv ) )
		{
			isDevEnv = true;
		}

		String testEnv = prop.getProperty("testEnv");

		if ( "Y".equals(testEnv ) )
		{
			isTestEnv = true;
		}
	}








	/**
	 * @return true if -DdevEnv=Y
	 */
	public static boolean isDevEnvironment()
	{
		return isDevEnv;
	}

	/**
	 * @return true if -DtestEnv=Y
	 */
	public static boolean isTestEnvironment()
	{
		return isTestEnv;
	}


}
