package org.jobcenter.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.jobcenter.jdbc.JobJDBCDAO;
import org.jobcenter.jdbc.JobJDBCDAOImpl;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 *
 */
public class HealthCheckServlet extends HttpServlet {

	private static final long serialVersionUID = -3742607353209195847L;

	@Override
    public void doGet (HttpServletRequest request,
           HttpServletResponse response)
           throws ServletException, IOException {


		   PrintWriter out = null;

		   try {


				WebApplicationContext springCTX = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());

				JobJDBCDAO jobJDBCDAO = JobJDBCDAOImpl.getFromApplicationContext( springCTX );




			   out = response.getWriter();

			   out.println( "<html><head><title></title></head><body>");

			   try {

				   jobJDBCDAO.healthCheck();

				   out.println( "success" );


			   } catch ( Exception ex ) {


				   out.println( "failure" );

			   }

			   out.println( "</body></html>");

		   } catch ( Exception ex ) {



		   } finally {

			   if ( out != null ) {

				   out.close();

			   }

		   }




	}

}
