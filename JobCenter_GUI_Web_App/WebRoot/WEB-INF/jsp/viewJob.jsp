<%@ include file="/WEB-INF/includes/imports.inc" %>

<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>


<%--  listJobs.jsp  --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


 <c:set var="headerAdditions">

	<script type="text/javascript" src="js/libs/handlebars-v2.0.0.min.js"></script>
	<script type="text/javascript" src="js/viewJob.js?x=${cacheBustValue}"></script> 

 </c:set>
 
 <c:set var="pageTitle">View Job</c:set>

 <%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>
 
 	<script id="job_id_value" type="text/text">${ param.job_id }</script>
	
   
  <div class="overall-enclosing-block">

<br>
	 <a href="listJobs.do">list jobs</a><br>


<h2>Job</h2>

		<style>
			.jobtype-status-table {  }
			.jobtype-status-table td { font-size: 14px; font-weight: bold; padding-bottom: 10px; }
		</style>
		
	 <table class="table-no-border-no-cell-spacing-no-cell-padding jobtype-status-table ">
	  <tr>
	   <td>JobType:</td>
	   <td><span id="job_type_name"></span></td>
	  </tr>
	  <tr>
	   <td>Status:</td>
	   <td ><span id="job_status"></span></td>
	  </tr>
	  <tr>
	   <td></td>
	   <td></td>
	  </tr>
	 </table>
	 

	<div id="requeue_job_block" style="display: none;" class="main-job-value-line" >
		<a href="javascript:" id="requeue_link" >
			Requeue Job
		</a>
	</div>
	<div id="cancel_job_block" style="display: none;" class="main-job-value-line" >
		<a href="javascript:" id="cancel_link" >
			Cancel Job
		</a>
	</div>

	<div class="main-job-value-line" >
		Update Job Priority (Not supported yet)
	  	<input type="text" id="new_job_priority">
	    <input type="button" value="Update Priority" id="update_priority_button" >
	</div>


<table border="1" cellpadding="5" cellspacing="0">

  <tr>
	<th>id</th>
	<th>priority</th>
	<th>submitter</th>
	<th style="white-space: nowrap;">submit date</th>
  </tr>
  <tr>
	<td>
		<span id="job_id"></span>
	</td>
	<td>
		<span id="job_priority"></span>
	</td>
	<td>
		<span id="job_submitter"></span>
	</td>
	<td style="white-space: nowrap;">
		<span id="job_submit_date"></span>
	</td>
  </tr>
</table>


<h3>Job parameters</h3>

	<%-- Style for job entries --%>
	<style >
		.main-job-value-line { margin-bottom: 5px; }
		.job-parameter-table { padding: 0px; border-collapse: collapse; }
		.job-parameter-table td { padding: 4px; border-spacing : 0px; border-color: grey; border-style: solid; border-width: 1px; }

	</style>

  <table id="job_parameter_table" class="job-parameter-table" >
  </table>
  
<script type="text/text" id="job_parameter_template">
	 <tr>
		<td nowrap="nowrap">
			{{ paramKey }}
		</td>

		<td nowrap="nowrap">
			{{ paramValue }}
		</td>

  	  </tr>
</script>

<h2>Runs</h2>
runs listed in reverse id order

<table id="run_list_table" border="1" cellpadding="5" cellspacing="0">

 <thead>
  <tr>
	<th>id</th>
	<th style="white-space: nowrap;" >status name</th>
	<th>Start Date</th>
	<th>End Date</th>
	<th>node</th>
  </tr>
 </thead>
 <tbody>
 </tbody>
</table>

	
	<script id="run_entry_template"  type="text/x-handlebars-template" >
		<%@ include file="/WEB-INF/jsp-includes/runEntryOnViewJobTemplate.jsp" %>
	</script>	

