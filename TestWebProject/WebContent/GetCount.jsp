<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="https://s.graphiq.com/sites/default/files/1045/media/images/Zoho_Support__284963.jpg" type="image/x-icon" />
<title>Zoho Support Logging Console</title>
<style>
body{
	background-color: ivory;
}
#container{
	margin-left:100px;
}
input{
	margin:20px;
}
.button {
    background-color: #00ACDA;
    border: none;
    color: white;
    padding: 8px 30px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 16px;
    margin: 4px 2px;
    cursor: pointer;
    height:55px;
}
.prevNext{
	width:40px;
	height:55px;
	cursor:pointer
}
.prevnextarr{
	width:30px;
	height:40px;
	cursor:pointer
}
.alertbuttonGreen {background-color: #4CAF50;} /* Green */
.alertbuttonRed {background-color: #f44336;} /* Red */
table {
    border-collapse: collapse;
    width: 100%;
}

th, td {
    text-align: left;
    padding: 8px;
}

tr:nth-child(even){background-color: #f2f2f2}

th {
    background-color: #B1CA41;
    color: white;
}
</style>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.0/themes/base/jquery-ui.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.0/jquery-ui.js"></script>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
<script src="html2canvas.js"> </script>
<script type="text/javascript" src="js/jquery.plugin.html2canvas.js"></script>
<script>
	jQuery(function() {
		jQuery(".datepicker").datepicker({ dateFormat: 'dd/mm/yy' });
  	});
	var urlArray=new Array();
	var chartContent = new Array();
	function loadChart(url,mode){
		var urlArr = url.split("_");
		var reqUri = urlArr[1]+"."+urlArr[2];
		var query = 'logtype="access" and request_uri contains "'+reqUri+'"';
		if(urlArr.length>3){
			length = urlArr.length;
			var paramname = urlArr[length-3];
			var paramValue = urlArr[length-2];
			query += ' and params="'+paramname+'='+paramValue+'" ';
			
		}
		jQuery.ajax({url:'CallLogApi',
        	type:"GET",
        	cache:"true",
        	data : {
            	'query' : query,
             	'action' : 'populateChart',
        	},
        	success : function(result) {
        	if(mode != "popup"){
        	chartContent.push(result);
        	}
        	else{
        		populateLineChart(result,url);
        	}
        	}	
        });
	}
	$(function() { 
	    $("#btnSave").click(function() { 
	        html2canvas($("#widget"), {
	            onrendered: function(canvas) {
	                theCanvas = canvas;
	                document.body.appendChild(canvas);

	                // Convert and download as image 
	                Canvas2Image.saveAsPNG(canvas); 
	                $("#img-out").append(canvas);
	                // Clean up 
	                //document.body.removeChild(canvas);
	            }
	        });
	    });
	}); 

	function populateLineChart(result,url){
		var chartLoadEle =  $('#LineChartContainer_'+url)
		if(url == "dynamicChartLoad"){
		 chartLoadEle = $("#dynamicChartLoad");
		}
		var lastWeekCount = result.lastWeekTotalCount;
		var lastMonthCount = result.lastMonthTotalCount;
		var todayCount = result.todayTotalCount;
		var lastHourCount = result.lastHourTotalCount;
		var urlVal = $("#urlVal_"+url).val();
		$(function () {
			chartLoadEle.highcharts({
		        title: {
		            text: 'A comparitive Chart for the Url "'+urlVal+'" at different time interval',
		            x: -20 //center
		        },
		        subtitle: {
		            text: 'Source: logs.zoho.com',
		            x: -20
		        },
		        xAxis: {
		        	title: {
		                 text: 'Time Slot (in milli seconds)'
		             },
		            categories: ['0-10','10-20', '20-30', '30-40', '40-50', '50-60','70-80','80-90', '90-100', '100-200', '200-300', '300-400','400-500','500-600', '600-700', '700-800','800-900','900-1000','1000-2000','2000-3000', '3000-4000', '4000-5000','5000-1000','10000-50000']
		       
		       
		        },
		        yAxis: {
		            title: {
		                text: '% of Request served in the Time Slot'
		            },
		            plotLines: [{
		                value: 0,
		                width: 1,
		                color: '#808080'
		            }]
		        },
		        tooltip: {
		            crosshairs: [false, true],
		            valueDecimals: 0,
		            formatter: function () {
		                var s = '<b>' + this.x +'ms</b>';
		                var count =0;
		                console.log(this.points[0].series.name);
		                $.each(this.points, function () {
		                	switch (this.series.name){
		                	case 'Last Month' : count = lastMonthCount; break;
		                	case 'Last Week' : count = lastWeekCount; break;
		                	case 'Today' : count = todayCount; break;
		                	case 'Last Hour' : count =lastHourCount;break;
		                }
		                	 var countHere =((this.y * count)/100);
				                var dispCount = parseInt(countHere,10);
								s = s + "<br/>" + this.series.name +" : " + dispCount+"<br>" ; 
		                });
		                
		               
		                return s;
		            },
		            
		            shared: true
		        },
		        legend: {
		            layout: 'vertical',
		            align: 'right',
		            verticalAlign: 'middle',
		            borderWidth: 0
		        },
		        series: [{
		            name: 'Last Month',
		            data: result.lastMonthPercentArray
		        }, {
		            name: 'Last Week',
		            data: result.lastWeekPercentArray
		        },
		        {
		            name: 'Today',
		            data: result.todayPercentArray
		        },
		        {
		            name: 'Last Hour',
		            data: result.lastHourPercentArray
		        }
				]
		    });
		});
	}
	
	function toggleChartContainer(id){
		//var tobbleObj = $("#")
		/* if(toggleObj.is(":visible")){
			toggleObj.hide();
		}
		else{
			toggleObj.show();
		} */
		
		$("#LineChartContainer_"+id).toggle();
	}
	function handlechange(id){
		alert("into change");
		var value = $("#"+id).val();
		var count = $("#"+value).val();
		$("#countDisp").html(count);
	}
	function handleQuery(mode){
		var query=$("#query").val();
		console.log(mode);
		$("#labResult").html("<font size=\"4\" color=\"green\">Please Wait ....</font>");
		if(mode =="range"){
			var fromDate = $("#fromDate").val();
			var toDate = $("#toDate").val();
			if(fromDate != "" && toDate != ""){
				jQuery.ajax({url:'CallLogApi',
		        	type:"GET",
		        	cache:"true",
		        	data : {
		            	'query' : query,
		             	'action' : 'customQueryRange',
		             	'fromDate' : fromDate,
		             	'toDate' : toDate
		        	},
		        	success : function(result) {
		        	processResult(result,"labResult","query");
		        	}	
		        });
			}
			else{
				alert("Enter Valid Date..!")
			}
		}
		else if (mode =="singleDate"){
			var date = $("#singleDate").val();
			if(date != ""){
				jQuery.ajax({url:'CallLogApi',
		        	type:"GET",
		        	cache:"true",
		        	data : {
		            	'query' : query,
		             	'action' : 'customQueryDate',
		             	'Date' : date
		        	},
		        	success : function(result) {
		        		processResult(result,"labResult","query");
		       		}
				});
			}
			else{
				alert("Enter Valid Date..!")
			}
		}
		else if(mode =="Urange"){
			console.log("Into range");
			var fromDate = $("#UfromDate").val();
			var toDate = $("#UtoDate").val();
			jQuery.ajax({url:'CallLogApi',
	        	type:"GET",
	        	cache:"true",
	        	data : {
	             	'action' : 'customUserRange',
	             	'fromDate' : fromDate,
	             	'toDate' : toDate
	        	},
	        	success : function(result) {
	        	processResult(result,"UlabResult","urls");
	        	
	        }
		});
		}else if (mode =="UsingleDate"){
			var date = $("#UsingleDate").val();
			console.log("Into single date");
			console.log(date);
			jQuery.ajax({url:'CallLogApi',
	        	type:"GET",
	        	cache:"true",
	        	data : {
	             	'action' : 'customUserDate',
	             	'Date' : date
	        	},
	        	success : function(result) {
	        	console.log(result);
	        	processResult(result,"UlabResult","urls");
	        	
	        }
		});
			
		}
	}
	function renderResponseTimeAnalysis(){
		jQuery.ajax({url:'CallLogApi',
        	type:"GET",
        	cache:"true",
        	data : {
             	'action' : 'responseTimeAnalysis',
        	},
        	success : function(result) {
        	console.log(result);
        	processReponseTimeResult(result);
        	
        }
	});
	}
	function processReponseTimeResult(result){
		if(result.indexOf('Exception') > -1){
			console.log("into Exception");
			var message = "<font size='3' color='red'>"+result+"</font>";
			$("#"+divName).html(message);
		}
		else if(JSON.stringify(result).indexOf('error') > -1){ 
			console.log("into error");
			var message = "<font size='3' color='red'>"+result[0].error+"</font>";
			$("#"+divName).html(message);
		}
		else{
			var htmlContent = "";
			var needSound = false;
			for(var i=0;i<result.length;i++){
				var url = result[i].urlParam;
				var urlId = url.replace(/[^A-Z0-9]+/ig, "_");
				urlArray[i] = urlId;
				var trend = result[i].trend;
				var todayAvgResponseTime = result[i].todayAvgResponseTime;
				var lastWeekAvgResponseTime = result[i].lastWeekAvgResponseTime;
				var lastMonthAvgResponseTime = result[i].lastMonthAvgResponseTime;
				var lastHourAvgResponsetime = result[i].lastHourAvgResponseTime;
				var needSound = result[i].needSound;
				var lastMonthCount = result[1].lastMonthCount;
				var lastWeekCount = result[i].lastWeekCount;
				var todayCount = result[i].todayCount;
				var lastHourCount = result[i].lastHourCount;
				var mean = result[i].mean;
				var urlContent2= "<table border=0><tr><th>ResponseTimePeriod </th><th> Time(in millis)</th></tr><tr><td>Last Month Average Response Time</td><td> "+lastMonthAvgResponseTime+"</td></tr>";
				urlContent2+="<tr><td>Last Week Average Response Time </td><td>"+lastWeekAvgResponseTime+"</td></tr><tr><td>Today Average Response Time</td><td> "+todayAvgResponseTime+"</td></tr>";
				urlContent2+="<tr><td> Last Hour Response Time </td><td>"+lastHourAvgResponsetime+"</td></tr></table>";
				urlContent2+="<input type=\"hidden\" value="+lastMonthCount+" id=\"lastMonthCount_"+urlId+"\">";
				urlContent2+="<input type=\"hidden\" value="+lastWeekCount+" id=\"lastWeekCount_"+urlId+"\">";
				urlContent2+="<input type=\"hidden\" value="+todayCount+" id=\"todayCount_"+urlId+"\">";
				urlContent2+="<input type=\"hidden\" value="+lastHourCount+" id=\"lastHourCount_"+urlId+"\">";
				urlContent2+="<input type=\"hidden\" value="+url+" id=\"urlVal_"+urlId+"\">";
				urlContent2+="<div id='LineChartContainer_"+urlId+"' style=\"min-width: 1000px; height: 400px; margin: 0 auto;\"> <font color='green'>The detailed chart for the "+url+" will be loaded  here please wait</font></div>";
				if(trend == "DEC"){
					htmlContent += "<div id ='allCont_"+urlId+"'><br><center><button class=\"button alertbuttonGreen\" onclick=\"toggleChartContainer('"+urlId+"');\">"+url+"&#8595;</button><img src='download.jpeg' class='prevNext'></center>"+urlContent2+"<br><img src='previous.ico' class='prevnextarr' onclick='changeChart("+(i-1)+")'><img src='next.ico' class='prevnextarr' onclick='changeChart("+(i+1)+")'></div>";
				}
				else{
					htmlContent += "<div id ='allCont_"+urlId+"'><br><center><button class=\"button alertbuttonRed\" onclick=\"toggleChartContainer('"+urlId+"');\"').show();\">"+url+"&#8593;</button><img src='upArrow.gif' class='prevNext'></center>"+urlContent2+"<br><img src='previous.ico' class='prevnextarr' onclick='changeChart("+(i-1)+")'><img src='next.ico' class='prevnextarr' onclick='changeChart("+(i+1)+")'></div>";
				}
				var prevTrnd = $("#trend_"+urlId).val();
				if(prevTrnd !=undefined && prevTrnd != trend){
					needSound=true;
				}
				
			}
			
			$("#responseTimeResult").html(htmlContent);
			$("#responseTimeResult").hide();
			invokeSound();
			 for(var i=0;i<result.length;i++){
				var url = result[i].urlParam;
				var urlId = url.replace(/[^A-Z0-9]+/ig, "_");
				loadChart(urlId);
			} 
			 setInterval(changeChart, 100000);
		}
	}
	function changeChart(iter){
		console.log(iter);
		if(iter == -1 || iter > ((chartContent.length)-1)){
			alert('no items available');
			return;
		}
		if(iter == undefined){
		iter=0;
		var iterContainer = $("#chartIter");
		if($("#chartIter").length !=0 ){
			iter=parseInt(iterContainer.val());
		}
		}
		var nextIter = iter+1
		if(nextIter>(urlArray.length-1)){
			nextIter = 0;
		}
		console.log(nextIter);
		var urlId = urlArray[iter];
		var cont = "<input type='hidden'id='chartIter' value='"+nextIter+"'/> ";
		var chartLoad = $("#allCont_"+urlId).html();
		cont = cont+chartLoad;
		//$("#dynamicChartLoad").fadeOut("slow").html(cont);
		$("#dynamicChartLoad").html(cont);
		console.log(chartContent[iter]);
		if(chartContent[iter]!=undefined){
			populateLineChart(chartContent[iter], urlId)
		} 
		var needSound = $("#needSound_"+urlId).val();
		if(needSound == true || needSound=="true"){
			console.log("sound invoked");
			//invokeSound();
		}
		
		 $("#ResponseTimeAnalysis").hide();
		// $("#rightContent").hide();
	}
	
	function invokeSound(){
		/* var audio = new Audio('Si01.aif');
		audio.play(); */
		$('<audio id="chatAudio"><source src="bicycle_bell.wav" type="audio/wav"></audio>').appendTo('body');

			// play sound
			$('#chatAudio')[0].play();

	}
	function processResult(result,divName,mode){
		console.log(result);
		if(result.indexOf('Exception') > -1){
			console.log("into Exception");
			var message = "<font size='3' color='red'>"+result+"</font>";
			$("#"+divName).html(message);
		}
		else if(JSON.stringify(result).indexOf('error') > -1){ 
			console.log("into error");
			var message = "<font size='3' color='red'>"+result[0].error+"</font>";
			$("#"+divName).html(message);
		}
		else if(mode == "query"){
		var respString = "<font size='6'> Time Taken along with Particular Count for the given query in support labs</font><br><table border='1'><tr><th>Time Range(Milli Seconds)</th><th>Count</th><th>Percent</th><th>Cumulative Percent</th></tr>";
    	for(var i=0;i<result.length;i++){
    		var count = result[i].SupportLabCount;
    		var timeSlot = result[i].timeRange;
    		var percent = result[i].percent;
    		var cumPercent = result[i].cumPercent;
    		console.log(timeSlot + "====>" + count);
    		respString +="<tr><td>"+timeSlot+"</td><td>"+count+"</td><td>"+percent+"</td><td>"+cumPercent+"</td></tr>";
    	}
    	respString +="</table>"
    	$("#"+divName).html(respString);
		}
		else if(mode == "urls"){
			var respString = "<font size='6'> Top most Accessed urls for the given date / date Range</font><br><table border='1'><tr> <th>URL</th><th>Details</th><th>PortalList</th></tr>";
			for(var i=0;i<result.length;i++){
				var url = result[i].url;
				respString +="<tr><td>"+url+"</td><td><table border='1'><tr><th>TimeSlot</th><th>Count</th><th>Percent</th></tr>";
				var details = result[i].details;
				console.log("details length"+details.length);
				for(var j=0;j<details.length;j++){
					console.log("j value"+j);
					var count = details[j].count;
					var timeslot = details[j].timeRange;
					var percent = details[j].percent;
					respString +="<tr><td>"+timeslot+"</td><td>"+count+"</td><td>"+percent+"</td></tr>"
				}
				respString +="</table></td><td><table border='1'><tr><th>Portal Name</th><th>Count</th></tr>";
				var portalDeatils = result[i].portalDeatils;
				for(var j=0;j<portalDeatils.length;j++){
					var url = portalDeatils[j].url;
					var count = portalDeatils[j].count;
					respString +="<tr><td style='text-align:center'>"+url+"</td><td>"+count+"</td></tr>"
				}
				respString +="</table></td>";
			}
			respString +="</table>";
			$("#"+divName).html(respString);
		}
	}
	   
   
    $(document).ready(function(){
        
    $("#timeSlot").change(function() {
    var value = $(this).val();
    if(value == "custom"){
        $("#customRange").show();
    }
    else{
        $("#customRange").hide();
    }
   });
    
    $("#dirQuery :radio").click(function(){
    	 var $this = $('input[name=datesel]:checked');
    	 var val = ($this.val());
    	 if(val == "Range"){
    		 $("#Range").show();
    		 $("#dateQ").hide();
    	 }
    	 else if(val == "dateQ"){
    		 $("#Range").hide();
    		 $("#dateQ").show();
    	 }
    })
    $("#userdUrlOpt :radio").click(function(){
    	 var $this = $('input[name=dateselU]:checked');
    	 var val = ($this.val());
    	 if(val == "UserRange"){
    		 $("#UserRange").show();
    		 $("#UserdateQ").hide();
    	 }
    	 else if(val == "UserdateQ"){
    		 $("#UserRange").hide();
    		 $("#UserdateQ").show();
    	 }
    })
     $("#chooseOpt :radio").click(function(){
    	 var $this = $('input[name=optionOpt]:checked');
    	 var val = ($this.val());
    	 if(val == "dirQueryOpt"){
    		 $("#dirQueryOpt").show();
    		 $("#userdUrlOpt").hide();
    		 $("#ResponseTimeAnalysis").hide();
    		 $("#rightContent").hide();
    	 }
    	 else if(val == "userdUrlOpt"){
    		 $("#dirQueryOpt").hide();
    		 $("#userdUrlOpt").show();
    		 $("#ResponseTimeAnalysis").hide();
    		 $("#rightContent").hide();
    	 }
    	 else if(val == "ResponseTimeAnalysis"){
    		 $("#dirQueryOpt").hide();
    		 $("#userdUrlOpt").hide();
    		 renderResponseTimeAnalysis();
    		 $("#ResponseTimeAnalysis").show();
    		 $("#rightContent").show();
    		// setInterval(renderResponseTimeAnalysis, 1800000);
    		 getMostInvokedUrls();
    	 }
    })

});
  function getMostInvokedUrls(){
	  jQuery.ajax({url:'CallLogApi',
      	type:"GET",
      	cache:"true",
      	data : {
           	'action' : 'getMostUsedUrls',
      	},
      	success : function(result) {
      	console.log(result);
		renderMostUsedUrls(result);
      	
      }
	});
	  
  }
  function renderMostUsedUrls(result){
	  var htmlContent = "";
	  htmlContent+="<table><tr><th>Most accessed Urls Along with Count for the past Two Hours</th></tr><th> URL</th><th>Last Before Hour Count</th><th>Last Hour Count</th></tr>";
	  htmlContentMQ = "<br><p>Most accessed Urls Along with Count for the past Two Hours</p><br>";
	  for(var i=0;i<result.length;i++){
		  var url = result[i].url;
		  var lastBeforeHourCount = result[i].lastBeforeHourCount;
		  var lastHourCount = result[i].lastHourCount;
		  htmlContent+="<tr><td>"+url+"</td><td>"+lastBeforeHourCount+"</td><td>"+lastHourCount+"</td></tr>";
		  htmlContentMQ+="<table><tr><th> URL</th><th>Last Before Hour Count</th><th>Last Hour Count</th><tr>";
		  htmlContentMQ+="<tr><td>"+url+"</td><td>"+lastBeforeHourCount+"</td><td>"+lastHourCount+"</td></tr></table>";
	  }
	  htmlContent+="</table>";
	  htmlContentMQ+="</tr></table>";
	  console.log(htmlContent);
	  $("#mostCalledUrls").html(htmlContent);
  }
</script>
<body>
<br>
<!-- <div><button id="btnSave">Screen Shot Button</button> <div id ="widget"> <img src="next.ico"></div><div id="img-out"></div></div> -->
<div id="container">
<h3>Click On a Option to explore the Logging Console</h3>
<div id="chooseOpt">
<input type="radio" name="optionOpt" value="dirQueryOpt">Use Predefined Options
<input type="radio" name="optionOpt" value="userdUrlOpt">Get Most Used Urls
<input type="radio" name="optionOpt" value="ResponseTimeAnalysis"> ResponseTimeAnalysis
</div>
<br>

<div id="userdUrlOpt" style="display:none"> 

	<h3>Select the Option for Date
		<input type="radio" name="dateselU" value="UserRange">Date Range &nbsp;&nbsp;
		<input type="radio" name="dateselU" value="UserdateQ">Single Date
	</h3>
	<form id="UserRange" style="display:none">
		From Date : <input type="text" id="UfromDate" class="datepicker"/> (YYYY-MM-DD)
		&nbsp;&nbsp;
		To Date : <input type="text" id="UtoDate" class="datepicker"/>(YYYY-MM-DD)&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" class="button" value="Fetch Logs" onclick="handleQuery('Urange');">
	</form>
	<form id="UserdateQ" style="display:none">
		Date : <input type="text" id="UsingleDate" class="datepicker"/>(YYYY-MM-DD)&nbsp;&nbsp;&nbsp;&nbsp;
		
		<input type="button" class="button" value="Fetch Logs" onclick="handleQuery('UsingleDate');">
	</form>
<div id="UlabResult"></div>
</div>

<div id="dirQueryOpt" style="display:none">
<h2>Provide the Direct Query Which needs to be displayed with time slots</h2>
<form id="dirQuery">
	<div>
		<h3>Type Your Query Here : </h3><textarea style="width: 800px; height: 30px;" id="query">logtype="access" AND params="type=1" AND request_uri  CONTAINS "MDashboard.do"</textarea>
	</div>
	<h3>Select the Option for Date
		<input type="radio" name="datesel" value="Range">Date Range &nbsp;&nbsp;
		<input type="radio" name="datesel" value="dateQ">Single Date
	</h3>
</form>
	<form id="Range" style="display:none">
		From Date : <input type="text" id="fromDate" class="datepicker"/> (YYYY-MM-DD)
		&nbsp;&nbsp;
		To Date : <input type="text" id="toDate" class="datepicker"/>(YYYY-MM-DD)&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" class="button" value="Fetch Logs" onclick="handleQuery('range');">
	</form>
	<form id="dateQ" style="display:none">
		Date : <input type="text" id="singleDate" class="datepicker"/>(YYYY-MM-DD)&nbsp;&nbsp;&nbsp;&nbsp;
		
		<input type="button" class="button" value="Fetch Logs" onclick="handleQuery('singleDate');">
	</form>

<div id="labResult"></div>
</div>
<div id="dynamicChartLoad"> </div>
<div id="ResponseTimeAnalysis" style="display:none"> 
	<div id="responseTimeResult">
	<font color="green">Please wait</font> 
	</div>
	<div id="urlDetails"></div>
</div>
</div>
</body>
</html>