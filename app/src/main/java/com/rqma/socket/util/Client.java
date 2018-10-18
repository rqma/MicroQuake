package com.rqma.socket.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created RQMA on 2018/10/14.
 */

public class Client {
    public static void main(String[] args) {
        String server_addr = "192.168.1.100";
        Integer server_port = 6666;
        Socket socket_client = null;
        OutputStream os = null;
        InputStream is = null;
        BufferedReader bufferedReader = null;
        try {
            //建立连接
            socket_client = new Socket(server_addr, server_port);

            //向服务器发送数据
            os = socket_client.getOutputStream();
            os.write("java socket".getBytes());
            os.flush();
            socket_client.shutdownOutput();

            //接收从服务器返回的数据
            is = socket_client.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            // sb.append("client");
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                sb.append(str);
            }
            System.out.println(sb.toString());

        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                is.close();
                os.close();
                socket_client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
