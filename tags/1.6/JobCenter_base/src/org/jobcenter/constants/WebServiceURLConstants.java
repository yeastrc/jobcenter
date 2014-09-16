package org.jobcenter.constants;

public interface WebServiceURLConstants {

	public static final String WEB_SERVICE_URL_BASE_POST_CONTEXT = "services/";

	public static final String UPDATE_SERVER_FROM_JOB_RUN_ON_CLIENT_SERVICE = "updateServerFromJobRunOnClient";
	public static final String GET_RUN_SERVICE = "getRunService";
	public static final String GET_RUN_ID_LIST_SERVICE = "GetRunIdListResponse";

	public static final String REQUEUE_JOB = "requeueJob";
	public static final String CANCEL_JOB = "cancelJob";
	public static final String JOB_CHANGE_PRIORITY = "jobChangePriority";


	public static final String CLIENT_STARTUP = "clientStartup";
	public static final String CLIENT_UPDATE_STATUS = "clientUpdateStatus";

	public static final String GET_NEXT_JOB_FOR_CLIENT_TO_PROCESS = "getNextJobForClient";
	
	public static final String SUBMIT_JOB = "submitJob";
	public static final String SUBMIT_JOBS_LIST_WITH_DEPENDENCIES = "submitJobsListWithDependencies";

	//  GUI

	public static final String GUI_LIST_JOBS = "GUIListJobs";
	public static final String GUI_VIEW_JOB = "GUIViewJob";
	public static final String GUI_LIST_REQUESTS = "GUIListRequests";

	public static final String GUI_LIST_JOB_TYPES = "GUIListJobTypes";
	public static final String GUI_LIST_REQUEST_TYPES = "GUIListRequestTypes";

	public static final String GUI_LIST_CLIENTS_STATUS = "GUIListClientsStatus";
	public static final String GUI_LIST_CLIENTS_CONNECTED = "GUIListClientsConnected";
	
	public static final String GUI_LIST_CLIENTS_USING_SAME_NODE_NAME = "GUIListClientsUsingSameNodeName";
	public static final String GUI_LIST_CLIENTS_FAILED_TO_CONNECT = "GUIListClientsFailedToConnect";
	
	public static final String SEND_CLIENTS_CHECKIN_LATE_NOTIFICATION = "sendClientsCheckinLateNotification";
}
