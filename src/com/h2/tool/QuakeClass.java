package com.h2.tool;

import java.awt.print.Printable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import com.h2.main.*;
import com.h2.backupData.WriteMotiData;
import com.h2.constant.Parameters;
import com.h2.constant.Sensor;
import com.h2.thread.ThreadStep3;


import mutiThread.MainThread;

public class QuakeClass
{
	/**
	 * compute the max amplitude of every sensor.
	 * @param sen the sensor status object used to store the status of every sensor.
	 * @param data the three vector data(30s) of every sensor.
	 */
	@SuppressWarnings("unused")
	public static void SensorMaxFudu(Sensor sen, Vector<String> data, int th, int motiNum, Sensor location,int [] l) throws ParseException
	{
		//保存剪切的字符型向量motiPreLa
		Vector<String> motiPreLa = new Vector<String>();
		
		//获取激发时间，以秒为单位
		String motiDate = sen.getTime();
		
		//记录激发的盘符（传感器），待接入数据库
		
		motiDate=motiDate.replace(":","");//替换掉时间中的:
		
		//切分为激发时间处前Parameters.startTime、后Parameters.endTime数据
		motiPreLa = cutOdata1(data, sen.getlineSeries(), Parameters.startTime, Parameters.endTime);
		
		int POS=0;
		POS = sen.getlineSeries();
		
		// 1：先扫一遍10秒的数据，确定用哪一个通道,顺便确定通道的最大值
		boolean flag = getFlag(motiPreLa, sen, th);
		
		// 2：获取b的时间
		getBTime(sen, flag, motiPreLa, th);// 单位是秒
		
		// 3：计算最大振幅,并将结果存入传感器
		sen.setFudu(getMaxA(sen, flag,motiPreLa,th));

		//计算持续时间震级		
//		sen.setDuring(calDuringTime(motiPreLa, sen, th));//持续时间，取lg，然后直接存入duringTime中，作为一个参数使用
		if(Parameters.offline==false) {
			
			String panfu = stringJoin.SJoin_Array(MainThread.fileStr,l);
			panfu=panfu.replaceAll(":/", "");//替换掉盘符中的:/
			
			sen.setpanfu(panfu);//存储盘符字符串
			
			String[] panfu1 = new String[Parameters.SensorNum];//替换掉所有盘符中的:/，用于作为文件名
			for(int i=0;i<Parameters.SensorNum;i++)
				panfu1[i] = MainThread.fileStr[i];
			for(int i=0;i<panfu1.length;i++) {
				panfu1[i]=panfu1[i].replace(":/", "");
			}
			
			//控制存储模块的开启与关闭
			if(Parameters.isStorageToDisk == 1){
				//存储5台站或更多激发数据至txt
				if(motiNum>3){
		//			if(location.getLatitude()>533990.667 && location.getLongtitude()>4417004.76
		//					&& location.getLatitude()<546000 && location.getLongtitude()<4429001.8){//在矿区内才进行激发事件的保存，否则，保存的激发次数太多。
					for(int i=0;i<Parameters.diskName.length;i++){
						if(MainThread.fileStr[th].equals(Parameters.diskName[i]) && Parameters.initPanfu[i]==0){
							WriteMotiData.writemotiData1(motiPreLa,Parameters.AbsolutePath5+panfu1[th]+"/"+panfu+motiDate+".txt",Parameters.FREQUENCY * Parameters.startTime);
							Parameters.initPanfu[i]=1;
							sen.setFilename_S(Parameters.AbsolutePath5+panfu1[th]+"/"+panfu+motiDate);//文件名
						}
					}
				}
				
				//存储3台站激发数据至txt
				if(motiNum==3){
					for(int i=0;i<Parameters.diskName.length;i++){
						if(MainThread.fileStr[th].equals(Parameters.diskName[i]) && Parameters.initPanfu[i]==0){
							WriteMotiData.writemotiData1(motiPreLa,Parameters.AbsolutePath3+panfu1[th]+"/"+panfu+motiDate+".txt",Parameters.FREQUENCY * Parameters.startTime);
							Parameters.initPanfu[i]=1;
							sen.setFilename_S(Parameters.AbsolutePath3+panfu1[th]+"/"+panfu+motiDate);
						}
					}
				}
			}
		}
		else {
			String panfu=stringJoin.SJoin_Array(MainThread.fileParentPackage,l);
			panfu = panfu.replaceAll("Test", "");
			
			String parent="";
			switch (MainThread.fileParentPackage[th]) {
				case "Test1": { parent="Test1";break; }
				case "Test2": { parent="Test2";break; }
				case "Test3": { parent="Test3";break; }
				case "Test4": { parent="Test4";break; }
				case "Test5": { parent="Test5";break; }
				case "Test6": { parent="Test6";break; }
				default: break;
			}
			if(Parameters.isStorageToDisk == 1){
				if (motiNum == 3) {
					WriteMotiData.writemotiData1(motiPreLa, Parameters.AbsolutePath3_offline+parent+"/" + panfu+motiDate+".txt", Parameters.FREQUENCY * Parameters.startTime);//写入10s的数据
					sen.setFilename_S(Parameters.AbsolutePath3_offline+parent+"//"+panfu+motiDate);//文件名
				}
				if (motiNum > 3) {
					WriteMotiData.writemotiData1(motiPreLa, Parameters.AbsolutePath5_offline+parent+"/" +panfu+motiDate+".txt", Parameters.FREQUENCY * Parameters.startTime);//写入10s的数据
					sen.setFilename_S(Parameters.AbsolutePath5_offline+parent+"//"+panfu+motiDate);//文件名
				}
			}
		}
	}
	
