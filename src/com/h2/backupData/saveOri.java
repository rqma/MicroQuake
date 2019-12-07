package com.h2.backupData;

import java.util.Vector;

import com.h2.constant.Parameters;

import mutiThread.MainThread;

public class saveOri {
	public String []panfu = new String[MainThread.fileStr.length];
	public saveOri() {
		for(int i=0;i<Parameters.SensorNum;i++)
			panfu[i] = MainThread.fileStr[i];
		
		for(int i = 0; i<panfu.length; i++) {
			panfu[i] = panfu[i].replace(":/","");
		}
	}
	/**
	 * 将容器数据写入记事本,写入60s的数据
	 * @param data 传入的容器数据
	 * @param path 存储实际盘符
	 * @param motiPos 当该行数据为激发位置时，记录为1，否则为0
	 */
	public void saveOrii(Vector<String> data,String panfu,int[] motiPos){
		
		String da = data.get(0).split(" ")[6];//将日期中的：替换为空格，作为文件名
		String panfu1="";
		da=da.replace(":","");
		
		panfu1=panfu.replace(":/", "");
		
		
		for(int i=0;i<Parameters.diskName.length;i++)	
			if(panfu.equals(Parameters.diskName[i]) && Parameters.initPanfu[i]==0) {
				WriteMotiData.writemotidata(data,Parameters.AbsolutePathMinute+panfu1+"/"+this.panfu[i]+da+".txt",motiPos); 
				Parameters.initPanfu[i]=1;
			}
		

		
		//清空Vector
		data.clear();		
	}
}
