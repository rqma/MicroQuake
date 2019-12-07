package com.h2.backupData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import com.h2.constant.Parameters;
import com.h2.constant.Sensor;

public class BackUp
{
	/**
	 * 备份发震时刻前后5秒的数据
	 * 
	 * @param before
	 *            前10秒的数据
	 * @param now
	 *            正在进行计算的十秒的数据
	 * @param after
	 *            后边的10秒的数据
	 * @param sensor
	 *            正在处理的传感器，存储有备份文件的位置
	 */
	public static void backUpData(Vector<String> before, Vector<String> now, Vector<String> after, Sensor sensor)
	{
		BufferedWriter writer = null;
		File fi = new File(sensor.getBackupFile());
		try
		{
			if (!fi.exists())
			{
				fi.createNewFile();
			}
		} catch (Exception e)
		{
			System.out.println("传感器-" + sensor.toString() + "-激发数据存储失败！");
		}

		try
		{
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer = new BufferedWriter(new FileWriter(fi, true));

			writer.write("---------------------------------------------------------\n");
			writer.write("传感器" + sensor.toString() + "的激发时间:    " + sensor.getTime() + "\n");// 传感器的激发时间
			writer.write("---------------------------------------------------------\n");

			// 1：获取正在进行计算的数据的时间
			String dataFileName = now.get(0).split(" ")[6];

			Calendar calFile = Calendar.getInstance();
			DateFormat df = new SimpleDateFormat("yyyy-MM-ddHH-mm-ss");
			try
			{
				calFile.setTime(df.parse(dataFileName));
			} catch (ParseException e)
			{
				System.out.println("数据文件时间转换错误！");
				e.printStackTrace();
			}
			// 2：获取激发时间
			Calendar calSign = Calendar.getInstance();
			try
			{
				calSign.setTime(df.parse(sensor.getTime()));
			} catch (ParseException e)
			{
				System.out.println("激发时间转换错误！");
				e.printStackTrace();
			}
			// 3：计算两者的差值,此处进行到天的计算，指从月初到第x天之前经历了多少秒
			int diff = (calSign.get(Calendar.DAY_OF_MONTH) - calFile.get(Calendar.DAY_OF_MONTH)) * 3600 * 24
					+ (calSign.get(Calendar.HOUR_OF_DAY) - calFile.get(Calendar.HOUR_OF_DAY)) * 3600
					+ (calSign.get(Calendar.MINUTE) - calFile.get(Calendar.MINUTE)) * 60
					+ (calSign.get(Calendar.SECOND) - calFile.get(Calendar.SECOND));
			// 4：存储数据
			if (diff <= 5)// 需要从before中读取数据，存储before和now中的数据
			{
				// 保存before中的数据
				int count = (diff + 5) * Parameters.FREQUENCY;// 读的第一条数据
				int count0 = (5 - diff) * Parameters.FREQUENCY;// 总共要读的数据数
				while (count0 > 0)
				{
					writer.write(before.get(count++) + "\n");
					count0--;
				}
				// 保存now中(diff+5)秒内的数据
				int count1 = (diff + 5) * Parameters.FREQUENCY;
				int i = 0;
				while (i < count1)
				{
					writer.write(now.get(i) + "\n");
					i++;
				}

			} else// 从after中读取数据，还要保存now中的数据
			{
				int begin = (diff - 5) * Parameters.FREQUENCY;// 开始读
				int end = (diff - 5) * Parameters.FREQUENCY;// after结束的记录数
				while (begin < 10 * Parameters.FREQUENCY)
				{
					writer.write(now.get(begin++) + "\n");
				}
				int i = 0;
				while (i < end)
				{
					writer.write(after.get(i++) + "\n");
				}
			}
		} catch (IOException e){e.printStackTrace();}
		finally
		{
			try{if (writer != null)	writer.close();}
			catch (IOException e){e.printStackTrace();}
		}
	}
}
