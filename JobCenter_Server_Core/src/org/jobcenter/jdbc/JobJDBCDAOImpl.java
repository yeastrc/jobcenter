package org.jobcenter.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jobcenter.constants.DBConstantsServerCore;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.constants.ServerCoreConstants;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.dto.RunMessageDTO;
import org.jobcenter.exception.RecordNotUpdatedException;
import org.jobcenter.request.ListJobsRequest;
import org.springframework.context.ApplicationContext;



public class JobJDBCDAOImpl extends JDBCBaseDAO implements JobJDBCDAO {

	private static Logger log = Logger.getLogger( JobJDBCDAOImpl.class);

	//  Used by the healthcheck servlet to get the bean
	public static JobJDBCDAO getFromApplicationContext(ApplicationContext ctx) {
		return (JobJDBCDAO) ctx.getBean("jobJDBCDAO");
	}




	/**
	 *
	 */
	private static String
	updateJobStatusOnIdAndStatusIdSqlString
	= "UPDATE job SET status_id = ?, db_record_version_number = ? "
		+ "   WHERE id = ? AND db_record_version_number = ? " ;


	/**
	 * @param job
	 * @param newJobStatus
	 * @throws Exception
	 */
	@Override
	public  void updateJobStatusOnIdAndDbRecordVersionNumber( final Job job, final int newJobStatus )
	{
		

		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {


				final String method = "WORK-updateJobStatus( Job job, int newJobStatus )";

				if ( job == null ) {

					throw new IllegalArgumentException( "in method method: " + method  + ", job == null" );
				}

				if ( log.isDebugEnabled() ) {
					log.debug( "Entering " + method );
				}

				PreparedStatement pstmt = null;

				int rowsUpdated = 0;

				//		Connection connection = null;

				try {
					//			connection = getConnection( );

					pstmt = connection.prepareStatement( updateJobStatusOnIdAndStatusIdSqlString );

					pstmt.setInt( 1, newJobStatus );

					pstmt.setInt( 2, job.getDbRecordVersionNumber() + 1 );

					pstmt.setInt( 3, job.getId() );

					pstmt.setInt( 4, job.getDbRecordVersionNumber() );

					rowsUpdated = pstmt.executeUpdate();

					if ( rowsUpdated <= 0 ) {

						log.info( method + ":No Rows Updated:  job.getId() = " + job.getId() + ", job.getDbRecordVersionNumber() = " + job.getDbRecordVersionNumber()
								+ " \nSQL = '" + updateJobStatusOnIdAndStatusIdSqlString );

						throw new RecordNotUpdatedException( "RecordNotUpdatedException" );
					}

					//  increment since did same on db using SQL
					job.setDbRecordVersionNumber( job.getDbRecordVersionNumber() + 1 );

				} catch (RecordNotUpdatedException ex) {

					throw ex;


				} catch (Throwable sqlEx) {

					log.error( method + ":Exception:  job.getId() = " + job.getId() + ", job.getDbRecordVersionNumber() = " + job.getDbRecordVersionNumber()
							+ " \nSQL = '" + updateJobStatusOnIdAndStatusIdSqlString
							+ "\n Exception message: " + sqlEx.toString() + '.', sqlEx);

					//  Wrap in RuntimeException for Spring Transactional rollback
					throw new RuntimeException( sqlEx );

				} finally {


					if (pstmt != null) {
						try {
							pstmt.close();
						} catch (SQLException ex) {
							// ignore
						}
					}
					
//					releaseConnection( connection );

				}
			}
		} );
	}


	



	/**
	 *
	 */
	private static String
	updateDelayUntilAndParamErrorCntOnIdSqlString
	= "UPDATE job SET delay_job_until = "
		+ " DATE_ADD( NOW(),INTERVAL " + ServerCoreConstants.PARAM_ERROR_DELAY_TIME + " SECOND), " 
		+ " param_error_retry_count = param_error_retry_count + 1 "
		+ "   WHERE id = ?" ;



	@Override
	public void updateDelayUntilAndParamErrorCntOnId( final int jobId ) {
		{
			

			super.doJDBCWork(  new JDBCBaseWorkIF() {
				public void execute(Connection connection) throws SQLException {


					final String method = "WORK-updateDelayUntilAndParamErrorCntOnId( Job job )";


					if ( log.isDebugEnabled() ) {
						log.debug( "Entering " + method );
					}

					PreparedStatement pstmt = null;

					int rowsUpdated = 0;

					//			Connection connection = null;

					try {
						//				connection = getConnection( );

						pstmt = connection.prepareStatement( updateDelayUntilAndParamErrorCntOnIdSqlString );

						pstmt.setInt( 1, jobId );

						rowsUpdated = pstmt.executeUpdate();

						if ( rowsUpdated <= 0 ) {

							log.info( method + ":No Rows Updated:  jobId = " + jobId
									+ " \nSQL = '" + updateDelayUntilAndParamErrorCntOnIdSqlString );

							throw new RecordNotUpdatedException( "RecordNotUpdatedException" );
						}


					} catch (RecordNotUpdatedException ex) {

						throw ex;


					} catch (Throwable sqlEx) {

						log.error( method + ":Exception:  job.getId() = " + jobId 
								+ " \nSQL = '" + updateJobStatusOnIdAndStatusIdSqlString
								+ "\n Exception message: " + sqlEx.toString() + '.', sqlEx);

						//  Wrap in RuntimeException for Spring Transactional rollback
						throw new RuntimeException( sqlEx );

					} finally {


						if (pstmt != null) {
							try {
								pstmt.close();
							} catch (SQLException ex) {
								// ignore
							}
						}
						
//						releaseConnection( connection );

					}
				}
			} );
		}

	}






	/**
	 *
	 */
	private static String
	getJobParametersQuerySqlString
	= "SELECT * FROM job_parameter WHERE job_id = ? ";


	/**
	 * @param job
	 * @param newJobStatus
	 * @throws Exception
	 */
	@Override
	public  void retrieveJobParameters( final Job job )
	{

		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {


				final String method = "WORK-retrieveJobParameters( Job job )";

				if ( job == null ) {

					throw new IllegalArgumentException( "in method method: " + method  + ", job == null" );
				}

				if ( log.isDebugEnabled() ) {
					log.debug( "Entering " + method );
				}


				PreparedStatement pstmt = null;

				ResultSet rs = null;


				//		Connection connection = null;

				try {
					//			connection = getConnection( );

					//  Retrieve job parameters

					Map<String, String> jobParameters = new HashMap<String, String>();

					job.setJobParameters( jobParameters );


					pstmt = connection.prepareStatement( getJobParametersQuerySqlString );

					pstmt.setInt( 1, job.getId() );

					rs = pstmt.executeQuery();

					while ( rs.next() ) {

						String paramKey = rs.getString( "key" );

						String paramValue = rs.getString( "value" );

						jobParameters.put(paramKey, paramValue);
					}

				} catch (Throwable sqlEx) {

					log.error( method + ":Exception:   job.getId() = " + job.getId() + "\nSQL = '" + getJobParametersQuerySqlString
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

//					releaseConnection( connection );
				}

			}
		} );
	}



	/**
	 * @param nodeId
	 * @param job
	 * @return
	 */
	@Override
	public  RunDTO insertRunTableRecord( int nodeId, Job job )
	{
		return insertRunTableRecord( nodeId, job, JobStatusValuesConstants.JOB_STATUS_RUNNING );
	}

	private static final String
	addRunRecordSqlString
	= "INSERT INTO run "
		+ " ( node_id, job_id, status_id, start_date ) "
		+ " VALUES ( ?, ?, ?, NOW() ) ";


	/**
	 * @param nodeId
	 * @param job
	 * @param statusId
	 * @return
	 */
	@Override
	public  RunDTO insertRunTableRecord( final int nodeId, final Job job, final int statusId )
	{

		final RunDTO[]  runDTOResult = { null };
		
		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {


				final String method = "WORK-insertRunTableRecord";


				if ( job == null ) {

					throw new IllegalArgumentException( "in method method: " + method  + ", job == null" );
				}


				if ( log.isDebugEnabled() ) {
					log.debug( "Entering " + method );
				}

				RunDTO runDTO = null;

				int rowsInserted = 0;

				PreparedStatement pstmt = null;

				ResultSet rsGenKeys = null;

				//		Connection connection = null;


				try {

					//			connection = getConnection( );

					pstmt = connection.prepareStatement( addRunRecordSqlString, Statement.RETURN_GENERATED_KEYS );

					pstmt.setInt( 1, nodeId);

					pstmt.setInt( 2, job.getId() );
					pstmt.setInt( 3, statusId );

					rowsInserted = pstmt.executeUpdate();

					rsGenKeys = pstmt.getGeneratedKeys();


					if ( rsGenKeys.next() ) {

						int insertedId = rsGenKeys.getInt( 1 );

						runDTO = new RunDTO();

						runDTO.setId( insertedId );

						runDTO.setJobId( job.getId() );
						runDTO.setNodeId( nodeId );
						runDTO.setStartDate( new Date() );
						runDTO.setStatusId( JobStatusValuesConstants.JOB_STATUS_RUNNING );
					}

				} catch (Throwable sqlEx) {

					log.error( method + ":Exception:   job.getId() = " + job.getId() + "\nSQL = '" + addRunRecordSqlString
							+ "\n Exception message: " + sqlEx.toString() + '.', sqlEx);

					//  Wrap in RuntimeException for Spring Transactional rollback
					throw new RuntimeException( sqlEx );

				} finally {


					if (rsGenKeys != null) {
						try {
							rsGenKeys.close();
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

//					releaseConnection( connection );

				}
				
				runDTOResult[ 0 ] = runDTO;
			}
		} );

		return runDTOResult[ 0 ];
	}




	private static final String
	getRunIdsByJobIdSqlString
	= "SELECT id FROM run WHERE job_id = ? ORDER BY id ";


	/**
	 * @param jobId
	 * @return
	 */
	@Override
	public  List<Integer> getRunIdsByJobId( final int jobId )
	{

		final List<Integer> runIds = new ArrayList<Integer>();

		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {


				final String method = "WORK-getRunIdsByJobId( int jobId )";

				if ( log.isDebugEnabled() ) {
					log.debug( "Entering " + method );
				}

				PreparedStatement pstmt = null;

				ResultSet rs = null;


				//		Connection connection = null;

				try {
					//			connection = getConnection( );


					pstmt = connection.prepareStatement( getRunIdsByJobIdSqlString );

					pstmt.setInt( 1, jobId );

					rs = pstmt.executeQuery();

					while ( rs.next() ) {

						runIds.add( rs.getInt( "id" ) );
					}

				} catch (Throwable sqlEx) {

					log.error( method + ":Exception:   jobId = " + jobId + "\nSQL = '" + getRunIdsByJobIdSqlString
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

//					releaseConnection( connection );
				}
			}
		} );

		return runIds;

	}










	////////////////////////////////////////////////////


	private static final String
	addRunMessageRecordSqlString
	= "INSERT INTO run_msg "
		+ " ( run_id, type, message ) "
		+ " VALUES ( ?, ?, ? ) ";



	/**
	 * @param runMessage
	 * @param run
	 * @throws Exception
	 */
	public  void insertRunMessageFromModuleRecord( final RunMessageDTO runMessage, final RunDTO run )
	{

		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {


				final String method = "WORK-insertRunMessageFromModuleRecord";

				if ( runMessage == null ) {

					throw new IllegalArgumentException( "in method method: " + method  + ", runMessageFromModule == null" );
				}

				if ( run == null ) {

					throw new IllegalArgumentException( "in method method: " + method  + ", run == null" );
				}

				if ( log.isDebugEnabled() ) {
					log.debug( "Entering " + method );
				}


				int rowsInserted = 0;

//				Connection connection = null;

				PreparedStatement pstmt = null;

				ResultSet rsGenKeys = null;

				try {

//					connection = getConnection( );

					pstmt = connection.prepareStatement( addRunMessageRecordSqlString, Statement.RETURN_GENERATED_KEYS );

					pstmt.setInt( 1, run.getId() );

					pstmt.setInt( 2, runMessage.getType() );
					pstmt.setString( 3, runMessage.getMessage() );

					rowsInserted = pstmt.executeUpdate();

					rsGenKeys = pstmt.getGeneratedKeys();


					if ( rsGenKeys.next() ) {

						int insertedId = rsGenKeys.getInt( 1 );

						runMessage.setId( insertedId );
					}

				} catch (Throwable sqlEx) {

					log.error( method + ":Exception:   run.getId() = " + run.getId() + "\nSQL = '" + addRunMessageRecordSqlString
							+ "\n Exception message: " + sqlEx.toString() + '.', sqlEx);

					//  Wrap in RuntimeException for Spring Transactional rollback
					throw new RuntimeException( sqlEx );

				} finally {

					if (rsGenKeys != null) {
						try {
							rsGenKeys.close();
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

//					releaseConnection( connection );

				}
			}
		} );
	}


	//////////////////////////////////////////////////////////////////////////////



	/**
	 *
	 */
	private static String
	insertRunOutputParameterSqlString
	= "INSERT INTO run_output_params ( `key`, value, run_id )  "
		+ "  VALUES ( ?, ?, ? ) " ;


	/* (non-Javadoc)
	 * @see org.jobcenter.jdbc.JobJDBCDAO#insertRunOutputParam(java.lang.String, java.lang.String, org.jobcenter.dto.RunDTO)
	 */
	@Override
	public  void insertRunOutputParam ( final String key, final String value, final RunDTO run ) {

		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {


				final String method = "WORK-insertRunOutputParam( String key, String value, RunDTO run )";

				if ( key == null || key.isEmpty()  ) {

					throw new IllegalArgumentException( "in method method: " + method  + ", key == null or empty " );
				}

				if ( log.isDebugEnabled() ) {
					log.debug( "Entering " + method );
				}

				PreparedStatement pstmt = null;


				ResultSet rsKeys = null;


				int rowsUpdated = 0;

//				Connection connection = null;


				try {

//					connection = getConnection( );


					pstmt = connection.prepareStatement( insertRunOutputParameterSqlString );

					pstmt.setString( 1, key );

					pstmt.setString( 2, value );

					pstmt.setInt( 3, run.getId() );

					rowsUpdated = pstmt.executeUpdate();

				} catch (Throwable sqlEx) {

					log.error( method + ":Exception:  runId = " + run.getId() + ", key = " + key
							+ " \nSQL = '" + insertRunOutputParameterSqlString
							+ "\n Exception message: " + sqlEx.toString() + '.', sqlEx);

					//  Wrap in RuntimeException for Spring Transactional rollback
					throw new RuntimeException( sqlEx );

				} finally {

					if (rsKeys != null) {
						try {
							rsKeys.close();
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


//					releaseConnection( connection );
				}
			}
		} );
	}




	/**
	 *
	 */
	private static String
	getRunOutputParamsQuerySqlString
	= "SELECT * FROM run_output_params WHERE run_id = ? ";


	/**
	 * @param run
	 */
	@Override
	public  void retrieveRunOutputParams( final RunDTO run )
	{
		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {


				final String method = "WORK-retrieveRunOutputParams( RunDTO run )";

				if ( run == null ) {

					throw new IllegalArgumentException( "in method method: " + method  + ", run == null" );
				}

				if ( log.isDebugEnabled() ) {
					log.debug( "Entering " + method );
				}


				PreparedStatement pstmt = null;

				ResultSet rs = null;


//				Connection connection = null;

				try {
//					connection = getConnection( );

					//  Retrieve runOutputParams

					Map<String, String> runOutputParams = new HashMap<String, String>();

					run.setRunOutputParams( runOutputParams );


					pstmt = connection.prepareStatement( getRunOutputParamsQuerySqlString );

					pstmt.setInt( 1, run.getId() );

					rs = pstmt.executeQuery();

					while ( rs.next() ) {

						String paramKey = rs.getString( "key" );

						String paramValue = rs.getString( "value" );

						runOutputParams.put(paramKey, paramValue);
					}

				} catch (Throwable sqlEx) {

					log.error( method + ":Exception:   run.getId() = " + run.getId() + "\nSQL = '" + getRunOutputParamsQuerySqlString
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

//					releaseConnection( connection );
				}
			}
		} );

	}



	//////////////////////////////////////////////////////////////////////////////



	private static String
	insertRequestSqlString
	= "INSERT INTO request ( type )  "
		+ "  VALUES ( ? ) " ;

	/**
	 * @param requestType
	 * @return
	 * @throws Exception
	 */
	public  int insertRequest( final int requestType )
	{

		final int[] requestIdResult = { 0 };

		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {


				final String method = "insertRequest( int requestType )";

				int requestId = 0;

				if ( log.isDebugEnabled() ) {
					log.debug( "Entering " + method );
				}

				PreparedStatement pstmt = null;


				ResultSet rsKeys = null;


				int rowsUpdated = 0;

				//		Connection connection = null;


				try {

					//			connection = getConnection( );

					pstmt = connection.prepareStatement( insertRequestSqlString, Statement.RETURN_GENERATED_KEYS );

					pstmt.setInt( 1, requestType);

					rowsUpdated = pstmt.executeUpdate();

					rsKeys = pstmt.getGeneratedKeys();


					if ( rsKeys.next() ) {

						requestId = rsKeys.getInt( 1 );

					} else {

						String msg =  "Failed retrieval of generated id for insertion of job record." ;

						log.error( msg );

						throw new Exception( msg );
					}

				} catch (Throwable sqlEx) {

					log.error( method + ":Exception:  requestType = " + requestType
							+ " \nSQL = '" + insertRequestSqlString
							+ "\n Exception message: " + sqlEx.toString() + '.', sqlEx);

					//  Wrap in RuntimeException for Spring Transactional rollback
					throw new RuntimeException( sqlEx );

				} finally {

					if (rsKeys != null) {
						try {
							rsKeys.close();
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

					//			releaseConnection( connection );
				}

				requestIdResult[ 0 ] = requestId;
	        }
		} );

		return requestIdResult[ 0 ];

	}



	private static String
	insertJobSqlString
	= "INSERT INTO job ( request_id, job_type_id, submitter, priority, required_execution_threads, status_id, job_parameter_count, insert_complete, submit_date  )  "
		+ "  VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, NOW() ) " ;

	/**
	 * @param job
	 * @throws Exception
	 */
	@Override
	public  void insertJob ( final Job job )
	{
		
		super.doJDBCWork(  new JDBCBaseWorkIF() {
	        public void execute(Connection connection) throws SQLException {
	        	
	        	insertJob( job, connection );
	        }
		} );
	}

	@Override
	public void insertJob ( final Job job, final Connection connection ) {

		final String method = "WORM-insertJob( Job job )";

		if ( job == null  ) {

			throw new IllegalArgumentException( "in method method: " + method  + ", job == null " );
		}

		if ( log.isDebugEnabled() ) {
			log.debug( "Entering " + method );
		}

		PreparedStatement pstmt = null;


		ResultSet rsKeys = null;


		int rowsUpdated = 0;

		//		Connection connection = null;


		try {

			//			connection = getConnection( );

			pstmt = connection.prepareStatement( insertJobSqlString, Statement.RETURN_GENERATED_KEYS );

			int paramIndex = 0;

			paramIndex++;    
			pstmt.setInt( paramIndex, job.getRequestId() );

			paramIndex++;    
			pstmt.setInt( paramIndex, job.getJobTypeId() );

			paramIndex++;    
			pstmt.setString( paramIndex, job.getSubmitter() );

			paramIndex++;    
			pstmt.setInt( paramIndex, job.getPriority() );

			paramIndex++;    

			if ( job.getRequiredExecutionThreads() == null ) {

				pstmt.setNull( paramIndex,  java.sql.Types.INTEGER );
			} else {
				pstmt.setInt( paramIndex, job.getRequiredExecutionThreads() );
			}


			paramIndex++;    
			pstmt.setInt( paramIndex, job.getStatusId() );

			paramIndex++;    
			pstmt.setInt( paramIndex, job.getJobParameterCount() );

			paramIndex++;    
			pstmt.setString( paramIndex, job.getInsertComplete() );

			rowsUpdated = pstmt.executeUpdate();

			// set to 1 since DB default
			job.setDbRecordVersionNumber( 1 );


			rsKeys = pstmt.getGeneratedKeys();


			if ( rsKeys.next() ) {

				int id = rsKeys.getInt( 1 );

				job.setId( id );

			} else {

				String msg =  "Failed retrieval of generated id for insertion of job record." ;

				log.error( msg );

				throw new Exception( msg );
			}

		} catch (Throwable sqlEx) {

			log.error( method + ":Exception:  job.getJobTypeId() = " + job.getJobTypeId() + ", job.getSubmitter() = " + job.getSubmitter()
					+ " \nSQL = '" + insertJobSqlString
					+ "\n Exception message: " + sqlEx.toString() + '.', sqlEx);

			//  Wrap in RuntimeException for Spring Transactional rollback
			throw new RuntimeException( sqlEx );

		} finally {

			if (rsKeys != null) {
				try {
					rsKeys.close();
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
	}



	/**
	 *
	 */
	private static String
	markJobInsertCompleteSqlString
	= "UPDATE job SET insert_complete = '" + DBConstantsServerCore.JobTableInsertCompleteT + "' WHERE id = ?" ;

	/**
	 * @param job
	 * @param newJobStatus
	 * @throws Exception
	 */
	public  void markJobInsertComplete ( final Job job )
	{
		

		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {

				markJobInsertComplete( job, connection );
			}
		});
	}
	
	public  void markJobInsertComplete ( Job job, final Connection connection ) {

		final String method = "WORK-markJobInsertComplete( Job job )";

		if ( job == null  ) {

			throw new IllegalArgumentException( "in method method: " + method  + ", job == null " );
		}

		if ( log.isDebugEnabled() ) {
			log.debug( "Entering " + method );
		}

		PreparedStatement pstmt = null;


		int rowsUpdated = 0;

		//		Connection connection = null;


		try {

			//			connection = getConnection( );

			pstmt = connection.prepareStatement( markJobInsertCompleteSqlString );

			pstmt.setInt( 1, job.getId() );

			rowsUpdated = pstmt.executeUpdate();

		} catch (Throwable sqlEx) {

			log.error( method + ":Exception:  job.getJobTypeId() = " + job.getJobTypeId() + ", job.getSubmitter() = " + job.getSubmitter()
					+ " \nSQL = '" + insertJobSqlString
					+ "\n Exception message: " + sqlEx.toString() + '.', sqlEx);

			//  Wrap in RuntimeException for Spring Transactional rollback
			throw new RuntimeException( sqlEx );

		} finally {

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}

		}
	}




	/**
	 *
	 */
	private static String
	insertJobParameterSqlString
	= "INSERT INTO job_parameter ( `key`, value, job_id )  "
		+ "  VALUES ( ?, ?, ? ) " ;


	/* (non-Javadoc)
	 * @see org.jobcenter.jdbc.JobJDBCDAO#insertJobParameter(java.lang.String, java.lang.String, int)
	 */
	public  void insertJobParameter ( final String key, final String value, final int jobId )
	{

		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {

				insertJobParameter( key, value, jobId, connection );
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.jobcenter.jdbc.JobJDBCDAO#insertJobParameter(java.lang.String, java.lang.String, int, java.sql.Connection)
	 */
	public  void insertJobParameter ( final String key, final String value, final int jobId, final Connection connection ) {
				
		final String method = "WORM-insertJobParameters( String key, String value, int jobId )";

		if ( key == null || key.isEmpty()  ) {

			throw new IllegalArgumentException( "in method method: " + method  + ", key == null or empty " );
		}

		if ( log.isDebugEnabled() ) {
			log.debug( "Entering " + method );
		}

		PreparedStatement pstmt = null;

		int rowsUpdated = 0;

		//		Connection connection = null;


		try {

			//			connection = getConnection( );


			pstmt = connection.prepareStatement( insertJobParameterSqlString );

			pstmt.setString( 1, key );

			pstmt.setString( 2, value );

			pstmt.setInt( 3, jobId );

			rowsUpdated = pstmt.executeUpdate();


		} catch (Throwable sqlEx) {

			log.error( method + ":Exception:  jobId = " + jobId + ", key = " + key
					+ " \nSQL = '" + insertJobParameterSqlString
					+ "\n Exception message: " + sqlEx.toString() + '.', sqlEx);

			//  Wrap in RuntimeException for Spring Transactional rollback
			throw new RuntimeException( sqlEx );

		} finally {


			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}


			//			releaseConnection( connection );
		}
	}
	


	/**
	 *
	 */
	private static String
	insertJobDependcySqlString
	= "INSERT INTO job_dependencies ( dependent_job, dependee_job )  "
		+ "  VALUES ( ?, ? ) " ;

	/* (non-Javadoc)
	 * @see org.jobcenter.jdbc.JobJDBCDAO#insertJobParameter(java.lang.String, java.lang.String, int)
	 */
	public  void insertJobDependcy( final int jobId, final int jobDependency )
	{

		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {

				insertJobDependcy( jobId, jobDependency, connection );
			}
		});
	}
				
	/* (non-Javadoc)
	 * @see org.jobcenter.jdbc.JobJDBCDAO#insertJobDependcy(int, int, java.sql.Connection)
	 */
	public  void insertJobDependcy( int jobId, int jobDependency, final Connection connection ) {

		final String method = "WORM-insertJobDependcy( final Integer jobId, final Integer jobDependency )";

		if ( log.isDebugEnabled() ) {
			log.debug( "Entering " + method );
		}

		PreparedStatement pstmt = null;

		int rowsUpdated = 0;

		//		Connection connection = null;


		try {

			//			connection = getConnection( );


			pstmt = connection.prepareStatement( insertJobDependcySqlString );

			pstmt.setInt( 1, jobId );

			pstmt.setInt( 2, jobDependency );

			rowsUpdated = pstmt.executeUpdate();


		} catch (Throwable sqlEx) {

			log.error( method + ":Exception:  jobId = " + jobId + ", jobDependency = " + jobDependency
					+ " \nSQL = '" + insertJobDependcySqlString
					+ "\n Exception message: " + sqlEx.toString() + '.', sqlEx);

			//  Wrap in RuntimeException for Spring Transactional rollback
			throw new RuntimeException( sqlEx );

		} finally {

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}


			//			releaseConnection( connection );
		}
	}
	


	/**
	 *
	 */
	private static String
	sqlHealthCheck
	= "SELECT *  FROM job LIMIT 1";


	/**
	 *  Does a retrieve from job table to confirm DB access and that table exists
	 *
	 */
	@Override
	public  void healthCheck ( )
	{
		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {


				final String method = "WORK-healthCheck( )";

				if ( log.isDebugEnabled() ) {
					log.debug( "Entering " + method );
				}

//				Connection connection = null;

				PreparedStatement pstmt = null;
				ResultSet rs = null;

				try {
//					connection = getConnection( );

					pstmt = connection.prepareStatement( sqlHealthCheck );

					rs = pstmt.executeQuery();

					if ( rs.next() ) {



					}

				} catch (Throwable sqlEx) {

					log.error( method + ":Exception: SQL = '" + sqlHealthCheck
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


//					releaseConnection( connection );

				}
			}
		} );
	}


	private static String
	getRequestFromIdAndRequestTypeQuerySqlString
	= "SELECT id from request where id = ? and type = ?";


	/**
	 * @param id
	 * @param type
	 * @return
	 */
	@Override
	public  Integer getRequestFromIdAndRequestType( final int id, final int type )
	{
		final Integer[] requestIdSearchResult = { null };
		
		
		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {


				final String method = "WORK-getRequestFromIdAndRequestType";

				if ( log.isDebugEnabled() ) {
					log.debug( "Entering " + method );
				}


//				Connection connection = null;

				PreparedStatement pstmt = null;

				ResultSet rs = null;

				try {

//					connection = getConnection( );

					pstmt = connection.prepareStatement( getRequestFromIdAndRequestTypeQuerySqlString );

					pstmt.setInt( 1, id );
					pstmt.setInt( 2, type );

					rs = pstmt.executeQuery();

					if ( rs.next() ) {

						requestIdSearchResult[ 0 ] = new Integer( rs.getInt( "id" ) );
					}

				} catch (Throwable sqlEx) {

					log.error( method + ":Exception: \nSQL = '" + getRequestFromIdAndRequestTypeQuerySqlString
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



//					releaseConnection( connection );
				}
			}
		} );


		return requestIdSearchResult[ 0 ];
	}


	//	private static String
	//	getJobsListQuerySqlString
	//	= "SELECT job.id AS job_id, job.request_id, job.job_type_id, job.priority AS job_priority, job.status_id, job.submit_date, job.submitter "
	//		+ " , job.db_record_version_number AS job_db_record_version_number "
	//		+ " , jt.id AS jt_id, jt.name AS jt_name, jt.description AS jt_description "
	//		+ " , status.name AS status_name, status.description AS status_description "
	//		+ " , rt.name AS rt_name "
	//
	//		+ "   FROM job "
	//		+ "          LEFT OUTER JOIN job_type AS jt ON job.job_type_id = jt.id  "
	//		+ "          LEFT OUTER JOIN status ON job.status_id = status.id  "
	//		+ "          LEFT OUTER JOIN request ON job.request_id = request.id  "
	//		+ "          LEFT OUTER JOIN request_type AS rt ON request.type = rt.id  ";
	//
	//
	//	/**
	//	 * @param listJobsRequest
	//	 * @return
	//	 */
	//	@Override
	//	public  List<Job>  getJobsList( ListJobsRequest listJobsRequest )
	//	{
	//		final String method = "getJobsList";
	//
	//		if ( log.isDebugEnabled() ) {
	//			log.debug( "Entering " + method );
	//		}
	//
	//		List<Job> jobList = new ArrayList<Job>();
	//
	//		Job job = null;
	//
	//		Connection connection = null;
	//
	//		PreparedStatement pstmt = null;
	//
	//		ResultSet rs = null;
	//
	//		String sql = null;
	//
	//		try {
	//
	//			connection = getConnection( );
	//
	//			StringBuilder sqlSB = new StringBuilder( 1000 );
	//
	//			sqlSB.append( getJobsListQuerySqlString );
	//
	//			boolean first = true;
	//
	//			Set<Integer> statusIds = listJobsRequest.getStatusIds();
	//
	//			if ( statusIds != null && ( ! statusIds.isEmpty() ) ) {
	//
	//				if ( first ) {
	//					first = false;
	//					sqlSB.append( " where " );
	//				} else {
	//					sqlSB.append( " AND " );
	//				}
	//
	//				sqlSB.append( " ( status_id IN ( " );
	//
	//				boolean firstStatus = true;
	//
	//				for ( int status : statusIds ) {
	//
	//					if ( firstStatus ) {
	//						firstStatus = false;
	//					} else {
	//
	//						sqlSB.append( ", " );
	//					}
	//					sqlSB.append( status );
	//				}
	//
	//				sqlSB.append( " ) ) " );
	//			}
	//
	//			String jobTypeName = listJobsRequest.getJobTypeName();
	//
	//			if ( ! StringUtils.isEmpty( jobTypeName ) ) {
	//
	//				if ( first ) {
	//					first = false;
	//					sqlSB.append( " where " );
	//				} else {
	//					sqlSB.append( " AND " );
	//				}
	//				sqlSB.append( " jt.name = ? " );
	//			}
	//
	//			if ( listJobsRequest.getRequestId() != null ) {
	//
	//				if ( first ) {
	//					first = false;
	//					sqlSB.append( " where " );
	//				} else {
	//					sqlSB.append( " AND " );
	//				}
	//				sqlSB.append( " job.request_id = ? " );
	//			}
	//
	//			if ( ! StringUtils.isEmpty( listJobsRequest.getRequestTypeName() ) ) {
	//
	//				if ( first ) {
	//					first = false;
	//					sqlSB.append( " where " );
	//				} else {
	//					sqlSB.append( " AND " );
	//				}
	//				sqlSB.append( " rt.name = ? " );
	//			}
	//
	//
	//			sqlSB.append( "ORDER BY  job.id DESC  LIMIT ?, ?" );
	//
	//			sql = sqlSB.toString();
	//
	//			pstmt = connection.prepareStatement( sql );
	//
	//			int paramCounter = 0;
	//
	//			// search params
	//
	//			if ( ! StringUtils.isEmpty( jobTypeName ) ) {
	//
	//				paramCounter++;
	//				pstmt.setString( paramCounter, jobTypeName );
	//			}
	//
	//			if ( listJobsRequest.getRequestId() != null ) {
	//
	//				paramCounter++;
	//				pstmt.setInt( paramCounter,  listJobsRequest.getRequestId()  );
	//			}
	//
	//			if ( ! StringUtils.isEmpty( listJobsRequest.getRequestTypeName() ) ) {
	//
	//				paramCounter++;
	//				pstmt.setString( paramCounter,  listJobsRequest.getRequestTypeName()  );
	//			}
	//
	//			//  Limit params
	//
	//			if ( listJobsRequest.getIndexStart() != null ) {
	//
	//				paramCounter++;
	//				pstmt.setInt( paramCounter,  listJobsRequest.getIndexStart() );
	//			} else {
	//				paramCounter++;
	//				pstmt.setInt( paramCounter, 0 );
	//			}
	//
	//			if ( listJobsRequest.getJobsReturnCountMax() != null
	//					&& listJobsRequest.getJobsReturnCountMax() <= ServerCoreConstants.MAX_JOBS_RETURNED ) {
	//
	//				paramCounter++;
	//				pstmt.setInt( paramCounter,  listJobsRequest.getJobsReturnCountMax() );
	//			} else {
	//				paramCounter++;
	//				pstmt.setInt( paramCounter,  ServerCoreConstants.MAX_JOBS_RETURNED );
	//			}
	//
	//
	//			rs = pstmt.executeQuery();
	//
	//			while ( rs.next() ) {
	//
	//				int job_id = rs.getInt( "job_id" );
	//
	//				int requestId = rs.getInt( "request_id" );
	//
	//				int jobTypeId = rs.getInt( "job_type_id" );
	//
	//				int priority = rs.getInt( "job_priority" );
	//
	//				int statusId = rs.getInt( "status_id" );
	//
	//				Date submitDate = rs.getTimestamp( "submit_date" );
	//
	//				String submitter = rs.getString( "submitter" );
	//
	//				job = new Job();
	//
	//				job.setId( job_id );
	//				job.setRequestId( requestId );
	//				job.setJobTypeId( jobTypeId );
	//				job.setPriority( priority );
	//				job.setStatusId( statusId );
	//
	//				job.setSubmitDate( submitDate );
	//				job.setSubmitter( submitter );
	//
	//				// Current database record version number, for optimistic locking version tracking
	//
	//				job.setDbRecordVersionNumber(  rs.getInt( "job_db_record_version_number" ) );
	//
	//
	//				JobType jobType = new JobType();
	//
	//				job.setJobType( jobType );
	//
	//				int jobTypeRecordId = rs.getInt( "jt_id" );
	//
	//				jobType.setId( jobTypeRecordId );
	//
	//				jobType.setName( rs.getString( "jt_name" ) );
	//
	//				StatusDTO statusDTO = new StatusDTO();
	//
	//				job.setStatus( statusDTO );
	//
	//				statusDTO.setId( statusId );
	//
	//				statusDTO.setName( rs.getString( "status_name" ) );
	//				statusDTO.setDescription( rs.getString( "status_description" ) );
	//
	//				jobList.add( job );
	//			}
	//
	//		} catch (Throwable sqlEx) {
	//
	//			log.error( method + ":Exception: \nSQL = '" + sql
	//					+ "\n Exception message: " + sqlEx.toString() + '.', sqlEx);
	//
	//			//  Wrap in RuntimeException for Spring Transactional rollback
	//			throw new RuntimeException( sqlEx );
	//
	//		} finally {
	//
	//			if (rs != null) {
	//				try {
	//					rs.close();
	//				} catch (SQLException ex) {
	//					// ignore
	//				}
	//			}
	//
	//			if (pstmt != null) {
	//				try {
	//					pstmt.close();
	//				} catch (SQLException ex) {
	//					// ignore
	//				}
	//			}
	//
	//
	//	releaseConnection( connection );
	//		}
	//
	//
	//		return jobList;
	//	}


	private static String
	getJobsListQuerySqlString
	=  "   FROM job "
		+ "          LEFT OUTER JOIN job_type AS jt ON job.job_type_id = jt.id  "
		+ "          LEFT OUTER JOIN status ON job.status_id = status.id  "
		+ "          LEFT OUTER JOIN request ON job.request_id = request.id  "
		+ "          LEFT OUTER JOIN request_type AS rt ON request.type = rt.id  ";



	@Override
	public  int  getJobCount( final ListJobsRequest listJobsRequest )
	{

		final int[] jobCount = { 0 };
		
		
		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {


				final String method = "WORK-getJobIdList";

				if ( log.isDebugEnabled() ) {
					log.debug( "Entering " + method );
				}

//				Connection connection = null;

				PreparedStatement pstmt = null;

				ResultSet rs = null;

				String sql = null;

				try {

//					connection = getConnection( );

					StringBuilder sqlSB = new StringBuilder( 1000 );

					sqlSB.append( "SELECT COUNT(job.id) AS job_count " );

					pstmt = getJobListPreparedStmt( sqlSB, listJobsRequest, true /* getCount */, connection );

					sql = sqlSB.toString();

					rs = pstmt.executeQuery();

					while ( rs.next() ) {

						jobCount[0] = rs.getInt( "job_count" );
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



//					releaseConnection( connection );
				}
			}
		} );


		return jobCount[ 0 ];
	}



	/**
	 * @param listJobsRequest
	 * @return
	 */
	@Override
	public  List<Integer>  getJobIdList( final ListJobsRequest listJobsRequest )
	{

		final List<Integer> jobIdList = new ArrayList<Integer>();

		
		super.doJDBCWork(  new JDBCBaseWorkIF() {
			public void execute(Connection connection) throws SQLException {


				final String method = "WORK-getJobIdList";

				if ( log.isDebugEnabled() ) {
					log.debug( "Entering " + method );
				}

//				Connection connection = null;

				PreparedStatement pstmt = null;

				ResultSet rs = null;

				String sql = null;

				try {

//					connection = getConnection( );

					StringBuilder sqlSB = new StringBuilder( 1000 );

					sqlSB.append( "SELECT job.id " );

					pstmt = getJobListPreparedStmt( sqlSB, listJobsRequest, false /* getCount */, connection );

					sql = sqlSB.toString();

					rs = pstmt.executeQuery();

					while ( rs.next() ) {

						int job_id = rs.getInt( "id" );

						jobIdList.add( job_id );
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



//					releaseConnection( connection );
				}
			}
		} );


		return jobIdList;
	}



	/**
	 * @param sqlSB
	 * @param listJobsRequest
	 * @param getCount
	 * @param connection
	 * @return
	 * @throws Throwable
	 */
	private PreparedStatement getJobListPreparedStmt( StringBuilder sqlSB, ListJobsRequest listJobsRequest, boolean getCount, Connection connection ) throws Throwable {

		PreparedStatement pstmt = null;

		sqlSB.append( getJobsListQuerySqlString );

		boolean first = true;

		Set<Integer> statusIds = listJobsRequest.getStatusIds();

		if ( statusIds != null && ( ! statusIds.isEmpty() ) ) {

			if ( first ) {
				first = false;
				sqlSB.append( " where " );
			} else {
				sqlSB.append( " AND " );
			}

			sqlSB.append( " ( status_id IN ( " );

			boolean firstStatus = true;

			for ( int status : statusIds ) {

				if ( firstStatus ) {
					firstStatus = false;
				} else {

					sqlSB.append( ", " );
				}
				sqlSB.append( status );
			}

			sqlSB.append( " ) ) " );
		}

		Set<String> jobTypeNames = listJobsRequest.getJobTypeNames();

		if ( jobTypeNames != null && ( ! jobTypeNames.isEmpty() ) ) {

			if ( first ) {
				first = false;
				sqlSB.append( " where " );
			} else {
				sqlSB.append( " AND " );
			}


			sqlSB.append( " ( " );

			boolean firstJobTypeName = true;

			for ( String jobTypeName : jobTypeNames ) {

				if ( StringUtils.isEmpty( jobTypeName ) ) {

					String msg = "Illegal argument, a jobtypeName in jobTypeNames is empty";

					throw new IllegalArgumentException( msg );
				}

				if ( firstJobTypeName ) {
					firstJobTypeName = false;
				} else {

					sqlSB.append( " OR " );
				}
				sqlSB.append( " jt.name = ? " );
			}

			sqlSB.append( " ) " );

		}

		if ( listJobsRequest.getRequestId() != null ) {

			if ( first ) {
				first = false;
				sqlSB.append( " where " );
			} else {
				sqlSB.append( " AND " );
			}
			sqlSB.append( " job.request_id = ? " );
		}


		Set<String> requestTypeNames = listJobsRequest.getRequestTypeNames();

		if ( requestTypeNames != null && ( ! requestTypeNames.isEmpty() ) ) {


			if ( first ) {
				first = false;
				sqlSB.append( " where " );
			} else {
				sqlSB.append( " AND " );
			}

			sqlSB.append( " ( " );

			boolean firstRequestTypeName = true;

			for ( String requestTypeName : requestTypeNames ) {

				if ( StringUtils.isEmpty( requestTypeName ) ) {

					String msg = "Illegal argument, a requestTypeName in requestTypeNames is empty";

					throw new IllegalArgumentException( msg );
				}

				if ( firstRequestTypeName ) {
					firstRequestTypeName = false;
				} else {

					sqlSB.append( " OR " );
				}
				sqlSB.append( " rt.name = ? " );
			}

			sqlSB.append( " ) " );

		}

		if ( ! getCount ) {

			sqlSB.append( "ORDER BY  job.id DESC  LIMIT ?, ?" );
		}

		String sql = sqlSB.toString();

		pstmt = connection.prepareStatement( sql );

		int paramCounter = 0;

		// search params


		if ( jobTypeNames != null && ( ! jobTypeNames.isEmpty() ) ) {

			for ( String jobTypeName : jobTypeNames ) {

				paramCounter++;
				pstmt.setString( paramCounter, jobTypeName );
			}
		}

		if ( listJobsRequest.getRequestId() != null ) {

			paramCounter++;
			pstmt.setInt( paramCounter,  listJobsRequest.getRequestId()  );
		}

		if ( requestTypeNames != null && ( ! requestTypeNames.isEmpty() ) ) {

			for ( String requestTypeName : requestTypeNames ) {

				paramCounter++;
				pstmt.setString( paramCounter,  requestTypeName  );
			}
		}


		if ( ! getCount ) {

		//  Limit params

			if ( listJobsRequest.getIndexStart() != null ) {

				paramCounter++;
				pstmt.setInt( paramCounter,  listJobsRequest.getIndexStart() );
			} else {
				paramCounter++;
				pstmt.setInt( paramCounter, 0 );
			}

			if ( listJobsRequest.getJobsReturnCountMax() != null
					&& listJobsRequest.getJobsReturnCountMax() <= ServerCoreConstants.MAX_JOBS_RETURNED ) {

				paramCounter++;
				pstmt.setInt( paramCounter,  listJobsRequest.getJobsReturnCountMax() );
			} else {
				paramCounter++;
				pstmt.setInt( paramCounter,  ServerCoreConstants.MAX_JOBS_RETURNED );
			}

		}

		return pstmt;
	}



}
