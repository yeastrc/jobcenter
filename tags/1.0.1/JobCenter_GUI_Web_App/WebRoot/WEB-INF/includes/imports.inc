<%--
  This is the master import file.  This include should never be directly referenced,
  rather, should include this file and then that include put on the .jsp page
--%>
<%@ page language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

  <%--  fmt:formatDate  uses the formats for java.text.SimpleDateFormat  --%>
  
  <%--  fmt:formatNumber uses the formats for  java.text.DecimalFormat --%>


<%@ taglib prefix="html-el" uri="http://struts.apache.org/tags-html-el" %>
<%@ taglib prefix="logic-el" uri="http://struts.apache.org/tags-logic-el" %>
<%@ taglib prefix="bean-el" uri="http://struts.apache.org/tags-bean-el" %>

                              
<%@ taglib prefix="bean" uri="http://jakarta.apache.org/struts/tags-bean" %>
<%@ taglib prefix="html" uri="http://jakarta.apache.org/struts/tags-html" %>
<%@ taglib prefix="logic" uri="http://jakarta.apache.org/struts/tags-logic" %>
<%@ taglib prefix="nested" uri="http://jakarta.apache.org/struts/tags-nested" %>
<%-- 
<%@ taglib prefix="template" uri="http://jakarta.apache.org/struts/tags-template" %>
--%>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>

<%--
<%@ taglib uri="/WEB-INF/struts-bean.tld"      prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld"      prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld"     prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld"    prefix="tiles" %>
--%>

<%--
<%@ page import="org.yeastrc.straindb.constant.ServletConstants" %>
<%@ page import="org.yeastrc.straindb.constant.SystemConstants" %>
<%@ page import="org.yeastrc.straindb.constant.ApplicationResourceConstants" %>
<%@ page import="org.yeastrc.straindb.constant.DatabaseConstants" %>
--%>