package com.ximmerse.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private static final String TAG = "MyService";
    private int i;

    public MyService() {
    }

    // 创建服务
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: 服务被创建");
        // 开启一个线程，从1数到100，模拟耗时的任务
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    for (i = 0; i < 100; i++) {
                        sleep(1000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // 启动
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: 服务被启动");
        return super.onStartCommand(intent, flags, startId);
    }

    // 绑定
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: 服务被绑定");
        // TODO: Return the communication channel to the service.
        return new MyBinder();
    }

    class MyBinder extends Binder{
        // 定义自己需要的方法（实现进度监控）
        public int getProcess(){
            return i;
        }
    }

    // 解绑
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: 服务解绑");
        return super.onUnbind(intent);
    }


    // 摧毁
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: 服务被摧毁");
        super.onDestroy();
    }
}