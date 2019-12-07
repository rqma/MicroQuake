package com.h2.tool;

import com.h2.constant.Parameters;
import com.h2.constant.Sensor;
import com.h2.main.EarthQuake;

/**
 * this function is used to get the relative P arrival time point.
 * @author Hanlin Zhang
 *
 */
public class relativeStatus {
	@SuppressWarnings("unused")
	public static void P_RelativeArrivalTime(Sensor[] sensors) {
		int nqk=-1;
		for(int i =0;i<Parameters.SensorNum;i++){
			if(sensors[i].getSecTime()!=0.0){
				nqk=i;//记录第一个不为零的传感器
				break;
			}
		}
		if(nqk>-1){
			for(int i=nqk+1;i<Parameters.SensorNum;i++){
				if(sensors[i].getSecTime()!=0.0){//若到时不为0，则说明激发了
					sensors[i].setSecTime(sensors[i].getSecTime()-sensors[nqk].getSecTime());//主要减去第一个不为零的时间
				}
			}
			sensors[nqk].setSecTime(0.0);//设置第一个传感器到时为0
			for(int i =0;i<Parameters.SensorNum;i++){//将相对到时时间相差大于1s的数据不作震级计算处理
//				System.out.println("P波到时："+sensors[i].getSecTime());
				if(Parameters.Adjust==true){
					EarthQuake.realMoti=true;//其中若有一个台站的相对其他台站相差大于1s则认为该事件不是同时激发的，且他们无效	
					break;
				}
				else{
					if(Parameters.SSIntervalToOtherSensors==true){
						if(Math.abs(sensors[i].getSecTime())>Parameters.IntervalToOtherSensors) {
							EarthQuake.realMoti=false;//其中若有一个台站的相对其他台站相差大于1s则认为该事件不是同时激发的，且他们无效	
						}
					}
					else{
						EarthQuake.realMoti=true;	
						break;
					}
				}
				
			}
		}		
	}
}
