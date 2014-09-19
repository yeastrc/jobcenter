
package org.jobcenter.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.jobcenter.constants.GUIWebAppConstants;
import org.jobcenter.guiclient.GUICallStatus;
import org.jobcenter.guiclient.GUIConnectionToServerClient;
import org.jobcenter.struts.BaseAction;
import org.jobcenter.struts.form.*;


public class UpdateJobPriorityAction extends BaseAction {

	private static Logger log = Logger.getLogger(UpdateJobPriorityAction.class);

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

		UpdateJobPriorityForm updateJobPriorityForm = (UpdateJobPriorityForm) form;

		String forwardTo = "success";

		log.info( "Entering execute" );

		try {

			int jobId = updateJobPriorityForm.getJobId();

			int dbRecordVersionNumber = updateJobPriorityForm.getJobRecordVersionId();

			int newPriority = 0;
			
			try {
				
				newPriority = Integer.parseInt( updateJobPriorityForm.getNewPriority() );
			} catch ( Throwable t ) {

				ActionErrors errors = new ActionErrors();
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.newpriority.not.valid"));
				saveMessages(request, errors);
			}
			
			if ( newPriority <= 0 ) {

				ActionErrors errors = new ActionErrors();
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.newpriority.not.valid"));
				saveMessages(request, errors);
			}
			
			
			if ( jobId > 0 && newPriority > 0 ) {

				GUIConnectionToServerClient connToServer = null;

				try {

					connToServer = new GUIConnectionToServerClient();

					connToServer.init( GUIWebAppConstants.URL_TO_SERVER );
					
					//  To not enforce dbRecordVersionNumber, pass null instead

					GUICallStatus status = connToServer.changeJobPriority( newPriority, jobId, dbRecordVersionNumber );

					if ( status == GUICallStatus.SUCCESS ) {

						
						
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