<%@include file="./headerForForm.jsp"%>
 <%
     String username = (String)session.getAttribute("username");
     if(username != null){
         String user = (String) session.getAttribute("userType");
         String txt = "Field Data User";
         if(user.equals("isAdmin")){
             txt = "Admin";
         }
         out.println("<div style=\"float: right; margin-top: -90px; margin-right: 10px;\">"
                 +       "<h3 style=\"text-align: center;\">Welcome, " + txt + "!"
                 +            "<br>"
                 +            "<a href=\"../logout.jsp\" style=\"margin-left: 35px;\">Logout</a>"
                 +        "</h3>"
                 +    "</div>");
     }else{
         response.sendRedirect("index.jsp");
     }
 %>