	/**
	 * 重新计算新到时点
	 * @param POS
	 * @param motiPreLa
	 * @param startTime
	 * @return POS 新到时点
	 */
	private static int reMotiPos(int POS,Vector<String> motiPreLa, int startTime){//重定位激发位置,在激发位置前后6s的数据中循环
		//切比雪夫1型滤波器,改用巴特沃兹滤波器，阶数不大于10
//		Double [] filteredBo = new Double[motiPreLa.size()];
		double [] filteredBo = new double[motiPreLa.size()];
		double [] motiPreLa1 = new double[motiPreLa.size()];
		
//		filteredBo = Filter.highpass(motiPreLa);
		
		for(int i=0;i<motiPreLa.size();i++) {//将容器中的数据提取出来
			
			motiPreLa1[i] = Double.parseDouble(motiPreLa.get(i).split(" ")[5]); 
		}
//		WriteMotiData.writeFiltered(motiPreLa1, "I:/111.txt");
		filteredBo = filter1.filt(motiPreLa1);
		int ser=0;//用于循环
//		WriteMotiData.writeFiltered(filteredBo, "I:/222.txt");
		int posInFilter = 0,newPos = 0;
		double T=0.0;//振幅阈值，取正负两个值
		//找出滤波后的波形长短时窗比值>10的位置
		posInFilter = motiDiag.motiExceed10(motiPreLa);//记录阈值大于10的位置（序号）
//		System.out.println("超出阈值的位置:"+posInFilter);
		if(posInFilter!=0) //新到时点存在
			newPos = posInFilter-1;//取该位置的前一个与曲线的交点，作为新的到时点
		//寻找振幅阈值，对原曲线进行处理，将大于振幅阈值的焦点找出，并记录序号
		double sumPre = 0;
		
		if(posInFilter!=0){
			for(int i=0;i<newPos;i++){//求初始位置到新到时点的总振幅和
				//向前寻找4秒的数据
				sumPre = sumPre + Math.abs(filteredBo[i]);
			}
			T = (sumPre/(newPos+1))*5;//求出平均值即阈值*5，该振幅阈值一定存在。
		}
		else{
			for(int i=0;i<POS;i++){//求初始位置到到原时点的总振幅和
				//向前寻找4秒的数据
				sumPre = sumPre + Math.abs(filteredBo[i]);
			}
			T = (sumPre/(POS+1))*5;
		}
		
		if(posInFilter!=0){//找到新到时点，则找老到时点附近1000个点是否超过阈值
			boolean flag1 = false;
			int k = 0;
			for(int i=newPos-500;i<newPos+500;i++){//前后500个点。
				if(Math.abs(filteredBo[i])>T){//若z方向的值大于阈值，则向前寻找500个点 数据，并进行滤波
					flag1 = true;//找到大于阈值的点
					k=i-1;//记录大于阈值的前一个位置，该位置一定为小于阈值的序号，从该点往前找500个点
					break;
				}//跳出for循环
			}
			
			if(flag1 == false){//若找不到大于振幅阈值的点，则新的到时点无效，我们继续使用原来程序中的老的到时点
				for(int i=POS-500;i<POS+500;i++){//前后500个点。
					if(Math.abs(filteredBo[i])>T){//若z方向的值大于阈值，则向前寻找500个点 数据，并进行滤波
						flag1 = true;//找到大于阈值的点
						k=i-1;//记录大于阈值的前一个位置，该位置一定为小于阈值的序号，从该点往前找500个点
						break;
					}//跳出for循环
				}
				if(flag1==false)//若还是找不到老到时点附近100个点大于阈值的点，只能用老到时点了
					return POS;//返回老到时点
				else{//若老到时点附近存在大于阈值的点，则取前面1000个点，找到极大值或极小值，返回他们的位置
					double split_motiPreLa [] = new double[1000];
					for(int i = k;i>k-1000;i--){//提取500个点的数据放入split_motiPreLa数组中
						split_motiPreLa[ser] = filteredBo[i];//将这些点存储到split数组中,并使得split数组中的数据顺序与motiPreLa中的一致
						ser++;
					}
					int pos = 0;
					ser=0;
					pos = exValue.exV(split_motiPreLa);//找极值点，返回极值点的位置
					POS = k-pos;
					return POS;
				}
			}
			else {//找到了大于振幅阈值的点
				double split_motiPreLa [] = new double[1000];
				for(int i = k;i>k-1000;i--){//提取500个点的数据放入split_motiPreLa数组中
					split_motiPreLa[ser] = filteredBo[i];//将这些点存储到split数组中,并使得split数组中的数据顺序与motiPreLa中的一致
					ser++;
				}
				int pos = 0;
				ser=0;
				pos = exValue.exV(split_motiPreLa);//找极值
				POS = k-pos;
				return POS;
			}
		}
		else{//未找到新到时点，则用老到时点计算
			boolean flag1 = false;
			int k = 0;
			for(int i=POS-500;i<POS+500;i++){//前后500个点。
				if(Math.abs(filteredBo[i])>T){//若z方向的值大于阈值，则向前寻找500个点 数据，并进行滤波
					flag1 = true;//找到大于阈值的点
					k=i-1;//记录大于阈值的前一个位置，该位置一定为小于阈值的序号，从该点往前找500个点
					break;
				}//跳出for循环
			}
			if(flag1 == false){//若找不到大于振幅阈值的点，则新的到时点无效，我们继续使用原来程序中的老的到时点
				return POS;
			}
			else {//找到了大于振幅阈值的点
				double split_motiPreLa [] = new double[1000];
				
				for(int i = k;i>k-1000;i--){//提取500个点的数据放入split_motiPreLa数组中
					split_motiPreLa[ser] = filteredBo[i];//将这些点存储到split数组中,并使得split数组中的数据顺序与motiPreLa中的一致
					ser++;
				}
				int pos = 0;
				ser=0;
				pos = exValue.exV(split_motiPreLa);//找极值
				POS = k-pos;//因为在数组中保存的时倒序，因此找到位置应为k-pos
				return POS;
			}
		}
	}
	
