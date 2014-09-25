package org.jobcenter.submission.internal.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jobcenter.coreinterfaces.JobSubmissionJobInterface;
import org.jobcenter.nondbdto.SubmittedJobAndDependencies;
import org.jobcenter.request.SubmitJobRequest;
import org.jobcenter.request.SubmitJobsListWithDependenciesRequest;

/**
 * !!!!!!!!!!    This is Jobcenter internal code and may change at any time   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * Transfers/Transforms submitted job data to the Request object sent to the server
 *
 */
public class JobSubmissionTransforms {

	/**
	 * @param requestTypeName
	 * @param requestId
	 * @param jobTypeName
	 * @param submitter
	 * @param priority
	 * @param requiredExecutionThreads
	 * @param jobParameters
	 * @param submissionNodeName
	 * @return
	 */
	public static SubmitJobRequest createSubmitJobRequest( String requestTypeName, Integer requestId, String jobTypeName, String submitter, Integer priority, Integer requiredExecutionThreads, Map<String, String> jobParameters, String submissionNodeName ) {
		

		if ( requestTypeName == null ) {

			throw new IllegalArgumentException( "requestTypeName cannot be null" );
		}
		

		if ( jobTypeName == null ) {

			throw new IllegalArgumentException( "jobTypeName cannot be null" );
		}
		
		SubmitJobRequest submitJobRequest = new SubmitJobRequest();

		submitJobRequest.setNodeName( submissionNodeName );

		submitJobRequest.setRequestTypeName( requestTypeName );
		submitJobRequest.setRequestId( requestId );

		submitJobRequest.setJobTypeName( jobTypeName );

		submitJobRequest.setPriority (priority );
		submitJobRequest.setRequiredExecutionThreads( requiredExecutionThreads );
		submitJobRequest.setSubmitter( submitter );
		submitJobRequest.setJobParameters( jobParameters );

		return submitJobRequest;
	}
	

	/**
	 * @param requestTypeName
	 * @param requestId
	 * @param submitter
	 * @param jobSubmissionJobLists
	 * @param submissionNodeName
	 * @return
	 */
	public static SubmitJobsListWithDependenciesRequest createSubmitJobsListWithDependenciesRequest( String requestTypeName,
			Integer requestId, 
			String submitter, 
			List<JobSubmissionJobInterface> jobSubmissionJobsList, 
			String submissionNodeName ) {


		if ( requestTypeName == null ) {

			throw new IllegalArgumentException( "requestTypeName cannot be null" );
		}
		
		if ( jobSubmissionJobsList == null ) {

			throw new IllegalArgumentException( "jobSubmissionJobsList cannot be null" );
		}

		if ( jobSubmissionJobsList.isEmpty() ) {

			throw new IllegalArgumentException( "jobSubmissionJobsList cannot be empty" );
		}
		
		SubmitJobsListWithDependenciesRequest submitJobsListWithDependenciesRequest = new SubmitJobsListWithDependenciesRequest();
		
		submitJobsListWithDependenciesRequest.setRequestTypeName( requestTypeName );
		submitJobsListWithDependenciesRequest.setRequestId( requestId );
		submitJobsListWithDependenciesRequest.setSubmitter( submitter );
		submitJobsListWithDependenciesRequest.setNodeName( submissionNodeName );
		
		List<SubmittedJobAndDependencies> submittedJobAndDependenciesList = new ArrayList<SubmittedJobAndDependencies>( jobSubmissionJobsList.size() );
		submitJobsListWithDependenciesRequest.setSubmittedJobAndDependenciesList( submittedJobAndDependenciesList );
		
		for ( JobSubmissionJobInterface item : jobSubmissionJobsList ) {
			

			if ( item.getJobTypeName() == null ) {

				throw new IllegalArgumentException( "jobTypeName cannot be null" );
			}
			
			SubmittedJobAndDependencies submittedJobAndDependencies = new SubmittedJobAndDependencies();
			
			submittedJobAndDependencies.setJobTypeName( item.getJobTypeName() );
			submittedJobAndDependencies.setPriority( item.getPriority() );
			submittedJobAndDependencies.setJobParameters( item.getJobParameters() );
			submittedJobAndDependencies.setDependencyJobListPositions( item.getDependencyJobsListPositions() );
			
			submittedJobAndDependenciesList.add( submittedJobAndDependencies );
		}
		
		return submitJobsListWithDependenciesRequest;
	}
	
}
