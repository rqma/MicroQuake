package utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class ReadDataSegmentHead {

	public  String readDataSegmentHead(String fileName) throws Exception {

		// 用于承装数据段头的字节
		byte[] dataSegmentHeadByte = new byte[34];
		File file = new File(fileName);

		// 打开流
		BufferedInputStream buffered = new BufferedInputStream(
				new FileInputStream(file));

		buffered.skip(284);

		buffered.read(dataSegmentHeadByte);

		buffered.close();

		byte[] segmentDate = FindByte.searchByteSeq(dataSegmentHeadByte, 8, 17);

		int year = segmentDate[0];
		int month = segmentDate[1];
		int day = segmentDate[2];
		int hour = segmentDate[3];
		int min = segmentDate[4];
		int sec = segmentDate[5];

		String dateString = "20"
				+ Integer.toString(year)
				+ (month < 10 ? "0" + Integer.toString(month) : Integer
						.toString(month))
				+ (day < 10 ? "0" + Integer.toString(day) : Integer
						.toString(day))
				+ (hour < 10 ? "0" + Integer.toString(hour) : Integer
						.toString(hour))
				+ (min < 10 ? "0" + Integer.toString(min) : Integer
						.toString(min))
				+ (sec < 10 ? "0" + Integer.toString(sec) : Integer
						.toString(sec));

		return dateString;

	}

	public String readDataSegmentHead(File file) throws Exception {

		// 用于承装数据段头的字节
		byte[] dataSegmentHeadByte = new byte[34];

		// 打开流
		BufferedInputStream buffered = new BufferedInputStream(
				new FileInputStream(file));

		buffered.read(dataSegmentHeadByte, 284, 34);

		buffered.close();

		byte[] segmentDate = FindByte.searchByteSeq(dataSegmentHeadByte, 8, 17);

		int year = segmentDate[0];
		int month = segmentDate[1];
		int day = segmentDate[2];
		int hour = segmentDate[3];
		int min = segmentDate[4];
		int sec = segmentDate[5];

		String dateString = Integer.toString(year) + Integer.toString(month)
				+ Integer.toString(day) + Integer.toString(hour)
				+ Integer.toString(min) + Integer.toString(sec);

		return dateString;

	}
}
