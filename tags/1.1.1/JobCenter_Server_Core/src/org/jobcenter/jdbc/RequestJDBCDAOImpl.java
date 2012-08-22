package org.jobcenter.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.constants.ServerCoreConstants;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.JobType;
import org.jobcenter.dto.Node;
import org.jobcenter.dto.RequestTypeDTO;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.dto.RunMessageDTO;
import org.jobcenter.dto.RunMsgTypeDTO;
import org.jobcenter.dto.StatusDTO;
import org.jobcenter.exception.RecordNotUpdatedException;
import org.jobcenter.request.JobRequest;
import org.jobcenter.request.JobRequestModuleInfo;
import org.jobcenter.request.ListJobsRequest;
import org.jobcenter.request.ListRequestsRequest;
import org.springframework.context.ApplicationContext;



public class RequestJDBCDAOImpl extends JDBCBaseDAO implements RequestJDBCDAO { 

	private static Logger log = Logger.getLogger(RequestJDBCDAOImpl.class);

	//  Used by the healthcheck servlet to get the bean
	public static RequestJDBCDAO getFromApplicationContext(ApplicationContext ctx) {
    	return (RequestJDBCDAO) ctx.getBean("requestJDBCDAO");
	}


	private static String
	getRequestIdListQuerySqlStringMainQuery =
	
		"SELECT order_date, request_id FROM "
		+ "( "
		
		+ "  SELECT max(order_date) AS order_date, request_id "
		+ "	  FROM  "
		
		+ "	 ( "
		+ "		SELECT start_date AS order_date, request_id, job.id AS job_id "
		+ "		 FROM "
		+ "		    job INNER JOIN run ON job.id = run.job_id  "
		
		+ "		  UNION "
		
		+ "		 SELECT submit_date AS order_date, request_id, job.id AS job_id "
		+ "		  FROM "
		+ "		    job LEFT OUTER JOIN run ON job.id = run.job_id  "
		+ "		           WHERE run.id IS NULL "
		
		+ "	  ) AS get_dates_and_request_ids "

		+ "	  GROUP BY request_id  "
		
		+ ") AS get_dates_and_request_ids "
		;
	
	private static String
	getRequestIdListQuerySqlStringMaxJobIdCriteriaStart =
		
		"   INNER JOIN "
		+ "   ( SELECT DISTINCT(request_id) AS request_id_max_job_id_criteria  FROM " 
		+          "( "

		+            "    SELECT MAX(id) AS job_id FROM job GROUP BY request_id ORDER BY request_id "
		+          " ) AS max_job_id_per_request "
		+ "   INNER JOIN job on job_id = id WHERE  "
		;

	private static String
	getRequestIdListQuerySqlStringMaxJobIdCriteriaEnd =

		" ) AS max_job_id_criteria ON get_dates_and_request_ids.request_id = max_job_id_criteria.request_id_max_job_id_criteria "
		;


	private static String
	getRequestIdListQuerySqlStringOtherCriteriaStart =

		"   INNER JOIN ( SELECT DISTINCT(request_id) AS request_id_other_criteria "
		+                  " FROM job " 
		+ 		            "   LEFT OUTER JOIN job_type AS jt ON job.job_type_id = jt.id  "  
		+ 		            "   LEFT OUTER JOIN request ON job.request_id = request.id "  
		+ 		            "   LEFT OUTER JOIN request_type AS rt ON request.type = rt.id " 
		+                  "    WHERE  "
		;

	private static String
	getRequestIdListQuerySqlStringOtherCriteriaEnd =

		" ) AS other_criteria ON get_dates_and_request_ids.request_id = other_criteria.request_id_other_criteria "
		;

	private static String
	getRequestIdListQuerySqlStringLastPart =

		" ORDER BY order_date DESC"
		+ " LIMIT ?, ? "
		;

