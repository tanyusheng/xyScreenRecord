package com.yusheng123.unityandroidbridge.screenrecorder;

import android.content.Context;
import android.widget.Toast;

/**
 * 定义一个显示toast的工具类，用于便捷显示Toast信息
 */

public class ToastUtil {

    public static Toast mToast;
    public static void show(Context context,String msg){
        if(mToast == null){
            mToast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
