package org.jobcenter.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.dao.JobDAO;
import org.jobcenter.dao.RunDAO;
import org.jobcenter.dao.StatusDAO;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.dto.RunMessageDTO;
import org.jobcenter.dto.StatusDTO;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.jdbc.JobJDBCDAO;
import org.jobcenter.request.UpdateServerFromJobRunOnClientRequest;
import org.jobcenter.response.UpdateServerFromJobRunOnClientResponse;
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

public class UpdateServerFromJobRunOnClientServiceImpl implements UpdateServerFromJobRunOnClientService {

	private static Logger log = Logger.getLogger(UpdateServerFromJobRunOnClientServiceImpl.class);


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
	private RunDAO runDAO;
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
	public RunDAO getRunDAO() {
		return runDAO;
	}
	public void setRunDAO(RunDAO runDAO) {
		this.runDAO = runDAO;
	}




	//  JDBCDAO

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
	@Override
	public UpdateServerFromJobRunOnClientResponse updateServerFromJobRunOnClient( UpdateServerFromJobRunOnClientRequest updateServerFromJobRunOnClientRequest, String remoteHost )
	{
		final String method = "updateServerFromJobRunOnClient";


		if ( updateServerFromJobRunOnClientRequest == null ) {

			log.error( method + "  IllegalArgument:updateServerFromJobRunOnClientRequest == null");

			throw new IllegalArgumentException( "updateServerFromJobRunOnClientRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( method + " updateServerFromJobRunOnClientRequest.getNodeName() = |" + updateServerFromJobRunOnClientRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		UpdateServerFromJobRunOnClientResponse updateServerFromJobRunOnClientResponse = new UpdateServerFromJobRunOnClientResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( updateServerFromJobRunOnClientResponse, updateServerFromJobRunOnClientRequest.getNodeName(), remoteHost ) ) {

			return updateServerFromJobRunOnClientResponse;
		}

		Job jobFromRequest = updateServerFromJobRunOnClientRequest.getJob();

		RunDTO runFromRequest = jobFromRequest.getCurrentRun();

		StatusDTO newStatus = statusDAO.findById(  jobFromRequest.getStatusId() );

		if ( newStatus == null ) {

			String msg = "New job status not found in status table so not valid, = " +  jobFromRequest.getStatusId();

			log.error( msg );
			throw new RuntimeException( msg );
		}

		//  update run

		RunDTO run = runDAO.findById( runFromRequest.getId() );

		if ( run == null ) {

			String msg = ", Unable to find run to update it, id = " + runFromRequest.getId();

			log.error( method + msg );

			throw new RuntimeException( msg );
		}

		if ( run.getStatus() == null ) {

			String msg = ", run.status = null. run id = " + runFromRequest.getId();

			log.error( method + msg );

			throw new RuntimeException( msg );
		}

		//  only update the run if it is currently in "Running" status
		if ( JobStatusValuesConstants.JOB_STATUS_RUNNING != run.getStatus().getId() ) {


			if ( log.isInfoEnabled() ) {
				log.info( method + " job id = " + jobFromRequest.getId() + ".  run id = " + run.getId() + " run status from db was = " + run.getStatus().getId() + ", run status is not 'running' ( "
						+ JobStatusValuesConstants.JOB_STATUS_RUNNING + " ) so not changing it to the status sent from the client (" + runFromRequest.getStatusId() + ")" );
			}


		} else {


			if ( log.isInfoEnabled() ) {
				log.info( method + " job id = " + jobFromRequest.getId() + ".  run id = " + run.getId() + " run status was = " + run.getStatus().getId() + ", changing run status to " + runFromRequest.getStatusId() );
			}


			run.setStatus( newStatus );

			if ( runFromRequest.getEndDate() != null ) {

				run.setEndDate( runFromRequest.getEndDate() );
			}

			runDAO.saveOrUpdate( run );

			// update job

			Job job = jobDAO.findById( jobFromRequest.getId() );

			if ( job == null ) {

				String msg = ", Unable to find job to update it, id = " + jobFromRequest.getId();

				log.error( method + msg );

				throw new RuntimeException( msg );
			}

			if ( log.isDebugEnabled() ) {
				log.debug( method + " job id = " + job.getId() + ".  job status was = " + job.getStatusId() + ", changing job status to " + jobFromRequest.getStatusId() );
			}

			job.setStatus( newStatus );

			jobDAO.saveOrUpdate( job );

			// save run messages

			List<RunMessageDTO> runMessages = runFromRequest.getRunMessages();

			if ( runMessages != null && ! runMessages.isEmpty() ) {

				for ( RunMessageDTO runMessage : runMessages ) {

					jobJDBCDAO.insertRunMessageFromModuleRecord( runMessage, run );
				}
			} else {

				if ( log.isDebugEnabled() ) {

					log.debug( method + "No run messages for run id " + run.getId() );
				}
			}

			Map<String, String> runOutputParams = runFromRequest.getRunOutputParams();


			if ( runOutputParams != null && ! runOutputParams.isEmpty() ) {

				for ( Entry<String, String> runOutputParam : runOutputParams.entrySet() ) {

					jobJDBCDAO.insertRunOutputParam( runOutputParam.getKey(), runOutputParam.getValue(), run );
				}
			}

		}


		return updateServerFromJobRunOnClientResponse;
	}




}
