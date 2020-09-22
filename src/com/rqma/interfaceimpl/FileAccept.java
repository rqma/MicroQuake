package com.rqma.interfaceimpl;

import java.io.File;
import java.io.FilenameFilter;

/**
* @Description:    file name filter
* @Author:         RQMA
* @CreateDate:     2020/6/3 12:39
*/
public class FileAccept implements FilenameFilter {

    private String extendName;
    public void setExtendName(String extendName) {
        this.extendName = "." + extendName;
    }
/**
 * @Description //TODO 
 * @Author RQMA 
 * @Date 2020/6/3 13:14
 * @Param [dir, name]
 * @return boolean
 **/
    public boolean accept(File dir, String name) {//重写接口的方法
        return name.endsWith(extendName);//是否是以某个格式结尾。是否是某个类型文件
    }
}
