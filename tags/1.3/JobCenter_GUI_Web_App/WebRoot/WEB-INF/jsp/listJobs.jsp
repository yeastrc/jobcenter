<%@ include file="/WEB-INF/includes/imports.inc" %>
<%@page import="org.jobcenter.misc.ValueRotator"%>

<%--  listJobs.jsp  --%>

<script type="text/javascript">

function showJobParams( index ) {

			var proteinDetails = document.getElementById("jobParam_" + index );

			if ( proteinDetails != null ) {
				proteinDetails.style.display = "";
			}


			var proteinDetailsLinkPlus = document.getElementById("jobParamLinkPlus_" + index );

			if ( proteinDetailsLinkPlus != null ) {
				proteinDetailsLinkPlus.style.display = "none";
			}

}


function hideJobParams( index ) {

			var proteinDetails = document.getElementById("jobParam_" + index );

			if ( proteinDetails != null ) {
				proteinDetails.style.display = "none";
			}

			var proteinDetailsLinkPlus = document.getElementById("jobParamLinkPlus_" + index );

			if ( proteinDetailsLinkPlus != null ) {
				proteinDetailsLinkPlus.style.display = "";
			}

			var proteinDetailsLinkMinus = document.getElementById("proteinDetailsLinkMinus_" + index );

			if ( proteinDetailsLinkMinus != null ) {
				proteinDetailsLinkMinus.style.display = "none";
			}
}



function clearStatusCheckBoxes(  ) {

	for( var counter = 1; counter <= 9; counter++ ) {

		var statusField = document.getElementById("statuses_" + counter );

		if ( statusField != null ) {
			statusField.checked = "";
		}
	}
}

</script>



<h2><c:out value="${ jobStatusLabel }"></c:out> Jobs</h2>

<html:form action="listJobs">

   <html:hidden property="jobGroup"/>

   Status (If none are selected, then all are included): <br/>

   Submitted: <html:checkbox property="statuses[1]" styleId="statuses_1" ></html:checkbox><br/>
   Running: <html:checkbox property="statuses[2]" styleId="statuses_2" ></html:checkbox><br/>
   Stalled: <html:checkbox property="statuses[3]" styleId="statuses_3" ></html:checkbox><br/>
   Complete: <html:checkbox property="statuses[4]" styleId="statuses_4" ></html:checkbox><br/>
   Complete with warnings: <html:checkbox property="statuses[5]" styleId="statuses_5" ></html:checkbox><br/>
   Hard Error: <html:checkbox property="statuses[6]" styleId="statuses_6" ></html:checkbox><br/>
   Soft Error: <html:checkbox property="statuses[7]" styleId="statuses_7" ></html:checkbox><br/>
   Canceled: <html:checkbox property="statuses[8]" styleId="statuses_8" ></html:checkbox><br/>
   Requeued: <html:checkbox property="statuses[9]" styleId="statuses_9" ></html:checkbox><br/>

	<input type="button" onclick="clearStatusCheckBoxes()" value="Clear Statuses" ></input  >


<%--
   <html:select property="status" >
        <html:option value="">All</html:option>
		<html:option value="1">Submitted</html:option>
		<html:option value="2">Running</html:option>
		<html:option value="3">Stalled</html:option>
		<html:option value="4">Complete</html:option>
		<html:option value="5">Complete with warnings</html:option>
		<html:option value="6">Hard Error</html:option>
		<html:option value="7">Soft Error</html:option>
		<html:option value="8">Canceled</html:option>
		<html:option value="9">Requeued</html:option>
   </html:select>
--%>
   <br/>

   <c:choose>
     <c:when test="${ ! empty jobTypes }">


	  Job Type:
	  <html:select property="jobType">
	    <html:option value="">All</html:option>
	    <html:optionsCollection  name="jobTypes" label="name" value="name" />
	  </html:select>
	  <br/>

     </c:when>
     <c:otherwise>

       Job Types is empty<br/>
     </c:otherwise>
   </c:choose>

   <c:choose>
     <c:when test="${ ! empty requestTypes }">

	   Request Type:
	   <html:select property="requestType">
	     <html:option value="">All</html:option>
	     <html:optionsCollection  name="requestTypes" label="name" value="name" />
	   </html:select>
	   <br/>

     </c:when>
     <c:otherwise>

     Request Types is empty<br/>

     </c:otherwise>
   </c:choose>

   request Id: <html:text property="requestId"></html:text><br/>

   indexStart: <html:text property="indexStart"></html:text><br/>
   jobsReturnCountMax: <html:text property="jobsReturnCountMax"></html:text><br/>

	   <html:submit>Update List</html:submit>


