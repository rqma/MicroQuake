package com.rqma.socket.easy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by RQMA on 2018/10/13.
 */
public class Server {
    public static void main(String[] args) {
        int server_port = 6666;
        ServerSocket serverSocket = null;
        Socket socket = null;
        InputStream is = null;
        BufferedReader bufferedReader = null;
        OutputStream os = null;
        try {

            serverSocket = new ServerSocket(server_port);
            System.out.println("服务器监听中:");
            while (true) {
                socket = serverSocket.accept();

                //从客户端接收数据
                is = socket.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String str = null;
                while ((str = bufferedReader.readLine()) != null) {
                    sb.append(str);
                }
                System.out.println("客户端[" + socket.getInetAddress().getHostAddress() + "]发来的信息是: " + sb.toString());
                socket.shutdownInput();

                //要向客户端发送的数据内容
                Random random = new Random();
                int result = random.nextInt(100);

                //向客户端发送数据
                os = socket.getOutputStream();
                os.write(String.valueOf(result).getBytes());
                os.flush();
                socket.shutdownOutput();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                bufferedReader.close();
                is.close();
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
