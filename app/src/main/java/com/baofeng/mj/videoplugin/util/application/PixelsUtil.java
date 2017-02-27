package com.baofeng.mj.videoplugin.util.application;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 像素工具类
 */
public class PixelsUtil {
	private static DisplayMetrics displayMetrics;

	public static DisplayMetrics getDisplayMetrics(Context context){
		if(displayMetrics == null){
			displayMetrics = context.getResources().getDisplayMetrics();
		}
		return displayMetrics;
	}

	/**
	 * 获取屏幕宽度
	 */
	public static int getWidthPixels(Context context){
		return getDisplayMetrics(context).widthPixels;
	}

	/**
	 * 获取屏幕高度
	 */
	public static int getheightPixels(Context context){
		return getDisplayMetrics(context).heightPixels;
	}

	/**
	 * 将dip值转换为px值
	 */
	public static int dip2px(Context context, float dpValue) {
		return (int) (dpValue * getDisplayMetrics(context).density + 0.5f);
	}
	
	/**
	 * 将px值转换为dip值
	 */
	public static int px2dip(Context context,float pxValue) {
		return (int) (pxValue / getDisplayMetrics(context).density + 0.5f);
	}
	
	/** 
     * 将sp值转换为px值
     */  
    public static int sp2px(Context context, float spValue) {
        return (int) (spValue * getDisplayMetrics(context).scaledDensity + 0.5f);
    }
}