package com.baofeng.mj.videoplugin.ui.view;

import android.content.Context;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.interfaces.IPlayerControlCallBack;
import com.baofeng.mj.videoplugin.util.MJGLUtils;
import com.baofeng.mj.videoplugin.util.TimeFormat;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.util.GLFocusUtils;
import com.bfmj.viewcore.view.GLImageView;
import com.bfmj.viewcore.view.GLProcessView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLTextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yushaochen on 2016/2/17.
 */
public class GLSeekBarView extends GLProcessView {
    public static boolean ISSHOW_DISPLAY = true;
    private int mResImg;
    private GLImageView mBarView;
    private GLTextView mDisplayView;
    private IPlayerControlCallBack mCallBack;
    private int bar_width = 30,bar_height=30;
    private int display_width = 130,display_height=60;
    private long mDuration=0; //秒
    public void setBarImage(int barImage){
        this.mResImg = barImage;
        initView();
        //this.setFocusListener(viewFocusListener);
        this.setOnKeyListener(mKeyListener);
    }
    public GLSeekBarView(Context context){
        super(context);
    }

    public void updateDisplayDuration(long duration){
        mDuration = duration;
    }
    public void setIPlayerControlCallBack(IPlayerControlCallBack callBack){
        this.mCallBack = callBack;
    }
    private void initView(){
        mBarView = new GLImageView(getContext());
        mBarView.setImage(mResImg);
        mBarView.setLayoutParams(bar_width,bar_height);
        this.addView(mBarView);
        mDisplayView = new GLTextView(getContext());
        mDisplayView.setTextColor(new GLColor(1f,1f,1f));
        mDisplayView.setTextSize(26);
        mDisplayView.setText("00:23:00");
        mDisplayView.setBackground(R.mipmap.playerbar_seekbar_displaybg);
        mDisplayView.setLayoutParams(display_width,display_height);
        mDisplayView.setAlignment(GLTextView.ALIGN_CENTER);
        mDisplayView.setPadding(0,10,0,0);
        this.addView(mDisplayView);
        mDisplayView.setVisible(false);

    }

    @Override
    public void addView(GLRectView view) {
        view.setX(this.getX() + this.getPaddingLeft() + view.getMarginLeft());
        view.setY(this.getY() + this.getPaddingTop() + view.getMarginTop());
        super.addView(view);
    }

    @Override
    public void setProcess(int process) {
        super.setProcess(process);
         float width = (this.getWidth() - this.getPaddingLeft() - this.getPaddingRight()) / 100.0F * process;
        this.mBarView.setLayoutParams(bar_width ,bar_height);
        this.mBarView.setX(this.getX() + this.getPaddingLeft()+width-bar_width/2);
        this.mBarView.setY(this.getY() + this.getPaddingTop());
    }
    private void setDisplayLayout(){
        if(mDisplayView!=null) {
            int xy[] = GLFocusUtils.getCursorPosition();
            int x=xy[0],y=xy[1];
            float viewX = GLSeekBarView.this.getX();
            float viewWidth = GLSeekBarView.this.getWidth();
            float viewY = GLSeekBarView.this.getY();

            if(x>=viewX&&x< viewX+viewWidth){
                float process =(x-viewX)/viewWidth;
                long current = (long) (mDuration*process);
                String currentStr = TimeFormat.format((int)(current));
                mDisplayView.setText(currentStr);
                mDisplayView.setLayoutParams(display_width, display_height);
                mDisplayView.setX(x - display_width / 2);
                mDisplayView.setY(viewY - display_height-10);
            }
        }
    }

    private GLViewFocusListener viewFocusListener = new GLViewFocusListener() {
        @Override
        public void onFocusChange(GLRectView glRectView, boolean b) {
            if(b){
                mDisplayView.setVisible(true);
                startTimer();
            }else {
                stopTimer();
                mDisplayView.setVisible(false);
            }
        }
    };

    Timer timer;
    public void startTimer(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MJGLUtils.exeGLQueueEvent(getContext(), new Runnable() {
                    @Override
                    public void run() {
                        setDisplayLayout();
                    }
                });
            }
        },0,300);

    }

    public void stopTimer(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
    }


    private GLOnKeyListener mKeyListener = new GLOnKeyListener() {
        @Override
        public boolean onKeyDown(GLRectView glRectView, int keyCode) {
            if(keyCode== MojingKeyCode.KEYCODE_ENTER){

//                    int xy[] = GLFocusUtils.getCursorPosition();
//                    int x=xy[0],y=xy[1];
//
//                    float viewX = GLSeekBarView.this.getX();
//                    float viewWidth = GLSeekBarView.this.getWidth();
//                    if(x>=viewX&&x< viewX+viewWidth){
////                    int process = (int)((x-viewX)/viewWidth*100);
////                    setProcess(process);
//                        float process =(x-viewX)/viewWidth;
//                        int current = (int) (mDuration*process);
//                        mCallBack.onSeekToChanged(current);
//                    }
//                }

                float[] pos = GLFocusUtils.getPosition(GLSeekBarView.this.getMatrixState().getVMatrix(), GLSeekBarView.this.getDepth());
                int x=(int)pos[0];//;,y=xy[1];
                float viewX = GLSeekBarView.this.getX();
                float viewWidth = GLSeekBarView.this.getWidth();
                if(x>=viewX&&x< viewX+viewWidth){
                    float process =(x-viewX)/viewWidth;
                    int current = (int) (mDuration*process);
                    mCallBack.onSeekToChanged(current);
                }
            }
            return false;
        }
        @Override
        public boolean onKeyUp(GLRectView glRectView, int i) {
            return false;
        }

        @Override
        public boolean onKeyLongPress(GLRectView glRectView, int i) {
            return false;
        }
    };
}
