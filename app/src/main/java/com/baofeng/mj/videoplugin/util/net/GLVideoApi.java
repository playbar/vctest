package com.baofeng.mj.videoplugin.util.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.videoplugin.application.UrlConfig;
import com.baofeng.mj.videoplugin.bean.ContentInfo;
import com.baofeng.mj.videoplugin.bean.GLDetailBean;
import com.baofeng.mj.videoplugin.bean.MainSubContentListBean;
import com.baofeng.mj.videoplugin.bean.ResponseBaseBean;
import com.baofeng.mj.videoplugin.util.okhttp.RequestCallBack;
import com.baofeng.mj.videoplugin.util.okhttp.ResultCallback;

import java.util.List;

/**
 * Created by yushaochen on 2017/2/16.
 */
public class GLVideoApi {

    public void getMiddleList(String url, final RequestCallBack requestCallBack) {

        OkHttp.getAsyn(url, new ResultCallback<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>(requestCallBack) {

            @Override
            public ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> parseResponse(String responseString) {
                ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> bean = JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
                        });
                return bean;
            }
        });
    }

    public void getVideoDetail(String url, final RequestCallBack requestCallBack) {

        OkHttp.getAsyn(UrlConfig.SERVICE_BASE_URL + url, new ResultCallback<ResponseBaseBean<GLDetailBean>>(requestCallBack) {

            @Override
            public ResponseBaseBean<GLDetailBean> parseResponse(String responseString) {
                ResponseBaseBean<GLDetailBean> bean = JSON.parseObject(
                        responseString, new TypeReference<ResponseBaseBean<GLDetailBean>>() {
                        });
                return bean;
            }
        });
    }
}
