package org.jobcenter.jdbc;

import org.jobcenter.dto.Job;
import org.jobcenter.request.JobRequest;

public interface GetNextJobForClientJDBCDAO {

	public Job retrieveNextJobRecordForJobRequest( JobRequest jobRequest );
	
	
}
