package com.h2.locate;

import java.text.ParseException;
import java.util.Vector;

import com.db.DbExcute;
import com.h2.constant.Parameters;
import com.h2.constant.Sensor;
import com.h2.main.EarthQuake;
import com.h2.thread.ThreadStep3;
import com.h2.tool.Doublelocate;
import com.h2.tool.Triplelocate;
import com.h2.tool.calDuringTimePar;
import com.h2.tool.stringJoin;

import bean.QuackResults;
import mutiThread.MainThread;
import utils.StringToDateTime;

public class Three_Locate {
	@SuppressWarnings("unused")
	public static String three(Sensor[] sensors, QuackResults aQuackResults, ThreadStep3[] sensorThread3,
			Vector<String> sensorData[][], int[] l, DbExcute aDbExcute, int countNumber) throws ParseException {
		
		//initialize the initPanfu variable.
		for(int i = 0;i<Parameters.SensorNum;i++)
			Parameters.initPanfu[i]=0;
		
		String outString=" ";
		int count=0;//count the final valid satisfied the conditions' sensors.
		int[] l1 = new int[Parameters.SensorNum];
		
		//We first need to diagnose all sensors that satisfy the conditions, when the number of motivated sensors are greater than 3, this function just starts the calculation. 
		for(int i = 0; i < countNumber; i++) {
			double e1=sensors[l[i]].getCrestortrough().getE1();
			if(Math.cos(Math.PI/4+e1/2)>=(-Parameters.S/Parameters.C)&&Math.cos(Math.PI/4+e1/2)<=(Parameters.S/Parameters.C)) {
				l1[count]=l[i];//record the sensors satisfied the angle conditions.
//				System.out.print(l1[count]);
				count++;
			}
		}
//		System.out.println();
		
		if(count<=2) {
			System.out.println("超出限制条件无法计算");			
		}
		
		//the number of sensors satisfy the condition is greater than 3, set the restrain to false.
		if(count>=3){
			//Take the top 3 to calculate the quake location and quake magnitude, it may need to optimize later.
			Sensor[] sensors1 = new Sensor[3];
	 		for(int i = 0; i < 3; i++) {
				sensors1[i]=sensors[l1[i]];
			}
			
			//compute the quake coordination.
			Sensor location=Triplelocate.tripleStationLocate(sensors1);
			
			//compute the quake time and quake coordination, the two sensors' locate is also adapt to the three location.
			location.setquackTime(Doublelocate.quakeTime(sensors1[0], location));
			//待定，不确定三台站时间算法，若不转换时间，则如何进行时间计算
//			Sensor location1_refine = Triplelocate.tripleStationLocate(triggersensor);
//			location1_refine.setquackTime(Doublelocate.quakeTime(triggersensor[0], location1));
			
			//we will cut the time to diagnose it's equal to "00:00:00".			
			String inteMotiTime = location.getquackTime().substring(10);
			String StartDay1 = "00:00:00";
			
			//the locate process probably return a NAN value or a INF value, so when this situation appears, the procedure will skip the current circulation.
			if(Double.isNaN(location.getLatitude())==false && Double.isInfinite(location.getLatitude()) == false){
				
				//The procedure will skip the current circulation when the time is equal to "00:00:00".
				if(inteMotiTime.compareTo(StartDay1) != 0){//当总的激发时间不等于“00:00:00”时，才进行后续计算，否则时间错误无法根据激发时间前后5s的数据计算震级
				
					Vector<String>  threeVectorOne1 = new Vector<String>();
					//integrate three vectors' content.
					for(int beforei = 0; beforei < sensors1.length; beforei++){
						for(int beforej = 0; beforej < sensorData[beforei].length; beforej++){
							for(int z=0; z<sensorData[beforei][beforej].size(); z++){
								threeVectorOne1.addElement(sensorData[beforei][beforej].get(z));//将5个传感器的三个容器的内容合起来保存到threeVectorOne数组中
							}
						}
						//Compute the near quake magnitude which data only must from the motivated sensors we selected, so the sensors1 is used.
						sensorThread3[beforei] = new ThreadStep3(sensors1[beforei],threeVectorOne1, location, beforei,3,l1);
						sensorThread3[beforei].calPos();
						threeVectorOne1.clear();//clear this vector and reassignment.	
					}
					
					//We integrate every sensors quake magnitude to compute the last quake magnitude.
					float earthQuakeFinal = 0;
					for (Sensor sen : sensors1)	earthQuakeFinal += sen.getEarthClassFinal();
					earthQuakeFinal /= 3;//For this method is only support 5 sensors, so we must divide 5 to calculate the last quake magnitude.
					if(Parameters.MinusAFixedOnMagtitude==true)
						earthQuakeFinal = (float) (earthQuakeFinal-0.7);// We discuss the consequen to minus 0.7 to reduce the final quake magnitude at datong coal mine.
					
					//calculate the during grade with 3 sensors.
					float duringEarthQuake = calDuringTimePar.computeDuringQuakeMagnitude(sensors1,3);
					
					//we will set 0 when the consequence appears NAN value.
					String quakeString = (Float.compare(Float.NaN, earthQuakeFinal) == 0) ? "0"	: String.format("%.2f", earthQuakeFinal);//修改震级，保留两位小数
					double quakeStringDuring = (Float.compare(Float.NaN, duringEarthQuake) == 0) ? 0:  duringEarthQuake;//修改震级，保留两位小数
					String result = location.toString() + " " + location.getquackTime() + " " + quakeString;//坐标+时间+震级
					
					//将各double坐标转换成数据库组形式
					java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
					nf.setGroupingUsed(false);
					
					aQuackResults.setxData(Double.parseDouble(nf.format(location.getLatitude())));
					aQuackResults.setyData(Double.parseDouble(nf.format(location.getLongtitude())));
					aQuackResults.setzData(Double.parseDouble(nf.format(location.getAltitude())));
					aQuackResults.setQuackTime(StringToDateTime.getDateSql(location.getquackTime()));
					aQuackResults.setQuackGrade(Double.parseDouble(quakeString));
					aQuackResults.setDuringGrade(quakeStringDuring);//持续时间震级
					
					aQuackResults.setPanfu(sensors1[0].getpanfu());
					aQuackResults.setNengliang(0);//能量，待解决
					aQuackResults.setFilename_S(sensors1[0].getFilename());

					//output the three locate consequence.
					System.out.println("三台站："+aQuackResults.toString());//在控制台输出结果
					
					//diagnose is not open the function of storing into the database.
					if(Parameters.isDatabase ==1) {
						try {
							aDbExcute.addElement3(aQuackResults);
						} catch (Exception e) {
							System.out.println("add to database error");
						}
					}
					//initialize the initPanfu variable.
					for(int i = 0;i<Parameters.SensorNum;i++)
						Parameters.initPanfu[i]=0;
					outString = result;
					return outString;
				}
			}
			//initialize the initPanfu variable.
			for(int i = 0;i<Parameters.SensorNum;i++)
				Parameters.initPanfu[i]=0;
			return outString;
		}
		return outString;
	}
}
