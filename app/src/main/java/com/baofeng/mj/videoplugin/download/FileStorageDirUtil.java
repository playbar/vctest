package com.baofeng.mj.videoplugin.download;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.baofeng.mj.videoplugin.application.AppConfig;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FileStorageDirUtil {
    private static String mojingDir;//mojing存储路径
    private static String savePath;//imageloader缓存路径
    /**
     * 重置路径
     */
    public static void resetPath(){
        mojingDir = null;
    }

    /**
     * 获取mojing存储路径
     */
    public static String getMojingDir() {
        if (!TextUtils.isEmpty(mojingDir)) {
            mkdir(mojingDir);//创建mojing存储路径
            return mojingDir;
        }
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            mojingDir = getExternalMojingDir();//外部mojing存储路径
        } else {
            mojingDir = getInternalMojingCacheDir();//内部mojing缓存路径
        }
        mkdir(mojingDir);//创建mojing存储路径
        return mojingDir;
    }

    /**
     * 获取内部mojing缓存路径
     */
    public static String getInternalMojingCacheDir(){
        String dataDirectory = Environment.getDataDirectory().getAbsolutePath();
        StringBuilder sb = new StringBuilder();
        sb.append(dataDirectory).append("/data/com.mj.govr/").append(AppConfig.STORAGE_DIR).append("/");
        String filePath = sb.toString();
        mkdir(filePath);
        return filePath;
    }

    /**
     * 获取外部mojing存储路径
     */
    public static String getExternalMojingDir(){
        String externalStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        StringBuilder sb = new StringBuilder();
        sb.append(externalStorageDir).append("/").append(AppConfig.STORAGE_DIR).append("/");
        String filePath = sb.toString();
        mkdir(filePath);
        return filePath;
    }

    /**
     * 获取外部mojing缓存路径
     */
    public static String getExternalMojingCacheDir(Context context) {
        String externalStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        return getExternalMojingCacheDir(context, externalStorageDir);
    }

    /**
     * 获取外部mojing缓存路径
     */
    public static String getExternalMojingCacheDir(Context context, String cacheDir) {
        String packageName = context.getPackageName();//包名
        StringBuilder sb = new StringBuilder();
        sb.append(cacheDir).append("/Android/data/").append(packageName).append("/")
          .append(AppConfig.STORAGE_DIR).append("/");
        String filePath = sb.toString();
        mkdir(filePath);
        return filePath;
    }


    /**
     * 获取手机内置，外置的存储根路径集合
     */
    public static String[] getAllStorageDir(Context context) {
        try {
            StorageManager mStorageManager = (StorageManager) context.getSystemService(Activity.STORAGE_SERVICE);
            Method mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
            return (String[]) mMethodGetPaths.invoke(mStorageManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 创建根目录
     */
    public static void mkdir(String filePath){
        File dirFile = new File(filePath);
        if(!dirFile.exists()){
            dirFile.mkdirs();//mkdir()不能创建多个目录，所以要用mkdirs()
        }
    }

    public static String getSavePath() {
        String path = "";
        if (!TextUtils.isEmpty(savePath)) {
            return savePath;
        }

        // sdcard
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String sdDir = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
            path = sdDir + "/" +AppConfig.STORAGE_DIR+ "/";

        } else {
            String romString = Environment.getDataDirectory().getAbsolutePath();
            path = romString + "/data/" + AppConfig.STORAGE_DIR + "/";
        }

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return savePath = path;
    }

}
