package com.h2.backupData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import com.h2.constant.Parameters;

public class WriteMotiData {
	public static void writemotidata(Vector<String> motidata,String filepath,int[] Pos) {
        
		File file = new File(filepath);
        BufferedWriter out = null;
        Vector<String> motidata1 = new Vector<String>();
        int count=1;
        motidata1 = motidata;
    	
        try {
        	out = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e1) {e1.printStackTrace();}
        try {
        	//System.out.println(motidata1.size());
        
        	if(!file.exists())file.createNewFile(); 
           
            for(int i =0;i<motidata1.size();i++) {
            	///System.out.println(motidata1.get(i));
            	if(Pos[count]==i && count<1000){//��λ�������е�������motidata�е�������Ӧʱ�������е����һ����Ϊ1����ʾ����Ϊ����ʱ���ļ���λ��
		            out.write(motidata1.get(i)+" "+"1"+"\r\n");//���ļ���д����
		            out.flush();
		            count++;//ÿ��һ��λ�ñ����Ϻ󣬾ͽ����+1������һ��λ��׼����֮ƥ��
            	}else{//���򽫸�����Ϊ0����ʾ����δ����
            		out.write(motidata1.get(i)+" "+"0"+"\r\n");//���ļ���д����
		            out.flush();
            	}
            }
            //out.write(motiData);
            //System.out.println("д���ݳɹ���");
        } catch (IOException e) {e.printStackTrace();}
        finally{
            if(out != null){
                try {
                    out.close();
                    //file.delete();
                } catch (IOException e) {e.printStackTrace();}
            }
        }
	}
	public static void deleteWritemotidata(String filepath) {
		System.out.println(filepath);
		File file = new File(filepath);
		if(file.exists())  file.delete();
	}
	public static void writemotiDate(String motiDate,String filepath) {
		File file=new File(filepath);
		
		BufferedWriter out = null;
		try {
        	out = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e1) {e1.printStackTrace();}
        try {
        	//System.out.println(motidata1.size());
        	if(!file.exists())file.createNewFile(); 
            	///System.out.println(motidata1.get(i));
            out.write(motiDate+"\r\n");//���ļ���д����
            out.flush();
            //out.write(motiData);
            //System.out.println("д���ݳɹ���");
        } catch (IOException e) {e.printStackTrace();}
        finally{
            if(out != null){
                try {
                    out.close();
                    //file.delete();
                } catch (IOException e) {e.printStackTrace();}
            }
        }
	}
	/**
	 * 将时间列转换为空格分隔的形式，便于后续用matlab进行处理
	 * @param motidata 激发数据，长度在前文中进行了设置
	 * @param filepath 文件存储的绝对路径，通过parameters文件进行设置
	 * @param line 
	 * @param Pos 
	 * @throws ParseException
	 */
	public static void writemotiData1(Vector<String> motidata,String filepath, int line) throws ParseException {
		File file=new File(filepath);
		BufferedWriter out = null;
		
		try {
        	out = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e1) {e1.printStackTrace();}
        try {
        	//System.out.println(motidata1.size());
        	if(!file.exists())file.createNewFile(); 
            	///System.out.println(motidata1.get(i));
        	for(int i =0;i<motidata.size();i++) {
        		
        		DateFormat format1 = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        		Date startDate = format1.parse(motidata.get(i).split(" ")[6]);
        		SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd HHmmss");
        		Calendar calendar = Calendar.getInstance(); //内存溢出的出错位置。~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  
	   			calendar.setTime(startDate);
	   			
	   			Date startDate1 = calendar.getTime();
	   			String date1 = format2.format(startDate1);
	   			//存储激发位置为开始时间长度处
        		out.write(motidata.get(i).split(" ")[0]+" "+motidata.get(i).split(" ")[1]+" "+motidata.get(i).split(" ")[2]+" "+motidata.get(i).split(" ")[3]+" "+motidata.get(i).split(" ")[4]+" "+motidata.get(i).split(" ")[5]+" "+date1+" "+Parameters.FREQUENCY*Parameters.startTime+" "+"\r\n");
        		out.flush();
        	}
		} catch (IOException e) {e.printStackTrace();}
		finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
}
	
