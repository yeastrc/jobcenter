package org.jobcenter.internalservice;

import java.util.Map;

import org.apache.log4j.Logger;
import org.jobcenter.constants.DBConstantsServerCore;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.dao.JobTypeDAO;
import org.jobcenter.dao.RequestTypeDAO;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.JobType;
import org.jobcenter.dto.RequestTypeDTO;
import org.jobcenter.internal_jdbc_dao_service.InsertJobInternalService;
import org.jobcenter.jdbc.JobJDBCDAO;
import org.jobcenter.nondbdto.SubmittedJobAndDependencies;
import org.jobcenter.request.SubmitJobRequest;
import org.jobcenter.request.SubmitJobsListWithDependenciesRequest;
import org.jobcenter.response.BaseResponse;
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

public class SubmitJobInternalServiceImpl implements SubmitJobInternalService  {

	private static Logger log = Logger.getLogger(SubmitJobInternalServiceImpl.class);



	//  Insert Job Internal Service
	
	private InsertJobInternalService insertJobInternalService;

	public InsertJobInternalService getInsertJobInternalService() {
		return insertJobInternalService;
	}
	public void setInsertJobInternalService(
			InsertJobInternalService insertJobInternalService) {
		this.insertJobInternalService = insertJobInternalService;
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

	
	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.SubmitJobInternalService#validateRequestTypeNameRequestId(java.lang.String, java.lang.Integer, org.jobcenter.response.BaseResponse)
	 */
	@Override
	public RequestTypeDTO validateRequestTypeNameRequestId( String requestTypeName, Integer requestId, BaseResponse baseResponse )
	{



		///////////

		//  validate request type name

		RequestTypeDTO requestTypeDTO = requestTypeDAO.findOneRecordByName( requestTypeName );

		if ( requestTypeDTO == null ) {

			//  record not found


			if ( log.isInfoEnabled() ) {

				log.info( ": RequestTypeName not found:  requestTypeName = |" + requestTypeName + "|."  );
			}

			baseResponse.setErrorResponse( true );

			baseResponse.setErrorCode( BaseResponse.ERROR_CODE_REQUEST_TYPE_NAME_NOT_FOUND );

			return null;

		}

		if ( requestId != null ) {

			// request id is supplied so confirm it is in the Db for the supplied request type

			Integer requestIdSearchResult = jobJDBCDAO.getRequestFromIdAndRequestType( requestId, requestTypeDTO.getId() );

			if ( requestIdSearchResult == null ) {

				//  record not found


				if ( log.isInfoEnabled() ) {

					log.info( ": RequestTypeName not found:  requestTypeName = |" + requestTypeName + "|."  );
				}

				baseResponse.setErrorResponse( true );

				baseResponse.setErrorCode( BaseResponse.ERROR_CODE_REQUEST_ID_NOT_FOUND_FOR_GIVEN_REQUEST_TYPE_NAME );

				return null;
			}
		}
		
		return requestTypeDTO;
	}

	
	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.SubmitJobInternalService#validateJobTypeName(java.lang.String, java.lang.Integer, org.jobcenter.response.BaseResponse)
	 */
	@Override
	public JobType validateJobTypeName( String jobTypeName, Integer requestId, BaseResponse baseResponse )
	{


		//////////////

//		JobType jobType = jobJDBCDAO.getJobTypeFromName( submitJobRequest.getJobTypeName() );

		JobType jobType = jobTypeDAO.findOneRecordByName( jobTypeName );

		if ( jobType == null ) {

			//  record not found


			if ( log.isInfoEnabled() ) {

				log.info( ": JobTypeName not found:  jobTypeName = |" + jobTypeName + "|."  );
			}

			baseResponse.setErrorResponse( true );

			baseResponse.setErrorCode( BaseResponse.ERROR_CODE_JOB_TYPE_NAME_NOT_FOUND );

			return null;
		}


		if ( jobType.getEnabled() == null || ( ! jobType.getEnabled() ) ) {

			//  record disabled


			if ( log.isInfoEnabled() ) {

				log.info( "JobType record disabled :  jobTypeName = |" + jobTypeName + "|."  );
			}

			baseResponse.setErrorResponse( true );

			baseResponse.setErrorCode( BaseResponse.ERROR_CODE_JOB_TYPE_NAME_DISABLED );

			return null;

		}
		
		return jobType;
	}
	
	
	

	@Override
	public boolean validateRequiredExecutionThreads( Integer submitJobRequestRequiredExecutionThreads, Integer jobTypeMaxRequiredExecutionThreads, BaseResponse baseResponse ) {


		if ( submitJobRequestRequiredExecutionThreads != null ) {

			//  RequiredExecutionThreads value specified on submitted job

			if ( jobTypeMaxRequiredExecutionThreads == null ) {
				
				//  No max specified on job type so error
				
				if ( log.isInfoEnabled() ) {

					log.info( ": error request: submitJobRequestRequiredExecutionThreads != null AND jobTypeMaxRequiredExecutionThreads == null."  );
				}

				baseResponse.setErrorResponse( true );

				baseResponse.setErrorCode( BaseResponse.ERROR_CODE_JOB_SPECIFIES_REQUIRED_EXEC_THREADS_BUT_JOB_TYPE_DOES_NOT_HAVE_MAX );

				return false;
			}
			

			if ( submitJobRequestRequiredExecutionThreads.intValue() > jobTypeMaxRequiredExecutionThreads.intValue() ) {
				
				//  No max specified on job type so error
				
				if ( log.isInfoEnabled() ) {

					log.info( ": error request: submitJobRequestRequiredExecutionThreads > jobTypeMaxRequiredExecutionThreads."  );
				}

				baseResponse.setErrorResponse( true );

				baseResponse.setErrorCode( BaseResponse.ERROR_CODE_JOB_REQUIRED_EXEC_THREADS_EXCEEDS_JOB_TYPE_MAX );

				return false;
			}

		}
		
		return true;
	}
		
	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.SubmitJobInternalService#insertRequest(org.jobcenter.dto.RequestTypeDTO)
	 */
	@Override
	public int insertRequest( RequestTypeDTO requestTypeDTO )
	{

		int requestId = jobJDBCDAO.insertRequest( requestTypeDTO.getId() );
		
		return requestId;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.SubmitJobInternalService#createJobFromSubmitJobRequest(org.jobcenter.request.SubmitJobRequest, org.jobcenter.dto.JobType, int)
	 */
	@Override
	public Job createJobFromSubmitJobRequest( SubmitJobRequest submitJobRequest, JobType jobType, int requestId ) {
		
		
		Job job = new Job();

		job.setInsertComplete( DBConstantsServerCore.JobTableInsertCompleteF );

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
		
		Integer submittedRequiredExecutionThreads = submitJobRequest.getRequiredExecutionThreads();
		
		job.setRequiredExecutionThreads( submittedRequiredExecutionThreads );


		job.setRequestId( requestId );
		
		return job;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.SubmitJobInternalService#createJobFromSubmittedJobAndDependencies(org.jobcenter.request.SubmittedJobAndDependencies, org.jobcenter.dto.JobType, int)
	 */
	@Override
	public Job createJobFromSubmittedJobAndDependencies( SubmitJobsListWithDependenciesRequest submitJobsListWithDependenciesRequest, SubmittedJobAndDependencies submittedJobAndDependencies, JobType jobType, Integer requestId ) {
		
		
		Job job = new Job();

		job.setInsertComplete( DBConstantsServerCore.JobTableInsertCompleteF );

		job.setJobTypeId( jobType.getId() );
		job.setJobParameters( submittedJobAndDependencies.getJobParameters() );
		job.setSubmitter( submitJobsListWithDependenciesRequest.getSubmitter() );

		job.setStatusId( JobStatusValuesConstants.JOB_STATUS_SUBMITTED );

		Integer submittedPriority = submittedJobAndDependencies.getPriority();

		if ( submittedPriority == null ) {

			job.setPriority( jobType.getPriority() );

		} else {

			job.setPriority( submittedPriority );
		}
		
		if ( requestId != null ) {

			job.setRequestId( requestId );
		}
		
		return job;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.SubmitJobInternalService#insertJob(org.jobcenter.dto.Job)
	 */
	@Override
	public int insertJob( Job job )
	{

		int jobParameterCount = -1;
		
		Map<String, String> jobParameters = job.getJobParameters();
		
		if ( jobParameters != null ) {

			jobParameterCount = jobParameters.size();
		}
		
		job.setJobParameterCount( jobParameterCount );
		
		insertJobInternalService.insertJob( job );

		return job.getId();
	}
	
	
	
}
