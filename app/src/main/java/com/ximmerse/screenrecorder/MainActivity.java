package com.ximmerse.screenrecorder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ximmerse.screenrecorder.service.ScreenRecordService;
import com.ximmerse.screenrecorder.service.ScreenUtil;
import com.ximmerse.screenrecorder.utils.CommonUtil;
import com.ximmerse.screenrecorder.utils.PermissionUtil;
import com.ximmerse.screenrecorder.utils.ToastUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private TextView mTvStart;
    private TextView mTvEnd;
    private TextView mTvTime;
    private ServiceConnection mServiceConnection;

    private int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 显示布局
        setContentView(R.layout.screen_record_layout);
        // 初始化方法，拿到当前设备屏幕参数
        CommonUtil.init(this);
        // 权限检查
        PermissionUtil.checkPermission(this);
        
        mTvStart = findViewById(R.id.tv_start);
        mTvStart.setOnClickListener(this);

        mTvEnd = findViewById(R.id.tv_end);
        mTvEnd.setOnClickListener(this);

        mTvTime = findViewById(R.id.tv_record_time);

        startScreenRecordService();
    }

    /**
     * 启动录屏服务
     */
    private void startScreenRecordService(){
        Log.d(TAG, "startScreenRecordService: 开始启动录屏服务....");
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                ScreenRecordService.RecordBinder recordBinder = (ScreenRecordService.RecordBinder) service;
                ScreenRecordService screenRecordService = recordBinder.getRecordService();
                ScreenUtil.setScreenService(screenRecordService);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        //创建服务意图
        Intent intent = new Intent(this,ScreenRecordService.class);
        bindService(intent,mServiceConnection,BIND_AUTO_CREATE);
        Log.d(TAG, "startScreenRecordService: 录屏服务已经启动....");
        ScreenUtil.addRecordListener(recordListener);
    }

    /**
     * 创建一个屏幕录制接口对象
     *
     */
    private ScreenUtil.RecordListener recordListener = new ScreenUtil.RecordListener() {
        @Override
        public void onStartRecord() {

        }

        @Override
        public void onPauseRecord() {

        }

        @Override
        public void onResumeRecord() {

        }

        @Override
        public void onStopRecord(String stopTip) {
            ToastUtil.show(MainActivity.this,stopTip);
        }

        @Override
        public void onRecording(String timeTip) {
            mTvTime.setText(timeTip);
        }
    };

    @Override
    protected void onResume() {
        Log.d(TAG, "onCreate: 屏幕宽度:"+CommonUtil.getmScreenWidth()+"屏幕高度"+CommonUtil.getmScreenHeight());
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            ScreenUtil.setUpData(requestCode,data);
        }else {
            ToastUtil.show(this,"拒绝录屏");
        }
    }

    @Override
    public void onClick(View view) {
        int resourceId = view.getId();
        if(resourceId == R.id.tv_start){
            // 实现textview的点击功能
            ToastUtil.show(this,"开始录屏");
            Log.d(TAG, "onClick: 系统开始录屏");
            // 启动屏幕录制
            ScreenUtil.startScreenRecord(this,REQUEST_CODE);

        } else if (resourceId == R.id.tv_end) {
            // 点击功能
            ToastUtil.show(this,"停止录屏");
            Log.d(TAG, "onClick: 系统结束录屏");
            ScreenUtil.stopScreenRecord(this);
        }
    }
}