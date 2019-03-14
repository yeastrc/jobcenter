<%@ include file="/WEB-INF/includes/imports.inc" %>

clientsConnectedList.jsp<br/>
<br/>


<h2>Clients That have connected since the server was last restarted</h2>

<table border="1" cellpadding="2">

  <tr>
	<th nowrap="nowrap">Node Name</th>
	<th nowrap="nowrap">Client Identifier</th>
	<th nowrap="nowrap">Client Status</th>
	<th nowrap="nowrap">Client IP Address</th>
	<th nowrap="nowrap">Client Start Time</th>
	<th nowrap="nowrap">Client Last Checkin Time</th>
	<th nowrap="nowrap">Client next expected checkin time</th>
	<th nowrap="nowrap">Client Last Get Job call (Start of Processing)</th>
	<th nowrap="nowrap">Client Last Get Job call (End of Processing)</th>
	<th nowrap="nowrap">Client Max Get Job call processing time, in milliseconds (Cleared after queried by this web app)</th>
  </tr>

 <c:forEach var="item" items="${ results }">

  <tr>
  
	<td nowrap="nowrap">
		<c:out value="${ item.nodeName }"></c:out>
	</td>
	<td nowrap="nowrap">
		<c:out value="${ item.clientIdentifierDTO.clientIdentifier }"></c:out>
	</td>
	<td nowrap="nowrap">
		<c:out value="${ item.clientStatus }"></c:out>
	</td>
	<td nowrap="nowrap">
		<c:out value="${ item.remoteIPAddress }"></c:out>
	</td>

	<td nowrap="nowrap">
		<c:out value="${ item.startTime }"></c:out>
	</td>
	<td nowrap="nowrap">
		<c:out value="${ item.lastStatusUpdatedTime }"></c:out>
	</td>
	<td nowrap="nowrap">
		<c:out value="${ item.nextExpectedStatusUpdatedTime }"></c:out>
	</td>
	<td nowrap="nowrap">
		<c:out value="${ item.lastGetJobStartProcessingTime }"></c:out>
	</td>
	<td nowrap="nowrap">
		<c:out value="${ item.lastGetJobEndProcessingTime }"></c:out>
	</td>
	<td nowrap="nowrap">
		<c:out value="${ item.display_GetJobMaxProcessingTimeSinceLastGUIQuery }"></c:out>
	</td>

  </tr>

 </c:forEach>


</table>
