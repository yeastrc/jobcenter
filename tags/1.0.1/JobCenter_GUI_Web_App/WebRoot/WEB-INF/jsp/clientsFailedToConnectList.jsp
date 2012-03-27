<%@ include file="/WEB-INF/includes/imports.inc" %>

clientsFailedToConnectList.jsp<br/>
<br/>


<h2>Clients that failed to connect since the server was last restarted</h2>

The "Client IP Address" is what has to match the configuration in the table "node_access_rule" field "network_address". <br/><br/>

<table border="1" cellpadding="2">

  <tr>
	<th nowrap="nowrap">Node Name</th>
	<th nowrap="nowrap">Client IP Address (At server)</th>
	<th nowrap="nowrap">Attempted Connection Time</th>
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
		<c:out value="${ item.remoteIPAddress }"></c:out>
	</td>
	<td nowrap="nowrap">
		<c:out value="${ item.lastStatusUpdatedTime }"></c:out>
	</td>
  </tr>

 </c:forEach>


</table>
