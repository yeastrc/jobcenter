package org.jobcenter.service;

import org.jobcenter.request.*;
import org.jobcenter.response.*;


public interface CancelJobService  {

	public CancelJobResponse cancelJob( CancelJobRequest cancelJobRequest, String remoteHost );


}
