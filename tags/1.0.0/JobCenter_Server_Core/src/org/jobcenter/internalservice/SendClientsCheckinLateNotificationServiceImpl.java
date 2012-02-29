package org.jobcenter.internalservice;

import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jobcenter.constants.ServerConfigKeyValues;
import org.jobcenter.dao.*;
import org.jobcenter.dto.*;
import org.jobcenter.service.GetClientsStatusListService;


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



public class SendClientsCheckinLateNotificationServiceImpl implements SendClientsCheckinLateNotificationService {




	private static Logger log = Logger.getLogger(SendClientsCheckinLateNotificationServiceImpl.class);

	//  Service

//	 private ClientNodeNameCheck clientNodeNameCheck;

	private GetClientsStatusListService getClientsStatusListService;

	private GetValueFromConfigService getValueFromConfigService;


	public GetValueFromConfigService getGetValueFromConfigService() {
		return getValueFromConfigService;
	}
	public void setGetValueFromConfigService(
			GetValueFromConfigService getValueFromConfigService) {
		this.getValueFromConfigService = getValueFromConfigService;
	}
	public GetClientsStatusListService getGetClientsStatusListService() {
		return getClientsStatusListService;
	}
	public void setGetClientsStatusListService(
			GetClientsStatusListService getClientsStatusListService) {
		this.getClientsStatusListService = getClientsStatusListService;
	}

//	public ClientNodeNameCheck getClientNodeNameCheck() {
//		return clientNodeNameCheck;
//	}
//	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
//		this.clientNodeNameCheck = clientNodeNameCheck;
//	}


	//  Hibernate DAO

	private NodeClientStatusDAO nodeClientStatusDAO;


	public NodeClientStatusDAO getNodeClientStatusDAO() {
		return nodeClientStatusDAO;
	}
	public void setNodeClientStatusDAO(NodeClientStatusDAO nodeClientStatusDAO) {
		this.nodeClientStatusDAO = nodeClientStatusDAO;
	}

	//  JDBC DAO


	private boolean loggedUnusableFromEmailAddressMsg = false;
	private boolean loggedUnusableToEmailAddressMsg = false;

	/* (non-Javadoc)
	 * @see org.jobcenter.service.SendClientsCheckinLateNotificationService#sendClientsCheckinLateNotification()
	 */
	@Override
	public  void sendClientsCheckinLateNotification(  )
	{


		MailConfig mailConfig = getMailConfig();


		if ( mailConfig != null ) {

			List<NodeClientStatusDTO> clients = getClientsStatusListService.retrieveClientsLateForCheckinList();

			if ( clients != null && ! clients.isEmpty() ) {

				boolean anyClientsNeedToBeMailed = false;

				for ( NodeClientStatusDTO client: clients ) {

					if ( ! client.getNotificationSentThatClientLate() ) {

						anyClientsNeedToBeMailed = true;
						break;
					}
				}


				if ( anyClientsNeedToBeMailed ) {

					sendMail( clients, mailConfig );

					//  update email sent indicator and write to db
					for ( NodeClientStatusDTO client: clients ) {

						if ( ! client.getNotificationSentThatClientLate() ) {

							client.setNotificationSentThatClientLate( true );

							nodeClientStatusDAO.saveOrUpdate( client );

						}
					}

				} else {

					log.debug( "Already sent email for all clients that are late." );
				}
			} else {

				log.debug( "No late clients." );
			}
		}
	}



	/**
	 * @return
	 */
	MailConfig getMailConfig() {


		String fromEmailAddress = getValueFromConfigService.getConfigValueAsString( ServerConfigKeyValues.CLIENT_CHECKIN_NOTIFICATION_FROM_EMAIL_ADDRESS );

		if ( fromEmailAddress == null || fromEmailAddress.isEmpty() ) {

			String msg = "Missing configuration for key '" + ServerConfigKeyValues.CLIENT_CHECKIN_NOTIFICATION_FROM_EMAIL_ADDRESS + "' so unable to send emails.";

			log.warn( msg );

			return null;
		}

		if ( ServerConfigKeyValues.CLIENT_CHECKIN_EMAIL_IN_PROVIDED_FILE.equals( fromEmailAddress ) ) {

			if ( ! loggedUnusableFromEmailAddressMsg ) {

				String msg = "Unusable value for key '" + ServerConfigKeyValues.CLIENT_CHECKIN_NOTIFICATION_FROM_EMAIL_ADDRESS + "' so unable to send emails.";

				log.warn( msg );

				loggedUnusableFromEmailAddressMsg = true;
			}

			return null;
		}

		String smtpEmailHost = getValueFromConfigService.getConfigValueAsString( ServerConfigKeyValues. CLIENT_CHECKIN_NOTIFICATION_SMTP_EMAIL_HOST );

		if ( smtpEmailHost == null || smtpEmailHost.isEmpty() ) {

			String msg = "Missing configuration for key '" + ServerConfigKeyValues.CLIENT_CHECKIN_NOTIFICATION_SMTP_EMAIL_HOST + "' so unable to send emails.";

			log.warn( msg );

			return null;
		}

		String[] toAddresses = getToAddressList();

		if ( toAddresses == null || toAddresses.length == 0 ) {

			String msg = "Missing configuration for key '" + ServerConfigKeyValues.CLIENT_CHECKIN_NOTIFICATION_TO_EMAIL_ADDRESS_LIST + "' so unable to send emails.";

			log.warn( msg );

			return null;
		}


		if ( ServerConfigKeyValues.CLIENT_CHECKIN_EMAIL_IN_PROVIDED_FILE.equals( toAddresses[0] ) ) {

			if ( ! loggedUnusableToEmailAddressMsg ) {

				String msg = "Unusable value for key '" + ServerConfigKeyValues.CLIENT_CHECKIN_EMAIL_IN_PROVIDED_FILE + "' so unable to send emails.";

				log.warn( msg );

				loggedUnusableToEmailAddressMsg = true;
			}

			return null;
		}



		MailConfig mailConfig = new MailConfig();

		mailConfig.setFromEmailAddress( fromEmailAddress );
		mailConfig.setToAddresses( toAddresses );
		mailConfig.setSmtpEmailHost( smtpEmailHost );

		return mailConfig;
	}

