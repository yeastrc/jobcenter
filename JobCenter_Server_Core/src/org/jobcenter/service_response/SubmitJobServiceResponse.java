package org.jobcenter.service_response;

import org.jobcenter.response.SubmitJobResponse;

/**
 * The server internal response to the submit job request.
 *
 */
public class SubmitJobServiceResponse {

	private SubmitJobResponse submitJobResponse;
	
	private int jobId;

	public SubmitJobResponse getSubmitJobResponse() {
		return submitJobResponse;
	}

	public void setSubmitJobResponse(SubmitJobResponse submitJobResponse) {
		this.submitJobResponse = submitJobResponse;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
}
