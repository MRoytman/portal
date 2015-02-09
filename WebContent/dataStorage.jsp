<%@page language="java" contentType="text/html; charset=ISO-8859-1" 
pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>MSF| Choose Form</title>
    </head>
    <%
    String userType = (String)session.getAttribute("userType");
    %>
    <%@include file="./welcomeForCommon.jsp" %>
    <center>
        <%@include file="./bigger.jsp"%>
        <br>
        <%
        if(userType.equals("isAdmin")){
        %>
        <a href="./modify.jsp">
            <input type="submit" value="Modify"/>
        </a>
        <%
        }
        %>
        <a href="./listAll.jsp">
            <input type="submit" value="List all"/>
        </a>
        <%
        if(userType.equals("isAdmin")){
        %>
        <a href="./country.jsp">
            <input type="submit" value="Generate"/>
        </a>
        <%
        }
        %>
    </center>
    <%@include file="./footer.jsp"%>
</html>
