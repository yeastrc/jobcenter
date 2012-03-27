package org.jobcenter.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.dto.JobType;

public class JobTypeJDBCDAOImpl extends JDBCBaseDAO implements JobTypeJDBCDAO {

	private static Logger log = Logger.getLogger(JobTypeJDBCDAOImpl.class);

	private static String
	getJobTypesQuerySqlString
	= "SELECT * FROM job_type "
		+ " ORDER BY name";


	/**
	 * @return
	 * @throws Exception
	 */
	@Override
	public  List<JobType> getJobTypes( )
	{
		final String method = "getJobTypes";

		if ( log.isDebugEnabled() ) {
			log.debug( "Entering " + method );
		}

		List<JobType> jobTypes = new ArrayList<JobType>();

		JobType jobType = null;

		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rs = null;

		try {

			connection = getConnection( );

			//			connection = JobManagerDBConnectionFactory.getConnection( );



			pstmt = connection.prepareStatement( getJobTypesQuerySqlString );

			rs = pstmt.executeQuery();

			while ( rs.next() ) {

				jobType = new JobType();

				jobType.setId( rs.getInt( "id" ) );
				jobType.setPriority( rs.getInt( "priority" ) );
				jobType.setName( rs.getString( "name" ) );
				jobType.setDescription( rs.getString( "description" ) );
				jobType.setModuleName( rs.getString( "module_name" ) );
				jobType.setEnabled( rs.getBoolean( "enabled" ) );
				jobType.setMinimumModuleVersion( rs.getShort( "minimum_module_version" ) );

				jobTypes.add( jobType );
			}

		} catch (Exception sqlEx) {

			log.error( method + ":Exception: \nSQL = '" + getJobTypesQuerySqlString
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


		return jobTypes;
	}


}
