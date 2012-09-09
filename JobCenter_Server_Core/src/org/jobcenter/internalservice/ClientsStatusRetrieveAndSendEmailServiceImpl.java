package org.jobcenter.internalservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jobcenter.constants.JobStatusValuesConstantsCopy;
import org.jobcenter.constants.ServerConfigKeyValues;
import org.jobcenter.dao.JobDAO;
import org.jobcenter.dao.NodeClientStatusDAO;
import org.jobcenter.dao.RunDAO;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.NodeClientStatusDTO;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.dtoservernondb.MailConfig;
import org.jobcenter.service.GetClientsStatusListService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//The only way to get proper database roll backs ( managed by Spring ) is to only use un-checked exceptions.
//
//The best way to make sure there are no checked exceptions is to have no "throws" on any of the methods.


//@Transactional causes Spring to surround calls to methods in this class with a database transaction.
//      Spring will roll back the transaction if a un-checked exception ( extended from RuntimeException ) is thrown.
//               Otherwise it commits the transaction.


/**
* This class does the retrieval of the status of the clients and generates the email
*
*/

//Spring database transaction demarcation.
//Spring will start a database transaction when any method is called and call commit when it completed
//  or call roll back if an unchecked exception is thrown.
@Transactional ( propagation = Propagation.REQUIRED, readOnly = true )

public class ClientsStatusRetrieveAndSendEmailServiceImpl {


	private static Logger log = Logger.getLogger(ClientsStatusRetrieveAndSendEmailServiceImpl.class);


	private static final String EMAIL_SUBJECT_LINE = "JobCenter clients status";


	//  Service

	private GetClientsStatusListService getClientsStatusListService;

	private GetValueFromConfigService getValueFromConfigService;
	
	private GetMailConfig getMailConfig;

	private SendEmailService sendEmailService;


	public GetClientsStatusListService getGetClientsStatusListService() {
		return getClientsStatusListService;
	}
	public void setGetClientsStatusListService(
			GetClientsStatusListService getClientsStatusListService) {
		this.getClientsStatusListService = getClientsStatusListService;
	}
	public GetValueFromConfigService getGetValueFromConfigService() {
		return getValueFromConfigService;
	}
	public void setGetValueFromConfigService(
			GetValueFromConfigService getValueFromConfigService) {
		this.getValueFromConfigService = getValueFromConfigService;
	}
	public GetMailConfig getGetMailConfig() {
		return getMailConfig;
	}
	public void setGetMailConfig(GetMailConfig getMailConfig) {
		this.getMailConfig = getMailConfig;
	}
	public SendEmailService getSendEmailService() {
		return sendEmailService;
	}
	public void setSendEmailService(SendEmailService sendEmailService) {
		this.sendEmailService = sendEmailService;
	}


	//  Hibernate DAO

	private RunDAO runDAO;
	private JobDAO jobDAO;
	private NodeClientStatusDAO nodeClientStatusDAO;

	public NodeClientStatusDAO getNodeClientStatusDAO() {
		return nodeClientStatusDAO;
	}
	public void setNodeClientStatusDAO(NodeClientStatusDAO nodeClientStatusDAO) {
		this.nodeClientStatusDAO = nodeClientStatusDAO;
	}	

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

	
	/**
	 * 
	 */
	public void sendClientsStatusNotification(  )
	{

		MailConfig mailConfig = getMailConfig.getClientStatusMailConfig();

		if ( mailConfig == null ) {

			return;
		}
		
		String serverBaseAddress = getValueFromConfigService.getConfigValueAsString( ServerConfigKeyValues.CLIENT_STATUS_NOTIFICATION_SERVER_ADDRESS );
		
		if ( StringUtils.isEmpty( serverBaseAddress ) ) {
			
			String msg = "Server address for links in Client Status email is not set so the running jobs will not be links.";
			
			log.warn( msg );
			
			serverBaseAddress = null;
			
		} else {
			if ( ! serverBaseAddress.startsWith( "http://" ) ) {
				
				serverBaseAddress = "http://" + serverBaseAddress;
			}
			
			if ( ! serverBaseAddress.endsWith( "/" ) ) {
			
				serverBaseAddress = serverBaseAddress + "/";
			}
		}

		List results = nodeClientStatusDAO.findAll();

		List<NodeClientStatusDTO> clients = (List<NodeClientStatusDTO> ) results;

		//  compute if late

		Date now = new Date();

		for ( NodeClientStatusDTO client : clients ) {

			if ( client.getLateForNextCheckinTime() != null && client.getLateForNextCheckinTime().before( now ) ) {

				client.setCheckinIsLate( true );
			}

			populateJobsForClient( client );
		}
		
		
		if ( clients != null && ! clients.isEmpty() ) {

			sendMail( clients, mailConfig, serverBaseAddress );
		}


	}



