/**
 * @author 韩百硕
 */
package com.h2.tool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import com.h2.constant.Parameters;
import com.h2.constant.Sensor;

/**
 * @author 韩百硕
 *
 */
public class SensorTool
{
	/**
	 * 初始化传感器信息
	 * 
	 * @param count
	 *            传感器的数量
	 * @return
	 */
	@SuppressWarnings("unused")
	public static Sensor[] initSensorInfo(int count,String [] Str)
	{
		Sensor[] sensors = new Sensor[count];
		int [] k = new int [5];//存储盘符对应的盘号
		if(Parameters.offline==false) {
			for(int j=0;j<Str.length;j++){
				for(int i=0;i<Parameters.diskName.length;i++)
					if(Str[j] .equals(Parameters.diskName[i])) k[j]=i;
			}
			
			for (int i = 0; i < count; i++)
			{
				sensors[i]=new Sensor();
				//(sensors[i]).setBackupFile(Parameters.EARTHDATAFILE[k[i]]);
				(sensors[i]).setLatitude(Parameters.SENSORINFO[k[i]][0]);
				(sensors[i]).setLongtitude(Parameters.SENSORINFO[k[i]][1]);
				(sensors[i]).setAltitude(Parameters.SENSORINFO[k[i]][2]);
			}
			return sensors;
		}else {
			for(int j=0;j<Str.length;j++){
				for(int i=0;i<Parameters.diskName_offline.length;i++)
					if(Str[j].substring(Str[j].length()-2, Str[j].length()-1) .equals(Parameters.diskName_offline[i]))
						k[j]=i;
			}
			if(Parameters.region_offline=="datong") {
				for (int i = 0; i < count; i++)
				{
					sensors[i]=new Sensor();
					//(sensors[i]).setBackupFile(Parameters.EARTHDATAFILE[k[i]]);
					(sensors[i]).setLatitude(Parameters.SENSORINFO_offline_datong[k[i]][0]);
					(sensors[i]).setLongtitude(Parameters.SENSORINFO_offline_datong[k[i]][1]);
					(sensors[i]).setAltitude(Parameters.SENSORINFO_offline_datong[k[i]][2]);
				}
			}
			if(Parameters.region_offline=="pingdingshan") {
				for (int i = 0; i < count; i++)
				{
					sensors[i]=new Sensor();
					//(sensors[i]).setBackupFile(Parameters.EARTHDATAFILE[k[i]]);
					(sensors[i]).setLatitude(Parameters.SENSORINFO_offline_pingdingshan[k[i]][0]);
					(sensors[i]).setLongtitude(Parameters.SENSORINFO_offline_pingdingshan[k[i]][1]);
					(sensors[i]).setAltitude(Parameters.SENSORINFO_offline_pingdingshan[k[i]][2]);
				}
			}
			return sensors;
		}
		
	}

