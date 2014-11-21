package org.jobcenter.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.jobcenter.constants.Constants;
import org.jobcenter.constants.JobConstants;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.constants.RunMessageTypesConstants;
import org.jobcenter.constants.ServerConfigKeyValues;
import org.jobcenter.constants.ServerCoreConstants;
import org.jobcenter.dao.JobTypeDAO;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.JobType;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.dto.RunMessageDTO;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.internalservice.GetValueFromConfigService;
import org.jobcenter.internalservice.SaveJobSentToClientService;
import org.jobcenter.jdbc.*;
import org.jobcenter.request.JobRequest;
import org.jobcenter.request.JobRequestModuleInfo;
import org.jobcenter.response.JobResponse;
import org.jobcenter.service_response.GetJobServiceResponse;
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

public class GetNextJobForClientServiceImpl implements GetNextJobForClientService {









	private static Logger log = Logger.getLogger(GetNextJobForClientServiceImpl.class);



	private static ClassLoader thisClassLoader = GetNextJobForClientServiceImpl.class.getClassLoader();




	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;
	private GetValueFromConfigService getValueFromConfigService;

	private SaveJobSentToClientService saveJobSentToClientService;

	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}
	public SaveJobSentToClientService getSaveJobSentToClientService() {
		return saveJobSentToClientService;
	}
	public void setSaveJobSentToClientService(
			SaveJobSentToClientService saveJobSentToClientService) {
		this.saveJobSentToClientService = saveJobSentToClientService;
	}
	public GetValueFromConfigService getGetValueFromConfigService() {
		return getValueFromConfigService;
	}
	public void setGetValueFromConfigService(
			GetValueFromConfigService getValueFromConfigService) {
		this.getValueFromConfigService = getValueFromConfigService;
	}
	
	//  Hibernate DAO
	
	private JobTypeDAO jobTypeDAO;

	public JobTypeDAO getJobTypeDAO() {
		return jobTypeDAO;
	}
	public void setJobTypeDAO(JobTypeDAO jobTypeDAO) {
		this.jobTypeDAO = jobTypeDAO;
	}
	
	// JDBC DAO


	private GetNextJobForClientJDBCDAO getNextJobForClientJDBCDAO;
	private JobJDBCDAO jobJDBCDAO;

	
	public JobJDBCDAO getJobJDBCDAO() {
		return jobJDBCDAO;
	}
	public void setJobJDBCDAO(JobJDBCDAO jobJDBCDAO) {
		this.jobJDBCDAO = jobJDBCDAO;
	}
	public GetNextJobForClientJDBCDAO getGetNextJobForClientJDBCDAO() {
		return getNextJobForClientJDBCDAO;
	}
	public void setGetNextJobForClientJDBCDAO(
			GetNextJobForClientJDBCDAO getNextJobForClientJDBCDAO) {
		this.getNextJobForClientJDBCDAO = getNextJobForClientJDBCDAO;
	}



	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 * @throws Exception
	 */
	public GetJobServiceResponse getNextJobForClientService( JobRequest jobRequest, String remoteHost )
	{
		final String method = "getNextJobForClientService";

		if ( jobRequest == null ) {

			log.error( method + " IllegalArgument:jobRequest == null");

			throw new IllegalArgumentException( "jobRequest == null" );
		}

		List<JobRequestModuleInfo> clientModules = jobRequest.getClientModules();

		if ( clientModules == null ) {

			log.error(method + " IllegalArgument:jobRequest.getClientModules() == null");

			throw new IllegalArgumentException( "jobRequest.getClientModules() == null" );
		}

		if ( clientModules.isEmpty() ) {

			log.error(method + " IllegalArgument:jobRequest.getClientModules() is empty");

			throw new IllegalArgumentException( "jobRequest.getClientModules() is empty" );
		}

		if ( remoteHost == null ) {

			log.error(method + " IllegalArgument:remoteHost == null");

			throw new IllegalArgumentException( "remoteHost == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( "JobService::retrieveJob  jobRequest.getNodeName() = |" + jobRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}
		
		{

			GetJobServiceResponse getJobServiceResponse = new GetJobServiceResponse();

			JobResponse jobResponse = new JobResponse();
			getJobServiceResponse.setJobResponse( jobResponse );

			if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( jobResponse, jobRequest.getNodeName(), remoteHost ) ) {

				return getJobServiceResponse;
			}

		}

		Integer nodeId = clientNodeNameCheck.getNodeIdForNodeName( jobRequest.getNodeName() );




		GetJobServiceResponse getJobServiceResponse = getNextJobForClient( jobRequest, nodeId, remoteHost );

		return getJobServiceResponse;
	}


	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 */
	private GetJobServiceResponse getNextJobForClient( JobRequest jobRequest, Integer nodeId, String remoteHost ) {

		final String method = "getNextJobForClient";

		if ( log.isDebugEnabled() ) {
			log.debug( "Entering " + method );
		}
		
		GetJobServiceResponse getJobServiceResponse = new GetJobServiceResponse();
		JobResponse jobResponse = new JobResponse();
		getJobServiceResponse.setJobResponse( jobResponse );


		jobResponse.setJobFound( false );


		Job job = null;


		try {

			job = getNextJobForClientJDBCDAO.retrieveNextJobRecordForJobRequest( jobRequest );

			if ( job == null ) {
				
				//  No job retrieved so return nothing
				
			} else {
				
				JobType jobTypeForThisJob = jobTypeDAO.findById( job.getJobTypeId() );
				
				if ( jobTypeForThisJob == null ) {
					
					String msg = "jobType record not found for job type id: " + job.getJobTypeId()
							+ ", job id: " + job.getId();
					
					log.error( msg );
					
					throw new RuntimeException( msg );
				}
				
				
				
				
				if ( job.getRequiredExecutionThreads() != null && 
						job.getRequiredExecutionThreads() > jobRequest.getNumberThreadsAvailableOnClient() )
				{
					
					//  Not enough available threads on client so return nothing
					
					//   This is done to ensure the next job is held until the number of threads is available
					
					jobResponse.setNextJobRequiresMoreThreads( true );
					
					
				} else {
					
					job.setJobType( jobTypeForThisJob );

					processJobRetrievedFromDB(jobRequest, nodeId, remoteHost, getJobServiceResponse, jobResponse, job);
				}
			}

		} catch (Throwable sqlEx) {

			log.error( method + ": Exception:  will be performing transaction rollback:  Exception message: " + sqlEx.toString() + '.', sqlEx);


			//  Wrap in RuntimeException for Spring Transactional rollback
			throw new RuntimeException( sqlEx );

		} finally {


		}

		return getJobServiceResponse;

	}
	
	
	
	/**
	 * @param jobRequest
	 * @param nodeId
	 * @param remoteHost
	 * @param getJobServiceResponse
	 * @param jobResponse
	 * @param job
	 */
	private void processJobRetrievedFromDB(JobRequest jobRequest,
			Integer nodeId, 
			String remoteHost,
			GetJobServiceResponse getJobServiceResponse,
			JobResponse jobResponse, 
			Job job) 
	{
		
		jobJDBCDAO.retrieveJobParameters( job );

		int jobParameterCount = 0;

		Map<String, String> jobParameters = job.getJobParameters();

		if ( jobParameters != null ) {

			jobParameterCount = jobParameters.size();
		}

		if ( job.getJobParameterCount() != JobConstants.JOB_PARAMETER_COUNT_NOT_SET && jobParameterCount != job.getJobParameterCount() ) {

			String msg = "Resetting job to 'Submitted' and delaying job.  job.getJobParameterCount() does not match number of parameters retrieved from the job parameters table."
				+ "  job.getJobParameterCount() = " + job.getJobParameterCount() 
				+ ", number of parameters retrieved from the job parameters table = " + jobParameterCount
				+ ", job id = " + job.getId()
				+ ", job.getCurrentRunId() = " + job.getCurrentRunId();

			log.error( msg );

			delayJob( job, nodeId );
			
			getJobServiceResponse.setTryAgain( true );

		} else {


			jobJDBCDAO.updateJobStatusOnIdAndDbRecordVersionNumber( job, JobStatusValuesConstants.JOB_STATUS_RUNNING );

			RunDTO currentRun = jobJDBCDAO.insertRunTableRecord( nodeId, job);

			currentRun.setJob( job );

			job.setCurrentRun( currentRun );

			job.setCurrentRunId( currentRun.getId() );

			job.setJobParameterCountWhenRetrievedByGetJob( jobParameterCount );

			Boolean saveJobSentToClientForDebugging = getValueFromConfigService.getConfigValueAsBoolean( ServerConfigKeyValues.SAVE_JOB_SENT_TO_CLIENT_FOR_DEBUGGING );
			
			if ( saveJobSentToClientForDebugging != null && saveJobSentToClientForDebugging == true ) {
			
				saveJobSentToClientService.saveJobSentToClient( job  );
			}
			
			
			jobResponse.setJob( job );


			logRetrievedJob(jobRequest, remoteHost, job);
			
			
			jobResponse.setJobFound( true );

		}
	}


	/**
	 * @param job
	 * @param nodeId
	 */
	private void delayJob( Job job, int nodeId ) {
		
		int paramErrorRetryCount = job.getParamErrorRetryCount();
		paramErrorRetryCount++;
		job.setParamErrorRetryCount( paramErrorRetryCount );
		
		if ( paramErrorRetryCount > ServerCoreConstants.PARAM_ERROR_RETRY_COUNT_MAX ) {
			
			jobJDBCDAO.updateJobStatusOnIdAndDbRecordVersionNumber( job, JobStatusValuesConstants.JOB_STATUS_HARD_ERROR);
			

			List<RunMessageDTO> runMessages = new ArrayList<RunMessageDTO>();

			RunMessageDTO runMessageDTO = new RunMessageDTO();
			runMessages.add( runMessageDTO );

			runMessageDTO.setType( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR );

			runMessageDTO.setMessage( "System error. Problem processing job parameters, param count mismatch." );
			
			RunDTO currentRun = jobJDBCDAO.insertRunTableRecord( nodeId, job, JobStatusValuesConstants.JOB_STATUS_HARD_ERROR );

			currentRun.setJob( job );


			// save run messages

			for ( RunMessageDTO runMessage : runMessages ) {

				jobJDBCDAO.insertRunMessageFromModuleRecord( runMessage, currentRun );
			}
			
		} else {
			
			jobJDBCDAO.updateDelayUntilAndParamErrorCntOnId( job.getId() );
			
		}
	}

	
	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @param method
	 * @param job
	 */
	private void logRetrievedJob(JobRequest jobRequest, String remoteHost, Job job) {
		
		final String method = "logRetrievedJob";
		
		if ( log.isInfoEnabled() ) {


			log.info( method + "Found job for node to process.  nodeName = |" + jobRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."
					+  "  job = " + job.getId() );

			try {

				ByteArrayOutputStream oStream = new ByteArrayOutputStream();

				JAXBContext jc = JAXBContext.newInstance( Constants.DTO_PACKAGE_PATH, thisClassLoader );

				Marshaller marshaller = jc.createMarshaller();

				marshaller.marshal( job, oStream );

				String jobXML = oStream.toString();

				log.info( method + "Found job for node to process.  nodeName = |" + jobRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."
						+  "  job id = " + job.getId() + ", job = " + jobXML );

			} catch (Throwable t) {

				log.error( method + ": Exception:  Logging job returned to client: Job Id = " + job.getId() + ", Exception = " + t.toString() + '.', t );
			}

		}
	}

}
