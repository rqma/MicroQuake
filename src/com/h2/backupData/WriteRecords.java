package com.h2.backupData;

import com.h2.constant.Parameters;
import com.h2.constant.Sensor;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Description:记录位置，手动计算，验证是否正确
 * @Auther: RQMA
 * @Date: 5/10/2019 12:28 PM
 */
public class WriteRecords {
    public static void Write(Sensor[]sensors , Sensor result,Sensor time_refine, String filepath) {
        File file = new File(filepath);
        BufferedWriter out = null;
        String record;
        try {
            out = new BufferedWriter(new FileWriter(file, true));
            if (!file.exists()) file.createNewFile();
            for (int i = 0; i < Parameters.SensorNum; i++) {
            	record=sensors[i].getLatitude()+"\t\t"+sensors[i].getLongtitude()+"\t\t"+sensors[i].getAltitude()+"\t\t"+sensors[i].getSecTime()+"\r\n";
                out.write(record);
            }
            record=result.getLatitude()+"\t\t"+result.getLongtitude()+"\t\t"+result.getAltitude()+"\t\t"+result.getquackTime()+"\r\n";

            out.write(record);
            out.write("---------------------------------------------------------------------------\r\n");
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
