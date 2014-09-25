package org.jobcenter.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The response to a cancel job 
 *
 */
@XmlRootElement(name = "cancelJobResponse")

public class CancelJobResponse extends BaseResponse {

	public static final int CANCEL_ERROR_JOB_NOT_FOUND_IN_DB = 1; 
	
	public static final int CANCEL_ERROR_DB_RECORD_VERSION_NUMBER_OUT_OF_SYNC = 2; 
	
	public static final int CANCEL_ERROR_JOB_NOT_CANCELABLE = 3; 
	
	
	private int cancelJobResponseErrorCode;


	public int getCancelJobResponseErrorCode() {
		return cancelJobResponseErrorCode;
	}
	public void setCancelJobResponseErrorCode(int cancelJobResponseErrorCode) {
		this.cancelJobResponseErrorCode = cancelJobResponseErrorCode;
	}

}