	/**
	 * 以日期为单位切分String型容器
	 * @param data
	 * @param D
	 * @return
	 */
	private static Vector<String> cutOdata(Vector<String> data, String D) {
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
		Date motiDate = new Date();
		Date t1 = new Date();
		long timeDiff = 0;
		
		try {
			motiDate = format1.parse(D);
		}
		catch (ParseException e1) {e1.printStackTrace();}
		
		Vector<String> resultSet = new Vector<String>();
		
		for(int i = 0; i < data.size(); i++){
			try {
				t1 = format1.parse(data.get(i).split(" ")[6]);
			}
			catch (ParseException e) {e.printStackTrace();}
			timeDiff = (motiDate.getTime()-t1.getTime())/1000;
			if(timeDiff < 5 && timeDiff >= -5){
				resultSet.add(data.get(i));
			}
		}
		
		return resultSet;
	}
	
	/**
	 * 以秒为单位切分String型容器
	 * @param data 要切分的容器
	 * @param line 激发位置
	 * @param startTime 前面截取的时间长度
	 * @param endTime 后面截取的时间长度
	 * @return 返回截取好的固定长度的容器
	 */
	private static Vector<String> cutOdata1(Vector<String> data, int line, int startTime, int endTime) {
		Vector<String> resultSet = new Vector<String>();
		
		for(int i = line-Parameters.FREQUENCY*startTime; i < line+Parameters.FREQUENCY*endTime; i++){//设置为6s，当在now容器中的初始时刻激发时
			if(i<=0){
				System.out.println("当i小于0时，输出line"+line);
				System.out.println("当i小于0时，输出data大小"+data.size());
				System.out.println("当i小于0时，输出i"+i);
			}
			resultSet.add(data.get(i));
		}
		return resultSet;
	}
	
