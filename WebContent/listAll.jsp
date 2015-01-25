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
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>

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
		<p><font size="5"><strong>List of existing Java Forms and PDF files for downloading</strong></font></p>
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
                }
                String unixAppFolder = dirFtpMsf + currentFolder.getName();
                String urlAppFolder =  "/ftpmsf/" + currentFolder.getName();
                File appFolder = new File(unixAppFolder);
                Date dd = new Date(appFolder.lastModified());
                SimpleDateFormat sdfDestination = new SimpleDateFormat("ha, dd.MM.yyyy");
                // parse the date into another format
                String strDate = sdfDestination.format(dd);
                
                %>
                <tr>
                    <td><strong><%=currentFolder.getName()%><strong></td>
                    <td> Created at: <%=strDate%></td>
                    
                <%
                
                
                File[] listOfFiles = appFolder.listFiles();
                for(int j = 0; j < listOfFiles.length; j++){
                	String fileName = listOfFiles[j].getName();
                    if(listOfFiles[j].isFile()){
                       if(fileName.endsWith("instruction2_en.html")){
        %>
                           <td>
                               <a href="<%=urlAppFolder + "/" + "instruction2_en.html"%>" target="_blank">
                                   App Link
                               </a>
                           </td>
                           <td></td>
                           <td></td>
        <%
                       }
                       if(fileName.endsWith("pdf")){
        %>
                              <td>
                                   <a href="<%=urlAppFolder + "/" + fileName%>" target="_blank">
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
