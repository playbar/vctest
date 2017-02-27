package com.baofeng.mj.videoplugin.util;

import android.content.Context;
import android.view.OrientationEventListener;


/**
 * Created by panxin on 2016/11/30.
 */
public class SreenOrientationListener extends OrientationEventListener {
    private boolean isHomeAtLeft = false;

    private Context mContext;

    public SreenOrientationListener(Context context) {
        super(context);
        this.mContext = context;
    }

    public boolean getIsHomeAtLeft(){
        return isHomeAtLeft;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        //System.out.println("!!!!!!!!!!!--------------orientation:"+orientation);
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return; // 手机平放时，检测不到有效的角度
        }
//        if (!isHomeAtLeft) {
//            if (orientation > 80 && orientation < 100) {
//                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
//                isHomeAtLeft = true;
//            }
//        }
//
//        if (isHomeAtLeft) {
//            if (orientation > 260 && orientation < 280) {
//                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                isHomeAtLeft = false;
//            }
//        }

        if(null != mCallBack) {
            mCallBack.callBack(orientation);
        }

    }

    public OrientationCallBack mCallBack;

    public void setCallBack(OrientationCallBack callBack) {
        mCallBack = callBack;
    }

    public interface OrientationCallBack {
        void callBack(int orientation);
    }
}