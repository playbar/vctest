package com.baofeng.mj.videoplugin.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.application.AppConfig;
import com.baofeng.mj.videoplugin.ui.view.GLResetView;
import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.input.base.MojingInputCallback;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.util.HeadControl;
import com.bfmj.viewcore.view.BaseViewActivity;
import com.bfmj.viewcore.view.GLPanoView;
import com.bfmj.viewcore.view.GLRootView;
import com.bfmj.viewcore.view.GLTextView;
import com.mojing.sdk.pay.widget.mudoles.Glass;
import com.mojing.sdk.pay.widget.mudoles.Manufacturer;
import com.mojing.sdk.pay.widget.mudoles.ManufacturerList;
import com.mojing.sdk.pay.widget.mudoles.Product;

import java.io.IOException;
import java.util.List;

public class GLBaseActivity extends BaseViewActivity implements MojingInputCallback {
	private boolean setBrightness = false;

	public static final int SCENE_TYPE_DEFAULT = 0x0;
	public static final int SCENE_TYPE_CINEMA = 0x1;
	private int mSceneType = -1;

	private HeadControl mHeadControlCursor;
	private GLResetView mResetView;

	private ImageView mSplash;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			Runtime.getRuntime().exec("setprop debug.sf.vr 0");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!TextUtils.isEmpty(AppConfig.GLASSES_KEY)) {
			setMojingType(AppConfig.GLASSES_KEY);
		}
		super.onCreate(savedInstanceState);

//		getRootView().setMultiThread(true);
//		getRootView().setTimeWarp(true);

		//开屏图
		mSplash = new ImageView(this);
		mSplash.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mSplash.setImageResource(R.mipmap.hengping_splash);
		getRootLayout().addView(mSplash);
		getRootView().setRenderCallback(new GLRootView.RenderCallback() {
			@Override
			public void onSurfaceCreated() {
				GLBaseActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (mSplash != null){
							getRootLayout().removeView(mSplash);
							mSplash = null;
						}
					}
				});
			}

			@Override
			public void updateFPS(float v) {

			}
		});

		showSkyBox(SCENE_TYPE_DEFAULT);
		showCursorView();
//		showResetView();

//		StickUtil.initStickUtil();
//		initLog();

