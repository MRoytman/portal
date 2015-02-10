<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>MSF| Choose Form</title>

</head>
    <script type="text/javascript">
        function goBack(){
            window.history.back();
        }
    </script>
<%@include file="./isAdmin.jsp" %>
<%@include file="./welcomeForCommon.jsp" %>
<%@include file="./countryNameLabel.jsp"%>
<center>
	<%@include file="./bigger.jsp"%>
	<form action="CreateGeneral" method="post">


		<table>
			<tr>
				<td><u><strong>PATIENT LEVEL</strong></u></td>
				<td><strong>select</strong></td>
			</tr>
			<tr>
				<td>Epidemics</td>
				<td><input type="checkbox" name="selectForm" value="f1"><input
					type="hidden" name="f1" value="EPID"></td>
			</tr>
			<tr>
				<td>Mental Health</td>
				<td><input type="checkbox" name="selectForm" value="f2"><input
					type="hidden" name="f2" value="OPD"></td>
			</tr>
			<tr>
				<td>Hospital IPD</td>
				<td><input type="checkbox" name="selectForm" value="f3"><input
					type="hidden" name="f3" value="IPD"></td>
			</tr>
			<tr>
				<td>NCD</td>
				<td><input type="checkbox" name="selectForm" value="f4"><input
					type="hidden" name="f4" value="CNCD"></td>
			</tr>
			<tr>
				<td>SGBV</td>
				<td><input type="checkbox" name="selectForm" value="f5"><input
					type="hidden" name="f5" value="SGBV"></td>
			</tr>
			<tr>
				<td>Nutrition</td>
				<td><input type="checkbox" name="selectForm" value="f6"><input
					type="hidden" name="f6" value="NUT"></td>
			</tr>
			<tr>
				<td>TB / HIV</td>
				<td><input type="checkbox" name="selectForm" value="f7"><input
					type="hidden" name="f7" value="HIV"></td>
			</tr>
			<tr>
				<td><br>
				<u><strong>AGGREGATE LEVEL</strong></u></td>
				<td></td>
			</tr>
			<tr>
				<td>General OPD</td>
				<td><input type="checkbox" name="selectForm" value="f8"><input
					type="hidden" name="f8" value="AGG_OPD"></td>
			</tr>
			<tr>
				<td>Vaccination</td>
				<td><input type="checkbox" disabled="disabled"
					name="selectForm" value="f9"><input enable='1'
					type="hidden" name="f9" value="AGG_VAC"></td>
			</tr>
			<tr>
				<td>Nutrition</td>
				<td><input type="checkbox" name="selectForm" value="f10"><input
					type="hidden" name="f10" value="AGG_NUT"></td>
			</tr>

			<tr>
				<td>
                    <input type="button" value="Back" onclick="goBack();">
                    <input type="submit" value="Confirm">
                </td>
				<td></td>
			</tr>
		</table>
	</form>
</center>

<%@include file="./footer.jsp"%>
</html>
