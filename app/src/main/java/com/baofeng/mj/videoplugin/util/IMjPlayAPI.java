package com.baofeng.mj.videoplugin.util;

import android.content.Context;

import com.baofeng.mj.videoplugin.application.AppConfig;
import com.baofeng.mj.videoplugin.application.UrlConfig;
import com.baofeng.mj.videoplugin.bean.DetailBean;
import com.baofeng.mj.videoplugin.bean.DetailVideoBean;
import com.baofeng.mj.videoplugin.download.DownloadUtil;
import com.baofeng.mj.videoplugin.util.application.ChannelUtil;
import com.baofeng.mj.videoplugin.util.application.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

/**
 * Created by yushaochen on 2016/4/11.
 */
public class IMjPlayAPI {

    private Context mContext;

    private static IMjPlayAPI mInstance = null;

    private RequestCallBack mRequestAppCallBack;

    private IMjPlayAPI(Context context) {
        mContext = context;
    }

    public static IMjPlayAPI getInstance(Context context) {
        if (mInstance == null) {
            synchronized (IMjPlayAPI.class) {
                if (mInstance == null) {
                    mInstance = new IMjPlayAPI(context);
                }
            }
        }
        return mInstance;
    }


    public void requestDownloadUrl(final RequestCallBack requestCallBack) {

        try {
            //网络请求
            Map<String, String> map = new HashMap();
            map.put("cdn_folder", ChannelUtil.getChannelCode(mContext));
            MJOkHttpUtil.postAsyn(UrlConfig.DOWNLOAD_URL, new MJOkHttpUtil.ResultCallback<String>() {

                @Override
                public void onError(Request request, Exception e) {
                    requestCallBack.isSuccess(false);
                }

                @Override
                public void onResponse(String json, String url) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (null != data) {
                            PreferenceUtil.instance(mContext).setDownloadUrl(data.getString("file_path"));
//                        PreferenceUtil.instance(mContext).setDownloadUrl("http://dlsw.baidu.com/sw-search-sp/soft/9d/14744/ChromeStandalone_50.0.2661.87_Setup.1461306176.exe");
                            PreferenceUtil.instance(mContext).setDownloadMD5(data.getString("file_md5"));
                            //引流下载开关打开 && 在wifi情况下，不点击，直接下载
                            if(AppConfig.AUTO_DOWNLOAD && NetworkUtil.isWIFIConnected(mContext)) {
                                DownloadUtil.getInstance(mContext).onlyDownLoadInstall(false);
                            }
                            requestCallBack.isSuccess(true);
                        } else {
                            requestCallBack.isSuccess(false);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        requestCallBack.isSuccess(false);
                    }

                }
            }, map);
        } catch (Exception e) {
            e.printStackTrace();
            requestCallBack.isSuccess(false);
        }
    }

    public void requestDetail(String url, final DetailRequestCallBack requestCallBack) {

        try {
            //网络请求
            Map<String, String> map = new HashMap();
            MJOkHttpUtil.postAsyn(UrlConfig.SERVICE_BASE_URL + url, new MJOkHttpUtil.ResultCallback<String>() {

                @Override
                public void onError(Request request, Exception e) {
                    requestCallBack.isSuccess(false, null);
                }

                @Override
                public void onResponse(String json, String url) {
                    try {

                        JSONObject jsonObject = new JSONObject(json);

                        if (jsonObject.getString("status").equals("0")) {
                            JSONObject dataJsonObject = jsonObject.getJSONObject("data");
                            DetailBean mDetailBean = new DetailBean();
                            mDetailBean.desc = dataJsonObject.getString("desc");
                            mDetailBean.is_panorama = dataJsonObject.getString("is_panorama");
                            mDetailBean.video_dimension = dataJsonObject.getString("video_dimension");
                            mDetailBean.score = dataJsonObject.getString("score");
                            mDetailBean.source = dataJsonObject.getString("source");
                            mDetailBean.title = dataJsonObject.getString("title");
                            JSONArray array = dataJsonObject.getJSONArray("video_attrs");
                            if (array != null && array.length() != 0) {
                                DetailVideoBean bean = new DetailVideoBean();
                                bean.play_url = array.getJSONObject(0).getString("play_url");
                                bean.size = array.getJSONObject(0).getString("size");
                                mDetailBean.video_attrs = bean;
                            }
                            requestCallBack.isSuccess(true, mDetailBean);


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        requestCallBack.isSuccess(false, null);
                    }

                }
            }, map);
        } catch (Exception e) {
            e.printStackTrace();
            requestCallBack.isSuccess(false, null);
        }
    }


//    private void setAppStatus(boolean status) {
//        if(null != mRequestAppCallBack) {
//            mRequestAppCallBack.isSuccess(null);
//        }
//    }


    public interface RequestCallBack {
        void isSuccess(boolean status);

    }

    public interface DetailRequestCallBack {
        void isSuccess(boolean status, DetailBean bean);
    }

}
