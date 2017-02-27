package com.baofeng.mj.videoplugin.application;

/**
 * Created by muyu on 2016/11/1.
 */
public class UrlConfig {

    //请求列表数据接口
    public static String SERVICE_BASE_URL = "http://res.static.mojing.cn/160630-1-1-1/android/zh/";
    public static String SERVICE_URL_HEAD= SERVICE_BASE_URL+"1/block/blockinfo/454967";
    public static String SERVICE_URL_TAIL = ".js";

    //下载mojing app接口
//    public static String DOWNLOAD_URL = "http://fuwu.mojing.cn/api/cdnres/getlastcdnres?cdn_folder=mj_bfyy";
    public static String DOWNLOAD_URL = "http://fuwu.mojing.cn/api/cdnres/getlastcdnres";
    //列表分页请求
    public static String getListUrl(int pageNum, int dataNum) {
        return SERVICE_URL_HEAD + "-" + "start" + pageNum + "-" + "num" + dataNum + SERVICE_URL_TAIL;
    }
}
