//package mutiThread;
//
//import com.h2.constant.Parameters;
//import com.yang.readFile.ReadData;
//
//import mianThread.DuiQi;
//
//public class verifyIsStart {
//	public static int duiqiProcessing(int discSymbol,ReadData[] readDataArray,int[] kuai) {
//		//aduiqi = null;aReadData01 = null;aReadData02 = null;aReadData03 = null;aReadData04 = null;aReadData05 = null;
//		String firstStatus = "第一种情况：没产生网络错误，产生了新文件！";
//		System.out.println(firstStatus);
//		//recordStatus.recordToTxt_status(firstStatus);
//		//---------------------------------------------------------求对齐数组
//		DuiQi aDuiQi = new DuiQi();
//		try {
//			DuiQi.duiqi = aDuiQi.DuiQiMain(MainThread.fileStr);//更新对齐位置，同样可能产生网络错误
//			for(int duii = 0; duii<DuiQi.duiqi.length; duii++)
//				System.out.println("对齐数组为："+DuiQi.duiqi[duii]);
//		}
//		catch (Exception e1) {
//			System.out.println("产生对齐数组时网络错误");
//			discSymbol++;
//			return -1;
//		}
//		//-------------------------------------------------------新建读对象
//		try {
//			for(int i=0;i<MainThread.fileStr.length;i++) {
//				try {
//					readDataArray[i]=new ReadData(MainThread.fileStr[i],i);
//				}
//				catch(Exception e) {
//					System.out.println(i+"号网络状况不佳，重新对齐");
//					return -1;
//				}
//			}
//		}
//		catch (Exception e1) {e1.printStackTrace();return -1;}
//		//-------------------------------------------------------对齐 
//		for(int i=0;i<Parameters.SensorNum;i++) readDataArray[i].timeCount=0;
//		
//		try {
//			MainThread.m1 = System.currentTimeMillis();
//			for(int i=0;i<Parameters.SensorNum;i++) {
//				kuai[i]=readDataArray[i].readDataDui(MainThread.fileStr[i],i);
//				if(kuai[i]==-1){System.out.println(i+"号网络状况不佳，重新对齐");continue;}
//			}
//			MainThread.m2 = System.currentTimeMillis();
//			System.out.println("对齐花费："+(MainThread.m2-MainThread.m1)/1000+"s");
//			return 0;
//		}
//		catch (Exception e) {e.printStackTrace();return -1;}
//	}
//}
