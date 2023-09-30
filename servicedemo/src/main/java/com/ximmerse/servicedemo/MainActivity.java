package com.ximmerse.servicedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final int START = R.id.start;
    public static final int STOP = R.id.stop;
    public static final int BIND = R.id.bind;
    public static final int UNBIND = R.id.unbind;
    private static final String TAG = "MyService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private ServiceConnection conn = new ServiceConnection() {
        // 当客户端正常连接着服务时，执行服务的绑定操作会被调用
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: 连接正常");
            MyService.MyBinder step = (MyService.MyBinder)iBinder;
            int process = step.getProcess();
            Log.d(TAG, "当前进度是:"+process);
        }

        // 当客户端和服务的连接丢失会调用该方法
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    public void operate(View view) {
        int resourceID = view.getId();
        if(resourceID == START){
            // 启动服务
            Intent it1 = new Intent(this,MyService.class);
            startService(it1);
        } else if (resourceID == STOP) {
            // 停止服务
            Intent it2 = new Intent(this, MyService.class);
            stopService(it2);

        } else if (resourceID == BIND) {
            Intent it4 = new Intent(this, MyService.class);
            bindService(it4,conn,BIND_AUTO_CREATE);
        } else if (resourceID == UNBIND) {
            unbindService(conn);
        }
    }
}