	/**
	 * 在这10s内激发的传感器,并设置激发传感器的标识和激发时间
	 * 
	 * @param data
	 * @param sensor
	 * @return
	 */
	private static boolean tunnel=false;
	public static boolean motivate(Vector<String> data, Sensor sensor)
	{
		int lineSeries = 0;
		
//		for (int i = 0; i < Parameters.WINDOWNUMBER; i++)//时间窗口共166个
		for(int i=0;i<data.size()-2*Parameters.N;i+=Parameters.INTERVAL)//滑动窗口跳数为100，i为窗口的第一条数据开始位置，到最后一个窗口
		{
			if(!sensor.isSign())//若激发标志为false
			{
				lineSeries=getToken(data, i);//激发位置，滑动距离为Parameters.INTERVAL个点，并判断使用哪三个通道
				
				//若激发，基本都是最后一个时窗
				if(lineSeries!=0)
				{
					//设置标志位
					sensor.setSign(true);
					
					//此时设置的激发位置，为now容器中的位置，即相对位置，在EarthQuack中，继续将Before容器中的数据点数加起来，进行处理。
					sensor.setlineSeries(lineSeries);
					
					//单位是毫秒，P波到时（也仅仅是相对到时），*号后面是每两个数据之间的间隔时间，以毫秒为单位，频率的倒数
					sensor.setSecTime(lineSeries*1/Parameters.FREQUENCY);
					
//					System.out.println("P波到时："+sensor.getSecTime());
//					sensor.setTime(getMotiTime(data.get(0).split(" ")[6], i));//获取now容器中的第7列，转换为yyMMddhhmmss形式
					
					//获取激发位置的日期，作为计算年月日类型日期使用，并最终存放数据库使用
					sensor.setTime(data.get(lineSeries).split(" ")[6]);
//					System.out.println("五个传感器的激发位置："+lineSeries);
//					System.out.println("五个传感器的激发时间："+sensor.getTime());
				}
				//取123通道，激发时窗最后一条数据
				if(tunnel){
					CrestorTrough temcre=new CrestorTrough(Double.parseDouble(data.get((i+1)+Parameters.N-1).split(" ")[0]),
														   Double.parseDouble(data.get((i+1)+Parameters.N-1).split(" ")[1]),
							                               Double.parseDouble(data.get((i+1)+Parameters.N-1).split(" ")[2]));
					sensor.setCrestortrough(temcre);
				}else{
					CrestorTrough temcre=new CrestorTrough(Double.parseDouble(data.get((i+1)+Parameters.N-1).split(" ")[3]),
							                               Double.parseDouble(data.get((i+1)+Parameters.N-1).split(" ")[4]),
                                                           Double.parseDouble(data.get((i+1)+Parameters.N-1).split(" ")[5]));
					sensor.setCrestortrough(temcre);
				}
			}
		}
		
		tunnel=false;
		return sensor.isSign() ? true : false;
	}

