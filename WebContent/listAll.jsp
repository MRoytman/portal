<%@page import="com.msf.form.CommonCode"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
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
        <link rel="stylesheet" href="css/listAll/listAll.css"/>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>MSF| List All</title>
    </head>
    <%@include file="./welcomeForCommon.jsp" %>
    <center>
        <%@include file="./bigger.jsp"%>
        <%
        // Get FormTypes enum
        FormType[] formTypes = FormType.values();
        
        // Create form map with key:{form_name}, value:{id}
        Map<String, String> formMap = new HashMap<String, String>();
        for(int i = 0; i < formTypes.length; i++){
            formMap.put(formTypes[i].getLabel(), String.valueOf(formTypes[i].getValue()));
        }

        // Create country map with key:{countryCode}, value:{countryName}
        BufferedReader rdCountry = new BufferedReader(new FileReader(session.getServletContext().getRealPath("/list_countries.csv")));
        Map<String, String> countryMap = new HashMap<String, String>();
        String lineCountry;
        while((lineCountry = rdCountry.readLine()) != null){
            // Example: AF   AFG Afghanistan Afghanistan Afganist�n  Asia    Southern Asia
            String[] strSplitCountry = lineCountry.split("\\t");
            String countryCode = strSplitCountry[0]; // Example: AF
            String countryName = strSplitCountry[2].toUpperCase(); // Example: Afghanistan
            countryMap.put(countryCode, countryName);
        }
        rdCountry.close();

        // Get userType
        String userType = (String) session.getAttribute("userType");
        boolean isResource = false;
        %>
        <p><font size="5"><strong>List of All Custom Tools By Activity Location</strong></font></p>
        <table class="CSSTableGenerator">
            <tr>
                <td>No</td>
                <td>Name</td>
                <td>Created at</td>
                <td>Custom Database Link</td>
                <td>Data Export Link</td>
                <td>Paper Form PDF Link</td>
        <%
                if("isAdmin".equals(userType)){
        %>
                <td>Modify</td>
        <%
                }
        %>
            </tr>
        <%

        String realPath = getServletContext().getRealPath("/");
        String dirFtpMsf = realPath.replace("MSFForm", "ftpmsf");

        File folder = new File(dirFtpMsf);
        File[] listOfFolders = folder.listFiles();

        int count = 1;
        for(int i = 0; i < listOfFolders.length; i++){
            File currentFolder = listOfFolders[i];
            if(currentFolder.getName().compareToIgnoreCase("logs") == 0){
                continue;
            }

            if(currentFolder.isDirectory()){
                String folderName = currentFolder.getName();
                String unixAppFolder = dirFtpMsf + folderName;
                String urlAppFolder =  "/ftpmsf/" + folderName;
                File appFolder = new File(unixAppFolder);
                Date dd = new Date(appFolder.lastModified());
                SimpleDateFormat sdfDestination = new SimpleDateFormat("ha, dd.MM.yyyy");
                // parse the date into another format
                String strDate = sdfDestination.format(dd);
                // Get form name, ex: NUT_LB => NUT
                String formName = folderName.split("_")[0];
                // Get country code, ex: NUT_LB => LB
                String countryCode = folderName.split("_")[1];

                // Get country name
                String countryName = "";
                if(countryMap.containsKey(countryCode)){
                    countryName = countryMap.get(countryCode);
                }
        %>
                <tr>
                    <td><%=count%></td>
                    <td class="name-col"><strong><%=countryName%> : <%=formName%><strong></td>
                    <td><%=strDate%></td>
        <%
                String dirAppLink = unixAppFolder + "/" + "instruction2_en.html";
                File fileAppLink = new File(dirAppLink);
        %>
                    <td>
        <%
                if(fileAppLink.exists()){
        %>
                        <a href="<%=urlAppFolder + "/" + "instruction2_en.html"%>" target="_blank">App Link</a>
        <%
                }
        %>
                    </td>
                    <td>
                        n/a
                    </td>
        <%
                String dirPDFLink = unixAppFolder + "/" + folderName + ".pdf";
                File filePDFLink = new File(dirPDFLink);
        %>
                    <td>
        <%
                if(filePDFLink.exists()){
        %>
                        <a href="<%=urlAppFolder + "/" + folderName + ".pdf"%>" target="_blank"><%=folderName%>.pdf</a>
        <%
                }
        %>
                    </td>
        <%
                if("isAdmin".equals(userType)){
                    if(formMap.containsKey(formName)){
                        String formNumber = formMap.get(formName);
        %>
                    <td>
                        <form action="CreateGeneral" method="post">
                            <input type="submit" class="btn-edit" value="Edit"/>
                            <input type="hidden" name="selectForm" value="f<%=formNumber%>">
                            <input type="hidden" name="f<%=formNumber%>" value="<%=formName%>">
                            <input type="hidden" name="appFolder" value="<%=folderName%>">
                            <input type="hidden" name="dirResources" value="<%=dirFtpMsf + folderName + "/resources/"%>">
                        </form>
                    </td>
        <%
                    }
                }
                isResource = true;
        %>
            </tr>
        <%
            count++;
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
        if("isAdmin".equals(userType)){
        %>
            <a href="./dataStorage.jsp">
                <input type="button" value="Back" class="btn-back"/>
            </a>
        <%
        }
        %>
    </center>
</html>
