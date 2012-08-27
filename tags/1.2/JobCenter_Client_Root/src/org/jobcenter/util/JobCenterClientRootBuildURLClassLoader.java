package org.jobcenter.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class JobCenterClientRootBuildURLClassLoader {

	private static final String CONFIG_DIR = "config/";



	/**
	 * Get a URLClassLoader based on the defined rules.
	 *
	 * The resulting class path will be <baseDirectory>/config; jars in <baseDirectory>/lib; jars in <baseDirectory>/main
	 * @param baseDirectory
	 * @return
	 */
	public ClassLoader getURLClassLoaderForBaseDirectory( String baseDirectory )  throws Throwable {

		List<URL> pathElements = new ArrayList<URL>();

		File baseDirectoryFileObject = new File( baseDirectory );

		if ( ! baseDirectoryFileObject.exists() ) {

			throw new IllegalArgumentException( "Base directory '" + baseDirectory + "' does not exist" );
		}

		if ( ! baseDirectoryFileObject.isDirectory() ) {

			throw new IllegalArgumentException( "Base directory '" + baseDirectory + "' is not a directory" );
		}


		File configDirFileObj = new File( baseDirectory, CONFIG_DIR );

		if ( ! configDirFileObj.exists() ) {

			System.out.println( "Configdir directory '" + configDirFileObj.getAbsolutePath() + "' does not exist" );

		} else if ( ! baseDirectoryFileObject.isDirectory() ) {

			System.out.println( "Configdir directory '" + configDirFileObj.getAbsolutePath() + "' is not a directory" );

		} else {

			URL configDirURL = configDirFileObj.toURI().toURL();

			pathElements.add( configDirURL );
		}


		@SuppressWarnings("unused")
		int foundLibJarsCount = addJarsForPath( pathElements, baseDirectory, "lib" );

		int foundMainJarsCount = addJarsForPath( pathElements, baseDirectory, "main_jar" );

		if ( foundMainJarsCount == 0 ) {

			throw new IllegalArgumentException( "No jar files found in directory " + baseDirectory + "/main_jar" );
		}


		URL[] urls = new URL[ pathElements.size() ];

		int index = 0;

		for ( URL pathElement : pathElements ) {

			urls[ index ] = pathElement;

			index++;
		}

		URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls, this.getClass().getClassLoader());

		return urlClassLoader;
	}


	/**
	 * @param pathElements
	 * @param baseDirectory
	 * @param subDirectory
	 * @throws Throwable
	 */
	private int addJarsForPath( List<URL> pathElements, String baseDirectory, String subDirectory )  throws Throwable {

		int jarsFoundCount = 0;

		File subDir = new File( baseDirectory, subDirectory );

		if ( ! subDir.exists() ) {

			throw new IllegalArgumentException( "Sub directory '" + baseDirectory + subDirectory + "' does not exist" );
		}

		if ( ! subDir.isDirectory() ) {

			throw new IllegalArgumentException( "Sub directory '" + baseDirectory + subDirectory + "' is not a directory" );
		}

		File[] subDirContents = subDir.listFiles();

		for ( File subDirFile : subDirContents ) {

			if ( subDirFile.isFile() && subDirFile.getName().endsWith(".jar") ) {

				jarsFoundCount++;

				String jarFile = subDirFile.getAbsolutePath();

				URL jarURL = subDirFile.toURI().toURL();

				pathElements.add( jarURL );
			}
		}

		return jarsFoundCount;
	}
}
