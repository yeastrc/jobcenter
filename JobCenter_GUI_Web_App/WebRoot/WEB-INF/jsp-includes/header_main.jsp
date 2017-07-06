

<%--  header_main.jsp    /WEB-INF/jsp-includes/header_main.jsp 

	  This is included in every page other than login, forgot password processing, ...  

	  The data for this header is populated in the 
	  
	  class GetPageHeaderData 
	    using getPageHeaderDataWithProjectId(...) or getPageHeaderDataWithoutProjectId(...)
	    
	  One of those 2 methods is required to be called for every page this header is included on



	Page variable "helpURLExtensionForSpecificPage" specifies the extension to the Help URL for the specific page
	
	Page variable "helpURLExtensionForSpecificPage" is used in 2 places in this file.
--%>


<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<%--  Default title --%>

<%-- 
	<c:if test="${ empty pageTitle }" >

		<c:set var="pageTitle" value="Protein Crosslinking Database" ></c:set>
	
	</c:if>
--%>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
response.addHeader("Cache-control", "no-store"); // tell proxy not to cache
response.addHeader("Cache-control", "max-age=0"); // stale right away
%>

<%--
	HTML5 DOCTYPE
	
	The DOCTYPE is partially put in to make IE not go into quirks mode (the default when there is no DOCTYPE).

--%>

<!DOCTYPE html>

<html class="no-js"> <!--  Modernizr will change "no-js" to "js" if Javascript is enabled -->


<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>

	<title>Jobcenter - <c:out value="${ pageTitle }" ></c:out></title>

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="${ contextPath }/js/jquery-1.11.0.min.js"></script>  --%>
	 
<%-- 	
	<script type="text/javascript" src="${ contextPath }/js/header_main.js?x=${cacheBustValue}"></script>
--%>
	
	<style >
	
		/* This depends on the JQueryUI ui-lightness theme being included in the web app  */
		/*
		body.crosslinks-page-main .modal-dialog-overlay-background { background: #666 url(${ contextPath }/css/jquery-ui-1.10.2-Themes/ui-lightness/images/ui-bg_diagonals-thick_20_666666_40x40.png) 50% 50% repeat; }
		*/ 	
	</style>

	<%--  Output the contents of "headerAdditions" here --%>
	<c:out value="${ headerAdditions }" escapeXml="false" ></c:out>


	<link rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" type="text/css" media="print, projection, screen" />
	<link rel="stylesheet" href="${ contextPath }/css/global.css?x=${cacheBustValue}" type="text/css" media="print, projection, screen" />


</head>
 

<body class="jobcenter-page-main <c:out value="${ pageBodyClass }" ></c:out>">


 <%@ include file="/WEB-INF/jsp-includes/body_section_start_include_every_page.jsp" %>


 <%--  Whole Page Div container, needed for forcing the footer to the bottom of the viewport --%>
		
 <div class="jobcenter-page-main-outermost-div">  <%--  This div is closed in footer_main.jsp --%>
 
  
  <div class="header-outer-container">  <%--  Outer Container for the Header --%>
  
    <%--  The Right side contents for the header are first  --%>
  
  	<div class="header-right-edge-container">  <%--  Container for Right Side contents --%>
		
  	
  	</div>  <%--  END:  Container for Right Side contents  --%>
	
	
	<%-- Left Side contents --%>
	
	<h1>Jobcenter</h1>
	
	<a href="home.do">HOME</a>

  </div>
	

		