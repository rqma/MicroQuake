package com.rqma.interfaceimpl;

import com.h2.constant.Parameters;
import com.rqma.bean.TimeLine;
import com.rqma.util.StrUtil;

import java.util.Comparator;

/**
 * @Description:
 * @Auther: RQMA
 * @Date: 4/26/2019 2:04 PM
 */
public class  ComparatorTimeLine implements Comparator<TimeLine> {
    @SuppressWarnings("unused")

	@Override
    public int compare(TimeLine o1, TimeLine o2) {
    	
    	
    	int diff=0;
    	if(Parameters.region_offline=="datong")
    		diff = StrUtil.getTimeFromMrLiuFileName(o1.getFilename()).compareTo(StrUtil.getTimeFromMrLiuFileName(o2.getFilename()));
    	else if(Parameters.region_offline=="pingdingshan"|| Parameters.region_offline=="hongyang") {
    		diff = o1.getFilename().compareTo(o2.getFilename());
    	}
        if (diff > 0)
            return 1;
        else if (diff == 0)
            return 0;
        else
            return -1;

    }
}
