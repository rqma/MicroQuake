package controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import com.h2.constant.Parameters;
import com.yang.readFile.ReadData;

import utils.FindByte;

/**
 * 读数据段头时间，读取第一个数据段头的时间
 */
public class ReadDataSegmentHead {

	public static String readDataSegmentHead(String fileName) throws Exception {

		// 用于承装数据段头的字节
		byte[] dataSegmentHeadByte = new byte[34];
		File file = new File(fileName);
		// 打开流
		BufferedInputStream buffered = new BufferedInputStream(new FileInputStream(file));
		buffered.skip(Parameters.WenJianTou);
		buffered.read(dataSegmentHeadByte);
		buffered.close();
		byte[] segmentDate = FindByte.searchByteSeq(dataSegmentHeadByte, 8, 17);
		String startDate;
		startDate = "20" + segmentDate[0]+"-";
		startDate = startDate + segmentDate[1]+"-";
		startDate = startDate + segmentDate[2]+" ";
		startDate = startDate + segmentDate[3]+":";
		startDate = startDate + segmentDate[4]+":";
		startDate = startDate + segmentDate[5];
		return startDate;
	}

	public String readDataSegmentHead(File file) throws Exception {
		// 用于承装数据段头的字节
		byte[] dataSegmentHeadByte = new byte[34];
		// 打开流
		BufferedInputStream buffered = new BufferedInputStream(new FileInputStream(file));
		buffered.read(dataSegmentHeadByte, 284, 34);
		buffered.close();
		byte[] segmentDate = FindByte.searchByteSeq(dataSegmentHeadByte, 8, 17);
		int year = segmentDate[0];
		int month = segmentDate[1];
		int day = segmentDate[2];
		int hour = segmentDate[3];
		int min = segmentDate[4];
		int sec = segmentDate[5];
		String dateString = Integer.toString(year) + Integer.toString(month) + Integer.toString(day) + Integer.toString(hour) + Integer.toString(min) + Integer.toString(sec);
		return dateString;
	}
}
