package com.rqma.socket.medium;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rqma.socket.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by RQMA on 2018/10/14.
 */
public class QQActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_send;
    private TextView tt_receive;
    private EditText et_input;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            StringBuilder str = (StringBuilder) msg.obj;
            tt_receive.setText(str.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq);
        init();
    }

    private void init() {
        bt_send = (Button) findViewById(R.id.bt_send);
        tt_receive = (TextView) findViewById(R.id.tt_receive);
        et_input = (EditText) findViewById(R.id.et_input);
        bt_send.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send: {
                System.out.println("-----------------------");
                NetThread netThread = new NetThread();
                new Thread(netThread).start();
                break;
            }
            default:
                break;
        }
    }

    class NetThread implements Runnable {
        private String server_addr = "192.168.137.1";
        private int server_port = 6668;

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
                os.write(String.valueOf(et_input.getText()).getBytes());
                os.flush();
                socket_client.shutdownOutput();

                //接收从服务器返回的数据
                is = socket_client.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String str ;
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
