package org.jobcenter.internalservice;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jobcenter.dto.JobType;
import org.jobcenter.dtoservernondb.MailConfig;
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
 * Send an email on Job Run Fail.  Only One email per 24 hours max
 * 
 * Singleton object
*/

//Spring database transaction demarcation.
//Spring will start a database transaction when any method is called and call commit when it completed
//  or call roll back if an unchecked exception is thrown.
@Transactional ( propagation = Propagation.REQUIRED, readOnly = false )

public class SendEmailOnJobRunFail_MaxOncePer24hours_ServiceImpl implements SendEmailOnJobRunFail_MaxOncePer24hours_Service {

	private static Logger log = Logger.getLogger(SendEmailOnJobRunFail_MaxOncePer24hours_ServiceImpl.class);

	private static final String EMAIL_SUBJECT_LINE = "JobCenter job exitted in hard error status";

	private static final long TIME_24_HOURS_IN_MILLISECONDS = 24 * 60 * 60 * 1000;

	//  Service

	private GetMailConfig getMailConfig;
	
	private SendEmailService sendEmailService;

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
	
	//  Internal variables
	
	private AtomicLong lastEmailSent = new AtomicLong( 0 );


	/**
	 * 
	 */
	@Override
	public  void sendEmailOnJobRunFailed( JobType jobTypeOfFailedJob ) {
		
		MailConfig mailConfig = getMailConfig.getClientCheckinMailConfig();
		if ( mailConfig == null ) {
			// Nothing configured for email so exit
			return;  // EARLY EXIT
		}
		
		long now = System.currentTimeMillis();
		
		long lastEmailSent_Local = lastEmailSent.longValue();
		
		if ( now > ( lastEmailSent_Local + TIME_24_HOURS_IN_MILLISECONDS ) ) {
			
			if ( lastEmailSent.compareAndSet( lastEmailSent_Local, now ) ) {
				
				sendMail( jobTypeOfFailedJob, mailConfig );
			}
		}
	}

	/**
	 * @param clients
	 * @param mailConfig
	 */
	private void sendMail( JobType jobTypeOfFailedJob, MailConfig mailConfig )
	{

		// send email
		try {

			// create the message body

			StringBuilder text = new StringBuilder(1000);

			text.append( "A JobCenter job failed with a hard error.\n\n" );
			
			text.append( "There will be no further emails about jobs that fail with a hard error (For any job type) for the next 24 hours.\n\n" );
			
			text.append( "The job type that failed is: " );
			
			text.append( jobTypeOfFailedJob.getName() );


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
