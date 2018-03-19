package sunwou.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author sunwuo
 * @version 2.0.0 by acy 170906
 * <p>
 * 统一的时间传入传出为String传入格式�?"2017-01-01 00:00:00"
 * 添加格式时请添加�?个私有的SimpleDateFormat和一个共有的静�?�变量并修改formatDate方法
 */

public class TimeUtil {

    private static SimpleDateFormat sdfLastHaveDay = new SimpleDateFormat("yyyy-MM-dd");

    private static SimpleDateFormat sdfCommon = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static SimpleDateFormat sdfWithoutInterval = new SimpleDateFormat("yyyyMMddHHmmss");

    
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>(); 

    //精确到秒
    public static final String TO_S = "yyyy-MM-dd HH:mm:ss";
    //精确到天
    public static final String TO_DAY = "yyyy-MM-dd";
    //精确到秒无符�?
    public static final String TO_S2 = "yyyyMMddHHmmss";
    //精确到毫秒无符号

    
    
    /**
     * 获取昨天的日期
     * @return
     */
    public static String getYesterday(){
    	SimpleDateFormat sdf=new SimpleDateFormat(TO_DAY);  
        Date date=new Date();  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.add(Calendar.DAY_OF_MONTH, -1);  
        date = calendar.getTime();  
        return sdf.format(date);  
    }
    
    public static String formatDate(Date date,String type){
	    	String result="";
	    	DateFormat df=threadLocal.get();
	    	if(df==null){
	    		df=new SimpleDateFormat(type);
	    	}
    	    switch (type) {
			case TO_S:
				result=sdfCommon.format(date);
				break;
			case TO_DAY:
				result=sdfLastHaveDay.format(date);
				break;
			case TO_S2:
				result=sdfWithoutInterval.format(date);
				break;
			}
            return result;
    }
    
    public static Date parse(String strDate,String type){
	    	Date result=null;
	    	DateFormat df=threadLocal.get();
	    	if(df==null){
	    		df=new SimpleDateFormat(type);
	    	}
	    	try {
	    	    switch (type) {
				case TO_S:
					result=sdfCommon.parse(strDate);
					break;
				case TO_DAY:
					result=sdfLastHaveDay.parse(strDate);
					break;
				case TO_S2:
						result=sdfWithoutInterval.parse(strDate);
					break;
				}
	    	} catch (ParseException e) {
	    	}
            return result;
    } 

	private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟";
    private static final String ONE_HOUR_AGO = "小时";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";
	/**
	 * 对时间格式成
	 * 几分钟前几小时前几天前
	 * @param time
	 * @return
	 */
    public static String formatTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long delta = new Date().getTime() - date.getTime();
        if (delta < 1L * ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }
    }
    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }

	public static boolean checkTX() {
		String day=formatDate(new Date(), TO_DAY).substring(8, 10);
		if(day.equals("25")||day.equals("26")||day.equals("27"))
		return true;
		else
		return false;
	}
	

}
