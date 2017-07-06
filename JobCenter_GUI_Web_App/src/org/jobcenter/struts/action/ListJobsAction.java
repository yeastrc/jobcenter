
package org.jobcenter.struts.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.JobType;
import org.jobcenter.dto.RequestTypeDTO;
import org.jobcenter.gui_connection_to_server_client_factory.GUIConnectionToServerClientFactory;
import org.jobcenter.guiclient.GUIConnectionToServerClient;
import org.jobcenter.guiclient.response.GUIListJobsResponse;
import org.jobcenter.struts.BaseAction;
import org.jobcenter.struts.form.ListJobsForm;

/**
 */
public class ListJobsAction extends  BaseAction  {

	private static Logger log = Logger.getLogger(ListJobsAction.class);

	/**
	 * Method execute
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response) {

		GUIConnectionToServerClient connToServer = null;

		try {
			
			ListJobsForm form = (ListJobsForm) actionForm;

			connToServer = GUIConnectionToServerClientFactory.getInstance().getGUIConnectionToServerClient();

			Integer requestId = null;
			String submitter = null;
			Set<Integer> statusIds = new HashSet<Integer>();
			
			boolean[] statuses = form.getStatuses();

			for ( int count = 0; count < statuses.length; count++ ) {
				
				if ( statuses[ count ] ) {
					
					statusIds.add( count );
				}
			}
			
//			if ( ! StringUtils.isEmpty( form.getStatus() ) ) {
//				
//				try {
//					int statusId = Integer.parseInt( form.getStatus() );
//			
//					statusIds.add( statusId );
//							
//				} catch ( Throwable t ) {
//					
//					log.error( "Status is not numeric, = |" + form.getStatus() + "|." );
//				}
//			}


			if ( ! StringUtils.isEmpty( form.getRequestId()) ) {
				
				try {
					requestId = Integer.parseInt( form.getRequestId() );
							
				} catch ( Throwable t ) {
					
					log.error( "requestId is not numeric, = |" + form.getRequestId() + "|." );
				}
			}

			Integer indexStart = null;         //  set to null for starting at first record
			Integer jobsReturnCountMax = null; // set to null to get defined max
			
			if ( ! StringUtils.isEmpty( form.getIndexStart() ) ) {

				try {
					indexStart = new Integer( form.getIndexStart() );
				} catch (Exception e) {
					
					log.info( "indexStart in form is invalid, is = |" + form.getIndexStart() + "|.");
				}
			}
			
			if ( ! StringUtils.isEmpty( form.getJobsReturnCountMax() ) ) {

				try {
					jobsReturnCountMax = new Integer( form.getJobsReturnCountMax() );
				} catch (Exception e) {
					
					log.info( "indexStart in form is invalid, is = |" + form.getIndexStart() + "|.");
				}
			}
			
			Set<String> requestTypeNames = new HashSet<String>();
			Set<String> jobTypeNames = new HashSet<String>();

			if ( ! StringUtils.isEmpty( form.getRequestType() ) ) {
				
				requestTypeNames.add( form.getRequestType() );
			}

			if ( ! StringUtils.isEmpty( form.getJobType() ) ) {
				
				jobTypeNames.add( form.getJobType() );
			}
			
	
			GUIListJobsResponse guiListJobsResponse 
				= connToServer.listJobs( statusIds, requestTypeNames, requestId, jobTypeNames, submitter, indexStart, jobsReturnCountMax );

				
			int jobCount = guiListJobsResponse.getJobCount();
			
			request.setAttribute("jobCount", jobCount);

			List<Job> jobList = guiListJobsResponse.getJobs();

			
			
			////////////////////
				
			request.setAttribute("jobList", jobList);

			List<JobType> jobTypes = connToServer.listJobTypes();

			request.setAttribute("jobTypes", jobTypes);

			List<RequestTypeDTO> requestTypes = connToServer.listRequestTypes();

			request.setAttribute("requestTypes", requestTypes);



		} catch (Throwable ex) {

			log.error( " Exception: " + ex.getMessage(), ex );

			ActionErrors errors = new ActionErrors();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.system"));
			saveMessages(request, errors);

		} finally {

		}

		String forwardTo = "success";

		ActionForward actionForward = getActionForward( forwardTo, mapping );


		return actionForward;
	}
}