package org.jobcenter.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.dto.RequestTypeDTO;

public class RequestTypeJDBCDAOImpl extends JDBCBaseDAO implements RequestTypeJDBCDAO {

	private static Logger log = Logger.getLogger(RequestTypeJDBCDAOImpl.class);


	private static String
	getRequestTypesQuerySqlString
	= "SELECT * FROM request_type "
		+ " ORDER BY name";



	/**
	 * @return
	 * @throws Exception
	 */
	public  List<RequestTypeDTO> getRequestTypes( )
	{
		final String method = "getRequestTypes";

		if ( log.isDebugEnabled() ) {
			log.debug( "Entering " + method );
		}

		List<RequestTypeDTO> requestTypes = new ArrayList<RequestTypeDTO>();

		RequestTypeDTO requestType = null;

		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rs = null;

		try {

			connection = getConnection( );

//			connection = JobCenterDBConnectionFactory.getConnection( );

			pstmt = connection.prepareStatement( getRequestTypesQuerySqlString );

			rs = pstmt.executeQuery();

			while ( rs.next() ) {

				requestType = new RequestTypeDTO();

				requestType.setId( rs.getInt( "id" ) );
				requestType.setName( rs.getString( "name" ) );

				requestTypes.add( requestType );

			}

		} catch (Exception sqlEx) {

			log.error( method + ":Exception: \nSQL = '" + getRequestTypesQuerySqlString
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


		return requestTypes;
	}


}
