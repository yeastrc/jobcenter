package org.jobcenter.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The response to a change priority job 
 *
 */
@XmlRootElement(name = "jobChangePriorityResponse")

public class JobChangePriorityResponse extends BaseResponse {

	public static final int CHANGE_PRIORITY_ERROR_JOB_NOT_FOUND_IN_DB = 1; 
	
	public static final int CHANGE_PRIORITY_ERROR_DB_RECORD_VERSION_NUMBER_OUT_OF_SYNC = 2; 
	
	
	private int jobChangePriorityResponseErrorCode;


	public int getJobChangePriorityResponseErrorCode() {
		return jobChangePriorityResponseErrorCode;
	}


	public void setJobChangePriorityResponseErrorCode(
			int jobChangePriorityResponseErrorCode) {
		this.jobChangePriorityResponseErrorCode = jobChangePriorityResponseErrorCode;
	}

}
