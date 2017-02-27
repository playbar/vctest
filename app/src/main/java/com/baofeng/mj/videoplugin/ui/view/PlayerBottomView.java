package com.baofeng.mj.videoplugin.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.application.AppConfig;
import com.baofeng.mj.videoplugin.interfaces.IPlayStopListener;
import com.baofeng.mj.videoplugin.ui.activity.GLIndexActivity;
import com.baofeng.mj.videoplugin.util.DataUtils;

/**
 * Created by panxin on 2016/11/2.
 */
public class PlayerBottomView extends RelativeLayout implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    private TextView textview_current_time;
    private SeekBar seekbar_progress;
    private CheckBox playImageBtn;
    private ImageButton imagebtn_in_vr;

    private Handler mMJProgressHandler;//进度更新
    private boolean releaseThread = false;
    private PanoramVideoPlayerView mPanoramVideoPlayerView;
    private TextView playEndTimeTV;

    private Context mContext;

    public PlayerBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View mjBottomView = LayoutInflater.from(mContext).inflate(R.layout.layout_play_bottomview, null);
        seekbar_progress = (SeekBar) mjBottomView.findViewById(R.id.seekbar_progress);
        textview_current_time = (TextView) mjBottomView.findViewById(R.id.textview_current_time);
        playImageBtn = (CheckBox) mjBottomView.findViewById(R.id.radio_play);
        imagebtn_in_vr = (ImageButton) mjBottomView.findViewById(R.id.imagebtn_in_vr);

        playEndTimeTV = (TextView) mjBottomView.findViewById(R.id.textview_all_time);

        if(AppConfig.CAN_GOVR){
            imagebtn_in_vr.setVisibility(View.VISIBLE);
        }
        initListener();
        addView(mjBottomView);
    }

    /**
     * 清除当前时间
     */
    public void clearCurrentTime(){
        textview_current_time.setText(DataUtils.getPlayShowTimer(0));
        seekbar_progress.setSecondaryProgress(0);
    }

    private void initListener(){

        mMJProgressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(mPanoramVideoPlayerView == null){
                    return;
                }
                final int currentTime = mPanoramVideoPlayerView.getCurrentPosition();
                if (currentTime > 0) {
                    seekbar_progress.setProgress(currentTime);
                    textview_current_time.setText(DataUtils.getPlayShowTimer(currentTime));
                }
                switch (AppConfig.PLAY_STATUE){
                    case AppConfig.PLAY_TIME_SHORT_STATUE:
                    case AppConfig.PLAY_TIME_MIDDLE_STATUS:
                        controlStop(currentTime, AppConfig.getMaxTime());
                        break;
                    case AppConfig.PLAY_ALL_STATUS:
                        int durationTime = mPanoramVideoPlayerView.getDuration();
                        controlStop(currentTime, durationTime);
                        break;
                }
            }
        };

        seekbar_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > 0) {
                    mPanoramVideoPlayerView.seekTo(seekBar.getProgress());
                }
            }
        });
        playImageBtn.setOnCheckedChangeListener(this);
        imagebtn_in_vr.setOnClickListener(this);
    }

    public void controlStop(int currentTime, int durationTime){
        if( currentTime >= durationTime - 25){ //播放误差25ms
            textview_current_time.setText(DataUtils.getPlayShowTimer(durationTime));
            if(mIPlayStopListener!=null){
                mIPlayStopListener.stopPlay();
            }
            return;
        }
    }

    public void setPlayBtnStatus(boolean isPlay){
        playImageBtn.setChecked(isPlay);
    }

    public void setParentView(PanoramVideoPlayerView view) {
        mPanoramVideoPlayerView = view;
    }


    public void setSecondaryProgress(int progress) {
        switch (AppConfig.PLAY_STATUE){
            case AppConfig.PLAY_TIME_SHORT_STATUE:
            case AppConfig.PLAY_TIME_MIDDLE_STATUS:
                seekbar_progress.setSecondaryProgress(AppConfig.getMaxTime() / 100 * progress);
                break;
            case AppConfig.PLAY_ALL_STATUS:
                int durationTime = mPanoramVideoPlayerView.getDuration();
                seekbar_progress.setSecondaryProgress(durationTime / 100 * progress);
                break;
        }

    }


    /**
     * 设置播放参数，并开启一个线程刷新进度
     */
    public void setPalyParam() {
        releaseThread = false;
        switch (AppConfig.PLAY_STATUE){
            case AppConfig.PLAY_TIME_SHORT_STATUE:
            case AppConfig.PLAY_TIME_MIDDLE_STATUS:
                seekbar_progress.setMax(AppConfig.getMaxTime());
                playEndTimeTV.setText(DataUtils.getPlayShowTimer(AppConfig.getMaxTime()));
                break;
            case AppConfig.PLAY_ALL_STATUS:
                int durationTime = mPanoramVideoPlayerView.getDuration();
                seekbar_progress.setMax(durationTime);
                playEndTimeTV.setText(DataUtils.getPlayShowTimer(durationTime));
                break;
        }
        DelayThread delayThread = new DelayThread(500);
        delayThread.start();
    }


    public void releaseThread() {
        releaseThread = true;
    }

    private String mUrl;
    public void setUrl(String url){
        this.mUrl = url;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.imagebtn_in_vr){
            Intent intent = new Intent(mContext,GLIndexActivity.class);
            intent.putExtra("toPage",1);
            intent.putExtra("data", mUrl);
            ((Activity)mContext).startActivityForResult(intent, AppConfig.OPEN_VR_CODE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isPlaying) {
        if(isPlaying){
            mPanoramVideoPlayerView.pausePlay();
        }else {
            mPanoramVideoPlayerView.startPlay();
        }
//        int status = mPanoramVideoPlayerView.getPlayStatus();
//        if(status == AppConfig.FLAG_PAUSE) {
//            mPanoramVideoPlayerView.startPlay();
//        }else if(status == AppConfig.FLAG_START){
//            mPanoramVideoPlayerView.pausePlay();
//        }
    }

    /**
     * 刷新播放进度
     */
    class DelayThread extends Thread {
        int milliseconds;

        public DelayThread(int i) {
            milliseconds = i;
        }

        public void run() {
            while (true) {
                try {
                    sleep(milliseconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (releaseThread) {
                    break;
                }
                mMJProgressHandler.sendEmptyMessage(0);

            }
        }
    }

    public IPlayStopListener mIPlayStopListener;
    public void setIPlayStopListener(IPlayStopListener listener){
        this.mIPlayStopListener = listener;
    }
}