	/**
	 * @return
	 */
	private String[] getToAddressList() {

		String[] toAddresses = null;

		String toAddressesString = getValueFromConfigService.getConfigValueAsString( ServerConfigKeyValues.CLIENT_CHECKIN_NOTIFICATION_TO_EMAIL_ADDRESS_LIST );

		if ( toAddressesString == null || toAddressesString.isEmpty() ) {

			String msg = "Missing configuration for key " + ServerConfigKeyValues.CLIENT_CHECKIN_NOTIFICATION_TO_EMAIL_ADDRESS_LIST;

			log.error( msg );

			return null;
		}

		toAddresses = toAddressesString.split( "," );


		return toAddresses;
	}

	/**
	 * @param clients
	 * @param mailConfig
	 */
	private void sendMail( List<NodeClientStatusDTO> clients, MailConfig mailConfig )
	{

		// send email
		try {

			// set the SMTP host property value
			Properties properties = System.getProperties();

			properties.put("mail.smtp.host", mailConfig.getSmtpEmailHost());


			// create a JavaMail session
			javax.mail.Session mSession = javax.mail.Session.getInstance(properties, null);

			// create a new MIME message
			MimeMessage message = new MimeMessage(mSession);

			// set the from address
			Address fromAddress = new InternetAddress( mailConfig.getFromEmailAddress() );

			message.setFrom(fromAddress);

			// set the subject
			message.setSubject("JobCenter clients are late for checkin");

			// set the message body

			StringBuilder text = new StringBuilder(1000);

			text.append( "The JobCenter clients listed below are late for checkin and may have problems.\n\n" );




			for ( NodeClientStatusDTO client : clients ) {


				if ( ! client.getNotificationSentThatClientLate() ) {

					text.append(  "client: " );
					text.append(  client.getNode().getName() );
					text.append( "\n" );
					text.append( "\n" );

					if ( client.getRunningJobs() != null && ! client.getRunningJobs().isEmpty() ) {

						text.append(  "The following jobs are in \"Running\" status on this node: " );
						text.append( "\n" );

						for ( Job job : client.getRunningJobs() ) {

							text.append(  "Job Id: " );
							text.append(  job.getId() );
							text.append( "\n" );

							text.append(  "Job Type Name: " );
							text.append(  job.getJobType().getName() );
							text.append( "\n" );

						}
					}

				}

			}


			String textStr = text.toString();

			message.setText(textStr);


			//  send to configured list of recipients in config table of the database

			for ( String toEmailAddress : mailConfig.getToAddresses() ) {

				if ( toEmailAddress != null && ! toEmailAddress.isEmpty() ) {

					// set the "To" address
					Address[]  toAddress = InternetAddress.parse( toEmailAddress );
					message.setRecipients(Message.RecipientType.TO, toAddress);


					// send the message
					Transport.send(message);
				}
			}




		} catch (Throwable e) {

			log.error("sendMail Exception: " + e.toString(), e);

			throw new RuntimeException( e );
		}


	}





	private class MailConfig {

		String[] toAddresses;
		String fromEmailAddress;
		String smtpEmailHost;


		public String[] getToAddresses() {
			return toAddresses;
		}
		public void setToAddresses(String[] toAddresses) {
			this.toAddresses = toAddresses;
		}
		public String getFromEmailAddress() {
			return fromEmailAddress;
		}
		public void setFromEmailAddress(String fromEmailAddress) {
			this.fromEmailAddress = fromEmailAddress;
		}
		public String getSmtpEmailHost() {
			return smtpEmailHost;
		}
		public void setSmtpEmailHost(String smtpEmailHost) {
			this.smtpEmailHost = smtpEmailHost;
		}



	}



}
