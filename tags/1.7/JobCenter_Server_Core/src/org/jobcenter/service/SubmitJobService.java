package org.jobcenter.service;

import org.jobcenter.request.SubmitJobRequest;
import org.jobcenter.response.SubmitJobResponse;


public interface SubmitJobService {

	public SubmitJobResponse submitJob( SubmitJobRequest submitJobRequest, String remoteHost );
}
