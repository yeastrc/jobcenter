package org.jobcenter.jdbc;

import java.sql.Connection;
import java.util.List;

import org.jobcenter.dto.*;
import org.jobcenter.request.*;




public interface JobJDBCDAO {

	public  void retrieveJobParameters( Job job );


	public  void updateJobStatusOnIdAndDbRecordVersionNumber( Job job, int newJobStatus );

	public RunDTO insertRunTableRecord( int nodeId, Job job );

	public  List<Integer> getRunIdsByJobId( int jobId );

	public  void insertRunMessageFromModuleRecord( RunMessageDTO runMessage, RunDTO run );

	public  void insertRunOutputParam ( String key, String value, RunDTO run );

	public  void retrieveRunOutputParams( RunDTO run );
	
	public  int insertRequest( int requestType );

	public  void insertJob ( Job job );

	public  void insertJobParameter ( String key, String value, int jobId );

	public  void healthCheck ( );

	public  Integer getRequestFromIdAndRequestType( int id, int type );


	public  int  getJobCount( ListJobsRequest listJobsRequest );

	public  List<Integer>  getJobIdList( ListJobsRequest listJobsRequest );
	
}
