package com.rqma.socket.easy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rqma.socket.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by RQMA on 2018/10/13.
 */
public class HelloActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_hello;
    private Button bt_send_hello;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            StringBuilder str = (StringBuilder) msg.obj;
            tv_hello.setText(str.toString());
            System.out.println(tv_hello.getText());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        init();

    }

    //初始化布局
    private void init() {
        tv_hello = (TextView) findViewById(R.id.tt_hello);
        bt_send_hello = (Button) findViewById(R.id.bt_send_hello);
        bt_send_hello.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send_hello: {
                NetThread netThread = new NetThread();
                new Thread(netThread).start();
                break;
            }
            default:
                break;
        }
    }
    //网络请求
    class NetThread implements Runnable {
        private String server_addr = "192.168.137.1";
        private int server_port = 6666;

        @Override
        public void run() {
            Socket socket_client = null;
            OutputStream os = null;
            InputStream is = null;
            BufferedReader bufferedReader = null;
            try {
                //建立连接
                socket_client = new Socket(server_addr, server_port);
                //向服务器发送数据
                os = socket_client.getOutputStream();
                os.write(String.valueOf(tv_hello.getText()).getBytes());
                os.flush();
                socket_client.shutdownOutput();

                //接收从服务器返回的数据
                is = socket_client.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    sb.append(str);
                }
                //将数据从work线程发送到主线程
                Message msg = handler.obtainMessage();
                msg.obj = sb;
                Log.v("msg", "str");
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
