<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.io.*, java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>G and G</title>
</head>
<body>
	<!-- Advanced Crawling options provides user to enter Advanced option from GUI -->
	<% 
    Date currenttime = new Date();
   out.print( "<div id=\"header\"><h4 align=\"right\">" +currenttime.toString()+"</h2></div>");
    %>
	<center>
		<img src="images.png" alt="G & G Icon"
			style="width: 250px; height: 100px;"> <br>
		<!-- Redirects page to Readcrawleroptions servlet -->
		<form action="Readcrawleroptions" method="get">
			<h3>Advanced crawling options</h3>
			<h4>Host URL</h4>
			<input type="text" size="20" name="hosturl">
			<h4>Maximum depth</h4>
			<input type="text" size="20" name="maxdepth">
			<h4>Maximum number of documents</h4>
			<input type="text" size="20" name="max_no_of_doc">
			<h4>Do you wish to move out of the domain: y/n</h4>
			<input type="text" size="20" name="domain">
			<h4>Shingle size</h4>
			<input type="text" size="20" name="shing_size">
			<br><br><input type="Submit" size="500" value="Submit">
		</form>
	</center>
</body>
</html>