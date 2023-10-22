package com.yusheng123.unityandroidbridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yusheng123.screenrecorder.R;
import com.yusheng123.unityandroidbridge.screenrecorder.CommonUtil;
import com.yusheng123.unityandroidbridge.screenrecorder.ScreenRecorderManager;
import com.yusheng123.unityandroidbridge.screenrecorder.ScreenUtil;


public class ScreenRecorderManagerActivity extends Activity {
    private static final String TAG = "ScreenRecorderManagerAc";

    private ScreenRecorderManager screenRecorderManager;
    private TextView mTvStart;

    private int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_record_layout);

        //获取录屏管理器对象
        screenRecorderManager = ScreenRecorderManager.getInstance(this);
        screenRecorderManager.init(this);

        // 启动录屏服务
        screenRecorderManager.startScreenRecordService();

        // 获取控件对象
        mTvStart = findViewById(R.id.tv_start);
        mTvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 启动录屏
                ScreenUtil.startScreenRecord(ScreenRecorderManagerActivity.this,REQUEST_CODE);
                Log.d(TAG, "onClick: 触发了开始录屏按钮");
            }
        });

        TextView mTvEnd = findViewById(R.id.tv_end);
        mTvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 停止录屏
                ScreenUtil.stopScreenRecord(ScreenRecorderManagerActivity.this);
                Log.d(TAG, "onClick: 触发了结束录屏按钮");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onCreate: 屏幕宽度:"+ CommonUtil.getmScreenWidth()+"，屏幕高度"+CommonUtil.getmScreenHeight());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            try {
                ScreenUtil.setUpData(resultCode,data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            // TODO 权限管理待优化
            Log.d(TAG, "onActivityResult: 用户拒绝录屏");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放screenRecorder对象
        screenRecorderManager.unBind();
    }
}