	/**
	 * @param clients
	 * @param mailConfig
	 */
	private void sendMail( List<NodeClientStatusDTO> clients, MailConfig mailConfig, String serverBaseAddress )
	{

		// send email
		try {

			// create the message body

			StringBuilder text = new StringBuilder(1000);

			text.append( "<p><b>The current status of the JobCenter clients.</b></p>" );


			text.append( "<table border='1' cellpadding='2'>" );

			text.append( "<tr>" );
			text.append( "<th nowrap='nowrap'>Node Name</th>" );
			text.append( "<th nowrap='nowrap'>Late</th>" );
			text.append( "<th nowrap='nowrap'>Up/Down</th>" );
			text.append( "<th nowrap='nowrap'>Last Checkin Time</th>" );
			text.append( "<th nowrap='nowrap'>Time considered late for next checkin time</th>" );
			text.append( "<th nowrap='nowrap'>Running jobs</th>" );
			text.append( "</tr>" );
			  
			  
			for ( NodeClientStatusDTO client : clients ) {

				text.append( "<tr>" );

				text.append( "<td nowrap='nowrap'>" );

				text.append( client.getNode().getName() );

				text.append( "</td>" );
				text.append( "<td nowrap='nowrap'>" );

				if ( client.isCheckinIsLate() ) {

					text.append( "<span style='color:red' >true</span>" );

				}
				text.append( "</td>" );
				text.append( "<td nowrap='nowrap'>" );

				if ( client.getClientStarted() != null && client.getClientStarted() ) {

					text.append( "Client was started" );
				} else {

					text.append( "Client has been shut down" );
				}
				text.append( "</td>" );

				text.append( "<td nowrap='nowrap'>" );

				text.append( client.getLastCheckinTime() );
				
				text.append( "</td>" );

				text.append( "<td nowrap='nowrap'>" );
				
				if (  client.getLateForNextCheckinTime() == null ) {
					
					text.append( "Shutdown so not expected to check in" );
				} else {
					text.append( client.getLateForNextCheckinTime() );
				}

				text.append( "</td>" );
				
				text.append( "<td >" );

				if ( client.getRunningJobs() != null ) {

					boolean firstJob = true;

					for ( Job runningJob : client.getRunningJobs() ) {


						if ( firstJob ) {

							firstJob = false;
						} else {

							text.append( "<br/>" );
						}
						
						if ( serverBaseAddress != null ) {

							text.append( "<a href='" );

							text.append( serverBaseAddress );
							text.append( "viewJob.do?jobId=" );
							text.append( Integer.toString( runningJob.getId() ) );
							text.append( "' >" );
						}
						text.append( runningJob.getJobType().getName() );
						text.append( "(" );
						if ( runningJob.getCurrentRun() != null && runningJob.getCurrentRun().getId() != null ) {
							text.append( runningJob.getCurrentRun().getId() );
						}
						text.append( ")" );

						if ( serverBaseAddress != null ) {

							text.append( "<a>" );
						}

					}
				}
			}


			String emailBody = text.toString();


			//  send to configured list of recipients in config table of the database

			for ( String toEmailAddress : mailConfig.getToAddresses() ) {

				if ( ! StringUtils.isEmpty( toEmailAddress ) ) {

					sendEmailService.sendEmailHTML ( mailConfig.getSmtpEmailHost(),
							mailConfig.getFromEmailAddress(), toEmailAddress, EMAIL_SUBJECT_LINE, emailBody );
				}
			}

		} catch (Throwable e) {

			log.error("sendMail Exception: " + e.toString(), e);

			throw new RuntimeException( e );
		}


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
