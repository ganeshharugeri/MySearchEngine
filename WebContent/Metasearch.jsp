<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.io.*, java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>GnG Metasearch</title>
</head>
<body>
	<!--Welcome Page with search field in the centre and displays current date at the top right -->
	<%
		Date currenttime = new Date();
		out.print("<div id=\"header\"><h4 align=\"right\">"
				+ currenttime.toString() + "</h2></div>");
	%>
	<!-- <h3>
		<a style="float: right;" href="Preferences.jsp">Preferences</a>
	</h3> -->
	<br></br>
	<br></br>
	<center>
		<img src="snow.gif" style="width: 250px; height: 70px;"> <br />
		<img src="ggsnowfall.gif" alt="G & G Icon"
			style="width: 250px; height: 100px;">
		<h4>Metasearch</h4>
		<!-- On submit sends response to Search_Servlet -->
		<form action="Metasearch_servlet" method="get">
			<input type="text" size="50" name="metasearchname"> <br>
			<input type="submit" size="500" value="Search"
				style="font-size: 14px; border: 5px light black; background-color: white; color: grey;">
			<input type="reset" name="clear" value="clear"
				style="font-size: 14px; border: 5px light black; background-color: white; color: grey;">
			<br> <br><font size =4.5><b> Preferences:</b> </font>
			<input TYPE=checkbox name="options" VALUE=1>Group1 
			<input TYPE=checkbox name="options" VALUE=2>Group2 
			<input TYPE=checkbox name="options" VALUE=3>Group3 
			<input TYPE=checkbox name="options" VALUE=4>Group4 
			<input TYPE=checkbox name="options" VALUE=5>Group5 
			<input TYPE=checkbox name="options" VALUE=6>Group6 
			<input TYPE=checkbox name="options" VALUE=7>Group7 
			<input TYPE=checkbox name="options" VALUE=8>Group8 
			<!--Select all check boxes  -->
			<br><input TYPE="checkbox" onClick="selectAll(this)" /> Select All<br />
		
			<!-- Function to check all check boxes -->
			<script language="JavaScript">
				function selectAll(source) {
					checkboxes = document.getElementsByName('options');
					for (var i = 0, n = checkboxes.length; i < n; i++) {
						checkboxes[i].checked = source.checked;
					}
				}
			</script>
		</form>
		<br> <br>
	</center>

</body>
</html>