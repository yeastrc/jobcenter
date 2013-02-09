package org.jobcenter.blast.main;

import java.io.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import org.apache.log4j.Logger;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;


public class BlastUtil {

	// The logger
	private static Logger log = Logger.getLogger(BlastUtil.class);


	public void submitBlast(String query, String task, String database, String outfmt, String alignments, String descriptions, String email, String filename, ConfigProperties p) 
	throws Throwable 
	{

		String blast_dir = p.blast_dir; // Path to blast binaries
		String tmp_dir = p.tmp_dir; // Path to output directory
		String db_dir = p.db_dir; // Path to database directory
		String smtp_host = p.smtp_host;
		String ip_host = p.ip_host;

		String time = null; // Time for unique filenames
		String query_file = null; // Temporary .fa file
		String outfile = ""; // Final name of file
		String filetype = null; // xml/csv/tab/txt
		String s = null; //standard out buffer string
		String result = ""; //results string
		String er = null; //standard error buffer string
		String errResult = ""; //error string

		FileWriter fstream = null;
		BufferedWriter out = null;
		InputStream stdErrorStream  = null;
		InputStream stdInputStream  = null;
		BufferedReader stdInput = null; 
		BufferedReader stdError = null; 

		Process p1 = null; // System process

		//------------------------------------------------------------------------------------------------------------------------------------------

		// get the timestamp and use it as the query file filename
		java.util.Date date = new java.util.Date();
		time = new Timestamp(date.getTime()).toString().replaceAll("\\p{Punct}+", "").replaceAll("\\s+", "");
		query_file = time + ".fa";

		// assign the correct file extension for the outfile
		if (Integer.parseInt(outfmt) == 5){filetype = ".xml";}
		if (Integer.parseInt(outfmt) == 6){filetype = ".tab";}
		if (Integer.parseInt(outfmt) == 8){filetype = ".txt";}
		if (Integer.parseInt(outfmt) == 10){filetype = ".csv";}

		//------------------------------------------------------------------------------------------------------------------------------------------

		//***** write the query to a file so that blast can use it.
		try {

			// Write the query string to a fasta file
			File f = new File(tmp_dir);
			boolean exists = f.exists();
			if (exists) {

				fstream = new FileWriter(tmp_dir + "/" + query_file);
				out = new BufferedWriter(fstream);
				out.write(query);
				log.debug("Writing query to " + tmp_dir + "/" + query_file);

			} else {
				// Do not continue because the file could not be written.
				log.error("Directory does not exist: " + tmp_dir) ;
			}

		} catch (Throwable t) {

			// Do not continue because the file could not be written.
			String msg = "Could not write query string to file. Check: " + tmp_dir + " exists and has write access.";
			log.error(msg, t);
			throw new Exception(msg, t);

		} finally {
			try {
				// Make sure to close the file.
				if (out != null) {
					out.close();
				}	
			}
			catch (Throwable t) {
				// Continue, but warn that closing the file failed.
				log.error("Error closing the " + query_file + " at out.close()");
			}
		}


		//------------------------------------------------------------------------------------------------------------------------------------------

		// Build the query 
		String blast = blast_dir + "/" + task + " -query " + tmp_dir + "/" + query_file + " -task " + task + " -db "
		+ db_dir + "/" + database + " -outfmt " + outfmt + " -num_alignments " + alignments + " -num_descriptions " + descriptions;
		log.debug( "Seguence: " + blast);

		//------------------------------------------------------------------------------------------------------------------------------------------

		try {

			// Execute the blast query
			p1 = Runtime.getRuntime().exec(blast);

			// try to read the standard input.
			try {

				stdInputStream = p1.getInputStream();
				stdInput = new BufferedReader(new InputStreamReader( stdInputStream ));

			} catch (Throwable t) {

				// If we cannot read the standard input, DO NOT CONTINUE
				String msg = "Could not read input stream: p.getInputStream().";
				log.error(msg, t);

				if (stdInputStream != null) {
					try {

						stdInputStream.close();

					} catch (Throwable t2) {
						// Could not close standard input stream
						log.error("Could not close standard input stream. from: p.getInputStream()", t2);

					}
				}

				throw new Exception(msg, t);

			}

			//------------------------------------------------------------------------------------------------------------------------------------------


			// try to read the standard error
			try {

				stdErrorStream = p1.getErrorStream();
				stdError = new BufferedReader(new InputStreamReader( stdErrorStream ));

			} catch (Throwable t) {

				// continue even though we cannot read the standard error of the command.
				log.error("Could not read standard error stream. getErrorStream() ", t);

				if (stdErrorStream != null) {
					try {

						stdErrorStream.close();

					} catch (Throwable t2) {
						//
						log.error("Could not close standard error stream. from: p.getErrorStream()", t2);

					}
				}
			}

			//------------------------------------------------------------------------------------------------------------------------------------------
			try {

				// read the input from the command
				while ((s = stdInput.readLine()) != null) {
					result+= s + "\n";
				}

			} catch (Throwable t) {

				String msg = "Reading standard input failed.";
				log.error(msg, t);
				throw new Exception(msg, t);
			}

			// write the standard input to the log
			log.debug("Here is the standard output of the command:");
			log.debug(result);

			try {

				// read any errors from the attempted command
				while ((er = stdError.readLine()) != null) {
					errResult+= er;
				}

			} catch (Throwable t) {

				String msg = "Reading standard error failed.";
				log.error(msg, t);
				throw new Exception(msg, t);

			}

			// write the standard input to the log
			log.debug("Here is the standard error of the command:");
			log.debug(errResult);

			//------------------------------------------------------------------------------------------------------------------------------------------

			// if standard input is not null, write it to a file using 'filename' if provided
			if (result != null && !result.isEmpty()) {
				try {

					// Write the result string to a fasta file using the correct filename
					if (filename.isEmpty()) {

						outfile = time + filetype;

					} else {

						outfile = filename + time + filetype;

					}

					// Write the file
					fstream = new FileWriter(tmp_dir + "/" + outfile);
					out = new BufferedWriter(fstream);
					out.write(result);
					// log the full path to the file
					log.debug("Writing result to " + tmp_dir + "/" + outfile);

				} catch (Throwable t) {

					// if writing the file failed, do not continue.
					String msg = "Could not write result to file. Stopping job.";
					log.error(msg, t);
					throw new Exception(msg, t);

				} finally {

					try {

						// Make sure to close the file
						if (out != null) {
							out.close();
						}

					}
					catch (Throwable t) {

						// log that the file was not closed, but continue
						log.error("Error closing the " + query_file + " at out.close()");
						return;

					}
				}
			}


			//------------------------------------------------------------------------------------------------------------------------------------------

			try {

				// cleanup the files that are not needed.
				deletefile(tmp_dir + "/" + query_file);

			} catch (Throwable t) {

				String msg = "Could not delete temporary file: " + query_file + " from: " + tmp_dir;
				log.error(msg);
				throw new Exception(msg, t);

			}

			if (!email.isEmpty()) {

				try {

					// if an email was provided, send the results.
					sendAttachment("Blast results", email, outfile, tmp_dir, smtp_host, ip_host);

				} catch (Throwable t) {

					log.error("Failed to send email. ", t);
					throw new Exception("Email failed to send.", t);

				}

			}

			// cleanup outfile if no filename was specified.
			if (filename.isEmpty() || !email.isEmpty()) {

				try {

					deletefile(tmp_dir + "/" + outfile);

				} catch (Throwable t) {

					String msg = "Could not delete temporary file: " + outfile + " from: " + tmp_dir;
					throw new Exception(msg, t);

				}

			}

			//------------------------------------------------------------------------------------------------------------------------------------------

			if (errResult != null && !errResult.isEmpty() ){

				// log the standard error if it was captured
				log.debug("Here is the standard error of the command (if any):\n" + errResult);
				
				// Send an email with the standard error
				sendAttachment("Error Performing Blast", email, errResult, tmp_dir, smtp_host, ip_host);

			}

			//------------------------------------------------------------------------------------------------------------------------------------------

		} catch (Throwable t) {

			// when all else fails, log the failure
			String msg = "There was a problem performing your blast.";
			log.error(msg, t);
			throw new Exception(msg, t);

		} finally {

			// close it up.
			try {
				if (stdInput != null) {stdInput.close();}
			}
			catch (Throwable t) {
				log.error("Could not close stdInput ", t);
			}
			try {
				if (stdError != null) {stdError.close();}

			} catch (Throwable t) {
				log.error("could not close stdError ", t);
			}

		}
	}

