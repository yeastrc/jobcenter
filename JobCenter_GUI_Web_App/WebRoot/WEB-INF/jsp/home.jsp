<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>


<%@ include file="/WEB-INF/includes/imports.inc" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>

    <title>Job Center - Home<c:if test="${ not configObject.webappSecurityInstalled }"> NO SECURITY INSTALLED</c:if></title>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
<%--
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
--%>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>

  <body>
  

	<c:if test="${ not configObject.webappSecurityInstalled }">
  		<H1>There is no access security in this installation</H1>
  	</c:if>
  	
  	<H1>Jobcenter</H1>

    <a href="listJobs.do">list jobs</a><br><br>

	<c:if test="${ not configObject.requestTypesDisable }">
    	<a href="listRequests.do">list jobs by request</a><br><br>
    </c:if>

	<c:if test="${ not configObject.simpleHomePage }">
    	<a href="listJobTypes.do">list job types</a><br><br>
    </c:if>

	<c:if test="${ not configObject.simpleHomePage and not configObject.requestTypesDisable }">
	    <a href="listRequestTypes.do">list request types</a><br><br>
	</c:if>
	
    <a href="listClientsStatus.do">list client status</a><br><br>

	<c:if test="${ not configObject.simpleHomePage }">
		<a href="clientsConnectedList.do">list clients that connected since the server last restarted</a><br><br>

		<a href="clientsFailedToConnectList.do">list clients that failed to connect since the server last restarted</a><br><br>
	
		<a href="clientsUsingSameNodeNameList.do">list clients where two or more are using the same node name that have connected since the server was last restarted</a><br><br>
	</c:if>
<%--
    <a href="listNodes.do">list nodes</a><br><br>
--%>

	Server URL: <c:out value="${ serverURL }" ></c:out>
  </body>
</html>
