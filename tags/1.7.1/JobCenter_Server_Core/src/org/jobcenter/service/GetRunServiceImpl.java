package org.jobcenter.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.dao.RunDAO;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.jdbc.JobJDBCDAO;
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

public class GetRunServiceImpl implements GetRunService { 

	private static Logger log = Logger.getLogger(GetRunServiceImpl.class);

	private static final int RUN_ID_NOT_SET = -1;

	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;

	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}

	//  DAO

	private RunDAO runDAO;

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


	/* (non-Javadoc)
	 * @see org.jobcenter.service.GetRunService#getRunFromJob(org.jobcenter.request.GetRunRequest, java.lang.String)
	 */
	@Override
	public GetRunResponse getRunFromJob( GetRunRequest getRunRequest, String remoteHost )
	{
		final String method = "getRunFromJob";


		if ( getRunRequest == null ) {

			log.error( method + "  IllegalArgument:getRunRequest == null");

			throw new IllegalArgumentException( "getRunRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( method + " getRunRequest.getNodeName() = |" + getRunRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		GetRunResponse getRunResponse = new GetRunResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( getRunResponse, getRunRequest.getNodeName(), remoteHost ) ) {

			return getRunResponse;
		}

		int jobId = getRunRequest.getJobId();

		Integer runIdFromRequest = getRunRequest.getCurrentRunId();

		//  get run

		List<Integer> runIdList = jobJDBCDAO.getRunIdsByJobId( jobId );

		//  only continue if first run id is not current run id ( if run id is provided )
		if ( runIdList != null && ( ! runIdList.isEmpty() ) 
				&& ( runIdFromRequest == null || runIdList.get( 0 ).intValue() != runIdFromRequest.intValue() ) ) {

			int runId = RUN_ID_NOT_SET;

			if ( getRunRequest.getGetRunRequestType() == GetRunRequest.GetRunRequestType.GET_FIRST_RUN ) {

				runId = runIdList.get( 0 );

			} else if ( getRunRequest.getGetRunRequestType() == GetRunRequest.GetRunRequestType.GET_PREVIOUS_RUN ) {

				for ( int count = runIdList.size() - 1; count >= 0; count-- ) {
					
					if ( runIdList.get(count).intValue() == runIdFromRequest.intValue() ) {
						
						runId = runIdList.get(count - 1).intValue();
						break;
					}
					
				}


			} else if ( getRunRequest.getGetRunRequestType() == GetRunRequest.GetRunRequestType.GET_RUN_FROM_INDEX ) {

				if ( getRunRequest.getRunIndex() >= 0 && getRunRequest.getRunIndex() < runIdList.size() ) {

					runId = runIdList.get( getRunRequest.getRunIndex() );

				} else {

					//  TODO  consider returning an error
				}

			} else {

				String msg = "'GetRunRequest.getRunRequestType' is not set.";

				log.error( msg );

				throw new IllegalArgumentException( msg );

			}

			if ( runId != RUN_ID_NOT_SET ) {

				RunDTO run = runDAO.findById( runId );

				if ( run != null ) {
					
					jobJDBCDAO.retrieveRunOutputParams( run );

					getRunResponse.setRun( run );
				}

			}

		}


		return getRunResponse;



	}
}