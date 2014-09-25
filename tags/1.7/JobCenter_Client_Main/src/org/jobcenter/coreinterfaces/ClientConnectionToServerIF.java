package org.jobcenter.coreinterfaces;

import org.jobcenter.request.ClientStatusUpdateRequest;
import org.jobcenter.request.ClientStartupRequest;
import org.jobcenter.request.GetRunIdListRequest;
import org.jobcenter.request.GetRunRequest;
import org.jobcenter.request.JobRequest;
import org.jobcenter.request.SubmitJobRequest;
import org.jobcenter.request.UpdateServerFromJobRunOnClientRequest;
import org.jobcenter.response.ClientStatusUpdateResponse;
import org.jobcenter.response.ClientStartupResponse;
import org.jobcenter.response.GetRunIdListResponse;
import org.jobcenter.response.GetRunResponse;
import org.jobcenter.response.JobResponse;
import org.jobcenter.response.SubmitJobResponse;
import org.jobcenter.response.UpdateServerFromJobRunOnClientResponse;

/**
 * This defines the interface between the core client code and the client code that connects to the server
 *
 */
public interface ClientConnectionToServerIF {

	/**
	 * Initialize
	 */
	public void init() throws Throwable;

	/**
	 *
	 */
	public void destroy();


	/**
	 * Connect to server to identify client and get node identifier
	 *
	 * @param clientStartupRequest
	 * @return
	 * @throws Throwable
	 */
	public ClientStartupResponse clientStartup( ClientStartupRequest clientStartupRequest ) throws Throwable;


	/**
	 * Connect to server to send client status update
	 *
	 * @param clientStatusUpdateRequest
	 * @return
	 * @throws Throwable
	 */
	public ClientStatusUpdateResponse clientStatusUpdate( ClientStatusUpdateRequest clientStatusUpdateRequest ) throws Throwable;



	/**
	 * Get job to process
	 *
	 * @param jobRequest
	 * @return
	 * @throws Throwable
	 */
	public JobResponse getNextJobToProcess( JobRequest jobRequest ) throws Throwable;



	/**
	 * Update job status
	 *
	 * @param updateJobStatusRequest
	 * @return
	 * @throws Throwable
	 */
	public UpdateServerFromJobRunOnClientResponse updateServerFromJobRunOnClient( UpdateServerFromJobRunOnClientRequest updateJobStatusRequest ) throws Throwable;



	/**
	 * Get a particular run object
	 *
	 * @param getRunRequest
	 * @return
	 * @throws Throwable
	 */
	public GetRunResponse getRunRequest( GetRunRequest getRunRequest ) throws Throwable;

	/**
	 * Get a list of run ids for a job id
	 *
	 * @param getRunIdListRequest
	 * @return
	 * @throws Throwable
	 */
	public GetRunIdListResponse getRunIdListRequest( GetRunIdListRequest getRunIdListRequest ) throws Throwable;


	public SubmitJobResponse submitJob( SubmitJobRequest submitJobRequest ) throws Throwable;

}
