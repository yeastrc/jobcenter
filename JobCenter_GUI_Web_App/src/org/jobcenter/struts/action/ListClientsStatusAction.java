/*
 * Generated by MyEclipse Struts
 * Template path: templates/java/JavaClass.vtl
 */
package org.jobcenter.struts.action;

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
import org.jobcenter.dto.NodeClientStatusDTO;
import org.jobcenter.gui_connection_to_server_client_factory.GUIConnectionToServerClientFactory;
import org.jobcenter.guiclient.GUIConnectionToServerClient;
import org.jobcenter.struts.BaseAction;

/** 
 * MyEclipse Struts
 * Creation date: 02-15-2012
 * 
 * XDoclet definition:
 * @struts.action
 */
public class ListClientsStatusAction extends BaseAction {

	private static Logger log = Logger.getLogger(ListClientsStatusAction.class);

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


		try {
			GUIConnectionToServerClient connToServer = GUIConnectionToServerClientFactory.getInstance().getGUIConnectionToServerClient();

			List<NodeClientStatusDTO> results = connToServer.listClientsStatus();

			request.setAttribute("results", results);

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