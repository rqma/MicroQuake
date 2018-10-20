package com.rqma.socket.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rqma.socket.R;
import com.rqma.socket.easy.HelloActivity;
import com.rqma.socket.medium.QQActivity;
import com.rqma.socket.hard.CarNavigationActivity;
/**
 * Created by RQMA on 2018/10/13.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //按钮:初级，中级，高级
    private Button bt_easy, bt_medium, bt_hard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    //初始化布局
    private void init() {
        bt_easy = (Button) findViewById(R.id.bt_easy);
        bt_medium = (Button) findViewById(R.id.bt_medium);
        bt_hard = (Button) findViewById(R.id.bt_hard);
        bt_easy.setOnClickListener(this);
        bt_medium.setOnClickListener(this);
        bt_hard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent  intent;
        switch (v.getId()) {
            case R.id.bt_easy: {
                //跳转到HelloActivity
                intent=new Intent(this,HelloActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.bt_medium: {
                //跳转到QQActivity
                intent=new Intent(this,QQActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.bt_hard: {
                //跳转到CarNavigationActivity
                intent=new Intent(this,CarNavigationActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }
}
