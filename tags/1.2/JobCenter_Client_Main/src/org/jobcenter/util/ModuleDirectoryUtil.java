package org.jobcenter.util;

public class ModuleDirectoryUtil {

	/**
	 * @param moduleDirectoryName
	 * @return - true if this directory should be excluded from module processing, false otherwise
	 * @throws Throwable
	 */
	public static boolean excludeModuleDirectory( String moduleDirectoryName ) throws Throwable {
		
		if ( "META-INF".equalsIgnoreCase( moduleDirectoryName ) ) {
			
			return true;
		}
		
		return false;
	}
	
}
