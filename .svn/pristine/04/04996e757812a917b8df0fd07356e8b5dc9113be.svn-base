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
   out.print( "<div id=\"header\"><h4 align=\"right\"> DATE :" +currenttime.toString()+"</h2></div>");
    %>
	<br>
	<br>
	<br>

 <center>
  <img src="images.png" alt="G & G Icon" style="width:250px;height:100px;"> <br>
  <!-- Redirects page to Readcrawleroptions servlet -->
  <form action="Readcrawleroptions" method="get"> 
  <br>
  <br>
   <h3> Advanced Crawling options</h3> <br>
   
  	<h4> Host URL                    </h4><input type="text" size="20" name="hosturl">  <br>
	<h4> Maximum Depth               </h4><input type="text" size="20" name="maxdepth"> <br>
	<h4> Maximum number of Documents </h4><input type="text" size="20" name="max_no_of_doc"> <br>
	<h4> Do you wish to move out of the Domain: y/n </h4><input type="text" size="20" name="domain">
	<br>
	 <input type="Submit" size= "500" value="Submit">
	  
  </form>

  
 </center>  
</body>
</html>