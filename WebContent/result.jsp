<script type="text/javascript">
  var counter = 0;
  function addNew() {
	  //var newLabel = document.createTextNode("Village");
	  
      var newText = document.createElement("input");
      newText.setAttribute("type", "text");
      newText.setAttribute("name", "Village"+counter);
      newText.setAttribute("value", "");
     
      var newDelButton = document.createElement("input");
      newDelButton.setAttribute("type", "button");
      newDelButton.setAttribute("value", "[ - ]");
      var newBr=document.createElement("br");

      var form=document.getElementById("addField");
      form.appendChild(newText);
      form.appendChild(newDelButton);
      form.appendChild(newBr);
      
      counter++;
      newDelButton.onclick = function () {
    	  form.removeChild(newText);
    	  form.removeChild(newDelButton);
    	  form.removeChild(newBr);
      };
  }
  function setLstConfig()
  {
	  var param="";
	  var x = document.getElementById("newConfig");
	  for (var i=0; i<x.length; i++)
	  {
		  if(x.elements[i].getAttribute("name")!=null)
		  	param += x.elements[i].getAttribute("name")+"<msf_split>";
	  }
	  var form=document.getElementById("newConfig");
	  var paraml=document.createElement("input");
	  paraml.setAttribute("type", "hidden");
	  paraml.setAttribute("name", "lstFiledName");
	  paraml.setAttribute("value",param);
      form.appendChild(paraml);
  }
</script>
<%@include file="./bigger.jsp" %>
<%@include file="./welcomeForCommon.jsp" %>
<%@include file="./header2.jsp" %>
 <h1>Form Export</h1>
<%
String strLink=(String)session.getAttribute("urlJavaForm");
%>
 <h3>Java Form Link: <a href='<%=strLink %>'><%=strLink %></a> </h3>
 <h3>Send Form Via Email:
 <input type="button"  value="[+] Add new email" onclick="addNew()" /></h3>
<div id="addField"></div>
 <input  type='submit' value='Send Emails'><br>	
<%@include file="./footer.jsp" %>