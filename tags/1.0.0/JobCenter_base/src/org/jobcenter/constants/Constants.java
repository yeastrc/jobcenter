package org.jobcenter.constants;

public class Constants {


	public static final String DTO_PACKAGE_PATH = "org.jobcenter.dto";

	public static final String SUBMISSION_CLIENT_NODE_NAME_DEFAULT = "submissionClientDefaultNodeName";


	public static final String GUI_CLIENT_NODE_NAME_DEFAULT = "guiClientDefaultNodeName";




	public static final String CLIENT_RUN_CONTROL_FILENAME = "jobcenter_run_control.txt";

	public static final String CLIENT_RUN_CONTROL_STOP_RUN_TEXT = "stop run";

	public static final String CLIENT_RUN_CONTROL_STOP_JOBS_TEXT = "stop jobs";

	public static final String CLIENT_RUN_CONTROL_INITIAL_CONTENTS = "\njobcenter client run control file.  \n"

		+ "Change this file so it begins with '"
		+ CLIENT_RUN_CONTROL_STOP_RUN_TEXT
		+ "' ( without the quotes ) at the beginning of this file to stop the client after all jobs are processed.  \n"

		+ "Change this file so it begins with '"
		+ CLIENT_RUN_CONTROL_STOP_JOBS_TEXT
		+ "' ( without the quotes ) at the beginning of this file to stop requesting new jobs.  Use this option if using a job manager that would restart this job automatically.\n"
		+ "This file will be updated with the status when all jobs are complete.\n" ;


	public static final String CLIENT_RUN_CONTROL_STOP_REQUEST_ACCEPTED = "\n\n\n !!! Stop request accepted and no new jobs will be retrieved for processing.";

	private static final String CLIENT_RUN_CONTROL_ALL_JOBS_COMPLETE = "Stop request specified in this file.  All jobs complete.";

	public static final String CLIENT_RUN_CONTROL_ALL_JOBS_COMPLETE_READY_FOR_SHUTDOWN = CLIENT_RUN_CONTROL_ALL_JOBS_COMPLETE + "  Ready for shutdown.\n";

	public static final String CLIENT_RUN_CONTROL_ALL_JOBS_COMPLETE_SHUTDOWN_PROCEEDING = CLIENT_RUN_CONTROL_ALL_JOBS_COMPLETE + "  Shutdown proceeding.\n";
}
