package com.baofeng.mj.videoplugin.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhangxiong on 2016/4/15.
 */
public class ApkUtils {
    /**
     * 是否安装了apk
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isApkInstalled(Context context, String packageName) {
        String targetPackage = packageName;
        PackageManager pm = context.getPackageManager();
        List packages = pm.getInstalledApplications(0);
        Iterator var6 = packages.iterator();

        while(var6.hasNext()) {
            ApplicationInfo packageInfo = (ApplicationInfo)var6.next();
            if(packageInfo.packageName.equals(targetPackage)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isApkFileExist(String appName,String fileDir) {
        String title = appName;
        if(!title.endsWith(".apk")) {
            title = title + ".apk";
        }

        String path = fileDir + File.separator + title;
        File file = new File(path);
        return file.exists();
    }

    public static void installApk(Context context,String filepath) {
        File file = new File(filepath);
        if(file.exists()) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setFlags(268435456);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }

}
