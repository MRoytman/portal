<%
    String _dirResources = (String) session.getAttribute("dirResources");
    // Check edit or add form
    if(_dirResources == null || _dirResources.length() == 0){ // Create new form
        String strCode = (String) session.getAttribute("CountryCode");
        String strCountry = (String) session.getAttribute("CountryName");
        if(strCode != null){
%>
            <center>
                <h2>Selected country:<%=strCountry%></h2>
            </center>
            <center>
                <h2>Selected form:<%=(String) session.getAttribute("typeForm")%></h2>
            </center>
<%
        }else{
            response.sendRedirect("index.jsp");
        }
    }
%>