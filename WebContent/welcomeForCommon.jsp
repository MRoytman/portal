<%@include file="./headerForCommon.jsp"%>
 <%
     String username = (String) session.getAttribute("username");
     if(username != null){
         out.println("<div style=\"float: right; margin-top: -90px; margin-right: 10px;\"><h3>Welcome "
                 + username
                 + "!<br><a href=\"./logout.jsp\">Logout</a></h3></div>");
     }else{
         response.sendRedirect("index.jsp");
     }
 %>
