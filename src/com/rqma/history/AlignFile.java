package com.rqma.history;

import com.h2.constant.Parameters;
import com.rqma.bean.TimeLine;
import com.rqma.interfaceimpl.ComparatorTime;
import com.rqma.interfaceimpl.ComparatorTimeLine;
import com.rqma.util.DateUtil;
import com.rqma.util.StrUtil;
import controller.ADMINISTRATOR;
import utils.DateArrayToIntArray;

import java.text.ParseException;
import java.util.*;

/**
 * @Description: align file
 * @Auther: RQMA
 * @CreateDate: 4/25/2019 5:54 PM
 */
public class AlignFile {
    private String str_oldest;
    private String str_newest;
    /**
     * 五个台站所读文件中的最老时间
     */
    long oldest_date;
    /**
     * 五个台站所读文件中的最最新时间
     */
    long newest_date;

    private String dateStr;

    public TimeLine time_sorted[] = new TimeLine[Parameters.SensorNum];

    /**
     * 五个台站的全路径
     */
    public String[] paths_original = new String[Parameters.SensorNum];

    /**
     * 存放所有文件名和所在文件夹名
     */
    private static String[][] allfilesname = new String[Parameters.SensorNum][24 * 360];
    /**
     * 第一次读，需要根据输入的目录名读文件，第一次以后，从内存读，速度快
     */
    private boolean first = true;

    public static int align[] = new int[Parameters.SensorNum];
    /**
     * 一组台站中的最新文件
     */
    public  static  String latestfilename = new String();

    public AlignFile() {
        super();
    }

    /**
     * make file alignment
     * @param fileName
     * @param time a time point given by user
     * @param manager whether a file is from the new equipment or the old equipment
     * @return time difference array of a set of files aligned
     * @throws Exception
     */

    public int[] getAlign(String fileName[], String time, ADMINISTRATOR manager) throws Exception {

        for (int i = 0; i < Parameters.SensorNum; i++) {
            align[i] = -1;
        }
        //除第一次外，从内存读取文件名
        System.out.println(1);
        if (!first) {
            int id = time_sorted[0].getId();
            time_sorted[0].setPosition(time_sorted[0].getPosition() + 1);
            int pos = (time_sorted[0].getPosition());
            if (pos >= allfilesname[id].length) {
                System.out.println("该台站此目录下的所有文件已处理完毕");
                System.exit(0);
            }
            time_sorted[0].setFilename(allfilesname[id][pos]);
            Map map = extractTime(time_sorted[0].getFilename());
            time_sorted[0].setBegintime((Long) map.get("begintime"));
            time_sorted[0].setEndtime((Long) map.get("endtime"));
            checkTime(time_sorted);
        }
        //第一次从磁盘读取文件名
        else {
            for (int i = 0; i < Parameters.SensorNum; i++) {

                this.allfilesname[i] = FindHistoryFile.getAllFilesName(fileName[i],time);//得到输入目录下的所有文件名

                time_sorted[i] = new TimeLine();//定义
                time_sorted[i].setId(i);
                time_sorted[i].setPosition(0);
                time_sorted[i].setFilename(allfilesname[i][0]);
                time_sorted[i].setFilepath(fileName[i]);
            }
            checkTime(time_sorted);
            first = false;
        }

   /*     System.out.println("timeLine数组当前值");
        for (int i = 0; i < Parameters.SensorNum; i++) {
           System.out.println(time_sorted[i].getId() + " " + time_sorted[i].getFilename() + " " + time_sorted[i].getFilepath());
        }*/

        /**
         * for循环用于路径还原
         */
        for (int i = 0; i < Parameters.SensorNum; i++) {
            // System.out.println(path);
            for (int j = 0; j < time_sorted.length; j++) {
                if (time_sorted[j].getId() == i) {
                    String path = time_sorted[j].getFilepath() + time_sorted[j].getFilename();
                    paths_original[i] = path;
                }
            }
            System.out.println("第个" + (i + 1) + "台站欲处理的文件为" + paths_original[i]);
        }
/*        //多线程读取开始--future方法
        CountDownLatch latch = new CountDownLatch(Parameters.SensorNum);
        ExecutorService executor = Executors.newFixedThreadPool(Parameters.SensorNum);
        List<Future<String>> results = new LinkedList<>();
        for (int i = 0; i < Parameters.SensorNum; i++) {
            ReadDateTask task = new ReadDateTask(latch,paths_original[i] );
            results.add(executor.submit(task));

        }
        // wait for tasks to finish:
        latch.await();
        //Shutting down the executor
        executor.shutdown();

        for (Future<String> result : results) {
            System.out.printf("文件第一个数据段段头时间: %s%n", result.get());
        }
        //多线程结束*/

        //多线程异步读取多个台站下文件的第一个数据段段头时间--CompletableFuture方法
        List<String> list= ThreadService.getSegmentHeadTime(paths_original);
        //list to array
        String[] dateString = list.toArray(new String[list.size()]);;
        DateArrayToIntArray intArray = new DateArrayToIntArray();
        //计算当前运行的一组文件中，第一个数据段非最新时间与最新时间的时间差
        int[] TimeDifferertInt = intArray.IntArray(dateString);
        //当前运行的一组文件中，第一个数据段的最新时间
        Date DateMax = intArray.getDateStr();
        //一组台站中的最新文件
        latestfilename=paths_original[DateArrayToIntArray.j];
        this.dateStr = DateUtil.toString(DateMax);
        System.out.println("文件第一个数据段段头最大时间：" + this.dateStr);
        System.out.print("文件时间差：");
        for(int diff:TimeDifferertInt){
            System.out.print(diff + " ");
        }
        System.out.println();
        return TimeDifferertInt;
    }


