<%@page import="com.msf.form.CommonCode"%>
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="java.io.File"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.msf.form.data.FormType"%>
<%@page import="com.msf.form.CommonCode"%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>MSF| Choose Form</title>
    </head>
    <%@include file="./welcome.jsp"%>
    <center>
        <%@include file="./bigger.jsp"%>
        <%
        // Get formType
        boolean isResource = false;
        %>
        <table>
        <%
        
        String realPath = getServletContext().getRealPath("/");
        String dirFtpMsf = realPath.replace("MSFForm", "ftpmsf");
        
        File folder = new File(dirFtpMsf);
        File[] listOfFolders = folder.listFiles();
        
        for(int i = 0; i < listOfFolders.length; i++){
            File currentFolder = listOfFolders[i];
            if(currentFolder.getName().compareToIgnoreCase("logs") == 0){
                continue;
            }
            
            if(currentFolder.isDirectory()){
                if(!isResource){
        %>
            <tr>
                <td>
                    <u><strong>List of existing Java Forms and PDF files for download</strong></u>
                </td>
            </tr>
        <%
                }
        %>
            <tr>
                <td><%=currentFolder.getName()%></td>
        <%
                String dirFile = dirFtpMsf + currentFolder.getName();
                String dirFileX =  "/ftpmsf/" + currentFolder.getName();
                File file = new File(dirFile);
                File[] listOfFiles = file.listFiles();
                for(int j = 0; j < listOfFiles.length; j++){
                    if(listOfFiles[j].isFile()){
                       if(listOfFiles[j].getName().endsWith("instruction2_en.html")){
        %>
                           <td>
                               <a href="<%=dirFileX + "/" + "instruction2_en.html"%>">
                                   App Link
                               </a>
                           </td>
                           <td></td>
                           <td></td>
        <%
                       }
                       if(listOfFiles[j].getName().endsWith("pdf")){
        %>
                              <td>
                                   <a href="<%=dirFileX + "/" + listOfFiles[j].getName()%>">
                                       PDF File Link
                                   </a>
                               </td>
        <%
                       }
                    }
                }
                isResource = true;
        %>
            </tr>
        <%
            }
        }
        %>
        </table>
        <%
        if(!isResource){
        %>
            <h2>Resource not found</h2>
        <%
        }
        %>
        <a href="./dataStorage.jsp">
            <input type="button" value="Back"/>
        </a>
    </center>
    <%@include file="./footer.jsp"%>
</html>
