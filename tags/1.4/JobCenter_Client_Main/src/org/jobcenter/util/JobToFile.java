package org.jobcenter.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.jobcenter.constants.Constants;
import org.jobcenter.constants.ClientConstants;
import org.jobcenter.dto.Job;

/**
 *
 *
 */
/**
 *
 *
 */
public class JobToFile {

	private static Logger log = Logger.getLogger(JobToFile.class);


	private static final int JOBS_RECEIVED_COUNT_MAX = 60;

	private static final int JOBS_RECEIVED_TO_REMOVE = 20;

	private static ClassLoader thisClassLoader = JobToFile.class.getClassLoader();

	private static MutableInt jobsReceivedCounter = new MutableInt( 0 );


	/**
	 *
	 */
	public static void cleanJobsInProgressDirectoryOfAllJobsFiles( ) {

		try {
			cleanDirectory( ClientConstants.JOBS_IN_PROGRESS_DIRECTORY, false );

		} catch (Throwable t) {

			// logged below so don't need to here
		}
	}

	/**
	 *
	 */
	public static void cleanFailedJobsDirectoryOfFilesOver30DaysOld( ) {

		try {
			cleanDirectory( ClientConstants.JOBS_FAILED_DIRECTORY, true );

		} catch (Throwable t) {

			// logged below so don't need to here
		}
	}


	/**
	 *
	 */
	public static List<Job> listJobsInJobsInProgressDirectory( ) throws Throwable {

		try {
			return listJobsInDirectory( ClientConstants.JOBS_IN_PROGRESS_DIRECTORY );

		} catch (Throwable t) {

			// logged below so don't need to here
		}

		return null;
	}


	/**
	 * @param job
	 */
	public static void saveJobToJobsInProgressDirectory( Job job ) throws Throwable {

		saveJobToFile( job, ClientConstants.JOBS_IN_PROGRESS_DIRECTORY, true );

		saveJobToJobsReceivedJobsDirectory( job );
	}

	/**
	 * @param job
	 */
	public static void saveJobToFailedJobsDirectory( Job job ) {

		try {
			saveJobToFile( job, ClientConstants.JOBS_FAILED_DIRECTORY, false );

		} catch (Throwable t) {

			// logged below so don't need to here
		}
	}

	/**
	 * @param job
	 */
	public static void saveJobToJobsReceivedJobsDirectory( Job job ) {

		boolean cleanDirectory = false;

		synchronized ( jobsReceivedCounter ) {

			jobsReceivedCounter.increment();

			if ( jobsReceivedCounter.intValue() > JOBS_RECEIVED_COUNT_MAX ) {

				cleanDirectory = true;
			}
		}

		if ( cleanDirectory ) {

			try {

				cleanJobsReceivedDirectoryDeleteOldestFiles( ClientConstants.JOBS_RECEIVED_DIRECTORY, JOBS_RECEIVED_TO_REMOVE );

			} catch (Throwable t) {

				// logged below so don't need to here
			}



		}



		try {
			saveJobToFile( job, ClientConstants.JOBS_RECEIVED_DIRECTORY, false );

		} catch (Throwable t) {

			// logged below so don't need to here
		}
	}



	/**
	 * @param job
	 */
	public static void deleteJobFromJobsInProgressDirectory( Job job ) throws Throwable {

		deleteJobFile( job, ClientConstants.JOBS_IN_PROGRESS_DIRECTORY, true );

	}








	// Private methods


	/**
	 * @param directoryString
	 * @param apply30DaysAgoCriteria
	 * @throws Throwable
	 */
	private static void cleanDirectory( String directoryString, boolean apply30DaysAgoCriteria ) throws Throwable {

		long time30DaysAgo = 0;

		if ( apply30DaysAgoCriteria ) {

			Calendar time30DaysAgoCalendar = Calendar.getInstance();

			time30DaysAgoCalendar.add( Calendar.DAY_OF_MONTH, -30 );

			time30DaysAgo = time30DaysAgoCalendar.getTimeInMillis();
		}

		try {

			try {

				File directory = new File( directoryString );

				if ( ! directory.exists() ) {

					throw new Exception( "Job directory does not exist.  Job directory = " + directory.getAbsolutePath() );
				}
				if ( ! directory.isDirectory() ) {

					throw new Exception( "Job directory is not a directory.  Job directory = " + directory.getAbsolutePath() );
				}


				if ( log.isDebugEnabled() ) {

					log.debug( "cleanDirectory(...): directory = " + directory.getAbsolutePath()
							+ ", apply30DaysAgoCriteria = " + apply30DaysAgoCriteria
							+ ", time30DaysAgo = " + new Date( time30DaysAgo ) );
				}

				File[] files = directory.listFiles();

				for ( File fileToDelete : files ) {

					String fileToDeleteFilename = fileToDelete.getName();

					if ( fileToDeleteFilename.startsWith( ClientConstants.JOBS_IN_PROGRESS_JOB_NUMBER_PREFIX )
							&& fileToDeleteFilename.endsWith( ClientConstants.JOBS_IN_PROGRESS_JOB_NUMBER_SUFFIX ) ) {

						if ( ( ! apply30DaysAgoCriteria ) || ( fileToDelete.lastModified() < time30DaysAgo ) ) {

							if ( ! fileToDelete.delete() ) {

								throw new Exception( "Cleaning job directory.  Job file failed to delete.  Job file = " + fileToDelete.getAbsolutePath() );

							} else {

								if ( log.isDebugEnabled() ) {

									log.debug( "Deletion of job file '" + fileToDelete.getAbsolutePath() + "' was successfully deleted." );
								}
							}
						}
					}
				}



			} catch (Throwable t) {

				throw t;

			} finally {

			}

		} catch ( Throwable  t) {

			log.error( "Exception in cleaning job directory. ", t );
		}
	}





