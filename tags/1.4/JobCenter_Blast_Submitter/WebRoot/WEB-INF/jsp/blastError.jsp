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
			href="${contextPath}/css/error.css" />

	</head>

	<body>
		<div class="container">
			<div class="header">
				Basic Local Alignment Search Tool (BLAST)
			</div>
			<div class="msg">

				<div class="msg_label">
					&nbsp;Error
				</div>

				<div class="msg_content">
					<p class="result_msg">
						There was a problem submitting your BLAST.
						<br />
						<br />
						Please re-submit.
					</p>
					<p class="links">
						<a href="http://128.95.70.130:8080/blast">Back to BLAST</a>
					</p>
				</div>

			</div>
		</div>
	</body>
</html>