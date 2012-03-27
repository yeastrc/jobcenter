package org.jobcenter.sample.main;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class EmailSendUtil {

	private static Logger log = Logger.getLogger(EmailSendUtil.class);




	/**
	 * @param experiments
	 * @throws Exception
	 */
	public static void sendTestMail( String msg  )
	throws Exception
	{

		// send email
		try {
			// set the SMTP host property value
			Properties properties = System.getProperties();

			properties.put("mail.smtp.host", "localhost");


			// create a JavaMail session
			javax.mail.Session mSession = javax.mail.Session.getInstance(properties, null);

			// create a new MIME message
			MimeMessage message = new MimeMessage(mSession);

			// set the from address
			Address fromAddress = new InternetAddress( "do_not_reply@ssss.org" );

			message.setFrom(fromAddress);

			// set the to address
			Address[] toAddress = InternetAddress.parse( "do_not_reply@ssss.org" );
			message.setRecipients(Message.RecipientType.TO, toAddress);

			// set the subject
			message.setSubject("Test email from JobManager_Sample_Module." + msg );

			// set the message body

			String textStr = "This is a test email from the JobManager_Sample_Module." + msg ;

			message.setText(textStr);

			// send the message
			Transport.send(message);

		} catch (Exception e) {

			log.error("sendMail Exception: " + e.toString(), e);

			throw e;
		}

	}


	/**
	 * Run the conversion job
	 * @param args
	 * @throws Exception
	 */
	public static void main( String[] args ) throws Exception {

		log.error("Test Error Log from JobManager_Sample_Module::main.");

		sendTestMail( "  Called from main()." ) ;
	}


}
