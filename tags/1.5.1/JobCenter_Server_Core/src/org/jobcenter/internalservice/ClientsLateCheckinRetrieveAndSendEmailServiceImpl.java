package org.jobcenter.internalservice;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.NodeClientStatusDTO;
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
* Does sub processing for the class ProcessClientsStatusForLateCheckinsServiceImpl
*
* This is to handle transactions and Hibernate sessions
*
* This class does the retrieval of the clients that are late and generates the emails
*
*/

//Spring database transaction demarcation.
//Spring will start a database transaction when any method is called and call commit when it completed
//  or call roll back if an unchecked exception is thrown.
@Transactional ( propagation = Propagation.REQUIRED, readOnly = false )

public class ClientsLateCheckinRetrieveAndSendEmailServiceImpl implements ClientsLateCheckinRetrieveAndSendEmailService {






	private static Logger log = Logger.getLogger(ClientsLateCheckinRetrieveAndSendEmailServiceImpl.class);


	private static final String EMAIL_SUBJECT_LINE = "JobCenter clients are late for checkin";


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
	public SendEmailService getSendEmailService() {
		return sendEmailService;
	}
	public void setSendEmailService(SendEmailService sendEmailService) {
		this.sendEmailService = sendEmailService;
	}
	public GetMailConfig getGetMailConfig() {
		return getMailConfig;
	}
	public void setGetMailConfig(GetMailConfig getMailConfig) {
		this.getMailConfig = getMailConfig;
	}


	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.ClientsLateCheckinRetrieveAndSendEmailService#sendClientsCheckinLateNotification()
	 */
	public  List<NodeClientStatusDTO> sendClientsCheckinLateNotification(  )
	{


		MailConfig mailConfig = getMailConfig.getClientCheckinMailConfig();


		if ( mailConfig == null ) {

			return null;

		}

		List<NodeClientStatusDTO> clients = getClientsStatusListService.retrieveClientsLateForCheckinList();

		if ( clients != null && ! clients.isEmpty() ) {

			sendMail( clients, mailConfig );

		} else {

			log.debug( "No late clients or already sent email for all late clients." );
		}

		return clients;

	}



	/**
	 * @param clients
	 * @param mailConfig
	 */
	private void sendMail( List<NodeClientStatusDTO> clients, MailConfig mailConfig )
	{

		// send email
		try {

			// create the message body

			StringBuilder text = new StringBuilder(1000);

			text.append( "The JobCenter clients listed below are late for checkin and may have problems.\n\n" );

			for ( NodeClientStatusDTO client : clients ) {


				if ( ! client.getNotificationSentThatClientLate() ) {

					text.append(  "client: " );
					text.append(  client.getNode().getName() );
					text.append( "\n" );
					text.append( "\n" );

					if ( client.getRunningJobs() != null && ! client.getRunningJobs().isEmpty() ) {

						text.append(  "     The following jobs are in \"Running\" status on this node: " );
						text.append( "\n" );
						text.append( "\n" );

						for ( Job job : client.getRunningJobs() ) {

							text.append(  "     Job Id: " );
							text.append(  job.getId() );
							text.append( "\n" );

							text.append(  "     Job Type Name: " );
							text.append(  job.getJobType().getName() );
							text.append( "\n" );
						}

						text.append( "\n" );
					}
				}
			}


			String emailBody = text.toString();


			//  send to configured list of recipients in config table of the database

			for ( String toEmailAddress : mailConfig.getToAddresses() ) {

				if ( ! StringUtils.isEmpty( toEmailAddress ) ) {

					sendEmailService.sendEmail( mailConfig.getSmtpEmailHost(),
							mailConfig.getFromEmailAddress(), toEmailAddress, EMAIL_SUBJECT_LINE, emailBody );
				}
			}

		} catch (Throwable e) {

			log.error("sendMail Exception: " + e.toString(), e);

			throw new RuntimeException( e );
		}


	}



}
