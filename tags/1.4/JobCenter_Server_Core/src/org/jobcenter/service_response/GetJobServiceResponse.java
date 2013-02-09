package org.jobcenter.service_response;

import org.jobcenter.response.JobResponse;

/**
 * The server internal response to the get job request.
 * 
 * This exists to handle internal errors that need to return to the top level Server_Jersey code to 
 *
 */
public class GetJobServiceResponse {

	public JobResponse getJobResponse() {
		return jobResponse;
	}
	public void setJobResponse(JobResponse jobResponse) {
		this.jobResponse = jobResponse;
	}
	private JobResponse jobResponse;
	private boolean tryAgain;
	

	public boolean isTryAgain() {
		return tryAgain;
	}
	public void setTryAgain(boolean tryAgain) {
		this.tryAgain = tryAgain;
	}
}
