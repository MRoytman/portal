<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <title>Login Form</title>
  <link rel="stylesheet" href="./css/login.css">
</head>
<img id='msfLogo' src="./img/head_logo_msf.gif" alt="head_logo_msf.gif" />
<body>
<section class="container">
    <div class="login">
    <p style = 'color:#FFOOOO'>
    	<%String strErr=(String)session.getAttribute("Error");
		if(strErr!=null){
		%>
		<%= strErr%>
		<%}
		%>
    </p>
      <form method="post" action="Login">
        <p><input type="text" name="user" value="" placeholder="Username"></p>
        <p><input type="password" name="pass" value="" placeholder="Password"></p>
        <p class="submit"><input type="submit" name="commit" value="Login"></p>
      </form>
    </div>
</section>
  <%@include file="./footer.jsp" %>
</body>
