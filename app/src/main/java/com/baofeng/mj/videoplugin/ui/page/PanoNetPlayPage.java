package com.baofeng.mj.videoplugin.ui.page;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.baofeng.mj.videoplugin.application.AppConfig;
import com.baofeng.mj.videoplugin.bean.GLDetailBean;
import com.baofeng.mj.videoplugin.bean.ResponseBaseBean;
import com.baofeng.mj.videoplugin.interfaces.IPlayerControlCallBack;
import com.baofeng.mj.videoplugin.receiver.MassageReceiver;
import com.baofeng.mj.videoplugin.receiver.ReceiverManager;
import com.baofeng.mj.videoplugin.ui.activity.GLBaseActivity;
import com.baofeng.mj.videoplugin.ui.activity.GLIndexActivity;
import com.baofeng.mj.videoplugin.ui.view.GLErrorTipsView;
import com.baofeng.mj.videoplugin.ui.view.GLMassageView;
import com.baofeng.mj.videoplugin.ui.view.GLPanoPlayerControlView;
import com.baofeng.mj.videoplugin.util.LightnessControl;
import com.baofeng.mj.videoplugin.util.MenuControl;
import com.baofeng.mj.videoplugin.util.application.NetworkUtil;
import com.baofeng.mj.videoplugin.util.net.GLVideoApi;
import com.baofeng.mj.videoplugin.util.okhttp.RequestCallBack;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.interfaces.IGLQiuPlayerListener;
import com.bfmj.viewcore.player.GLQiuPlayer;
import com.bfmj.viewcore.util.GLExtraData;
import com.bfmj.viewcore.view.GLPanoView;
import com.bfmj.viewcore.view.GLPlayerView2;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLViewPage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yushaochen on 2017/2/16.
 */

public class PanoNetPlayPage extends GLViewPage {

    private Context mContext;

    private GLRelativeView mRootView;

    private GLQiuPlayer player;//播放器

    private String detailUrl;

    private int from = 0;

    private GLPanoPlayerControlView controlView;

    private final int playerHeight = 800;
    private final int playerLeft = 500;

    private int defaultLight;

    private boolean mPrepared = false;

    private GLErrorTipsView errorTipsView;

    private NetWorkReceiver netWorkReceiver;

    public PanoNetPlayPage(Context context) {
        super(context);
        mContext = context;

        defaultLight = LightnessControl.GetLightness((GLIndexActivity) mContext);

        ((GLBaseActivity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LightnessControl.SetLightness((GLIndexActivity) mContext,155);
            }
        });

        ((GLBaseActivity) getContext()).hideSkyBox();
//        ((GLBaseActivity) getContext()).hideResetView();


        netWorkReceiver = new NetWorkReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(netWorkReceiver, filter);

