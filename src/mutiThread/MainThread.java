package mutiThread;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.h2.backupData.recordStatus;
import com.h2.backupData.saveOri;
import com.h2.constant.Parameters;
import com.h2.main.EarthQuake;
import com.h2.tool.motiDiag;
import com.rqma.history.*;

import com.yang.readFile.ReadData;
import mianThread.DuiQi;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import utils.DateArrayToIntArray;
import controller.SensorDataRead;
import bean.DataRec;
import bean.Location;
/**
 *
 * @author Yilong Zhang, Hanlin Zhang, rqma, Rui Cao, et al.
 */
public class MainThread extends Thread{
    /**
     * fileStr must define at first with different path for your destination.
     */
    public static String fileStr[] = new String[5];
    /**this variable obtain the parent fold name.*/
    public static String fileParentPackage[] = new String[Parameters.SensorNum];
    /**this variable exchange data to the foreground.*/
    public static DataRec[] aDataRec = new DataRec[Parameters.SensorNum];
    /**a counter count per 10 sec. for storing one minute data.*/
    public static int countRestart = 0;
    /**reduiqi status*/
    public static int []reduiqi=new int[Parameters.SensorNum];
    /**Is not restart the procedure*/
    public static int IsContinue=0;
    /**is not the first time start the procedure*/
    public static boolean isFirst = true;
    /**the file creation time*/
    public static String [] dateString=new String[Parameters.SensorNum];
    public static long m1=0,m2=0;

    @SuppressWarnings("unused")
    public void run() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();//do not call this function many times.
        /**when we need to read data offline, we can use the absolute path as follows.*/
        fileStr[0] = "F:/datong/1/Test1/";
        fileStr[1] = "F:/datong/2/Test2/";
        fileStr[2] = "F:/datong/4/Test4/";
        fileStr[3] = "F:/datong/5/Test5/";
        fileStr[4] = "F:/datong/6/Test6/";

        /**when we need to read data online, we can use the absolute path as follows.*/
	/*	fileStr[0] = "z:/";
		fileStr[1] = "y:/";
		fileStr[2] = "u:/";
		fileStr[3] = "v:/";
		fileStr[4] = "w:/";
		fileStr[5] = "x:/";
		fileStr[6] = "y:/";
		fileStr[7] = "z:/";
	*/

