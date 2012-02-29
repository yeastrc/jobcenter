<%@ include file="/WEB-INF/includes/imports.inc" %>

listJobTypes.jsp<br/>
<br/>


<h2>Job Types</h2>

<table border="1" cellpadding="2">

  <tr>
	<th></th>
	<th nowrap="nowrap">Name</th>
	<th nowrap="nowrap">Description</th>
	<th nowrap="nowrap">Priority</th>
	<th nowrap="nowrap">enabled</th>
	<th nowrap="nowrap">moduleName</th>
	<th nowrap="nowrap">minimumModuleVersion</th>
  </tr>

 <c:forEach var="item" items="${ jobTypes }">

  <tr>
	<td nowrap="nowrap">
			<c:out value="${ item.id }"></c:out>
	</td>
	<td nowrap="nowrap">
	<c:out value="${ item.name }"></c:out>
	</td>

	<td nowrap="nowrap">
	<c:out value="${ item.description }"></c:out>
	</td>
	<td nowrap="nowrap">
	<c:out value="${ item.priority }"></c:out>
	</td>
	<td nowrap="nowrap">
	<c:out value="${ item.enabled }"></c:out>
	</td>
	<td nowrap="nowrap">
	<c:out value="${ item.moduleName }"></c:out>
	</td>
	<td nowrap="nowrap">
	<c:out value="${ item.minimumModuleVersion }"></c:out>
	</td>

  </tr>

 </c:forEach>


</table>
