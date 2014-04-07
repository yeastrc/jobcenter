<%@ include file="/WEB-INF/includes/imports.inc" %>

viewRun<br/>
<br/>
    <a href="viewJob.do?jobId=<c:out value="${ run.jobId }"></c:out>">view job</a><br>
<br/>
<br/>
<br/>

<h2>Run</h2>


<table border="1">

  <tr>
	<th>id</th>
	<th nowrap="nowrap">status name</th>
	<th>Start Date</th>
	<th>End Date</th>
	<th>jobId</th>
	<th>nodeId</th>
  </tr>

  <tr>
	<td>
	<c:out value="${ run.id }"></c:out>
	</td>
	<td>
	<c:out value="${ run.status.name }"></c:out>
	</td>
	<td nowrap="nowrap">
	<c:out value="${ run.startDate }"></c:out>
	</td>
	<td nowrap="nowrap">
	<c:out value="${ run.endDate }"></c:out>
	</td>
	<td>
	<c:out value="${ run.jobId }"></c:out>
	</td>
	<td>
	<c:out value="${ run.nodeId }"></c:out>
	</td>
  </tr>


</table>



<h3>run messages</h3>



<table border="1">

  <tr>
	<th nowrap="nowrap">message type</th>
	<th nowrap="nowrap">message</th>
  </tr>


  <c:forEach var="itemMsg" items="${ run.runMessages }">
  <tr>
	<td>
	<c:out value="${ itemMsg.type }"></c:out>
	</td>
	<td>
		<c:out value="${ itemMsg.message }"></c:out>
	</td>
  </tr>
  </c:forEach>


</table>

