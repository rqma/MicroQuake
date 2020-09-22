package com.rqma.util;

import com.h2.constant.Parameters;

/**
* @Description:    string utils
* @Author:         RQMA
* @CreateDate:     2020/6/3 12:39
*/
public class StrUtil {
	public static String [] getFileParentPackage(String[] path) {
        String[] SubStr = new String[Parameters.SensorNum];//记录 线下跑时 文件所在父目录
        for (int i = 0; i < Parameters.SensorNum; i++) {
            int strcount = 0;
            for (int j = path[i].length() - 2; j >= 0; j--) {
                if (path[i].charAt(j) == '/') {
                    break;
                }
                strcount++;
            }
            SubStr[i] = path[i].substring(path[i].length() - 1 - strcount, path[i].length() - 1);
        }
        return SubStr;
    }
	public static String getTimeFromMrLiuFileName(String fileName) {

        String []str=new String[2];
        str = fileName.split("_");
       // System.out.println(str[1]);
        return  str[1].split("\\u002E")[0];

    }

    /**
     * 新设备文件名中的时间截取 ；如 NO4_2020-01-02 19-39-24.bin 截取为 20200102193924
     * @param fileName
     * @return
     */
    public static String getTimeFromMrMaFileName(String fileName) {

        String []str1= fileName.split("_");  //分割“_”
        String []str2= str1[1].split("\\."); //分割“.”
        //System.out.println(str2[0]);
        String time= str2[0].replace("-","");
        time=time.replace(" ","");
       // System.out.println(time);
        return time;
    }

    /**
     * 判断是否是新设备文件
     * @param filename
     * @return
     */
     public static boolean isMrMaEquipment(String filename){
         //filename太长的话，调用endsWith出错，先取最后10个字符，再判断扩展名类型
         filename=filename.length()>10?filename.substring(filename.length() -10):filename;
         return filename.endsWith(".bin");
     }

    /**
     * 判断是否是旧设备文件
     * @param filename
     * @return
     */
    public static boolean isMrLiuEquipment(String filename){
        //filename太长的话，调用endsWith出错，先取最后10个字符，再判断扩展名类型
        filename=filename.length()>10?filename.substring(filename.length() -10):filename;
        return filename.endsWith(".HFMED");
    }

    /**
     *自动判断一个目录下存放的是旧设备文件还是新设备文件，就无需在程序开始时，在 MainThread类 对manger.isMrMa 赋初值
     * @param filename
     * @return
     */
    public static boolean autojudgePackage(String filename){
        //filename太长的话，调用endsWith出错
        filename=filename.substring(filename.length() -10);
        return filename.endsWith(".bin");
    }
    public static void main(String[] args) {
       // StrUtil.getTimeFromMrMaFileName("NO4_2020-01-02 19-39-24.bin");
        String str="NO4_2020-01-02 19-39-24.HFMED";
        System.out.println( isMrMaEquipment(str));
    }

}
