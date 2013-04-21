package org.jobcenter.jdbc;


import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

/**
 * Base class for JDBC based DAO
 *
 */
public class JDBCBaseDAO {

	private static Logger log = Logger.getLogger(JDBCBaseDAO.class);



	/**
	 * Hibernate Session factory, can get a JDBC connection from it
	 */
	private SessionFactory  sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * Run a JDBC statement using the SQL connection from the Hibernate session
	 * @param jdbcBaseWork
	 */
	protected void doJDBCWork( final JDBCBaseWorkIF jdbcBaseWork ) {
			
		sessionFactory.getCurrentSession().doWork( new org.hibernate.jdbc.Work() {
			public void execute(Connection connection) throws SQLException {

				jdbcBaseWork.execute( connection );
			}
		} );
		
	}
	


//	/**
//	 * @return
//	 * @throws Throwable
//	 */
//	protected Connection getConnection( ) throws Exception {
//
//		Connection connection = null;
//
//		connection = sessionFactory.getCurrentSession().connection();
//
//		return connection;
//	}
//	
//	protected void releaseConnection( Connection connection ) {
//		
//		//  Closed by Hibernate Open Session in view
//		
////		if (connection != null) {
////			try {
////				connection.close();
////			} catch (SQLException ex) {
////				// ignore
////			}
////		}
//	
//	}
	
	
}
