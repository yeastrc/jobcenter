package org.jobcenter.service;

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

/**
 * Everything related to Job management through the GUI
 *
 */
public class GetJobForGUIServiceImpl implements GetJobForGUIService {

	private static Logger log = Logger.getLogger(GetJobForGUIServiceImpl.class);

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
	private RunDAO runDAO;

	public RunDAO getRunDAO() {
		return runDAO;
	}
	public void setRunDAO(RunDAO runDAO) {
		this.runDAO = runDAO;
	}
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
	 * @param viewJobRequest
	 * @param remoteHost
	 * @return
	 */
	@Override
	public  ViewJobResponse retrieveJob( ViewJobRequest viewJobRequest, String remoteHost )
	{
		ViewJobResponse viewJobResponse = new ViewJobResponse();

		if ( log.isDebugEnabled() ) {

			log.debug( "retrieveJob  listJobsRequest.getNodeName() = |" + viewJobRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}


		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( viewJobResponse, viewJobRequest.getNodeName(), remoteHost ) ) {
			
			return viewJobResponse;
		}

		Job job = jobDAO.findById( viewJobRequest.getJobId() );

		if ( job != null ) {

			jobJDBCDAO.retrieveJobParameters( job );

			//  run messages pre-populated via Hibernate config
			List<RunDTO> runs = runDAO.findByProperty( RunDAO.JOB_ID, job.getId() );

			//  get run output params for each run
			for ( RunDTO run : runs ) {
				
				jobJDBCDAO.retrieveRunOutputParams( run );
			}

			job.setAllRuns( runs );
			

		}

		viewJobResponse.setJob( job );

		return viewJobResponse;
	}


}
