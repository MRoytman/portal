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
<script type="text/javascript">
        function goBack(){
            window.history.back();
        }
</script>
</head>
<body>
<%@include file="../isAdmin.jsp" %>
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
<input type="button" value="Back" onclick="goBack();">
<input type="submit"  value="Confirm"></form>
</body>
</html>