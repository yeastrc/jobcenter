<%@ include file="/WEB-INF/includes/imports.inc" %>

listClientsStatus.jsp<br/>
<br/>


<h2>Clients Status</h2>

<table border="1" cellpadding="2">

  <tr>
	<th nowrap="nowrap">Node Name</th>
	<th nowrap="nowrap">Late</th>
	<th nowrap="nowrap">Up/Down</th>
	<th nowrap="nowrap">Last Checkin Time</th>
	<th nowrap="nowrap">Time considered late for next checkin time</th>
	<th nowrap="nowrap">Running jobs</th>
  </tr>

 <c:forEach var="item" items="${ results }"  varStatus="itemStatus" >

  <tr>
  
	<td nowrap="nowrap">
		<c:out value="${ item.node.name }"></c:out>
	</td>
	<td nowrap="nowrap">
		<c:if test="${ item.checkinIsLate }"><span style="color:red" ></c:if>
		<c:out value="${ item.checkinIsLate }"></c:out>
		<c:if test="${ item.checkinIsLate }"></span ></c:if>
	</td>
	<td nowrap="nowrap">
		<c:choose>
			<c:when test="${ item.clientStarted }">
				Client was started
			</c:when>
			<c:otherwise>
				Client has been shut down
			</c:otherwise>
		
		</c:choose>
	</td>
	
	<td nowrap="nowrap">
		<c:out value="${ item.lastCheckinTime }"></c:out>
	</td>
	<td nowrap="nowrap">
		<c:out value="${ item.lateForNextCheckinTime }"></c:out>
	</td>

	<td  nowrap="nowrap">

	  <c:forEach var="job" items="${ item.runningJobs }" varStatus="jobItemStatus">

		 <a href="viewJob.do?jobId=<c:out value="${ job.id }"></c:out>" ><c:out value="${ job.jobType.name }"></c:out>
	   	 (<c:out value="${ job.currentRun.id }"></c:out>)</a><c:if test="${ jobItemStatus.count < fn:length(item.runningJobs) }" ><br /></c:if>
	   		
	  </c:forEach >
	  
	  
<%-- 	  
<c:if test="${ item.node.name  == 'localNode' }" >	  

<a href="viewJob.do?jobId=142" >sampleModule (750)</a>,

<a href="viewJob.do?jobId=142" >sampleModule (751)</a>,

<a href="viewJob.do?jobId=142" >sampleModule (752)</a>, 

<a href="viewJob.do?jobId=142" >sampleModule (753)</a>

 	
</c:if>
--%>

	</td>

  </tr>


<%-- 
  <c:if test="${ ! empty  item.runningJobs }" >

  <tr class=" ">
   <td colspan="4">

	<h3>Jobs in running status on that node</h3>


    <table border="1" cellpadding="5" cellspacing="0">
     <tr>
     	<th nowrap="nowrap" >job id</th>
	    <th nowrap="nowrap" >job name</th>  
     	<th nowrap="nowrap" >current run id</th>
     </tr>
     

	 <c:forEach var="job" items="${ item.runningJobs }">
	
	  <tr>
	  
		<td nowrap="nowrap">     
			<c:out value="${ job.id }"></c:out>
		</td>
		<td nowrap="nowrap">     
			<c:out value="${ job.jobType.name }"></c:out>
		</td>
		<td nowrap="nowrap">     
			<c:out value="${ job.currentRun.id }"></c:out>
		</td>
	  </tr>

	 </c:forEach >


    </table>
     
     
   </td>
  </tr>

  </c:if>
--%>
     

 </c:forEach>


</table>
