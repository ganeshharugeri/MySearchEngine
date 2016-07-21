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
  <form action="Adregistration" method="get"> 
  <br>
  <br>
   <h2> AD Registration</h2> <br>
   
  	<h4> N-grams (Terms for which your AD should be displayed)</h4><textarea name="n-gram" cols="20" rows="3" ></textarea>
  	 <br>
  	 <h4> Title </h4><input type="text" size="20" name="title">
  	  <h4> Customer ID </h4><input type="text" size="20" name="custid">
  	 <h4> URL: </h4><input type="text" size="20" name="linkURL">
	<h4> Enter your AD information              </h4><textarea name="adinfo" cols="40" rows="5" ></textarea> <br>
	<h4> Total Budget </h4><input type="text" size="20" name="Budget"> <br>
	<h4> Image URL: (optional) </h4><input type="text" size="20" name="imageURL">
	<br>
	 <input type="Submit" size= "500" value="Submit">
	  
  </form>

  
 </center>  
</body>
</html>