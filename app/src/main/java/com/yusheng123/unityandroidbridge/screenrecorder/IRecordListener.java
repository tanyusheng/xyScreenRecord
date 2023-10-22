package com.yusheng123.unityandroidbridge.screenrecorder;

/**
 * 录屏监听器接口
 */
public interface IRecordListener {
    void onStartRecord();
    void onPauseRecord();
    void onResumeRecord();
    void onStopRecord(String stopTip);
    void onRecording(String timeTip);
}
