package com.ximmerse.screenrecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ximmerse.screenrecorder.utils.CommonUtil;
import com.ximmerse.screenrecorder.utils.PermissionUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private TextView mTvStart;
    private TextView mTvEnd;
    private TextView mTvTime;
    private ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化方法，拿到当前设备屏幕参数
        CommonUtil.init(this);
        setContentView(R.layout.screen_record_layout);
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
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onCreate: 屏幕宽度:"+CommonUtil.getmScreenWidth()+"屏幕高度"+CommonUtil.getmScreenHeight());
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        int resourceId = view.getId();
        if(resourceId == R.id.tv_start){
            //todo
            Toast.makeText(this,"开始录屏",Toast.LENGTH_SHORT).show();
        } else if (resourceId == R.id.tv_end) {
            // todo
            Toast.makeText(this,"停止录屏",Toast.LENGTH_SHORT).show();
        }
    }
}