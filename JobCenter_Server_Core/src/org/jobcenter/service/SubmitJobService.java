package org.jobcenter.service;

import org.jobcenter.request.SubmitJobRequest;
import org.jobcenter.service_response.SubmitJobServiceResponse;


public interface SubmitJobService {

	public SubmitJobServiceResponse submitJob( SubmitJobRequest submitJobRequest, String remoteHost );
}
