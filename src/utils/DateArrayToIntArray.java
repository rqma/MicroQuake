package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.h2.constant.Parameters;

//日期数组，转化为整型数组，整型数组为日期数组 与最大值得差
public class DateArrayToIntArray {

	private Date dateMax;
	
	public Date getDateStr() {
		return dateMax;
	}

	public void setDateStr(Date dateMax) {
		this.dateMax = dateMax;
	}

	public int[] IntArray(String[] dateStr) throws ParseException{//处理完变为3列0了
		
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date [] startDate = new Date[Parameters.SensorNum];
		for(int i=0; i<Parameters.SensorNum; i++){
			startDate[i] = format1.parse(dateStr[i]);//变为Date类型
			//System.out.println(startDate[i]);
		}
		/*
		for (int i = 0; i < startDate.length; i++) {
			startDate[i]=String2Date.strTDate(dateStr[i]);
		}*/
		
		Date DateMax = FindMaxDate(startDate);
		this.dateMax =DateMax;
		int[] DateDifferntInt =new int[startDate.length];
		for (int i = 0; i < DateDifferntInt.length; i++) {
			
			DateDifferntInt[i]=Math.abs((int) ((startDate[i].getTime()-DateMax.getTime())/1000 ));					
		}
		
		/*for (int i = 0; i < DateDifferntInt.length; i++) {
			//System.out.println(DateDifferntInt[i]);
		}*/
        return DateDifferntInt;
	}

	private static Date FindMaxDate(Date[] aDates) {
		// TODO Auto-generated method stub
		Date MaxDate = aDates[0];

		for (int i = 0; i < aDates.length; i++) {

			int cha = (int) ((MaxDate.getTime() - aDates[i].getTime()) / 1000);

			if (cha <= 0) {
				MaxDate = aDates[i];
			}

		}

		return MaxDate;
	}
		
		
		
		
		
}
