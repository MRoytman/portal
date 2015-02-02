<%@page import="java.io.File"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="./popup.jsp" %>
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
            String ss0="Patient";
            String ss1="Neonatology";
            String ss2="Paediatrics";
            String ss3="Medicine";
            String ss4="Delivery";
            String ss5="Abortion";
            String ss6="Emergency Room";
            String ss7="Surgery";

            String fileName0=jspPath+"/Patient-FieldsLabel.txt";
            String fileName1=jspPath+"/Encounterneonatology-FieldsLabel.txt";
            String fileName2=jspPath+"/Encounterpaediatrics-FieldsLabel.txt";
            String fileName3=jspPath+"/Encountermedicine-FieldsLabel.txt";
            String fileName4=jspPath+"/Encounterdelivery-FieldsLabel.txt";
            String fileName5=jspPath+"/Encounterabortion-FieldsLabel.txt";
            String fileName6=jspPath+"/Encounteremergency_room-FieldsLabel.txt";
            String fileName7=jspPath+"/Encountersurgery-FieldsLabel.txt";


           BufferedReader reader;
           StringBuilder sb;
           String line;
           String strLabel;
           sb= new StringBuilder();



           out.print("<strong><a  href=\"javascript:toggle('"+ss0+"');\"><h2>"+ss0+"</h2></a></strong>");
           out.print("<div id='"+ss0+"' style=\"display: none\" href=\"javascript:toggle();\"><h3>");
           reader= new BufferedReader(new FileReader(fileName0));
           while((line = reader.readLine())!= null){
               strLabel=line.split("\t")[1].toString().trim();
               if("".compareToIgnoreCase(strLabel.trim())!=0)
                       sb.append("<br>"+strLabel);
               reader.readLine();//ignore line fr

           }
           out.print(sb.toString());
           out.print("</h3></div>");
           reader.close();


           if("1".compareToIgnoreCase(session.getAttribute("encounter1").toString())==0){
               sb= new StringBuilder();
               out.print("<strong><a  href=\"javascript:toggle('"+ss1+"');javascript:collapse('"+ss0+"');\"><h2>"+ss1+"</h2></a></strong>");
               out.print("<div id='"+ss1+"' style=\"display: none\" href=\"javascript:toggle();\"><h3>");
               reader= new BufferedReader(new FileReader(fileName1));
               while((line = reader.readLine())!= null){
                   strLabel=line.split("\t")[1].toString().trim();
                   if("".compareToIgnoreCase(strLabel.trim())!=0)
                           sb.append("<br>"+strLabel);
                   reader.readLine();//ignore line fr
               }
               out.print(sb.toString());
               out.print("</h3></div>");
               reader.close();
           }

           if("1".compareToIgnoreCase(session.getAttribute("encounter2").toString())==0){
               sb= new StringBuilder();
               out.print("<strong><a  href=\"javascript:toggle('"+ss2+"');javascript:collapse('"+ss1+"');\"><h2>"+ss2+"</h2></a></strong>");
               out.print("<div id='"+ss2+"' style=\"display: none\" href=\"javascript:toggle();\"><h3>");
               reader= new BufferedReader(new FileReader(fileName2));
               while((line = reader.readLine())!= null){
                   strLabel=line.split("\t")[1].toString().trim();
                   if("".compareToIgnoreCase(strLabel.trim())!=0)
                           sb.append("<br>"+strLabel);
                   reader.readLine();//ignore line fr
               }
               out.print(sb.toString());
               out.print("</h3></div>");
               reader.close();
           }
           if("1".compareToIgnoreCase(session.getAttribute("encounter3").toString())==0){
               sb= new StringBuilder();
               out.print("<strong><a  href=\"javascript:toggle('"+ss3+"');javascript:collapse('"+ss2+"');\"><h2>"+ss3+"</h2></a></strong>");
               out.print("<div id='"+ss3+"' style=\"display: none\" href=\"javascript:toggle();\"><h3>");
               reader= new BufferedReader(new FileReader(fileName3));
               while((line = reader.readLine())!= null){
                   strLabel=line.split("\t")[1].toString().trim();
                   if("".compareToIgnoreCase(strLabel.trim())!=0)
                           sb.append("<br>"+strLabel);
                   reader.readLine();//ignore line fr
               }
               out.print(sb.toString());
               out.print("</h3></div>");
               reader.close();
           }
           if("1".compareToIgnoreCase(session.getAttribute("encounter4").toString())==0){
               sb= new StringBuilder();
               out.print("<strong><a  href=\"javascript:toggle('"+ss4+"');javascript:collapse('"+ss3+"');\"><h2>"+ss4+"</h2></a></strong>");
               out.print("<div id='"+ss4+"' style=\"display: none\" href=\"javascript:toggle();\"><h3>");
               reader= new BufferedReader(new FileReader(fileName4));
               while((line = reader.readLine())!= null){
                   strLabel=line.split("\t")[1].toString().trim();
                   if("".compareToIgnoreCase(strLabel.trim())!=0)
                           sb.append("<br>"+strLabel);
                   reader.readLine();//ignore line fr
               }
               out.print(sb.toString());
               out.print("</h3></div>");
               reader.close();
           }
           if("1".compareToIgnoreCase(session.getAttribute("encounter5").toString())==0){
               sb= new StringBuilder();
               out.print("<strong><a  href=\"javascript:toggle('"+ss5+"');javascript:collapse('"+ss4+"');\"><h2>"+ss5+"</h2></a></strong>");
               out.print("<div id='"+ss5+"' style=\"display: none\" href=\"javascript:toggle();\"><h3>");
               reader= new BufferedReader(new FileReader(fileName5));
               while((line = reader.readLine())!= null){
                   strLabel=line.split("\t")[1].toString().trim();
                   if("".compareToIgnoreCase(strLabel.trim())!=0)
                           sb.append("<br>"+strLabel);
                   reader.readLine();//ignore line fr
               }
               out.print(sb.toString());
               out.print("</h3></div>");
               reader.close();
           }
           if("1".compareToIgnoreCase(session.getAttribute("encounter6").toString())==0){
               sb= new StringBuilder();
               out.print("<strong><a  href=\"javascript:toggle('"+ss6+"');javascript:collapse('"+ss5+"');\"><h2>"+ss6+"</h2></a></strong>");
               out.print("<div id='"+ss6+"' style=\"display: none\" href=\"javascript:toggle();\"><h3>");
               reader= new BufferedReader(new FileReader(fileName6));
               while((line = reader.readLine())!= null){
                   strLabel=line.split("\t")[1].toString().trim();
                   if("".compareToIgnoreCase(strLabel.trim())!=0)
                           sb.append("<br>"+strLabel);
                   reader.readLine();//ignore line fr
               }
               out.print(sb.toString());
               out.print("</h3></div>");
               reader.close();
           }
           if("1".compareToIgnoreCase(session.getAttribute("encounter7").toString())==0){
               sb= new StringBuilder();
               out.print("<strong><a  href=\"javascript:toggle('"+ss7+"');javascript:collapse('"+ss6+"');\"><h2>"+ss7+"</h2></a></strong>");
               out.print("<div id='"+ss7+"' style=\"display: none\" href=\"javascript:toggle();\"><h3>");
               reader= new BufferedReader(new FileReader(fileName7));
               while((line = reader.readLine())!= null){
                   strLabel=line.split("\t")[1].toString().trim();
                   if("".compareToIgnoreCase(strLabel.trim())!=0)
                           sb.append("<br>"+strLabel);
                   reader.readLine();//ignore line fr
               }
               out.print(sb.toString());
               out.print("</h3></div>");
               reader.close();
           }


    }
    String getURL=request.getRequestURL().toString();
    //../GenerateJar
%>

<div style="display: inline-block;">
    <a href="./f3.jsp"><input type="button" value="Back"></a>
</div>
<div style="display: inline-block;">
    <form action="../waiting.jsp" id="newJar" name="newJar" method="post">
        <input type="submit" value="Generate Java Form">
    </form>
</div>
<div style="display: inline-block;">
    <form action="../WritePDFServlet" method="post">
        <input type="hidden" name="pathPDF" value="/deployf3/resources/">
        <input type="hidden" name="htmlBack" value='<a href="<%=getURL%>"><input type="button" value="Back" ></a>'>
        <input type="submit" value="Generate PDF">
    </form>
</div>

<%@include file="../footer2.jsp" %>