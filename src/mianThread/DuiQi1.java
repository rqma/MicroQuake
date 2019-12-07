//package mianThread;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//
//import com.h2.constant.Parameters;
//import com.yang.readFile.FindNewHFMED;
//import com.yang.readFile.FindNewFile;
//import com.yang.readFile.findNew;
//import com.yang.readFile.ReadData;
//
//import utils.DateArrayToIntArray;
//
//import bean.Location;
//
//import controller.ReadDataSegmentHead;
//import controller.SensorDataReadForDuiQi;
//
//public class DuiQi {
//
//	public static int duiqi[] = new int[Parameters.SensorNum];
//	/**
//	 * 查找到的最新文件，
//	 */
//	public static String[] file1 = new String[Parameters.SensorNum];
//	
//	private String dateStr;
//	public DuiQi() {
//		super();
//		for(int i=0;i<Parameters.SensorNum;i++)
//		duiqi[i] = 0;
//	}
//
//	public String getDateStr() {
//		return dateStr;
//	}
//
//	public void setDateStr(String dateStr) {
//		this.dateStr = dateStr;
//	}
//
//	public int[] DuiQiMain(String[] fileName)
//			throws Exception {
//		
//		FindNewFile aFile =new FindNewFile();
//		for(int i=0;i<fileName.length;i++) {
////			fileName1[i]=aFile.researchFile(fileName[i], i).getAbsolutePath();
//			file1[i]=findNew.find(fileName[i],i).getAbsolutePath();
//		}
////		fileName1=findNew.find(fileName01,0).getAbsolutePath();
////		fileName2=findNew.find(fileName02,1).getAbsolutePath();
////		fileName3=findNew.find(fileName03,2).getAbsolutePath();
////		fileName4=findNew.find(fileName04,3).getAbsolutePath();
////		fileName5=findNew.find(fileName05,4).getAbsolutePath();
//		/*catch(Exception e){
//			System.out.println("网络状况不佳，无法启动程序，请稍后再试");
//			for(int i = 0 ; i<Parameters.SensorNum;i++)
//				duiqi[i]=-1;
//			return duiqi;
//		}*/
//		int[] TimeDifferertInt = new int[fileName.length];
//
//		String[] dateString = new String[TimeDifferertInt.length];
//
//		// 一个传感器的时间
//		try{
//			for(int i=0;i<fileName.length;i++) {
//				ReadDataSegmentHead readDateSegmentHead = new ReadDataSegmentHead();
//				String dateStr = readDateSegmentHead.readDataSegmentHead(file1[i]);
//				dateString[i] = dateStr;
//			}
//		}
//		catch(Exception e1){
//			ReadData.netError = true;
//			for(int k = 0; k < Parameters.SensorNum; k++){
//				DuiQi.duiqi[k] = -1;
//			}
//			return DuiQi.duiqi;//返回了-1的数组
//		}
//		System.out.println("对齐数组为：");
//		for(int i = 0; i <fileName.length; i++){
//			System.out.println(dateString[i]);
//		}
//				
//		DateArrayToIntArray aDateArrayToIntArray =new DateArrayToIntArray();
//		TimeDifferertInt = aDateArrayToIntArray.IntArray(dateString); 
//		Date DateMax  =new Date();
//		DateMax = aDateArrayToIntArray.getDateStr();
//				 
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
//		//String str=sdf.format(date); 
//		this.dateStr=sdf.format(DateMax);
//		System.out.println("对齐最大时间："+this.dateStr);
//		
//		/*for (int i = 0; i < TimeDifferertInt.length; i++) {
//			SensorDataReadForDuiQi aDataReadForDuiQi = new SensorDataReadForDuiQi(
//					TimeDifferertInt[i]);
//			aLocation[i] = aDataReadForDuiQi.GetLocationDuiQi();
//		}*/
//		System.out.println("对齐成功");
//		return TimeDifferertInt;
//	}
//
//}
