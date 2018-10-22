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
import com.rqma.socket.entity.Server_Addr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.Socket;


/**
 * Created by RQMA on 2018/10/15.
 */
public class CarNavigationActivity extends Activity implements OnClickListener {
    //按钮前进,后退,左转,右转
    private Button bt_forward, bt_backward, bt_left, bt_right;
    //小车
    private ImageView iv_car;
    //显示坐标信息
    private TextView tt_location;
    //坐标信息字符串
    private String str_location;
    //返回的结果值
    private int result;
    //当前坐标
    private float current_X = 300;
    private float current_Y = 300;
    //小车移动速度
    private float speed = 10;
    private int direction = 0;
    //四个常量，表示前进,后退,左转,右转
    private final int DIRECTION_LEFT = -1;
    private final int DIRECTION_RIGHT = 1;
    private final int DIRECTION_FORWARD = -2;
    private final int DIRECTION_BACKWARD = 2;

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
                    case DIRECTION_LEFT: {
                        iv_car.setX(current_X - speed);
                        current_X = iv_car.getX();
                        break;
                    }
                    case DIRECTION_RIGHT: {
                        iv_car.setX(current_X + speed);
                        current_X = iv_car.getX();
                        break;
                    }
                    case DIRECTION_FORWARD: {

                        iv_car.setY(current_Y - speed);
                        current_Y = iv_car.getY();
                        break;
                    }
                    case DIRECTION_BACKWARD: {
                        iv_car.setY(current_Y + speed);
                        current_Y = iv_car.getY();
                        break;
                    }
                }
                str_location = iv_car.getX() + "," + iv_car.getY();
                tt_location.setText(str_location);
            }

            System.out.println("当前坐标: " + str_location);
            System.out.println("*********************");
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
                direction = DIRECTION_FORWARD;
                break;
            case R.id.bt_backward:
                direction = DIRECTION_BACKWARD;
                break;
            case R.id.bt_left:
                direction = DIRECTION_LEFT;
                break;
            case R.id.bt_right:
                direction = DIRECTION_RIGHT;
                break;
            default:
                break;
        }
        new Thread(new NetThread()).start();
    }

    class NetThread implements Runnable {
        private String server_ip = Server_Addr.IP;
        private int server_port = Server_Addr.PORT_CARNAVIGATION;

        private String data;

        @Override
        public void run() {
            Socket socket_client = null;
            OutputStream os = null;
            InputStream is = null;
            BufferedReader bufferedReader = null;
            try {
                //建立连接
                socket_client = new Socket(server_ip, server_port);

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
