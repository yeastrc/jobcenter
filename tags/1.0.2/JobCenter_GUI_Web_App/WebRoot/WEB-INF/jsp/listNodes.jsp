<%@ include file="/WEB-INF/includes/imports.inc" %>

listNodes.jsp<br/>
<br/>


<h2>Nodes</h2>

<table border="1">

  <tr>
	<th>id</th>
	<th>name</th>
	<th>description</th>
	<th nowrap="nowrap">nodeAccessRule id</th>
	<th nowrap="nowrap">nodeAccessRule networkAddress</th>
  </tr>
  
	
 <c:forEach var="item" items="${ nodeList }">
  <tr>
	<td>
	<c:out value="${ item.id }"></c:out>
	</td>
	<td>
	<c:out value="${ item.name }"></c:out>
	</td>
	<td>
	<c:out value="${ item.description }"></c:out>
	</td>

	<td>
	<c:out value="${ item.nodeAccessRule.id }"></c:out>
	</td>
	<td>
	<c:out value="${ item.nodeAccessRule.networkAddress }"></c:out>
	</td>
  </tr>
  
 </c:forEach>


</table>