	// Method so that I dont have to write this several times.
	private void deletefile(String file) {

		try {
			File f1 = new File(file);
			boolean success = f1.delete();

			if (!success) {

				log.error("Could not remove unused files.");

			}else {

				log.debug("Unused file: " + file + " was removed.");

			}
		} catch (Throwable t) {
			// Only need to log the failure. it is not grounds for failing the the job.
			log.info("Could not delete the file: " + file, t);
		}
	}

	public static void sendAttachment( String subject, String email, String file, String tmpdir, String smtp_host, String ip_host )
	throws Throwable {

		// set the SMTP host property value
		Properties properties = System.getProperties();

		properties.put(smtp_host, ip_host);

		// create a JavaMail session
		// old // javax.mail.Session mSession = javax.mail.Session.getInstance(properties, null);
		Session session = Session.getInstance(properties, null);

		// send email
		try {

			// create a new MIME message
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("wilsonmc@uw.edu"));
			message.setRecipients(Message.RecipientType.TO, email);
			message.setSubject(subject);

			// create and fill the first message part
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText("See attachment.");

			// create the second message part
			MimeBodyPart mbp2 = new MimeBodyPart();

			// attach the file to the message
			FileDataSource fds = new FileDataSource(tmpdir + "/" + file);
			mbp2.setDataHandler(new DataHandler(fds));
			mbp2.setFileName(fds.getName());

			// create the Multipart and add its parts to it
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);
			mp.addBodyPart(mbp2);