	/* (non-Javadoc)
	 * @see org.jobcenter.jdbc.RequestJDBCDAO#getRequestIdList(org.jobcenter.request.ListRequestsRequest)
	 */
	public  List<Integer>  getRequestIdList( ListRequestsRequest listRequestsRequest )
	{
		final String method = "getRequestIdList";

		if ( log.isDebugEnabled() ) {
			log.debug( "Entering " + method );
		}

		List<Integer> requestIdList = new ArrayList<Integer>( ServerCoreConstants.MAX_JOBS_RETURNED + 1 );

		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rs = null;

		String sql = null;

		try {

			connection = getConnection( );

			StringBuilder sqlSB = new StringBuilder( 1000 );

			sqlSB.append( getRequestIdListQuerySqlStringMainQuery );


			boolean firstMaxJobIdCriteria = true;

			StringBuilder sqlMaxJobIdCriteriaSB = new StringBuilder( 1000 );
			
			
			boolean firstOtherCriteria = true;

			StringBuilder sqlOtherCriteriaSB = new StringBuilder( 1000 );
			
			
			// job limiting parameters
			
			
			Set<Integer> statusIds = listRequestsRequest.getStatusIds();

			if ( statusIds != null && ( ! statusIds.isEmpty() ) ) {

				if ( firstMaxJobIdCriteria ) {
					firstMaxJobIdCriteria = false;
				} else {
					sqlMaxJobIdCriteriaSB.append( " AND " );
				}

				sqlMaxJobIdCriteriaSB.append( " ( status_id IN ( " );

				boolean firstStatus = true;

				for ( int status : statusIds ) {

					if ( firstStatus ) {
						firstStatus = false;
					} else {

						sqlMaxJobIdCriteriaSB.append( ", " );
					}
					sqlMaxJobIdCriteriaSB.append( status );
				}

				sqlMaxJobIdCriteriaSB.append( " ) ) " );
			}

			String jobTypeName = listRequestsRequest.getJobTypeName();

			if ( ! StringUtils.isEmpty( jobTypeName ) ) {

				if ( firstOtherCriteria ) {
					firstOtherCriteria = false;
				} else {
					sqlOtherCriteriaSB.append( " AND " );
				}
				sqlOtherCriteriaSB.append( " jt.name = ? " );
			}

			if ( listRequestsRequest.getRequestId() != null ) {

				if ( firstOtherCriteria ) {
					firstOtherCriteria = false;
				} else {
					sqlOtherCriteriaSB.append( " AND " );
				}
				sqlOtherCriteriaSB.append( " job.request_id = ? " );
			}

			if ( ! StringUtils.isEmpty( listRequestsRequest.getRequestTypeName() ) ) {

				if ( firstOtherCriteria ) {
					firstOtherCriteria = false;
				} else {
					sqlOtherCriteriaSB.append( " AND " );
				}
				sqlOtherCriteriaSB.append( " rt.name = ? " );
			}


			if ( ! firstMaxJobIdCriteria ) {
				
				//  found MaxJobId criteria ( utilizes the job with the highest id for a given request )
				
				
				sqlSB.append( getRequestIdListQuerySqlStringMaxJobIdCriteriaStart );
				
				String sqlMaxJobIdCriteria = sqlMaxJobIdCriteriaSB.toString();
				
				sqlSB.append( sqlMaxJobIdCriteria );
				
				sqlSB.append( getRequestIdListQuerySqlStringMaxJobIdCriteriaEnd );
			}
			
			
			if ( ! firstOtherCriteria ) {
				
				//  found other criteria
				
				
				sqlSB.append( getRequestIdListQuerySqlStringOtherCriteriaStart );
				
				String sqlOtherCriteria = sqlOtherCriteriaSB.toString();
				
				sqlSB.append( sqlOtherCriteria );
				
				sqlSB.append( getRequestIdListQuerySqlStringOtherCriteriaEnd );
			}
			
			
			
			sqlSB.append( getRequestIdListQuerySqlStringLastPart );


			sql = sqlSB.toString();

			pstmt = connection.prepareStatement( sql );

			int paramCounter = 0;

			// search params

			if ( ! StringUtils.isEmpty( jobTypeName ) ) {

				paramCounter++;
				pstmt.setString( paramCounter, jobTypeName );
			}

			if ( listRequestsRequest.getRequestId() != null ) {

				paramCounter++;
				pstmt.setInt( paramCounter,  listRequestsRequest.getRequestId()  );
			}

			if ( ! StringUtils.isEmpty( listRequestsRequest.getRequestTypeName() ) ) {

				paramCounter++;
				pstmt.setString( paramCounter,  listRequestsRequest.getRequestTypeName()  );
			}

			//  Limit params

			if ( listRequestsRequest.getIndexStart() != null ) {

				paramCounter++;
				pstmt.setInt( paramCounter,  listRequestsRequest.getIndexStart() );
			} else {
				paramCounter++;
				pstmt.setInt( paramCounter, 0 );
			}

			if ( listRequestsRequest.getJobsReturnCountMax() != null
					&& listRequestsRequest.getJobsReturnCountMax() <= ServerCoreConstants.MAX_REQUESTS_RETURNED ) {

				paramCounter++;
				pstmt.setInt( paramCounter,  listRequestsRequest.getJobsReturnCountMax() );
			} else {
				paramCounter++;
				pstmt.setInt( paramCounter,  ServerCoreConstants.MAX_JOBS_RETURNED );
			}


			rs = pstmt.executeQuery();

			while ( rs.next() ) {

				int requestId = rs.getInt( "request_id" );

				requestIdList.add( requestId );
			}

		} catch (Throwable sqlEx) {

			log.error( method + ":Exception: \nSQL = '" + sql
					+ "\n Exception message: " + sqlEx.toString() + '.', sqlEx);

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


		return requestIdList;
	}


}
