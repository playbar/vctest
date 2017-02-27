package com.baofeng.mj.videoplugin.interfaces;


/**
 * Created by yushaochen on 2016/2/17.
 */
public interface IPlayerControlCallBack {
    void onPlayChanged();//播放状态改变
    void onBackPage();//页面返回
    void onSeekToChanged(int curPosition); //秒

}
