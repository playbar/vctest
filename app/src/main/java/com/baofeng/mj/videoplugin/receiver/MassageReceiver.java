package com.baofeng.mj.videoplugin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by yushaochen on 2017/2/23.
 */

public class MassageReceiver extends BroadcastReceiver{

    public static final String MASSAGE_ACTION = "com.baofeng.mj.videoplugin.action.MASSAGE_APP_TO_PLUGIN";

    private ArrayList<ReceiverCallBack> callbacklist;

    public MassageReceiver(){
        callbacklist = new ArrayList();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(MASSAGE_ACTION.equals(intent.getAction())) {
            int connectStatus = intent.getIntExtra("connectStatus", 0);
            int modeIndex = intent.getIntExtra("modeIndex", -1);
            if(null != callbacklist && callbacklist.size() > 0) {
                for(ReceiverCallBack receiver : callbacklist) {
                    receiver.onCallBack(connectStatus, modeIndex);
                }
            }
        }
    }

    public void setCallBack(ReceiverCallBack receiverCallBack) {
        if(null != receiverCallBack) {
            callbacklist.add(receiverCallBack);
        }
    }

    public void removeCallBack(ReceiverCallBack receiverCallBack) {
        if(null != receiverCallBack) {
            callbacklist.remove(receiverCallBack);
        }
    }

    public void clearCallBack() {
        if(null != callbacklist && callbacklist.size() > 0) {
            callbacklist.clear();
        }
    }

    public interface ReceiverCallBack {
        void onCallBack(int connectStatus, int modeIndex);
    }
}
