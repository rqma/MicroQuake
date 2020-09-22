package com.rqma.examples;

import com.rqma.util.StrUtil;
import com.yang.readFile.ReadDataSegmentHead;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @Description:
 * @Auther: RQMA
 * @Date: 2020/6/1 17:16
 */
public class ReadDateTask implements Callable<String> {
    /**
     * 多线程读取文件第一个数据段段头时间：使用future实现，
     */
    private final CountDownLatch latch;
    private String path;

    public ReadDateTask(CountDownLatch latch, String path) {
        this.latch = latch;
        this.path = path;
    }

    @Override
    public String call() throws Exception {
        String dateString;
        if (StrUtil.isMrMaEquipment(path))
            dateString = ReadDataSegmentHead.readDataSegmentHead_MrMa_String(path);
        else
            dateString = ReadDataSegmentHead.readDataSegmentHeadall(path);
        latch.countDown();
        return dateString;
    }
}
