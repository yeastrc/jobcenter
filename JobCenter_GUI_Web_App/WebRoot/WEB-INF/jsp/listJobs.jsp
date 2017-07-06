<%@page import="org.jobcenter.constants.JobStatusValuesConstantsCopy"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>


<%--  listJobs.jsp  --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


 <c:set var="headerAdditions">

	<script type="text/javascript" src="js/libs/handlebars-v2.0.0.min.js"></script>
	<script type="text/javascript" src="js/listJobs.js?x=${cacheBustValue}"></script> 

 </c:set>

 <c:set var="pageTitle">List Jobs</c:set>
 
 <%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>
   
  <div class="overall-enclosing-block">

	   

	<h2>Jobs Listing</h2>

	<div  style="font-size: 16px; font-weight: bold; margin-bottom: 10px;">
		Filter jobs shown
	</div>
	<div style="margin-bottom: 3px;">
	   <span style="font-size: 14px; font-weight: bold;">Job Status</span> (If none are selected, then all are included): 
	</div>	
	<div style="margin-left: 5px;">
		<div class="status-selector-container" >
		 <label>
		   <input type="checkbox" class=" status_selector_entry_jq " data-status_id="<%= JobStatusValuesConstantsCopy.JOB_STATUS_SUBMITTED %>" >
		   Submitted: 
		 </label>
		</div>	
		<div class="status-selector-container">
		 <label>
		   <input type="checkbox" class=" status_selector_entry_jq " data-status_id="<%= JobStatusValuesConstantsCopy.JOB_STATUS_RUNNING %>" >
		   Running: 
		 </label>
		</div>	
		<div class="status-selector-container">
		 <label>
		   <input type="checkbox" class=" status_selector_entry_jq " data-status_id="<%= JobStatusValuesConstantsCopy.JOB_STATUS_FINISHED %>" >
		   Complete: 
		 </label>
		</div>	
		<c:if test="${ not configObject.simpleJobStatusFilter }">
			<div class="status-selector-container">
			 <label>
			   <input type="checkbox" class=" status_selector_entry_jq " data-status_id="<%= JobStatusValuesConstantsCopy.JOB_STATUS_FINISHED_WITH_WARNINGS %>" >
			   Complete with warnings: 
			 </label>
			</div>
		</c:if>	
		<div class="status-selector-container">
		 <label>
		   <input type="checkbox" class=" status_selector_entry_jq " data-status_id="<%= JobStatusValuesConstantsCopy.JOB_STATUS_HARD_ERROR %>" >
		   Hard Error: 
		 </label>
		</div>	
		<c:if test="${ not configObject.simpleJobStatusFilter }">
			<div class="status-selector-container">
			 <label>
			   <input type="checkbox" class=" status_selector_entry_jq " data-status_id="<%= JobStatusValuesConstantsCopy.JOB_STATUS_SOFT_ERROR %>" >
			   Soft Error: 
			 </label>
			</div>	
		</c:if>	
		<div class="status-selector-container">
		 <label>
		   <input type="checkbox" class=" status_selector_entry_jq " data-status_id="<%= JobStatusValuesConstantsCopy.JOB_STATUS_CANCELED %>" >
		   Canceled: 
		 </label>
		</div>	
		<div class="status-selector-container">
		 <label>
		   <input type="checkbox" class=" status_selector_entry_jq " data-status_id="<%= JobStatusValuesConstantsCopy.JOB_STATUS_REQUEUED %>" >
		   Requeued: 
		 </label>
		</div>	
		<c:if test="${ not configObject.simpleJobStatusFilter }">
			<div class="status-selector-container">
			 <label>
			   <input type="checkbox" class=" status_selector_entry_jq " data-status_id="<%= JobStatusValuesConstantsCopy.JOB_STATUS_STALLED %>" >
			   Stalled: 
			 </label>
			</div>	
		  </div>
		</c:if>	
	<div>
		<input type="button" value="Clear Statuses" id="clear_statuses_button" />
	</div>
	
	<div style="font-size: 14px; font-weight: bold; margin-bottom: 10px; margin-top: 10px;">
		Job Type:
	  	<select id="job_type_selector"></select>
	
	</div>
	
	<c:if test="${ not configObject.requestTypesDisable }">
		<div style="font-size: 14px; font-weight: bold; margin-bottom: 6px; margin-top: 10px;">
			Request Type:
			<select id="request_type_selector"></select>
		</div>
	</c:if>
	<div >
		Submitter:
		<input id="submitter" type="text"> 
	</div>

	<div style="margin-top: 10px;">
	    <input type="button" value="Update List" id="update_list_button">
	</div>
	
	<div style="margin-top: 15px; margin-bottom: 15px; ">
		<div style="font-size: 16px; font-weight: bold;">
			Job Count:&nbsp;&nbsp;<span id="job_count_display"></span>
		</div>
		<div >
			(total job count for filters)
		</div>
	</div>
		
	<%-- Style for job entries --%>
	<style >
		/* zebra stripe the jobs */
		#job_list > div:nth-child(odd) { background-color: #dddddd; }
		#job_list > div:nth-child(even) { background-color: #f0f0f0; }		
	
		.main-job-value-line { margin-bottom: 5px; }
		.job-parameter-table { padding: 0px; border-collapse: collapse; }
		.job-parameter-table td { font-size: 10pt; padding: 4px; border-spacing : 0px; border-color: grey; border-style: solid; border-width: 1px; }

	</style>
	
	<div id="job_list"></div>
	

	<script id="selector_all_option" type="text/text" >
		<option value="">All</option>
	</script>
	
	<script id="job_type_selector_name_template"  type="text/x-handlebars-template" >
		<option value="{{ name }}">{{ name }}</option>
	</script>
	
	<script id="request_type_selector_name_template"  type="text/x-handlebars-template" >
		<option value="{{ name }}">{{ name }}</option>
	</script>
	
	<script id="job_entry_template"  type="text/x-handlebars-template" >
		<%@ include file="/WEB-INF/jsp-includes/jobEntryOnListJobsTemplate.jsp" %>
	</script>	
	
	</div>
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
