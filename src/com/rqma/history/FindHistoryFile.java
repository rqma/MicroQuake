package com.rqma.history;

import com.h2.constant.Parameters;
import com.rqma.interfaceimpl.ComparatorByFileName;
import com.rqma.interfaceimpl.FileAccept;
import com.rqma.util.StrUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: Find history files
 * @Auther: RQMA
 * @CreateDate: 4/24/2019 10:29 AM
 */

/**
* @Description:
* @Author:         RQMA
* @Date:     2020/6/3 13:18
*/
public class FindHistoryFile {
    private boolean isnewequipment = false;

    public FindHistoryFile() {

    }

    /**
     *
     * @param path
     * @param sid
     * @param time
     * @return
     */
    public static File getFile(String path, int sid, String time) {
        File file = new File(path);
        File[] files = file.listFiles();//文件夹下的所有文件或文件夹
        if (files == null)
            return null;
        Arrays.sort(files, new ComparatorByFileName());
        for (File file2 : files) {
            //找到后缀为HFMED的并且时间大于给定时间的第一个文件
            //5-17形式 ：190101124000

            if (Parameters.region_offline == "datong") {
                if (file2.getPath().endsWith(".HFMED") && StrUtil.getTimeFromMrLiuFileName(file2.getName()).compareTo(time) >= 0) {
                    System.out.println(file2.getName());
                    return file2;//返回该文件
                }
            } else if (Parameters.region_offline == "pingdingshan" || Parameters.region_offline == "hongyang") {
                if (file2.getPath().endsWith(".HFMED") && file2.getName().substring(5, 17).compareTo(time) >= 0) {
                    System.out.println(file2.getName());
                    return file2;
                }
            }
        }
        return null;
    }

    public static File getFile(String path, int sid) {
        File file = new File(path);
        if (file == null)
            return null;
        return file;
    }


    /**
     * get all file names within a given time range of a station,with parameter isnewequipment
     *
     * @param path           file directory
     * @param sid            station id
     * @param time           starting time given by user
     * @param isnewequipment whether it is a new equipment file
     * @return all file names with its time more than starting time
     */
   /**
    * @Description TODO
    * @Author RQMA
    * @Date 2020/6/3 13:17
    * @Param [path, sid, time, isnewequipment]
    * @return java.lang.String[]
    **/
   /**
    * @Description
    * @Author RQMA
    * @Date 2020/6/3 13:17
    * @Param [path, sid, time, isnewequipment]
    * @return java.lang.String[]
    **/
    public static String[] getAllFilesName(String path, int sid, String time, boolean isnewequipment) {

        File file = new File(path);
        String[] files = null;
        FileAccept accept = new FileAccept();
        int i = 0;

        //旧设备文件
        if (!isnewequipment) {
            accept.setExtendName("HFMED");
            files = file.list(accept);

            if (files == null)
                return null;
            Arrays.sort(files);
            if (Parameters.region_offline == "datong") {
                for (; i < files.length; i++) {
                    if (StrUtil.getTimeFromMrLiuFileName(files[i]).compareTo(time) >= 0) {
                        break;
                    }
                }
            } else if (Parameters.region_offline == "pingdingshan" || Parameters.region_offline == "hongyang") {
                for (; i < files.length; i++) {
                    if (files[i].substring(5, 17).compareTo(time) >= 0) {
                        break;
                    }
                }
            }
        }
        //新设备文件
        else {
            //TODO:目前新设备只用在红阳矿，则文件名的规则固定，若以后用在别的矿，当设备写入文件名的规则发生改变时，这里的代码需要稍加修改
            accept.setExtendName("bin");
            files = file.list(accept);
            if (files == null)
                return null;
            Arrays.sort(files);
            //旧设备命名格式：Test_191221162725.HFMED
            //新设备命名格式：NO4_2020-01-02 19-39-24.bin
            for (; i < files.length; i++) {
                time = "20" + time;
                String filetime = StrUtil.getTimeFromMrMaFileName(files[i]);
                if (StrUtil.getTimeFromMrMaFileName(files[i]).compareTo(time) >= 0) {
                    break;
                }
            }
        }
        //将大于给定时间的文件名复制给filename
        String filename[] = Arrays.copyOfRange(files, i, files.length);
        return filename;
    }

    /**
     * get all file names within a given time range of a station,no need to specify parameter isnewequipment
     * this function is more efficient
     * @param path file directory
     * @param time starting time given by user
     * @return all file names with its time more than starting time
     */
    public static String[] getAllFilesName(String path, String time) {
        //whether it is a new equipment file
        boolean isMrMa = false;
        File file = new File(path);
        String[] files = file.list((dir, name) -> name.endsWith(".HFMED") || name.endsWith(".bin"));
        if (files == null && files.length < 1)
            return null;
        //if the first file ends with .bin, then suppose all files end with .bin
        if (files[0].endsWith(".bin")) {
            isMrMa = true;
        }
        List<String> namelist = new ArrayList<>();
        if (!isMrMa) {
            if (Parameters.region_offline == "datong") {
                for (String filename : files) {
                    if (StrUtil.getTimeFromMrLiuFileName(filename).compareTo(time) >= 0) {
                        namelist.add(filename);
                    }
                }
            } else if (Parameters.region_offline == "pingdingshan" || Parameters.region_offline == "hongyang") {
                for (String filename : files) {
                    if (filename.substring(5, 17).compareTo(time) >= 0) {
                        namelist.add(filename);
                    }
                }
            }
        } else {
            for (String filename : files) {
                time = "20" + time;
                String filetime = StrUtil.getTimeFromMrMaFileName(filename);
                if (StrUtil.getTimeFromMrMaFileName(filename).compareTo(time) >= 0) {
                    namelist.add(filename);
                }
            }
        }
        //transform namelist type from List to Array and return
        return namelist.toArray(new String[namelist.size()]);
    }


    public static void main(String[] args) {
        String fileStr = "E:/CoalMine/data/平顶山/Test1/";
        String timeStr = "190101143901";

       /* File f= getFile(fileStr,1,timeStr);
        System.out.println(f.getPath());
        TimeLine  timeLine=new TimeLine();

        timeLine.setId(111);*/
        String[] s = getAllFilesName(fileStr, timeStr);
        System.out.println(s.length);
    }

}
