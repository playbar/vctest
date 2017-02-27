package com.baofeng.mj.videoplugin.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.baofeng.mj.videoplugin.application.AppConfig;
import com.baofeng.mj.videoplugin.ui.page.ChannelListPage;
import com.baofeng.mj.videoplugin.ui.page.PanoNetPlayPage;
import com.baofeng.mj.videoplugin.util.net.OkHttp;
import com.baofeng.mojing.MojingSDK;
import com.bfmj.viewcore.util.GLExtraData;

public class GLIndexActivity extends GLBaseActivity {

    public static final int CHANNEL_LIST_PAGE = 0;
    public static final int PANO_NET_PLAY_PAGE = 1;

    public static final int INSIDE = 0;
    public static final int EXTERNAL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        init();
//        MojingSDKReport.openActivityDurationTrack(false);
        OkHttp.getInstance().enableGlthread(this);

    }

    /**
     * 初始化mojingSDK和播放核心库
     */
    private void init() {
        if (!MojingSDK.GetInitSDK()) {
            MojingSDK.Init(this.getApplicationContext());
        }

//        GLRelativeView view = new GLRelativeView(this);
//        view.setLayoutParams(2400,2400);
//        view.setX(0);
//        view.setY(0);
//        view.setBackground(new GLColor(0xff0000));
//        getRootView().addView(view);

        AppConfig.modeData = new String[]{"运动恢复","舒展活络","休憩促眠","工作减压","肩颈重点","腰椎舒缓"};

        int toPage = getIntent().getIntExtra("toPage",CHANNEL_LIST_PAGE);//默认开启视频列表页面
        switch (toPage){
            case CHANNEL_LIST_PAGE://视频列表
                GLExtraData channelData = new GLExtraData();
                getPageManager().push(new ChannelListPage(this), channelData);
                break;
            case PANO_NET_PLAY_PAGE://播放页面
                GLExtraData panoData = new GLExtraData();
                panoData.putExtraInt("from",EXTERNAL);
                panoData.putExtraString("data",getIntent().getStringExtra("data"));
                getPageManager().push(new PanoNetPlayPage(this), panoData);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onZKeyUp(int j) {
        // TODO 自动生成的方法存根

        return super.onZKeyUp(j);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

//            return false;
        }

//        /**系统音量按键事件检测*/
//        if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
//            SystemVolumSubscribe.getInstance().notify(keyCode);
//        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
//            SystemVolumSubscribe.getInstance().notify(keyCode);
//        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_MUTE){
//            SystemVolumSubscribe.getInstance().notify(keyCode);
//        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
//        StickUtil.disconnect();
        super.onDestroy();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void finish() {
        super.finish();
        clearOtherMem();

    }

    /**
     * 清除内存
     */
    private void clearOtherMem() {
//        LruCachUtil.destroy();
//        SimpleDiskLruCacheUtil.destroy();
        OkHttp.disableGlThread();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        SystemVolumSubscribe.registerReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

//        SystemVolumSubscribe.unRegisterReceiver(this);
    }
}
