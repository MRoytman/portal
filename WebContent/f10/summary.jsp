<%@page import="java.io.File"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../bigger.jsp" %>
<%@include file="../welcome2.jsp" %>
<%@include file="../header2.jsp" %>
<center><h2>Summary of selected fields</h2></center>
 <%
 	String jspPath = session.getAttribute("resourcePath").toString();
	File dirMasterFile=new File(jspPath);
	if (!dirMasterFile.exists()) {
		dirMasterFile.mkdirs();
		out.print("First time: need to copy the resources.");
	}else{
	 		String fileName0=jspPath+"/AggregatedTheme-nutrition-SectionLabels.txt";
           BufferedReader reader;
           StringBuilder sb;
           String line;
           String strLabel;
           sb= new StringBuilder();
           out.print("<strong><h2>Aggregated Theme:</h2></strong><h3>");
           reader= new BufferedReader(new FileReader(fileName0));
           while((line = reader.readLine())!= null){
        	   if("".compareToIgnoreCase(line.trim())!=0){
	        	   strLabel=line.split("\t")[1].toString().trim();
	        	   if("".compareToIgnoreCase(strLabel.trim())!=0)
	               		sb.append(strLabel+"<br>");
	        	   reader.readLine();//ignore line fr
        	   }
           }
           out.print(sb.toString());
           out.print("</h3>");
           reader.close();
	}
	String getURL=request.getRequestURL().toString();
	
%>
<hr>

	<table>
		<tr>
			<td><a href="./f10.jsp"><input type="button" value="Back"></a>
			</td>
			<td>
				<form action="../waiting.jsp" id="newJar" name="newJar" method="post">
					<input type="submit" value="Generate Java Form">
				</form>
			</td>
			<td>
				<form action="../WritePDFServlet" method="post">
					<input type="hidden" name="pathPDF" value="/deployf10/resources/">
					<input type="hidden" name="htmlBack" value='<a href="<%=getURL%>"><input type="button" value="Back" ></a>'>
					<input type="submit" value="Generate PDF">
				</form>
			</td>

		</tr>
	</table>

<%@include file="../footer2.jsp" %>