			// add the Multipart to the message
			message.setContent(mp);

			// set the Date: header
			message.setSentDate(new Date());

			// send the message
			Transport.send(message);

		} catch (Throwable t) {
			log.error("sendEmail Exception: " + t);
			throw t;
		}

	}
	
	public static void sendErrorMsg( String subject, String email, String body, String tmpdir, String smtp_host, String ip_host )
	throws Throwable {

		// set the SMTP host property value
		Properties properties = System.getProperties();

		properties.put(smtp_host, ip_host);

		// create a JavaMail session
		// old // javax.mail.Session mSession = javax.mail.Session.getInstance(properties, null);
		Session session = Session.getInstance(properties, null);

		// send email
		try {

			// create a new MIME message
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("wilsonmc@uw.edu"));
			message.setRecipients(Message.RecipientType.TO, email);
			message.setSubject(subject);

			// create and fill the first message part
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(body);

			// create the second message part
			MimeBodyPart mbp2 = new MimeBodyPart();

			// attach the file to the message
			//FileDataSource fds = new FileDataSource(tmpdir + "/" + file);
			//mbp2.setDataHandler(new DataHandler(fds));
			//mbp2.setFileName(fds.getName());

			// create the Multipart and add its parts to it
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);
			mp.addBodyPart(mbp2);

			// add the Multipart to the message
			message.setContent(mp);

			// set the Date: header
			message.setSentDate(new Date());

			// send the message
			Transport.send(message);

		} catch (Throwable t) {
			log.error("sendEmail Exception: " + t);
			throw t;
		}

	}

}
