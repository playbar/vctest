package com.baofeng.mj.videoplugin.util.okhttp;

/**
 * Created by muyu on 2016/11/1.
 */
public abstract class RequestCallBack<T> {

    public void onSuccess(T result) {
    }

    public void onFailure(Throwable error, String content) {
    }

//    public void onFinish() {
//    }
//
//    public void onStart() {
//    }
//
//    public void onCache(T result) {
//    }
//
//    public void onProgress(int bytesWritten, int totalSize) {

//    }
}
