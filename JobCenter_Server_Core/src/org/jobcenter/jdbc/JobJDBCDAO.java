package org.jobcenter.jdbc;

import java.sql.Connection;
import java.util.List;

import org.jobcenter.dto.*;
import org.jobcenter.request.*;




public interface JobJDBCDAO {

	public  void retrieveJobParameters( Job job );


	public  void updateJobStatusOnIdAndDbRecordVersionNumber( Job job, int newJobStatus );
	
	public  void updateDelayUntilAndParamErrorCntOnId( int jobId );
	
		

	public RunDTO insertRunTableRecord( int nodeId, Job job );
	
	public  RunDTO insertRunTableRecord( int nodeId, Job job, int statusId );


	public  List<Integer> getRunIdsByJobId( int jobId );

	public  void insertRunMessageFromModuleRecord( RunMessageDTO runMessage, RunDTO run );

	public  void insertRunOutputParam ( String key, String value, RunDTO run );

	public  void retrieveRunOutputParams( RunDTO run );

	public  int insertRequest( int requestType );

	public  void insertJob ( Job job );
	
	public void insertJob ( final Job job, final Connection connection );

	public  void markJobInsertComplete ( Job job );
	
	public  void markJobInsertComplete ( Job job, final Connection connection );

	public  void insertJobParameter ( String key, String value, int jobId );

	public  void insertJobParameter ( String key, String value, int jobId, final Connection connection );
	

	public  void insertJobDependcy( int jobId, int jobDependency );

	public  void insertJobDependcy( int jobId, int jobDependency, final Connection connection );

	public  void healthCheck ( );

	public  Integer getRequestFromIdAndRequestType( int id, int type );


	public  int  getJobCount( ListJobsRequest listJobsRequest );

	public  List<Integer>  getJobIdList( ListJobsRequest listJobsRequest );

}
