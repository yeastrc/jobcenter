package org.jobcenter.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jobcenter.dao.*;
import org.jobcenter.dto.*;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.jdbc.*;
import org.jobcenter.request.*;
import org.jobcenter.response.*;


import org.apache.log4j.Logger;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//The only way to get proper roll backs ( managed by Spring ) is to only use un-checked exceptions.
//
//The best way to make sure there are no checked exceptions is to have no "throws" on any of the methods.


//@Transactional causes Spring to surround calls to methods in this class with a database transaction.
//        Spring will roll back the transaction if a un-checked exception ( extended from RuntimeException ) is thrown.
//                 Otherwise it commits the transaction.

@Transactional ( propagation = Propagation.REQUIRED, readOnly = false )



public class GetRequestListServiceImpl implements GetRequestListService {

	private static Logger log = Logger.getLogger(GetRequestListServiceImpl.class);

	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;

	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}


	//  Hibernate DAO

	private JobDAO jobDAO;

	private RequestDAO requestDAO;


	public JobDAO getJobDAO() {
		return jobDAO;
	}
	public void setJobDAO(JobDAO jobDAO) {
		this.jobDAO = jobDAO;
	}
	public RequestDAO getRequestDAO() {
		return requestDAO;
	}
	public void setRequestDAO(RequestDAO requestDAO) {
		this.requestDAO = requestDAO;
	}


	//  JDBC DAO

	private JobJDBCDAO jobJDBCDAO;

	private RequestJDBCDAO requestJDBCDAO;

	public RequestJDBCDAO getRequestJDBCDAO() {
		return requestJDBCDAO;
	}
	public void setRequestJDBCDAO(RequestJDBCDAO requestJDBCDAO) {
		this.requestJDBCDAO = requestJDBCDAO;
	}

	public JobJDBCDAO getJobJDBCDAO() {
		return jobJDBCDAO;
	}
	public void setJobJDBCDAO(JobJDBCDAO jobJDBCDAO) {
		this.jobJDBCDAO = jobJDBCDAO;
	}
	
	/* (non-Javadoc)
	 * @see org.jobcenter.service.GetRequestListService#retrieveRequestsList(org.jobcenter.request.ListRequestsRequest, java.lang.String)
	 */
	public  ListRequestsResponse retrieveRequestsList( ListRequestsRequest listRequestsRequest, String remoteHost )
	{
		ListRequestsResponse listRequestsResponse = new ListRequestsResponse();

		if ( listRequestsRequest == null ) {

			log.error("retrieveJobsList(...) IllegalArgument: listRequestsRequest == null");

			throw new IllegalArgumentException( "listRequestsRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( "retrieveJobsList  listRequestsRequest.getNodeName() = |" + listRequestsRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}



		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( listRequestsResponse, listRequestsRequest.getNodeName(), remoteHost ) ) {
			
			return listRequestsResponse;
		}
		
		if ( log.isDebugEnabled() ) {
		
			log.debug( "retrieveJobsList  listRequestsRequest = " + listRequestsRequest );
		}

		List<Integer> requestIdList = requestJDBCDAO.getRequestIdList( listRequestsRequest );
		
		List<RequestDTO> requestsList = null;

		if ( requestIdList != null ) {
			
			requestsList = new ArrayList<RequestDTO>( requestIdList.size() );
			
			for ( int requestId : requestIdList ) {
				
				RequestDTO requestDTO = requestDAO.findById( requestId );
				
				if ( requestDTO != null ) {

					requestsList.add( requestDTO );
					
					List<Job> jobs = jobDAO.findByRequestId( requestId );
					
					Collections.sort( jobs, new Job.ReverseSortByIdComparator() );
					
					requestDTO.setJobList( jobs );

					for ( Job job : jobs ) {

						jobJDBCDAO.retrieveJobParameters( job );
					}
					
					
				}
			}

		}

		listRequestsResponse.setRequests( requestsList );

		return listRequestsResponse;
	}

}
