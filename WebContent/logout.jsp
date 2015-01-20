 <%
 session = request.getSession(true);
 session.invalidate();
 response.sendRedirect("index.jsp");
%>