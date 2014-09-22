package org.jobcenter.internalservice;

import org.jobcenter.dto.Job;
import org.jobcenter.dto.JobType;
import org.jobcenter.dto.RequestTypeDTO;
import org.jobcenter.nondbdto.SubmittedJobAndDependencies;
import org.jobcenter.request.SubmitJobRequest;
import org.jobcenter.request.SubmitJobsListWithDependenciesRequest;
import org.jobcenter.response.BaseResponse;

public interface SubmitJobInternalService {

	/**
	 * @param requestTypeName
	 * @param requestId
	 * @param baseResponse
	 * @return
	 */
	public abstract RequestTypeDTO validateRequestTypeNameRequestId(
			String requestTypeName, Integer requestId, BaseResponse baseResponse);

	/**
	 * @param jobTypeName
	 * @param requestId
	 * @param baseResponse
	 * @return
	 */
	public abstract JobType validateJobTypeName(String jobTypeName,
			Integer requestId, BaseResponse baseResponse);

	/**
	 * @param submitJobRequestRequiredExecutionThreads
	 * @param jobTypeMaxRequiredExecutionThreads
	 * @param baseResponse
	 * @return false if fails to validate
	 */
	public abstract boolean validateRequiredExecutionThreads( Integer submitJobRequestRequiredExecutionThreads, 
			Integer jobTypeMaxRequiredExecutionThreads, BaseResponse baseResponse );

	
	/**
	 * @param requestTypeDTO
	 * @return
	 */
	public abstract int insertRequest(RequestTypeDTO requestTypeDTO);

	/**
	 * @param submitJobRequest
	 * @param jobType
	 * @param requestId
	 * @return
	 */
	public abstract Job createJobFromSubmitJobRequest(
			SubmitJobRequest submitJobRequest, JobType jobType, int requestId);

	/**
	 * @param submitJobsListWithDependenciesRequest
	 * @param submittedJobAndDependencies
	 * @param jobType
	 * @param requestId
	 * @return
	 */
	public abstract Job createJobFromSubmittedJobAndDependencies( SubmitJobsListWithDependenciesRequest submitJobsListWithDependenciesRequest, SubmittedJobAndDependencies submittedJobAndDependencies, JobType jobType, Integer requestId );

	/**
	 * @param job
	 * @return
	 */
	public abstract int insertJob(Job job);

}