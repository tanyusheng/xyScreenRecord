package com.ximmerse.unityandroidbridge.screenrecorder;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

public class CommonUtil {

    private static int mScreenWidth;
    private static int mScreenHeight;
    private static int mScreenDpi;

    /**
     * 通过当前Activity，获取设备的屏幕的高度宽度DPI
     * @param activity Activity
     */
    public static void init(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mScreenDpi = metrics.densityDpi;
    }

    public static int getmScreenDpi() {
        return mScreenDpi;
    }

    public static int getmScreenWidth() {
        return mScreenWidth;
    }

    public static int getmScreenHeight() {
        return mScreenHeight;
    }
}
