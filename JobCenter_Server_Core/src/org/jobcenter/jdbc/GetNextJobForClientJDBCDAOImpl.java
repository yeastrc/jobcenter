package org.jobcenter.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.JobType;
import org.jobcenter.jdbc.JDBCBaseDAO;
import org.jobcenter.request.JobRequest;
import org.jobcenter.request.JobRequestModuleInfo;

public class GetNextJobForClientJDBCDAOImpl extends JDBCBaseDAO implements GetNextJobForClientJDBCDAO {

	private static final Logger log = Logger.getLogger(GetNextJobForClientJDBCDAOImpl.class);

	
	//  Restoring old version of get next job SQL that does not use dependencies

	private static String
	getJobForJobRequestQuerySqlString
	= "SELECT job.*, job.id AS job_id, job.priority AS job_priority, "
		+ " jt.id AS jt_id, jt.name, jt.description, jt.priority AS jt_priority, jt.module_name, jt.minimum_module_version "
		+ "   FROM job INNER JOIN job_type AS jt ON job.job_type_id = jt.id"
		
		+ " WHERE ( status_id = " + JobStatusValuesConstants.JOB_STATUS_SUBMITTED
		+ "           OR  status_id = "  + JobStatusValuesConstants.JOB_STATUS_SOFT_ERROR
		+ "           OR  status_id = "  + JobStatusValuesConstants.JOB_STATUS_REQUEUED + " )  "
		
		+     " AND ( delay_job_until IS NULL OR delay_job_until < NOW()  ) " //  delay_job_until is not set or is in the past
		
		+     " AND insert_complete = 'T' AND enabled = 1 "
		+     " AND ( ";

	
	
	
	//  Comment out new version of get next job SQL since not currently using job dependencies
	
//	private static String
//	GET_JOB_FOR_JOB_REQUEST_QUERY_SUB_SELECT 
//	=  "              AND ( job.status_id = " + JobStatusValuesConstants.JOB_STATUS_SUBMITTED 
//	+                    " OR  job.status_id = " + JobStatusValuesConstants.JOB_STATUS_SOFT_ERROR 
//	+                    " OR  job.status_id = " + JobStatusValuesConstants.JOB_STATUS_REQUEUED + " )  \n"
//	
//	+     "         AND ( job.delay_job_until IS NULL OR job.delay_job_until < NOW()  ) \n" //  delay_job_until is not set or is in the past
//	
//	+     "        AND job.insert_complete = 'T'  \n";
//	
//
//	
//
//	private static String
//	getJobForJobRequestQuerySqlString
//	= "SELECT job.*, job.id AS job_id, job.priority AS job_priority, \n"
//		+ " jt.id AS jt_id, jt.name, jt.description, jt.priority AS jt_priority, jt.module_name, jt.minimum_module_version \n"
//		+ " FROM \n"
//		+ "  ( \n"
//		
//		
//		//   select jobs that are dependent on other jobs
//		+ "     SELECT job.*  \n" 
//		+ "     FROM job   \n" 
//		+ "     LEFT OUTER JOIN \n" 
//		+ "     ( \n" 
//
//		+ "      		SELECT dependent_jobs.id  \n" 
//		+ "      		FROM job AS dependent_jobs  \n" 
//		+ "      		    INNER JOIN job_dependencies AS jd ON dependent_jobs.id = jd.dependent_job \n" 
//		+ "      		    INNER JOIN job AS dependencee_jobs ON jd.dependee_job = dependencee_jobs.id \n" 
//		+ "      		WHERE  \n" 
//		+ "      		    ( dependencee_jobs.status_id != " + JobStatusValuesConstants.JOB_STATUS_FINISHED 
//		+                   " AND dependencee_jobs.status_id != " + JobStatusValuesConstants.JOB_STATUS_FINISHED_WITH_WARNINGS 
//		+                 " ) \n"
//		
//		+ "     ) AS jobs_with_dependencies_not_finished  \n" 
//		+ "             ON job.id = jobs_with_dependencies_not_finished.id  \n" 
//		+ "     WHERE  \n" 
//		+ "       jobs_with_dependencies_not_finished.id IS NULL   \n" // exclude all the job ids found in the subquery result jobs_with_dependencies_not_finished
//		
//		
//		
//		
//		+     				GET_JOB_FOR_JOB_REQUEST_QUERY_SUB_SELECT
//		
//		 
//		+ "      		UNION \n" 
//
//		//   select jobs that are NOT dependent on other jobs
//		
//		+ "      		SELECT job.*  \n" 
//		+ "      		FROM job   \n" 
//		+ "      		    LEFT OUTER JOIN job_dependencies AS jd ON job.id = jd.dependent_job \n" 
//		+ "      		WHERE  \n" 
//		+ "      		    jd.dependee_job IS NULL \n" 
//
//		+					GET_JOB_FOR_JOB_REQUEST_QUERY_SUB_SELECT
//		 
//		+ "      ) AS job \n" 
//
//		+ " INNER JOIN job_type AS jt ON job.job_type_id = jt.id \n"
//		
//		
//		+ " WHERE  jt.enabled = 1 \n"
//		
//		+     " AND ( \n";


