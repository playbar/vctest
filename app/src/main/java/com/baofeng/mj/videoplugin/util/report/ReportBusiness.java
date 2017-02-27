package com.baofeng.mj.videoplugin.util.report;

import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.baofeng.mj.videoplugin.bean.BaseReportBean;
import com.baofeng.mj.videoplugin.bean.ReportClickBean;
import com.baofeng.mojing.MojingSDKReport;

/**
 * Created by muyu on 2016/11/9.
 */
public class ReportBusiness {
    public static final String UNKNOW = "UNKNOWN";

    private static ReportBusiness instance;
    private boolean isDebug = false;

    private ReportBusiness() {
    }

    public static ReportBusiness getInstance() {
        if (instance == null) {
            instance = new ReportBusiness();
        }
        return instance;
    }

    public void reportClick(BaseReportBean bean) {
        try {
            String json = JSONObject.toJSONString(bean);
//            writeLogToFile(json);
            MojingSDKReport.onEvent(json, UNKNOW, UNKNOW, 0, UNKNOW, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void writeLogToFile(String conent) {
//        if (!isDebug) {
//            return;
//        }
//        if (TextUtils.isEmpty(conent)) {
//            return;
//        }
//        String path = FileUtils.getDownloadDir() + "report.txt";
//        BufferedWriter out = null;
//        try {
//            out = new BufferedWriter(new OutputStreamWriter(
//                    new FileOutputStream(path, true)));
//            out.write(conent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                out.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /***
     * 从url中截取resid
     * @param url
     * @return
     */
    public String getResIdFromUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        int firstIndex = url.lastIndexOf("/");
        int lastIndex = url.lastIndexOf(".");
        if (firstIndex == -1 || lastIndex == -1) {
            return null;
        }
        return url.substring(firstIndex + 1, lastIndex);
    }

    /***
     * 获取点击类型
     * @param apkState apk状态
     */
    public static String getClickType(int apkState) {
//        switch (apkState){
//            case ApkUtil.NEED_DOWNLOAD:
//                return CLICK_TYPE_DOWNLOAD;
//            case ApkUtil.NEED_INSTALL:
//                return CLICK_TYPE_INSTALL;
//            case ApkUtil.CAN_PLAY:
//                return CLICK_TYPE_OPEN;
//            case ApkUtil.NEED_UPDATE:
//                return CLICK_TYPE_UPDATE;
//            default:
                return "";
//        }
    }


}

