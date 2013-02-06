package org.jobcenter.service;

import java.util.ArrayList;
import java.util.List;

import org.jobcenter.dao.*;
import org.jobcenter.dto.*;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.jdbc.*;
import org.jobcenter.request.*;
import org.jobcenter.response.*;


import org.apache.log4j.Logger;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//The only way to get proper roll backs ( managed by Spring ) is to only use un-checked exceptions.
//
//The best way to make sure there are no checked exceptions is to have no "throws" on any of the methods.


//@Transactional causes Spring to surround calls to methods in this class with a database transaction.
//        Spring will roll back the transaction if a un-checked exception ( extended from RuntimeException ) is thrown.
//                 Otherwise it commits the transaction.

@Transactional ( propagation = Propagation.REQUIRED, readOnly = false )



public class GetJobListServiceImpl implements GetJobListService {

	private static Logger log = Logger.getLogger(GetJobListServiceImpl.class);

	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;

	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}


	//  Hibernate DAO

	private JobDAO jobDAO;

	public JobDAO getJobDAO() {
		return jobDAO;
	}
	public void setJobDAO(JobDAO jobDAO) {
		this.jobDAO = jobDAO;
	}



	//  JDBC DAO

	private JobJDBCDAO jobJDBCDAO;

	public JobJDBCDAO getJobJDBCDAO() {
		return jobJDBCDAO;
	}
	public void setJobJDBCDAO(JobJDBCDAO jobJDBCDAO) {
		this.jobJDBCDAO = jobJDBCDAO;
	}

	/**
	 * @param listJobsRequest
	 * @param remoteHost
	 * @return
	 */
	@Override
	public  ListJobsResponse retrieveJobsList( ListJobsRequest listJobsRequest, String remoteHost )
	{
		ListJobsResponse listJobsResponse = new ListJobsResponse();

		if ( listJobsRequest == null ) {

			log.error("retrieveJobsList(...) IllegalArgument: listJobsRequest == null");

			throw new IllegalArgumentException( "listJobsRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( "retrieveJobsList  listJobsRequest.getNodeName() = |" + listJobsRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}



		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( listJobsResponse, listJobsRequest.getNodeName(), remoteHost ) ) {
			
			return listJobsResponse;
		}
		
		if ( log.isDebugEnabled() ) {
		
			log.debug( "retrieveJobsList  listJobsRequest = " + listJobsRequest );
		}
		
		int jobCount = jobJDBCDAO.getJobCount( listJobsRequest );

		List<Integer> jobIdList = jobJDBCDAO.getJobIdList( listJobsRequest );

		List<Job> jobs = new ArrayList<Job>( jobCount );

		if ( jobIdList != null ) {

			for ( int jobId : jobIdList ) {
				
				Job job = jobDAO.findById( jobId );
				
				if ( job == null ) {
					
					String msg = "Failed to retrieve job for id just retrieved from database.  job id = " + jobId;
					
					log.error( msg );
					
					throw new RuntimeException( msg );
				}
				
				jobs.add( job );
			}
		
			for ( Job job : jobs ) {

				jobJDBCDAO.retrieveJobParameters( job );
			}
		}

		listJobsResponse.setJobs( jobs );
		
		listJobsResponse.setJobCount( jobCount );

		return listJobsResponse;
	}

}
