package com.yusheng123.unityandroidbridge.screenrecorder;

import static android.content.Context.BIND_AUTO_CREATE;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ScreenRecorderManager {
    private static final String TAG = "ScreenRecorderManager";
    private static volatile ScreenRecorderManager sInstance;
    private final Context mContext;
    private volatile boolean isInit = false;
    private ServiceConnection mServiceConnection;

    private ScreenRecorderManager(Context context){
        mContext = context;
    }

    /**
     * 通过单例模式创建屏幕录制管理器对象，使用Double Checked Locking来确保线程安全
     * @param context 上下文
     * @return 屏幕录制管理器对象
     */
    public static ScreenRecorderManager getInstance(Context context){
        if (context == null){
            Log.w(TAG, "getInstance: context is null");
            return sInstance;
        } else {
            if(sInstance == null){
                synchronized (ScreenRecorderManager.class){
                    if(sInstance == null){
                        sInstance = new ScreenRecorderManager(context.getApplicationContext());
                    }
                }
            }
            return sInstance;
        }
    }

    public void init(Activity activity){
        if(!this.isInit){
            Log.i(TAG, "init():录屏管理器初始化");
            isInit = true;
            // 获取当前屏幕尺寸
            CommonUtil.init(activity);
            // 权限检查：头显可以不用考虑运行时权限
            // PermissionUtil.checkPermission(activity);

        }
    }

    /**
     * 启动录屏服务
     */
    public void startScreenRecordService(){
        Log.d(TAG, "startScreenRecordService: 开始启动录屏服务....");
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                ScreenRecordService.RecordBinder recordBinder = (ScreenRecordService.RecordBinder) service;
                ScreenRecordService screenRecordService = recordBinder.getRecordService();
                ScreenUtil.setScreenService(screenRecordService);
                Log.d(TAG, "onServiceConnected: 服务连接器创建成功....");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        //创建服务意图
        Intent intent = new Intent(mContext,ScreenRecordService.class);
        //绑定录屏服务
        mContext.bindService(intent, mServiceConnection,BIND_AUTO_CREATE);
        Log.d(TAG, "startScreenRecordService: 录屏服务已经启动....");
        ScreenUtil.addRecordListener(recordListener);
    }

    // 创建录屏监听器匿名内部类实例
    private IRecordListener recordListener = new IRecordListener() {
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
            Toast.makeText(mContext,stopTip,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRecording(String timeTip) {
            // 更新计时器计数
            // todo 在非Activity类中操作UI组件可能出现问题！
        }
    };

    /**
     * 在Activity摧毁时，调用该方法；
     * 用于解除服务绑定，防止内存泄漏
     */
    public void unBind(){
        if(mServiceConnection != null){
            mContext.unbindService(mServiceConnection);
            mServiceConnection = null;
        }
    }
}
