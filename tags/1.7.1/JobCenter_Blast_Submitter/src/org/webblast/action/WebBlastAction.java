package org.webblast.action;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.webblast.form.WebBlastForm;
import org.webblast.submit.WebBlastSubmit;

public class WebBlastAction extends Action {

	private static final Logger log = Logger.getLogger(WebBlastAction.class);
	private static final String FIND_FORWARD_ERROR = "error";
	private static final String FIND_FORWARD_SUCCESS = "success";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)  
	{

		WebBlastForm fm = null;
		WebBlastSubmit submit = null;
		
		String query = null;
		String task = null;
		String database = null;
		String outfmt = null;
		String alignments = null;
		String descriptions = null;
		String email = null;
		String filename = null;

		try {
			// Get parameters from form
			fm = (WebBlastForm) form;
			query = fm.getQuery();
			task = fm.getTask();
			database = fm.getDatabase();
			outfmt = fm.getOutfmt();
			alignments = fm.getAlignments();
			descriptions = fm.getDescriptions();
			email = fm.getEmail();
			filename = fm.getFilename();

		} catch (Throwable t) {
			
			log.error("Error getting parameters. ", t);
			return mapping.findForward(FIND_FORWARD_ERROR);
			
		}

		try {
			
			submit = new WebBlastSubmit();
			submit.main(query, task, database, outfmt, alignments, descriptions, email, filename);
			
		} catch (Throwable t) {
			
			log.error("Error submitting paramaters to main. ", t);
			return mapping.findForward(FIND_FORWARD_ERROR);
			
		}
		
		return mapping.findForward(FIND_FORWARD_SUCCESS);
	}



}