    /**
     * Check whether a group of files from multiple stations can be aligned,,
     * and find a group of files can be aligned
     * @param timeLine
     * @throws ParseException
     */
    public void checkTime(TimeLine timeLine[]) throws ParseException {
        for (int i = 0; i < timeLine.length; i++) {
            Map map = extractTime(timeLine[i].getFilename());
            timeLine[i].setBegintime((Long) map.get("begintime"));
            timeLine[i].setEndtime((Long) map.get("endtime"));
        }
        while (!isTimeOverlap(timeLine)) {
            int id = timeLine[0].getId();
            timeLine[0].setPosition(time_sorted[0].getPosition() + 1);
            int pos = (time_sorted[0].getPosition());
            if (pos >= allfilesname[id].length) {
                System.out.println("该台站此目录下的所有文件已处理完毕(---没有能够对齐的文件---)");
                System.exit(0);
            }

            timeLine[0].setFilename(allfilesname[id][pos]);
            Map map = extractTime(timeLine[0].getFilename());
            timeLine[0].setBegintime((Long) map.get("begintime"));
            timeLine[0].setEndtime((Long) map.get("endtime"));
        }
    }

    /**
     * extract the time from a file name
     * @param filename
     * @return
     * @throws ParseException
     */
    public Map extractTime(String filename) throws ParseException {

        String timestr;
        Map map = new HashMap();
        long begintime = 0;
        long endtime = 0;
        //刘老师设备
        if (!StrUtil.isMrMaEquipment(filename)) {
            timestr = "20" + StrUtil.getTimeFromMrLiuFileName(filename);//
            begintime = DateUtil.toDate(timestr).getTime();

            //大同台站
            if (Parameters.region_offline == "datong") {
                endtime = begintime + (1800 - 1 * 60) * 1000;
            }
            //平顶山或红阳台站
            else if (Parameters.region_offline == "pingdingshan" || Parameters.region_offline == "hongyang") {
                endtime = begintime + (3600 - 3 * 60) * 1000;
            }
        }
        //马老师设备
        else {
            timestr = StrUtil.getTimeFromMrMaFileName(filename);//
            begintime = DateUtil.toDate(timestr).getTime();
            endtime = begintime + (7200 - 5 * 60) * 1000;
        }
        map.put("begintime", begintime);
        map.put("endtime", endtime);
        return map;
    }

