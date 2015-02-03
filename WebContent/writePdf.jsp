<%@include file="./bigger.jsp" %>
<%@include file="./welcomeForCommon.jsp" %>

<br><br>
<center>
    <form id="isWritePDFServlet" action="WritePDFServlet" method="post"></form>
</center>
<%@include file="./footer.jsp" %>

<script type="text/javascript">
    document.getElementById("isWritePDFServlet").submit();
</script>