        if(Parameters.offline==false) {
            System.out.println("开始读实时数据主线程！");
            Vector<String>[] beforeVector=new Vector[Parameters.SensorNum];
            Vector<String>[] nowVector=new Vector[Parameters.SensorNum];
            Vector<String>[] afterVector=new Vector[Parameters.SensorNum];
            ReconnectToRemoteDisk ReConnect = new ReconnectToRemoteDisk(3,fileStr);//重新分配盘符
            DataRec[] dataRecArray=new DataRec[Parameters.SensorNum];
            Random r = new Random();

            for(int i=0;i<Parameters.SensorNum;i++) {
                dataRecArray[i] = new DataRec(beforeVector[i],nowVector[i],afterVector[i]);
                aDataRec[i] = dataRecArray[i];
            }

            Vector<String>[] oneMinuteData = new Vector[Parameters.SensorNum];
            final Vector<String> sensorData[][] = new Vector[Parameters.SensorNum][3];

            //3个线程池，分别用于查找文件、对齐和读取数据
//			ExecutorService executor_readdata = Executors.newFixedThreadPool(Parameters.SensorNum);

//			ExecutorService executor_duiqi = Executors.newFixedThreadPool(Parameters.SensorNum);

            ExecutorService executor_find = Executors.newFixedThreadPool(Parameters.SensorNum);

            /**保存程序开始运行的时间 */
            long StartProTime = System.currentTimeMillis();

            /**进行盘符连通判断*/
            int discSymbol = ReConnect.arrayList.size()-1;

            /**an object of readData*/
            verifyIsStart []ve = new verifyIsStart[Parameters.SensorNum];

            while(true) {
                try {
                    Thread.sleep(1000*(r.nextInt(10)+1));
                } catch (InterruptedException e2) {	e2.printStackTrace();}
                if(ReadData.netError==true||ReadData.newData==true||isFirst==true){//只有产生了网络错误或者新文件时才清空所有容器数据
                    //---------------------------------------------------------判断对齐时的花费时间，若任意台站对齐时间超过10s种，则说明此时的台站分布情况不合理，需要重新安排对齐情况
                    System.out.println("进入了重对齐！");
                    if(fileStr.length<3)
                        discSymbol=0;
                    else if(discSymbol<=-1)
                        discSymbol=ReConnect.arrayList.size()-1;//When the all situations has been considered, the procedure will start from the beginning.

                    if(isFirst==false) {
                        if(fileStr.length>=3)
                            fileStr = ReConnect.rearrange(discSymbol);
                        for(int i=0;i<Parameters.SensorNum;i++) {
                            //						dataRecArray[i].afterVector.removeAllElements();
                            //						dataRecArray[i].nowVector.removeAllElements();
                            //						dataRecArray[i].beforeVector.removeAllElements();
                            ve[i].readDataArray.timeCount=0;
                            DuiQi.duiqi[i]=0;
                            System.out.print(fileStr[i]);
                        }
                        System.out.println();
                    }

                    final CountDownLatch threadSignal_find = new CountDownLatch(Parameters.SensorNum);
                    //---------------------------------------------------------求对齐数组
                    for(int i=0;i<Parameters.SensorNum;i++) {
                        DuiQi aDuiQi = new DuiQi(threadSignal_find,fileStr[i],i);
                        executor_find.execute(aDuiQi);
                    }

                    try {threadSignal_find.await();}
                    catch (InterruptedException e1) {e1.printStackTrace();}

                    for(int i=0;i<Parameters.SensorNum;i++){
                        if(reduiqi[i]==-1) {
                            IsContinue=-1;
                            break;
                        }
                    }
                    if(IsContinue==-1){
                        discSymbol--;
                        IsContinue=0;
                        for(int i=0;i<Parameters.SensorNum;i++)
                            reduiqi[i]=0;
                        continue;
                    }

                    DateArrayToIntArray aDateArrayToIntArray =new DateArrayToIntArray();
                    try {
                        DuiQi.duiqi = aDateArrayToIntArray.IntArray(dateString);
                    } catch (ParseException e) {e.printStackTrace();}

                    Date DateMax=new Date();
                    DateMax = aDateArrayToIntArray.getDateStr();
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    System.out.println("对齐的最大时间："+sdf.format(DateMax));

                    final CountDownLatch threadSignal_duiqi = new CountDownLatch(Parameters.SensorNum);
                    for(int i=0;i<Parameters.SensorNum;i++) {
                        ve[i] = new verifyIsStart(threadSignal_duiqi,i);
                        executor_find.execute(ve[i]);
                    }
                    try {threadSignal_duiqi.await();}
                    catch (InterruptedException e1) {e1.printStackTrace();}

                    for(int i=0;i<Parameters.SensorNum;i++){
                        if(reduiqi[i]==-1) {
                            IsContinue=-1;
                            break;
                        }
                    }
                    if(IsContinue==-1){
                        for(int i=0;i<Parameters.SensorNum;i++)
                            reduiqi[i]=0;
                        IsContinue=0;
                        continue;
                    }
                    MainThread.isFirst=false;
                    ReadData.netError = false;
                    ReadData.newData = false;//对齐完成，所有台站已经开始读新文件了
                    discSymbol=ReConnect.arrayList.size()-1;
                    IsContinue=0;
                }

                //--------------------------------------------------------------------开启线程池，读取5个地点数据
                if(ReadData.netError == false){//网络错误时不执行读数据
                    //				MainThread.countRestart = 0;
                    final CountDownLatch threadSignal_readdata = new CountDownLatch(Parameters.SensorNum);
                    for(int i=0;i<Parameters.SensorNum;i++) {
                        readTask task = new readTask(threadSignal_readdata,i,dataRecArray[i],ve[i].readDataArray,fileStr[i]);
                        executor_find.execute(task);
                    }
                    try {threadSignal_readdata.await();}
                    catch (InterruptedException e1) {e1.printStackTrace();}
                }
                saveOri save = new saveOri();//存储1分钟数据类
                motiDiag moti = new motiDiag();//激发判断类
                int[] motiPos = new int[1000];//激发位置数组
                int diagnoseIsFull = 0;

                if(ReadData.newData == false && ReadData.netError==false){//错过最后一次计算，也就是说最后的几秒钟数据废弃了
                    MainThread.countRestart++;

                    for(int i=0;i<Parameters.SensorNum;i++) {
                        sensorData[i][0] = dataRecArray[i].getBeforeVector();
                        if(sensorData[i][0]==null) {diagnoseIsFull=1;}
                        sensorData[i][1] = dataRecArray[i].getNowVector();
                        if(sensorData[i][1]==null) {diagnoseIsFull=1;}
                        sensorData[i][2] = dataRecArray[i].getAfterVector();
                        if(sensorData[i][2]==null) {diagnoseIsFull=1;}
                    }

                    if(diagnoseIsFull==1) continue;//当now容器中没有数据时，不进入计算
                    diagnoseIsFull=0;
                    if(Parameters.isStorageOne == 1) {
                        for(int i=0;i<MainThread.fileStr.length;i++){
                            oneMinuteData[i].addAll(sensorData[i][2]);
                            if(MainThread.countRestart%6==0){//每逢6就将数据导出到txt
                                motiPos = moti.motiD(oneMinuteData[i]);//对0号台站进行激发数据的测试
                                save.saveOrii(oneMinuteData[i], MainThread.fileStr[i],motiPos);//保存最新容器中的10s数据，以1分钟为单位
                                oneMinuteData[i].clear();
                            }
                        }
                        for(int i=0;i<motiPos.length;i++) motiPos[i]=0;
                    }

                    //初始化盘符存储变量，因为后面还有存储任务，需要用到改变量。
                    for(int i=0;i<Parameters.SensorNum;i++)
                        Parameters.initPanfu[i] = 0;

                    //震级计算
                    try {
                        EarthQuake.runmain(sensorData);
                        //					System.out.println("chart");
                        for(int i=0;i<Parameters.SensorNum;i++)
                            Parameters.initPanfu[i] = 0;
                    } catch (Exception e) {e.printStackTrace();}
                }
            }//end while(true)
        }
        else if(Parameters.offline==true) {

            System.out.println("开始读历史数据主线程！");
            /**
             * 要计算的起始时间
             */
            String timeStr = "170101010100";
            /**
             * 五个传感器数据
             */
            final Vector<String> sensorData[][] = new Vector[Parameters.SensorNum][3];

            int count = 0;//程序计数器

            //注意，不要删除这行注释
            //牛家村(Test1)、洗煤厂(Test2)、香山矿(Test3)、王家村(Test4)、十一矿工业广场老办公楼西南角花坛(Test5)、西风井(Test6)、

            fileParentPackage= SubStrUtil.getFileParentPackage(fileStr);//文件所在的目录名

            DataRec[] dataRecArray = new DataRec[Parameters.SensorNum];

            for (int i = 0; i < Parameters.SensorNum; i++) {
                dataRecArray[i] = new DataRec(null, null, null);//不然会出现空指针
                aDataRec[i] = dataRecArray[i];
            }

            //-------------------------------------------------------------计算对齐数组
            /**
             * AlignFile ：类似于读最新文件里的 Duiqi
             * 注意，注意，注意！！！在整个程序里必须只定义一次
             */
            AlignFile alignFile = new AlignFile();

            ReadData[] readDataArray = getDataArray(alignFile, timeStr);
            count++;
            if (readDataArray != null)
                System.out.println("----------开始处理第 " + count + " 组数据---------");
            //---------------死循环开始-------------
            while (true) {
                synchronized (this) {
                    while (readDataArray == null || ReadData.newData == true) {
                        count++;
                        System.out.println("----------开始处理第 " + count + " 组数据---------");
                        readDataArray = getDataArray(alignFile, timeStr);
                        if(readDataArray==null)
                            continue;
                        ReadData.newData = false;
                    }
                    ReadData.netError = false;

                    //-----------开启线程池，读取5个地点数据
                    if (ReadData.netError == false) {//网络错误时不执行读数据

                        ExecutorService executor = Executors.newFixedThreadPool(5);
                        final CountDownLatch threadSignal = new CountDownLatch(5);

                        for (int i = 0; i < Parameters.SensorNum; i++) {
                            readTask task = new readTask(threadSignal, i, dataRecArray[i], readDataArray[i], fileStr[i]);
                            executor.execute(task);
                        }
                        try {
                            threadSignal.await();
                            executor.shutdown();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                if (ReadData.netError == false) {//错过最后一次计算，也就是说最后的几秒钟数据废弃了
                    MainThread.countRestart++;

                    for (int i = 0; i < Parameters.SensorNum; i++) {
                        if (dataRecArray[i].getBeforeVector() == null)
                            dataRecArray[i].setBeforeVector(new Vector<>());
                        if (dataRecArray[i].getNowVector() == null)
                            dataRecArray[i].setNowVector(new Vector<>());

                        sensorData[i][0] = dataRecArray[i].getBeforeVector();
                        sensorData[i][1] = dataRecArray[i].getNowVector();
                        sensorData[i][2] = dataRecArray[i].getAfterVector();
                    }

                    try {
                        EarthQuake.runmain(sensorData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }//end while(true)
        }
    }
    /**
     * 该函数体里的内容，会在程序里出现两次，为了避免代码的冗余，故放在一个函数体了，在两个地方调用函数即可
     *
     * @param alignFile
     * @param timeStr
     * @return 五个 ReadData
     */
    private ReadData[] getDataArray(AlignFile alignFile, String timeStr) {
        /**保存对齐后的timeCount变量*/
        int kuai[] = new int[Parameters.SensorNum];
        /**统计对齐时间开销*/

        /**
         * 定义 ReadData
         */
        ReadData[] readDataArray = new ReadData[Parameters.SensorNum];
        long m1 = 0;
        long m2 = 0;
        try {
            AlignFile.align = alignFile.getAlign(fileStr, timeStr);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        for (int i = 0; i < Parameters.SensorNum; i++) {
            if (AlignFile.align[i] == -1) {
                System.out.println("对齐文件时出错");
                return null;
            }
        }
        //-------------------------------------------------------------创建读对象
        String file[] = alignFile.paths_original;//文件路径
        for (int i = 0; i < Parameters.SensorNum; i++) {
            try {
                readDataArray[i] = new ReadData(file[i], i);
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        }
        //----------------------------------------------------------------对齐
        m1 = System.currentTimeMillis();
        for (int i = 0; i < Parameters.SensorNum; i++) {
            try {
                readDataArray[i].timeCount = 0;
                kuai[i] = readDataArray[i].readDataAlign(fileStr[i], i);
                if (kuai[i] == -1) {
                    System.out.println("该组数据无法对齐，开始对齐下一组---------");
                    readDataArray=null;
                    return null;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        m2 = System.currentTimeMillis();
        System.out.println("对齐时间花费：" + (m2 - m1) + "ms");

        return readDataArray;
    }
}


class readTask implements Runnable{
    private CountDownLatch downLatch;
    private int sensorID;
    private String sensorName;
    private DataRec dataRec;
    private ReadData readData;
    private Vector<String> aVector;
    private Vector<String> temVector;
    private int num;

    public readTask(CountDownLatch downLatch, int sensorName, DataRec dataRec, ReadData readData,String fileStr) {
        super();
        this.downLatch = downLatch;
        this.sensorID = sensorName;
        this.sensorName = fileStr;
        this.dataRec = dataRec;
        this.readData = readData;
        num=1;
        aVector = new Vector<String>();
        temVector = new Vector<String>();
    }

    @SuppressWarnings("unused")
    private void doWork() throws Exception {

        while (num <= 10) {//读取10s的数据
            if(Parameters.offline==true)
                temVector = readData.getOfflineData(sensorName, sensorID);
            else
                temVector = readData.getData(sensorName,sensorID);//获取1s数据，传递盘符、盘号

            if(ReadData.newData==true)	{
                System.out.println("第"+sensorID+"号"+ReadData.newData+"进入while");
                return;
            }
            if(ReadData.netError==true)	{
                return;
            }
            aVector.addAll(temVector);
            num++;
        }
        dataRec.DataSwap(aVector);
    }
    public void run(){
        try {this.doWork();}
        catch (Exception e) {e.printStackTrace();}
        this.downLatch.countDown();
    }
}