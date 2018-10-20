package com.rqma.socket.medium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by RQMA on 2018/10/14.
 */
public class ServerThread implements Runnable {
    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream is = null;
        BufferedReader bufferedReader = null;
        OutputStream os = null;
        try {
            //从客户端接收数据
            is = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String str ;
            while ((str = bufferedReader.readLine()) != null) {
                sb.append(str);
            }
            System.out.println("客户端[" + socket.getInetAddress().getHostAddress()+ "]发来的信息是: " + sb.toString());
            socket.shutdownInput();


            //向客户端发送数据
            os = socket.getOutputStream();
            os.write(String.valueOf(sb).getBytes());
            os.flush();
            socket.shutdownOutput();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                bufferedReader.close();
                is.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void start() {

        new Thread(new ServerThread(socket)).start();
    }
}
