
package org.jobcenter.struts.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.jobcenter.dto.JobType;
import org.jobcenter.guiclient.GUIConnectionToServerClient;
import org.jobcenter.struts.BaseAction;


public class ListJobTypesAction extends BaseAction {

	private static Logger log = Logger.getLogger(ListJobTypesAction.class);

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


		GUIConnectionToServerClient connToServer = null;

		try {

			connToServer = new GUIConnectionToServerClient();

			connToServer.init( GUIWebAppConstants.URL_TO_SERVER );

			List<JobType> jobTypes = connToServer.listJobTypes();

			request.setAttribute("jobTypes", jobTypes);



		} catch (Throwable ex) {

			log.error( " Exception: " + ex.getMessage(), ex );

			ActionErrors errors = new ActionErrors();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.system"));
			saveMessages(request, errors);

		} finally {

			if ( connToServer != null ) {

				try {

					connToServer.destroy();

				} catch (Throwable ex) {

					log.error( "ListJobsAction: Exception calling connToServer.destroy(): " + ex.getMessage(), ex );
				}
			}
		}


		String forwardTo = "success";

		ActionForward actionForward = getActionForward( forwardTo, mapping );


		return actionForward;
	}
}