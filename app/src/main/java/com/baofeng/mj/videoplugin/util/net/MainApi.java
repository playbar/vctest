package com.baofeng.mj.videoplugin.util.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baofeng.mj.videoplugin.application.UrlConfig;
import com.baofeng.mj.videoplugin.bean.ContentInfo;
import com.baofeng.mj.videoplugin.bean.MainSubContentListBean;
import com.baofeng.mj.videoplugin.bean.ResponseBaseBean;
import com.baofeng.mj.videoplugin.util.MJOkHttpUtil;
import com.baofeng.mj.videoplugin.util.okhttp.RequestCallBack;
import com.baofeng.mj.videoplugin.util.okhttp.ResultCallback;
import java.util.List;

/**
 * Created by muyu on 2016/11/1.
 */
public class MainApi {

    public void mainList(String url, final RequestCallBack requestCallBack) {

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
}
