package com.baofeng.mj.videoplugin.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.baofeng.mj.videoplugin.application.AppConfig;


/**
 * Description：SharedPreferences的管理类
 *
 */
public class PreferenceUtil {

    private static PreferenceUtil mPreferenceUtil;
    public static SharedPreferences mSharedPreferences = null;
    private static Editor mEditor = null;

    private static final String KEY_DOWNLOAD_SIZE = "key_download_size";
    private static final String KEY_DOWNLOAD_MD5 = "key_download_md5";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";
    private static final String KEY_NET_DOWNLOAD = "key_net_download";
    private static final String KEY_CLICK_PAUSE = "key_click_pause";

    public static PreferenceUtil instance(Context context) {
        if (null == mPreferenceUtil) {
            mPreferenceUtil = new PreferenceUtil();
            mSharedPreferences = context.getSharedPreferences("mj_plugin", context.MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();
        }
        return mPreferenceUtil;
    }

    public static void removeKey(String key) {
        mEditor = mSharedPreferences.edit();
        mEditor.remove(key);
        mEditor.commit();
    }

    public static void removeAll() {
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.commit();
    }

    public static void setString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public static String getString(String key, String faillValue) {
        return mSharedPreferences.getString(key, faillValue);
    }

    public static void setInt(String key, int value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public static int getInt(String key, int failValue) {
        return mSharedPreferences.getInt(key, failValue);
    }

    public static void setLong(String key, long value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public static long getLong(String key, long failValue) {
        return mSharedPreferences.getLong(key, failValue);
    }

    public static void setBoolean(String key, boolean value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public static Boolean getBoolean(String key, boolean failValue) {
        return mSharedPreferences.getBoolean(key, failValue);
    }

    public void setDownloadSize(long size){
        mEditor.putLong(KEY_DOWNLOAD_SIZE, size);
        mEditor.commit();
    }

    public long getDownloadSize(){
        return mSharedPreferences.getLong(KEY_DOWNLOAD_SIZE, 0);
    }

    public void setDownloadMD5(String md5){
        mEditor.putString(KEY_DOWNLOAD_MD5, md5);
        mEditor.commit();
    }

    public String getDownloadMD5(){
        return mSharedPreferences.getString(KEY_DOWNLOAD_MD5, "");
    }

    public void setDownloadUrl(String md5){
        mEditor.putString(KEY_DOWNLOAD_URL, md5);
        mEditor.commit();
    }

    public String getDownloadUrl(){
        return mSharedPreferences.getString(KEY_DOWNLOAD_URL, "");
    }


    /**
     * 设置网络状态是否可以下载，0默认弹出询问，1立即下载，2wifi自动下载
     * @param status
     */
    public void setNetDownloadStatus(int status){
        mEditor.putInt(KEY_NET_DOWNLOAD, status);
        mEditor.commit();
    }

    public int getNetDownloadStatus(){
        return mSharedPreferences.getInt(KEY_NET_DOWNLOAD, AppConfig.TYPE_NET_DOWNLOAD_DEFAULT);
    }


    /**
     * 是否手动点击暂停
     * @param status
     */
    public void setClickPause(boolean status){
        mEditor.putBoolean(KEY_CLICK_PAUSE, status);
        mEditor.commit();
    }

    public boolean getClickPause(){
        return mSharedPreferences.getBoolean(KEY_CLICK_PAUSE, false);
    }
}