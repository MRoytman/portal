<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <%@include file="./bigger.jsp" %>
    <%@include file="./headerForCommon.jsp" %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Send email to IT-Helpdesk</title>
    </head>
    <body>
        <form action="SendMailAttachServlet" method="post" enctype="multipart/form-data">
            <table border="0" width="60%" align="center">
                <caption><h2>Send E-mail to IT-Helpdesk</h2></caption>
                <tr>
                    <td></td>
                    <td><div id="error"></div><td>
                </tr>
                <tr>
                    <td width="50%">Recipient Address:</td>
                    <td>
                        <input type="text" name="recipient" size="50"/>
                        <br>(for testing only, will be set to dsi-support.gva@geneva.msf.org once validated)
                    </td>
                </tr>
                <tr>
                    <td>Your mission / project:</td>
                    <td><input type="text" name="yourMissionProject" size="50"/></td>
                </tr>
                <tr>
                    <td>Your position:</td>
                    <td><input type="text" name="yourPostion" size="50"/></td>
                </tr>
                <tr>
                    <td>Subject:</td>
                    <td><input type="text" name="subject" size="50"/></td>
                </tr>
                <tr>
                    <td>Content:</td>
                    <td><textarea rows="12" cols="73" name="content"></textarea> </td>
                </tr>
                <tr>
                    <td>Attach File (optional):</td>
                    <td><input type="file" name="file" size="50" /></td>
                </tr>
                <tr>
                    <td colspan="2" align="center"><input type="button" value="Send" onclick="validate(this.form)"/></td>
                </tr>
            </table>
        </form>
    </body>
    <script type="text/javascript">
        function validate(form){
            var error = document.getElementById('error');
    
            // Check email incorrect
            if(!validateEmail(form.recipient.value.trim())){
                error.innerHTML = '<span style="color:#F00;font-weight:bold;margin:0 auto">Email incorrect</span>';
                //alert("Please fill in your email");
                form.recipient.focus();
                return;
            }
    
            // Check your mission / project empty
            if(form.yourMissionProject.value.trim() == ""){
                //alert("Please fill in your content");
                error.innerHTML = '<span style="color:#F00;font-weight:bold;margin:0 auto">Your mission / project not empty</span>';
                form.yourMissionProject.focus();
                return;
            }
    
            // Check your position empty
            if(form.yourPostion.value.trim() == ""){
                //alert("Please fill in your content");
                error.innerHTML = '<span style="color:#F00;font-weight:bold;margin:0 auto">Your postion not empty</span>';
                form.yourPostion.focus();
                return;
            }
    
             // Check subject empty
            if(form.subject.value.trim() == ""){
                //alert("Please fill in your title");
                error.innerHTML = '<span style="color:#F00;font-weight:bold;margin:0 auto">Subject not empty</span>';
                form.subject.focus();
                return;
            }
    
            // Check content empty
            if(form.content.value.trim() == ""){
                //alert("Please fill in your content");
                error.innerHTML = '<span style="color:#F00;font-weight:bold;margin:0 auto">Content not empty</span>';
                form.content.focus();
                return;
            }
    
            if(form.file.files.length != 0){
                // Check file is .exe
                var arrName = form.file.files[0].name.trim().split('.');
                var name = arrName[arrName.length - 1];
                if (name == 'exe') {
                    error.innerHTML = '<span style="color:#F00;font-weight:bold;margin:0 auto">Please choose file not *.exe</span>';
                    form.file.focus();
                    return;
                }
    
                // Check empty file
                if(form.file.files[0].size == 0){
                    error.innerHTML = '<span style="color:#F00;font-weight:bold;margin:0 auto">File is empty</span>';
                    form.file.focus();
                    return;
                }
    
                // Check file size less than 25M (megabytes)
                if(parseFloat(form.file.files[0].size / (1024 * 1024)) > 25){
                    error.innerHTML = '<span style="color:#F00;font-weight:bold;margin:0 auto">Please choose file size less than 25M (megabytes)</span>';
                    form.file.focus();
                    return;
                }
            }
            // Sumit form
            form.submit();
        }
        function validateEmail(email) {
            var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return re.test(email);
        }
    </script>
</html>
