<%@ include file="/WEB-INF/includes/imports.inc" %>

listRequestTypes.jsp<br/>
<br/>


<h2>Request Types</h2>

<table border="1" cellpadding="2">

  <tr>
	<th></th>
	<th nowrap="nowrap">Name</th>
  </tr>

 <c:forEach var="item" items="${ requestTypes }">

  <tr>
	<td nowrap="nowrap">
			<c:out value="${ item.id }"></c:out>
	</td>
	<td nowrap="nowrap">
	<c:out value="${ item.name }"></c:out>
	</td>


  </tr>

 </c:forEach>


</table>
