package com.yang.readFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.h2.constant.Parameters;
import com.yang.util.FindByte;

/**
 * the class reads date segment head ,
 * but only returns the date of the first data segment 
 * @author hyena
 *
 */
public class ReadDataSegmentHead {
	
	
     
	
	/**
	 * returns the string date of the first data segment ,
	 * you can change this string date to any form whatever you want !
	 * eg: dateString : 20161126173902    
	 * year : 2016 
	 * month: 11
	 * day  : 26 
	 * hour : 17  5pm
	 * min  : 39 
	 * sec  : 02
	 *
	 * change them to the format you desired :
	 * String dateString  = readDateSegmentHead.readDataSegmentHead(fileName) ;
	 *	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss") ;
	 *	Date date = simpleDateFormat.parse(dateString) ;
	 * 
	 * @param fileName
	 * @return  String  represents the date of the first data segment
	 * @throws Exception
	 */
	public String readDataSegmentHead(String fileName) throws Exception{
		
		
		//用于承装数据段头的字节
		byte[] dataSegmentHeadByte = new byte[34] ;
		File file = new File(fileName) ;
		
		//打开流
		BufferedInputStream buffered = new BufferedInputStream(new FileInputStream(file)) ;
		
		buffered.skip(Parameters.WenJianTou) ;
		
		buffered.read(dataSegmentHeadByte);
		
		buffered.close() ;
		
		byte[] segmentDate = FindByte.searchByteSeq(dataSegmentHeadByte, 8, 17) ;
		
		int year = segmentDate[0] ;
		int month = segmentDate[1] ;
		int day = segmentDate[2] ;
		int hour = segmentDate[3] ;
		int min = segmentDate[4] ;
		int sec = segmentDate[5] ;
		
		String dateString = "20" + Integer.toString(year) + "-" +
				(month < 10 ? "0" + Integer.toString(month) : Integer.toString(month)) + "-" + 
				(day < 10 ? "0" + Integer.toString(day) : Integer.toString(day))  + "." +
				(hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour)) + ":" +
				(min < 10 ? "0" + Integer.toString(min) : Integer.toString(min)) + ":" +
				(sec < 10 ? "0" + Integer.toString(sec) : Integer.toString(sec)) ;
		
		
		
		return dateString ;
		
		
		
	}
	
	/**
	 * returns the string date of the first data segment ,
	 * you can change this string date to any form whatever you want !
	 * eg: dateString : 20161126173902    
	 * year : 2016 
	 * month: 11
	 * day  : 26 
	 * hour : 17  5pm
	 * min  : 39 
	 * sec  : 02
	 *
	 * change them to the format you desired :
	 * String dateString  = readDateSegmentHead.readDataSegmentHead(fileName) ;
	 *	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss") ;
	 *	Date date = simpleDateFormat.parse(dateString) ;
	 * 
	 * @param fileName
	 * @return  String  represents the date of the first data segment
	 * @throws Exception
	 */
     public String readDataSegmentHead(File file) throws Exception{
		
		//用于承装数据段头的字节
		byte[] dataSegmentHeadByte = new byte[34] ;
		
		
		//打开流
		BufferedInputStream buffered = new BufferedInputStream(new FileInputStream(file)) ;
		buffered.skip(Parameters.WenJianTou) ;
		int count = buffered.read(dataSegmentHeadByte);
		//System.out.println("count :  " + count) ;
		buffered.close() ;
		byte[] segmentDate = FindByte.searchByteSeq(dataSegmentHeadByte, 8, 17) ;
		String startDate;
		startDate = "20" + segmentDate[0]+"-";
		startDate = startDate + segmentDate[1]+"-";
		startDate = startDate + segmentDate[2]+".";
		startDate = startDate + segmentDate[3]+":";
		startDate = startDate + segmentDate[4]+":";
		startDate = startDate + segmentDate[5];
		
		return startDate ;	
     }
}
