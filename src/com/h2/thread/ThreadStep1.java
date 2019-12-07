/**
 * @author 韩百硕
 */
package com.h2.thread;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import com.h2.constant.Sensor;
import com.h2.tool.SensorTool;

/**
 * 用于完成激发的验证步
 * @revision 2019-12-3
 * We revise this function to simplify the number of variables. 
 * @author 韩百硕 、张翰林（BaiShuo Han, HanLin Zhang）
 */
public class ThreadStep1 
{
	private Vector<String> now; //It's used to store the data of 10 seconds.
	private Sensor sensor; // It's used to store the status of every sensor.
	public ThreadStep1(Vector<String> now, Sensor s)
	{
		this.now = now;
		this.sensor = s;
	}
	
	/**
	 * 判断当前传感器是否激发
	 * @parameter active 判断激发
	 * 
	 */
	public void judgeMoti()
	{
		//set the motivation flag, when the condition achieves, the flag of the motivation is set to true.
		boolean active = SensorTool.motivate(now, sensor);//存储了激发时间和激发的标志位
		//sensor object is used universally after definition in the 'sensorTool.java'.
		sensor.setSign(active);
	}
}
