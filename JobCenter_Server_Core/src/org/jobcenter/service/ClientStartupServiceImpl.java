package org.jobcenter.service;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableLong;
import org.apache.log4j.Logger;

import org.jobcenter.constants.ClientStatusUpdateTypeEnum;
import org.jobcenter.constants.ServerConfigKeyValues;
import org.jobcenter.constants.ServerCoreConstants;
import org.jobcenter.dto.NodeClientStatusDTO;
import org.jobcenter.dtoservernondb.MailConfig;
import org.jobcenter.dtoservernondb.NodeClientStatusDTOPrevCurrent;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.internalservice.ClientStatusDBUpdateService;
import org.jobcenter.internalservice.ClientsConnectedTrackingService;
import org.jobcenter.internalservice.GetMailConfig;
import org.jobcenter.internalservice.GetValueFromConfigService;
import org.jobcenter.internalservice.SendEmailService;
import org.jobcenter.nondbdto.ClientConnectedDTO;
import org.jobcenter.nondbdto.ClientIdentifierDTO;

import org.jobcenter.request.*;
import org.jobcenter.response.*;



/**
*
*
*/

//  Transactions handled at lower levels
public class ClientStartupServiceImpl implements ClientStartupService {


	private static Logger log = Logger.getLogger(ClientStartupServiceImpl.class);


	private static final String EMAIL_SUBJECT_LINE_CLIENT_NOT_SHUTDOWN = "JobCenter client started up but was not previously shut down";

	private static final String EMAIL_SUBJECT_LINE_CLIENT_NORMAL_STARTUP = "JobCenter client started up normally";

	//  one copy for all instances
	private static MutableLong prevTime = new MutableLong();




	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;
	private GetValueFromConfigService getValueFromConfigService;

	private ClientStatusDBUpdateService clientStatusDBUpdateService;

	private ClientsConnectedTrackingService clientsConnectedTrackingService;

	private GetMailConfig getMailConfig;
	private SendEmailService sendEmailService;


