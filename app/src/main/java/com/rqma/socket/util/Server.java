package com.rqma.socket.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created RQMA on 2018/10/14.
 */
public class Server {
    public static void main(String[] args) {
        int server_port = 6666;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(server_port);
            while (true) {
                System.out.println("服务器监听中:");
                Socket socket = serverSocket.accept();
                new ServerThread(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
