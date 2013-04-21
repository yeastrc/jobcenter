package org.webblast.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.EmailValidator;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public class WebBlastForm extends ActionForm {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String query;
	private String task = "blastp";
	private String database;
	private String outfmt = "5";
	private String email;
	private String alignments = "250";
	private String descriptions = "500";
	private String filename;
	
	
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if ( query.isEmpty() ) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "Query"));
		} 
		
		if ( !task.equals("blastn") && !task.equals("blastp")) {
			errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "Blast Type"));
		}
		
		if ( outfmt == null || outfmt.isEmpty()){
			errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "Output Format"));
		}
		else {
			try {
			Integer.parseInt(outfmt);
			} catch (NumberFormatException n) {
				errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.integer", "Output Format"));
			}
		}
		
		if ( email.isEmpty() && filename.isEmpty()) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.atleastone", "Job Title or Email" ));
		} 
		
		if ( !email.isEmpty() ) {
			EmailValidator validator = EmailValidator.getInstance();

			if ( !validator.isValid(email) ) {

				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.email", email ));
			}
		}

		if ( alignments == null || alignments.isEmpty()){
			errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "Number of Alignments"));
		}
		else {
			try {
			Integer.parseInt(alignments);
			} catch (NumberFormatException n) {
				errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.integer", alignments));
			}
		}

		if ( descriptions == null || descriptions.isEmpty()){
			errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "Number of Descriptions"));
		}
		else {
			try {
			Integer.parseInt(descriptions);
			} catch (NumberFormatException n) {
				errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.integer", descriptions));
			}
		}
		
		return errors;
	}

	
	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}
	
	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}
	
	/**
	 * @param task the task to set
	 */
	public void setTask(String task) {
		this.task = task;
	}
	
	/**
	 * @return the task
	 */
	public String getTask() {
		return task;
	}
	
	/**
	 * @param outfmt the outfmt to set
	 */
	public void setOutfmt(String outfmt) {
		this.outfmt = outfmt;
	}
	
	/**
	 * @return the outfmt
	 */
	public String getOutfmt() {
		return outfmt;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param alignments the alignments to set
	 */
	public void setAlignments(String alignments) {
		this.alignments = alignments;
	}

	/**
	 * @return the alignments
	 */
	public String getAlignments() {
		return alignments;
	}

	/**
	 * @param descriptions the descriptions to set
	 */
	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}

	/**
	 * @return the descriptions
	 */
	public String getDescriptions() {
		return descriptions;
	}

	/**
	 * @param database the database to set
	 */
	public void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}
	
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	
}
