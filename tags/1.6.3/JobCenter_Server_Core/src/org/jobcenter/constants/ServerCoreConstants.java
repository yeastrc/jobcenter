package org.jobcenter.constants;

public interface ServerCoreConstants {

	public static final int MAX_JOBS_RETURNED = 100;

	public static final int MAX_REQUESTS_RETURNED = 20;


	public static final int DEFAULT_CLIENT_CHECKIN_TIME_IN_SECONDS = 30;


	public static final int DEFAULT_CLIENT_CHECKIN_OVERAGE_BEFORE_LATE_PERCENT = 80;

	public static final int DEFAULT_CLIENT_CHECKIN_OVERAGE_BEFORE_LATE_MAX_VALUE = 120;
	
	
	public static final int DEFAULT_SOFT_ERROR_RETRY_COUNT_MAX = 5;
	
	public static final int SOFT_ERROR_DELAY_TIME = 5;  //  Seconds
	
	public static final int PARAM_ERROR_RETRY_COUNT_MAX = 5;
	
	public static final int PARAM_ERROR_DELAY_TIME = 5;  //  Seconds
}
