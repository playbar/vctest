package com.bfmj.viewcore.view;

import android.content.Context;
import android.opengl.Matrix;

import com.bfmj.viewcore.interfaces.IGLQiuPlayerListener;
import com.bfmj.viewcore.render.GLRenderParams;

/**
 * GL播放器
 * ClassName: GLPlayerView <br/>
 * @author lixianke
 * @date: 2015-3-9 下午1:51:19 <br/>
 * description:
 */
public abstract class GLPlayerView2 extends GLPanoView {

//    private SurfaceTexture mSurfaceTexture;
    private GLRenderParams mRenderParams;
    private String mPath;
    private boolean isSurfaceCreated = false;
    private IGLQiuPlayerListener mListener;

    public GLPlayerView2(Context context) {
        super(context);
    }

    public GLPlayerView2(Context context, GLRootView mGLRootView) {
        super(context,mGLRootView);
    }

    abstract protected boolean openVideo();

    /**
     * 重置播放状态
     * @author lixianke  @Date 2015-3-17 下午3:37:26
     * @param
     * @return
     */
//    abstract protected void reset();

    /**
     * 开始播放
     * @author lixianke  @Date 2015-3-17 下午3:38:06
     * @param
     * @return
     */
    public void start(){ }


    /**
     * 暂停播放
     * @author lixianke  @Date 2015-3-17 下午3:38:30
     * @param
     * @return
     */
    public void pause(){}

    /**
     * 停止播放
     * @author lixianke  @Date 2015-3-17 下午3:38:59
     * @param
     * @return
     */
    abstract public void stop();

    /**
     * 转到指定位置播放
     * @author lixianke  @Date 2015-3-17 下午3:39:42
     * @param pos 播放位置（毫秒）
     * @return
     */
    abstract public void seekTo(int pos);

    /**
     * 获取当前的播放位置
     * @author lixianke  @Date 2015-3-17 下午3:40:29
     * @param
     * @return 播放位置（毫秒）
     */
    abstract public int getCurrentPosition();

    /**
     * 获取影片长度
     * @author lixianke  @Date 2015-4-9 上午9:49:26
     * @param
     * @return 影片长度（毫秒）
     */
    abstract public int getDuration();

    /**
     * 获取是否在正在播放
     * @author lixianke  @Date 2015-4-9 上午11:01:16
     * @param
     * @return 是否正在播放
     */
    abstract public boolean isPlaying();

//    /**
//     * 获取SurfaceTexture
//     * @author lixianke  @Date 2015-3-17 下午2:52:16
//     * @param
//     * @return
//     */
//    public SurfaceTexture getSurfaceTexture(){
//        return mSurfaceTexture;
//    }

//    /**
//     * 缩放场景， x y z三轴以同样的比例缩放
//     * @author lixianke  @Date 2015-6-19 下午5:33:21
//     * @param scale 缩放比例
//     * @return
//     */
//    public void scale(float scale){
//        scale(scale, scale, scale);
//    }
//
//    /**
//     * 缩放场景
//     * @author lixianke  @Date 2015-6-19 下午5:35:07
//     * @param sx x轴缩放比例
//     * @param sy y轴缩放比例
//     * @param sz z轴缩放比例
//     * @return
//     */
//    public void scale(float sx, float sy, float sz){
////    	float[] mtx = getMatrixState().getScaleMatrix();
////    	System.arraycopy(GLMatrixState.getInitMatrix(), 0, mtx, 0, 16);
//        mScaleX = sx;
//        mScaleY = sy;
//        mScaleZ = sz;
//    }

    /**
     * 平移场景
     * @author lixianke  @Date 2015-6-19 下午5:38:38
     * @param tx x轴平移距离
     * @param ty y轴平移距离
     * @param tz z轴平移距离
     * @return
     */
    public void translate(float tx, float ty, float tz) {
        Matrix.translateM(getMatrixState().getCurrentMatrix(), 0, tx, ty, tz);
    }

