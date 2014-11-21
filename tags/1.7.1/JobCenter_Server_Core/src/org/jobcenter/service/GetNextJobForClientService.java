package org.jobcenter.service;

import org.jobcenter.request.JobRequest;
import org.jobcenter.service_response.GetJobServiceResponse;



public interface GetNextJobForClientService {

	public GetJobServiceResponse getNextJobForClientService( JobRequest jobRequest, String remoteHost );

}
