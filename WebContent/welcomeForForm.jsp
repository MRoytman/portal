<%@include file="./headerForForm.jsp"%>
 <%
     String username = (String)session.getAttribute("username");
     if(username != null){
         out.println("<div style=\"float: right; margin-top: -90px; margin-right: 10px;\">"
                 +        "<h3>Welcome Field Data User!"
                 +            "<br>"
                 +            "<a href=\"../logout.jsp\" style=\"margin-left: 87px;\">Logout</a>"
                 +        "</h3>"
                 +    "</div>");
     }else{
         response.sendRedirect("index.jsp");
     }
 %>
