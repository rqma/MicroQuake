package com.h2.thread;

import java.text.ParseException;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import com.h2.constant.Sensor;
import com.h2.tool.QuakeClass;
import com.h2.tool.calDuringTimePar;

/**
 * 用于计算震级的多线程
 * 
 * @author 韩百硕
 *
 */
public class ThreadStep3 
{
	private Sensor sensor;
	private Vector<String> dataCalculate = new Vector<String>();
	private Sensor location;
	private int motiNum;
	private int i;
	private int[] l;
	public ThreadStep3(Sensor s, Vector<String> d, Sensor lo, int i,int motiNum,int[] l)
	{
		this.sensor = s;
		this.i=i;//sensor ID.
		this.dataCalculate.addAll(d);
		this.location = lo;
		this.motiNum = motiNum;
		this.l = l;
	}

	public void calPos() throws ParseException
	{
		//calculate the near quake magnitude.
		QuakeClass.SensorMaxFudu(this.sensor, this.dataCalculate, this.i, this.motiNum, this.location,this.l);
		QuakeClass.getOneEarthClass(location, sensor);
	}
}
