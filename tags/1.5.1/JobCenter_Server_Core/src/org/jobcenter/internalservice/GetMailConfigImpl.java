package org.jobcenter.internalservice;

import org.apache.log4j.Logger;
import org.jobcenter.constants.ServerConfigKeyValues;
import org.jobcenter.dtoservernondb.MailConfig;

/**
 * 
 *
 */
public class GetMailConfigImpl implements GetMailConfig {





	private static Logger log = Logger.getLogger(GetMailConfigImpl.class);

	

	private GetValueFromConfigService getValueFromConfigService;

	public GetValueFromConfigService getGetValueFromConfigService() {
		return getValueFromConfigService;
	}

	public void setGetValueFromConfigService(
			GetValueFromConfigService getValueFromConfigService) {
		this.getValueFromConfigService = getValueFromConfigService;
	}
	
	
	
	private boolean loggedUnusableFromEmailAddressMsg = false;
	private boolean loggedUnusableToEmailAddressMsg = false;

	
	private boolean loggedUnusableStatusFromEmailAddressMsg = false;
	private boolean loggedUnusableStatusToEmailAddressMsg = false;



	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.GetMailConfig#getMailConfig()
	 */
	@Override
	public MailConfig getClientCheckinMailConfig() {


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

		String toAddressesString = getValueFromConfigService.getConfigValueAsString( ServerConfigKeyValues.CLIENT_CHECKIN_NOTIFICATION_TO_EMAIL_ADDRESS_LIST );

		if ( toAddressesString == null || toAddressesString.isEmpty() ) {

			String msg = "Missing configuration for key " + ServerConfigKeyValues.CLIENT_CHECKIN_NOTIFICATION_TO_EMAIL_ADDRESS_LIST;

			log.error( msg );

			return null;
		}


		String[] toAddresses = getToAddressList( toAddressesString );

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
	
	

	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.GetMailConfig#getMailConfig()
	 */
	@Override
	public MailConfig getClientStatusMailConfig() {

		
		Boolean sendClientStatusNotification = getValueFromConfigService.getConfigValueAsBoolean( ServerConfigKeyValues.CLIENT_STATUS_NOTIFICATION );
		
		if ( sendClientStatusNotification == null || ( ! sendClientStatusNotification ) ) {
			
			return null;
		}

		String fromEmailAddress = getValueFromConfigService.getConfigValueAsString( ServerConfigKeyValues.CLIENT_STATUS_NOTIFICATION_FROM_EMAIL_ADDRESS );

		if ( fromEmailAddress == null || fromEmailAddress.isEmpty() ) {

			String msg = "Missing configuration for key '" + ServerConfigKeyValues.CLIENT_STATUS_NOTIFICATION_FROM_EMAIL_ADDRESS + "' so unable to send emails.";

			log.warn( msg );

			return null;
		}

		if ( ServerConfigKeyValues.CLIENT_CHECKIN_EMAIL_IN_PROVIDED_FILE.equals( fromEmailAddress ) ) {

			if ( ! loggedUnusableStatusFromEmailAddressMsg ) {

				String msg = "Unusable value for key '" + ServerConfigKeyValues.CLIENT_STATUS_NOTIFICATION_FROM_EMAIL_ADDRESS + "' so unable to send emails.";

				log.warn( msg );

				loggedUnusableStatusFromEmailAddressMsg = true;
			}

			return null;
		}

		String smtpEmailHost = getValueFromConfigService.getConfigValueAsString( ServerConfigKeyValues. CLIENT_CHECKIN_NOTIFICATION_SMTP_EMAIL_HOST );

		if ( smtpEmailHost == null || smtpEmailHost.isEmpty() ) {

			String msg = "Missing configuration for key '" + ServerConfigKeyValues.CLIENT_CHECKIN_NOTIFICATION_SMTP_EMAIL_HOST + "' so unable to send emails.";

			log.warn( msg );

			return null;
		}

		String toAddressesString = getValueFromConfigService.getConfigValueAsString( ServerConfigKeyValues.CLIENT_STATUS_NOTIFICATION_TO_EMAIL_ADDRESS_LIST );

		if ( toAddressesString == null || toAddressesString.isEmpty() ) {

			String msg = "Missing configuration for key " + ServerConfigKeyValues.CLIENT_STATUS_NOTIFICATION_TO_EMAIL_ADDRESS_LIST;

			log.error( msg );

			return null;
		}

		String[] toAddresses = getToAddressList( toAddressesString );

		if ( toAddresses == null || toAddresses.length == 0 ) {

			String msg = "Missing configuration for key '" + ServerConfigKeyValues.CLIENT_STATUS_NOTIFICATION_TO_EMAIL_ADDRESS_LIST + "' so unable to send emails.";

			log.warn( msg );

			return null;
		}


		if ( ServerConfigKeyValues.CLIENT_CHECKIN_EMAIL_IN_PROVIDED_FILE.equals( toAddresses[0] ) ) {

			if ( ! loggedUnusableStatusToEmailAddressMsg ) {

				String msg = "Unusable value for key '" + ServerConfigKeyValues.CLIENT_STATUS_NOTIFICATION_TO_EMAIL_ADDRESS_LIST + "' so unable to send emails.";

				log.warn( msg );

				loggedUnusableStatusToEmailAddressMsg = true;
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
	private String[] getToAddressList( String toAddressesString ) {

		String[] toAddresses = null;

		toAddresses = toAddressesString.split( "," );


		return toAddresses;
	}

}
