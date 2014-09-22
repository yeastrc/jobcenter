<%@ include file="/WEB-INF/includes/imports.inc" %>

clientsUsingSameNodeNameList.jsp<br/>
<br/>


<h2>Clients where two or more are using the same node name that have connected since the server was last restarted</h2>

<table border="1" cellpadding="2">

  <tr>
	<th nowrap="nowrap">Node Name</th>
	<th nowrap="nowrap">Client Identifier</th>
	<th nowrap="nowrap">Client Status</th>
	<th nowrap="nowrap">Client IP Address</th>
	<th nowrap="nowrap">Client Start Time</th>
	<th nowrap="nowrap">Client Last Checkin Time</th>
	<%-- 
	<th nowrap="nowrap">Client next expected checkin time</th>
	--%>
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
<%-- 
	<td nowrap="nowrap">
		<c:out value="${ item.nextExpectedStatusUpdatedTime }"></c:out>
	</td>
--%>
  </tr>

 </c:forEach>


</table>
