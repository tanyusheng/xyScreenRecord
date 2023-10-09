package com.ximmerse.screenrecorder.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;



import com.ximmerse.screenrecorder.R;
import com.ximmerse.screenrecorder.utils.CommonUtil;
import com.ximmerse.screenrecorder.utils.FileUtil;

import java.io.File;
import java.io.IOException;

public class ScreenRecordService extends Service implements Handler.Callback{
    private static final String TAG = "ScreenRecordService";

    private boolean mIsRunning;

    private MediaProjectionManager mProjectionManager;

    private MediaProjection mMediaProjection;

    private int mResultCode;
    private Intent mResultData;
    private String mRecordFilePath;

    private MediaRecorder mMediaRecorder;

    private int mRecordWidth = CommonUtil.getmScreenWidth();
    private int mRecordHeight = CommonUtil.getmScreenHeight();
    private int mRecordDpi = CommonUtil.getmScreenDpi();
    private Handler mHandler;

    private int mRecordSeconds = 0;

    public static final int MSG_TYPE_COUNT_DOWN = 110;
    private VirtualDisplay mVirtualDisplay;



    /**
     * 判断录屏是否正在进行
     * @return
     */
    public boolean ismIsRunning(){
        return mIsRunning;
    }

    /**
     * 判断录屏服务是否准备完毕
     * @return
     */
    public boolean isReady(){
        // todo 媒体投射器与返回数据不为空说明录屏服务已经准备好，显然这里还没有准备好
        return mMediaProjection != null && mResultData != null;
    }

    public void clearRecordElement(){
        clearAll();
        if(mMediaRecorder != null){
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        mResultData = null;
        mIsRunning = false;
    }

    public void clearAll(){
        if(mMediaProjection != null){
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    /**
     * 创建服务
     */
    @Override
    public void onCreate() {
        super.onCreate();

        mIsRunning = false;
        // 创建媒体录制器对象
        mMediaRecorder = new MediaRecorder();
        // 创建handler对象
        mHandler = new Handler(Looper.getMainLooper(),this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 服务被异常中断后，会尝试重新启动服务
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    /**
     * 启动录制功能
     * @return 是否启动成功
     */
    public boolean startRecord() {
        if(mIsRunning){
            return false;
        }
        if(mMediaProjection == null){
            mMediaProjection = mProjectionManager.getMediaProjection(mResultCode,mResultData);
        }
        // 启动媒体录制器
        setUpMediaRecorder();
        // 创建虚拟屏幕
        createVirtualDisplay();
        // 媒体录制器启动
        mMediaRecorder.start();
        // 屏幕工具类开始录制
        ScreenUtil.startRecord();

        // 设置录制有效时间，每隔一秒发送一条Handler消息，用于计时
        mHandler.sendEmptyMessageDelayed(MSG_TYPE_COUNT_DOWN,1000);
        // 更新录制状态
        mIsRunning = true;
        return true;
    }

    /**
     * 停止录制功能
     * @return 是否停止成功
     */
    public boolean stopRecord(String tip){
        // 如果录制状态为停止那么直接返回
        if(!mIsRunning){
            return false;
        }
        // 如果当前录制状态为正常录制中
        mIsRunning = false;

        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder = null;
            mVirtualDisplay.release();
            mMediaProjection.stop();
        } catch (IllegalStateException e) {
            Log.d(TAG, "stopRecord: 无法正常停止录制");
            e.printStackTrace();
            mMediaRecorder.reset();
            mMediaRecorder = null;
        }

        mMediaProjection = null;
        mHandler.removeMessages(MSG_TYPE_COUNT_DOWN);
        ScreenUtil.stopRecord(tip);

        if(mRecordSeconds <= 2){
            //录制实际如果小于2s,删除录制的文件内容
            FileUtil.deleteSDFile(mRecordFilePath);
        }else {
            //通知系统图库更新
            FileUtil.fileScanVideo(this,mRecordFilePath,mRecordWidth,mRecordHeight,mRecordSeconds);
        }
        mRecordSeconds = 0;
        return true;
    }



    /**
     * 获取文件存储目录
     * @return 如果媒体目录挂载则获取绝对路径，否则返回空
     */
    public String getSaveDirectory(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }else {
            return null;
        }
    }

    /**
     * 创建虚拟屏幕
     */
    private void createVirtualDisplay(){
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("MainScreen", mRecordWidth, mRecordHeight, mRecordDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
    }

    /**
     * 设置媒体录制器
     */
    private void setUpMediaRecorder(){
        mRecordFilePath = getSaveDirectory() + File.separator + System.currentTimeMillis() + ".mp4";
        if(mMediaRecorder == null){
            mMediaRecorder = new MediaRecorder();
        }
        // 设置媒体录制器的音频来源于麦克风
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置媒体录制器的视频来源于surface
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        // 设置媒体录制器的输出文件格式为mp4
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 设置输出文件路径
        mMediaRecorder.setOutputFile(mRecordFilePath);
        // 设置输出画面尺寸
        mMediaRecorder.setVideoSize(mRecordWidth,mRecordHeight);
        // 设置视频编码格式
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // 设置音频编码格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 设置视频编码比特率
        mMediaRecorder.setVideoEncodingBitRate((int)(mRecordWidth*mRecordHeight*3.6));
        // 设置视频帧率
        mMediaRecorder.setVideoFrameRate(20);

        // ---- 使用canvas绘制一个界面并通过getSurface录制下来；
        //mMediaRecorder.getSurface();

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "setUpMediaRecorder: 媒体录制器启动异常");
            throw new RuntimeException(e);
        }

    }

    // todo 处理消息待完善
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case MSG_TYPE_COUNT_DOWN: {
                String str = null;
                boolean enough = FileUtil.getSDFreeMemory() / (1024 * 1024) < 4;
                if (enough) {
                    // 系统存储空间小于4M，停止录屏
                    str = getString(R.string.record_space_tip);
                    stopRecord(str);
                    mRecordSeconds = 0;
                    break;
                }
                mRecordSeconds++;
                int minute = 0;
                int second = 0;
                if (mRecordSeconds >= 60) {
                    minute = mRecordSeconds / 60;
                    second = mRecordSeconds % 60;
                } else {
                    second = mRecordSeconds;
                }
                ScreenUtil.onRecording("0" + minute + ":" + (second < 10 ? "0" + second : second + ""));

                if (mRecordSeconds < 3 * 60) {
                    mHandler.sendEmptyMessageDelayed(MSG_TYPE_COUNT_DOWN, 1000);
                } else if (mRecordSeconds == 3 * 60) {
                    str = getString(R.string.record_time_end_tip);
                    stopRecord(str);
                    mRecordSeconds = 0;
                }
                break;
            }
        }
        return true;
    }

    public void setResultData(int resultCode, Intent resultData) {
        mResultCode = resultCode;
        mResultData = resultData;

        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if(mProjectionManager != null){
           mMediaProjection = mProjectionManager.getMediaProjection(mResultCode,mResultData);
        }
    }


    /**
     * 创建一个RecordBinder内部类
     */
    public class RecordBinder extends Binder{
        public ScreenRecordService getRecordService(){
            return ScreenRecordService.this;
        }
    }
}
