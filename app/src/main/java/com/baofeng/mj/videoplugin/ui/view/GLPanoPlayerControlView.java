package com.baofeng.mj.videoplugin.ui.view;

import android.content.Context;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.application.AppConfig;
import com.baofeng.mj.videoplugin.interfaces.IPlayerControlCallBack;
import com.baofeng.mj.videoplugin.ui.activity.GLBaseActivity;
import com.baofeng.mj.videoplugin.ui.page.PanoNetPlayPage;
import com.baofeng.mj.videoplugin.util.MJGLUtils;
import com.baofeng.mj.videoplugin.util.TimeFormat;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.render.GLConstant;
import com.bfmj.viewcore.util.HeadControl;
import com.bfmj.viewcore.view.GLImageView;
import com.bfmj.viewcore.view.GLLinearView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLTextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yushaochen on 2017/2/17.
 */

public class GLPanoPlayerControlView extends GLRelativeView {

    private Context mContext;
    private GLRelativeView mRootView;
    private final int VIEW_HEIGHT=200;
    private final int VIEW_WIDTH=1440;
    private final int PROCESSTEXT_WIDTH=300;
    private final int TOP_LAYOUT_HEIGHT=100;
    private final String ROOTVIEW_ID="rootview_id";
    private final String BOOTVIEW_ID="bootview_id";
    private final String TOPVIEW_ID="topview_id";

    private GLLinearView topLayout;
    private GLTextView mProceeText;
    private GLSeekBarView processView;

    private GLImageView mBackBtn;

    private GLImageView mPlayOrPauseBtn;

    private GLMassageView mMassageView;

    private PanoNetPlayPage mPage;

    public GLPanoPlayerControlView(Context context,PanoNetPlayPage page) {
        super(context);
        this.mContext = context;
        mPage = page;
        this.setId(BOOTVIEW_ID);
        this.setFocusListener(mLayoutFoucsListener);
        this.setLayoutParams(VIEW_WIDTH,VIEW_HEIGHT+200);
        mRootView = new GLRelativeView(context);
        mRootView.setId(ROOTVIEW_ID);
        mRootView.setLayoutParams(VIEW_WIDTH,VIEW_HEIGHT+200);
//        mRootView.setFocusListener(mLayoutFoucsListener);
        //创建播放进度条
        createSeekView();
        //创建底部返回按钮
        createBackButton();
        //创建播放暂停按钮
        createPlayOrPauseButton();
        //创建按摩模式操作按钮
        createMassageView();

        this.addView(mRootView);
    }

    private void createMassageView() {
        mMassageView = new GLMassageView(mContext);
        mMassageView.setMargin(800f,0,0,50f);
        mRootView.addViewBottom(mMassageView);

        mMassageView.setDatas(AppConfig.modeData);
        mMassageView.setSlected(AppConfig.modeIndex+"");
        if(AppConfig.connectStatus == 0) {
            mMassageView.setVisible(false);
        } else {
            mMassageView.setVisible(true);
        }
    }

    public void showMassageView(boolean isShow) {
        if(null != mMassageView) {
            mMassageView.setVisible(isShow);
        }
    }

    public void setMassageViewSelected(String id) {
        if(null != mMassageView) {
            mMassageView.setSlected(id);
        }
    }

    private void createPlayOrPauseButton() {
        mPlayOrPauseBtn = new GLImageView(mContext);
        mPlayOrPauseBtn.setLayoutParams(100f,100f);
        mPlayOrPauseBtn.setBackground(R.mipmap.hengping_video_play);
        mPlayOrPauseBtn.setMargin(600f,0,0,50f);
        mPlayOrPauseBtn.setOnKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                mCallBack.onPlayChanged();
                return false;
            }

            @Override
            public boolean onKeyUp(GLRectView view, int keycode) {
                return false;
            }

