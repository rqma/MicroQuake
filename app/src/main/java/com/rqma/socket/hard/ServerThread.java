package com.rqma.socket.hard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by RQMA on 2018/10/15.
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
        int result;
        try {
            //从客户端接收数据
            is = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                sb.append(str);
            }
            System.out.println("客户端[" + socket.getInetAddress().getHostAddress()+ "]发来的信息是: " + sb.toString());
            socket.shutdownInput();

            //判断移动方向是否合法
            result = IsOutSquare(sb) ? 1 : 0;
            System.out.println("向客户端发送的数据信息: " + result);

            //向客户端发送数据
            os = socket.getOutputStream();
            os.write(String.valueOf(result).getBytes());
           // System.out.println(result);
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

    private boolean IsOutSquare(StringBuilder sb) {

        final int DIRECTION_LEFT = -1;
        final int DIRECTION_RIGHT = 1;
        final int DIRECTION_FORWARD = -2;
        final int DIRECTION_BACKWARD = 2;

        final float MAGIN_LEFT = 200;//左边界
        final float MAGIN_RIGHT = 400;//右边界
        final float MAGIN_FORWARD = 200;//上边界
        final float MAGIN_BACKWARD = 400;//下边界

        String str_loc[] = sb.toString().split(",");
        float loc_X = Float.valueOf(str_loc[0]);
        float loc_Y = Float.valueOf(str_loc[1]);
        int direction = Integer.valueOf(str_loc[2]);
        float speed = Float.valueOf(str_loc[3]);
        int flag = 0;
        switch (direction) {
            case DIRECTION_LEFT: {
                if (loc_X - speed < MAGIN_LEFT)
                    flag = 1;
                break;
            }
            case DIRECTION_RIGHT: {
                if (loc_X + speed > MAGIN_RIGHT)
                    flag = 1;
                break;
            }
            case DIRECTION_FORWARD: {
                if (loc_Y - speed < MAGIN_FORWARD)
                    flag = 1;
                break;
            }
            case DIRECTION_BACKWARD: {
                if (loc_Y + speed > MAGIN_BACKWARD)
                    flag = 1;
                break;
            }

        }
        return flag == 1 ? true : false;
    }

    public void start() {

        new Thread(new ServerThread(socket)).start();
    }
}
