package org.jobcenter.internalservice;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

/**
 * Send an email
 *
 */
public class SendEmailServiceImpl implements SendEmailService {

	private static Logger log = Logger.getLogger(SendEmailServiceImpl.class);

	
	private static final String TEXT_CONTENT_MIME_TYPE = "text/plain";
	
	private static final String HTML_CONTENT_MIME_TYPE = "text/html";


	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.SendEmailService#sendEmail(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void sendEmail( String smtpEmailHost, String fromEmailAddress, String toEmailAddress, String emailSubject, String emailBody  ) throws Throwable {

		sendEmailInternal( smtpEmailHost, fromEmailAddress, toEmailAddress, emailSubject, emailBody, TEXT_CONTENT_MIME_TYPE );
	}
	
	


	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.SendEmailService#sendEmailHTML(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void sendEmailHTML(String smtpEmailHost, String fromEmailAddress, String toEmailAddress, String emailSubject, String emailBody) throws Throwable {

		sendEmailInternal( smtpEmailHost, fromEmailAddress, toEmailAddress, emailSubject, emailBody, HTML_CONTENT_MIME_TYPE );
		
	}
	
	
	/**
	 * @param smtpEmailHost
	 * @param fromEmailAddress
	 * @param toEmailAddress
	 * @param emailSubject
	 * @param emailBody
	 * @param contentType
	 * @throws Throwable
	 */
	private void sendEmailInternal( String smtpEmailHost, String fromEmailAddress, String toEmailAddress, String emailSubject, String emailBody, String contentType  ) throws Throwable {

		if ( log.isDebugEnabled() ) {
			
			log.debug( "sendEmail: contentType = " + contentType );
			log.debug( "sendEmail: smtpEmailHost = " + smtpEmailHost );
			log.debug( "sendEmail: fromEmailAddress = " + fromEmailAddress );
			log.debug( "sendEmail: toEmailAddress = " + toEmailAddress );
			log.debug( "sendEmail: emailSubject = " + emailSubject );
			log.debug( "sendEmail: emailBody = " + emailBody );
			
		}
		
		
		try {

			// set the SMTP host property value
			Properties properties = System.getProperties();


			properties.put("mail.smtp.host", smtpEmailHost );


			// create a JavaMail session
			javax.mail.Session mSession = javax.mail.Session.getInstance(properties, null);

			// create a new MIME message
			MimeMessage message = new MimeMessage(mSession);

			// set the from address
			Address fromAddress = new InternetAddress( fromEmailAddress );

			message.setFrom(fromAddress);

			// set the subject
			message.setSubject( emailSubject );

//			message.setText( emailBody );
			
			message.setContent( emailBody, contentType );


			// set the "To" address
			Address[]  toAddress = InternetAddress.parse( toEmailAddress );
			message.setRecipients(Message.RecipientType.TO, toAddress);


			// send the message
			Transport.send(message);

		} catch (Throwable e) {

			log.error("sendMail Exception: " + e.toString(), e);

			throw new RuntimeException( e );
		}
	}






}
