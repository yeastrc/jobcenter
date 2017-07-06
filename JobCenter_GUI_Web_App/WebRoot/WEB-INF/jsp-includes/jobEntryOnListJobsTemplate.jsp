<%--  jobEntryOnListJobsTemplate.jsp  

	Handlebars Template for a single Job entry on List Jobs page
--%>

<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  Must have enclosing div, with NO styling --%>

<div class=" single_job_entry_jq ">
	
	<div style="margin-bottom: 20px; padding: 8px; "> 
		
		<style>
			.jobtype-status-table {  }
			.jobtype-status-table td { font-size: 14px; font-weight: bold; padding-bottom: 10px; }
		</style>
		
	 <table class="table-no-border-no-cell-spacing-no-cell-padding jobtype-status-table ">
	  <tr>
	   <td style="padding-right: 10px;">Job Type:</td>
	   <td>{{ jobTypeName }}</td>
	  </tr>
	  <tr>
	   <td>Status:</td>
	   <td style="color: {{ statusColor }}">{{ status }}</td>
	  </tr>
	  <tr>
	   <td></td>
	   <td></td>
	  </tr>
	 </table>
	  
	  <div class="main-job-value-line" style=" margin-bottom: 10px;">
	  	 <%-- hashChar is a page variable that renders just # since #{ is a JSP EL expression.  replace with just # if change from JSP  --%>
	  	<c:set var="hashChar">#</c:set>
		<a href="viewJob.do?job_id={{ jobId }}${ hashChar }{{ jobId }}" >
			View Job <%-- {{ jobId }} --%>
		</a>
		<c:if test="${ not configObject.requestTypesDisable }">
			&nbsp;&nbsp;&nbsp;
			<a href="listJobs.do?requestId={{ requestId }}" >
				View All Jobs for this Request Id <%-- {{ id }} --%>
			</a>
		</c:if>
	  </div>

	  <div class="main-job-value-line">
		Job ID: {{ jobId }}
	  </div>
	  <div class="main-job-value-line">
		Request ID:  {{ requestId }}
	  </div>
	  <div class="main-job-value-line">
		submitter: {{ submitter }}
	  </div>
	  <div class="main-job-value-line">
		submit date: {{ submitDate }}
	  </div>
	  <div class="main-job-value-line">
			priority: {{ priority }}
	  </div>
	
		{{#if requeueable }}
		  <div class="main-job-value-line">
				<a href="javascript:" class=" requeue_job_link_jq "
					data-job_id="{{ jobId }}"
					data-job_record_version_id="{{ dbRecordVersionNumber }}" >
					Requeue Job
				</a>
		  </div>
		{{/if}}
	
		{{#if cancellable }}
		  <div class="main-job-value-line">
				<a href="javascript:" class=" cancel_job_link_jq "
					data-job_id="{{ jobId }}"
					data-job_record_version_id="{{ dbRecordVersionNumber }}" >
					Cancel Job
				</a>
		  </div>
		{{/if}}
	
	  <table class="job-parameter-table" >
	
		{{#each jobParameterList }}
	
		 <tr>
			<td style="white-space: nowrap;" >
				{{ paramKey }}
			</td>
	
			<td style="white-space: nowrap;" >
				{{ paramValue }}
			</td>
	
	  	  </tr>
	
		{{/each}}
	
	  </table>
	  
	
	</div>	
	
</div>
