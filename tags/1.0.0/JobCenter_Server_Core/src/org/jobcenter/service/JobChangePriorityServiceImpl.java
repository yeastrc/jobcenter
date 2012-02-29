package org.jobcenter.service;

import org.apache.log4j.Logger;

import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.dao.*;
import org.jobcenter.dto.*;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.jdbc.*;

import org.jobcenter.request.*;
import org.jobcenter.response.*;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//The only way to get proper database roll backs ( managed by Spring ) is to only use un-checked exceptions.
//
//The best way to make sure there are no checked exceptions is to have no "throws" on any of the methods.


//@Transactional causes Spring to surround calls to methods in this class with a database transaction.
//        Spring will roll back the transaction if a un-checked exception ( extended from RuntimeException ) is thrown.
//                 Otherwise it commits the transaction.


/**
*
*
*/

//  Spring database transaction demarcation.  
//  Spring will start a database transaction when any method is called and call commit when it completed  
//    or call roll back if an unchecked exception is thrown.
@Transactional ( propagation = Propagation.REQUIRED, readOnly = false )

public class JobChangePriorityServiceImpl implements JobChangePriorityService {

	private static Logger log = Logger.getLogger(JobChangePriorityServiceImpl.class);


	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;

	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}

	//  DAO

	private JobDAO jobDAO;
//	private StatusDAO statusDAO;
//	
//	public StatusDAO getStatusDAO() {
//		return statusDAO;
//	}
//	public void setStatusDAO(StatusDAO statusDAO) {
//		this.statusDAO = statusDAO;
//	}
	public JobDAO getJobDAO() {
		return jobDAO;
	}
	public void setJobDAO(JobDAO jobDAO) {
		this.jobDAO = jobDAO;
	}
	
	
	//  JDBCDAO


	/* (non-Javadoc)
	 * @see org.jobcenter.service.JobChangePriorityService#jobChangePriority(org.jobcenter.request.JobChangePriorityRequest, java.lang.String)
	 */
//	@Override
	public JobChangePriorityResponse jobChangePriority( JobChangePriorityRequest jobChangePriorityRequest, String remoteHost )
	{
		final String method = "jobChangePriority";


		if ( jobChangePriorityRequest == null ) {

			log.error( method + "  IllegalArgument:requeueJobRequest == null");

			throw new IllegalArgumentException( "requeueJobRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( method + " requeueJobRequest.getNodeName() = |" + jobChangePriorityRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		JobChangePriorityResponse jobChangePriorityResponse = new JobChangePriorityResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( jobChangePriorityResponse, jobChangePriorityRequest.getNodeName(), remoteHost ) ) {
			
			return jobChangePriorityResponse;
		}
		
		Job job = jobDAO.findById( jobChangePriorityRequest.getJob().getId() );
		
		if ( job == null ) {
			
			log.debug( "Job id not found in database, returning ERROR_CODE_DATABASE_NOT_UPDATED. id = " + jobChangePriorityRequest.getJob().getId() );

			jobChangePriorityResponse.setErrorResponse( true );
			jobChangePriorityResponse.setErrorCode( BaseResponse.ERROR_CODE_DATABASE_NOT_UPDATED );
			
			jobChangePriorityResponse.setJobChangePriorityResponseErrorCode( JobChangePriorityResponse.CHANGE_PRIORITY_ERROR_JOB_NOT_FOUND_IN_DB );

			return jobChangePriorityResponse;
		}
		
		if ( jobChangePriorityRequest.getJob().getDbRecordVersionNumber() != null ) {
			
			if ( job.getDbRecordVersionNumber().intValue() != jobChangePriorityRequest.getJob().getDbRecordVersionNumber().intValue() ) {

				log.debug( "DbRecordVersionNumber in database not match value supplied, returning ERROR_CODE_DATABASE_NOT_UPDATED and JobChangePriorityResponse.CHANGE_PRIORITY_ERROR_DB_RECORD_VERSION_NUMBER_OUT_OF_SYNC. " 
						+ "id = " + jobChangePriorityRequest.getJob().getId()
						+ ", jobChangePriorityRequest.getJob().getDbRecordVersionNumber() = " + jobChangePriorityRequest.getJob().getDbRecordVersionNumber() 
						+ ", job record from database [ job.getDbRecordVersionNumber() ] = " + job.getDbRecordVersionNumber() );

				jobChangePriorityResponse.setErrorResponse( true );
				jobChangePriorityResponse.setErrorCode( BaseResponse.ERROR_CODE_DATABASE_NOT_UPDATED );

				jobChangePriorityResponse.setJobChangePriorityResponseErrorCode( JobChangePriorityResponse.CHANGE_PRIORITY_ERROR_DB_RECORD_VERSION_NUMBER_OUT_OF_SYNC );

				return jobChangePriorityResponse;
			}
		}
		
		log.debug( method + " job priority was = " + job.getPriority() + ", changing priority to " + jobChangePriorityRequest.getJob().getPriority() );
		
		job.setPriority( jobChangePriorityRequest.getJob().getPriority() );
		
		jobDAO.saveOrUpdate( job );
		
		jobChangePriorityResponse.setErrorCode( BaseResponse.ERROR_CODE_NO_ERRORS );

		return jobChangePriorityResponse;
	}




}
