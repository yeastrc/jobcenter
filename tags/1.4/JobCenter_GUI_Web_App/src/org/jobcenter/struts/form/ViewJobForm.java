
package org.jobcenter.struts.form;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


public class ViewJobForm extends ActionForm {
	/*
	 * Generated fields
	 */

	/** jobId property */
	private int jobId;

	/*
	 * Generated Methods
	 */

	/**
	 * Method validate
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 */
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Method reset
	 * @param mapping
	 * @param request
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// TODO Auto-generated method stub
	}

	/**
	 * Returns the jobId.
	 * @return int
	 */
	public int getJobId() {
		return jobId;
	}

	/**
	 * Set the jobId.
	 * @param jobId The jobId to set
	 */
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
}