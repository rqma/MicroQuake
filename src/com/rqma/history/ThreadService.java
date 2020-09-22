package com.rqma.history;

import com.h2.constant.Parameters;
import com.rqma.util.StrUtil;
import com.yang.readFile.ReadData;
import com.yang.readFile.ReadDataSegmentHead;
import controller.ADMINISTRATOR;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: multiply thread service
 * @Author: RQMA
 * @CreateDate: 2020/6/3 12:37
 */
public class ThreadService {
    /**
     * @Description 多线程异步读取来自于多个台站的文件的第一个数据段段头时间--CompletableFuture方法
     * @Author RQMA
     * @Date 2020/6/3 14:04
     * @param path file path,example: E:/CoalMine/data/12.21红阳三矿2.2地震/Test4/NO4_2019-04-17 20-39-24.bin
     * @return java.util.List<java.lang.String> segment header time list
     */
    public static List<String> getSegmentHeadTime(String[] path) {
        CompletableFuture<String> array[] = new CompletableFuture[Parameters.SensorNum];
        for (int i = 0; i < path.length; i++) {
            String str = path[i];
            array[i] = CompletableFuture.supplyAsync(() -> readDate(str));
        }
        //将所有台站的段头时间存到list
        List<String> rs = Stream.of(array).map(CompletableFuture::join).collect(Collectors.toList());
        System.out.printf("文件第一个数据段段头时间: %s%n", rs);
        //多线程结束
        return rs;

    }

    /**
     * Read the header time of the first data segment of the file under the given path
     *
     * @param path file absolute path
     * @return time string
     */
    public static String readDate(String path) {
        String dateString = null;
        try {
            if (StrUtil.isMrMaEquipment(path))
                dateString = ReadDataSegmentHead.readDataSegmentHead_MrMa_String(path);
            else
                dateString = ReadDataSegmentHead.readDataSegmentHeadall(path);
            System.out.println("task线程：" + Thread.currentThread().getName() + " " + path + ",完成！+" + new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }
    /**
     * @Description 将所有台站下的文件对齐至最新时间
     * @Author RQMA
     * @Date 2020/6/3 14:02
     * @param file
     * @param manager
     * @return java.util.List<com.yang.readFile.ReadData>
     */
    public static List<ReadData> alignTime(String file[], ADMINISTRATOR manager) {
        //多线程进行时间对齐(移动缓冲区数据位置)
        CompletableFuture<ReadData> futures[] = new CompletableFuture[Parameters.SensorNum];
        for (int i = 0; i < Parameters.SensorNum; i++) {
            String str = file[i];
            int th = i;
            futures[i] = CompletableFuture.supplyAsync(() -> moveBufPosition(str, th, manager));
        }
        List<ReadData> rs = Stream.of(futures).map(CompletableFuture::join).collect(Collectors.toList());
        return rs;
    }

    /**
     * @Description 时间对齐的本质-->移动缓冲区位置
     * @Author RQMA
     * @Date 2020/6/3 13:58
     * @param path
     * @param th
     * @param manager
     * @return com.yang.readFile.ReadData
     */
    public static ReadData moveBufPosition(String path, int th, ADMINISTRATOR manager) {
        int kuai = 0;
        ReadData readDataArray = null;
        long m1, m2;
        try {
            readDataArray = new ReadData(path, th, manager);
            m1 = System.currentTimeMillis();
            //进行时间对齐
            if (!StrUtil.isMrMaEquipment(path))
                kuai = readDataArray.readDataAlign(path, th);   //旧设备文件时间对齐
            else
                kuai = readDataArray.readDataDui_MrMa(path, th);//新设备文件时间对齐
            m2 = System.currentTimeMillis();
            System.out.println(path + "盘对齐花费：" + (m2 - m1) / 1000 + "s");
            if (kuai == -1) {
                System.out.println("该台站下的数据无法对齐，开始对齐下一组---------");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readDataArray;
    }

}
