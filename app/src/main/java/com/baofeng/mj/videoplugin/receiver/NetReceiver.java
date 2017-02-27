package com.baofeng.mj.videoplugin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.baofeng.mj.videoplugin.interfaces.INetReceiverListener;


/**
 * Created by panxin on 2016/7/19.
 */
public class NetReceiver extends BroadcastReceiver {

    private boolean connection = false;
    private Context mContext;
    public NetReceiver(Context mContext){
        this.mContext = mContext;
        connection = false;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!connection) {//第一次网络状态不响应，因为第一次是注册BroadcastReceiver时调起的
            connection = true;
            return;
        }
        if(mINetReceiverListener!=null){
            mINetReceiverListener.receiver();
        }
    }

    public void registerReceiver() {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mContext.registerReceiver(this, filter);
    }

    public void unregisterReceiver() {
            mContext.unregisterReceiver(this);
    }

    private INetReceiverListener mINetReceiverListener;
    public void setINetReceiverListener(INetReceiverListener listener){
        this.mINetReceiverListener = listener;
    }
}
