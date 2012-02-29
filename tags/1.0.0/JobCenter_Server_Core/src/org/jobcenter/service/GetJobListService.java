package org.jobcenter.service;

import org.jobcenter.request.*;
import org.jobcenter.response.*;

public interface GetJobListService {

	public  ListJobsResponse retrieveJobsList( ListJobsRequest listJobsRequest, String remoteHost );

}
