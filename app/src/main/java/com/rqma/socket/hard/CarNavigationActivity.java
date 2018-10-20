package com.rqma.socket.hard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.rqma.socket.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.Socket;


/**
 * Created by RQMA on 2018/10/15.
 */
public class CarNavigationActivity extends Activity implements OnClickListener {

    private Button bt_forward, bt_backward, bt_left, bt_right;
    private ImageView iv_car;
    private TextView tt_location;
    private String str_location;
    private int result;

    private float current_X = 300;
    private float current_Y = 300;

    private float speed = 10;
    private int direction = 0;

    private final int LEFT = -1;
    private final int RIGHT = 1;
    private final int FORWARD = -2;
    private final int BACKWARD = 2;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            result = Integer.valueOf(msg.obj.toString());

            System.out.println("服务器返回的result值:" + result);
            if (result == 1) {
                Toast.makeText(CarNavigationActivity.this, "哈哈哈,此路不通", Toast.LENGTH_SHORT).show();
            } else {
                switch (direction) {
                    case LEFT: {
                        iv_car.setX(current_X - speed);
                        current_X = iv_car.getX();
                        break;
                    }
                    case RIGHT: {
                        iv_car.setX(current_X + speed);
                        current_X = iv_car.getX();
                        break;
                    }
                    case FORWARD: {

                        iv_car.setY(current_Y - speed);
                        current_Y = iv_car.getY();
                        break;
                    }
                    case BACKWARD: {
                        iv_car.setY(current_Y + speed);
                        current_Y = iv_car.getY();
                        break;
                    }
                }
                str_location = iv_car.getX() + "," + iv_car.getY();
                tt_location.setText(str_location);
            }

            System.out.println("当前坐标: " + str_location);
            System.out.println("*********************" );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_car_navigation);
        init();
    }

    private void init() {
        bt_forward = (Button) findViewById(R.id.bt_forward);
        bt_backward = (Button) findViewById(R.id.bt_backward);
        bt_left = (Button) findViewById(R.id.bt_left);
        bt_right = (Button) findViewById(R.id.bt_right);
        bt_forward.setOnClickListener(this);
        bt_backward.setOnClickListener(this);
        bt_left.setOnClickListener(this);
        bt_right.setOnClickListener(this);
        addImageView();
    }

    private void addImageView() {
        RelativeLayout layout_car = (RelativeLayout) findViewById(R.id.layout_addImageView);
        iv_car = new ImageView(CarNavigationActivity.this);
        iv_car.setImageResource(R.drawable.car);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 300);
        iv_car.setX(current_X);
        iv_car.setY(current_Y);
        iv_car.setLayoutParams(params);
        layout_car.addView(iv_car);
        str_location = iv_car.getX() + "," + iv_car.getY();
        tt_location = (TextView) findViewById(R.id.tt_location);
        tt_location.setText(str_location);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_forward:
                direction = -2;
                break;
            case R.id.bt_backward:
                direction = 2;
                break;
            case R.id.bt_left:
                direction = -1;
                break;
            case R.id.bt_right:
                direction = 1;
                break;
            default:
                break;
        }
        new Thread(new NetThread()).start();
    }

    class NetThread implements Runnable {
        private String server_addr = "192.168.137.1";
        private int server_port = 6667;
        private String data;

        @Override
        public void run() {
            Socket socket_client = null;
            OutputStream os = null;
            InputStream is = null;
            BufferedReader bufferedReader = null;
            try {
                //建立连接
                socket_client = new Socket(server_addr, server_port);

                //要向服务器发送的数据：坐标,移动方向,速度
                data = str_location + "," + direction + "," + speed;
                System.out.println("向服务器发送的数据信息:" + data);

                //向服务器发送数据
                os = socket_client.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                socket_client.shutdownOutput();

                //接收从服务器返回的数据
                is = socket_client.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String str = null;
                while ((str = bufferedReader.readLine()) != null) {
                    sb.append(str);
                }

                //将数据从work线程发送到主线程
                Message msg = handler.obtainMessage();
                msg.obj = sb;
                handler.sendMessage(msg);
                //关闭IO资源

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
}
