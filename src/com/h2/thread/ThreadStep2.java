package com.h2.thread;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import com.h2.backupData.BackUp;
import com.h2.constant.Sensor;

/**
 * 用于存储前后5秒数据的多线程，目前废弃不用了
 * 
 * @author 韩百硕
 *
 */
public class ThreadStep2 extends Thread
{
	private Vector<String> before;// 存储过去的10秒的数据
	private Vector<String> now;// 存储正在处理的10秒的数据
	private Vector<String> after;// 存储未来的10秒的数据

	CountDownLatch count;
	Sensor sensor;// 保存有备份数据文件的位置

	public ThreadStep2(Vector<String> b, Vector<String> n, Vector<String> a, CountDownLatch c, Sensor s)
	{
		this.before = b;
		this.now = n;
		this.after = a;
		this.count = c;
		this.sensor = s;
	}

	@Override
	public void run()
	{
		// 存储前后10秒的数据
		BackUp.backUpData(before, now, after, sensor);
		// 线程结束标识减一
		count.countDown();
	}
}
