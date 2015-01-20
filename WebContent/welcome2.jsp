<div style="background-color: #E5E5E5;height: 120px;"><br>
<img id='msfLogo' src="../img/head_logo_msf.gif" alt="head_logo_msf.gif" />
 <%
    String username=(String)session.getAttribute("username");
    if(username!=null){
           out.println("<div style=\"float: right;\"><h3>Welcome "+username+"!<br><a href=\"../logout.jsp\">Logout</a></h3></div>");
    }
    else {
         response.sendRedirect("index.jsp");
    }
%>
</div>