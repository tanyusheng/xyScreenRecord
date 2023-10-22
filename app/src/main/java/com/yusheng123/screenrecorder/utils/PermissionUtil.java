package com.yusheng123.screenrecorder.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;



public class PermissionUtil {
    private static final String TAG = "PermissionUtil";
    public static void checkPermission(AppCompatActivity activity){
        // Android 6.0之后需要运行时权限
        if(Build.VERSION.SDK_INT >= 23){
            int checkPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                    + ContextCompat.checkSelfPermission(activity,Manifest.permission.READ_PHONE_STATE)
                    + ContextCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    + ContextCompat.checkSelfPermission(activity,Manifest.permission.READ_EXTERNAL_STORAGE);
            if(checkPermission != PackageManager.PERMISSION_GRANTED){
                //动态申请权限
                Log.d(TAG, "checkPermission: 正在申请运行时权限");
                ActivityCompat.requestPermissions(activity,new String[]{
                       Manifest.permission.RECORD_AUDIO,
                       Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },123);
                return;
            }else {
                Log.d(TAG, "checkPermission: 已经获取运行时权限");
                return;
            }
        }
        Log.d(TAG, "checkPermission: 不需要申请运行时权限");
    }
}
