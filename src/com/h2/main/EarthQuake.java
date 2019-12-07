
package com.h2.main;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.math.*;
import utils.StringToDateTime;
import bean.QuackResults;
import java.beans.*;
import mutiThread.MainThread;

import com.db.DbExcute;
import com.h2.backupData.WriteMotiData;
import com.h2.backupData.WriteRecords;
import com.h2.backupData.recordNegativeGrade;
import com.h2.constant.Parameters;
import com.h2.constant.Sensor;
import com.h2.locate.Five_Locate;
import com.h2.locate.Three_Locate;
import com.h2.thread.ThreadStep1;
import com.h2.thread.ThreadStep2;
import com.h2.thread.ThreadStep3;
import com.h2.tool.CrestorTrough;
import com.h2.tool.Doublelocate;
import com.h2.tool.Location;
import com.h2.tool.Location2;
import com.h2.tool.QuakeClass;
import com.h2.tool.SensorTool;
import com.h2.tool.Triplelocate;
import com.h2.tool.calDuringTimePar;
import com.h2.tool.relativeStatus;
import com.h2.tool.stringJoin;

/**
 * @revision 2019-12-3
 * We revise this function on the locating of three sensors and five sensors, which is packaged to a function to manage convenient.
 * And we also delete some variables to simplify the function such as threadstep1~threadstep3.
 * @author 韩百硕,Hanlin Zhang, Yali Hao, Ze Chen, et al.
 */
public class EarthQuake {
	/**
	 * @param ssen all sensors' data in three vectors.
	 * @return the " " or the consequence of computation, " " indicates there are no sensors are inspired or the number of data is not enough.
	 */
	
	/**Return computation results String.*/
	public static String outString = "";
	
	/**Used to execute the sql of database.*/
	static DbExcute aDbExcute =new DbExcute();

	/**store the computation consequence of every location methods.*/
	static QuackResults aQuackResults=new QuackResults();
	
	/**indicate the motivation is not a real valid motivation, if this is a real motivation it will become true, or it will be set to false.*/
	public static boolean realMoti = true;
	
	@SuppressWarnings("unused")
	public static String runmain(Vector<String> ssen[][])	throws Exception {
		
		//the number of data must enough to calculate, or it will appear mistake consequence for the current data.
		for (Vector<String>[] vectors : ssen) {
			for (Vector<String> vector : vectors) {
				if (vector.size() < Parameters.FREQUENCY * 10)	return " ";//this function must be return a String to foreground.
			}
		}
		// --------------------------------------------------------------------------初始化传感器信息，传感器信息后期不会改变
		
		//We must initialize the Sensor object, when the procedure first use.
		Sensor[] sensors = SensorTool.initSensorInfo(Parameters.SensorNum,MainThread.fileStr);//设置传感器地理坐标 备份文件位置 
			
		// sensorData[2][1] indicate the third sensor's second vector.
		Vector<String> sensorData[][] = ssen;
		ThreadStep1[] sensorThread1 = new ThreadStep1[Parameters.SensorNum];
		ThreadStep3[] sensorThread3 = new ThreadStep3[Parameters.SensorNum];
		
		//Set every sensor's motivation flag to indicate the sensor is inspired or not.
		for (int bei = 0; bei < Parameters.SensorNum; bei++) {
			sensorThread1[bei] = new ThreadStep1(sensorData[bei][1], sensors[bei]);
			sensorThread1[bei].judgeMoti();
		}
		
		//get the relative time point as millisecond, this function is update the sensors object's setSecTime method's Sectime variable.
		relativeStatus.P_RelativeArrivalTime(sensors);
		
		//calculate the number of motivation sensors, and save the series to the l array.
		//Meanwhile, we will move the position to the absolute position in 30 seconds, which is point to the position in the now Vector. It's used to cut the motiData and used to calculate during quake magnitude.
		int countNumber = 0;
		int[] l = new int[Parameters.SensorNum];
		for (int i=0;i<Parameters.SensorNum;i++){
			if (sensors[i].isSign()) {
				l[countNumber]=i;//record the motivated sensors.
				countNumber++;
				sensors[i].setlineSeries(sensors[i].getlineSeries()+sensorData[i][0].size());
			}
		}
		
		//if countNumber>=5, the procedure start calculating the earthquake magnitude and the location of quake happening.
		if (countNumber >= 5 && EarthQuake.realMoti==true) {
			outString = Five_Locate.five(sensors, aQuackResults, sensorThread3, sensorData, l, aDbExcute);	
		}
		
		//if the number of motivated sensors is greater than 3, we will calculate three location.
		if(countNumber>=3 && EarthQuake.realMoti==true){
			outString = Three_Locate.three(sensors, aQuackResults, sensorThread3, sensorData, l, aDbExcute, countNumber);
		}
		
		//output the reasons why the calculation process is not executing.
		System.out.println("激发个数："+countNumber+" 台站间的激发间隔时间是否小于"+Parameters.IntervalToOtherSensors+"?"+
				EarthQuake.realMoti+""+	sensorData[0][0].get(0).split(" ")[6]);
		
		//reset the global variable.
		EarthQuake.realMoti=true;
		return outString;
	}
}
