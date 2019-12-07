package com.h2.locate;

import java.text.ParseException;
import java.util.Vector;

import com.db.DbExcute;
import com.h2.backupData.WriteRecords;
import com.h2.constant.Parameters;
import com.h2.constant.Sensor;
import com.h2.thread.ThreadStep3;
import com.h2.tool.Location;
import com.h2.tool.Location2;
import com.h2.tool.calDuringTimePar;

import bean.QuackResults;
import utils.StringToDateTime;

public class Five_Locate {
	@SuppressWarnings("unused")
	public static String five(Sensor[] sensors, QuackResults aQuackResults, ThreadStep3[] sensorThread3,
			Vector<String> sensorData[][], int[] l, DbExcute aDbExcute) throws ParseException {
		
		String outString=" ";
		//Take the top 5 to calculate the quake location and quake magnitude, it may need to optimize later.
		Sensor[] sensors1 = new Sensor[5];
		for(int i = 0; i < 5; i++) {
			sensors1[i]=sensors[l[i]];
		}
		
		//calculate the coordinations of the quake source, location variable only store the quake time, not store the motivation time, and store the coordinations of the quake happening.
		Sensor location = Location.getLocation(sensors1);//calculate the quake time in seconds.
		Sensor location_refine = Location2.getLocation(sensors1);//calculate the quake time in milliseconds.
		
		//As the calculation may produce the illegal consequence in time and second, so we will cut the time String to diagnose the time isn't legal.
		String intequackTime = location.getquackTime().substring(10);
		//when the time is "00:00:00", the time is illegal, so we will not enter the quake calculation.
		String StartDay = "00:00:00";
		
		if(intequackTime.compareTo(StartDay) != 0){
			//we also record the quake location in a '.txt' file, for calculating manually.
			if(Parameters.isStorageToDisk==1) {
				WriteRecords.Write(sensors1,location,location_refine,Parameters.AbsolutePathMan);
			}
			//we define a new Vector<String> variable called 'threeVectorOne' to store the three vector contents.
			Vector<String>  threeVectorOne = new Vector<String>();
			//merge the three vectors' contents to calculate various of quake magnitudes.
			for(int beforei = 0; beforei < sensors1.length; beforei++){
				for(int beforej = 0; beforej < sensorData[beforei].length; beforej++){
					for(int z=0; z<sensorData[beforei][beforej].size(); z++){
						threeVectorOne.addElement(sensorData[beforei][beforej].get(z));//将5个传感器的三个容器的内容合起来保存到threeVectorOne数组中
					}
				}
				sensorThread3[beforei] = new ThreadStep3(sensors1[beforei],threeVectorOne, location, beforei,5,l);
				sensorThread3[beforei].calPos();//计算单个传感器的近震震级
				threeVectorOne.clear();//clear this vector and reassignment.
			}
			
			
			//We integrate every sensors quake magnitude to compute the last quake magnitude.
			float earthQuakeFinal = 0;
			for (Sensor sen : sensors1)	earthQuakeFinal += sen.getEarthClassFinal();
			earthQuakeFinal /= 5;//For this method is only support 5 sensors, so we must divide 5 to calculate the last quake magnitude.
			if(Parameters.MinusAFixedOnMagtitude==true)
				earthQuakeFinal = (float) (earthQuakeFinal-0.7);// We discuss the consequen to minus 0.7 to reduce the final quake magnitude at datong coal mine.
			
			//calculate the during grade with 5 sensors.
			float duringEarthQuake = calDuringTimePar.computeDuringQuakeMagnitude(sensors1,5);
			
			//we will set 0 when the consequence appears NAN value.
			String quakeString = (Float.compare(Float.NaN, earthQuakeFinal) == 0) ? "0"	: String.format("%.2f", earthQuakeFinal);//修改震级，保留两位小数
			double quakeStringDuring = (Float.compare(Float.NaN, duringEarthQuake) == 0) ? 0:  duringEarthQuake;//修改震级，保留两位小数
			String result = location.toString() + " " + location.getquackTime() + " " + quakeString + " " + quakeStringDuring;
			
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			
			aQuackResults.setxData(Double.parseDouble(nf.format(location_refine.getLatitude())));
			aQuackResults.setyData(Double.parseDouble(nf.format(location_refine.getLongtitude())));
			aQuackResults.setzData(Double.parseDouble(nf.format(location_refine.getAltitude())));
			aQuackResults.setQuackTime(StringToDateTime.getDateSql(location.getquackTime()));//粗计算时间，用于存入数据库和显示
			aQuackResults.setQuackGrade(Double.parseDouble(quakeString));//近震震级
			aQuackResults.setDuringGrade(quakeStringDuring);//持续时间震级
			aQuackResults.setParrival(location_refine.getSecTime());//P波到时，精确到毫秒
			aQuackResults.setPanfu(sensors1[0].getpanfu());//盘符
			aQuackResults.setNengliang(0);//能量，待解决
			aQuackResults.setFilename_S(sensors1[0].getFilename());//文件名，当前第一个台站的文件名，其他台站需要进一步改变第一个字符为其他台站，则为其他台站的文件名。
			
			//output the five locate consequence.
			System.out.println("五台站："+aQuackResults.toString());//在控制台输出结果
			
			//diagnose is not open the function of storing into the database.
			if(Parameters.isDatabase == 1) {
				try {
					aDbExcute.addElement(aQuackResults);
				} catch (Exception e) {
					System.out.println("add to database error");
				}
			}
			outString = result;
			return outString;
		}
		return outString;
	}
}