	/**
	 * 确定读取哪一个通道. 先扫一遍10秒的数据，确定用哪一个通道,顺便确定通道的最大值
	 * @param sen 激发的传感器
	 * @return 标志，f表示4 5通道，t表示1 2通道
	 */
	private static boolean getFlag(Vector<String> data, Sensor sen, int th)
	{
		boolean flag = false;// 标识
		String[] inte = new String[7];
		// 存储1245通道的最大值
		int max1 = 0;
		int max2 = 0;
		int max4 = 0;
		int max5 = 0;
		//System.out.println("第"+th+"台站"+data.size()+"的getFlag函数数据大小");
		for (int i = 0; i < data.size(); i++)
		{
			inte = data.get(i).split(" ");
			if (!flag)
			{
				if (testValue(inte[4]) || testValue(inte[3]))
				{
					flag = true;
				}
			}
			max1 = (Math.abs(max1) > Math.abs(Integer.parseInt(inte[0]))) ? max1 : Integer.parseInt(inte[0]);//将最大值转换为整型处理
			max2 = (Math.abs(max2) > Math.abs(Integer.parseInt(inte[1]))) ? max2 : Integer.parseInt(inte[1]);
			max4 = (Math.abs(max4) > Math.abs(Integer.parseInt(inte[3]))) ? max4 : Integer.parseInt(inte[3]);
			max5 = (Math.abs(max5) > Math.abs(Integer.parseInt(inte[4]))) ? max5 : Integer.parseInt(inte[4]);
		}
		sen.setMax1(max1);
		sen.setMax2(max2);
		sen.setMax4(max4);
		sen.setMax5(max5);
		return flag;
	}

