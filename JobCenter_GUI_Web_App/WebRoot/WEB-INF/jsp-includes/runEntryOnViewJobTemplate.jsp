<%--  runEntryOnViewJobTemplate.jsp  

	Handlebars Template for a single Run entry on View Job page
--%>

<tr>
	<td>{{ runId }}</td>
	<td>{{ status }}</td>
	<td>{{ startDate }}</td>
	<td>{{ endDate }}</td>
	<td>{{ node }}</td>
</tr>
<tr>

</tr>
  <td colspan="5">
  	<h3>Run Messages</h3>
	  
    <table border="1" cellpadding="5" cellspacing="0">
     <tr>
     	<th >run message type</th>
	    <th nowrap="nowrap">run message message</th>
     </tr>

	
		{{#each runMessageList }}
	
		 <tr>
			<td nowrap="nowrap">
			{{ runMsgType.name }}
			</td>
	
			<td >
			{{ message }}
			</td>
	
	  	  </tr>
	
		{{/each}}
	
	  </table>
	  
	
  </td>
</tr>
