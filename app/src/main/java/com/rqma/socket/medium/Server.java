package com.rqma.socket.medium;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by RQMA on 2018/10/14.
 */
public class Server {
    public static void main(String[] args) {
        int server_port = 6668;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(server_port);
            System.out.println("服务器监听中:");
            while (true) {
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
