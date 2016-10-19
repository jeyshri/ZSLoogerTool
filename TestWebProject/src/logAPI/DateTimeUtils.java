package logAPI;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class DateTimeUtils {

	public static String[] lastweekDates(){
		Date date = new Date();
		  Calendar c = Calendar.getInstance();
	    c.setTime(date);
	    int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
	    c.add(Calendar.DATE, -i - 6);
	    Date start = c.getTime();
	    Date limit = new Date(1476354600000L);
	    if(start.before(limit)){
	    	start = limit;
	    }
	    c.add(Calendar.DATE, 4);
	    Date end = c.getTime();
	    SimpleDateFormat s=new SimpleDateFormat("dd/MM/yyyy");
	    s.setTimeZone(TimeZone.getTimeZone("PST"));
	    String startDate = s.format(start);
	    String endDate = s.format(end);
	    String [] lastWeekDates = new String[2];
	    lastWeekDates[0] = startDate;
	    lastWeekDates[1] = endDate;
	    return lastWeekDates;
	    
	}
	public static String[] getTodayDayRange(){
		Date date = new Date();
		 Calendar c = Calendar.getInstance();
		    c.setTime(date);
		    c.add(Calendar.DATE, -1);
		SimpleDateFormat s=new SimpleDateFormat("dd/MM/yyyy");
		s.setTimeZone(TimeZone.getTimeZone("PST"));
		String todayRange [] = new String[2];
		todayRange[1] =s.format(date);
		todayRange[0] = s.format(c.getTime());
		
		return todayRange;
	}
	public static String[] getLastMonthDays(){
		Date date = new Date();
	    Calendar c = Calendar.getInstance();
	    c.setTime(date);
	    c.add(Calendar.DATE, -30);
	    Date start = c.getTime();
	    Date limit = new Date(1489572995000L);
	    if(start.before(limit)){
	    	start = limit;
	    }
	    SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");
	    s.setTimeZone(TimeZone.getTimeZone("PST"));
	    String lastMonthDays[] = new String[2];
	    lastMonthDays[0] = s.format(start);
	    lastMonthDays[1] = s.format(date);
	    System.out.println(lastMonthDays[0]);
	    System.out.println(lastMonthDays[1]);
	    return lastMonthDays;
	}
	public static String[] getLastHourRange(){
		Date date = new Date();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, -1);
		Date oneHourBack = cal.getTime();
		
		SimpleDateFormat hourFormatter=new SimpleDateFormat("HH:mm");
		hourFormatter.setTimeZone(TimeZone.getTimeZone("PST"));
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("PST"));
		String[] returnString = new String[2];
		returnString [0] = dateFormatter.format(oneHourBack)+" "+hourFormatter.format(oneHourBack);
		returnString[1] = dateFormatter.format(date)+" "+hourFormatter.format(date);
		System.out.println(returnString[0]);
		System.out.println(returnString[1]);
		return returnString;
	}
	public static String[] getLastBeforeHourRange(){
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, -1);
		Date oneHourBack = cal.getTime();
		cal.setTime(date);
		cal.add(Calendar.HOUR, -2);
		Date twoHourBack = cal.getTime();
		SimpleDateFormat hourFormatter=new SimpleDateFormat("HH:mm");
		hourFormatter.setTimeZone(TimeZone.getTimeZone("PST"));
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("PST"));
		String[] returnString = new String[2];
		returnString[0] = dateFormatter.format(twoHourBack)+" "+hourFormatter.format(twoHourBack);
		returnString [1] = dateFormatter.format(oneHourBack)+" "+hourFormatter.format(oneHourBack);
		System.out.println(date);
		System.out.println(returnString[0]);
		System.out.println(returnString[1]);
		return returnString;
	}
	public static String[] getLastTwoBusinessDays(){
		Date date = new Date();
	    Calendar onebusinessDaybefore = getBeforeBusinessDay(date, -1);
	    Calendar twoBusinessDaysbefore = getBeforeBusinessDay(onebusinessDaybefore.getTime(), -1);
	  
	    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("PST"));
		String [] returnString = new String[2];
		returnString[0] = dateFormatter.format(twoBusinessDaysbefore.getTime());
		returnString[1] = dateFormatter.format(onebusinessDaybefore.getTime());
		return returnString;
	}
	public static String[] getLastThreeBusinessDays(){

		Date date = new Date();
	    Calendar onebusinessDaybefore = getBeforeBusinessDay(date, -1);
	    Calendar twoBusinessDaysbefore = getBeforeBusinessDay(onebusinessDaybefore.getTime(), -1);
	    Calendar threeBusinessDaysbefore = getBeforeBusinessDay(twoBusinessDaysbefore.getTime(), -1);
	    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("PST"));
		String [] returnString = new String[2];
		returnString[0] = dateFormatter.format(threeBusinessDaysbefore.getTime());
		returnString[1] = dateFormatter.format(onebusinessDaybefore.getTime());
		System.out.println(returnString[0]);
		System.out.println(returnString[1]);
		return returnString;
	
	}
	private static Calendar getBeforeBusinessDay(Date date , int noOfDays){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		 int dayOfWeek;
		 int count = 0;
		  do {
			  	if(count ==0){
		        cal.add(Calendar.DAY_OF_MONTH, noOfDays);
			  	}else{
			  		cal.add(Calendar.DAY_OF_MONTH, -1);
			  	}
			  	count++;
		        System.out.println(cal.getTime());
		        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		    } while (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
		  System.out.println(cal.get(Calendar.DAY_OF_WEEK));
		  return cal;
	}
	public static void main (String args[]){
		getLastHourRange();
		getLastBeforeHourRange();
	}
}
