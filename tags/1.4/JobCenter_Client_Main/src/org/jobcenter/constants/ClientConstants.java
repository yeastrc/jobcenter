package org.jobcenter.constants;

public interface ClientConstants {

	public static final String MODULE_BASE_LOCATION = "jobcenter_modules";


	public static final String CLIENT_SERVER_INTERFACE_MODULE_FILE_PATH  = "jobcenter_client_code/client_server_interface_jersey_1.3";

	public static final String CLIENT_SERVER_INTERFACE_MODULE_CLASS_NAME = "org.jobcenter.client.main.ClientConnectionToServer";

	public static final String JOBS_IN_PROGRESS_DIRECTORY = "jobs_in_progress";

	public static final String JOBS_IN_PROGRESS_JOB_NUMBER_PREFIX = "job_";

	public static final String JOBS_IN_PROGRESS_JOB_NUMBER_MIDDLE = "_run_";

	public static final String JOBS_IN_PROGRESS_JOB_NUMBER_SUFFIX = ".xml";


	public static final String JOBS_FAILED_DIRECTORY = "jobs_failed";

	public static final String JOBS_RECEIVED_DIRECTORY = "jobs_received";


	public static final String TIME_CONTROL_CONFIG_FILENAME = "time_control_settings.txt";


	public static final int MULTIPLE_FOR_COMPUTING_TIME_CHECKING_WITH_NO_WORKER_THREADS = 30;


}
