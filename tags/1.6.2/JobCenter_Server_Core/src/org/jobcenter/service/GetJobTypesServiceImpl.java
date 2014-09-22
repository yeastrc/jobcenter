package org.jobcenter.service;

import java.util.ArrayList;
import java.util.Date;
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
public class GetJobTypesServiceImpl implements GetJobTypesService {

	private static Logger log = Logger.getLogger(GetJobTypesServiceImpl.class);

	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;

	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}


	//  Hibernate DAO

	private JobTypeDAO jobTypeDAO;
	
	public JobTypeDAO getJobTypeDAO() {
		return jobTypeDAO;
	}
	public void setJobTypeDAO(JobTypeDAO jobTypeDAO) {
		this.jobTypeDAO = jobTypeDAO;
	}

	//  JDBC DAO




//	private JobTypeJDBCDAO jobTypeJDBCDAO;
//
//	public JobTypeJDBCDAO getJobTypeJDBCDAO() {
//		return jobTypeJDBCDAO;
//	}
//	public void setJobTypeJDBCDAO(JobTypeJDBCDAO jobTypeJDBCDAO) {
//		this.jobTypeJDBCDAO = jobTypeJDBCDAO;
//	}




	/**
	 * @param listJobTypesRequest
	 * @param remoteHost
	 * @return
	 */
	@Override
	public  ListJobTypesResponse retrieveJobTypes( ListJobTypesRequest listJobTypesRequest, String remoteHost )
	{
		ListJobTypesResponse listJobTypesResponse = new ListJobTypesResponse();

		if ( log.isDebugEnabled() ) {

			log.debug( "retrieveJob  listJobsJob.getNodeName() = |" + listJobTypesRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( listJobTypesResponse, listJobTypesRequest.getNodeName(), remoteHost ) ) {
			
			return listJobTypesResponse;
		}

//		List<JobType> jobTypes = jobTypeJDBCDAO.getJobTypes();
		
		List<String> jobTypeNames = listJobTypesRequest.getJobTypeNames();
		
		List<JobType> jobTypes = null;
		
		if ( jobTypeNames == null || jobTypeNames.isEmpty() ) {
		
			jobTypes = jobTypeDAO.findAllOrderedByName();
			
		} else {

			jobTypes = new ArrayList<JobType>();
			
			for ( String jobTypeName : jobTypeNames ) {
			
				JobType jobType = jobTypeDAO.findOneRecordByName( jobTypeName );
				
				jobTypes.add( jobType );
			}
			
		}

		listJobTypesResponse.setJobTypes( jobTypes );

		return listJobTypesResponse;
	}
}
