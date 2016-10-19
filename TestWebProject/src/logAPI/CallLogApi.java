//$Id$
package logAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class CallLogApi
 */
@WebServlet("/CallLogApi")
public class CallLogApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CallLogApi() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    static final int QUERY_MODE=1;
    static final int URL_MODE=2;
    public static final Map<Integer, Integer> rangMap;
	
	
    static{
    	HashMap <Integer, Integer>modifiableRangeMap = new HashMap<Integer, Integer>();
    	modifiableRangeMap.put(0, 10);
    	modifiableRangeMap.put(10, 20);
    	modifiableRangeMap.put(20, 30);
    	modifiableRangeMap.put(30, 40);
    	modifiableRangeMap.put(40, 50);
    	modifiableRangeMap.put(50, 60);
    	modifiableRangeMap.put(60, 70);
    	modifiableRangeMap.put(70, 80);
    	modifiableRangeMap.put(80, 90);
    	modifiableRangeMap.put(90, 100);
    	modifiableRangeMap.put(100, 200);
    	modifiableRangeMap.put(200, 300);
    	modifiableRangeMap.put(300, 400);
    	modifiableRangeMap.put(400, 500);
    	modifiableRangeMap.put(500, 600);
    	modifiableRangeMap.put(600, 700);
    	modifiableRangeMap.put(700, 800);
    	modifiableRangeMap.put(800, 900);
    	modifiableRangeMap.put(900, 1000);
    	modifiableRangeMap.put(1000, 2000);
    	modifiableRangeMap.put(2000, 3000);
    	modifiableRangeMap.put(3000, 4000);
    	modifiableRangeMap.put(4000, 5000);
    	modifiableRangeMap.put(5000, 10000);
    	rangMap = Collections.unmodifiableMap(modifiableRangeMap);
    	
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String action = request.getParameter("action");
		 if("customquery".equals(action)){
			String query = request.getParameter("query");
			String date = request.getParameter("date");
			String url = LoggerUtil.LoggerServiceUrl+"search?appid="+URLEncoder.encode("36064526", "UTF-8")+"&service="+URLEncoder.encode("support", "UTF-8")+"&date="+URLEncoder.encode(date, "UTF-8")+"&fromTime="+URLEncoder.encode("00:00", "UTF-8")+"&toTime="+URLEncoder.encode("24:59", "UTF-8")+"&range=1-25&authtoken="+URLEncoder.encode("fe291e4790477eaaaae29859a97d29f3", "UTF-8")+"&query="+URLEncoder.encode(query,"UTF-8");
		}
		else if("lab".equals(action)){
			String accessurl = request.getParameter("accessurl");
			String date = request.getParameter("date");
			String param = request.getParameter("param").trim();
	
			try {
				JSONArray resultArray = LoggerUtil.processUrl(accessurl,date,param);
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.print(resultArray.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if("customQueryRange".equals(action) || "customQueryDate".equals(action)){
			String query = request.getParameter("query");
			if(!(query.contains("app_ip"))){
				query += "groupby app_ip";
			}
			String fromDate = null;
			String toDate = null;
			if("customQueryRange".equals(action)){
				 fromDate = request.getParameter("fromDate");
				 toDate = request.getParameter("toDate");
				
			}
			else if("customQueryDate".equals(action)){
				fromDate = request.getParameter("Date");
			}
			
			try{
				JSONArray resultArray = LoggerUtil.processQuery(query,fromDate,toDate,QUERY_MODE);
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.print(resultArray.toString());
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.setContentType("application/text");
				PrintWriter out = response.getWriter();
				out.print("Exception occurred while processing query "+e.getMessage());
			}
		}
		else if("customUserDate".equals(action) || "customUserRange".equals(action)){
			
			String fromDate = null;
			String toDate = null;
			if("customUserRange".equals(action)){
				 fromDate = request.getParameter("fromDate");
				 toDate = request.getParameter("toDate");
				
			}
			else if("customUserDate".equals(action)){
				fromDate = request.getParameter("Date");
			}
			try{
				JSONArray resultArray = LoggerUtil.processUserReq(fromDate,toDate);
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.print(resultArray.toString());
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.setContentType("application/text");
				PrintWriter out = response.getWriter();
				out.print("Exception occurred while processing query "+e.getMessage());
			}
		}
		else if("responseTimeAnalysis".equals(action)){
			try{
			JSONArray resultArray = LoggerUtil.processResponseTime();
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(resultArray.toString());
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.setContentType("application/text");
				PrintWriter out = response.getWriter();
				out.print("Exception occurred while processing query "+e.getMessage());
			}
		}
		else if("populateChart".equals(action)){
			try{
				String query = request.getParameter("query");
				System.out.println(query);
				JSONObject returnObj = LoggerUtil.getPercentStats(query);
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.print(returnObj.toString());
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.setContentType("application/text");
				PrintWriter out = response.getWriter();
				out.print("Exception occurred while processing query "+e.getMessage());
			}
		}
		else if ("getMostUsedUrls".equals(action)){
			try{
			JSONArray resultObj = LoggerUtil.getMostUsedUrls();
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(resultObj.toString());
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setContentType("application/text");
			PrintWriter out = response.getWriter();
			out.print("Exception occurred while processing query "+e.getMessage());
		}
		}
		
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}