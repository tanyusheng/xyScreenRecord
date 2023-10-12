package com.ximmerse.screenrecorder.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

public class FileUtil {

    private static final String TAG = "FileUtil";

    /**
     * 判断SD卡是否存在并且可用
     * @return SD卡是否存在
     */
    public static boolean isSDExists(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡剩余容量
     * @return SD卡容量单位 Byte
     */
    public static long getSDFreeMemory(){
        try {
            if(isSDExists()){
                File pathFile = Environment.getExternalStorageDirectory();
                // 检索文件系统空间的全部信息
                StatFs statFs = new StatFs(pathFile.getPath());
                // 获取磁盘中每一个block的size
                long blockSize = statFs.getBlockSize();
                long availableBlocks = statFs.getAvailableBlocks();
                long nSDFreeSize = availableBlocks * blockSize;
                return nSDFreeSize;
            }
        } catch (Exception e) {
            Log.d(TAG, "getSDFreeMemory: 无法读取存储空间大小");
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除SD卡的文件和目录
     * @param path 指定路径
     * @return 是否删除成功
     */
    public static boolean deleteSDFile(String path){
        return deleteSDFile(path,false);
    }
    public static boolean deleteSDFile(String path,boolean deleteParent) {
        if(TextUtils.isEmpty(path)){
            return false;
        }
        File file = new File(path);
        if(!file.exists()){
            return true;
        }
        return deleteFile(file,deleteParent);
    }

    private static boolean deleteFile(File file, boolean deleteParent) {
        boolean flag = false;
        if(file == null){
            return flag;
        }
        if(file.isDirectory()){
            File[] files = file.listFiles();
            // todo 可能会报空指针异常
            if(files.length > 0){
                for (int i = 0; i < files.length; i++) {
                    flag = deleteFile(files[i],true);
                    if(!flag){
                        return flag;
                    }
                }
            }
            if(deleteParent){
                flag = file.delete();
            }
        }else {
            flag = file.delete();
        }
        file = null;
        return flag;
    }

    /**
     * 添加到媒体数据库
     * @param context 上下文
     * @param videoPath 视频文件路径
     * @param videoWidth 视频文件宽度
     * @param videoHeight 视频文件高度
     * @param videoTime 视频文件创建时间
     * @return 是否更新视频文件到媒体数据库
     */
    public static Uri fileScanVideo(Context context, String videoPath, int videoWidth, int videoHeight, int videoTime) {
        File file = new File(videoPath);
        if(file.exists()){
            Uri uri = null;
            long size = file.length();
            String fileName = file.getName();
            long dateTaken = System.currentTimeMillis();
            ContentValues values = new ContentValues(11);
            // 路径
            values.put(MediaStore.Video.Media.DATA,videoPath);
            // 标题
            values.put(MediaStore.Video.Media.TITLE,fileName);
            // 时长
            values.put(MediaStore.Video.Media.DURATION,videoTime * 1000);
            // 视频文件显示宽度
            values.put(MediaStore.Video.Media.WIDTH,videoWidth);
            // 视频文件显示高度
            values.put(MediaStore.Video.Media.HEIGHT,videoHeight);
            // 视频文件大小
            values.put(MediaStore.Video.Media.SIZE,size);
            // 视频插入时间
            values.put(MediaStore.Video.Media.DATE_TAKEN,dateTaken);
            // 文件名
            values.put(MediaStore.Video.Media.DISPLAY_NAME,fileName);
            // 视频修改时间
            values.put(MediaStore.Video.Media.DATE_MODIFIED,dateTaken / 1000);
            // 视频添加时间
            values.put(MediaStore.Video.Media.DATE_ADDED,dateTaken / 1000);
            // 文件类型
            values.put(MediaStore.Video.Media.MIME_TYPE,"video/mp4");

            ContentResolver resolver = context.getContentResolver();

            if(resolver != null){
                try {
                    uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                } catch (Exception e) {
                    e.printStackTrace();
                    uri = null;
                }
            }

            if(uri == null){
                MediaScannerConnection.scanFile(context, new String[]{videoPath}, new String[]{"video/*"},
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String s, Uri uri) {

                            }
                        });
            }
            return uri;
        }
        return null;
    }
}
