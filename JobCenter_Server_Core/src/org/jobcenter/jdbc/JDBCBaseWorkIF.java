package org.jobcenter.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface of what is passed to JDBCBaseDAO to run a JDBC statement 
 *
 */
public interface JDBCBaseWorkIF {

	public void execute(Connection connection) throws SQLException;
}
