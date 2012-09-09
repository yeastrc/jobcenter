package org.jobcenter.internalservice;

public interface SendEmailService {

	public abstract void sendEmail(String smtpEmailHost,
			String fromEmailAddress, String toEmailAddress,
			String emailSubject, String emailBody) throws Throwable;

	public void sendEmailHTML( String smtpEmailHost, String fromEmailAddress, String toEmailAddress, String emailSubject, String emailBody  ) throws Throwable;

}