package org.jobcenter.service;

import org.apache.log4j.Logger;

import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.dao.*;
import org.jobcenter.dto.*;
import org.jobcenter.internalservice.ClientNodeNameCheck;

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

public class CancelJobServiceImpl implements CancelJobService {

	private static Logger log = Logger.getLogger(CancelJobServiceImpl.class);


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



	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 */
	@Override
	public CancelJobResponse cancelJob( CancelJobRequest cancelJobRequest, String remoteHost )
	{
		final String method = "cancelJob";


		if ( cancelJobRequest == null ) {

			log.error( method + "  IllegalArgument:cancelJobRequest == null");

			throw new IllegalArgumentException( "cancelJobRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( method + " cancelJobRequest.getNodeName() = |" + cancelJobRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		CancelJobResponse cancelJobResponse = new CancelJobResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( cancelJobResponse, cancelJobRequest.getNodeName(), remoteHost ) ) {

			return cancelJobResponse;
		}

		Job job = jobDAO.findById( cancelJobRequest.getJob().getId() );

		if ( job == null ) {


			//  TODO  return different things if there is a general failure compared to the version being different

			cancelJobResponse.setErrorResponse( true );
			cancelJobResponse.setErrorCode( BaseResponse.ERROR_CODE_DATABASE_NOT_UPDATED );

			cancelJobResponse.setCancelJobResponseErrorCode( CancelJobResponse.CANCEL_ERROR_JOB_NOT_FOUND_IN_DB );

			return cancelJobResponse;
		}

		if ( cancelJobRequest.getJob().getDbRecordVersionNumber() != null ) {

			if ( job.getDbRecordVersionNumber() != cancelJobRequest.getJob().getDbRecordVersionNumber() ) {


				//  TODO  return different things if there is a general failure compared to the version being different

				cancelJobResponse.setErrorResponse( true );
				cancelJobResponse.setErrorCode( BaseResponse.ERROR_CODE_DATABASE_NOT_UPDATED );

				cancelJobResponse.setCancelJobResponseErrorCode( CancelJobResponse.CANCEL_ERROR_DB_RECORD_VERSION_NUMBER_OUT_OF_SYNC );

				return cancelJobResponse;
			}
		}

		if ( ! job.isCancellable() ) {

			//  TODO  return different things if there is a general failure compared to the version being different

			cancelJobResponse.setErrorResponse( true );
			cancelJobResponse.setErrorCode( BaseResponse.ERROR_CODE_DATABASE_NOT_UPDATED );

			cancelJobResponse.setCancelJobResponseErrorCode( CancelJobResponse.CANCEL_ERROR_JOB_NOT_CANCELABLE );

			return cancelJobResponse;


		}

		log.debug( method + " job status was = " + job.getStatusId() + ", changing job status to " + cancelJobRequest.getJob().getStatusId() );

		StatusDTO newStatus = statusDAO.findById(  JobStatusValuesConstants.JOB_STATUS_CANCELED );

		if ( newStatus == null ) {

			String msg = "New job status not found in status table so not valid, = " +  cancelJobRequest.getJob().getStatusId();

			log.error( msg );
			throw new RuntimeException( msg );
		}

		job.setStatus( newStatus );

		jobDAO.saveOrUpdate( job );

		return cancelJobResponse;
	}




}
