package com.baofeng.mj.videoplugin.ui.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.baofeng.mj.videoplugin.application.AppConfig;
import com.baofeng.mj.videoplugin.interfaces.IPlayStopListener;
import com.bfmj.viewcore.interfaces.IGLQiuPlayerListener;
import com.bfmj.viewcore.player.GLQiuPlayer;
import com.bfmj.viewcore.view.GLPanoView;
import com.bfmj.viewcore.view.GLPlayerView2;
import com.bfmj.viewcore.view.GLRootView;


public class PanoramVideoPlayerView extends RelativeLayout {

    private GLRootView rootView;
    private GLQiuPlayer player;
    private PlayerBottomView mPlayerBottomView;
    private Context mContext;
    private int playStatus = AppConfig.FLAG_START;

    public PanoramVideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPlayer(context);
    }

    public void initPlayer(Context context){
        mContext = context;
        rootView = new GLRootView(context);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(lp);
        player = new GLQiuPlayer(mContext, rootView);
//        player.scale(0.8f);
//        player.rotate(180, 0, 0, 1);
        player.translate(6, 0, 0);
        player.setListener(playerListener);
        player.setVisible(true);
        rootView.addView(player);
        addView(rootView);
    }

    //    public void setOnGLClickListener(IGLViewClickListener mIGLViewClickListener){
//        rootView.setOnGLClickListener(mIGLViewClickListener);
//    }

    public void initHeadView(){
        rootView.initHeadView();
    }

    /**
     * 设置双屏
     * @param isDouble
     */
    public void setDoubleScreen(boolean isDouble){
        rootView.setDoubleScreen(isDouble);
    }

    public void setVideoPath(String path){
        player.setVideoPath(path);
    }

    public void setBottomView(PlayerBottomView bottomView){
        this.mPlayerBottomView = bottomView;
        mPlayerBottomView.setIPlayStopListener(new IPlayStopListener() {
            @Override
            public void stopPlay() {
                player.releasePlay();
            }
        });
    }

    /**
     * 播放器事件回调
     */
    private IGLQiuPlayerListener playerListener = new IGLQiuPlayerListener() {


        @Override
        public void onSeekComplete(GLPlayerView2 player) {
        }

        @Override
        public void onPrepared(GLPlayerView2 player) {
            mPlayerBottomView.setPalyParam();
            startPlay();
        }

        @Override
        public boolean onInfo(GLPlayerView2 player, int what, Object extra) {
            return false;
        }

        @Override
        public boolean onError(GLPlayerView2 player, int what) {
//            hasPlayError = true;
            return false;
        }

        @Override
        public void onCompletion(GLPlayerView2 player) {
//            releasePlay();
//            finish();
        }

        @Override
        public void onBufferingUpdate(GLPlayerView2 player, int percent) {
            mPlayerBottomView.setSecondaryProgress(percent);
        }
    };

    public void startPlay() {
        playStatus = AppConfig.FLAG_START;
        if(player != null){
            if (player.isPlaying()) {
                return;
            }
            player.start();
        }
    }

    public void pausePlay(){
        playStatus = AppConfig.FLAG_PAUSE;
        if(player != null){
            if (!player.isPlaying()) {
                return;
            }
            player.pause();
        }
    }

    public void seekTo(int position){
        playStatus = AppConfig.FLAG_START;
        if(player!=null){
            player.seekTo(position);
        }
    }

    public void releasePlay(){
        mPlayerBottomView.releaseThread();
        playStatus = AppConfig.FLAG_PAUSE;
        if (player != null) {
            player.releasePlay();
            player = null;
        }
    }


    public void resumeView(){
        startPlay();
        if(rootView!=null){
            rootView.onResume();
        }
    }

    public void pauseView(){

        pausePlay();
        if(rootView!=null){
            rootView.onPause();
        }
    }

    public void destroyView(){
        if(rootView!=null){
            rootView.onDestroy();
        }
        releasePlay();
    }


    /**
     * 设置陀螺仪开关
     * @param value
     */
    public void setGyroscopeEnable(boolean value){
        if(rootView!=null){
            rootView.setGroyEnable(value);
        }
    }

    /**
     * 获取陀螺仪开关状态
     * @return
     */
    public boolean isGyroscopeEnable(){
        if(rootView!=null){
           return rootView.isGroyEnable();
        }
        return false;
    }

    /**
     * 获取视频总时长
     * @return 毫秒
     */
    public int getDuration(){
        if(player!=null){
            return player.getDuration();
        }
        return -1;
    }

    /**
     * 获取视频当前播放进度
     * @return 毫秒
     */
    public int getCurrentPosition(){
        if(player!=null){
            return player.getCurrentPosition();
        }
        return -1;
    }

    public MediaPlayer getMediaPlayer(){
        if(player != null)
            return player.getMediaPlayer();
        return null;
    }
    /**
     * 获取当前播放状态
     * @return
     */
    public int getCurrentPlayStatus(){
        return player.getCurrentPlayStatus();
    }



    public void setRenderCallback(GLRootView.RenderCallback mRenderCallback){
        rootView.setRenderCallback(mRenderCallback);
    }

//    public void setLandscape(boolean isLandscape){
//        rootView.isLandscape = isLandscape;
//    }

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


}
