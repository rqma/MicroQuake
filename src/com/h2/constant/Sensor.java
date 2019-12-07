/**
 * @author 韩百硕
 * 给传感器创建一个类
 */
package com.h2.constant;

import com.h2.tool.CrestorTrough;
public class Sensor {
	public Sensor() {
		this.sign = false;// 标识是否激发
		this.time = "000000000000";// 激发的时间
		this.quackTime="000000000000";//发震时刻
		this.fudu = 0;// 最大幅度
		this.EarthClassFinal = 0;//M平均值之前的求和

		this.Altitude = 0;// 海拔z
		this.Longtitude = 0;// 经度y
		this.Latitude = 0;// 维度x
		this.lineSeries=0;//记录5个台站激发位置
		this.BackupFile = "";
		this.duringTime = 0.0;
		this.panfu = "";
		this.filename_S = "";
		// 用于震级的计算
		Max1 = 0;// 通道1的最大值
		Max2 = 0;// 通道2的最大值
		Max4 = 0;// 通道4的最大值
		Max5 = 0;// 通道5的最大值
		Bn = 0;// An中两个记录间的记录数，用于确定时间
		Be = 0;// Ae中两个记录间的记录数，用于确定时间
		crestortrough=null;//判断激发后对其进行赋值
	}

	@Override
	public String toString() {
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		return nf.format(Longtitude) + " " + nf.format(Latitude) + " "
				+ nf.format(Altitude);
	}

	public double getEarthClassFinal() {		return EarthClassFinal;	}
	public void setEarthClassFinal(double earthClassFinal) {		EarthClassFinal = earthClassFinal;	}

	public boolean isSign() {		return sign;	}
	public void setSign(boolean sign) {		this.sign = sign;	}

	//激发时刻，每个传感器的激发时刻
	public String getTime() {		return time;	}
	public void setTime(String time) {		this.time = time;	}
	
	//P波到时，激发时刻，以秒为单位
	public double getSecTime() {return Sectime;}
	public void setSecTime(double Sectime) { this.Sectime=Sectime;}

	//获取发震时刻，日期格式
	public String getquackTime() {		return quackTime;}
	public void setquackTime(String time) {		this.quackTime=time;}

	//获取x
	public double getLongtitude() {		return Longtitude;	}
	public void setLongtitude(double longtitude) {		Longtitude = longtitude;	}

	//获取y
	public double getLatitude() {		return Latitude;	}
	public void setLatitude(double latitude) {		Latitude = latitude;	}

	//
	public double getAltitude() {		return Altitude;	}
	public void setAltitude(double altitude) {		Altitude = altitude;	}

	//
	public String getBackupFile() {		return BackupFile;	}
	public void setBackupFile(String backupFile) {		BackupFile = backupFile;	}

	//获取幅度
	public double getFudu() {		return fudu;	}
	public void setFudu(double fudu) {		this.fudu = fudu;	}
	
	//获取持续时间震级
	public double getDuring() {		return duringTime;	}
	public void setDuring(double duringTime) {		this.duringTime = duringTime;	}
	
	public void setlineSeries(int lineSeries) { this.lineSeries = lineSeries;	}
	public int getlineSeries() {	return this.lineSeries;	}

	public int getMax4() {		return Max4;	}
	public void setMax4(int max4) {		Max4 = max4;	}

	public int getMax5() {		return Max5;	}
	public void setMax5(int max5) {		Max5 = max5;	}

	public int getMax1() {		return Max1;	}
	public void setMax1(int max1) {		Max1 = max1;	}

	public int getMax2() {		return Max2;	}
	public void setMax2(int max2) {		Max2 = max2;	}

	public double getBn() {		return Bn;	}
	public void setBn(double bn) {		Bn = bn;	}

	public double getBe() {		return Be;	}
	public void setBe(double be) {		Be = be;	}
	
	//获取盘符
	public String getpanfu() {		return panfu;	}
	public void setpanfu(String panfu) {		this.panfu = panfu;	}

	public String getFilename() {
		return filename_S;
	}
	public void setFilename_S(String filename_S) {
		this.filename_S = filename_S;
	}
public CrestorTrough getCrestortrough() {
		return crestortrough;
	}
	
	public void setCrestortrough(CrestorTrough crestortrough) {
		this.crestortrough = crestortrough;
	}
	/**
	 * 标识是否被激发
	 */
	private boolean sign;
	/**
	 * 激发的时间，时间12位格式为yyMMddhhmmss
	 */
	private String time;
	/**
	 * P波相对到时
	 */
	private double Sectime;
	/**
	 * 激发的时间，时间12位格式为yyMMddhhmmss
	 */
	private String quackTime;
	/**
	 * 传感器最大振幅，用于震级计算
	 */
	private double fudu;
	/**
	 * 经线
	 */
	private double Longtitude;
	/**
	 * 纬线
	 */
	private double Latitude;
	/**
	 * 海拔
	 */
	private double Altitude;
	/**
	 * 数据输出位置
	 */
	private String BackupFile;

	// 最大震级公式修改后增加的字段
	/**
	 * 通道1的最大值
	 */
	private int Max1;
	/**
	 * 通道2的最大值
	 */
	private int Max2;
	/**
	 * 通道4的最大值
	 */
	private int Max4;
	/**
	 * 通道5的最大值
	 */
	private int Max5;
	/**
	 * An中的记录数，用于震级计算中获取时间
	 */
	private double Bn;
	/**
	 * Ae中的记录数，用于震级计算中获取时间
	 */
	private double Be;
	/**
	 * 最后求得的每个传感器计算出来的震级
	 */
	private double EarthClassFinal;
	private CrestorTrough crestortrough;
	/**
	 * 激发位置，在now容器中
	 */
	private int lineSeries;
	/**
	 * 持续时间震级
	 */
	private double duringTime;
	/**
	 * 存储激发的盘符
	 */
	private String panfu;
	
	/**
	 * 存储文件名
	 */
	private String filename_S;
}
