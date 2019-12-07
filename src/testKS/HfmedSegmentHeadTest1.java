package testKS;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import utils.ReadDataSegmentHead;

public class HfmedSegmentHeadTest1 {

	public static void main(String[] args) throws Exception {

		String fileName = "B:/Users/lemo/Documents/Tencent Files/1159639005/FileRecv/gygc_161126173902.HFMED";

		// String fileName =
		// "C:/Users/NiuNiu/Desktop/HYJ/GYGC/04/gygc_161126173902.HFMED" ;

		ReadDataSegmentHead readDateSegmentHead = new ReadDataSegmentHead();
		
		String dateString = readDateSegmentHead.readDataSegmentHead(fileName);
		System.out.println(dateString);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyMMddhhmmss");
		//dateString=dateString.substring(2);
		System.out.println(dateString);
		Date date = simpleDateFormat.parse(dateString);
		//Date date2=date.;
		
		Date date3 = (new SimpleDateFormat("yyyyMMddhhmmss")).parse(dateString);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date3);
		for (int i = 0; i < 100; i++) {
			cal.add(Calendar.SECOND,1);
		}
		
		date =cal.getTime();
		String startdate = (new SimpleDateFormat("yyyyMMddhhmmss")).format(date);//加一天后结果
		
		
		System.out.println(date.toString());
		System.out.println(startdate+" startdate");

		Date calendarTdate=cal.getTime();
		System.out.println(calendarTdate);
	}

}
