<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Metasearch Preferences</title>
</head>
<body>

<!-- <form action="Metasearch_servlet" id="prferenceList" method="get">
 -->
 <form  method="post" action="Metasearch_servlet" id="prferenceList" >

        <input TYPE=checkbox name="options[]" VALUE=Group1 checked>Group1
        <button>delete</button>
         <input TYPE=checkbox name="options[]" VALUE=Group2 >Group2
        <button>delete</button>
         <input TYPE=checkbox name="options[]" VALUE=Group3 checked>Group3
        <button>delete</button>
         <input TYPE=checkbox name="options[]" VALUE=Group4 checked>Group4
        <button>delete</button>
         <input TYPE=checkbox name="options[]" VALUE=Group5 >Group5
        <button>delete</button>
	<br> <INPUT TYPE=submit name=submit Value="Submit">
    
</form>

<script>
function removeOption() {
    var x = document.getElementById("mySelect");
    x.remove(x.selectedIndex);
}
</script>

</body>
</html>