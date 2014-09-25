package org.jobcenter.service;

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

/**
 * Everything related to Job management through the GUI
 *
 */
public class GetRequestTypesServiceImpl implements GetRequestTypesService {

	private static Logger log = Logger.getLogger(GetRequestTypesServiceImpl.class);

	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;

	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}


	//  Hibernate DAO

	private RequestTypeDAO requestTypeDAO;
	
	public RequestTypeDAO getRequestTypeDAO() {
		return requestTypeDAO;
	}
	public void setRequestTypeDAO(RequestTypeDAO requestTypeDAO) {
		this.requestTypeDAO = requestTypeDAO;
	}


	//  JDBC DAO


//	private RequestTypeJDBCDAO requestTypeJDBCDAO;
//
//	public RequestTypeJDBCDAO getRequestTypeJDBCDAO() {
//		return requestTypeJDBCDAO;
//	}
//	public void setRequestTypeJDBCDAO(RequestTypeJDBCDAO requestTypeJDBCDAO) {
//		this.requestTypeJDBCDAO = requestTypeJDBCDAO;
//	}



	/**
	 * @param listRequestTypesRequest
	 * @param remoteHost
	 * @return
	 */
	@Override
	public  ListRequestTypesResponse retrieveRequestTypes( ListRequestTypesRequest listRequestTypesRequest, String remoteHost )
	{
		ListRequestTypesResponse listRequestTypesResponse = new ListRequestTypesResponse();

		if ( log.isDebugEnabled() ) {

			log.debug( "retrieveRequestTypes  listRequestTypesRequest.getNodeName() = |" + listRequestTypesRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}


		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( listRequestTypesResponse, listRequestTypesRequest.getNodeName(), remoteHost ) ) {
			
			return listRequestTypesResponse;
		}

//		List<RequestTypeDTO> requestTypes = requestTypeJDBCDAO.getRequestTypes();
		
		List<RequestTypeDTO> requestTypes = requestTypeDAO.findAllOrderedByName();

		listRequestTypesResponse.setRequestTypes( requestTypes );

		return listRequestTypesResponse;
	}

}
