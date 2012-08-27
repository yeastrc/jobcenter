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

public class RequeueJobServiceImpl implements RequeueJobService {

	private static Logger log = Logger.getLogger(RequeueJobServiceImpl.class);


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
	private StatusDAO statusDAO;
	
	public StatusDAO getStatusDAO() {
		return statusDAO;
	}
	public void setStatusDAO(StatusDAO statusDAO) {
		this.statusDAO = statusDAO;
	}
	public JobDAO getJobDAO() {
		return jobDAO;
	}
	public void setJobDAO(JobDAO jobDAO) {
		this.jobDAO = jobDAO;
	}
	
	
	//  JDBCDAO


	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 */
	@Override
	public RequeueJobResponse requeueJob( RequeueJobRequest requeueJobRequest, String remoteHost )
	{
		final String method = "requeueJob";


		if ( requeueJobRequest == null ) {

			log.error( method + "  IllegalArgument:requeueJobRequest == null");

			throw new IllegalArgumentException( "requeueJobRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( method + " requeueJobRequest.getNodeName() = |" + requeueJobRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		RequeueJobResponse requeueJobResponse = new RequeueJobResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( requeueJobResponse, requeueJobRequest.getNodeName(), remoteHost ) ) {
			
			return requeueJobResponse;
		}
		
		Job job = jobDAO.findById( requeueJobRequest.getJob().getId() );
		
		if ( job == null ) {

			requeueJobResponse.setErrorResponse( true );
			requeueJobResponse.setErrorCode( BaseResponse.ERROR_CODE_DATABASE_NOT_UPDATED );
			
			requeueJobResponse.setRequeueJobResponseErrorCode( RequeueJobResponse.REQUEUE_ERROR_JOB_NOT_FOUND_IN_DB );

			return requeueJobResponse;
		}
		
		if ( ! job.isRequeueable() ) {
			
			requeueJobResponse.setErrorResponse( true );
			requeueJobResponse.setErrorCode( BaseResponse.ERROR_CODE_DATABASE_NOT_UPDATED );

			requeueJobResponse.setRequeueJobResponseErrorCode( RequeueJobResponse.REQUEUE_ERROR_JOB_NO_LONGER_REQUEUABLE );

			return requeueJobResponse;
		}
		
		if ( requeueJobRequest.getJob().getDbRecordVersionNumber() != null ) {
			
			if ( job.getDbRecordVersionNumber().intValue() != requeueJobRequest.getJob().getDbRecordVersionNumber().intValue() ) {

				requeueJobResponse.setErrorResponse( true );
				requeueJobResponse.setErrorCode( BaseResponse.ERROR_CODE_DATABASE_NOT_UPDATED );

				requeueJobResponse.setRequeueJobResponseErrorCode( RequeueJobResponse.REQUEUE_ERROR_DB_RECORD_VERSION_NUMBER_OUT_OF_SYNC );

				return requeueJobResponse;
			}
		}
		
		log.debug( method + " job status was = " + job.getStatusId() + ", changing job status to " + requeueJobRequest.getJob().getStatusId() );
		
		StatusDTO newStatus = statusDAO.findById(  JobStatusValuesConstants.JOB_STATUS_REQUEUED );
		
		if ( newStatus == null ) {
			
			String msg = "New job status not found in status table so not valid, = " +  requeueJobRequest.getJob().getStatusId();
			
			log.error( msg );
			throw new RuntimeException( msg );
		}
		
		job.setStatus( newStatus );
		
		jobDAO.saveOrUpdate( job );
		
		requeueJobResponse.setErrorCode( BaseResponse.ERROR_CODE_NO_ERRORS );

		return requeueJobResponse;
	}




}
