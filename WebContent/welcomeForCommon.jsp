<%@include file="./headerForCommon.jsp"%>
 <%
     String username = (String) session.getAttribute("username");
     if(username != null){
         String user = (String) session.getAttribute("userType");
         String txt = "Field Data User";
         if(user.equals("isAdmin")){
             txt = "Admin";
         }
         out.println("<div style=\"float: right; margin-top: -90px; margin-right: 10px;\">"
                    +     "<h3 style=\"text-align: center;\">Welcome, " + txt + "!"
                    +         "<br>"
                    +         "<a href=\"./logout.jsp\">Logout</a>"
                    +     "</h3>"
                    + "</div>");
     }else{
        // Get back URL
        String backURL = request.getRequestURL().toString();
        session.setAttribute("backURL", backURL);
        response.sendRedirect("login.jsp");
     }
 %>
