package com.rqma.interfaceimpl;

import com.h2.constant.Parameters;
import com.rqma.bean.TimeLine;
import com.rqma.util.StrUtil;

import java.io.File;
import java.util.Comparator;

/**
* @Description:    compare file name order
* @Author:         RQMA
* @CreateDate:     2020/6/3 12:38
*/
public class ComparatorByFileName implements Comparator<File> {
    private TimeLine timeLine;
    @SuppressWarnings("unused")
	public int compare(File f1, File f2) {
    	int diff=0;
    	if(Parameters.region_offline=="datong")
    		diff = StrUtil.getTimeFromMrLiuFileName(f1.getName()).compareTo(StrUtil.getTimeFromMrLiuFileName(f2.getName()));
    	else if(Parameters.region_offline=="pingdingshan")
    		diff = f1.getName().compareTo(f2.getName());
    	
        if (diff > 0)
            return 1;
        else if (diff == 0)
            return 0;
        else
            return -1;
    }


}