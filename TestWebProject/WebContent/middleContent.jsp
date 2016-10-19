<%@page import="java.util.Set"%>
<%@page import="logAPI.LoggerUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<style>
nav {
    float: left;
    max-width: 160px;
    margin: 0;
    padding: 1em;
}

nav ul {
    list-style-type: none;
    padding: 0;
    color:black;
}
   
nav ul a {
    text-decoration: none;
}
@import "http://fonts.googleapis.com/css?family=Raleway";
/*----------------------------------------------
CSS settings for HTML div Exact Center
------------------------------------------------*/
.abc {
width:50%;
height:60%;
opacity:.85;
top:20;
left:250px;
display:none;
position:fixed;
background-color:white;
overflow:auto
}
.close {
position:absolute;
left:250px;
top:20;
width:20px;
height:20px;
cursor:pointer
}
.popupContact {
position:absolute;
left:50%;
top:17%;
margin-left:-202px;
font-family:'Raleway',sans-serif;
background-color:white; 
}




span {
color:red;
font-weight:700
}

</style>
<script>
	//Function To Display Popup
	function div_show(id) {
	$('#popup_'+id).show();
	var content = $('#LineChartContainer__'+id).text();
	console.log(content);
	if(id == "mostUsedUrls"){
		var html = $("#mostCalledUrls").html();
		$('#popup_'+id+"_cont").html(html);
	}
	 if(content.indexOf("Please wait the chart will be loaded...")!=-1){
		loadChart('_'+id,"popup");
	} 
	}
	//Function to Hide Popup
	function div_hide(id){
		$('#popup_'+id).hide();
	}

</script>
<body>


<div style="float:left; width:80%;"><%@ include file="GetCount.jsp" %></div>
<div style="float:right; width:20%;display:none " id="rightContent">
<br><br>
<div>
<center><b>Other Critical Urls</b></center>
<nav>
  <ul>
	<%
	Set<String> keySet = LoggerUtil.lessCriticalUrl.keySet();
	for(String key:keySet){
		
		String keyID = key.replace(".","_");
		keyID = keyID.replace("/", "");
		String param = LoggerUtil.lessCriticalUrl.get(key);
		if(param!=null){
			key = key + " " +param;
		}
		%>
		<li><button id="button_<%=keyID%>" class="button" onclick="div_show('<%=keyID%>')"><%=key%></button></li>
		
	<%} %>
  </ul>
</nav>
</div>

<% 
for(String key:keySet){
		String keyID = key.replace(".","_");
		keyID = keyID.replace("/", "");
		%>
		<div id="popup_<%=keyID%>" class="abc">
			<!-- Popup Div Starts Here -->
			<div id="popupContact_<%=keyID%>" class="popupContact">
				<b> Response Time Chart for the URl "<%=key%>" </b>
				<img id="close_<%=keyID%>" class="close" src="download.png" onclick ="div_hide('<%=keyID%>')">
				<div id='LineChartContainer__<%=keyID%>' style='min-width: 600px; height: 400px; margin: 0 auto'><font color='green'>Please wait the chart will be loaded....</font></div>
				
					
				</div>
			
				</div>		
			<%} %>
	
	

<div id="mostCalledUrls" onclick ="div_show('mostUsedUrls')">
		
	</div>
	
<div id="popup_mostUsedUrls" class="abc">
			<!-- Popup Div Starts Here -->
			<div id="popupContact_mostUsedUrls" class="popupContact">
				<img id="close_mostUsedUrls" class="close" src="download.png" onclick ="div_hide('mostUsedUrls')">
				<div id="popup_mostUsedUrls_cont"></div>
				
					
				</div>
			
				</div>	
				</div>

</body>
</html>