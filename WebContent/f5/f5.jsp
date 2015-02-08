<%@page import="java.io.File"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MSF</title>
<%@include file="./popup.jsp" %>
<%@include file="../bigger.jsp" %>
</head>
<body>

<%@include file="../welcomeForForm.jsp" %>
<%@include file="../header2.jsp" %>

<form action="../CreateForm" id="newForm" name="newForm" method="post" onsubmit="setLstParam()">
<input type="hidden" name="msf_language" value="en">
<h2><a  href="javascript:toggle('sessionPatient');">
PATIENT INFORMATION (show)</a></h2>
<div id="sessionPatient" style="display: none" href="javascript:toggle();">	
<%@include file="./patient.jsp" %>
</div>
<br>
<h2><a  href="javascript:toggle('sessionDisease');">
DISEASE INFORMATION (show)</a></h2>
<div id="sessionDisease" style="display: none" href="javascript:toggle();">	

<%@include file="./encounter1.jsp" %>
<%@include file="./encounter2.jsp" %>
<%@include file="./encounter3.jsp" %>
<%@include file="./encounter4.jsp" %>
<br>
</div>
<hr>
<a   href="../modify.jsp"><input type="button"  value="Back" ></a>
<input type="submit"  value="Confirm"></form>
</body>
<%@include file="../footer2.jsp" %>
</html>