package org.jobcenter.jdbc;

import java.sql.Connection;

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
	 * @return
	 * @throws Throwable
	 */
	protected Connection getConnection( ) throws Exception {

		Connection connection = null;

		connection = sessionFactory.getCurrentSession().connection();

		return connection;
	}
}
