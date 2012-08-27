package org.jobcenter.service;

import org.jobcenter.request.*;
import org.jobcenter.response.*;


public interface RequeueJobService {

	public RequeueJobResponse requeueJob( RequeueJobRequest requeueJobRequest, String remoteHost );

}