	// Private methods


	/**
	 * @param directoryString
	 * @throws Throwable
	 */
	private static void cleanJobsReceivedDirectoryDeleteOldestFiles( String directoryString, int numberToRemove ) throws Throwable {


		try {

			try {

				File directory = new File( directoryString );

				if ( ! directory.exists() ) {

					throw new Exception( "Job directory does not exist.  Job directory = " + directory.getAbsolutePath() );
				}
				if ( ! directory.isDirectory() ) {

					throw new Exception( "Job directory is not a directory.  Job directory = " + directory.getAbsolutePath() );
				}


				if ( log.isDebugEnabled() ) {

					log.debug( "cleanDirectory(...): directory = " + directory.getAbsolutePath()
							+ ", numberToRemove = " + numberToRemove );
				}

				File[] files = directory.listFiles();

				List<FileHolderForSorting> filesList = new ArrayList<FileHolderForSorting>( files.length );

				for ( File file : files ) {

					FileHolderForSorting holder = new FileHolderForSorting();
					holder.setFile( file );
					filesList.add( holder );
				}

				// sort so oldest files are first
				Collections.sort( filesList );

				for( int counter = 0; counter < numberToRemove; counter++ ) {

					FileHolderForSorting holder = filesList.get(counter);

					File fileToDelete = holder.getFile();

					String fileToDeleteFilename = fileToDelete.getName();

					if ( fileToDeleteFilename.startsWith( ClientConstants.JOBS_IN_PROGRESS_JOB_NUMBER_PREFIX )
							&& fileToDeleteFilename.endsWith( ClientConstants.JOBS_IN_PROGRESS_JOB_NUMBER_SUFFIX ) ) {

						if ( ! fileToDelete.delete() ) {

							throw new Exception( "Cleaning job directory.  Job file failed to delete.  Job file = " + fileToDelete.getAbsolutePath() );

						} else {

							if ( log.isDebugEnabled() ) {

								log.debug( "Deletion of job file '" + fileToDelete.getAbsolutePath() + "' was successfully deleted." );
							}
						}
					}
				}


				File[] filesAfterDeltion = directory.listFiles();

				synchronized (jobsReceivedCounter) {

					jobsReceivedCounter.setValue( filesAfterDeltion.length );
				}

			} catch (Throwable t) {

				throw t;

			} finally {

			}

		} catch ( Throwable  t) {

			log.error( "Exception in cleaning job directory. ", t );
		}
	}


	/**
	 * little class to enable sorting of files by last modified date
	 *
	 */
	private static class FileHolderForSorting implements Comparable<FileHolderForSorting> {

		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
			this.fileLastModifiedDate = file.lastModified();
		}

		File file;

		long fileLastModifiedDate;