	public ClientsConnectedTrackingService getClientsConnectedTrackingService() {
		return clientsConnectedTrackingService;
	}
	public void setClientsConnectedTrackingService(
			ClientsConnectedTrackingService clientsConnectedTrackingService) {
		this.clientsConnectedTrackingService = clientsConnectedTrackingService;
	}
	public GetValueFromConfigService getGetValueFromConfigService() {
		return getValueFromConfigService;
	}
	public void setGetValueFromConfigService(
			GetValueFromConfigService getValueFromConfigService) {
		this.getValueFromConfigService = getValueFromConfigService;
	}
	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}
	public ClientStatusDBUpdateService getClientStatusDBUpdateService() {
		return clientStatusDBUpdateService;
	}
	public void setClientStatusDBUpdateService(
			ClientStatusDBUpdateService clientStatusDBUpdateService) {
		this.clientStatusDBUpdateService = clientStatusDBUpdateService;
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



	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 */
	@Override
	public ClientStartupResponse clientStartup( ClientStartupRequest clientStartupRequest, String remoteHost )
	{
		final String method = "clientStartup";


		if ( clientStartupRequest == null ) {

			log.error( method + "  IllegalArgument:clientStartupRequest == null");

			throw new IllegalArgumentException( "clientStartupRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( method + " clientStartupRequest.getNodeName() = |" + clientStartupRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		ClientStartupResponse clientStartupResponse = new ClientStartupResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( clientStartupResponse, clientStartupRequest.getNodeName(), remoteHost ) ) {

			return clientStartupResponse;
		}

		long currentTime = System.currentTimeMillis();

		clientStartupResponse.setCurrentServerTime( currentTime );


		synchronized (prevTime) {

			//  prevent assigning the same value to two different clients

			if ( prevTime.longValue() == currentTime ) {

				currentTime++;
			}

			prevTime.setValue( currentTime );
		}







		ClientIdentifierDTO clientIdentifierDTO = new ClientIdentifierDTO();

		//  assign client identifier to current time ( milliseconds )

		clientIdentifierDTO.setClientIdentifier( currentTime );

		clientStartupResponse.setClientIdentifierDTO( clientIdentifierDTO );


		//  TODO  Also need to store the info for the client with this id


		ClientConnectedDTO clientConnectedDTO = new ClientConnectedDTO();

		clientConnectedDTO.setNodeName( clientStartupRequest.getNodeName() );
		clientConnectedDTO.setRemoteIPAddress( remoteHost );

		clientConnectedDTO.setClientIdentifierDTO( clientIdentifierDTO );

		long now = System.currentTimeMillis();

		clientConnectedDTO.setStartTime( now );
		clientConnectedDTO.setLastStatusUpdatedTime( now );


		int waitTimeForNextClientCheckin = ServerCoreConstants.DEFAULT_CLIENT_CHECKIN_TIME_IN_SECONDS;

		Integer waitTimeForNextClientCheckinFromConfig = getValueFromConfigService.getConfigValueAsInteger( ServerConfigKeyValues.CLIENT_CHECKIN_WAIT_TIME );

		if ( waitTimeForNextClientCheckinFromConfig != null ) {

			waitTimeForNextClientCheckin = waitTimeForNextClientCheckinFromConfig;
		}

		long nextExpectedStatusUpdatedTime = now + ( waitTimeForNextClientCheckin * 1000 );

		clientConnectedDTO.setNextExpectedStatusUpdatedTime(nextExpectedStatusUpdatedTime);

		clientConnectedDTO.setClientStatus( ClientStatusUpdateTypeEnum.CLIENT_UP );

		clientsConnectedTrackingService.addClient( clientConnectedDTO );

		NodeClientStatusDTOPrevCurrent nodeClientStatusDTOPrevCurrent = clientStatusDBUpdateService.updateDBWithStatusFromClientStartup( clientStartupRequest, waitTimeForNextClientCheckin, remoteHost );

		NodeClientStatusDTO nodeClientStatusDTOPrev = nodeClientStatusDTOPrevCurrent.getPrevNodeClientStatusDTO();

		if ( nodeClientStatusDTOPrev != null ) {

			if ( nodeClientStatusDTOPrev.getClientStarted() ) {

				Boolean sendNotShutdownNotification
					= getValueFromConfigService.getConfigValueAsBoolean( ServerConfigKeyValues.CLIENT_STARTUP_CLIENT_NOT_PREV_SHUTDOWN_NOTIFICATION );

				if ( sendNotShutdownNotification != null && sendNotShutdownNotification ) {

					MailConfig mailConfig = getMailConfig.getClientCheckinMailConfig();

					if ( mailConfig != null ) {

						// create the message body

						StringBuilder emailBodySB = new StringBuilder(1000);

						emailBodySB.append( "The JobCenter client '" );

						emailBodySB.append( clientStartupRequest.getNodeName() );

						emailBodySB.append( "' has started up but was previously not shut down (The server did not receive the shutdown message).\n\n" );

						String emailBody = emailBodySB.toString();

						//  send to configured list of recipients in config table of the database

						for ( String toEmailAddress : mailConfig.getToAddresses() ) {

							if ( ! StringUtils.isEmpty( toEmailAddress ) ) {

								try {
									sendEmailService.sendEmail( mailConfig.getSmtpEmailHost(),
											mailConfig.getFromEmailAddress(), toEmailAddress, EMAIL_SUBJECT_LINE_CLIENT_NOT_SHUTDOWN, emailBody );
								} catch (Throwable e) {

									String msg = "Exception sending email for client startup when client not previously shut down";

									log.error( msg, e );
									
									//  Don't rethrow exception.  Still want the client to come up.

//									throw new RuntimeException( msg, e );
								}
							}
						}


					} else {

						log.error( "Notification config key '" + ServerConfigKeyValues.CLIENT_STARTUP_CLIENT_NOT_PREV_SHUTDOWN_NOTIFICATION
								+ "' = true/yes/1 but the mail config is incomplete or empty " );
					}

				}
			} else {


				Boolean sendNormalStartupNotification
					= getValueFromConfigService.getConfigValueAsBoolean( ServerConfigKeyValues.CLIENT_NORMAL_STARTUP_NOTIFICATION );

				if ( sendNormalStartupNotification != null && sendNormalStartupNotification ) {

					MailConfig mailConfig = getMailConfig.getClientCheckinMailConfig();

					if ( mailConfig != null ) {


						// create the message body

						StringBuilder emailBodySB = new StringBuilder(1000);

						emailBodySB.append( "The JobCenter client '" );

						emailBodySB.append( clientStartupRequest.getNodeName() );

						emailBodySB.append( "' has started up normally.\n\n" );

						String emailBody = emailBodySB.toString();


						//  send to configured list of recipients in config table of the database

						for ( String toEmailAddress : mailConfig.getToAddresses() ) {

							if ( ! StringUtils.isEmpty( toEmailAddress ) ) {

								try {
									sendEmailService.sendEmail( mailConfig.getSmtpEmailHost(),
											mailConfig.getFromEmailAddress(), toEmailAddress, EMAIL_SUBJECT_LINE_CLIENT_NORMAL_STARTUP, emailBody );
								} catch (Throwable e) {

									String msg = "Exception sending email for client normal startup";

									log.error( msg, e );

									throw new RuntimeException( msg, e );
								}
							}
						}


					} else {

						log.error( "Notification config key '" + ServerConfigKeyValues.CLIENT_NORMAL_STARTUP_NOTIFICATION
								+ "' = true/yes/1 but the mail config is incomplete or empty " );
					}

				}
			}
		}




		log.info( method + " called, clientConnectedDTO added = " + clientConnectedDTO );


		clientStartupResponse.setWaitTimeForNextClientCheckin( waitTimeForNextClientCheckin );


		return clientStartupResponse;
	}





}
