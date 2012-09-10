package org.jobcenter.service;

import org.jobcenter.request.JobRequest;
import org.jobcenter.response.JobResponse;



public interface GetNextJobForClientService {

	public JobResponse getNextJobForClientService( JobRequest jobRequest, String remoteHost );

}
