package com.rqma.bean;

/**
 * @Description: entity class：describe a set of files from multiple stations
 * @Auther: RQMA
 * @CreateDate: 4/26/2019 1:19 PM
 */
/**
* @Description:  
* @Author: RQMA
* @Date: 2020/6/3 13:43
*/
public class TimeLine {
    private int id;
    private String filename;
    private String filepath;//文件夹
    private int position;//记录位置
    private long begintime;//file start time
    private long endtime;//file end time

    public long getBegintime() {
        return begintime;
    }

    public void setBegintime(long begintime) {
        this.begintime = begintime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
