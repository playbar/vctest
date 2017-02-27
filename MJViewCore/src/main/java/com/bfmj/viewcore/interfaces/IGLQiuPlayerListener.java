package com.bfmj.viewcore.interfaces;

import com.bfmj.viewcore.view.GLPlayerView2;

/**
 * 
 * ClassName: IGLPlayerListener <br/>
 * @author lixianke    
 * @date: 2015-4-3 上午11:00:23 <br/>  
 * description:
 */
public interface IGLQiuPlayerListener {
	void onPrepared(GLPlayerView2 player);
	boolean onInfo(GLPlayerView2 player, int what, Object extra);
	void onBufferingUpdate(GLPlayerView2 player, int percent);
	void onCompletion(GLPlayerView2 player);
	void onSeekComplete(GLPlayerView2 player);
	boolean onError(GLPlayerView2 player, int what);
//	void onPrepared(GLQiuPlayerView player);
//	boolean onInfo(GLQiuPlayerView player, int what, Object extra);
//	void onBufferingUpdate(GLQiuPlayerView player, int percent);
//	void onCompletion(GLQiuPlayerView player);
//	void onSeekComplete(GLQiuPlayerView player);
//	boolean onError(GLQiuPlayerView player, int what, int extra);
//	void onVideoSizeChanged(GLQiuPlayerView player, int width, int height);
//	void onTimedText(GLQiuPlayerView player, String text);
}
