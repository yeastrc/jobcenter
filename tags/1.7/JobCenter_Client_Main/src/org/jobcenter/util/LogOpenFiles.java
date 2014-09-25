package org.jobcenter.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;

import org.apache.log4j.Logger;

/**
 *
 *
 */
public class LogOpenFiles {

	private static Logger log = Logger.getLogger(LogOpenFiles.class);


	public static final boolean LIST_FILES_TRUE = true;
	public static final boolean LIST_FILES_FALSE = false;

	/**
	 * @return
	 */
	private static int pid() {
		String id = ManagementFactory.getRuntimeMXBean().getName();
		String[] ids = id.split("@");
		return Integer.parseInt(ids[0]);
	}


	/**
	 * Create log entry with list of open files
	 */
	public static void logOpenFiles( boolean listFiles ) {

		return;

//		if ( OperatingSystemDetection.isWindows() ) {
//
//			log.info( "Unable to show open files with this approach since running on Windows." );
//
//		} else {
//
//
//			InputStream is = null;
//			InputStreamReader isr = null;
//			BufferedReader reader = null;
//
//			InputStream inStreamErr = null;
//			OutputStream outStrToProcess = null;
//
//			try {
//				int pid = pid();
//				Runtime runtime = Runtime.getRuntime();
//				Process process = runtime.exec("lsof -p " + pid);
//				is = process.getInputStream();
//				inStreamErr = process.getErrorStream();
//				outStrToProcess = process.getOutputStream();
//				isr = new InputStreamReader(is);
//				reader = new BufferedReader(isr);
//				String line;
//
//				StringBuilder openFilesSB = new StringBuilder( 1000 );
//
//				openFilesSB.append( "The list of open files: \n" );
//
//				int fileCount = 0;
//				int slashDataCtr = 0;
//				int jarCtr = 0;
//				int soCtr = 0;
//				int logCtr = 0;
//				int slashDevCtr = 0;
//				int cannotIdentifyProtocolCtr = 0;
//				int pipeCtr = 0;
//
//				while ((line = reader.readLine()) != null) {
//
//					fileCount++;
//
//					if ( line.contains( "/data/" ) ) {
//
//						slashDataCtr++;
//					}
//
//					if ( line.endsWith( ".jar" ) ) {
//
//						jarCtr++;
//					}
//
//					if ( line.endsWith( ".so" ) ) {
//
//						soCtr++;
//					}
//
//
//					if ( line.endsWith( ".log" ) ) {
//
//						logCtr++;
//					}
//
//
//					if ( line.contains( "/dev/" ) ) {
//
//						slashDevCtr++;
//					}
//
//
//					if ( line.contains( "can't identify protocol" ) ) {
//
//						cannotIdentifyProtocolCtr++;
//					}
//
//
//					if ( line.endsWith( "pipe" ) ) {
//
//						pipeCtr++;
//					}
//
//
//
//
//					openFilesSB.append( "Open File: \t" );
//
//					openFilesSB.append( line );
//					openFilesSB.append( "\n" );
//				}
//
//				String openFiles = openFilesSB.toString();
//
//				log.info( "Number of open files = " + fileCount
//						+ "\t'/data/' files = \t" + slashDataCtr
//						+ "\t'.jar' files = \t" + jarCtr
//						+ "\t'.so' files = \t" + soCtr
//						+ "\t'.log' files = \t" + logCtr
//						+ "\t'/dev/' files = \t" + slashDevCtr
//						+ "\t'can't identify protocol' files = \t" + cannotIdentifyProtocolCtr
//						+ "\t'pipe' files = \t" + pipeCtr );
//
//				if ( listFiles ) {
//
//					log.debug( openFiles );
//
//				}
//
//
//			} catch ( Throwable t ) {
//
//				log.error( "Exception in listOpenFiles(): ", t );
//
//
//				//  TODO   handle and/or log exception
//
//
//			} finally {
//
//				if ( reader != null ) {
//
//					try {
//						reader.close();
//					} catch ( Throwable t ) {
//					}
//					reader = null;
//				}
//
//				if ( isr != null ) {
//
//					try {
//						isr.close();
//					} catch ( Throwable t ) {
//					}
//					isr = null;
//				}
//
//				if ( is != null ) {
//					try {
//						is.close();
//					} catch ( Throwable t ) {
//					}
//					is = null;
//				}
//
//				if ( inStreamErr != null ) {
//					try {
//						inStreamErr.close();
//					} catch ( Throwable t ) {
//					}
//					inStreamErr = null;
//				}
//
//
//				if ( outStrToProcess != null ) {
//					try {
//						outStrToProcess.close();
//					} catch ( Throwable t ) {
//					}
//					outStrToProcess = null;
//				}
//
//			}
//
//		}

	}

}
