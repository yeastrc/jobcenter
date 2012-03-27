package org.jobcenter.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.jobcenter.constants.Constants;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.jdbc.*;
import org.jobcenter.request.JobRequest;
import org.jobcenter.request.JobRequestModuleInfo;
import org.jobcenter.response.BaseResponse;
import org.jobcenter.response.JobResponse;
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

	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}

	//  DAO

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
	public JobResponse getNextJobForClientService( JobRequest jobRequest, String remoteHost )
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


		JobResponse jobResponse = new JobResponse();


		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( jobResponse, jobRequest.getNodeName(), remoteHost ) ) {

			return jobResponse;
		}

		Integer nodeId = clientNodeNameCheck.getNodeIdForNodeName( jobRequest.getNodeName() );




		Job job = getNextJobForClient( jobRequest, nodeId );

		jobResponse.setJob( job );

		if ( job == null ) {

			jobResponse.setJobFound( false );
		} else {

			jobResponse.setJobFound( true );


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

		return jobResponse;
	}


	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 */
	private Job getNextJobForClient( JobRequest jobRequest, Integer nodeId ) {

		final String method = "getNextJobForClient";

		if ( log.isDebugEnabled() ) {
			log.debug( "Entering " + method );
		}

		Job job = null;

		RunDTO currentRun = null;

		try {

			job = getNextJobForClientJDBCDAO.retrieveNextJobRecordForJobRequest( jobRequest );

			if ( job != null ) {

				jobJDBCDAO.updateJobStatusOnIdAndDbRecordVersionNumber( job, JobStatusValuesConstants.JOB_STATUS_RUNNING );

				currentRun = jobJDBCDAO.insertRunTableRecord( nodeId, job);

				currentRun.setJob( job );

				job.setCurrentRun( currentRun );

				job.setCurrentRunId( currentRun.getId() );

				jobJDBCDAO.retrieveJobParameters( job );
			}

		} catch (Throwable sqlEx) {

			log.error( method + ": Exception:  will be performing transaction rollback:  Exception message: " + sqlEx.toString() + '.', sqlEx);


			//  Wrap in RuntimeException for Spring Transactional rollback
			throw new RuntimeException( sqlEx );

		} finally {


		}

		return job;

	}



}
