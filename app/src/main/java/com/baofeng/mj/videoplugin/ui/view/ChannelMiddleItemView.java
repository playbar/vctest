package com.baofeng.mj.videoplugin.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.text.TextUtils;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.bean.ContentInfo;
import com.baofeng.mj.videoplugin.ui.activity.GLBaseActivity;
import com.baofeng.mj.videoplugin.ui.activity.GLIndexActivity;
import com.baofeng.mj.videoplugin.ui.page.PanoNetPlayPage;
import com.baofeng.mj.videoplugin.util.ImageLoaderUtils;
import com.bfmj.viewcore.animation.GLAnimation;
import com.bfmj.viewcore.animation.GLTranslateAnimation;
import com.bfmj.viewcore.entity.LayerInfo;
import com.bfmj.viewcore.entity.TextInfo;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.util.GLExtraData;
import com.bfmj.viewcore.util.HeadControl;
import com.bfmj.viewcore.view.GLImageView;
import com.bfmj.viewcore.view.GLMultiLayerView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLViewPage;

import java.util.ArrayList;

import static com.baofeng.mj.videoplugin.R.mipmap.hengping_list_moren;

/**
 * Created by yushaochen on 2017/2/15.
 */
public class ChannelMiddleItemView extends GLRelativeView {

    private Context mContext;

    private GLMultiLayerView mRootView;

    private GLMultiLayerView playBtn;

    private ContentInfo data;

    private GLImageView glImageView;//边框

    private GLViewPage mPage;

    private ImageLoaderUtils imageLoader;

    public ChannelMiddleItemView(Context context, GLViewPage page) {
        super(context);
        mContext = context;
        mPage = page;
        this.setLayoutParams(368f,225f);

        glImageView = new GLImageView(mContext);
        glImageView.setWidth(434f);
        glImageView.setHeight(299f);
        glImageView.setMargin(-34f,-38f,0,0);
//        glImageView.setBackground(R.drawable.item_biankuang);
        glImageView.setVisible(false);

        mRootView = new GLMultiLayerView(mContext);
        mRootView.setLayoutParams(368f,225f);

        this.addView(glImageView);
        this.addView(mRootView);

        imageLoader = ImageLoaderUtils.getInstance(mContext);

        //添加子view
        createView();
        //创建button
        createButton();

        setListener();
    }

    public void createView() {

        ArrayList<LayerInfo> layerInfos = new ArrayList<>();
        //创建资源图view
        LayerInfo layerInfo = new LayerInfo();
        layerInfo.setType(LayerInfo.LayerType.TYPE_RESOURCE_ID);
        layerInfo.setResourceId(R.mipmap.hengping_list_moren);
        layerInfos.add(layerInfo);
        //创建阴影遮罩
        layerInfo = new LayerInfo();
        layerInfo.setType(LayerInfo.LayerType.TYPE_RESOURCE_ID);
        layerInfo.setResourceId(R.mipmap.hengping_list_text_bj);
        layerInfos.add(layerInfo);
        //创建资源标题view
        layerInfo = new LayerInfo();
        layerInfo.setType(LayerInfo.LayerType.TYPE_TEXT);
        TextInfo textInfo = new TextInfo();
        textInfo.setSize(32);
        textInfo.setColor(new GLColor(0xffffff));
        textInfo.setContent("");
        layerInfo.setTextInfo(textInfo);
        layerInfo.setRect(new Rect(10,180,358,220));
        layerInfos.add(layerInfo);

        mRootView.setLayerInfos(layerInfos);
    }

    public void createButton(){

        playBtn = new GLMultiLayerView(mContext);

        ArrayList<LayerInfo> layerInfos = new ArrayList<>();

        LayerInfo layerInfo = new LayerInfo();
        layerInfo.setType(LayerInfo.LayerType.TYPE_RESOURCE_ID);
        layerInfo.setResourceId(R.mipmap.hengping_list_play);
        layerInfos.add(layerInfo);

        playBtn.setLayerInfos(layerInfos);
        playBtn.setLayoutParams(90f,90f);
        playBtn.setMargin(139f,0,0,-52.5f);
        playBtn.setVisible(false);

        mRootView.addViewBottom(playBtn);

        HeadControl.bindView(playBtn);
    }

    public void setData(ContentInfo data){
        this.data = data;
        refreshView();
    }

    public void refreshView() {

        if(null != data && !TextUtils.isEmpty(data.getPic_url())) {
            imageLoader.loadImage(mContext, data.getPic_url(), new ImageLoaderUtils.LoadComplete() {
                @Override
                public void loadImage(Bitmap bitmap) {
                    if(null != bitmap) {
                        mRootView.getLayerInfos().get(0).setType(LayerInfo.LayerType.TYPE_BITMAP);
                        mRootView.getLayerInfos().get(0).setBitmap(bitmap);
                    } else {
                        mRootView.getLayerInfos().get(0).setType(LayerInfo.LayerType.TYPE_RESOURCE_ID);
                        mRootView.getLayerInfos().get(0).setResourceId(hengping_list_moren);
                    }
                    mRootView.updateUI();
                }
            });
        } else {
            mRootView.getLayerInfos().get(0).setType(LayerInfo.LayerType.TYPE_RESOURCE_ID);
            mRootView.getLayerInfos().get(0).setResourceId(hengping_list_moren);
        }

        if(null != data && !TextUtils.isEmpty(data.getTitle())) {
            String title = data.getTitle();
            int length = 10;
            if (title.length() > length) {
                title = title.substring(0, length) +"...";
            }
            mRootView.getLayerInfos().get(2).getTextInfo().setContent("");
            mRootView.getLayerInfos().get(2).getTextInfo().setContent(title);
        } else {
            mRootView.getLayerInfos().get(2).getTextInfo().setContent("");
        }

        mRootView.updateUI();
    }

    private void setListener() {
        mRootView.setFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView glRectView, final boolean b) {
//                if(null != data) {
                    if(b) {
                        playBtn.setVisible(true);
                    } else {
                        playBtn.setVisible(false);
                    }
                    startTranslate(ChannelMiddleItemView.this ,b);
//                }
            }
        });
        playBtn.setOnKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView glRectView, int keycode) {
                GLExtraData panoData = new GLExtraData();
                panoData.putExtraInt("from", GLIndexActivity.INSIDE);
                panoData.putExtraString("data", data.getUrl());
                ((GLBaseActivity) mContext).getPageManager().push(new PanoNetPlayPage(mContext), panoData);
                return false;
            }

            @Override
            public boolean onKeyUp(GLRectView glRectView, int i) {
                return false;
            }

            @Override
            public boolean onKeyLongPress(GLRectView glRectView, int keycode) {
                return false;
            }
        });


    }


    public void startTranslate(final GLRectView view , boolean focuse){
        if(focuse) {
            glImageView.setVisible(true);
            GLAnimation animation1 = new GLTranslateAnimation(0, 0, -0.2f);
            animation1.setAnimView(view);
            animation1.setDuration(300);
            view.startAnimation(animation1);
        } else {
            GLAnimation animation1 = new GLTranslateAnimation(0, 0, 0.2f);
            animation1.setAnimView(view);
            animation1.setDuration(300);
            view.startAnimation(animation1);
            animation1.setOnGLAnimationListener(new GLAnimation.OnGLAnimationListener() {
                @Override
                public void onAnimationStart(GLAnimation glAnimation) {

                }

                @Override
                public void onAnimationEnd(GLAnimation glAnimation) {
                    view.setDepth(view.getParent().getDepth());
                }
            });
            glImageView.setVisible(false);
        }
    }
}
