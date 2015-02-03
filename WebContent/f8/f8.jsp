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

<form action="../CreateForm" id="newForm" name="newForm" method="post">
<input type="hidden" name="msf_language" value="en">
<h2>AGGREGATED THEME</h2>
<%@include file="./section1.jsp" %>
<%@include file="./section2.jsp" %>
<%@include file="./section3.jsp" %>
<%@include file="./section4.jsp" %>
<%@include file="./section5.jsp" %>
<%@include file="./section6.jsp" %>
<%@include file="./section7.jsp" %>
<%@include file="./section8.jsp" %>

<hr>
<a href="../f0.jsp"><input type="button"  value="Back" ></a>
<input type="submit"  value="Confirm"></form>
</body>
<%@include file="../footer2.jsp" %>
</html>