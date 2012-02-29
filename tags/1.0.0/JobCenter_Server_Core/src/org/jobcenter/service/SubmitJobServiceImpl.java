package org.jobcenter.service;


import java.util.Map;

import org.apache.log4j.Logger;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.dao.JobTypeDAO;
import org.jobcenter.dao.RequestTypeDAO;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.JobType;
import org.jobcenter.dto.RequestTypeDTO;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.jdbc.JobJDBCDAO;
import org.jobcenter.request.SubmitJobRequest;
import org.jobcenter.response.BaseResponse;
import org.jobcenter.response.SubmitJobResponse;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//The only way to get proper roll backs ( managed by Spring ) is to only use un-checked exceptions.
//
//The best way to make sure there are no checked exceptions is to have no "throws" on any of the methods.


//@Transactional causes Spring to surround calls to methods in this class with a database transaction.
//        Spring will roll back the transaction if a un-checked exception ( extended from RuntimeException ) is thrown.
//                 Otherwise it commits the transaction.


/**
*
*
*/
@Transactional ( propagation = Propagation.REQUIRED, readOnly = false )

public class SubmitJobServiceImpl implements SubmitJobService {

	private static Logger log = Logger.getLogger(SubmitJobServiceImpl.class);


	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;

	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}



	//  Hibernate DAO

	private RequestTypeDAO requestTypeDAO;
	private JobTypeDAO jobTypeDAO;

	public JobTypeDAO getJobTypeDAO() {
		return jobTypeDAO;
	}
	public void setJobTypeDAO(JobTypeDAO jobTypeDAO) {
		this.jobTypeDAO = jobTypeDAO;
	}
	public RequestTypeDAO getRequestTypeDAO() {
		return requestTypeDAO;
	}
	public void setRequestTypeDAO(RequestTypeDAO requestTypeDAO) {
		this.requestTypeDAO = requestTypeDAO;
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
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 */
	public SubmitJobResponse submitJob( SubmitJobRequest submitJobRequest, String remoteHost )
	{
		final String method = "submitJob";


		if ( submitJobRequest == null ) {

			log.error( method + "  IllegalArgument: submitJobRequest == null");

			throw new IllegalArgumentException( "submitJobRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( "JobService::retrieveJob  submitJobRequest.getNodeName() = |" + submitJobRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		SubmitJobResponse submitJobResponse = new SubmitJobResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( submitJobResponse, submitJobRequest.getNodeName(), remoteHost ) ) {

			return submitJobResponse;
		}


		///////////

		//  validate request type name

		RequestTypeDTO requestTypeDTO = requestTypeDAO.findOneRecordByName( submitJobRequest.getRequestTypeName() );

		if ( requestTypeDTO == null ) {

			//  record not found


			if ( log.isInfoEnabled() ) {

				log.info( "JobService::retrieveJob: RequestTypeName not found:  submitJobRequest.getRequestTypeName() = |" + submitJobRequest.getRequestTypeName() + "|."  );
			}

			submitJobResponse.setErrorResponse( true );

			submitJobResponse.setErrorCode( BaseResponse.ERROR_CODE_REQUEST_TYPE_NAME_NOT_FOUND );

			return submitJobResponse;

		}

		if ( submitJobRequest.getRequestId() != null ) {

			// request id is supplied so confirm it is in the Db for the supplied request type

			Integer requestIdSearchResult = jobJDBCDAO.getRequestFromIdAndRequestType( submitJobRequest.getRequestId(), requestTypeDTO.getId() );

			if ( requestIdSearchResult == null ) {

				//  record not found


				if ( log.isInfoEnabled() ) {

					log.info( "JobService::retrieveJob: RequestTypeName not found:  submitJobRequest.getRequestTypeName() = |" + submitJobRequest.getRequestTypeName() + "|."  );
				}

				submitJobResponse.setErrorResponse( true );

				submitJobResponse.setErrorCode( BaseResponse.ERROR_CODE_REQUEST_ID_NOT_FOUND_FOR_GIVEN_REQUEST_TYPE_NAME );

				return submitJobResponse;
			}
		}


		//////////////

//		JobType jobType = jobJDBCDAO.getJobTypeFromName( submitJobRequest.getJobTypeName() );

		JobType jobType = jobTypeDAO.findOneRecordByName( submitJobRequest.getJobTypeName() );

		if ( jobType == null ) {

			//  record not found


			if ( log.isInfoEnabled() ) {

				log.info( "JobService::retrieveJob: JobTypeName not found:  submitJobRequest.getJobTypeName() = |" + submitJobRequest.getJobTypeName() + "|."  );
			}

			submitJobResponse.setErrorResponse( true );

			submitJobResponse.setErrorCode( BaseResponse.ERROR_CODE_JOB_TYPE_NAME_NOT_FOUND );

			return submitJobResponse;
		}


		if ( jobType.getEnabled() == null || ( ! jobType.getEnabled() ) ) {

			//  record disabled


			if ( log.isInfoEnabled() ) {

				log.info( "JobService::retrieveJob: JobType record disabled :  submitJobRequest.getName() = |" + submitJobRequest.getJobTypeName() + "|."  );
			}

			submitJobResponse.setErrorResponse( true );

			submitJobResponse.setErrorCode( BaseResponse.ERROR_CODE_JOB_TYPE_NAME_DISABLED );

			return submitJobResponse;

		}


		Job job = new Job();

		job.setJobTypeId( jobType.getId() );
		job.setJobParameters( submitJobRequest.getJobParameters() );
		job.setSubmitter( submitJobRequest.getSubmitter() );

		job.setStatusId( JobStatusValuesConstants.JOB_STATUS_SUBMITTED );

		Integer submittedPriority = submitJobRequest.getPriority();

		if ( submittedPriority == null ) {

			job.setPriority( jobType.getPriority() );

		} else {

			job.setPriority( submittedPriority );
		}


		Integer requestId = submitJobRequest.getRequestId();

		if ( requestId == null ) {

			requestId = jobJDBCDAO.insertRequest( requestTypeDTO.getId() );
		}

		job.setRequestId( requestId );

		jobJDBCDAO.insertJob( job );

		Map<String,String > jobParameters = job.getJobParameters();

		if ( jobParameters != null && ( ! jobParameters.isEmpty() ) ) {

			for ( Map.Entry<String, String> entry : jobParameters.entrySet() ) {

				jobJDBCDAO.insertJobParameter( entry.getKey(), entry.getValue(), job.getId() );

			}

		}

		submitJobResponse.setRequestId( job.getRequestId() );

		return submitJobResponse;
	}
}