    /**
     * 旋转场景
     * @author lixianke  @Date 2015-6-19 下午5:39:51
     * @param angle 旋转角度
     * @param rx 沿x的向量分量
     * @param ry 沿y的向量分量
     * @param rz 沿z的向量分量
     * @return
     */
    public void rotate(float angle, float rx, float ry, float rz){
        Matrix.rotateM(getMatrixState().getCurrentMatrix(), 0, angle, rx, ry, rz);
    }
    /**
     * 设置播放地址
     * @author lixianke  @Date 2015-3-17 下午3:36:46
     * @param path 播放地址
     * @return
     */
    public void setVideoPath(String path){
        mPath = path;
        openVideo();
    }

    protected String getPath(){
        return mPath;
    }

    @Override
    public void initDraw() {

        if (isSurfaceCreated == false){
            onSurfaceCreated();
        }

        isSurfaceCreated = true;
        super.initDraw();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();

        isSurfaceCreated = false;
    }
//    GLPanoView panoView;
    @Override
    public void onSurfaceCreated() {
        super.onSurfaceCreated();

//        panoView = GLPanoView.getSharedPanoView(getContext(),mGLRootView);
//        reset();
        setRenderType(GLPanoView.RENDER_TYPE_VIDEO);
        setPlayType(getPlayType());
        setSceneType(getSceneType());
        setVisible(true);

//		int textureId = GLTextureUtils.createVideoTextureID();
//
//		mRenderParams = new GLRenderParams(GLRenderParams.RENDER_TYPE_VIDEO);
//		mRenderParams.setTextureId(textureId);

//
//        mSurfaceTexture = panoView.getSurfaceTexture();

//		mSurfaceTexture = new SurfaceTexture(textureId);
//		panoView.setSurfaceTexture(mSurfaceTexture);
//		panoView.setTexture(textureId);
    }

//    @Override
//    public void onBeforeDraw(boolean isLeft) {
//        if (mRenderParams == null){
//            return;
//        }


//		if (mSurfaceTexture != null && isLeft){
//			try {
//				mSurfaceTexture.updateTexImage();
//			} catch (RuntimeException e) {
//				return;
//			}
//
//		}
//
//		if (mRenderParams == null){
//			return;
//		}
//
//		if (!isVisible() || !isSurfaceCreated){
//			return;
//		}
//
//		GLMatrixState state = getMatrixState();
//
//		state.pushMatrix();
//		getEyeMatrix(state.getVMatrix(), isLeft);

//		GLVideoRect2.getInstance().draw(mRenderParams, getMatrixState());
//    }

//    @Override
//    public void onAfterDraw(boolean isLeft) {
//        if (!isVisible() || !isSurfaceCreated){
//            return;
//        }
////		getMatrixState().popMatrix();
//    }

//    @Override
//    public void draw() {
////        super.draw(left);
//        super.draw();
//    }

    @Override
    public void release() {
        super.release();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        // TODO Auto-generated method stub

    }


    /**
     * 设置播放事件回调
     * @author lixianke  @Date 2015-3-17 下午3:36:01
     * @param listener ISystemPlayerListener回调对象
     * @return
     */
    public void setListener(IGLQiuPlayerListener listener){
        mListener = listener;
    }


    protected void playPrepared(){
        if (mListener != null){
            mListener.onPrepared(this);
        }
//        startRender();
    }

    protected boolean playInfo(int what, Object extra){
        if (mListener != null){
            return mListener.onInfo(this, what, extra);
        }
        return false;
    }

    protected void playCompletion(){
        if (mListener != null){
            mListener.onCompletion(this);
        }
    }

    protected void playSeekComplete(){
        if (mListener != null){
            mListener.onSeekComplete(this);
        }
//        startRender();
    }

    protected void playBufferingUpdate(int percent){
        if (mListener != null){
            mListener.onBufferingUpdate(this, percent);
        }
    }

    protected boolean playError(int what){
        if (mListener != null){
            return mListener.onError(this, what);
        }
        return false;
    }
}

