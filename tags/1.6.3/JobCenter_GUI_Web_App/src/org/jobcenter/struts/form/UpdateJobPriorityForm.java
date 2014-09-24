
package org.jobcenter.struts.form;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 *
 *
 */
public class UpdateJobPriorityForm extends ActionForm {

	private static final long serialVersionUID = 2479537139410197078L;



	private int jobId;
	
	private String newPriority;

	private int jobRecordVersionId;



	/**
	 * Method validate
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 */
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {

		return null;
	}

	/**
	 * Method reset
	 * @param mapping
	 * @param request
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {


	}




	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public int getJobRecordVersionId() {
		return jobRecordVersionId;
	}

	public void setJobRecordVersionId(int jobRecordVersionId) {
		this.jobRecordVersionId = jobRecordVersionId;
	}

	public String getNewPriority() {
		return newPriority;
	}

	public void setNewPriority(String newPriority) {
		this.newPriority = newPriority;
	}

}