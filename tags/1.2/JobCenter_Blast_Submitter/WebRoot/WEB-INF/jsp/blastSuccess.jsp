<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">

		<title>Successful blast submission landing page</title>

		<link rel="stylesheet" type="text/css"
			href="${contextPath}/css/success.css" />

	</head>

	<body>
		<div class="container">
			<div class="header">
				Basic Local Alignment Search Tool (BLAST)
			</div>
			<div class="msg">

				<div class="msg_label">
					&nbsp;Results
				</div>

				<div class="msg_content">
					<p class="result_msg">
						Your blast will be done shortly. If you provided an email address,
						the results will be sent to you. If you did not, please check the
						output folder in a few moments.
					</p>
					<p>
						Here are some links while you wait...
						<br />
						<br />
						<a href="http://128.95.70.130:8080/blast">Back to BLAST</a>
						&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
						<a href="http://www.yeastrc.org/pdr/pages/front.jsp">YRC Home</a>
					</p>
				</div>

			</div>
		</div>
	</body>
</html>