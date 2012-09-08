package org.jobcenter.struts;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;

public class BaseAction  extends Action {

	private static Logger log = Logger.getLogger(BaseAction.class); 

	
	protected ActionForward getActionForward ( String forwardTo, ActionMapping mapping ) {
		
		ActionForward actionForward = mapping.findForward( forwardTo );
		
		if ( actionForward == null ) {
			log.error("ActionForward not found for '" + forwardTo + "'.", new Exception( "ActionForward not found for '" + forwardTo + "'." ));
		}

		return actionForward;
	}
}
