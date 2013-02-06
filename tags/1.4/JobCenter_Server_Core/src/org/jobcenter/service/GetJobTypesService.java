package org.jobcenter.service;

import org.jobcenter.request.*;
import org.jobcenter.response.*;

public interface GetJobTypesService {


	/**
	 * @param listJobTypesRequest
	 * @param remoteHost
	 * @return
	 */
	public  ListJobTypesResponse retrieveJobTypes( ListJobTypesRequest listJobTypesRequest, String remoteHost );

}
