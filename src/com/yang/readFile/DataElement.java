package com.yang.readFile;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Author�����˶������ɷ�    Date��2018��7��9��
 * Description������Ԫ�أ�ÿһ������Ԫ�ذ���ÿ��ͨ����һ������
 */
public class DataElement {
    /*x1��y1��z1...�ֱ��Ӧ1~7ͨ��������ǰ����ͨ����¼����ĳʱ�̸ù۲����x��y��z��������������ݣ�
     * ֮�������ͨ����¼��ʵ������	ǰ����ͨ���Ŵ���ֵ������Ŵ������ǲ���Ҫ֪������
     * ��ֻ������ͨ��ʱ��ʵ������ֻ�����˺�����ͨ������ʱ�������ļ��в�����ǰ��ͨ�������ݣ�
     * ���㷨��ʹ�õ�������������Ԫ�أ�����ÿ��ͨ�������ݣ�����˳����е�����Ԫ������7���ֶεģ�
     * ֻ���ڶ�ȡ���ݵ�ʱ��ֻ�����ĸ�ͨ����ǰ����ͨ������Ĭ��ֵ��0��
     * ���һ��ͨ����¼���ǵ�ƽʱ���źţ�volt�������ڵ͵�ƽʱ��ͨ�����ݵ�ֵ�Ƚ�С��<100��20���ң���
     * ���ڸߵ�ƽʱ��ֵ�ܴ�>5000�����Ҹߵ�ƽһ�����һ����GPSͳһ��ʱ��֤���ȣ���
     * ��˸�ͨ������ʵ�������������������ھ�ȷ��ʱ�ģ������������в����õ������ݣ�
     * ����ڱ����а�volt�滻���˸�����Ԫ�صļ�¼ʱ�䣨dataCalendar��*/
 	private short x1 ;
 	private short y1 ;
 	private short z1 ;
 	
 	private short x2 ;
 	private short y2 ;
 	private short z2 ;
 		
// 	private Calendar dataCalendar;//������Ԫ�صļ�¼ʱ�䣨��ȷ���룩
 	private String dataCalendar;//������Ԫ�صļ�¼ʱ�䣨��ȷ���룩
 	//������
 	public DataElement(){}
 	
 	
 	//��������������
	public short getX1() {
		return x1;
	}
	public void setX1(short x1) {
		this.x1 = x1;
	}

	public short getY1() {
		return y1;
	}
	public void setY1(short y1) {
		this.y1 = y1;
	}

	public short getZ1() {
		return z1;
	}
	public void setZ1(short z1) {
		this.z1 = z1;
	}
	
	
	public short getX2() {
		return x2;
	}
	public void setX2(short x2) {
		this.x2 = x2;
	}

	public short getY2() {
		return y2;
	}
	public void setY2(short y2) {
		this.y2 = y2;
	}
	
	public short getZ2() {
		return z2;
	}
	public void setZ2(short z2) {
		this.z2 = z2;
	}	
	
//	public void setDataCalendar(Calendar calendar){
//		this.dataCalendar = calendar ;
//	}
	public void setDataCalendar(String calendar){
		this.dataCalendar = calendar ;
	}
//	public Calendar getDataCalendar(){
//		return dataCalendar ;
//	}
	public String getDataCalendar(){
		return dataCalendar ;
	}
	
	//���������ŷ�Ͼ��룩
	public double getAmplitude(){
		return Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2) + Math.pow(z2, 2));
	}
	
	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
//		return ( "[" + x1 + "," + y1 + "," + z1 + ",\t"
//				+ x2 + ",\t" + y2 + ",\t" + z2 + ",\t"
//				+ dateFormat.format(dataCalendar.getTime()) + "]");
		return ( x1 + " " + y1 + " " + z1 + " "
				+ x2 + " " + y2 + " " + z2 + " "
				+ dataCalendar );
	}
	
}