	/**
	 * 确定一个传感器在60ms内是否被激发 思想：将一个时窗内的数据(600条数据)全部读取到vector中，
	 * 并在读取的时候判断是否有数据超出阈值，跟据读取的结果给channel赋值。
	 * 
	 * @param sensor 传感器
	 * @param number 每隔一定点个数就进行一次长短时窗的判断
	 * @return 激发的位置
	 */
	@SuppressWarnings("unused")
	public static int getToken(Vector<String> data, int number)
	{
		// 这是以前写的，当时传入的数据不是vector，现在是vector，懒得改了
		int line=0;//初始化
		int[] iStr = new int[6]; // 存储记录切割值，因为后边用到分割s的值
		boolean channel = false;// 确定读取那三列数据，f表示读取345列，如果为t则读取123列
		int inte = -1;// 存储平均值
		String s;// 存储中间的值
		
		Vector<String> container = new Vector<String>();// 存储当前时窗的数据

		int count = 0;//循环控制

		long sumLong = 0;
		long sumShort = 0;

		double aveLong = 0;
		double aveShort = 0;
		
		//开始读取的第一条记录的编号
		int lineNumber = number;
		
		container.clear();
		
		// 读取本时窗的数据，拆分data容器的内容，保存到container容器中，并同时判断每个通道是否超出最大阈值，若超出，则启用另3个通道
		while (count < Parameters.N)//在某个激发的时窗中提取所有数据
		{
			s = data.get(lineNumber + count);
			String[] str = s.split(" ");
			
			for (int i = 3; i < 6; i++)// 共7列数据，最后一列为时间
			{
				iStr[i] = Integer.parseInt(str[i]);
			}

			if (!TestThreshold(iStr[3]) && !TestThreshold(iStr[4]) && !TestThreshold(iStr[5]))
			{
				container.add(s);
			} else
			{
				channel = true;
				tunnel=true;
				container.add(s);
			}
			count++;
		}

		//求长时窗平均值
		if (channel)// 读取前三列
		{
			for (int i = 0; i < Parameters.N1; i++)
			{
				String[] str = container.get(i).split(" ");
//				inte = (int) Math.hypot(Math.hypot(Integer.parseInt(str[0]), Integer.parseInt(str[1])),
//						Integer.parseInt(str[2]));
				
				//取出z分量
				inte = Math.abs(Integer.parseInt(str[2]));
				sumLong += inte;
			}
		} else
		{
			for (int i = 0; i < Parameters.N1; i++)
			{
				String[] str = container.get(i).split(" ");
//				inte = (int) Math.hypot(Math.hypot(Integer.parseInt(str[3]), Integer.parseInt(str[4])),
//						Integer.parseInt(str[5]));
				
				//取出z分量
				inte = Math.abs(Integer.parseInt(str[5]));
				sumLong += inte;
			}
		}
		aveLong = (double) sumLong / Parameters.N1;

		//求短时窗平均值
		if (channel)// 读取前三列
		{
			for (int i = Parameters.N1; i < Parameters.N; i++)//长时窗最后一个点到短时窗最后一个点
			{
				String[] str = container.get(i).split(" ");
//				inte = (int) Math.hypot(Math.hypot(Integer.parseInt(str[0]), Integer.parseInt(str[1])),
//						Integer.parseInt(str[2]));
				
				//取出z分量
				inte = Math.abs(Integer.parseInt(str[2]));
				sumShort += inte;
			}
		} else
		{
			for (int i = Parameters.N1; i < Parameters.N; i++)//长时窗最后一个点到短时窗最后一个点
			{
				String[] str = container.get(i).split(" ");
//				inte = (int) Math.hypot(Math.hypot(Integer.parseInt(str[3]), Integer.parseInt(str[4])),
//						Integer.parseInt(str[5]));
				
				//取出z分量
				inte = Math.abs(Integer.parseInt(str[5]));
				sumShort += inte;
			}
		}
		aveShort = (double) sumShort / Parameters.N2;
		if(Parameters.Adjust==false){
		//短长视窗比值大于2.5，并且振幅绝对值大于500时，判定为激发 
			if((aveShort / aveLong) >= Parameters.ShortCompareLong){
				if(channel){
					if(Math.abs(Integer.parseInt(container.get(Parameters.N-Parameters.N2).split(" ")[2])) > Parameters.ThresholdZ){
						line=lineNumber+Parameters.N1;
						return line;
					}
					else {
						return line;
					}
				}
				else{
					if(Math.abs(Integer.parseInt(container.get(Parameters.N-Parameters.N2).split(" ")[5])) > Parameters.ThresholdZ){
						line=lineNumber+Parameters.N-Parameters.N2;//记录最后一个激发时窗在now容器中的位置
						return line;
					}
					else {
						return line;
					}
				}
			}
			else return line;
		}
		else{//调试情况
			if((aveShort / aveLong) >= Parameters.ShortCompareLongAdjust){
				if(channel){
					line=lineNumber+Parameters.N1;
					return line;
				}
				else{
					line=lineNumber+Parameters.N-Parameters.N2;//记录最后一个激发时窗在now容器中的位置
					return line;
				}
			}
			else return line;
		}
	}

	/**
	 * 计算通道转换
	 * 
	 * @param num 传进来的值     
	 * @return 通道是否转换
	 */
	private static boolean TestThreshold(int num)
	{
		if (num > Parameters.HEAD || num < Parameters.TAIL)
		{
			return true;
		} else
		{
			return false;
		}

	}

	/**
	 * 计算传感器的激发时间
	 * 
	 * @param dataFileName
	 *            文件名中有时间
	 * @param i
	 *            第几个60ms
	 * @return 传感器的激发时间
	 *  
	 */
	private static String getMotiTime(String motivateDate, int i) 
	{
		Calendar cal = Calendar.getInstance();
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
		
		try
		{
			cal.setTime(df1.parse(motivateDate));
		} catch (ParseException e)
		{
			System.out.println("激发时间转换错误！："+motivateDate);
			e.printStackTrace();
		}
		cal.add(Calendar.SECOND, 60 * i / 1000);
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
	    Date motivateDate1 = cal.getTime();
		String cal1 = df2.format(motivateDate1);
		
		return cal1;
	}
//	public static Vector<String> mergeVector(Vector<String> vector1, Vector<String> vector2){
//
//		
//		Vector<String> t1 = new Vector<String>();
//		Vector<String> t2 = vector2;
//
//		for(int i = 0; i < t2.size(); i++)
//		  {
//		   		    
//		  // System.out.println(t);
//		   t1.add(vector2.get(i));
//		   
//		  }
//		
//		return t1;
//	}

}
