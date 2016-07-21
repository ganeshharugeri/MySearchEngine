<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.io.*, java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>G and G</title>
</head>
<body>
	<!--Welcome Page with search field in the centre and displays current date at the top right -->
	<% 
    Date currenttime = new Date();
   out.print( "<div id=\"header\"><h4 align=\"right\">" +currenttime.toString()+"</h4></div>");
    %>
    <h3>
		<a style="float: right;" href="image.jsp">Bildersuche</a>
	</h3>
	<br></br>
	<br></br>
	<center>
		<img src="snow.gif" style="width: 250px; height: 70px;"> 
		<br/><img src="ggsnowfall.gif" alt="G & G Icon" style="width: 250px; height: 100px;">
		<!-- On submit sends response to Search_Servlet -->
		<form action="Search_Servlet_German" method="get">

			<input type="text" size="50" name="searchname"> <br> <input type="submit" size="500" value="Suche" style="font-size: 14px; border: 5px light black; background-color: white; color: black;">
			<!-- Advanced Crawling option redirects to Crawling.jsp file -->
			<input type="reset" name="clear" value="Löschen"
				style="font-size: 14px; border: 5px light black; background-color: white; color: black;">
			<h4>Suchen Sie auch angeboten in: <a href="index.jsp"> Englisch </a>
			</h4>
			<br>
		</form>

		<br></br>

	</center>
</body>
</html>