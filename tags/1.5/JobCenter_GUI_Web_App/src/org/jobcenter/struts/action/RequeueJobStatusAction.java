
package org.jobcenter.struts.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.jobcenter.constants.GUIWebAppConstants;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.dto.Job;
import org.jobcenter.guiclient.GUICallStatus;
import org.jobcenter.guiclient.GUIConnectionToServerClient;
import org.jobcenter.struts.BaseAction;
import org.jobcenter.struts.form.UpdateJobStatusForm;



public class RequeueJobStatusAction extends BaseAction {

	private static Logger log = Logger.getLogger(ListJobsAction.class);


	/**
	 * Method execute
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		UpdateJobStatusForm updateJobStatusForm = (UpdateJobStatusForm) form;

		String forwardTo = "success";


		try {

			int jobId = updateJobStatusForm.getJobId();

			int dbRecordVersionNumber = updateJobStatusForm.getJobRecordVersionId();

			if ( jobId > 0 ) {

				GUIConnectionToServerClient connToServer = null;

				try {

					connToServer = new GUIConnectionToServerClient();

					connToServer.init( GUIWebAppConstants.URL_TO_SERVER );

					//  To not enforce dbRecordVersionNumber, pass null instead

					GUICallStatus status = connToServer.requeueJob( jobId, dbRecordVersionNumber );

					if ( status == GUICallStatus.SUCCESS ) {


					} else if ( status == GUICallStatus.FAILED_JOB_NO_LONGER_REQUEUEABLE ) {

						ActionErrors errors = new ActionErrors();
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.job.no.longer.requeueable"));
						saveMessages(request, errors);

						forwardTo = "error";


					} else if ( status == GUICallStatus.FAILED_JOB_NOT_FOUND ) {

						ActionErrors errors = new ActionErrors();
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.job.not.found"));
						saveMessages(request, errors);

						forwardTo = "error";

					} else if ( status == GUICallStatus.FAILED_VERSION_NOT_MATCH_DB ) {

						ActionErrors errors = new ActionErrors();
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.record.not.updated"));
						saveMessages(request, errors);

						forwardTo = "error";
					} else {

						log.error( "Unknown Status returned from 'connToServer.requeueJob', status = " + status );

						ActionErrors errors = new ActionErrors();
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.system"));
						saveMessages(request, errors);

						forwardTo = "error";

					}


				} finally {

					if ( connToServer != null ) {
						try {
							connToServer.destroy();
						} catch (Throwable ex) {

						}
					}
				}

			}

		} catch (Throwable ex) {

			log.error( "Exception: " + ex.getMessage(), ex );

			ActionErrors errors = new ActionErrors();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.system"));
			saveMessages(request, errors);

			forwardTo = "error";
		}


		ActionForward actionForward = getActionForward( forwardTo, mapping );


		return actionForward;

	}
}