	/**
	 * 计算最大值后面第一个数值为0的点的时间标识
	 * 
	 * @param sen
	 * @param flag
	 *            选择的通道是45还是12
	 * @param data
	 *            now中的数据
	 */
	///对应算法公式中的     a - b
	private static void getBTime(Sensor sen, boolean flag, Vector<String> data, int th)
	{
		//System.out.println("第"+th+"台站"+data.size()+"的getBTime函数数据大小");
		if (!flag)// 选择45通道
		{
			double An = getRecordCount(sen, 5, data, th) / (double) Parameters.FREQUENCY;//求最大值到第一个小于0的数的时间间隔
			sen.setBn(An);
			double Ae = getRecordCount(sen, 4, data, th) / (double) Parameters.FREQUENCY;
			sen.setBe(Ae);
		} else// 选择12通道
		{
			double An = getRecordCount(sen, 2, data, th) / (double) Parameters.FREQUENCY;
			sen.setBn(An);
			double Ae = getRecordCount(sen, 1, data, th) / (double) Parameters.FREQUENCY;
			sen.setBe(Ae);
		}
	}

	/**
	 * 计算传感器的最大振幅
	 * 
	 * @param sen
	 *            激发的传感器
	 * @param flag
	 *            标志是否换通道
	 * @return 激发传感器的最大振幅
	 */
	private static double getMaxA(Sensor sen, boolean flag, Vector<String> data,int th)
	{
		if ((sen.getBn()==0)||(sen.getBe()==0))
		{
			return 1;
		}
		int num = 0;// 增益倍数
		double An = 0;
		double Ae = 0;
		if (flag)//12通道，增益倍数是32
		{
			num = 32;
			An =calSum(sen, 2, data,th)*0.00004768 * 1000;
			Ae =calSum(sen, 1, data,th)*0.00004768 * 1000;

		} else//45通道，增益倍数是512
		{
			num = 100;
			An =calSum(sen, 5, data,th)*0.00000298 * 1000;
			Ae =calSum(sen, 4, data,th)*0.00000298 * 1000;
		}
		return ((Ae + An) *num ) / 2;
	}

	/**
	 * 返回最大值到0值之间的记录数
	 * 
	 * @param sen
	 *            激发传感器
	 * @param channel
	 *            第几通道
	 * @return 记录数
	 */
	private static int []begin1 = new int[Parameters.SensorNum];// 标识最大值的那条记录
	private static int []end1 = new int[Parameters.SensorNum];// 标识0值的那条记录
	private static int []begin2 = new int[Parameters.SensorNum];// 标识最大值的那条记录
	private static int []end2 = new int[Parameters.SensorNum];// 标识0值的那条记录
	