//		/** 注册监听home按键事件 */
//		registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
//				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
//
//		/** 注册监听设置屏幕亮度模式 自动或手动的变化 */
//		this.getContentResolver()
//				.registerContentObserver(
//						Settings.System
//								.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE),
//						true, mBrightnessObserver);
//		/** 注册监听屏幕亮度变化 */
//		this.getContentResolver().registerContentObserver(
//				Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
//				true, mBrightnessObserver);
	}

	/**
	 * 显示天空盒场景
	 * @param type 场景类型
     */
	public synchronized void showSkyBox(int type){
		mSceneType = type;

		GLPanoView mSkyboxView = GLPanoView.getSharedPanoView(this);
		mSkyboxView.reset();
		if(type == SCENE_TYPE_CINEMA){
			mSkyboxView.setImage(R.mipmap.hengping_skybox_launcher);
		} else {
			mSkyboxView.setImage(R.mipmap.hengping_skybox_launcher);
		}

		mSkyboxView.setVisible(true);
	}

	public void setSkyboxFixed(boolean fixed){
		GLPanoView.getSharedPanoView(this).setFixed(fixed);
	}

	/**
	 * 隐藏天空盒场景
	 */
	public void hideSkyBox(){
		GLPanoView.getSharedPanoView(this).setVisible(false);
	}

	@Override
	public void showCursorView() {
		if (mHeadControlCursor == null){
			mHeadControlCursor = new HeadControl(this);
			mHeadControlCursor.setX( 1160);
			mHeadControlCursor.setY( 1160);
			mHeadControlCursor.setDepth(3f);
//			mHeadControlCursor.setImage(R.drawable.cursor_normal);
//			mHeadControlCursor.setLayoutParams(80, 80);
			getRootView().addView(mHeadControlCursor);
		}

		mHeadControlCursor.setVisible(true);
	}

	public void setCursorFixed(boolean fixed){
		if (mHeadControlCursor != null ) {
			mHeadControlCursor.setFixed(fixed);
		}
	}

	@Override
	public void hideCursorView() {
		if (mHeadControlCursor != null ) {
			mHeadControlCursor.setVisible(false);
		}
	}

	/**
	 * 显示复位图标
	 */
	public void showResetView() {
		if (mResetView == null){
			mResetView = new GLResetView(this);
			mHeadControlCursor.bindView(mResetView);
			getRootView().addView(mResetView);
		}

		mResetView.setVisible(true);
	}

	/**
	 * 隐藏复位图标
	 */
	public void hideResetView() {
		if (mResetView != null ) {
			mResetView.setVisible(false);
		}
	}

	@Override
	protected void onDestroy() {
		GLPanoView.finish();
		super.onDestroy();
//		unregisterReceiver(mHomeKeyEventReceiver);
//		this.getContentResolver()
//				.unregisterContentObserver(mBrightnessObserver);
		try {
			Runtime.getRuntime().exec("setprop debug.sf.vr 1");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {

		super.onPause();
		//StickUtil.disconnect();// 取消遥控器回调
		setBrightness = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		//StickUtil.connect(this);

		if (getRootView() != null){
			getRootView().initHeadView();
		}
	}

//	/**
//	 * 监听是否点击了home键将客户端推到后台
//	 */
//	private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
//		String SYSTEM_REASON = "reason";
//		String SYSTEM_HOME_KEY = "homekey";
//		String SYSTEM_HOME_KEY_LONG = "recentapps";
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//				String reason = intent.getStringExtra(SYSTEM_REASON);
//				if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
//					// 表示按了home键,程序到了后台
//					ScreenBrightnessUtils.setModel(GLBaseActivity.this, false);
//					setBrightness = true;
//
//
////					Log.d("MJ", "GLBaseActivity 按了home键");
//					// GlobLockDialog.getInstance(context).isVisiableLockDialogView(false);
//				} else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
//					// 表示长按home键,显示最近使用的程序列表
//					ScreenBrightnessUtils.setModel(GLBaseActivity.this, false);
//					setBrightness = true;
//
//
////					Log.d("MJ", "GLBaseActivity 长按home键");
//					// GlobLockDialog.getInstance(context).isVisiableLockDialogView(false);
//				}
//			}
//		}
//	};


	@Override
	protected void onStart() {
//		ScreenBrightnessUtils.setModel(this, true);
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		setBrightness = true;
	}

	/**
	 * @author qiguolong @Date 2015-6-24 下午6:13:47
	 * @description:{gl事件 延迟
	 * @param runnable
	 */
	public void queenGlRunableDelay(final Runnable runnable, final int times) {
		new Thread() {
			public void run() {
				try {
					sleep(times);
					queenGlRunable(runnable);
				} catch (InterruptedException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

			};
		}.start();

	}

	/**
	 * @author qiguolong @Date 2015-6-24 下午6:13:47
	 * @description:{gl事件
	 * @param runnable
	 */
	public void queenGlRunable(final Runnable runnable) {

		if (getRootView() != null)
			getRootView().queueEvent(runnable);

	}

//	private ContentObserver mBrightnessObserver = new ContentObserver(
//			new Handler()) {
//		@Override
//		public void onChange(boolean selfChange) {
//			int model = ScreenBrightnessUtils.getScreenMode();
//			if (setBrightness || model == 1) {
//				ScreenBrightnessUtils.initModel(GLBaseActivity.this);
//			}
//		}
//	};

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
//		if (StickUtil.dispatchKeyEvent(event) && event.getKeyCode() != 4)
//			return true;
//		else
			return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent event) {
//		if (StickUtil.dispatchGenericMotionEvent(event))
//			return true;
//		else
			return super.dispatchGenericMotionEvent(event);
	}

	@Override
	public void onBluetoothAdapterStateChanged(int arg0) {
//		StickUtil.onBluetoothAdapterStateChanged(arg0);
	}

	@Override
	public void onMojingDeviceAttached(String arg0) {
//		StickUtil.isConnected = true;
//		StickUtil.onMojingDeviceAttached(arg0);

	}

	@Override
	public void onMojingDeviceDetached(String arg0) {
//		StickUtil.isConnected = false;
//		StickUtil.onMojingDeviceDetached(arg0);

	}

	@Override
	public boolean onMojingKeyDown(String deviceName, final int keyCode) {
//		StickUtil.onMojingKeyDown(deviceName, keyCode);
		onZKeyDown(keyCode);
		return false;
	}

	@Override
	public boolean onMojingKeyLongPress(String deviceName, final int keyCode) {
//		StickUtil.onMojingKeyLongPress(deviceName, keyCode);
		onZKeyLongPress(keyCode);
		return false;
	}

	@Override
	public boolean onMojingKeyUp(String deviceName, final int keyCode) {
//		StickUtil.onMojingKeyUp(deviceName, keyCode);
		onZKeyUp(keyCode);
		return false;
	}

	@Override
	public boolean onMojingMove(String deviceName, int axis, float x, float y, float z) {
//		StickUtil.onMojingMove(deviceName, axis, x, y, z);
		return false;
	}

	@Override
	public boolean onMojingMove(String deviceName, int axis, float value) {
//		StickUtil.onMojingMove(deviceName, axis, value);
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (StickUtil.isConnected){
//			return true;
//		}

		 if(keyCode==KeyEvent.KEYCODE_VOLUME_MUTE||keyCode==KeyEvent.KEYCODE_VOLUME_DOWN|| keyCode==KeyEvent.KEYCODE_VOLUME_UP){
			 return super.onKeyDown(keyCode, event);
		 }
		 if(keyCode==KeyEvent.KEYCODE_VOLUME_MUTE||keyCode==KeyEvent.KEYCODE_VOLUME_DOWN|| keyCode==KeyEvent.KEYCODE_VOLUME_UP){
			 return super.onKeyDown(keyCode, event);
		 }
		if (keyCode != KeyEvent.KEYCODE_BACK || getPageManager().hasMorePage()){
			if (keyCode == 23){
				keyCode = MojingKeyCode.KEYCODE_ENTER;
			}
			onZKeyDown(keyCode);
			return  true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		if (StickUtil.isConnected){
//			return true;
//		}

		if (keyCode != KeyEvent.KEYCODE_BACK || getPageManager().hasMorePage()){
			if (keyCode == 23){
				keyCode = MojingKeyCode.KEYCODE_ENTER;
			}
			onZKeyUp(keyCode);
			return  true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN){
			onZKeyDown(MojingKeyCode.KEYCODE_ENTER);
		} else if (event.getAction() == MotionEvent.ACTION_UP){
			onZKeyUp(MojingKeyCode.KEYCODE_ENTER);
		}

		return super.onTouchEvent(event);
	}

	private String getGlassKey(){
		if (!MojingSDK.GetInitSDK()) {
			MojingSDK.Init(this.getApplicationContext());
		}

		ManufacturerList m_ManufacturerList = ManufacturerList.getInstance("zh");

		List<Manufacturer> manufacturers = m_ManufacturerList.mManufaturerList;
		List<Product> products = manufacturers.get(0).mProductList;
		List<Glass> glasses = products.get(0).mGlassList;
		String key = glasses.get(0).mKey;
		return key;
	}

	private void initLog(){
		final GLTextView fps = new GLTextView(this);
		fps.setX(900);
		fps.setY(2200);
		fps.setLayoutParams(600, 100);
		fps.setFixed(true);
		fps.setBackground(new GLColor(0x000000, 0.5f));
		fps.setTextColor(new GLColor(0xffffff));
		fps.setTextSize(80);

		getRootView().addView(fps);

		new Thread(new Runnable() {
			long times = 0;
			int max = 0;
			int min = 60;

			@Override
			public void run() {
				getRootView().getFPS();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (true){
					final int f = getRootView().getFPS();
					if (f > 0 && f < 70){
						times++;
						if (times > 2) {
							max = Math.max(f, max);
							min = Math.min(f, min);
							getRootView().queueEvent(new Runnable() {
								@Override
								public void run() {
									String msg = "FPS : " + f;
									if (max > 0){
										msg +=  " [" + min + "~" + max + "]";
									}
									fps.setText(msg);
								}
							});
						}
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();
	}
}
