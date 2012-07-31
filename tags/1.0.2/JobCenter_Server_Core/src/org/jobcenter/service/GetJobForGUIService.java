package org.jobcenter.service;

import org.jobcenter.request.*;
import org.jobcenter.response.*;

public interface GetJobForGUIService {

	/**
	 * @param viewJobRequest
	 * @param remoteHost
	 * @return
	 */
	public  ViewJobResponse retrieveJob( ViewJobRequest viewJobRequest, String remoteHost );
	
}