        ReceiverManager.getInstance(mContext).setMassageReceiverCallBack(receiver);
    }

    private MassageReceiver.ReceiverCallBack receiver = new MassageReceiver.ReceiverCallBack() {
        @Override
        public void onCallBack(int connectStatus, int modeIndex) {
            AppConfig.connectStatus = connectStatus;
            AppConfig.modeIndex = modeIndex;
//            System.out.println("!!!!!!!!!!!!!!-----------PanoNetPlayPage----connectStatus:"+connectStatus);
//            System.out.println("!!!!!!!!!!!!!!-----------PanoNetPlayPage----modeIndex:"+modeIndex);
            if(null != controlView) {
                if(connectStatus == 1) {
                    controlView.showMassageView(true);
                    controlView.setMassageViewSelected(modeIndex+"");
                } else {
                    controlView.showMassageView(false);
                    controlView.setMassageViewSelected(GLMassageView.NO_SELECTED);
                }
            }
        }
    };

    @Override
    protected GLRectView createView(GLExtraData data) {
        finishFlag = false;
        detailUrl = (String) data.getExtraObject("data");

        from = data.getExtraInt("from",0);

        mRootView = new GLRelativeView(mContext);
        mRootView.setLayoutParams(2400,960);
        mRootView.setX(0);
        mRootView.setY(720);

        //创建播放器view
        player = new GLQiuPlayer(mContext);
        player.setListener(playerListener);
        player.setVisible(true);
        ((GLBaseActivity) getContext()).getRootView().addView(player);

        //创建底部控制栏
        controlView = new GLPanoPlayerControlView(mContext,this);
        controlView.setMargin(playerLeft-15,playerHeight+15,0,0);
        controlView.setIPlayerControlCallBack(playerControlCallBack);
        controlView.setRootViewVisible(false);

        mRootView.addView(controlView);

        addMenuContrl();

        //创建网络提示
        createErrorTipsView();

        //获取播放视频数据
        initData();

        return mRootView;
    }

    private void createErrorTipsView() {
        errorTipsView = new GLErrorTipsView(mContext);
        errorTipsView.setMargin(800f,120f,0,0);
        errorTipsView.setDepth(3.6f);
        errorTipsView.setVisible(false);
        errorTipsView.setTipsOnKeyListener(new GLErrorTipsView.TipsOnKeyListener(){
            @Override
            public void onOk() {
                onBack();
            }
        });
        mRootView.addView(errorTipsView);
    }

    private void initData() {

        if(TextUtils.isEmpty(detailUrl)) {
            errorTipsView.setText("未找到播放地址，请稍后再试!");
            errorTipsView.showBackBtn(true);
            errorTipsView.setVisible(true);
            errorTipsView.isUseCallback(true);
            return;
        }

        new GLVideoApi().getVideoDetail(detailUrl, new RequestCallBack<ResponseBaseBean<GLDetailBean>>() {
            @Override
            public void onSuccess(ResponseBaseBean<GLDetailBean> result) {
                errorTipsView.setVisible(false);
                errorTipsView.isUseCallback(false);
                if(null != result && result.getStatus() == 0) {
                    refreshData(result.getData());
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if(!NetworkUtil.isWIFIConnected(mContext)) {
                    errorTipsView.setText("您的网络连接有问题，请稍后再试!");
                    errorTipsView.showBackBtn(true);
                    errorTipsView.setVisible(true);
                    errorTipsView.isUseCallback(true);
                }
            }
        });
    }

    private void refreshData(GLDetailBean bean) {
        if (bean != null) {
            setSenceAndMode(Integer.parseInt(bean.getIs_panorama()), Integer.parseInt(bean.getVideo_dimension()));
//            if (NetworkUtil.isWIFIConnected(mContext)) {
                //System.out.println("!!!!!!!!!!!!----------1:"+bean.getVideo_attrs().get(0).getPlay_url());
            if(null != bean.getVideo_attrs() && bean.getVideo_attrs().size() > 0) {
                player.setVideoPath(bean.getVideo_attrs().get(0).getPlay_url());
            }
//            }
        }
    }

    public void onBack() {

        if(from == 0) {//横屏播放，返回横屏列表
            ((GLBaseActivity) getContext()).getPageManager().pop();
        } else {//横屏播放返回竖屏
            errorTipsView.setText("请把手机从魔镜中取出!");
            errorTipsView.showBackBtn(false);
            errorTipsView.setVisible(true);
            errorTipsView.isUseCallback(false);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    errorTipsView.setVisible(false);
                    errorTipsView.isUseCallback(false);
                    ((GLIndexActivity) getContext()).finish();
                }
            },2000);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startUpdateTimer();
        startPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopUpdateTimer();
        pausePlay();
    }

    public boolean finishFlag = false;

    public boolean isFinish() {
        return finishFlag;
    }

    @Override
    public void onFinish() {
        finishFlag = true;
        if(mRootView.getRootView()!=null&&menuControl!=null) {
            mRootView.getRootView().removeView(menuControl);
        }

        ((GLBaseActivity) getContext()).showCursorView();
        ((GLBaseActivity) getContext()).showSkyBox(GLBaseActivity.SCENE_TYPE_DEFAULT);
//        ((GLBaseActivity) getContext()).showResetView();

        releasePlay();
        ((GLBaseActivity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LightnessControl.SetLightness((GLIndexActivity) mContext,defaultLight);
            }
        });

        if(null != netWorkReceiver) {
            mContext.unregisterReceiver(netWorkReceiver);
        }

        ReceiverManager.getInstance(mContext).removeMassageReceiverCallBack(receiver);

        super.onFinish();
    }

    public void startPlay() {
        if(player != null){
            if (player.isPlaying()) {
                return;
            }
            player.start();
        }
    }

    public void pausePlay(){
        if(player != null){
            if (!player.isPlaying()) {
                return;
            }
            player.pause();
        }
    }

    public void seekTo(int position){
        if(player!=null){
            player.seekTo(position);
        }
    }

    public void releasePlay(){
        stopUpdateTimer();
        if (player != null) {
            player.releasePlay();
            player = null;
        }
    }

    /**
     * 播放器事件回调
     */
    private IGLQiuPlayerListener playerListener = new IGLQiuPlayerListener() {


        @Override
        public void onSeekComplete(GLPlayerView2 player) {
            //System.out.println("!!!!!!!!!!!!!!!!------------onSeekComplete");
        }

        @Override
        public void onPrepared(GLPlayerView2 player) {
            //System.out.println("!!!!!!!!!!!!!!!!------------onPrepared");
            startPlay();
            startUpdateTimer();
            mPrepared = true;
            if(null != controlView) {
                controlView.isPrepared(mPrepared);
                controlView.updateDisplayDuration(player.getDuration());
                controlView.setRootViewVisible(true);
                controlView.showPauseBtn();
            }
        }

        @Override
        public boolean onInfo(GLPlayerView2 player, int what, Object extra) {
            //System.out.println("!!!!!!!!!!!!!!!!------------onInfo");
            return false;
        }

        @Override
        public boolean onError(GLPlayerView2 player, int what) {
            //System.out.println("!!!!!!!!!!!!!!!!------------onError:"+what);
            return false;
        }

        @Override
        public void onCompletion(GLPlayerView2 player) {
            //System.out.println("!!!!!!!!!!!---------------onCompletion");
            stopUpdateTimer();
            if(null != controlView) {
                controlView.showPlayBtn();
                controlView.updateProgress(0,player.getDuration());
            }
        }

        @Override
        public void onBufferingUpdate(GLPlayerView2 player, int percent) {

        }
    };

    /**
     * 设置播放场景和播放模式
     * @param sence
     * @param mode
     */
    public void setSenceAndMode(int sence,int mode){
        switch (sence){
            case 2://360
                player.setSceneType(GLPanoView.SCENE_TYPE_SPHERE);
                break;
            case 3://180
                player.setSceneType(GLPanoView.SCENE_TYPE_HALF_SPHERE);
                break;
            case 4://立方体
                player.setSceneType(GLPanoView.SCENE_TYPE_SKYBOX);
                break;
        }
        switch (mode){
            case 1:
                player.setPlayType(GLPanoView.PLAY_TYPE_2D);
                break;
            case 2:
                player.setPlayType(GLPanoView.PLAY_TYPE_3D_TB);
                break;
            case 3:
                player.setPlayType(GLPanoView.PLAY_TYPE_3D_LR);
                break;
        }

    }

    /**
     * 更新播放进度
     */
    private void updateProgress() {
        if (player == null || player.getDuration() < 1) {
            return;
        }
        int duration = player.getDuration();

        int current = player.getCurrentPosition();

        if (current < 0) {
            return;
        }

        if(controlView!=null && duration>2){
            controlView.updateProgress(current,duration);
        }

    }

    private Timer mUpdateTimer;

    private void startUpdateTimer() {
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }
        mUpdateTimer = new Timer();
        mUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateProgress();
            }
        }, 1000, 1000);
    }

    private void stopUpdateTimer() {
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }
    }

    private IPlayerControlCallBack playerControlCallBack = new IPlayerControlCallBack() {

        @Override
        public void onPlayChanged() {
            if(player != null){
                if (player.isPlaying()) {
                    stopUpdateTimer();
                    player.pause();
                    if(null != controlView) {
                        controlView.showPlayBtn();
                    }
                } else {
                    startUpdateTimer();
                    player.start();
                    if(null != controlView) {
                        controlView.showPauseBtn();
                    }
                }
            }
        }

        @Override
        public void onBackPage() {
            onBack();
        }

        @Override
        public void onSeekToChanged(int curPosition) {
            if(curPosition > 0) {
                seekTo(curPosition * 1000);
            }
            startUpdateTimer();
            if(null != controlView) {
                controlView.showPauseBtn();
            }
        }
    };

    private MenuControl menuControl;

    public void addMenuContrl(){
        menuControl = new MenuControl(getContext());
        mRootView.getRootView().addView(menuControl);
        menuControl.setFocusListener( new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView glRectView, boolean b) {
                if(!mPrepared) return;
                float rootViewAngel = mRootView.getLookAngle();
                float yAngle = menuControl.getyAngle();
                if (b){
                    if (Math.abs(yAngle - rootViewAngel)> 70){
                        mRootView.setLookAngle(yAngle);
                        //menuControl.setBackground( new GLColor( 0xffffff));
                        controlView.setRootViewVisible(true);
                        controlView.setDelayVisiable();
                    }else if(!controlView.isPlayerContrl()){
                        mRootView.setLookAngle(yAngle);
                        //menuControl.setBackground( new GLColor( 0xffffff));
                        controlView.setRootViewVisible(true);
                        controlView.setDelayVisiable();
                    }
                }else{
                    //menuControl.setBackground( new GLColor( 0xff0000));
                }
            }
        });
    }

    private class NetWorkReceiver extends BroadcastReceiver {

        private boolean connection = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!connection) {//第一次网络状态不响应，因为第一次是注册BroadcastReceiver时调起的
                connection = true;
                return;
            }
            //System.out.println("!!!!!!!!!!!!!!!!--------"+NetworkUtil.isNetworkConnected(context));
            if(NetworkUtil.isNetworkConnected(context)) {
                startUpdateTimer();
                if(null != controlView) {
                    controlView.showPauseBtn();
                }
                startPlay();
            } else {
                stopUpdateTimer();
                if(null != controlView) {
                    controlView.showPlayBtn();
                }
                pausePlay();
            }
        }
    }
}
