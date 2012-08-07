package org.jobcenter.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;

//import com.sun.org.apache.commons.logging.Log;
//import com.sun.org.apache.commons.logging.LogFactory;

public class ServletFilter implements Filter {


	private static final int BUFFER_SIZE = 10000000;

    /**
     * logger
     */

//	based on
	//  imported from jsf-impl.jar < part of the "Java EE 5 Libraries" and not exported to the WAR >

	//import com.sun.org.apache.commons.logging.Log;
	//import com.sun.org.apache.commons.logging.LogFactory;

//	private static final Log log = LogFactory.getLog(JobCenterFilter.class);


	private static final Logger log = Logger.getLogger(ServletFilter.class);


	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		log.debug(" getRequestURL = " + httpRequest.getRequestURL() );


		String getContentType = httpRequest.getContentType();



//		// inside service(ServletRequest req, ServletResponse res)
//		// of a class that implements Servlet
//		// or
//		// inside doPost(HttpServletRequest req, HttpServletResponse resp)
//		// of a class that implements HTTPServlet
//
//		StringBuilder stringBuilder = new StringBuilder();
//		BufferedReader bufferedReader = null;
//		try {
//		  InputStream inputStream = request.getInputStream();
//		  if (inputStream != null) {
//		   bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//		   char[] charBuffer = new char[128];
//		   int bytesRead = -1;
//		   while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
//		    stringBuilder.append(charBuffer, 0, bytesRead);
//		   }
//		  } else {
//		   stringBuilder.append("");
//		  }
//		} catch (IOException ex) {
//
//			System.out.println( "Exception reading message body" );
//			ex.printStackTrace();
//
////		  throw ex;
//		} finally {
//		  if (bufferedReader != null) {
//		   try {
//		    bufferedReader.close();
//		   } catch (IOException ex) {
//
//				System.out.println( "Exception closing message body stream" );
//				ex.printStackTrace();
//
////				throw ex;
//		   }
//		  }
//		}
//		String body = stringBuilder.toString();
//
//		System.out.println( "retrieveJob: Message body: " );
//
//		System.out.println( "|" + body + "|");
//
//		System.out.println( "END:  retrieveJob: Message body: " );

		chain.doFilter(request, response);



//		HttpServletResponse httpResponse = (HttpServletResponse) response;
//		MyHttpServletResponseWrapper wrapper =
//		  new MyHttpServletResponseWrapper(httpResponse);

//		String content = wrapper.toString();
//
//			System.out.println( "retrieveJob: Response Message body: " );
//
//			System.out.println( "|" + content + "|");
//
//			System.out.println( "END: Response retrieveJob: Message body: " );
//
//

	}


    /**
     * Initialize this filter by reading its configuration parameters
     *
     * @param config
     *            Configuration from web.xml file
     */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}


//
//	static class MyHttpServletResponseWrapper
//	  extends HttpServletResponseWrapper {
//
//	  private StringWriter sw = new StringWriter(BUFFER_SIZE);
//
//	  public MyHttpServletResponseWrapper(HttpServletResponse response) {
//	    super(response);
//	  }
//
//	  public PrintWriter getWriter() throws IOException {
//	    return new PrintWriter(sw);
//	  }
//
//	  public ServletOutputStream getOutputStream() throws IOException {
//	    throw new UnsupportedOperationException();
//	  }
//
//	  public String toString() {
//	    return sw.toString();
//	  }
//	}


}
