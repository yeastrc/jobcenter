package org.jobcenter.service;

import org.jobcenter.request.UpdateServerFromJobRunOnClientRequest;
import org.jobcenter.response.UpdateServerFromJobRunOnClientResponse;


public interface UpdateServerFromJobRunOnClientService {

	public UpdateServerFromJobRunOnClientResponse updateServerFromJobRunOnClient( UpdateServerFromJobRunOnClientRequest updateJobStatusRequest, String remoteHost );

}
