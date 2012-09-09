
package org.jobcenter.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 *
 *
 */
public class ListRequestsForm extends ActionForm {


	private static Logger log = Logger.getLogger(ListRequestsForm.class);

	private static final int MAX_STATUS_VALUES = 20;

	private static final long serialVersionUID = -5158944202444943221L;

	private String jobGroup;

	private String jobType;
	private String requestType;
	private String status;
	
	private boolean[] statuses = new boolean[ MAX_STATUS_VALUES ];
	
	private String indexStart;
	private String jobsReturnCountMax;
	
	private String requestId;

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


		jobGroup = null;
		jobType = null;
		requestType = null;
		status = null;
		
		statuses = new boolean[ MAX_STATUS_VALUES ];

		indexStart = null;
		jobsReturnCountMax = null;
		
		requestId = null;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getIndexStart() {
		return indexStart;
	}

	public void setIndexStart(String indexStart) {
		this.indexStart = indexStart;
	}

	public String getJobsReturnCountMax() {
		return jobsReturnCountMax;
	}

	public void setJobsReturnCountMax(String jobsReturnCountMax) {
		this.jobsReturnCountMax = jobsReturnCountMax;
	}

	public boolean[] getStatuses() {
		return statuses;
	}

	public void setStatuses(boolean[] statuses) {
		this.statuses = statuses;
	}



}