</html:form>
<br/>
<br/>

<h3>Job Count = <c:out value="${ jobCount } " /></h3>
<br/>

<br/>

<%

ValueRotator valueRotator = new ValueRotator();

valueRotator.setValue("A:B");

request.setAttribute("valueRotator", valueRotator );


 %>


<table  cellpadding="10" cellspacing="0" style="width:960px;" align="left">


 <c:forEach var="job" items="${ jobList }" varStatus="jobListVarStatus" >

	<%--  retrieve value from 'valueRotator.value' once per iteration so it is the same value all the way through that iteration --%>
	<c:set var="rowClass" value="${ valueRotator.value }" />

  <tr class="listJobs listJobs_<c:out value="${ rowClass } " />" >

   <td>

	<div  >


	job.jobType.name = <c:out value="${ job.jobType.name }" /><br/><br/>

	<c:choose>

	 <c:when test="${ job.jobType.name == 'DavisLabExtractFromR3DFiles' }">

			Job for Davis Lab Image Upload,  Extract from R3D<br/><br/>


			<c:if test="${ ! empty   job.jobParameters['UploadR3DFilesDirectory']   }" >

				UploadR3DFilesDirectory: <c:out value="${ job.jobParameters['UploadR3DFilesDirectory'] }" />

				<br/><br/>

			</c:if>

			<c:if test="${ ! empty   job.jobParameters['UploadStrain']   }" >

				UploadStrain : <c:out value="${ job.jobParameters['UploadStrain'] }" />

				<br/><br/>

			</c:if>

	 </c:when>

	 <c:when test="${ job.jobType.name == 'DavisLabImportR3DToImagerepo' }">

			Job for Davis Lab Image Upload, Import to Imagerepo<br/><br/>


			<c:if test="${ ! empty   job.jobParameters['UploadR3DFilesDirectory']   }" >

				UploadR3DFilesDirectory: <c:out value="${ job.jobParameters['UploadR3DFilesDirectory'] }" />

				<br/><br/>

			</c:if>

			<c:if test="${ ! empty   job.jobParameters['UploadStrain']   }" >

				UploadStrain : <c:out value="${ job.jobParameters['UploadStrain'] }" />

				<br/><br/>

			</c:if>

	 </c:when>


	 <c:when test="${ job.jobType.name == 'PhiliusProcessUploadFile'}">

			Philius processing:  Job for processing the initial FASTA file the user uploaded, :<br/><br/>

			<c:if test="${ ! empty   job.jobParameters['UploadR3DFilesDirectory']   }" >

				UploadR3DFilesDirectory: <c:out value="${ job.jobParameters['UploadR3DFilesDirectory'] }" />

				<br/><br/>

			</c:if>

			<c:if test="${ ! empty   job.jobParameters['UploadStrain']   }" >

				UploadStrain : <c:out value="${ job.jobParameters['UploadStrain'] }" />

				<br/><br/>

			</c:if>



	 </c:when>



	 <c:when test="${ job.jobType.name == 'PhiliusRunPhilius'  }">



			Job for running Philius on the blade:<br/><br/>

			<c:if test="${ ! empty   job.jobParameters['UploadR3DFilesDirectory']   }" >

				UploadR3DFilesDirectory: <c:out value="${ job.jobParameters['UploadR3DFilesDirectory'] }" />

				<br/><br/>

			</c:if>

			<c:if test="${ ! empty   job.jobParameters['UploadStrain']   }" >

				UploadStrain : <c:out value="${ job.jobParameters['UploadStrain'] }" />

				<br/><br/>

			</c:if>


	 </c:when>

	 <c:when test="${ job.jobType.name == 'PhiliusCreateResponse' }">

			Job for creating the Philius run response to the submitter:<br/><br/>


			<c:if test="${ ! empty   job.jobParameters['UploadR3DFilesDirectory']   }" >

				UploadR3DFilesDirectory: <c:out value="${ job.jobParameters['UploadR3DFilesDirectory'] }" />

				<br/><br/>

			</c:if>

			<c:if test="${ ! empty   job.jobParameters['UploadStrain']   }" >

				UploadStrain : <c:out value="${ job.jobParameters['UploadStrain'] }" />

				<br/><br/>

			</c:if>


	 </c:when>

	 <c:when test="${ job.jobType.name == 'PhiliusLoadPhiliusResults' }">

			Job for loading the philius results to the database:<br/><br/>

			<c:if test="${ ! empty   job.jobParameters['UploadR3DFilesDirectory']   }" >

				UploadR3DFilesDirectory: <c:out value="${ job.jobParameters['UploadR3DFilesDirectory'] }" />

				<br/><br/>

			</c:if>

			<c:if test="${ ! empty   job.jobParameters['UploadStrain']   }" >

				UploadStrain : <c:out value="${ job.jobParameters['UploadStrain'] }" />

				<br/><br/>

			</c:if>


	 </c:when>



	 <c:otherwise >



	 </c:otherwise>

	</c:choose>

				Job ID: <c:out value="${ job.id }"></c:out>

				<br/>
				<br/>
				Request ID:  <c:out value="${ job.requestId }"></c:out>
				<br/>
				<br/>

				<a href="viewJob.do?jobId=<c:out value="${ job.id }"></c:out>" >
					View Job <%-- <c:out value="${ job.id }"> </c:out> --%>
				</a>

				&nbsp;&nbsp;&nbsp;

				<a href="listJobs.do?requestId=<c:out value="${ job.requestId }"></c:out>" >
					View All Jobs for this Request Id <%-- <c:out value="${ job.id }"> </c:out> --%>
				</a>


			<br/><br/>

			status: <c:out value="${ job.status.name }"></c:out>
			<br/><br/>


			submitter: <c:out value="${ job.submitter }"></c:out>
			<br/><br/>

			submit date: <fmt:formatDate value="${ job.submitDate }" type="both" timeStyle="full" />

			<br/><br/>

			priority: <c:out value="${ job.priority }"></c:out>
			<br/><br/>



			<c:if test="${ job.requeueable == true }" >

				<a href="requeueJob.do?jobId=<c:out value="${ job.id }" />&jobRecordVersionId=<c:out value="${ job.dbRecordVersionNumber }" />" >
					Requeue Job
				</a>
			</c:if>


				<br/>
				<br/>

			<c:if test="${ job.cancellable }" >

				<a href="cancelJob.do?jobId=<c:out value="${ job.id }" />&jobRecordVersionId=<c:out value="${ job.dbRecordVersionNumber }" />" >
					Cancel Job
				</a>

			</c:if>

			<br/>
			<br/>
			<br/>
			<br/>

			 <div id="jobParamLinkPlus_<c:out value="${ jobListVarStatus.count }" />" >

				<a href="javascript:showJobParams( '<c:out value="${ jobListVarStatus.count }" />' )"
    				style="color:#ff0000;font-size:8pt; text-decoration: none;"
	    			onmouseover="Tip('display all job parameters')" onmouseout="UnTip()"  >all job parameters
				</a>

			</div>

			<div id="jobParam_<c:out value="${ jobListVarStatus.count }" />"  style="display:none;"  >
			   <a href="javascript:hideJobParams( '<c:out value="${ jobListVarStatus.count }" />' )"
		 		    style="color:#ff0000;font-size:8pt;text-decoration: none;"
		 		    onmouseover="Tip('not show all job parameters')" onmouseout="UnTip()"  >hide all job parameters
		       </a>
			<br/>
			<br/>


			All Job Parameters

		  <table border="1" cellpadding="2">


		  <tr>
			<th>key</th>
			<th>value</th>
		  </tr>


			<c:forEach var="jobParam" items="${ job.jobParameters }" >

			 <tr>
				<td nowrap="nowrap">
				<c:out value="${ jobParam.key }"></c:out>
				</td>

				<td nowrap="nowrap">
				<c:out value="${ jobParam.value }"></c:out>
				</td>

		  	  </tr>

			</c:forEach>

		  </table>

		    </div>

	</div>

  </td>
  </tr>

</c:forEach>

</table>
