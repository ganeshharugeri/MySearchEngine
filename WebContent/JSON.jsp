<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.io.*, java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>G and G</title>
    </head>
    <body>
    <% 
    Date currenttime = new Date();
   out.print( "<div id=\"header\"><h4 align=\"right\"> DATE :" +currenttime.toString()+"</h2></div>");
    %>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
 <center>
                  <img src="images.png" alt="G & G Icon" style="width:250px;height:100px;"> <br>
  <form action="JSONformat" method="get"> 
 	              <h4>             JSON Search                   </h4> <input type="text" size="30" name="jsonsearch">  <br>
 	              <h4> Enter the number of documents to retrieve </h4> <input type="text" size="20" name="json_no_of_docs"> <br>
	 <input type="submit" size= "500" value="Search"  style="font-size: 14px;border:5px light black;background-color:white;color:grey;">
	 <!-- Advanced Crawling option redirects to Crawling.jsp file -->
	 <input type="reset" name= "clear" value="clear"  style="font-size: 14px;border:5px light black;background-color:white;color:grey;">
	  
	 <!--  <button"class=btn1" name="action"> Submit</button>-->
  </form> 
  <br>
   <br>
    
 </center>  
</body>
</html>