    /**
     * Determine whether the time periods of multiple stations overlap,
     * if they overlap, they can be aligned
     * @param timeLine:
     * @return
     */
    public boolean isTimeOverlap(TimeLine timeLine[]) {
        Arrays.sort(timeLine, new ComparatorTime());
        //Skip the first time period without judgment
        for (int i = 1; i < timeLine.length; i++) {
            for (int j = 0; j < i; j++) {
                if ((timeLine[j].getEndtime() - timeLine[i].getBegintime()) < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 检查旧设备一组文件是否能够对齐，已废弃不用
     * @param timeLine
     * @throws ParseException
     */
    @Deprecated
    private void timecheck(TimeLine timeLine[]) throws ParseException {
        Arrays.sort(timeLine, new ComparatorTimeLine());

        //平顶山和大同的文件命名不统一
        if (Parameters.region_offline == "datong") {
            str_oldest = "20" + StrUtil.getTimeFromMrLiuFileName(timeLine[0].getFilename());//最老时间字符串
            str_newest = "20" + StrUtil.getTimeFromMrLiuFileName(timeLine[Parameters.SensorNum - 1].getFilename());//最新时间字符串
        } else if (Parameters.region_offline == "pingdingshan" || Parameters.region_offline == "hongyang") {
            str_oldest = "20" + timeLine[0].getFilename().substring(5, 17);//最老时间字符串
            str_newest = "20" + timeLine[Parameters.SensorNum - 1].getFilename().substring(5, 17);//最新时间字符串
        }

        oldest_date = DateUtil.toDate(str_oldest).getTime(); //最老时间
        newest_date = DateUtil.toDate(str_newest).getTime(); ///最新时间
        /**
         * 必须保证需要处理的一组文件的时间差在一小时内，所以  (newest_date - oldest_date) >= 3600 * 1000
         * 后来跑程序发现，一个台站的下一个文件与上一个文件相隔不是严格的一小时，可能57，58分钟左右，如果按照一小时算，当五台站中有几个是同一个台站时，会导致一个台站的上一个文件与下一个文件在同一组计算
         因此,时间差取 57分钟  (3600-3*60) * 1000
         注意，以后若观察到下一个文件与上一个文件相差更少，需要再调整时间差
         */

        /**
         *意想不到是，大同的文件半小时产生一份，少年，这个bug找累了吗，哈哈哈...... 2019.06.19
         */
        if (Parameters.region_offline == "datong") {
            while ((newest_date - oldest_date) >= (1800 - 1 * 60) * 1000) {
                int id = timeLine[0].getId();
                timeLine[0].setPosition(timeLine[0].getPosition() + 1);
                int pos = (timeLine[0].getPosition());
                if (pos >= allfilesname[id].length) {
                    System.out.println("该台站此目录下的所有文件已处理完毕(---没有能够对齐的文件---)");
                    System.exit(0);
                }

                timeLine[0].setFilename(allfilesname[id][pos]);
                Arrays.sort(timeLine, new ComparatorTimeLine());
                str_oldest = "20" + StrUtil.getTimeFromMrLiuFileName(timeLine[0].getFilename());
                str_newest = "20" + StrUtil.getTimeFromMrLiuFileName(timeLine[Parameters.SensorNum - 1].getFilename());

                oldest_date = DateUtil.toDate(str_oldest).getTime();
                newest_date = DateUtil.toDate(str_newest).getTime();
            }
        } else if (Parameters.region_offline == "pingdingshan" || Parameters.region_offline == "hongyang") {
            while ((newest_date - oldest_date) >= (3600 - 3 * 60) * 1000) {
                int id = timeLine[0].getId();
                timeLine[0].setPosition(timeLine[0].getPosition() + 1);
                int pos = (timeLine[0].getPosition());
                if (pos >= allfilesname[id].length) {
                    System.out.println("该台站此目录下的所有文件已处理完毕(---没有能够对齐的文件---)");
                    System.exit(0);
                }

                timeLine[0].setFilename(allfilesname[id][pos]);
                Arrays.sort(timeLine, new ComparatorTimeLine());
                str_oldest = "20" + timeLine[0].getFilename().substring(5, 17);
                str_newest = "20" + timeLine[Parameters.SensorNum - 1].getFilename().substring(5, 17);

                oldest_date = DateUtil.toDate(str_oldest).getTime();
                newest_date = DateUtil.toDate(str_newest).getTime();
            }
        }


    }

    /**
     * 旧设备的文件对齐方法，一次读一个文件，一小时处理之后，再去找文件，费时间，已废弃不用
     *
     * @param fileName
     * @param time
     * @return
     * @throws Exception
     */
    @Deprecated
    public int[] AlignMain(String fileName[], String time) throws Exception {

        String str_oldest;
        String str_newest;
        long oldest_date;
        long newest_date;
        for (int i = 0; i < Parameters.SensorNum; i++) {
            time_sorted[i] = new TimeLine();
            time_sorted[i].setId(i);
            time_sorted[i].setFilename(FindHistoryFile.getFile(fileName[i], i, time).getName());
            time_sorted[i].setFilepath(FindHistoryFile.getFile(fileName[i], i, time).getParent());

        }
        Arrays.sort(time_sorted, new ComparatorTimeLine()); //五个台站时间排序

        for (int i = 0; i < Parameters.SensorNum; i++) {
            System.out.println(time_sorted[i].getId() + " " + time_sorted[i].getFilename() + " " + time_sorted[i].getFilepath());

        }

        str_oldest = "20" + time_sorted[0].getFilename().substring(5, 17);//最老时间字符串
        str_newest = "20" + time_sorted[Parameters.SensorNum - 1].getFilename().substring(5, 17);//最新时间字符串

        oldest_date = DateUtil.toDate(str_oldest).getTime(); //最老时间
        newest_date = DateUtil.toDate(str_newest).getTime(); ///最新时间
        if (Parameters.region_offline == "datong") {
            while ((newest_date - oldest_date) > 1800 * 1000) {
                String next_time = DateUtil.toString(new Date(oldest_date + 1800 * 1000)).substring(2);

                time_sorted[0].setFilename(FindHistoryFile.getFile(time_sorted[0].getFilepath(), time_sorted[0].getId(), next_time).getName());

                Arrays.sort(time_sorted, new ComparatorTimeLine());

                str_oldest = "20" + time_sorted[0].getFilename().substring(5, 17);
                str_newest = "20" + time_sorted[Parameters.SensorNum - 1].getFilename().substring(5, 17);

                oldest_date = DateUtil.toDate(str_oldest).getTime();
                newest_date = DateUtil.toDate(str_newest).getTime();
            }
        } else if (Parameters.region_offline == "pingdingshan") {
            while ((newest_date - oldest_date) > 3600 * 1000) {
                String next_time = DateUtil.toString(new Date(oldest_date + 3600 * 1000)).substring(2);

                time_sorted[0].setFilename(FindHistoryFile.getFile(time_sorted[0].getFilepath(), time_sorted[0].getId(), next_time).getName());

                Arrays.sort(time_sorted, new ComparatorTimeLine());

                str_oldest = "20" + time_sorted[0].getFilename().substring(5, 17);
                str_newest = "20" + time_sorted[Parameters.SensorNum - 1].getFilename().substring(5, 17);

                oldest_date = DateUtil.toDate(str_oldest).getTime();
                newest_date = DateUtil.toDate(str_newest).getTime();
            }
        }

        for (int i = 0; i < Parameters.SensorNum; i++) {
            System.out.println(time_sorted[i].getFilename());
        }


        return null;
    }

}
