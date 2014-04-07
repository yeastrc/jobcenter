package org.jobcenter.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/**
 * For testing only
 *
 */
public class GetResourceDirectoryContents {

	private static Logger log = Logger.getLogger(GetResourceDirectoryContents.class);


	  /**
	   * List directory contents for a resource folder. Not recursive.
	   * This is basically a brute-force implementation.
	   * Works for regular files and also JARs.
	   *
	   * @author Greg Briggs
	   * @param clazz Any java class that lives in the same place as the resources you want.
	   * @param path Should end with "/", but not start with one.
	   * @return Just the name of each member item, not the full paths.
	   * @throws URISyntaxException
	   * @throws IOException
	   */
	  public static String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {

		  ClassLoader classLoader = clazz.getClassLoader();

	      URL dirURL = clazz.getClassLoader().getResource(path);



	      if (dirURL != null && dirURL.getProtocol().equals("file")) {
	        /* A file path: easy enough */

		      System.out.println( "dirURL.getAuthority(); = |" + dirURL.getAuthority() + "|." );


		      System.out.println( "dirURL.getPath(); = |" + dirURL.getPath() + "|." );



	    	  String[] resourceListing = new File(dirURL.toURI()).list();

	    	  printList( resourceListing, path );


	    	  return resourceListing;
	      }

	      if (dirURL == null) {
	        /*
	         * In case of a jar file, we can't actually find a directory.
	         * Have to assume the same jar as clazz.
	         */
	        String me = clazz.getName().replace(".", "/")+".class";
	        dirURL = clazz.getClassLoader().getResource(me);
	      }

	      log.info( "after dirURL == null and reset dirURL: dirURL.getAuthority(); = |" + dirURL.getAuthority() + "|." );


	      log.info( "after dirURL == null and reset dirURL: dirURL.getPath(); = |" + dirURL.getPath() + "|." );


	      if (dirURL.getProtocol().equals("jar")) {
	        /* A JAR path */
	        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
	        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
	        Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
	        Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
	        while(entries.hasMoreElements()) {
	          String name = entries.nextElement().getName();
	          if (name.startsWith(path)) { //filter according to the path
	            String entry = name.substring(path.length());
	            int checkSubdir = entry.indexOf("/");
	            if (checkSubdir >= 0) {
	              // if it is a subdirectory, we just return the directory name
	              entry = entry.substring(0, checkSubdir);
	            }
	            result.add(entry);
	          }
	        }

	        String[] resourceListing = result.toArray(new String[result.size()]);

	        printList( resourceListing, path );


	        return resourceListing;
	      }

	      throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
	  }

	  /**
	 * @param result
	 * @param path
	 */
	private static void printList( String[] result, String path ) {


	        log.info( "Resource listing for path = |" + path + "|." );

	        for ( String element : result ) {

	        	log.info( element );
	        }
	  }
}
