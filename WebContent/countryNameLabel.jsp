<%
    String strCode = (String) session.getAttribute("CountryCode");
    String strCountry = (String) session.getAttribute("CountryName");
    if(strCode != null){
%>
    <center>
        <h2>Selected country: <%=strCountry%></h2>
    </center>
<%
    }else{
        response.sendRedirect("index.jsp");
    }
%>