package org.jobcenter.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jobcenter.constants.JobStatusValuesConstantsCopy;
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



public class GetClientsStatusListServiceImpl implements GetClientsStatusListService {

	private static Logger log = Logger.getLogger(GetClientsStatusListServiceImpl.class);

	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;

	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}


	//  Hibernate DAO

	private RunDAO runDAO;
	private JobDAO jobDAO;

	NodeClientStatusDAO nodeClientStatusDAO;


	public RunDAO getRunDAO() {
		return runDAO;
	}
	public void setRunDAO(RunDAO runDAO) {
		this.runDAO = runDAO;
	}
	public JobDAO getJobDAO() {
		return jobDAO;
	}
	public void setJobDAO(JobDAO jobDAO) {
		this.jobDAO = jobDAO;
	}
	public NodeClientStatusDAO getNodeClientStatusDAO() {
		return nodeClientStatusDAO;
	}
	public void setNodeClientStatusDAO(NodeClientStatusDAO nodeClientStatusDAO) {
		this.nodeClientStatusDAO = nodeClientStatusDAO;
	}

	//  JDBC DAO



	/* (non-Javadoc)
	 * @see org.jobcenter.service.GetClientsStatusListService#retrieveClientsStatusList(org.jobcenter.request.ListClientsStatusRequest, java.lang.String)
	 */
	@Override
	public  ListClientsStatusResponse retrieveClientsStatusList( ListClientsStatusRequest listClientsStatusRequest, String remoteHost )
	{
		ListClientsStatusResponse listClientsStatusResponse = new ListClientsStatusResponse();

		if ( listClientsStatusRequest == null ) {

			log.error("retrieveClientsStatusList(...) IllegalArgument: listClientsStatusRequest == null");

			throw new IllegalArgumentException( "listClientsStatusRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( "retrieveClientsStatusList  listClientsStatusRequest.getNodeName() = |" + listClientsStatusRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}



		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( listClientsStatusResponse, listClientsStatusRequest.getNodeName(), remoteHost ) ) {

			return listClientsStatusResponse;
		}

		if ( log.isDebugEnabled() ) {

			log.debug( "retrieveClientsStatusList  listClientsStatusRequest = " + listClientsStatusRequest );
		}

		List results = nodeClientStatusDAO.findAll();

		List<NodeClientStatusDTO> clients = (List<NodeClientStatusDTO> ) results;

		//  compute if late

		Date now = new Date();

		for ( NodeClientStatusDTO client : clients ) {

			if ( client.getLateForNextCheckinTime().before( now ) ) {

				client.setCheckinIsLate( true );
			}

			populateJobsForClient( client );
		}


		listClientsStatusResponse.setClients( clients );

		return listClientsStatusResponse;
	}

	
	
	/* 
	 * retrieve clients that are past due to check in.
	 * 
	 * (non-Javadoc)
	 * @see org.jobcenter.service.GetClientsStatusListService#retrieveClientsLateForCheckinList()
	 */
	@Override
	public  List<NodeClientStatusDTO>  retrieveClientsLateForCheckinList(  )
	{
		Date now = new Date();

		List results = nodeClientStatusDAO.findByLastCheckinTimeLessThanParameter( now );

		List<NodeClientStatusDTO> clients = (List<NodeClientStatusDTO> ) results;

		//  compute if late

		for ( NodeClientStatusDTO client : clients ) {

			if ( client.getLateForNextCheckinTime().before( now ) ) {

				client.setCheckinIsLate( true );
			}

			populateJobsForClient( client );
		}


		return clients;
		
		
	}
	
	
	
	
	
	
	
	
	
	

	/**
	 * @param client
	 */
	private void populateJobsForClient( NodeClientStatusDTO client ) {

		List<Job> runningJobs = new ArrayList<Job>();


		List<RunDTO> runs = runDAO.findByNodeIdAndStatusId( client.getNodeId(), JobStatusValuesConstantsCopy.JOB_STATUS_RUNNING );

		if ( log.isDebugEnabled() ) {

			log.debug( "populateJobsForClient(...):  Searching for runs for node id = " + client.getNodeId()
					+ ", number of runs found = " + runs.size() );
		}

		if ( runs != null ) {

			for ( RunDTO run : runs ) {

				Job job = jobDAO.findById( run.getJobId() );

				if ( job != null ) {

					job.setCurrentRun( run );

					runningJobs.add( job );
				}
			}

		}

		client.setRunningJobs( runningJobs );

	}


}
