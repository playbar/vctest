package com.baofeng.mj.videoplugin.util.application;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * 获取去掉工具类
 * Created by muyu on 2017/2/22.
 */
public class ChannelUtil {

    /**
     * 获取 meta-data 中 DEVELOPER_CHANNEL_ID的值
     * @return
     */
    public static String getChannelCode(Context context){
        return getChannelCode(context, "DEVELOPER_CHANNEL_ID");
    }

    /**
     * 获取AndroidManifest meta-data中指定key的值
     * @param key
     * @return
     */
    public static String  getChannelCode(Context context, String key) {
        try {
            ApplicationInfo ai = context
                    .getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Object value = ai.metaData.get(key);
            if (value != null) {
                return value.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "000";

    }
}