            @Override
            public boolean onKeyLongPress(GLRectView view, int keycode) {
                return false;
            }
        });
        HeadControl.bindView(mPlayOrPauseBtn);
        mRootView.addViewBottom(mPlayOrPauseBtn);
    }

    public void showPlayBtn() {
        if(null != mPlayOrPauseBtn) {
            MJGLUtils.exeGLQueueEvent(mContext, new Runnable() {
                @Override
                public void run() {
                    mPlayOrPauseBtn.setBackground(R.mipmap.hengping_video_play);
                }});
        }
    }

    public void showPauseBtn() {
        if(null != mPlayOrPauseBtn) {
            MJGLUtils.exeGLQueueEvent(mContext, new Runnable() {
                @Override
                public void run() {
                    mPlayOrPauseBtn.setBackground(R.mipmap.hengping_video_pause);
                }});
        }
    }

    private void createBackButton() {
        mBackBtn = new GLImageView(mContext);
        mBackBtn.setLayoutParams(100f,100f);
        mBackBtn.setBackground(R.mipmap.hengping_back);
        mBackBtn.setMargin(400f,0,0,50f);
        mBackBtn.setOnKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                if(null != mCallBack) {
                    mCallBack.onBackPage();
                }
                return false;
            }

            @Override
            public boolean onKeyUp(GLRectView view, int keycode) {
                return false;
            }

            @Override
            public boolean onKeyLongPress(GLRectView view, int keycode) {
                return false;
            }
        });
        HeadControl.bindView(mBackBtn);
        mRootView.addViewBottom(mBackBtn);
    }

    private void createSeekView() {
        topLayout = new GLLinearView(mContext);
        topLayout.setLayoutParams(VIEW_WIDTH,TOP_LAYOUT_HEIGHT);
        topLayout.setBackground(R.mipmap.seekbar_bg);
        topLayout.setAlign(GLConstant.GLAlign.CENTER_VERTICAL);
        topLayout.setId(TOPVIEW_ID);
        topLayout.setMargin(0,50f,0,0);
//        topLayout.setBackground(new GLColor(0xff0000));

        processView = new GLSeekBarView(mContext);
        processView.setBackground(R.mipmap.hengping_playbar_progressbar_bg);
        processView.setProcessColor(R.mipmap.hengping_playbar_progressbar);
        processView.setBarImage(R.mipmap.hengping_playbar_slidebar);
        processView.setLayoutParams(VIEW_WIDTH-PROCESSTEXT_WIDTH-80,20);
        processView.setMargin(30,35,0,0);
        processView.setProcess(0);
        HeadControl.bindView(processView);

        mProceeText = new GLTextView(mContext);
        mProceeText.setTextColor(new GLColor(0xff355177));
        mProceeText.setTextSize(35);
        mProceeText.setText("00:00:00/00:00:00");
        mProceeText.setLayoutParams(PROCESSTEXT_WIDTH,50);
        mProceeText.setAlign(GLConstant.GLAlign.CENTER);
        mProceeText.setMargin(30,20,0,0);

        topLayout.addView(processView);
        topLayout.addView(mProceeText);

        mRootView.addView(topLayout);
    }

    public void updateProgress(final int current,final int duration){
        MJGLUtils.exeGLQueueEvent(mContext, new Runnable() {
            @Override
            public void run() {
                if(processView!=null) {
                    int progress = (int) ((float) current / duration * 100);
                    processView.setProcess(progress);
                }
                if(mProceeText!=null){
                    String cur = TimeFormat.format(current / 1000);
                    String dur = TimeFormat.format(duration / 1000);
                    mProceeText.setText(cur+"/"+dur);
                }
            }
        });
    }

    public void updateDisplayDuration(long duration){
        if(processView!=null) {
            processView.updateDisplayDuration(duration/1000);
        }
    }

    private boolean mPrepared = false;

    public void isPrepared(boolean prepared) {
        mPrepared = prepared;
    }

    private IPlayerControlCallBack mCallBack;

    public void setIPlayerControlCallBack(IPlayerControlCallBack callBack){
        mCallBack = callBack;
        if(processView!=null) {
            processView.setIPlayerControlCallBack(callBack);
        }
    }

    public void setRootViewVisible(boolean isShow) {
        mRootView.setVisible(isShow);
    }

    public boolean isPlayerContrl(){
        return mRootView.isVisible();
    }

    private GLViewFocusListener mLayoutFoucsListener = new GLViewFocusListener() {

        @Override
        public void onFocusChange(GLRectView view, boolean focused) {
            if(null != mPage && mPage.isFinish()) {
                return;
            }
            if(!mPrepared) {
                return;
            }
            String id = view.getId();
            if(id.equals(BOOTVIEW_ID)) {
                if(focused){
                    ((GLBaseActivity)mContext).showCursorView();
                }else {
                    ((GLBaseActivity)mContext).hideCursorView();
                }
                if(focused){
                    setRootViewVisible(true);
                    if(timer!=null){
                        timer.cancel();
                    }
                }else {
                    setDelayVisiable();
                }
            }
        }
    };

    Timer timer ;
    public void setDelayVisiable(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                MJGLUtils.exeGLQueueEvent(mContext, new Runnable() {
                    @Override
                    public void run() {
                        if(!mRootView.isFocused())
                            setRootViewVisible(false);
                    }
                });
            }
        };
        timer.schedule(task,2*1000);
    }
}
