package logAPI;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

public class LoggerUtil {

	public static final List<String> criticalUrls = Arrays.asList("/ShowEntityInfo.do","/ShowDetails.do","/SendMail.do","/ShowSelectedCustomView.do","/getrequestqueuedata.do","/ShowTabInfo.do","/MDashboard.do");
	public static final String LoggerServiceUrl = "https://us3-logs.zoho.com/"; 
	 public static final Map<String, String> paramSpecificCriticalUrls;
	static{
		HashMap <String, String>urlParamTempMap = new HashMap<String, String>();
		urlParamTempMap.put("/ShowEntityInfo.do", "params=\"module=Cases\"");
		urlParamTempMap.put("/ShowDetails.do", "params=\"module=Cases\"");
		urlParamTempMap.put("/ShowSelectedCustomView.do", "params=\"module=Cases\"");
		urlParamTempMap.put("/ShowTabInfo.do", "params=\"module=Cases\" and params=\"tabName=Threads\"");
		urlParamTempMap.put("/MDashboard.do", "params=\"action=pollData\"");
		paramSpecificCriticalUrls = urlParamTempMap;
	}
	public static final Map<String,String> lessCriticalUrl;
	static{
		HashMap <String, String>urlParamTempMap = new HashMap<String, String>();
		urlParamTempMap.put("/Dashboard.do", null);
		urlParamTempMap.put("/CustomReport.do", null);
		urlParamTempMap.put("/api/web/Solutions/Dashboard", null);
		urlParamTempMap.put("/getCommunity.do", null);
		urlParamTempMap.put("/getrequestqueuedata.do", null);
		lessCriticalUrl = urlParamTempMap;
	}
	static JSONArray processQuery(String query,String fromDate,String toDate, int mode) throws Exception{
		JSONArray resultArray = new JSONArray();
		HashMap<Integer, Integer> rangMap = new HashMap<Integer, Integer>();
		rangMap.put(0, 10);
		rangMap.put(10, 20);
		rangMap.put(20, 30);
		rangMap.put(30, 40);
		rangMap.put(40, 50);
		rangMap.put(50, 60);
		rangMap.put(60, 70);
		rangMap.put(70, 80);
		rangMap.put(80, 90);
		rangMap.put(90, 100);
		rangMap.put(100, 200);
		rangMap.put(200, 300);
		rangMap.put(300, 400);
		rangMap.put(400, 500);
		rangMap.put(500, 600);
		rangMap.put(600, 700);
		rangMap.put(700, 800);
		rangMap.put(800, 900);
		rangMap.put(900, 1000);
		rangMap.put(1000, 2000);
		rangMap.put(2000, 3000);
		rangMap.put(3000, 4000);
		rangMap.put(4000, 5000);
		rangMap.put(5000, 10000);
		rangMap.put(10000, 50000);
		Set<Integer> keySet = rangMap.keySet();
		SortedSet<Integer> set=new TreeSet(keySet);
		int totalCount = 0;
		for(Integer from : set){
			Integer to = rangMap.get(from);
			String extraQuery = " and time_taken>="+from+" and time_taken<="+to+" groupby app_ip limit 50";
			String curquery = query.replace("groupby app_ip", extraQuery);
			if(toDate == null){
				toDate = fromDate;
			}
			
			
			String url = LoggerServiceUrl+"search?appid="+URLEncoder.encode("36064526", "UTF-8")+"&service="+URLEncoder.encode("support", "UTF-8")+"&fromDateTime="+URLEncoder.encode(fromDate+" 00:00", "UTF-8")+"&toDateTime="+URLEncoder.encode(toDate+" 23:59", "UTF-8")+"&range=1-25&authtoken="+URLEncoder.encode("fe291e4790477eaaaae29859a97d29f3", "UTF-8")+"&query="+URLEncoder.encode(curquery,"UTF-8");
			
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			int rest  = con.getResponseCode();
			String inputLine;
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			StringBuffer html = new StringBuffer();
			final InputStream is = con.getInputStream();
			while ((inputLine = in.readLine()) != null) {
				html.append(inputLine);
			}
			in.close();
			int supportLabCount = 0;
			
			try {
				JSONObject resultObject = new JSONObject(html.toString());
				JSONObject returnObject = new JSONObject();
				if(resultObject.has("ErrorCode") && resultObject.getInt("ErrorCode")!=0){
					returnObject.put("error",resultObject.get("Message"));
					resultArray.put(returnObject);
					return resultArray;
				}
				else{
				
				JSONArray countArray = resultObject.getJSONArray("tableValues");
				for(int i =0;i<countArray.length();i++ ){
					JSONObject currObj =countArray.getJSONObject(i); 
					String appip = currObj.getString("groupby(app_ip)");
					int count = currObj.getInt("count");
					//if(IPGRIDMAP.get("SupportLab").contains(appip)){ 
						supportLabCount = supportLabCount+count;
					//}
					
				}
				String logUrl = curquery.replace("groupby app_ip", "");
				//logUrl = logUrl.replace("/", "////");
				logUrl = URLEncoder.encode(logUrl, "UTF-8");
				
				logUrl = logUrl.replaceAll("\\+", " ");
				logUrl = logUrl.replaceAll("%", "%25");
				//System.out.println(logUrl);
				String encodedDateVal = "";
				if(toDate == null){
				encodedDateVal = URLEncoder.encode(fromDate+" 00:00 23:59","UTF-8");
				
				}
				else{
					
					encodedDateVal = URLEncoder.encode(fromDate+" 00:00 "+toDate+" 23:59", "UTF-8");
				}
				encodedDateVal = encodedDateVal.replaceAll("%", "%25");
				encodedDateVal = encodedDateVal.replaceAll("\\+", "%2520");
				String countLink = "<a target=\"_blank\" href=\""+LoggerServiceUrl+"app/36064526/zlogs.zl#/zlh/support/search/"+encodedDateVal+"/1-100/descending/"+logUrl+"\">"+supportLabCount+"</a>";
				
				//System.out.println(countLink);
				returnObject.put("SupportLabCount", countLink);
				
				returnObject.put("count",supportLabCount);
				if(mode == CallLogApi.URL_MODE){
				returnObject.put("countKey", supportLabCount+"."+from+to);
				}
				returnObject.put("timeRange", from+"-"+to);
				totalCount +=supportLabCount;
				resultArray.put(returnObject);
				//System.out.println(resultArray);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		resultArray = LoggerUtil.processForCountPercent(resultArray,totalCount,mode);
		return resultArray;
	
		
	}

	static JSONArray processUserReq(String fromDate,String toDate) throws Exception{
		JSONArray resultArray = new JSONArray();
		String query = "logtype=\"access\" and request_uri CONTAINS \"/support\" count groupby request_uri limit  111803";
		if(toDate == null){
			toDate = fromDate;
		}
		String url = LoggerServiceUrl+"search?appid="+URLEncoder.encode("36064526", "UTF-8")+"&service="+URLEncoder.encode("support", "UTF-8")+"&fromDateTime="+URLEncoder.encode(fromDate+" 00:00", "UTF-8")+"&toDateTime="+URLEncoder.encode(toDate+" 23:59", "UTF-8")+"&range=1-25&authtoken="+URLEncoder.encode("fe291e4790477eaaaae29859a97d29f3", "UTF-8")+"&query="+URLEncoder.encode(query,"UTF-8");
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			int rest  = con.getResponseCode();
			String inputLine;
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			StringBuffer html = new StringBuffer();
			final InputStream is = con.getInputStream();
			while ((inputLine = in.readLine()) != null) {
				html.append(inputLine);
			}
			in.close();
			//System.out.println(html.toString());
			
				HashMap<String,Integer> urlSet = new HashMap<String,Integer>();
				JSONObject resultObject = new JSONObject(html.toString());
				JSONObject returnObject = new JSONObject();
				if(resultObject.has("ErrorCode") && resultObject.getInt("ErrorCode")!=0){
					returnObject.put("error",resultObject.get("Message"));
					resultArray.put(returnObject);
					return resultArray;
				}
				else{
				
				JSONArray countArray = resultObject.getJSONArray("tableValues");
				int maxCount=0; 
				System.out.println(countArray.length());
				for(int i =0;i<countArray.length();i++ ){
					JSONObject currObj =countArray.getJSONObject(i); 
					
					String cururl = currObj.getString("groupby(url_report)");
					int count = currObj.getInt("count");
					System.out.println(cururl);
					if(!cururl.contains(".do")){
						continue;
					}
					String urlSplitArr[] = cururl.split("/");
					if(urlSplitArr.length == 4){
						 cururl = cururl.split("/")[3];
					}
					if(urlSet.containsKey(cururl)){
						count = count+urlSet.get(cururl);	
					}
					if(count>maxCount){
						maxCount = count;
					}
					urlSet.put(cururl, count);
				}
				System.out.println(urlSet.size());
				TreeMap<String, Integer> treeMap = LoggerUtil.sortMapByValue(urlSet);
				System.out.println("The Max Count is =====>"+maxCount);
				int count = 0;
				for(String urluser : treeMap.keySet()){
					if(count > 10){
						break;
					}
					String queryTorender="logtype=\"access\" and request_uri CONTAINS \""+urluser+"\" groupby app_ip";
					JSONArray curResultArray =  processQuery(queryTorender,fromDate,toDate,CallLogApi.URL_MODE);
					//System.out.println("curResultArray====>"+curResultArray);
					JSONArray portalDeatilsAry= LoggerUtil.portalbasedDeatils(urluser,countArray);
					//System.out.println("portalDeatilsAry===>"+portalDeatilsAry);
					JSONObject curObject = new JSONObject();
					curObject.put("url", urluser);
					curObject.put("details", curResultArray);
					curObject.put("portalDeatils", portalDeatilsAry);
					resultArray.put(curObject);
					count ++;
				}
				
				}
		System.out.println(resultArray);
		return resultArray;
	}

	public static JSONArray portalbasedDeatils(String urluser,JSONArray jsonArray)throws Exception{
		
		HashMap map = new HashMap();
		JSONArray portalDetails = new JSONArray();
		for(int i=0;i<jsonArray.length();i++){
			JSONObject obj = jsonArray.getJSONObject(i);
			String url = obj.getString("groupby(url_report)");
			if(url.contains(urluser)){
				int cnt = obj.getInt("count");
				map.put(url, cnt);
			}
			continue;
		}
		TreeMap<String, Integer> treeMap = LoggerUtil.sortMapByValue(map);
		int i = 0;
		for (Map.Entry<String, Integer> entry : treeMap.entrySet())
		{
			if(i<10){
				JSONObject obj = new JSONObject();
				obj.put("url",entry.getKey().split("/")[2]);
				obj.put("count",entry.getValue());
				portalDetails.put(obj);
			}
			i++;
		}
		return portalDetails;
	}

	public static TreeMap<String, Integer> sortMapByValue(HashMap<String, Integer> map){
		Comparator<String> comparator = new ValueComparator(map);
		//TreeMap is a map sorted by its keys. 
		//The comparator is used to sort the TreeMap by keys. 
		TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
		result.putAll(map);
		return result;
	}

	static JSONArray processUrl(String accessUrl,String date,String param) throws Exception{
		JSONArray resultArray = new JSONArray();
	
		Set<Integer> keySet = CallLogApi.rangMap.keySet();
		SortedSet<Integer> set=new TreeSet(keySet);
		int totalCount = 0;
		for(Integer from : set){
			Integer to = CallLogApi.rangMap.get(from);
			String query = "logtype=\"access\" AND time_taken>="+from+" AND time_taken<="+to+" AND request_uri CONTAINS \""+accessUrl+"\"";
			if(param!=null && param.length()>0){
				query = query+" AND params=\""+param+"\"";
			}
			query = query + "groupby app_ip";
			String url = LoggerServiceUrl+"search?appid="+URLEncoder.encode("36064526", "UTF-8")+"&service="+URLEncoder.encode("support", "UTF-8")+"&date="+URLEncoder.encode(date, "UTF-8")+"&fromTime="+URLEncoder.encode("00:00", "UTF-8")+"&toTime="+URLEncoder.encode("23:59", "UTF-8")+"&range=1-25&authtoken="+URLEncoder.encode("fe291e4790477eaaaae29859a97d29f3", "UTF-8")+"&query="+URLEncoder.encode(query,"UTF-8");
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			int rest  = con.getResponseCode();
			String inputLine;
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			StringBuffer html = new StringBuffer();
			final InputStream is = con.getInputStream();
			while ((inputLine = in.readLine()) != null) {
				html.append(inputLine);
			}
			in.close();
			int supportLabCount = 0;
			try {
				JSONObject resultObject = new JSONObject(html.toString());
				JSONObject returnObject = new JSONObject();
				JSONArray countArray = resultObject.getJSONArray("tableValues");
				for(int i =0;i<countArray.length();i++ ){
					JSONObject currObj =countArray.getJSONObject(i); 
					String appip = currObj.getString("groupby(app_ip)");
					int count = currObj.getInt("count");
					 
						supportLabCount = supportLabCount+count;
					
					
				}
				
				String logUrl = "logtype%253D%2522access%2522%2520AND%2520time_taken%253E%253D"+from+"%2520AND%2520time_taken%253C%253D"+to+"%2520AND%2520request_uri%2520CONTAINS%2520%2522%252F"+accessUrl+"%2522%2520and%2520group_name%253D%2522supportlab%2522";
				String countLink = "<a target=\"_blank\" href=\""+LoggerServiceUrl+"app/36064526/zlogs.zl#/zlh/support/access/"+date+"/00:00/23:59/1-100/descending/"+logUrl+"\">"+supportLabCount+"</a>";
				returnObject.put("SupportLabCount", countLink);
				returnObject.put("count",supportLabCount);
				returnObject.put("timeRange", from+"-"+to);
				
				totalCount +=supportLabCount;
				resultArray.put(returnObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		LoggerUtil.processForCountPercent(resultArray,totalCount,CallLogApi.QUERY_MODE);
		return resultArray;
	}

	static JSONArray processForCountPercent (JSONArray resultArray,int totalCount,int mode) throws Exception{
		JSONArray returnArray = new JSONArray();
		Float cumPercent = 0.0f;
		List<Double> percentList = new ArrayList<Double>();
		if(mode == CallLogApi.QUERY_MODE){
		for(int i=0;i<resultArray.length();i++){
			JSONObject currObj =resultArray.getJSONObject(i); 
			int count =  currObj.getInt("count");
			float percent = (count * 100.0f) / totalCount;
			cumPercent = percent+cumPercent;
			Double percent1 =Math.round(percent * 100.00) / 100.00;
			//percentList.add(percent1);
			currObj.put("percent",percent1);
			currObj.put("cumPercent",Math.round(cumPercent * 100.00) / 100.00);
			returnArray.put(currObj);
		}
		return returnArray;
		}
		else if(mode == CallLogApi.URL_MODE){
			for(int i=0;i<resultArray.length();i++)
			{
				JSONObject currObj = resultArray.getJSONObject(i);
				Double count = Double.valueOf(currObj.getString("countKey"));
				percentList.add(count);
			}
			Collections.sort(percentList);
			System.out.println(percentList);
			for(int i=percentList.size()-1;i>(percentList.size()-6);i--){
				JSONObject currObj = new JSONObject();
				String value = String.valueOf(percentList.get(i));
				//System.out.println(value);
				String valueArr[] = value.split("\\.");
				//System.out.println(valueArr);
				int count = Integer.valueOf(valueArr[0]);
				String timeRange = valueArr[1];
				if(timeRange.length()>1){
					timeRange = timeRange.substring(0, timeRange.length()-1);
				}
				else {
					timeRange = timeRange+"0";
				}
				int fromTime = Integer.valueOf(timeRange);
				//System.out.println("fromTime ====>"+fromTime);
				currObj.put("count", count);
				//System.out.println("count ====>"+count);
				int to = CallLogApi.rangMap.get(fromTime);
				//System.out.println(to);
				currObj.put("timeRange", fromTime+"-"+to);
				//System.out.println(currObj);
				float percent = (count * 100.0f) / totalCount;
				Double percent1 =Math.round(percent * 100.00) / 100.00;
				currObj.put("percent", percent1);
				returnArray.put(currObj);
			}
			return returnArray;
		}
		return returnArray;
	}
	
	static JSONArray processResponseTime() throws Exception{
		JSONArray returnArray = new JSONArray();
		for(String url : criticalUrls){
			String query = "logtype=\"access\"  AND request_uri CONTAINS \""+url+"\" and group_name CONTAINS \"supportlab\""; 
			String param = null;
			if(paramSpecificCriticalUrls.containsKey(url)){
				param = paramSpecificCriticalUrls.get(url);
				query = query + " and "+ param;
			}
			query = query +" avg(time_taken)";
			System.out.println(query);
			String lastMonthDates[] = DateTimeUtils.getLastMonthDays();
			String todayRange[] = DateTimeUtils.getTodayDayRange();
			String lastWeekBusinessDays[] = DateTimeUtils.lastweekDates();
			String lastHourRange[] = DateTimeUtils.getLastHourRange();
			JSONObject resultObjectLastMonth= processQuery(query,lastMonthDates[0],lastMonthDates[1]);

			JSONObject resultObjectLastWeek = processQuery(query,lastWeekBusinessDays[0],lastWeekBusinessDays[1]);
			JSONObject resultObjecttoday = processQuery(query,todayRange[0],todayRange[1]);
			System.out.println(lastHourRange[0]+","+lastHourRange[1]);
			JSONObject resultObjectlastHour = processQuery(query,lastHourRange[0],lastHourRange[1]);
			JSONArray countArray = resultObjectLastMonth.getJSONArray("tableValues");
			System.out.println(countArray);
			System.out.println(resultObjectLastMonth);
			JSONObject currObj =countArray.getJSONObject(0); 
			Double lastMonthAvgResponseTime = Double.valueOf(Math.round(currObj.getDouble("avg(time_taken)") * 100)/100);
			System.out.println(countArray);
			int lastMonthCount = resultObjectLastMonth.getInt("numFound"); 
			System.out.println(lastMonthCount);
			countArray = resultObjectLastWeek.getJSONArray("tableValues");
			currObj =countArray.getJSONObject(0); 
			Double lastWeekAvgResponseTime =Double.valueOf(Math.round(currObj.getDouble("avg(time_taken)") * 100)/100);
			int lastWeekCount = resultObjectLastWeek.getInt("numFound");
			countArray = resultObjecttoday.getJSONArray("tableValues");
			currObj =countArray.getJSONObject(0); 
			Double todayAvgResponseTime = Double.valueOf(Math.round(currObj.getDouble("avg(time_taken)") * 100)/100);
			int todayCount = resultObjecttoday.getInt("numFound");
			countArray = resultObjectlastHour.getJSONArray("tableValues");
			System.out.println(resultObjectlastHour);
			currObj =countArray.getJSONObject(0); 
			System.out.println("value of stupid "+currObj.get("avg(time_taken)"));
			Double lastHourAvgResponseTime = todayAvgResponseTime;
			int lastHourCount = 0;
			System.out.println(currObj.get("avg(time_taken)").getClass());
			if(currObj.get("avg(time_taken)") != JSONObject.NULL){
				System.out.println("into idiot");
			 lastHourAvgResponseTime =Double.valueOf(Math.round(currObj.getDouble("avg(time_taken)") * 100)/100);
			  lastHourCount = resultObjectlastHour.getInt("numFound");
			}
			System.out.println(query);
		
		
			String trend = "DEC";
			boolean needSound = false;
			if(lastHourAvgResponseTime>todayAvgResponseTime || lastHourAvgResponseTime> lastWeekAvgResponseTime || lastHourAvgResponseTime > lastMonthAvgResponseTime){
				trend = "IN";
			}
			JSONObject urlDetails = new JSONObject();
			if(param !=null){
				param = param.replace('"', '\"');
				urlDetails.put("urlParam", url+" and "+param);
			}
			else{
				urlDetails.put("urlParam", url);
			}
			urlDetails.put("trend", trend);
			urlDetails.put("lastMonthAvgResponseTime", lastMonthAvgResponseTime);
			urlDetails.put("lastWeekAvgResponseTime", lastWeekAvgResponseTime);
			urlDetails.put("todayAvgResponseTime", todayAvgResponseTime);
			urlDetails.put("lastHourAvgResponseTime", lastHourAvgResponseTime);
			urlDetails.put("lastMonthCount", lastMonthCount);
			urlDetails.put("lastWeekCount", lastWeekCount);
			urlDetails.put("todayCount", todayCount);
			urlDetails.put("lastHourCount", lastHourCount);
			urlDetails.put("needSound", needSound);
			returnArray.put(urlDetails);
		}
		return returnArray;
	}
	static JSONObject processQuery(String query,String fromDate,String toDate) throws Exception{
		String url = LoggerServiceUrl+"search?appid="+URLEncoder.encode("36064526", "UTF-8")+"&service="+URLEncoder.encode("support", "UTF-8")+"&fromDateTime="+URLEncoder.encode(fromDate+" 00:00", "UTF-8")+"&toDateTime="+URLEncoder.encode(toDate+" 23:59", "UTF-8")+"&range=1-25&authtoken="+URLEncoder.encode("fe291e4790477eaaaae29859a97d29f3", "UTF-8")+"&query="+URLEncoder.encode(query,"UTF-8");
		if(fromDate.contains(":")){
			url = LoggerServiceUrl+"search?appid="+URLEncoder.encode("36064526", "UTF-8")+"&service="+URLEncoder.encode("support", "UTF-8")+"&fromDateTime="+URLEncoder.encode(fromDate, "UTF-8")+"&toDateTime="+URLEncoder.encode(toDate, "UTF-8")+"&range=1-25&authtoken="+URLEncoder.encode("fe291e4790477eaaaae29859a97d29f3", "UTF-8")+"&query="+URLEncoder.encode(query,"UTF-8");
		}
		URL obj = new URL(url);
		System.out.println("gonna make connection=====>"+query);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		int rest  = con.getResponseCode();
		String inputLine;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		StringBuffer html = new StringBuffer();
		final InputStream is = con.getInputStream();
		while ((inputLine = in.readLine()) != null) {
			html.append(inputLine);
		}
		in.close();
		System.out.println("end connection");
		try {
			JSONObject resultObject = new JSONObject(html.toString());
			System.out.println(resultObject);
			return resultObject;
		}catch(Exception e){
			return null;
		}
	}
	public static JSONObject getPercentStats(String query)throws Exception{
		String lastMonthDates[] = DateTimeUtils.getLastMonthDays();
		String todayRange[] = DateTimeUtils.getTodayDayRange();
		String lastWeekBusinessDays[] = DateTimeUtils.lastweekDates();
		String lastHourRange[] = DateTimeUtils.getLastHourRange();
		Set<Integer> keySet = CallLogApi.rangMap.keySet();
		SortedSet<Integer> set=new TreeSet(keySet);
		JSONArray lastMonthArray = new JSONArray();
		JSONArray lastWeekArray = new JSONArray();
		JSONArray todayArray = new JSONArray();
		JSONArray lastHourArray = new JSONArray();
		int lastMonthTotalCount = 0;
		int lastWeekTotalCount = 0;
		int todayTotalCount = 0;
		int lastHourTotalCount = 0;
		for(Integer from : set){
			Integer to= CallLogApi.rangMap.get(from);
			String curQuery = query;
			curQuery = curQuery+"and group_name CONTAINS \"supportlab\" and time_taken>="+from+" and time_taken<="+to+" groupby app_ip limit 200";
			JSONObject lastMonthObject = processQuery(curQuery, lastMonthDates[0], lastMonthDates[1]);
			JSONObject lastWeekObject = processQuery(curQuery, lastWeekBusinessDays[0], lastWeekBusinessDays[1]);
			JSONObject todayRangeObject = processQuery(curQuery, todayRange[0], todayRange[1]);
			JSONObject lastHourRangeObject = processQuery(curQuery, lastHourRange[0], lastHourRange[1]);
			System.out.println(lastMonthObject);
			lastMonthObject = getCountForPercentCalculation(lastMonthObject);
			lastWeekObject = getCountForPercentCalculation(lastWeekObject);
			todayRangeObject = getCountForPercentCalculation(todayRangeObject);
			lastHourRangeObject = getCountForPercentCalculation(lastHourRangeObject);
			lastMonthObject.put("timeRange", from+"-"+to);
			lastWeekObject.put("timeRange", from+"-"+to);
			todayRangeObject.put("timeRange", from+"-"+to);
			lastHourRangeObject.put("timeRange", from+"-"+to);
			lastMonthTotalCount +=lastMonthObject.getInt("count");
			lastWeekTotalCount +=lastWeekObject.getInt("count");
			todayTotalCount +=todayRangeObject.getInt("count");
			lastHourTotalCount+=lastHourRangeObject.getInt("count");
			lastMonthArray.put(lastMonthObject);
			lastWeekArray.put(lastWeekObject);
			todayArray.put(todayRangeObject);
			lastHourArray.put(lastHourRangeObject);
		}
		lastMonthArray = processForCountPercent(lastMonthArray,lastMonthTotalCount,CallLogApi.QUERY_MODE);
		lastWeekArray = processForCountPercent(lastWeekArray,lastWeekTotalCount,CallLogApi.QUERY_MODE);
		todayArray = processForCountPercent(todayArray,todayTotalCount,CallLogApi.QUERY_MODE);
		lastHourArray = processForCountPercent(lastHourArray, lastHourTotalCount, CallLogApi.QUERY_MODE);
		//List<Double> lastMonthPercentArray = Arrays.asList(0.216, 0.268, 1.716, 1.035, 12.275, 107.856, 168.508, 187.352, 161.335, 117.140, 282.618, 21.030, 11.262, 3.629, 2.664, 2.576, 1.406, 0.771, 0.528, 3.023, 1.499, 1.038, 0.680, 1.473);
		//List<Double> lastWeekPercentArray = Arrays.asList(0.057, 0.105, 0.533, 0.366, 3.585, 36.693, 62.013, 69.021, 60.953, 44.729, 120.885, 11.271, 4.636, 1.690, 1.201, 1.224, 0.689, 0.370, 0.231, 1.486, 0.778, 0.502, 0.361, 0.763);
		//List<Double> todayPercentArray = Arrays.asList(0.017, 0.005, 0.036, 0.010, 0.243, 2.686, 4.638, 5.298, 4.399, 3.088, 5.844, 0.271, 0.165, 0.043, 0.026, 0.023, 0.007, 0.003, 0.003, 0.015, 0.001, 0.00, 0.00, 0.00);
		//List<Double> lastHourPercentArray = Arrays.asList(0.017, 0.005, 0.036, 0.010, 0.243, 2.686, 4.638, 5.298, 4.399, 3.088, 5.844, 0.271, 0.165, 0.043, 0.026, 0.023, 0.007, 0.003, 0.003, 0.015, 0.001, 0.00, 0.00, 0.00);
		List<Double> lastMonthPercentArray = new ArrayList<Double>();
		List<Double> lastWeekPercentArray = new ArrayList<Double>();
		List<Double> todayPercentArray = new ArrayList<Double>();
		List<Double> lastHourPercentArray = new ArrayList<Double>();
		for(int i = 0;i<lastMonthArray.length();i++){
			lastMonthPercentArray.add(lastMonthArray.getJSONObject(i).getDouble("percent"));
			lastWeekPercentArray.add(lastWeekArray.getJSONObject(i).getDouble("percent"));
			todayPercentArray.add(todayArray.getJSONObject(i).getDouble("percent"));
			lastHourPercentArray.add(lastHourArray.getJSONObject(i).getDouble("percent"));
		}
		
		JSONObject returnObject = new JSONObject();
		returnObject.put("lastMonthPercentArray", lastMonthPercentArray.toArray());
		returnObject.put("lastWeekPercentArray", lastWeekPercentArray.toArray());
		returnObject.put("todayPercentArray", todayPercentArray.toArray());
		returnObject.put("lastHourPercentArray", lastHourPercentArray.toArray());
		returnObject.put("lastMonthTotalCount", lastMonthTotalCount);
		returnObject.put("lastWeekTotalCount", lastWeekTotalCount);
		returnObject.put("todayTotalCount", todayTotalCount);
		returnObject.put("lastHourTotalCount", lastHourTotalCount);
		System.out.println(returnObject);
		return  returnObject;
	}
	public static JSONObject getCountForPercentCalculation(JSONObject detailsObject) throws Exception{
		
			JSONObject returnObject = new JSONObject();
			if(detailsObject.has("ErrorCode") && detailsObject.getInt("ErrorCode")!=0){
				returnObject.put("error",detailsObject.get("Message"));
				return returnObject;
			}
			else{
			int supportLabCount = 0;
			JSONArray countArray = detailsObject.getJSONArray("tableValues");
			for(int i =0;i<countArray.length();i++ ){
				JSONObject currObj =countArray.getJSONObject(i); 
				int count = currObj.getInt("count");
				supportLabCount = supportLabCount+count;
			}
			detailsObject.put("count",supportLabCount);
			}
			return detailsObject;
			
		
	}
	public static JSONArray getMostUsedUrls() throws Exception{
		JSONArray returnArr = new JSONArray();
		HashMap<String, Integer> lastHourMap = new HashMap<String, Integer>();
		HashMap<String, Integer> lastBeforeHourMap = new HashMap<String, Integer>();
		String query = "logtype=\"access\" groupby url_report limit 100";
		String lastHourRange [] = DateTimeUtils.getLastHourRange();
		String lastBeforeHourRange[] = DateTimeUtils.getLastBeforeHourRange();
		JSONObject lastHourObj = processQuery(query, lastHourRange[0], lastHourRange[1]);
		JSONObject lastBeforeHourObj = processQuery(query, lastBeforeHourRange[0], lastBeforeHourRange[1]);
		System.out.println(lastHourObj);
		JSONArray lastHourArr = lastHourObj.getJSONArray("tableValues");
		JSONArray lastBeforeHourArr = lastBeforeHourObj.getJSONArray("tableValues");
		for(int i = 0;i<lastHourArr.length();i++){
			JSONObject lastHourDetObj = lastHourArr.getJSONObject(i);
			JSONObject lastBeforeHourDetObj = lastBeforeHourArr.getJSONObject(i);
			lastHourMap.put(lastHourDetObj.getString("groupby(url_report)"), lastHourDetObj.getInt("count"));
			lastBeforeHourMap.put(lastBeforeHourDetObj.getString("groupby(url_report)"), lastBeforeHourDetObj.getInt("count"));
		}
//		int count = 0;
		for(String url: lastBeforeHourMap.keySet()){
//			if(count<10){
			int lastBeforeCount = lastBeforeHourMap.get(url);
			if(lastHourMap.containsKey(url)){
				int lastHourCount = lastHourMap.get(url);
				int cutOffCount = (int) (lastBeforeCount * 0.5);
				if(lastHourCount > cutOffCount){
					JSONObject tempObj = new JSONObject();
					tempObj.put("url", url);
					tempObj.put("lastBeforeHourCount", lastBeforeCount);
					tempObj.put("lastHourCount", lastHourCount);
					returnArr.put(tempObj); 
//					count++;
				}
			}
//			}else{
//				break;
//			}
		}
		//System.out.println(lastHourMap);
		//System.out.println(lastBeforeHourMap);
		System.out.println(returnArr);
		return returnArr;
	}
	public static void main(String args[]) throws Exception{
		getPercentStats("logtype=\"access\" and request_uri CONTAINS \"getrequestqueuedata.do\"");
		//getMostUsedUrls();
	}
}
