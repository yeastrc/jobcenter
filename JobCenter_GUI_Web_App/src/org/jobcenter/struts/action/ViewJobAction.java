
package org.jobcenter.struts.action;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.dto.RunMessageDTO;
import org.jobcenter.gui_connection_to_server_client_factory.GUIConnectionToServerClientFactory;
import org.jobcenter.guiclient.GUIConnectionToServerClient;
import org.jobcenter.struts.BaseAction;
import org.jobcenter.struts.form.ViewJobForm;


public class ViewJobAction extends  BaseAction  {

	private static Logger log = Logger.getLogger(ViewJobAction.class);


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

		ViewJobForm viewJobForm = (ViewJobForm) form;

		try {

			int jobId = viewJobForm.getJobId();

			if ( jobId > 0 ) {

				try {
					GUIConnectionToServerClient connToServer = GUIConnectionToServerClientFactory.getInstance().getGUIConnectionToServerClient();

					Job job = connToServer.viewJob( jobId );
					
					List<RunDTO> allRuns = job.getAllRuns();
					
					
					//  This solution for sorting only works if paging is not required
					
					if ( allRuns != null && ( ! allRuns.isEmpty() ) ) {

						Collections.sort( allRuns, new RunDTO.ReverseSortByStartDateComparator() );

						for ( RunDTO runDTO : allRuns ) {

							List<RunMessageDTO> runMessages = runDTO.getRunMessages();

							if ( runMessages != null  ) {

								// put the messages in id order

								Collections.sort( runMessages, new Comparator<RunMessageDTO>() {

									@Override
									public int compare(RunMessageDTO o1, RunMessageDTO o2) {

										return o1.getId() - o2.getId();
									}  
								} );
							}				
						}
						
						
					}
					
					request.setAttribute("job", job);




				} finally {
				}
			}


		} catch (Throwable ex) {

			log.error( "ListJobsAction: Exception: " + ex.getMessage(), ex );

			ActionErrors errors = new ActionErrors();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.system"));
			saveMessages(request, errors);

		}

		String forwardTo = "success";

		ActionForward actionForward = getActionForward( forwardTo, mapping );


		return actionForward;

	}
}