	private static int getRecordCount(Sensor sen, int channel, Vector<String> data, int th)
	{
		int end1=0;int begin1=0;//用于求后T/4的数据面积
		int end2=0;int begin2=0;//用于求前T/4的数据面积
		//System.out.println("第"+th+"台站"+data.size()+"的getRecordCount函数数据大小");
		// 1：先取指定channel的最大值
		int Max = 0;
		switch (channel)
		{
		case 1:
			Max = sen.getMax1();
			
			break;
		case 2:
			Max = sen.getMax2();
			
			break;
		case 4:
			Max = sen.getMax4();
			
			break;
		case 5:
			Max = sen.getMax5();
			
			break;
		}
		//System.out.println(Max);
		// 2：定位到最大值的那条记录
		
		
		//用于前后T/4的数据面积
		
		while (Integer.parseInt(data.get(begin1).split(" ")[channel - 1]) !=  Max)
		{
			begin1++;
		}
		
		
		end1 = begin1 + 1;// end指向最大值的下一条数据
		// 3:定位到值为0的那条记录
		if(Max>0){
			if(Integer.parseInt(data.get(0).split(" ")[channel - 1]) > 0) {
				end1 = begin1;
			}
			else {
				while (Integer.parseInt(data.get(end2).split(" ")[channel - 1]) > 0)
				{
					end1++;
				}
			}
		}
		else{
			if(Integer.parseInt(data.get(0).split(" ")[channel - 1]) < 0) {
				end1 = begin1;
			}
			else {
				while (Integer.parseInt(data.get(end2).split(" ")[channel - 1]) <0)
				{
					end1++;
				}
			}
		}
		QuakeClass.begin1[th]=begin1;
		QuakeClass.end1[th]=end1;
		//用于求前T/4的数据面积
		
		while (Integer.parseInt(data.get(begin2).split(" ")[channel - 1]) !=  Max)
		{
			begin2++;
		}
		end2 = begin2 - 1;// end指向最大值的前一条数据
		// 3:定位到值为0的那条记录
		if(Max>0){
			if(Integer.parseInt(data.get(0).split(" ")[channel - 1]) > 0) {
				end2 = begin2;
			}
			else {
				while (Integer.parseInt(data.get(end2).split(" ")[channel - 1]) >0)
				{
					end2--;
				}
			}
		}
		else{//Max小于0
			if(Integer.parseInt(data.get(0).split(" ")[channel - 1]) < 0) {
				end2 = begin2;
			}
			else {
				while (Integer.parseInt(data.get(end2).split(" ")[channel - 1]) <0)
				{
					end2--;
				}
			}
		}
		QuakeClass.begin2[th]=begin2;
		QuakeClass.end2[th]=end2;
		double sum1 = 0;
		double sum2 = 0;
		for(int i=QuakeClass.begin1[th];i<QuakeClass.end1[th];i++){
			sum1+=Math.abs(Integer.parseInt(data.get(i).split(" ")[channel - 1]));//出现超出数组范围错误		
		}
		for(int i=QuakeClass.end2[th];i<QuakeClass.begin2[th];i++){
			sum2+=Math.abs(Integer.parseInt(data.get(i).split(" ")[channel - 1]));
		}
		if(sum1<sum2) {
			return (QuakeClass.begin2[th] - QuakeClass.end2[th]);
		}
		else {
			return (QuakeClass.end1[th] - QuakeClass.begin1[th]);
		}
	}
	
	
	private static double calSum(Sensor sen, int channel, Vector<String> data, int th){
		double sum1=0;
		double sum2=0;
		double maxSum=0;
	
		for(int i=QuakeClass.begin1[th];i<QuakeClass.end1[th];i++){
			sum1+=Math.abs(Integer.parseInt(data.get(i).split(" ")[channel - 1]));
			
		}
		for(int i=QuakeClass.end2[th];i<QuakeClass.begin2[th];i++){
			sum2+=Math.abs(Integer.parseInt(data.get(i).split(" ")[channel - 1]));
			
		}
		if(Math.abs(sum1)>Math.abs(sum2)) {
			maxSum=sum1/5000;//乘以频率，得面积
			
		}
		else {
			maxSum=sum2/5000;
			
		}
		//System.out.print("最大值"+QuakeClass.begin1+"数值"+data.get(QuakeClass.begin1).split(" ")[channel - 1]);
		//System.out.print("最小值"+QuakeClass.end1+"数值"+data.get(QuakeClass.end1).split(" ")[channel - 1]);
		//System.out.println("两个面积分别为："+sum1/5000+"和"+sum2/5000+"其中的最大值为"+maxSum);
		return maxSum;//可能为0
	}

