package com.baofeng.mj.videoplugin.receiver;

import android.content.Context;
import android.content.IntentFilter;

/**
 * Created by yushaochen on 2017/2/23.
 */

public class ReceiverManager {

    private Context mContext;
    private static ReceiverManager instance;

    private ReceiverManager(Context context) {
        mContext = context;
    }

    public static ReceiverManager getInstance(Context context) {
        if (instance == null) {
            instance = new ReceiverManager(context);
        }
        return instance;
    }

    public void initAllReceiver() {
        //初始化按摩相关BroadcastReceiver
        initMassageReceiver();
    }

    public void clearAllReceiver() {
        clearMassageReceiver();
    }

    private MassageReceiver massageReceiver;

    private void initMassageReceiver() {
        massageReceiver = new MassageReceiver();
        IntentFilter intentFilter = new IntentFilter(MassageReceiver.MASSAGE_ACTION);
        mContext.registerReceiver(massageReceiver, intentFilter);
    }

    private void clearMassageReceiver() {
        if(null != massageReceiver) {
            massageReceiver.clearCallBack();
            mContext.unregisterReceiver(massageReceiver);
        }
    }

    public void setMassageReceiverCallBack(MassageReceiver.ReceiverCallBack receiverCallBack) {
        if(null != massageReceiver) {
            massageReceiver.setCallBack(receiverCallBack);
        }
    }

    public void removeMassageReceiverCallBack(MassageReceiver.ReceiverCallBack receiverCallBack) {
        if(null != massageReceiver) {
            massageReceiver.removeCallBack(receiverCallBack);
        }
    }

}
