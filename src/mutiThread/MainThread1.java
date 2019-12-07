//package mutiThread;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Vector;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.ReadWriteLock;
//import java.util.concurrent.locks.ReentrantLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
//import com.h2.backupData.recordStatus;
//import com.h2.backupData.saveOri;
//import com.h2.constant.Parameters;
//import com.h2.main.EarthQuake;
//import com.h2.tool.motiDiag;
//import com.yang.readFile.ReadData;
//import mianThread.DuiQi;
//import controller.SensorDataRead;
//import bean.DataRec;
//import bean.Location;
//
//public class MainThread  extends Thread{
//
//
//	private static ReentrantLock lock = new ReentrantLock();
//	public static DataRec aDataRec11;
//	public static DataRec aDataRec12;
//	public static DataRec aDataRec13;
//	public static DataRec aDataRec14;
//	public static DataRec aDataRec15;
//	public static int countRestart = 0;
//	/**
//	 * fileStr变量说明：该变量控制所有传感器点的个数，原则上该数组应当与矿区传感器个数相同
//	 */
//	public static String fileStr[] = new String[5];
//	public static long m1=0,m2=0;
//	public static int discRe = 0;//测试第三种情况时的临时变量，后期可删除
//	public void run() {
//		System.out.println("开始主线程！");
//
//		fileStr[0] = "z:/";
//		fileStr[1] = "z:/";
//		fileStr[2] = "z:/";
//		fileStr[3] = "z:/";
//		fileStr[4] = "z:/";
//
//		Vector<String>[] beforeVector=new Vector[Parameters.SensorNum];
//		Vector<String>[] nowVector=new Vector[Parameters.SensorNum];
//		Vector<String>[] afterVector=new Vector[Parameters.SensorNum];
//		ReconnectToRemoteDisk ReConnect = new ReconnectToRemoteDisk(3,fileStr);//重新分配盘符
//		DataRec[] dataRecArray=new DataRec[Parameters.SensorNum];
//		for(int i=0;i<Parameters.SensorNum;i++) {
//			dataRecArray[i] = new DataRec(beforeVector[i],nowVector[i],afterVector[i]);
//		}
//
//		Vector<String>[] oneMinuteData = new Vector[Parameters.SensorNum];
//		final Vector<String> sensorData[][] = new Vector[Parameters.SensorNum][3];
//		/**保存盘符*/
//
//		/**保存对齐后的timeCount变量*/
//		int kuai[]=new int[Parameters.SensorNum];
//
//		/**保存程序开始运行的时间 */
//		long StartProTime = System.currentTimeMillis();
//
//		/**进行盘符连通判断*/
//		int discSymbol = 0;
//
//		//-------------------------------------------------------------计算对齐数组
//		DuiQi aduiqi = new DuiQi();
//		try {
//			DuiQi.duiqi = aduiqi.DuiQiMain(fileStr);
//			for(int i=0;i<Parameters.SensorNum;i++) {
//				if(DuiQi.duiqi[i]==-1) {
//					System.out.println("网络状况不佳，请检查盘符或等待一段时间再启动!");
//					System.exit(0);
//				}
//			}
//		}
//		catch (Exception e1) {
//			System.out.println("网络状况不佳，请检查盘符或等待一段时间再启动!");
//			System.exit(0);
//		}
//		ReadData[] readDataArray=new ReadData[Parameters.SensorNum];
//		//-------------------------------------------------------------创建读对象
//		try {
//			for(int i=0;i<fileStr.length;i++) {
//				try {
//					readDataArray[i]=new ReadData(fileStr[i],i);
//				}
//				catch(Exception e) {
//					System.out.println(i+"号网络状况不佳，请检查盘符或等待一段时间再启动");
//					System.exit(0);
//				}
//			}
//		}
//		catch (Exception e1) {e1.printStackTrace();}
//
//		//----------------------------------------------------------------对齐
//		try {
//			m1 = System.currentTimeMillis();
//			for(int i=0;i<Parameters.SensorNum;i++) {
//				kuai[i]=readDataArray[i].readDataDui(fileStr[i],i);
//				if(kuai[i]==-1){System.out.println("网络状况不佳，请检查盘符或等待一段时间再启动!");System.exit(0);}
//			}
//			m2 = System.currentTimeMillis();
//			System.out.println("对齐花费："+(m2-m1)/1000+"s");
//		}
//		catch (Exception e) {e.printStackTrace();}
//
//		ExecutorService executor = Executors.newFixedThreadPool(5);
//		//int discRe = 0;//测试第三章情况时的临时变量，后期可删除
//		//String []netDetectArray = new String[26];
//		//-------------------------------------------------------------------------------------------------------------------------------------
//		while(true) {
//			//discRe++;
//			synchronized(this){
//
//				if(ReadData.netError==true||ReadData.newData==true){//只有产生了网络错误或者新文件时才清空所有容器数据
//					//---------------------------------------------------------判断对齐时的花费时间，若任意台站对齐时间超过10s种，则说明此时的台站分布情况不合理，需要重新安排对齐情况
//
//					if(discSymbol==ReConnect.arrayList.size()) discSymbol=0;//When the all situations has been considered, the procedure will start from the beginning.
//					fileStr = ReConnect.rearrange(discSymbol);
//					for(int i=0;i<Parameters.SensorNum;i++) {
//						dataRecArray[i].afterVector.removeAllElements();
//						dataRecArray[i].nowVector.removeAllElements();
//						dataRecArray[i].beforeVector.removeAllElements();
//						System.out.print(fileStr[i]);
//					}
//				}
//				if(ReadData.netError == false && ReadData.newData == true){//某个台站产生了新文件，再次进行对齐，但没产生网络错误，正常对齐
//					//aduiqi = null;aReadData01 = null;aReadData02 = null;aReadData03 = null;aReadData04 = null;aReadData05 = null;
//					String firstStatus = "第一种情况：没产生网络错误，产生了新文件！";
//					System.out.println(firstStatus);
//					//recordStatus.recordToTxt_status(firstStatus);
//					//---------------------------------------------------------求对齐数组
//					DuiQi aDuiQi = new DuiQi();
//					try {
//						DuiQi.duiqi = aDuiQi.DuiQiMain(fileStr);//更新对齐位置，同样可能产生网络错误
//						for(int duii = 0; duii<DuiQi.duiqi.length; duii++)
//							System.out.println("对齐数组为："+DuiQi.duiqi[duii]);
//					}
//					catch (Exception e1) {
//						System.out.println("产生对齐数组时网络错误");
//						discSymbol++;
//						continue;
//					}
//					//-------------------------------------------------------新建读对象
//					try {
//						for(int i=0;i<fileStr.length;i++) {
//							try {
//								readDataArray[i]=new ReadData(fileStr[i],i);
//							}
//							catch(Exception e) {
//								System.out.println(i+"号网络状况不佳，重新对齐");
//								continue;
//							}
//						}
//					}
//					catch (Exception e1) {e1.printStackTrace();}
//					//-------------------------------------------------------对齐
//					for(int i=0;i<Parameters.SensorNum;i++) readDataArray[i].timeCount=0;
//
//					try {
//						m1 = System.currentTimeMillis();
//						for(int i=0;i<Parameters.SensorNum;i++) {
//							kuai[i]=readDataArray[i].readDataDui(fileStr[i],i);
//							if(kuai[i]==-1){System.out.println(i+"号网络状况不佳，重新对齐");continue;}
//						}
//						m2 = System.currentTimeMillis();
//						System.out.println("对齐花费："+(m2-m1)/1000+"s");
//					}
//					catch (Exception e) {e.printStackTrace();}
//					ReadData.netError = false;
//					ReadData.newData = false;//对齐完成，所有台站已经开始读新文件了
//					discSymbol=0;
//				}
//
//				if(ReadData.netError == true && ReadData.newData == true){//某个台站产生了新文件，网络错误
//					//aduiqi = null;aReadData01 = null;aReadData02 = null;aReadData03 = null;aReadData04 = null;aReadData05 = null;
//					String secondStatus = "第二种情况：产生了网络错误，且产生了新文件！";
//					System.out.println(secondStatus);
//					//recordStatus.recordToTxt_status(secondStatus);
//					//产生网络错误后产生新文件，正常对齐
//					DuiQi aDuiQi = new DuiQi();
//					try {
//						DuiQi.duiqi = aDuiQi.DuiQiMain(fileStr);//更新对齐位置，同样可能产生网络错误
//						System.out.print("对齐数组为：");
//						for(int duii = 0; duii<DuiQi.duiqi.length; duii++)
//							System.out.println(DuiQi.duiqi[duii]);
//
//						if(DuiQi.duiqi[0] == -1){
//							Thread.sleep(1000*10);
//							continue;
//						}
//					}
//					catch (Exception e1) {
//						System.out.println("产生对齐数组时网络错误");
//						discSymbol++;
//						continue;
//					}
//					//-------------------------------------------------------新建读对象
//					try {
//						for(int i=0;i<fileStr.length;i++) {
//							try {
//								readDataArray[i]=new ReadData(fileStr[i],i);
//							}
//							catch(Exception e) {
//								System.out.println(i+"号网络状况不佳，请检查盘符或等待一段时间再启动");
//							}
//						}
//					}
//					catch (Exception e1) {e1.printStackTrace();}
//					//-------------------------------------------------------对齐
//					for(int i=0;i<Parameters.SensorNum;i++) readDataArray[i].timeCount=0;
//
//					try {
//						m1 = System.currentTimeMillis();
//						for(int i=0;i<Parameters.SensorNum;i++) {
//							kuai[i]=readDataArray[i].readDataDui(fileStr[i],i);
//							if(kuai[i]==-1){System.out.println(i+"号网络状况不佳，重新对齐");continue;}
//						}
//						m2 = System.currentTimeMillis();
//						System.out.println("对齐花费："+(m2-m1)/1000+"s");
//					}
//					catch (Exception e) {e.printStackTrace();}
//					ReadData.netError = false;
//					ReadData.newData = false;//对齐完成，所有台站已经开始读新文件了
//					discSymbol=0;
//				}
//
//				if(ReadData.netError == true && ReadData.newData == false){//此时只产生了网络错误，没有新文件产生
//					//if(discRe>10){
//					//aduiqi = null;aReadData01 = null;aReadData02 = null;aReadData03 = null;aReadData04 = null;aReadData05 = null;
//					String thirdStatus = "第三种情况：只产生了网络错误，没有产生新文件！";
//					System.out.println(thirdStatus);
//					DuiQi aDuiQi = new DuiQi();
//					try {
//						DuiQi.duiqi = aDuiQi.DuiQiMain(fileStr);//更新对齐位置，同样可能产生网络错误
//						for(int duii = 0; duii<DuiQi.duiqi.length; duii++)
//							System.out.println("对齐数组为："+DuiQi.duiqi[duii]);
//						if(DuiQi.duiqi[0] == -1){
//							Thread.sleep(1000*10);
//							continue;
//						}
//					}
//					catch (Exception e1) {
//						System.out.println("产生对齐数组时网络错误");
//						discSymbol ++;
//						continue;
//					}
//					//-------------------------------------------------------新建读对象
//					try {
//						for(int i=0;i<Parameters.SensorNum;i++) {
//							try {
//								readDataArray[i]=new ReadData(fileStr[i],i);
//							}
//							catch(Exception e) {
//								System.out.println(i+"号网络状况不佳，重新对齐");
//								continue;
//							}
//						}
//					}
//					catch (Exception e1) {e1.printStackTrace();}
//					//-------------------------------------------------------对齐
//					for(int i=0;i<Parameters.SensorNum;i++) readDataArray[i].timeCount=0;
//
//					try {
//						m1 = System.currentTimeMillis();
//						for(int i=0;i<Parameters.SensorNum;i++) {
//							kuai[i]=readDataArray[i].readDataDui(fileStr[i],i);
//							if(kuai[i]==-1){System.out.println(i+"号网络状况不佳，重新对齐");continue;}
//						}
//						m2 = System.currentTimeMillis();
//						System.out.println("对齐花费："+(m2-m1)/1000+"s");
//					}
//					catch (Exception e) {e.printStackTrace();}
//					ReadData.netError = false;
//					ReadData.newData = false;//对齐完成，所有台站已经开始读新文件了
//					discSymbol=0;
//				}
//				//--------------------------------------------------------------------开启线程池，读取5个地点数据
//				if(ReadData.netError == false){//网络错误时不执行读数据
//	//				MainThread.countRestart = 0;
//					final CountDownLatch threadSignal = new CountDownLatch(Parameters.SensorNum);
//					for(int i=0;i<Parameters.SensorNum;i++) {
//						readTask task = new readTask(threadSignal,i,dataRecArray[i],readDataArray[i],fileStr[i]);
//						executor.execute(task);
//					}
//					try {threadSignal.await();}
//					catch (InterruptedException e1) {e1.printStackTrace();}
//				}
//			}
//
//			saveOri save = new saveOri();//存储1分钟数据类
//			motiDiag moti = new motiDiag();//激发判断类
//			int[] motiPos = new int[1000];//激发位置数组
//
//			if(ReadData.newData == false && ReadData.netError==false){//错过最后一次计算，也就是说最后的几秒钟数据废弃了
//				MainThread.countRestart++;
//
//				for(int i=0;i<Parameters.SensorNum;i++) {
//					sensorData[i][0] = dataRecArray[i].getBeforeVector();
//					sensorData[i][1] = dataRecArray[i].getNowVector();
//					sensorData[i][2] = dataRecArray[i].getAfterVector();
//				}
//				if(sensorData[0][1]==null||sensorData[0][0]==null) continue;//当now容器中没有数据时，不进入计算
//				if(Parameters.isStorage1 == 1) {
//					for(int i=0;i<MainThread.fileStr.length;i++){
//						oneMinuteData[i].addAll(sensorData[i][2]);
//						if(MainThread.countRestart%6==0){//每逢6就将数据导出到txt
//							motiPos = moti.motiD(oneMinuteData[i]);//对0号台站进行激发数据的测试
//							save.saveOrii(oneMinuteData[i], MainThread.fileStr[i],motiPos);//保存最新容器中的10s数据，以1分钟为单位
//							oneMinuteData[i].clear();
//						}
//					}
//					for(int i=0;i<motiPos.length;i++) motiPos[i]=0;
//				}
//
//				//初始化盘符存储变量，因为后面还有存储任务，需要用到改变量。
//				for(int i=0;i<Parameters.SensorNum;i++)
//				Parameters.initPanfu[i] = 0;
//
//				//震级计算
//				try {
//					EarthQuake.runmain(sensorData);
//					System.out.println("一次震级计算");
//
//					//还需要初始化一遍盘符存储变量
//					for(int i=0;i<Parameters.SensorNum;i++)
//						Parameters.initPanfu[i] = 0;
//
//				} catch (Exception e) {e.printStackTrace();}
//			}//end if
//		}//end synchronized
//	}
//}
//
// class readTask implements Runnable{
//    private CountDownLatch downLatch;
//    private int sensorID;
//    private String sensorName;
//	private DataRec dataRec;
//	private ReadData readData;
//	private Vector<String> aVector;
//	private Vector<String> temVector;
//	private int num;
//
//	public readTask(CountDownLatch downLatch, int sensorName, DataRec dataRec, ReadData readData,String fileStr) {
//		super();
//		this.downLatch = downLatch;
//		this.sensorID = sensorName;
//		this.sensorName = fileStr;
//		this.dataRec = dataRec;
//		this.readData = readData;
//		num=1;
//		aVector = new Vector<String>();
//		temVector = new Vector<String>();
//	}
//
//	private void doWork() throws Exception {
//
//		while (num <= 10) {//读取10s的数据
//			temVector = readData.getData(sensorName,sensorID);//获取1s数据，传递盘符、盘号
//			if(ReadData.newData==true)	{
//				System.out.println("第"+sensorID+"号"+ReadData.newData+"进入while");
//				return;
//			}
//			if(ReadData.netError==true)	{
//				return;
//			}
//			aVector.addAll(temVector);
//			num++;
//		}
//		dataRec.DataSwap(aVector);
//	}
//	public void run(){
//		try {this.doWork();}
//		catch (Exception e) {e.printStackTrace();}
//		this.downLatch.countDown();
//	}
//}