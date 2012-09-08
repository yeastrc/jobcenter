package org.jobcenter.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class BuildURLClassLoader {

	private static final String CONFIG_DIR = "config/";




	private static Logger log = Logger.getLogger(BuildURLClassLoader.class);



	/**
	 * The class loader for JobCenter_Root
	 */
	private static ClassLoader rootClassLoader;



	/**
	 * Get a ClassLoader based on the defined rules.
	 *
	 * The resulting class path will be <baseDirectory>/config; jars in <baseDirectory>/lib; jars in <baseDirectory>/main
	 *
	 * The parent class loader will be the class loader for this class
	 *
	 * @param baseDirectory
	 * @return
	 */
	public ClassLoader getClassLoaderForBaseDirectoryThisClassLoaderAsParent( String baseDirectory )  throws Throwable {

		return getClassLoaderForBaseDirectory( baseDirectory, this.getClass().getClassLoader() );
	}


	/**
	 * Get a ClassLoader based on the defined rules.
	 *
	 * The resulting class path will be <baseDirectory>/config; jars in <baseDirectory>/lib; jars in <baseDirectory>/main
	 *
	 * The parent class loader will be the System class loader
	 *
	 * @param baseDirectory
	 * @return
	 */
	public ClassLoader getClassLoaderForBaseDirectorySystemClassLoaderAsParent( String baseDirectory )  throws Throwable {

		return getClassLoaderForBaseDirectory( baseDirectory, ClassLoader.getSystemClassLoader() );


	}

	/**
	 * Get a URLClassLoader based on the defined rules.
	 *
	 * The resulting class path will be <baseDirectory>/config; jars in <baseDirectory>/lib; jars in <baseDirectory>/main
	 * @param baseDirectory
	 * @return
	 */
	private ClassLoader getClassLoaderForBaseDirectory( String baseDirectory, ClassLoader parentClassLoader )  throws Throwable {

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

			String msg =  "Configdir directory '" + configDirFileObj.getAbsolutePath() + "' does not exist" ;

			System.out.println( msg );

			log.error( msg );

		} else if ( ! baseDirectoryFileObject.isDirectory() ) {

			String msg = "Configdir directory '" + configDirFileObj.getAbsolutePath() + "' is not a directory";

			System.out.println( msg );

			log.error( msg );

		} else {

			URL configDirURL = configDirFileObj.toURI().toURL();

			pathElements.add( configDirURL );
		}


		int foundMainJarsCount = addJarsForPath( pathElements, baseDirectory, "main_jar" );

		if ( foundMainJarsCount == 0 ) {

			throw new IllegalArgumentException( "No jar files found in directory " + baseDirectory + "/main_jar" );
		}


		@SuppressWarnings("unused")
		int foundLibJarsCount = addJarsForPath( pathElements, baseDirectory, "lib" );



		URL[] urls = new URL[ pathElements.size() ];

		int index = 0;

		for ( URL pathElement : pathElements ) {

			urls[ index ] = pathElement;

			index++;
		}

//		URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls, this.getClass().getClassLoader());

		URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls, parentClassLoader ); // ClassLoader.getSystemClassLoader() ); // rootClassLoader

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

			throw new IllegalArgumentException( "Sub directory '" + baseDirectory + File.separator +  subDirectory + "' does not exist" );
		}

		if ( ! subDir.isDirectory() ) {

			throw new IllegalArgumentException( "Sub directory '" + baseDirectory + File.separator + subDirectory + "' is not a directory" );
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


	public static ClassLoader getRootClassLoader() {
		return rootClassLoader;
	}


	public static void setRootClassLoader(ClassLoader rootClassLoader) {
		BuildURLClassLoader.rootClassLoader = rootClassLoader;
	}
}
