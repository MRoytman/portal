<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script type="text/javascript" src="js/jquery-1.11.1.js" ></script>
<%@include file="./bigger.jsp" %>
<%@include file="./headerForCommon.jsp"%>
<%@include file="./bigger.jsp" %>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Result</title>
</head>
<body>
	<center>
		<h3>${requestScope.message}</h3>
	</center>
</body>

<%@include file="./footer.jsp" %>
</html>
