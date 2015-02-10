<%@include file="./bigger.jsp" %>
<%@include file="./isAdmin.jsp" %>
<%@include file="./welcomeForCommon.jsp" %>
<!-- %@include file="../header2.jsp"%-->

<br><br>
<center>
<h3>The generation of the Java Form takes around 2 to 4 minutes. Please be patient!<br> The link to download the Java Form will be shown when the generation is done.<br> Thank you!
</h3>
<img src="./img/wait.png" alt="Please wait"/>
<form id="idGenerateJar" action="GenerateJar" method="post">
</form>
</center>
<%@include file="./footer.jsp" %>

<script type="text/javascript">
	document.getElementById("idGenerateJar").submit();
</script>