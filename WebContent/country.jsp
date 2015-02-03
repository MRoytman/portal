<%@page import="java.io.File"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<script type="text/javascript" src="js/jquery-1.11.1.js" ></script>
<%@include file="./bigger.jsp" %>
<%@include file="./welcomeForCommon.jsp" %>

<script type="text/javascript">
        $(document).ready(function () {
            $('#countryID').val('LB<msf_value>Lebanon');
        });
</script>
<br><br><br><br><br><br><br>
<table  align="center">
<tr><td><h2>Select Country</h2></td><td>
<select name="country" form="frmcCountry" id="countryID" >
<%
//load list country from csv
	File dirCountry=new File(session.getServletContext().getRealPath("/list_countries.csv"));
	if (!dirCountry.exists()) {
		out.print("First time: need to copy the resources.");
	}else{
 		BufferedReader rdCountry = new BufferedReader(new FileReader(session.getServletContext().getRealPath("/list_countries.csv")));
	 	String lineCountry;
	 	String[] strSplitCountry;
		ArrayList<String> lstCode=new ArrayList<String>();
		ArrayList<String> lstLabel=new ArrayList<String>();
 	 	while((lineCountry = rdCountry.readLine())!= null){
 			strSplitCountry=lineCountry.split("	");
     	   	lstCode.add(strSplitCountry[0]);
     	   	lstLabel.add(strSplitCountry[2]);
	 	}
 	 	rdCountry.close();
	 	for(int i=0;i<lstCode.size();i++)
	 	{
	 		%>
	 		<option  value="<%=lstCode.get(i)+"<msf_value>"+lstLabel.get(i)%>"><%=lstLabel.get(i)%></option>
	 		<%
	 	}
	}
%>  
</select>
</td></tr>
<tr><td></td><td>
<form id="frmcCountry" method="post" action="ProjectSelection">
<input type="submit" value="Select"></form></td></tr>
</table>

<%@include file="./footer.jsp" %>