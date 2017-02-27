package com.baofeng.mj.videoplugin.application;

import android.app.Application;
import android.widget.Toast;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.download.DownloadUtil;
import com.baofeng.mj.videoplugin.interfaces.INetReceiverListener;
import com.baofeng.mj.videoplugin.receiver.NetReceiver;
import com.baofeng.mj.videoplugin.util.PreferenceUtil;
import com.baofeng.mj.videoplugin.util.application.NetworkUtil;
import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.sdk.download.entity.NativeCallbackInfo;
import com.baofeng.mojing.sdk.download.utils.MjDownloadSDK;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;

/**
 * Created by panxin on 2016/11/4.
 */
public class BaseApplication extends Application{

    public static BaseApplication INSTANCE;

    private long memoryMaxSize = 256 * 1024 * 1024;
    private long fileMaxSize = 1024 * 1024 * 1024;
    private long singleMemoryMaxSize = 50 * 1024 * 1024;
    private long singleFileMaxSize = 500 * 1024 * 1024;
    private int maxTask = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        initLib();
        registerReceiver();
    }

    private void initLib(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MojingSDK.Init(INSTANCE);
            }
        }).start();
//        MojingSDKReport.openActivityDurationTrack(false);
        /**
         * 初始化下载sdk
         */
        MjDownloadSDK.init(INSTANCE, memoryMaxSize,
                fileMaxSize,
                singleMemoryMaxSize,
                singleFileMaxSize,
                maxTask,
                getCacheDir().getPath());
    }

    private NetReceiver mNetReceiver;
    private void registerReceiver() {
        if(null == mNetReceiver) {
            mNetReceiver = new NetReceiver(this);
            mNetReceiver.setINetReceiverListener(new INetReceiverListener() {
                @Override
                public void receiver() {
                    checkNetConnect();
                }
            });
            mNetReceiver.registerReceiver();
        }
    }

    private void unregisterReceiver() {
        if (null != mNetReceiver) {
            mNetReceiver.unregisterReceiver();
            mNetReceiver = null;
        }
    }

    private void checkNetConnect(){
        if(NetworkUtil.networkEnable(BaseApplication.INSTANCE)){
            if(NetworkUtil.isWIFIConnected(BaseApplication.INSTANCE)){
                if(PreferenceUtil.instance(BaseApplication.INSTANCE).getNetDownloadStatus() == AppConfig.TYPE_NET_DOWNLOAD_WAIT_WIFI){
                    if(!PreferenceUtil.instance(BaseApplication.INSTANCE).getClickPause()){
                        DownloadUtil.getInstance().startDownload();
                    }
                }
            } else {
                if(PreferenceUtil.instance(BaseApplication.INSTANCE).getNetDownloadStatus() == AppConfig.TYPE_NET_DOWNLOAD_DEFAULT
                        ||PreferenceUtil.instance(BaseApplication.INSTANCE).getNetDownloadStatus() == AppConfig.TYPE_NET_DOWNLOAD_WAIT_WIFI){
                    String url = PreferenceUtil.instance(BaseApplication.INSTANCE).getDownloadUrl();
                    NativeCallbackInfo info = DownloadUtil.getInstance().getNativeCallbackInfo(url);
                    if(info!=null){
                        if(info.getStatus() == MjDownloadStatus.DOWNLOADING||info.getStatus() == MjDownloadStatus.CONNECTING){
                            DownloadUtil.getInstance().pauseDownload(info.getJobID());
                            if(PreferenceUtil.instance(BaseApplication.INSTANCE).getNetDownloadStatus() == AppConfig.TYPE_NET_DOWNLOAD_DEFAULT) {
                               if(DownloadUtil.getInstance().mIShowNetDialogListener != null){
                                   DownloadUtil.getInstance().mIShowNetDialogListener.showDialog();
                               }
                            }
                        }
                    }


                }
            }
        }else{
//            Toast.makeText(this,getResources().getString(R.string.mj_string_network_not_connection),Toast.LENGTH_SHORT).show();
        }

    }
}
