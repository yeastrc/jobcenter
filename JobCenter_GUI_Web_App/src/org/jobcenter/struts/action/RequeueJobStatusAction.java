
package org.jobcenter.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.jobcenter.gui_connection_to_server_client_factory.GUIConnectionToServerClientFactory;
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
				try {
					GUIConnectionToServerClient connToServer = GUIConnectionToServerClientFactory.getInstance().getGUIConnectionToServerClient();
					
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