		@Override
		public int compareTo(FileHolderForSorting o) {

			return (int) (fileLastModifiedDate - o.fileLastModifiedDate);
		}
	}



	/**
	 *
	 */
	private static List<Job> listJobsInDirectory( String directoryString ) throws Throwable {

		List<Job> jobsInDirectory = new ArrayList<Job>();

		try {

			try {

				File directory = new File( directoryString );

				if ( ! directory.exists() ) {

					if ( ! directory.mkdir() ) {

						throw new Exception( "Unable to create job directory.  Job directory = " + directory.getAbsolutePath() );
					}
				}

				if ( ! directory.isDirectory() ) {

					throw new Exception( "Job directory is not a directory.  Job directory = " + directory.getAbsolutePath() );
				}

				File[] files = directory.listFiles();


				if ( files.length > 0 ) {

					JAXBContext jc = JAXBContext.newInstance( Constants.DTO_PACKAGE_PATH, thisClassLoader );

					Unmarshaller unmarshaller = jc.createUnmarshaller();


					for ( File jobFile : files ) {

						String jobFileFilename = jobFile.getName();

						if ( jobFileFilename.startsWith( ClientConstants.JOBS_IN_PROGRESS_JOB_NUMBER_PREFIX )
								&& jobFileFilename.endsWith( ClientConstants.JOBS_IN_PROGRESS_JOB_NUMBER_SUFFIX ) ) {


							Job job = (Job) unmarshaller.unmarshal( jobFile );

							jobsInDirectory.add( job );

						}
					}

				}

			} catch (Throwable t) {

				throw t;

			} finally {

			}

		} catch ( Throwable  t) {

			log.error( "Exception in listing job directory. ", t );
		}

		return jobsInDirectory;
	}



	/**
	 * @param job
	 * @param directoryString
	 * @param rethrowExceptions
	 * @throws Throwable
	 */
	private static void saveJobToFile( Job job, String directoryString, boolean rethrowExceptions ) throws Throwable {

		File outputFile = null;

		try {

			try {

				File outputDirectory = new File( directoryString );

				if ( ! outputDirectory.exists() ) {

					if ( ! outputDirectory.mkdir() ) {

						throw new Exception( "Unable to create output directory.  Output directory = " + outputDirectory.getAbsolutePath() );
					}
				}
				if ( ! outputDirectory.isDirectory() ) {

					throw new Exception( "Output directory is not a directory.  Output directory = " + outputDirectory.getAbsolutePath() );
				}

				String outputFileString = filenameForJob(job );

				outputFile = new File( outputDirectory, outputFileString );



				JAXBContext jc = JAXBContext.newInstance( Constants.DTO_PACKAGE_PATH, thisClassLoader );

				Marshaller marshaller = jc.createMarshaller();

				marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

				marshaller.marshal( job, outputFile );

				if ( log.isDebugEnabled() ) {

					log.debug( "saveJobToFile(...):  successfully saved job to file, job id = " + job.getId() + ", run id = " + job.getCurrentRunId()
							+ ", filename = " + outputFile.getAbsolutePath() );
				}

			} catch (Throwable t) {

				throw t;

			} finally {


			}

		} catch ( Throwable  t) {

			String jobId = "job == null";

			String filename = "Filename not set";

			if ( job != null ) {

				if ( job.getCurrentRun() != null ) {

					jobId = "job.id = " + job.getId() + ", job.getCurrentRunId() = " + job.getCurrentRunId();
				} else {

					jobId = "job.id = " + job.getId();
				}
			}

			if ( outputFile != null ) {

				filename = "Filename trying to save to = " + outputFile.getAbsolutePath();
			}

			log.error( "Exception in saving job: " + jobId + ".  " + filename, t );

			if ( rethrowExceptions ) {

				throw t;
			}
		}
	}



	/**
	 * @param job
	 * @throws Throwable
	 */
	private static void deleteJobFile( Job job, String directoryString, boolean rethrowExceptions ) throws Throwable {

		File jobFile = null;

		try {

			try {

				File outputDirectory = new File( directoryString );

				if ( ! outputDirectory.exists() ) {

					throw new Exception( "Job directory does not exist.  Job directory = " + outputDirectory.getAbsolutePath() );
				}
				if ( ! outputDirectory.isDirectory() ) {

					throw new Exception( "Job directory is not a directory.  Job directory = " + outputDirectory.getAbsolutePath() );
				}

				String jobFileString = filenameForJob(job );

				jobFile = new File( outputDirectory, jobFileString );

				if ( ! jobFile.exists() ) {

					throw new Exception( "Job file does not exist.  Job file = " + jobFile.getAbsolutePath() );
				}
				if ( ! jobFile.isFile() ) {

					throw new Exception( "Job file is not a file.  Job file = " + jobFile.getAbsolutePath() );
				}

				if ( ! jobFile.delete() ) {

					throw new Exception( "Job file failed to delete.  Job file = " + jobFile.getAbsolutePath() );

				} else {

					if ( log.isDebugEnabled() ) {

						log.debug( "deleteJobFile(...): The job file was successfully deleted, filename = '" + jobFile.getAbsolutePath() + "'." );

					}
				}


			} catch (Throwable t) {

				throw t;

			} finally {


			}

		} catch ( Throwable  t) {

			String jobId = "job == null";

			String filename = "Filename not set";

			if ( job != null ) {

				if ( job.getCurrentRun() != null ) {

					jobId = "job.id = " + job.getId() + ", job.getCurrentRunId() = " + job.getCurrentRunId();
				} else {

					jobId = "job.id = " + job.getId();
				}
			}

			if ( jobFile != null ) {

				filename = "Filename trying to delete = " + jobFile.getAbsolutePath();
			}

			log.error( "Exception in deleting job: " + jobId + ".  " + filename, t );

			if ( rethrowExceptions ) {

				throw t;
			}
		}
	}



	/**
	 * @param job
	 * @return
	 * @throws Throwable
	 */
	private static String filenameForJob( Job job ) throws Throwable {

		if ( job == null ) {

			throw new Exception( "job cannot be null" );
		}

		String filename = ClientConstants.JOBS_IN_PROGRESS_JOB_NUMBER_PREFIX
				+ job.getId()
				+ ClientConstants.JOBS_IN_PROGRESS_JOB_NUMBER_MIDDLE
				+ job.getCurrentRunId()
				+ ClientConstants.JOBS_IN_PROGRESS_JOB_NUMBER_SUFFIX;

		return filename;
	}
}