	/**
	 * 计算是否超出界限
	 * 
	 * @param s
	 *            可以转换为int的string
	 * @return 是否越界
	 */
	private static boolean testValue(String s)
	{
		int a = Integer.parseInt(s);
		if (a > Parameters.HEAD || a < Parameters.TAIL){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * 通过两个传感器计算震级
	 * 
	 * @param s
	 *            震源传感器
	 * @param s2
	 *            部署的并被激发的传感器
	 * @return 震级
	 */
	public static void getOneEarthClass(Sensor s, Sensor s2)
	{
		
		double distance = getDistance(s, s2);
		double dis_s1Tos2 = getD(s,s2);
//		System.out.println("震中距："+distance);
//		System.out.println("震中距（两点平面距离）："+dis_s1Tos2);
		/*if (distance < 0.0 || distance > 5.0)
		{
			s2.setEarthClassFinal(Math.log10(s2.getFudu()) + getR(distance));
		} else
		{*/
			s2.setEarthClassFinal(Math.log10(s2.getFudu()) + getR(distance));
			
		/*}*/
	}

	/**
	 * 计算两个坐标之间的距离，通过经纬度计算直线距离
	 * 
	 * @param s1
	 *            震源传感器
	 * @param s2
	 *            部署的并被激发的传感器
	 * @return 距离
	 */
	public static double getDistance(Sensor s1, Sensor s2)
	{
		// http://www.jianshu.com/p/18efaabab98e
		double radLat1 = rad(s1.getLatitude() / 100);
		double radLat2 = rad(s2.getLatitude() / 100);
		double a = radLat1 - radLat2;
		double b = rad(s1.getLongtitude() / 100) - rad(s2.getLongtitude() / 100);
		double s = 2 * Math.asin(Math.sqrt(
				Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		return Math.abs(s / 1000);
	}
	
	public static double getD(Sensor s1, Sensor s2){
		double x0 = s1.getLatitude();
		double y0 = s1.getAltitude();
		
		double x1 = s2.getLatitude();
		double y1 = s2.getAltitude();
		
		double dis_s1Tos2 = Math.sqrt(Math.pow((x0-x1), 2)+Math.pow((y0-y1), 2));
		return dis_s1Tos2;
	}

	private static double rad(double d)
	{
		return d * Math.PI / 180.0;
	}

	private static double getR(double d)
	{
		if (d >= 0 && d < 0.5)	return 0.48;
		if (d >= 0.5 && d < 1)	return 0.78;
		if (d >= 1 && d < 1.5)	return 1.05;
		if (d >= 1.5 && d < 2)	return 1.21;
		if (d >= 2 && d < 2.5)	return 1.36;
		if (d >= 2.5 && d < 3)	return 1.47;
		if (d >= 3 && d < 3.5)	return 1.57;
		if (d >= 3.5 && d < 4)	return 1.66;
		if (d >= 4 && d < 4.5)	return 1.73;
		if (d >= 4.5 && d <= 5.0)	return 1.8;
		if(d>5.0)	return 1.8;
		return 0;
	}
	
	/**
	 * 求持续时间震级，其中方差数组需要提前预先计算！！！
	 * @param Vector data, sen , th
	 * @return 直接返回持续震级
	 */
	public static double calDuringTime(Vector<String> data, Sensor sen, int th){
		double array[]=new double[Parameters.FREQUENCY];
		double variance;
		double fuDu=0.0;	
		double lastingTime = 0.0;//持续时间，单位：ms
		int k=0,duringTime=0;
		for(int h=sen.getlineSeries();h<data.size()-Parameters.FREQUENCY;h++){//从激发位置往后滑动1s长度的时间窗口，步长为1.
			for(int i=h;i<h+Parameters.FREQUENCY;i++){
				array[i]=Double.parseDouble(data.get(i).split(" ")[5]);//将激发位置推迟1s的数据保存到array数组中
			}
			variance=varianceImperative(array);
			if(h==sen.getlineSeries())	System.out.println("方差为："+variance);
			for(int i=0;i<MainThread.fileStr.length;i++) {
				if(MainThread.fileStr[th].equals(Parameters.diskName[i])){
					if(variance<=2*Parameters.backGround[i]){
						k=h+Parameters.FREQUENCY;//记录方差等于2倍背景噪声的位置作为持续时间的结束位置，此时算法停止
						duringTime=k-sen.getlineSeries();//持续的数据的条个数
						lastingTime = duringTime*0.2;//持续的总时间，单位：ms
						break;
					}
				}
			}
		}
		return Math.log10(lastingTime);//直接取lg
	}

	//计算方差数组
	public static double varianceImperative(double[] population) {		
		double average = 0.0;		
		for (double p : population) {
			average += p;		
		}
		average /= population.length;
		double variance = 0.0;
		for (double p : population) {
			variance += (p - average) * (p - average);		
		}
		return variance / population.length;	
	}

	//地球半径
	private static final double EARTH_RADIUS = 6378137;
}
