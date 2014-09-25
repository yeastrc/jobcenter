package org.jobcenter.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jobcenter.constants.DBConstantsServerCore;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.dao.JobTypeDAO;
import org.jobcenter.dao.RequestTypeDAO;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.JobType;
import org.jobcenter.dto.RequestTypeDTO;
import org.jobcenter.dtoservernondb.JobAndDependenciesHolder;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.internalservice.SubmitJobInternalService;
import org.jobcenter.jdbc.JobJDBCDAO;
import org.jobcenter.nondbdto.SubmittedJobAndDependencies;
import org.jobcenter.request.SubmitJobsListWithDependenciesRequest;
import org.jobcenter.response.BaseResponse;
import org.jobcenter.response.SubmitJobsListWithDependenciesResponse;
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

public class SubmitJobsListWithDependenciesServiceImpl implements SubmitJobsListWithDependenciesService {

	private static Logger log = Logger.getLogger(SubmitJobsListWithDependenciesServiceImpl.class);



	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;
	private SubmitJobInternalService submitJobInternalService;

	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}

	public SubmitJobInternalService getSubmitJobInternalService() {
		return submitJobInternalService;
	}
	public void setSubmitJobInternalService(
			SubmitJobInternalService submitJobInternalService) {
		this.submitJobInternalService = submitJobInternalService;
	}

	
	
	/* (non-Javadoc)
	 * @see org.jobcenter.service.SubmitJobsListWithDependenciesService#submitJobsListWithDependencies(org.jobcenter.request.SubmitJobsListWithDependenciesRequest, java.lang.String)
	 */
	@Override
	public SubmitJobsListWithDependenciesResponse submitJobsListWithDependencies( SubmitJobsListWithDependenciesRequest submitJobsListWithDependenciesRequest, String remoteHost )
	{
		final String method = "submitJobsListWithDependencies";


		if ( submitJobsListWithDependenciesRequest == null ) {

			log.error( method + "  IllegalArgument: submitJobsListWithDependenciesRequest == null");

			throw new IllegalArgumentException( "submitJobsListWithDependenciesRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( "SubmitJobsListWithDependenciesServiceImpl::" + method + " submitJobsListWithDependenciesRequest.getNodeName() = |" + submitJobsListWithDependenciesRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		SubmitJobsListWithDependenciesResponse submitJobsListWithDependenciesResponse = new SubmitJobsListWithDependenciesResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( submitJobsListWithDependenciesResponse, submitJobsListWithDependenciesRequest.getNodeName(), remoteHost ) ) {

			return submitJobsListWithDependenciesResponse;
		}



		///////////

		//  validate request type name
		

		RequestTypeDTO requestTypeDTO = submitJobInternalService.validateRequestTypeNameRequestId( submitJobsListWithDependenciesRequest.getRequestTypeName(), submitJobsListWithDependenciesRequest.getRequestId(), submitJobsListWithDependenciesResponse );
		
		if ( requestTypeDTO == null ) {
			
			return submitJobsListWithDependenciesResponse;
		}


		List<SubmittedJobAndDependencies> submittedJobAndDependenciesList = submitJobsListWithDependenciesRequest.getSubmittedJobAndDependenciesList();
		
		if ( ! validateJobDependencies( submittedJobAndDependenciesList, submitJobsListWithDependenciesResponse) ) {
			
			return submitJobsListWithDependenciesResponse;
		}

		List<JobAndDependenciesHolder> jobsToInsert = new ArrayList<JobAndDependenciesHolder>( submittedJobAndDependenciesList.size() );
		
		
		for ( SubmittedJobAndDependencies submittedJobAndDependencies : submittedJobAndDependenciesList ) {
			
			JobType jobType = submitJobInternalService.validateJobTypeName( submittedJobAndDependencies.getJobTypeName(), submitJobsListWithDependenciesRequest.getRequestId(), submitJobsListWithDependenciesResponse );

			if ( jobType == null ) {

				return submitJobsListWithDependenciesResponse;
			}

			
			Job job = submitJobInternalService.createJobFromSubmittedJobAndDependencies( submitJobsListWithDependenciesRequest, submittedJobAndDependencies, jobType, null /* requestId */ );


			JobAndDependenciesHolder jobAndDependenciesHolder = new JobAndDependenciesHolder();
			jobAndDependenciesHolder.setJob( job );
			jobAndDependenciesHolder.setDependencyJobListPositions( submittedJobAndDependencies.getDependencyJobListPositions() );
			
			jobsToInsert.add( jobAndDependenciesHolder );
		}
		
		
		Integer requestId = submitJobsListWithDependenciesRequest.getRequestId();

		if ( requestId == null ) {

			requestId = submitJobInternalService.insertRequest( requestTypeDTO );
		}
		
		//  Update requestId on all jobs
		
		int currentJobIndex = -1;
		
		for ( JobAndDependenciesHolder jobAndDependenciesHolder : jobsToInsert ) {
			
			currentJobIndex++;

			Job job = jobAndDependenciesHolder.getJob();
			
			job.setRequestId( requestId );
			
			//  TODO  Also need to update the dependency job ids with the job ids from the prev jobs
			
			setJobDependenciesWithJobIds( jobAndDependenciesHolder, currentJobIndex, jobsToInsert );

			submitJobInternalService.insertJob( job );
		}

		submitJobsListWithDependenciesResponse.setRequestId( requestId );

		return submitJobsListWithDependenciesResponse;
	}
	
	
	/**
	 * @param submittedJobAndDependenciesList
	 * @param submitJobsListWithDependenciesResponse
	 * @return true if all dependencies are valid, false otherwise
	 */
	private boolean validateJobDependencies( List<SubmittedJobAndDependencies> submittedJobAndDependenciesList, SubmitJobsListWithDependenciesResponse submitJobsListWithDependenciesResponse ) {
		
		int currentJobIndex = -1;
		
		for ( SubmittedJobAndDependencies submittedJobAndDependencies : submittedJobAndDependenciesList ) {
			
			currentJobIndex++;
			
			List<Integer> dependencyJobListPositions = submittedJobAndDependencies.getDependencyJobListPositions();

			if ( dependencyJobListPositions != null && ( ! dependencyJobListPositions.isEmpty() ) ) {

				for ( int dependencyJobListPosition : dependencyJobListPositions ) {

					if ( dependencyJobListPosition >= currentJobIndex || dependencyJobListPosition < 0 ) {
						
						submitJobsListWithDependenciesResponse.setErrorCode( BaseResponse.ERROR_CODE_JOB_DEPENDENCY_REFERENCES_INVALID_INDEX_POSITION );
						submitJobsListWithDependenciesResponse.setErrorResponse(true);
						
						return false;
					}

				}
			}

		}
		
		return true;
		

	}
	
	
	
	private void setJobDependenciesWithJobIds( JobAndDependenciesHolder currentJobAndDependenciesHolder, int currentJobIndex,  List<JobAndDependenciesHolder> jobsToInsert ) {
		
		Job currentJob = currentJobAndDependenciesHolder.getJob();
		
		List<Integer> currentJobDependencyJobListPositions = currentJobAndDependenciesHolder.getDependencyJobListPositions();
		
		if ( currentJobDependencyJobListPositions != null ) {
		
			List<Integer> currentJobDependencyJobIdList = new ArrayList<Integer>( currentJobDependencyJobListPositions.size() );
			currentJob.setJobDependencies( currentJobDependencyJobIdList );

			for ( int dependencyJobListPosition : currentJobDependencyJobListPositions ) {

				JobAndDependenciesHolder prevJobAndDependenciesHolder = null;

				try {
					prevJobAndDependenciesHolder = jobsToInsert.get( dependencyJobListPosition );

				} catch (IndexOutOfBoundsException e) {

					String msg = "Unable to get prevJobAndDependenciesHolder for position: " + dependencyJobListPosition;
					log.error( msg );
					throw new RuntimeException( msg, e );
				}

				Job prevJob = prevJobAndDependenciesHolder.getJob();

				currentJobDependencyJobIdList.add( prevJob.getId() );
			}
		}

	}
}
