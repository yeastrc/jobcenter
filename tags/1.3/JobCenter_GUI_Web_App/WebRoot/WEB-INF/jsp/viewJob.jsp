<%@ include file="/WEB-INF/includes/imports.inc" %>

viewJob<br/>
<br/>
    <a href="listJobs.do">list jobs</a><br>
<br/>

<h2>Job</h2>
<br/>
job.jobType.name = <c:out value="${ job.jobType.name }" /><br/><br/>


	<c:if test="${ job.requeueable }" >

		<a href="requeueJob.do?jobId=<c:out value="${ job.id }" />&jobRecordVersionId=<c:out value="${ job.dbRecordVersionNumber }" />" >
			Requeue Job
		</a>

		<br/>
		<br/>
	</c:if>



	<c:if test="${ job.cancellable }" >

		<a href="cancelJob.do?jobId=<c:out value="${ job.id }" />&jobRecordVersionId=<c:out value="${ job.dbRecordVersionNumber }" />" >
			Cancel Job
		</a>
		<br/>
		<br/>

	</c:if>

		<br/>
		<br/>

<html:form action="updateJobPriority">

	Update Job Priority
  <%-- jobId --%><html-el:hidden property="jobId" value="${ job.id }"/>
  <%-- jobRecordVersionId --%><html-el:hidden property="jobRecordVersionId" value="${ job.dbRecordVersionNumber }"/>
  <%-- newPriority --%><html:text property="newPriority"></html:text>

<html:submit>Update Priority</html:submit>

</html:form>

		<br/>
		<br/>



<table border="1" cellpadding="5" cellspacing="0">

  <tr>
	<th>id</th>
	<th nowrap="nowrap">status</th>
	<th>priority</th>
	<th>submitter</th>
	<th>submitDate</th>

  </tr>
  <tr>
	<td>
	<c:out value="${ job.id }"></c:out>
	</td>
	<td>
	<c:out value="${ job.status.name }"></c:out>
	</td>
	<td>
	<c:out value="${ job.priority }"></c:out>
	</td>
	<td>
	<c:out value="${ job.submitter }"></c:out>
	</td>
	<td>
	<fmt:formatDate value="${ job.submitDate }" type="both" timeStyle="full" />
	</td>

<%--
	<td>
	<c:out value="${ job.jobTypeId }"></c:out>
	</td>
--%>
  </tr>



</table>


<h3>Job parameters</h3>


<table border="1" cellpadding="5" cellspacing="0">

  <tr>
	<th>key</th>
	<th>value</th>
  </tr>


 <c:forEach var="item" items="${ job.jobParameters }" varStatus="jobParamVarStatus" >
  <tr>
	<td nowrap="nowrap">
		<c:out value="${ item.key }"></c:out>
	</td>
	<td nowrap="nowrap">
	<c:out value="${ item.value }"></c:out>
	</td>
  </tr>

 </c:forEach>


</table>

<br/>
<br/>
<br/>

<h2>Runs</h2>
runs listed in reverse id order

<table border="1" cellpadding="5" cellspacing="0">

  <tr>
	<th>id</th>
	<th nowrap="nowrap">status name</th>
	<th>Start Date</th>
	<th>End Date</th>
	<th>node</th>

  </tr>

<%--
  	private int id;

	private int jobId;
	private int nodeId;
	private int statusId;

	private Date startDate;
	private Date endDate;

	List<RunMessageFromModule> runMessages;

	private Node node;


	private StatusDTO status;

--%>

 <c:forEach var="item" items="${ job.allRuns }">
  <tr class="listJobs listJobs_B ">
	<td nowrap="nowrap">
		<c:out value="${ item.id }"></c:out>
<%--
	  <a href="viewRun.do?runId=<c:out value="${ item.id }"></c:out>" >
		View Run <c:out value="${ item.id }"></c:out>
	  </a>
--%>
	</td>
	<td>
	<c:out value="${ item.status.name }"></c:out>
	</td>
	<td nowrap="nowrap">
	<fmt:formatDate value="${ item.startDate }" type="both" timeStyle="full" />
	</td>
	<td nowrap="nowrap">
	<fmt:formatDate value="${ item.endDate }" type="both" timeStyle="full" />
	</td>
	<td>
	<c:out value="${ item.node.name }"></c:out>
	</td>
  </tr>



  <c:if test="${ ! empty  item.runMessages }" >

  <tr class="listJobs listJobs_A ">
   <td colspan="5">

	<h3>Run Messages</h3>

    <table border="1" cellpadding="5" cellspacing="0">
     <tr>
     	<th >run message type</th>
	    <th nowrap="nowrap">run message message</th>
     </tr>

	  <c:forEach var="itemMsg" items="${ item.runMessages }">
	  <tr>

		<td valign="top" align="center">
		<c:out value="${ itemMsg.runMsgType.name }"></c:out>
		</td>
		<td>
			<c:out value="${ itemMsg.message }"></c:out>
		</td>
	  </tr>
	  </c:forEach>

      </table>

    </td>
  </tr>

  </c:if>


  <c:if test="${ ! empty  item.runOutputParams }" >

  <tr class="listJobs listJobs_A ">
   <td colspan="5">

	<h3>Run Output parameters</h3>


	<table border="1" cellpadding="5" cellspacing="0">

	  <tr>
		<th>key</th>
		<th>value</th>
	  </tr>


	 <c:forEach var="itemOutputParam" items="${ item.runOutputParams }" varStatus="runOutputParamVarStatus" >
	  <tr>
		<td nowrap="nowrap">
			<c:out value="${ itemOutputParam.key }"></c:out>
		</td>
		<td nowrap="nowrap">
		<c:out value="${ itemOutputParam.value }"></c:out>
		</td>
	  </tr>

	 </c:forEach>


	</table>

    </td>
  </tr>

  </c:if>

 </c:forEach>


</table>