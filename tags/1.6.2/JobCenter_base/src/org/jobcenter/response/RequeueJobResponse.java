package org.jobcenter.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The response to a requeue job 
 *
 */
@XmlRootElement(name = "requeueJobResponse")

public class RequeueJobResponse extends BaseResponse {

	public static final int REQUEUE_ERROR_JOB_NOT_FOUND_IN_DB = 1; 

	public static final int REQUEUE_ERROR_DB_RECORD_VERSION_NUMBER_OUT_OF_SYNC = 2; 

	public static final int REQUEUE_ERROR_JOB_NO_LONGER_REQUEUABLE = 3; 

	
	
	private int requeueJobResponseErrorCode;


	public int getRequeueJobResponseErrorCode() {
		return requeueJobResponseErrorCode;
	}
	public void setRequeueJobResponseErrorCode(int requeueJobResponseErrorCode) {
		this.requeueJobResponseErrorCode = requeueJobResponseErrorCode;
	}
}
