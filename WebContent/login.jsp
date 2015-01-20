
<div style="background-color: #E5E5E5;height: 120px;"><br>
<img id='msfLogo' src="./img/head_logo_msf.gif" alt="head_logo_msf.gif" /></div>
<%@include file="./bigger.jsp" %>
<br><br><br><br><br><br>

<form method="post" action="Login">
<table align="center">
<tr><td><h1>Username</h1></td><td><input style=" font-size: 30px; width:300px" type="text" name="user" value=""></td></tr>
<tr><td><h1>Password</h1></td><td><input type="password"  style=" font-size: 30px;; width:300px" name="pass"></td></tr>
<tr><td></td><td><input type="submit" value="Login"></td></tr>
<tr><%String strErr=(String)session.getAttribute("Error");
		if(strErr!=null){
		%>
		<%= strErr%>
		<%}
		%></tr>
</table>
</form>


<%@include file="./footer.jsp" %>