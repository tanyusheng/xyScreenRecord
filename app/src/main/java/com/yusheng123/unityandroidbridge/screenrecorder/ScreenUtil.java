package com.yusheng123.unityandroidbridge.screenrecorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

public class ScreenUtil {
    private static final String TAG = "ScreenUtil";
    private static ScreenRecordService s_ScreenRecordService;

    private static List<IRecordListener> s_RecordListener = new ArrayList<>();



    public static void setScreenService(ScreenRecordService screenService){
        s_ScreenRecordService = screenService;
    }



    /**
     * 开始屏幕录制
     * @param activity 当前Activity
     * @param requestCode 请求码
     */
    public static void startScreenRecord(Activity activity,int requestCode){
        if(s_ScreenRecordService != null && !s_ScreenRecordService.ismIsRunning()){
            if(!s_ScreenRecordService.isReady()){
                Log.d(TAG, "startScreenRecord: 屏幕录制服务未准备好....");
                // MediaPeojectionManager要求Android api 21+才可以用
                MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                if(mediaProjectionManager != null){
                    Intent intent = mediaProjectionManager.createScreenCaptureIntent();
                    PackageManager packageManager = activity.getPackageManager();
                    if(packageManager.resolveActivity(intent,PackageManager.MATCH_DEFAULT_ONLY) != null){
                        //存在录屏授权的Activity
                        activity.startActivityForResult(intent,requestCode);
                        Log.d(TAG, "startScreenRecord: 开启录制跳转...");
                    }else {
                        Log.d(TAG, "startScreenRecord: 暂时无法录制");
                    }
                }
            }else {
                s_ScreenRecordService.startRecord();
                Log.d(TAG, "startScreenRecord: 屏幕录制服务准备完毕");
            }
        }else {
            Log.d(TAG, "startScreenRecord: 屏幕录制服务未添加");
        }
    }




    public static void addRecordListener(IRecordListener listener){
        //如果监听器对象不为空，且录制监听器集合中没有这个监听器，那么就添加到录制监听器集合中
        if(listener != null && !s_RecordListener.contains(listener)){
            s_RecordListener.add(listener);
        }
    }

    public static void startRecord(){
        if(s_RecordListener.size() > 0){
            for(IRecordListener listener : s_RecordListener){
                listener.onStartRecord();
            }
        }
    }

    public static void stopRecord(String stopTip) {
        if (s_RecordListener.size() > 0){
            for (IRecordListener listener : s_RecordListener){
                listener.onStopRecord(stopTip);
            }
        }
    }

    public static void stopScreenRecord(Context context) {
        if(s_ScreenRecordService != null && s_ScreenRecordService.ismIsRunning()){
            //String str = context.getString(R.string.stop_recording);
            String str = "结束录制";
            s_ScreenRecordService.stopRecord(str);
        }
    }

    /**
     * 用户运行录屏后，设置必要数据
     */
    public static void setUpData(int resultCode, Intent resultData) throws Exception{
        if(s_ScreenRecordService != null && !s_ScreenRecordService.ismIsRunning()){
            s_ScreenRecordService.setResultData(resultCode,resultData);
            //todo
            s_ScreenRecordService.startRecord();
        }
    }

    public static void onRecording(String timeTip) {
        if(s_RecordListener.size() > 0){
            for (IRecordListener listener : s_RecordListener){
                listener.onRecording(timeTip);
            }
        }
    }


}
