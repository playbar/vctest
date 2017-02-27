package com.baofeng.mj.videoplugin.application;

import android.os.Environment;

public class AppConfig {
	
	public static final String SD_DIR =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	public static final String STORAGE_DIR = "mj_plugin";//存储目录
	public static final String SAVE_DIR = SD_DIR + STORAGE_DIR+"/";
	public static final String APK_NAME = "mjapp.apk";
	public static final String MJ_PACKAGE_NAME = "com.baofeng.mj";


	//网络切换时状态
	public static final int TYPE_NET_DOWNLOAD_DEFAULT = 0;//弹出网络确认框
	public static final int TYPE_NET_DOWNLOAD_MOBILE = 1;//任何网络都可立即下载
	public static final int TYPE_NET_DOWNLOAD_WAIT_WIFI = 2;//wifi时自动下载


	public static final int FLAG_START = 0;
	public static final int FLAG_PAUSE = 1;


	public static final String KEY_INTENT_URL = "key_intent_url";
	public static final String KEY_INTENT_RESID = "key_intent_resid";
	public static final String KEY_INTENT_TITLE = "key_intent_title";
	public static final String KEY_INTENT_FROMPAGE = "key_intent_frompage";
	public static final String KEY_INTENT_BUNDLE = "key_intent_bundle";

	//从竖屏播放页面到横屏播放页面，requestCode
	public static final int OPEN_VR_CODE = 101;

	/**
	 * 初始化sdk一些参数
	 */
	public static final long memoryMaxSize = 256 * 1024 * 1024;
	public static final long fileMaxSize = 1024 * 1024 * 1024;
	public static final long singleMemoryMaxSize = 50 * 1024 * 1024;
	public static final long singleFileMaxSize = 500 * 1024 * 1024;
	public static final int maxTask = 1;

	public static final int PLAY_TIME_SHORT_STATUE = 0; //10秒钟
	public static final int PLAY_TIME_MIDDLE_STATUS = 1; //30秒钟
	public static final int PLAY_ALL_STATUS = 2; //全长播放

	public static final int PLAY_TIME_SHORT = 10; //10秒钟
	public static final int PLAY_TIME_MIDDLE = 30; //30秒钟

	//横屏开关
	public static final boolean CAN_GOVR = true;
	//引流下载开关
	public static final boolean AUTO_DOWNLOAD = true;
	//默认魔镜S1,眼镜ids
	public static final String COMPANY_ID = "1";
	public static final String PRODUCT_ID = "11";
	public static final String LENS_ID = "19";
	//通过ids获取到的key
	public static String GLASSES_KEY = "";

	//0:预览10秒 1：预览30秒 2：完整预览（视频播放总长度）
	public static final int PLAY_STATUE = 2;
	/**
	 * 播放状态为0，1时，获取预览时长
	 * @return
	 */
	public static int getMaxTime(){
		int playTime = 0;
		switch (PLAY_STATUE){
			case PLAY_TIME_SHORT_STATUE:
				playTime  = PLAY_TIME_SHORT * 1000;
				break;
			case PLAY_TIME_MIDDLE_STATUS:
				playTime =  PLAY_TIME_MIDDLE * 1000;
				break;
		}
		return playTime;
	}

	//按摩椅传过来状态等数据
	public static int connectStatus = 0;
	public static String[] modeData;
	public static int modeIndex = -1;
}
