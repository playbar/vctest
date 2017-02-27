package com.bfmj.viewcore.player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;

import com.bfmj.viewcore.view.GLPlayerView2;
import com.bfmj.viewcore.view.GLRootView;

import java.io.IOException;

/**
 * Created by wangfuzheng on 2015/12/9.
 */
public class GLQiuPlayer extends GLPlayerView2 {

    private MediaPlayer mMediaPlayer;

    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static int mState = STATE_IDLE;

    public GLQiuPlayer(Context context) {
        super(context);
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
        }
    }

    public GLQiuPlayer(Context context, GLRootView glRootView) {
        super(context, glRootView);
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
        }
    }


    @SuppressLint("NewApi")
    @Override
    protected boolean openVideo(){
        if (getPath() == null || getSurfaceTexture() == null){
            return false;
        }

        try {
            if(mMediaPlayer == null){
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(getPath());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setSurface(new Surface(getSurfaceTexture()));
            setListener();

            mMediaPlayer.prepareAsync();
            mState = STATE_PREPARING;
            return true;
        } catch (IllegalArgumentException e) {
            handleError(e, 9001);
        } catch (SecurityException e) {
            handleError(e, 9002);
        } catch (IllegalStateException e) {
            handleError(e, 9003);
        } catch (IOException e) {
            handleError(e, 9004);
        }

        return false;
    }


    private void handleError(Exception e, final int errorCode){
        reset();
        if (getContext() instanceof Activity){
            ((Activity)getContext()).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    playError(errorCode);
                }
            });
        }
        e.printStackTrace();
    }


    @SuppressLint("NewApi")
    private void setListener(){
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mState = STATE_PREPARED;

//                int width = mp.getVideoWidth();
//                int height = mp.getVideoHeight();
//                setVideoSize(width, height);

                playPrepared();
            }
        });

        mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                return playInfo(what, extra);
            }
        });

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                playBufferingUpdate(percent);
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                playCompletion();
            }
        });

        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {

            @Override
            public void onSeekComplete(MediaPlayer mp) {
                playSeekComplete();
            }
        });

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
//				reset();
                return playError(what);
            }
        });

//        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//
//            @Override
//            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//                setVideoSize(width, height);
//                playVideoSizeChanged(GLQiuPlayer.this, width, height);
//            }
//        });
//
//        mMediaPlayer.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
//
//            @Override
//            public void onTimedText(MediaPlayer mp, TimedText text) {
//                playTimedText(GLQiuPlayer.this, text != null ? text.toString() : "");
//
//            }
//        });
    }

    @Override
    public void initDraw() {
        super.initDraw();
    }

    @Override
    public void reset(){
        if (mMediaPlayer == null || mState == STATE_IDLE){
            return;
        }

        mMediaPlayer.reset();
        mState = STATE_IDLE;
    }

    @Override
    public void start(){
        if (mMediaPlayer != null && mState == STATE_PREPARED){
            try {
                mMediaPlayer.start();
            } catch (Exception e) {}

        }

        super.start();
    }

    @Override
    public void pause(){
        if (mMediaPlayer != null && isPlaying()){
            mMediaPlayer.pause();
        }

        super.pause();
    }

    @Override
    public void stop(){
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
        }
    }

    public void releasePlay(){

        if (mMediaPlayer != null && mState != STATE_IDLE){
            mMediaPlayer.release();
            mState = STATE_IDLE;
        }
        super.release();
        mMediaPlayer = null;
    }

    @Override
    public void seekTo(int pos){
        if (mMediaPlayer != null&&mState == STATE_PREPARED){
            try {
                if (!isPlaying()){
                    mMediaPlayer.start();
                }
                mMediaPlayer.seekTo(pos);
            } catch (Exception e) {}
        }
    }

    @Override
    public int getCurrentPosition(){
        if (mMediaPlayer != null && mState == STATE_PREPARED){
            try {
                return mMediaPlayer.getCurrentPosition();
            } catch (Exception e) {}
        }

        return -1;
    }

    @Override
    public int getDuration(){
        if (mMediaPlayer != null && mState == STATE_PREPARED){
            try {
                return mMediaPlayer.getDuration();
            } catch (Exception e) {}
        }

        return -1;
    }

    @Override
    public boolean isPlaying(){
        if (mMediaPlayer != null && mState == STATE_PREPARED){
            try {
                return mMediaPlayer.isPlaying();
            } catch (IllegalStateException e) {
                return false;
            }

        }
        return false;
    }

    @Override
    public void onSurfaceCreated() {
        super.onSurfaceCreated();

        if (mState == STATE_IDLE || mMediaPlayer == null){
            openVideo();
        } else {
            try {
                mMediaPlayer.setSurface(new Surface(getSurfaceTexture()));
            } catch (IllegalStateException e) {
                handleError(e, 9005);
            }

        }
    }

    public MediaPlayer getMediaPlayer(){
        if(mMediaPlayer != null)
            return mMediaPlayer;
        return null;
    }

    public int getCurrentPlayStatus(){
        return mState;
    }

}
