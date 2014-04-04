package org.jobcenter.service;

import org.jobcenter.request.*;
import org.jobcenter.response.*;


public interface GetRequestTypesService {

	/**
	 * @param listRequestTypesRequest
	 * @param remoteHost
	 * @return
	 */
	public  ListRequestTypesResponse retrieveRequestTypes( ListRequestTypesRequest listRequestTypesRequest, String remoteHost );

}
