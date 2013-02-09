<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
   "http://www.w3.org/TR/html4/strict.dtd">
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>


<%@ include file="/WEB-INF/includes/imports.inc" %>

<tiles:importAttribute/>
<html:html lang="en">
<head>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="-1">
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">




<title>JobCenter Maintenance</title>

 <link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/global.css">

<script language="JavaScript">

function onLoadMessage()
{
	i = 0;
}
var buttonClicked = false;
function doubleClickCheck(form)
{
    if(buttonClicked == false)
   {
       buttonClicked = true;
       return true;
    }
    return false;
}
</script>

</head>



<body leftmargin="10" topmargin="0" marginheight="0" marginwidth="0" onLoad="onLoadMessage();"   > <%-- style="font-size:6pt;" --%>
<%-- style="margin-left:20px;font-size:14pt;background-color:#000960;color:#FFFFFF;" --%>

 <script type="text/javascript" src="${contextPath}/js/wz_tooltip/wz_tooltip.js"></script>

<!-- Server URL <c:out value="${ serverURL }" ></c:out>  -->
<a name="top" ></a>

<tiles:insert attribute="header" ignore="true"/>

<table border="0" cellpadding="0" cellspacing="0" width="950">

  <tr height="500" valign="top">
    <td width="950" valign="top">
      <table border="0" cellpadding="10" cellspacing="0" width="100%">


<%--   Old errors output
        <tr>
          <td><span style="color:red;" >

          <html:errors/>

<logic:messagesPresent message="true">
  <html:messages id="msg" message="true">
      <bean:write name="msg" filter="false" />
  </html:messages>
</logic:messagesPresent>


          </span></td>
        </tr>
--%>

      <logic:present name="org.apache.struts.action.ERROR">

        <tr>
          <td>
          	<center>
          	  <div class="error_header" style="width:500px; ">
          	  	<h2 class="contentBoxHeader">Errors</h2>
          	  </div>
          	  <div class="error" style="width:500px;" align="left">

 	         	<html:errors/>

 	          </div>
 	        </center>

          </td>
        </tr>
      </logic:present>


	<logic:messagesPresent message="true">
	        <tr>
	          <td>

          	<center>
          	  <div class="error_header" style="width:500px; "><h2 class="contentBoxHeader">Errors</h2></div><div class="error" style="width:500px;" align="left">

 	         	<html:errors/>

	  <html:messages id="msg" message="true">
	      <bean:write name="msg" filter="false" />
	  </html:messages>

 	          </div>
 	        </center>

	          </td>
	        </tr>
	</logic:messagesPresent>



        <tr>
          <td>

<tiles:insert attribute="content" ignore="true"/>
         </td>
       </tr>
      </table>
    </td>
  </tr>
</table>

<!--end main content area-->
</body>
</html:html>
