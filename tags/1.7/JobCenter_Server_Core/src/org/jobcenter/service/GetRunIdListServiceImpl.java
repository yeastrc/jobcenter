package org.jobcenter.service;

import java.util.List;

import org.apache.log4j.Logger;
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

public class GetRunIdListServiceImpl implements GetRunIdListService {  

	private static Logger log = Logger.getLogger(GetRunIdListServiceImpl.class);

	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;

	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
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
	 * @see org.jobcenter.service.GetRunIdListService#getRunIdListFromJob(org.jobcenter.request.GetRunIdListRequest, java.lang.String)
	 */
	@Override
	public GetRunIdListResponse getRunIdListFromJob( GetRunIdListRequest getRunIdListRequest, String remoteHost )
	{
		final String method = "getRunIdListFromJob";


		if ( getRunIdListRequest == null ) {

			log.error( method + "  IllegalArgument:getRunIdListRequest == null");

			throw new IllegalArgumentException( "getRunIdListRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( method + " getRunIdListRequest.getNodeName() = |" + getRunIdListRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		GetRunIdListResponse getRunResponse = new GetRunIdListResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( getRunResponse, getRunIdListRequest.getNodeName(), remoteHost ) ) {

			return getRunResponse;
		}

		int jobId = getRunIdListRequest.getJobId();

		List<Integer> runIdList = jobJDBCDAO.getRunIdsByJobId( jobId );

		getRunResponse.setRunIdList( runIdList );


		return getRunResponse;



	}
}