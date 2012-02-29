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

	private static Logger log = Logger.getLogger(GetNextJobForClientJDBCDAOImpl.class);
	
	



	private static String
		getJobForJobRequestQuerySqlString
		= "SELECT job.id AS job_id, job.request_id, job.job_type_id, job.priority AS job_priority, job.status_id, job.submit_date, job.submitter, "
			+ " job.db_record_version_number AS job_db_record_version_number, "
			+ " jt.id AS jt_id, jt.name, jt.description, jt.priority AS jt_priority, jt.module_name, jt.minimum_module_version "
			+ "   FROM job INNER JOIN job_type AS jt ON job.job_type_id = jt.id"
			+ " WHERE ( status_id = " + JobStatusValuesConstants.JOB_STATUS_SUBMITTED
			+ "           OR  status_id = "  + JobStatusValuesConstants.JOB_STATUS_SOFT_ERROR
			+ "           OR  status_id = "  + JobStatusValuesConstants.JOB_STATUS_REQUEUED + " )  "
				+ " AND enabled = 1 AND ( ";

	private static String
	getJobForJobRequestQuerySqlStringOrderBy
		= " ) ORDER BY job.priority , job.submit_date  LIMIT 1 FOR UPDATE" ;



	/**
	 * @return
	 */
	@Override
	public Job retrieveNextJobRecordForJobRequest( JobRequest jobRequest )
	{
		final String method = "retrieveJobForJobRequest";

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

				sql.append( " OR ");
			}

			sql.append( " ( jt.module_name = ? AND jt.minimum_module_version <= ? ) " );
		}

		sql.append( getJobForJobRequestQuerySqlStringOrderBy );

		String sqlString = sql.toString();
		
		Connection connection = null;


		try {

			connection = getConnection( );

			pstmt = connection.prepareStatement( sqlString );

			int paramIndex = 0;

			for ( JobRequestModuleInfo moduleInfo : clientModules ) {

				paramIndex++;

				pstmt.setString( paramIndex, moduleInfo.getModuleName() );

				paramIndex++;

				pstmt.setInt( paramIndex, moduleInfo.getModuleVersion() );
			}


			rs = pstmt.executeQuery();

			if ( rs.next() ) {


				int job_id = rs.getInt( "job_id" );

				int requestId = rs.getInt( "request_id" );

				int jobTypeId = rs.getInt( "job_type_id" );

				int priority = rs.getInt( "job_priority" );

				int statusId = rs.getInt( "status_id" );

				Date submitDate = rs.getTimestamp( "submit_date" );

				String submitter = rs.getString( "submitter" );

				job = new Job();

				job.setId( job_id );
				job.setRequestId( requestId );
				job.setJobTypeId( jobTypeId );
				job.setPriority( priority );
				job.setStatusId( statusId );

				job.setSubmitDate( submitDate );
				job.setSubmitter( submitter );

				// Current database record version number, for optimistic locking version tracking

				job.setDbRecordVersionNumber(  rs.getInt( "job_db_record_version_number" ) );


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

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
		
		}

		return job;
	}



}
