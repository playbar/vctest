package com.baofeng.mj.videoplugin.ui.view;

import android.content.Context;

import com.baofeng.mj.videoplugin.util.MJGLUtils;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLTextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wanghongfang on 2016/8/2.
 */
public class GLToast extends GLRelativeView {
    GLTextView textView;
    int width = 500;
    int height = 90;
    Timer timer ;
    public GLToast(Context context){
        super(context);
        initView();
    }
    private void initView(){
        textView = new GLTextView(getContext());
        textView.setTextSize(36);
        textView.setTextColor(new GLColor(1f,1f,1f));
        textView.setPadding(0,20,0,0);
        textView.setAlignment(GLTextView.ALIGN_CENTER);
        textView.setLayoutParams(width,height);
        this.addView(textView);
//        this.setBackground(R.drawable.player_centerview_bg);
        this.setLayoutParams(width,height);
        this.setVisible(false);
    }

    public void showToast(String text,int duration){
        textView.setText(text);
        setDelayVisiable(duration);
    }

    public void setText(String text){
        textView.setText(text);
    }

    public void cancelTimer(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        GLToast.this.setVisible(false);
    }

    public void showToast(final String text ){

        MJGLUtils.exeGLQueueEvent(getContext(), new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
                GLToast.this.setVisible(true);
            }
        });
        setDelayVisiable(3*1000);
    }
    public void setDelayVisiable(int duration){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                MJGLUtils.exeGLQueueEvent(getContext(), new Runnable() {
                    @Override
                    public void run() {
                        GLToast.this.setVisible(false);
                    }
                });
            }
        };
        timer.schedule(task,duration);
    }

}
