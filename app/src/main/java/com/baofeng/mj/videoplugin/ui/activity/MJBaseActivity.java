package com.baofeng.mj.videoplugin.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.baofeng.mj.videoplugin.application.AppConfig;
import com.baofeng.mj.videoplugin.download.DownloadUtil;
import com.baofeng.mj.videoplugin.interfaces.INetReceiverListener;
import com.baofeng.mj.videoplugin.interfaces.ISDPermissionListener;
import com.baofeng.mj.videoplugin.interfaces.IShowNetDialogListener;
import com.baofeng.mj.videoplugin.receiver.NetReceiver;
import com.baofeng.mj.videoplugin.ui.dialog.NetworkDialog;
import com.baofeng.mj.videoplugin.util.PreferenceUtil;
import com.baofeng.mj.videoplugin.util.application.NetworkUtil;
import com.baofeng.mojing.MojingSDKReport;
import com.baofeng.mojing.sdk.download.entity.NativeCallbackInfo;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;

/**
 * Created by panxin on 2016/11/1.
 */
public abstract class MJBaseActivity extends Activity{
    private NetworkDialog mNetworkDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
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
        if(NetworkUtil.networkEnable(this)){
            if(NetworkUtil.isWIFIConnected(this)){
                if(PreferenceUtil.instance(this).getNetDownloadStatus() == AppConfig.TYPE_NET_DOWNLOAD_WAIT_WIFI){
                    if(!PreferenceUtil.instance(this).getClickPause()){
                        DownloadUtil.getInstance(this).startDownload();
                    }
                }
            } else {
                if(PreferenceUtil.instance(this).getNetDownloadStatus() == AppConfig.TYPE_NET_DOWNLOAD_DEFAULT
                        ||PreferenceUtil.instance(this).getNetDownloadStatus() == AppConfig.TYPE_NET_DOWNLOAD_WAIT_WIFI){
                    String url = PreferenceUtil.instance(this).getDownloadUrl();
                    NativeCallbackInfo info = DownloadUtil.getInstance(this).getNativeCallbackInfo(url);
                    if(info!=null){
                        if(info.getStatus() == MjDownloadStatus.DOWNLOADING||info.getStatus() == MjDownloadStatus.CONNECTING){
                            DownloadUtil.getInstance(this).pauseDownload(info.getJobID());
                            if(PreferenceUtil.instance(this).getNetDownloadStatus() == AppConfig.TYPE_NET_DOWNLOAD_DEFAULT) {
                                if(DownloadUtil.getInstance(this).mIShowNetDialogListener != null){
                                    DownloadUtil.getInstance(this).mIShowNetDialogListener.showDialog();
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

    public abstract void initView();

    public abstract void initData();

    public abstract void initListener();

    @Override
    protected void onResume() {
        super.onResume();
        MojingSDKReport.onResume(this);
        DownloadUtil.getInstance(MJBaseActivity.this).onResume();
        setListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MojingSDKReport.onPause(this);
        DownloadUtil.getInstance(this).onPause();
    }


    private void setListener(){
        DownloadUtil.getInstance(this).setIShowNetDialogListener(new IShowNetDialogListener() {
            @Override
            public void showDialog() {
                showNetDialog();
            }
        });
        DownloadUtil.getInstance(this).setISDPermissionListener(new ISDPermissionListener() {
            @Override
            public void showSDPermissionDialog() {
                requestSDPermission();
            }
        });
    }

    private final int sdPermission = 0;
    private void requestSDPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    sdPermission);
        }
    }


    public void showNetDialog(){

        if (mNetworkDialog == null) {
            mNetworkDialog = new NetworkDialog(this, new NetworkDialog.DialogCallBack() {

                @Override
                public void downLoad() {
                    PreferenceUtil.instance(MJBaseActivity.this).setNetDownloadStatus(AppConfig.TYPE_NET_DOWNLOAD_MOBILE);
                    DownloadUtil.getInstance(MJBaseActivity.this).startDownload();
                }

                @Override
                public void cancal() {
                    DownloadUtil.getInstance(MJBaseActivity.this).setIsCanClick(true);
                    PreferenceUtil.instance(MJBaseActivity.this).setNetDownloadStatus(AppConfig.TYPE_NET_DOWNLOAD_WAIT_WIFI);
                }

                //                @Override
//                public void waitWifi() {
//                    PreferenceUtil.instance(BaseApplication.INSTANCE).setNetDownloadStatus(AppConfig.TYPE_NET_DOWNLOAD_WAIT_WIFI);
//                }
            });
        }
        if(mNetworkDialog.isShowing()){
            return;
        }
        mNetworkDialog.show();
    }
}