	private static String
	getJobForJobRequestQuerySqlStringOrderBy
		= " \n ) \n "
		+ " ORDER BY job.priority , job.submit_date  \n"
		+ " LIMIT 1 FOR UPDATE  \n" ;



	/**
	 * @return
	 */
	@Override
	public Job retrieveNextJobRecordForJobRequest( final JobRequest jobRequest )
	{

		//  Declare array to store result in.
		
		final Job jobResult[] = { null };
		
		
		
		
		
		super.doJDBCWork(  new JDBCBaseWorkIF() {
	        public void execute(Connection connection) throws SQLException {

	        	final String method = "retrieveJobForJobRequest-Work";

	        	if ( log.isDebugEnabled() ) {
	        		log.debug( "Entering " + method );
	        	}

	        	if ( jobRequest == null ) {

	        		log.error("JobService::retrieveJob  IllegalArgument:jobRequest == null");

	        		throw new IllegalArgumentException( "jobRequest == null" );
	        	}

	        	List<JobRequestModuleInfo> clientModules = jobRequest.getClientModules();

	        	if ( clientModules == null ) {

	        		log.error( method + "  IllegalArgument:jobRequest.getClientModules() == null");

	        		throw new IllegalArgumentException( "jobRequest.getClientModules() == null" );
	        	}

	        	if ( clientModules.isEmpty() ) {

	        		log.error( method + " IllegalArgument:jobRequest.getClientModules() is empty");

	        		throw new IllegalArgumentException( "jobRequest.getClientModules() is empty" );
	        	}



	        	Job job = null;


	        	PreparedStatement pstmt = null;

	        	ResultSet rs = null;

	        	StringBuilder sql = new StringBuilder( 1000 );

	        	sql.append( getJobForJobRequestQuerySqlString );

	        	boolean first = true;

	        	for ( JobRequestModuleInfo moduleInfo : clientModules ) {

	        		if ( first ) {

	        			first = false;
	        		} else {

	        			sql.append( "\n OR ");
	        		}

	        		sql.append( "\n ( jt.module_name = ? AND jt.minimum_module_version <= ?  " );
	        		sql.append     ( "AND ( jt.required_execution_threads IS NULL OR jt.required_execution_threads <= ? ) ) " );
	        	}
	        	
	        	sql.append( getJobForJobRequestQuerySqlStringOrderBy );

	        	String sqlString = sql.toString();


	        	try {

	        		pstmt = connection.prepareStatement( sqlString );
	        		

		        	if ( log.isDebugEnabled() ) {
		        		log.debug( "In " + method +": sqlString: " + sqlString );
		        	}

	        		int paramIndex = 0;

	        		for ( JobRequestModuleInfo moduleInfo : clientModules ) {

	        			paramIndex++;

	        			pstmt.setString( paramIndex, moduleInfo.getModuleName() );

	        			paramIndex++;

	        			pstmt.setInt( paramIndex, moduleInfo.getModuleVersion() );

	        			paramIndex++;    //  comparison to jt.required_execution_threads
	        			pstmt.setInt( paramIndex, jobRequest.getTotalNumberThreadsConfiguredOnClient() );

	    	        	if ( log.isDebugEnabled() ) {
			        		log.debug( "In " + method +": moduleInfo.getModuleName(): " + moduleInfo.getModuleName() 
			        				+ ", moduleInfo.getModuleVersion(): " + moduleInfo.getModuleVersion() );
	    	        	}
	        		}


	        		rs = pstmt.executeQuery();

	        		if ( rs.next() ) {


	        			int job_id = rs.getInt( "job_id" );

	        			int requestId = rs.getInt( "request_id" );

	        			int jobTypeId = rs.getInt( "job_type_id" );

	        			int priority = rs.getInt( "job_priority" );

	        			int statusId = rs.getInt( "status_id" );

	        			int jobParameterCount  = rs.getInt( "job_parameter_count" );

	        			Date submitDate = rs.getTimestamp( "submit_date" );

	        			String submitter = rs.getString( "submitter" );

	        			job = new Job();

	        			job.setId( job_id );
	        			job.setRequestId( requestId );
	        			job.setJobTypeId( jobTypeId );
	        			job.setPriority( priority );
	        			job.setStatusId( statusId );
	        			job.setJobParameterCount( jobParameterCount );

	        			job.setSubmitDate( submitDate );
	        			job.setSubmitter( submitter );


	        			job.setDelayJobUntil( rs.getTimestamp( "delay_job_until" ) );
	        			job.setParamErrorRetryCount( rs.getInt( "param_error_retry_count" ) );
	        			job.setSoftErrorRetryCount( rs.getInt( "soft_error_retry_count" ) );

	        			// Current database record version number, for optimistic locking version tracking
	        			job.setDbRecordVersionNumber(  rs.getInt( "db_record_version_number" ) );


	        			JobType jobType = new JobType();

	        			job.setJobType( jobType );

	        			int jobTypeRecordId = rs.getInt( "jt_id" );

	        			int jobTypePriority = rs.getInt( "jt_priority" );

	        			int minimumModuleVersion = rs.getInt( "minimum_module_version" );

	        			String jobTypeName = rs.getString( "name" );

	        			String jobTypeModuleName =  rs.getString( "module_name" );

	        			jobType.setId( jobTypeRecordId );

	        			jobType.setPriority( jobTypePriority );

	        			jobType.setMinimumModuleVersion( minimumModuleVersion );

	        			jobType.setModuleName( jobTypeModuleName );

	        			jobType.setName( jobTypeName );

	        		}


	        	} catch (Throwable sqlEx) {

	        		log.error( method + ":Exception: \nSQL = '" + sqlString
	        				+ "\n Exception message: " + sqlEx.toString() + '.', sqlEx);

	        		if (connection != null) {
	        			try {
	        				connection.rollback();

	        			} catch (SQLException ex) {
	        				// ignore
	        			}
	        		}

	        		//  Wrap in RuntimeException for Spring Transactional rollback
	        		throw new RuntimeException( sqlEx );

	        	} finally {

	        		if (rs != null) {
	        			try {
	        				rs.close();
	        			} catch (SQLException ex) {
	        				// ignore
	        			}
	        		}

	        		if (pstmt != null) {
	        			try {
	        				pstmt.close();
	        			} catch (SQLException ex) {
	        				// ignore
	        			}
	        		}

	        	}

	        	// store the resulting job in the array declared in the surrounding method
	        	jobResult[0] = job;
	        }
		} );
		
		
		//  Outside the call to do work, the jobResult[0] has been updated.
		
    	return jobResult[0];
		
	}		
		



}
