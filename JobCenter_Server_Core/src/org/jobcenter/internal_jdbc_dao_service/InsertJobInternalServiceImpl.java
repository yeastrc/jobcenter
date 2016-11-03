package org.jobcenter.internal_jdbc_dao_service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jobcenter.constants.DBConstantsServerCore;
import org.jobcenter.dto.Job;
import org.jobcenter.jdbc.JDBCBaseDAO;
import org.jobcenter.jdbc.JDBCBaseWorkIF;
import org.jobcenter.jdbc.JobJDBCDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//The only way to get proper roll backs ( managed by Spring ) is to only use un-checked exceptions.
//
//The best way to make sure there are no checked exceptions is to have no "throws" on any of the methods.


//@Transactional causes Spring to surround calls to methods in this class with a database transaction.
//      Spring will roll back the transaction if a un-checked exception ( extended from RuntimeException ) is thrown.
//               Otherwise it commits the transaction.


//  extends JDBCBaseDAO since will get the Hibernate session and a way to run JDBC commands

/**
*
*
*/
@Transactional ( propagation = Propagation.REQUIRED, readOnly = false )

public class InsertJobInternalServiceImpl extends JDBCBaseDAO implements InsertJobInternalService {

	private static Logger log = Logger.getLogger( InsertJobInternalServiceImpl.class );
	
	
	//  JDBC DAO

	private JobJDBCDAO jobJDBCDAO;

	public JobJDBCDAO getJobJDBCDAO() {
		return jobJDBCDAO;
	}
	public void setJobJDBCDAO(JobJDBCDAO jobJDBCDAO) {
		this.jobJDBCDAO = jobJDBCDAO;
	}

	
	/* (non-Javadoc)
	 * @see org.jobcenter.internal_jdbc_dao_service.InsertJobInternalService#insertJob(org.jobcenter.dto.Job)
	 */
	@Override
	public int insertJob( final Job job) {


    	final String method = "WORM-insertJob( Job job )";

    	if ( job == null  ) {

    		throw new IllegalArgumentException( "in method method: " + method  + ", job == null " );
    	}

		final Map<String, String> jobParameters = job.getJobParameters();
		

		super.doJDBCWork(  
				
		  new JDBCBaseWorkIF() {
	        public void execute(Connection connection) throws SQLException {


	        	if ( log.isDebugEnabled() ) {
	        		log.debug( "Entering " + method );
	        	}

	        	jobJDBCDAO.insertJob( job, connection );

	        	if ( jobParameters == null ) {

		        	if ( log.isDebugEnabled() ) {
		        		log.debug( "Method: " + method + ": jobParameters == null" );
		        	}
	        		
	        	} else if ( jobParameters.isEmpty() ) {

		        	if ( log.isDebugEnabled() ) {
		        		log.debug( "Method: " + method + ": jobParameters is empty" );
		        	}
	        	} else {

	        		for ( Map.Entry<String, String> entry : jobParameters.entrySet() ) {

	        			jobJDBCDAO.insertJobParameter( entry.getKey(), entry.getValue(), job.getId(), connection );

	        		}
	        	}

	        	List<Integer> jobDependencies = job.getJobDependencies();

	        	if ( jobDependencies != null && ( ! jobDependencies.isEmpty() ) ) {

	        		for ( Integer jobDependency : jobDependencies ) {

	        			jobJDBCDAO.insertJobDependcy( job.getId(), jobDependency, connection );

	        		}
	        	}



	        	//  set field "insert_complete" to "T" so that the job can be sent to the client

	        	job.setInsertComplete( DBConstantsServerCore.JobTableInsertCompleteT );

	        	jobJDBCDAO.markJobInsertComplete( job, connection );
	        };
		});
		
		return job.getId();
	}

}
