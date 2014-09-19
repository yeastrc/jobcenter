package org.jobcenter.jdbc;

import java.util.List;

import org.jobcenter.dto.JobType;

public interface JobTypeJDBCDAO {

	public  List<JobType> getJobTypes( );
}
