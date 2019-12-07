package utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeDifferent {

	/*
	 * 比较两个日期字符的差值
	 */
	public static int  DateDifferent(String date1Str,String date2Str) throws ParseException{
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss") ;
		
		Date date1 = simpleDateFormat.parse(date1Str) ;
		
		Date date2 = simpleDateFormat.parse(date2Str) ;
		
		int interval = (int)((date1.getTime() - date2.getTime())/1000);
		//返回的是毫秒，所以除以一千
		return interval;		
	}
}
