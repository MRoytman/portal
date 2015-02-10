<%
    String userAdmin = (String) session.getAttribute("userType");
    if(!userAdmin.equals("isAdmin")){
        response.sendRedirect("index.jsp");
    }
%>