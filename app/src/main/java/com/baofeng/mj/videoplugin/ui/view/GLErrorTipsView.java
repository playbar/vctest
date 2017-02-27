package com.baofeng.mj.videoplugin.ui.view;

import android.content.Context;
import android.graphics.Rect;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.bfmj.viewcore.entity.LayerInfo;
import com.bfmj.viewcore.entity.TextInfo;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.util.HeadControl;
import com.bfmj.viewcore.view.GLImageView;
import com.bfmj.viewcore.view.GLMultiLayerView;
import com.bfmj.viewcore.view.GLRectView;

import java.util.ArrayList;

/**
 * Created by yushaochen on 2016/8/3.
 */
public class GLErrorTipsView extends GLMultiLayerView {

    private Context mContext;
    private GLImageView image;
    private boolean isUse = false;

    public GLErrorTipsView(Context context) {
        super(context);
        mContext = context;
        this.setLayoutParams(700f,450f);
        //创建子view
        createView();

        setListener();
    }

    private void createView() {

        ArrayList<LayerInfo> layerInfos = new ArrayList<>();

        LayerInfo layerInfo = new LayerInfo();
        layerInfo.setType(LayerInfo.LayerType.TYPE_RESOURCE_ID);
        layerInfo.setResourceId(R.mipmap.hengping_tips_bg);
        layerInfos.add(layerInfo);

        layerInfo = new LayerInfo();
        layerInfo.setType(LayerInfo.LayerType.TYPE_TEXT);
        TextInfo textInfo = new TextInfo();
        textInfo.setSize(32);
        textInfo.setColor(new GLColor(0xffffff));
        textInfo.setContent("您的网络连接有问题，请稍后再试!");
        layerInfo.setTextInfo(textInfo);
        layerInfo.setRect(new Rect(100,100,600,240));
        layerInfos.add(layerInfo);

        image = new GLImageView(mContext);
        image.setBackground(R.mipmap.hengping_back);
        image.setLayoutParams(90f,90f);
        image.setMargin(305f,340f,305f,0);

        this.setLayerInfos(layerInfos);
        this.addView(image);
        HeadControl.bindView(image);
    }

    private void setListener() {
        image.setOnKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView glRectView, int keycode) {
                switch (keycode) {
                    case MojingKeyCode.KEYCODE_ENTER:
                        GLErrorTipsView.this.setVisible(false);
                        if(isUse && null != mTipsOnKeyListener) {
                            mTipsOnKeyListener.onOk();
                        }
                        break;
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
        });
    }

    public void setTipsOnKeyListener(TipsOnKeyListener tipsOnKeyListener){
        mTipsOnKeyListener = tipsOnKeyListener;
    }

    private TipsOnKeyListener mTipsOnKeyListener;

    public static abstract class TipsOnKeyListener{
        public abstract void onOk();
    }

    public void isUseCallback(boolean isUse){
        this.isUse = isUse;
    }

    public void showBackBtn(boolean isShow) {
        if(null != image) {
            image.setVisible(isShow);
        }
    }

    public void setText(String text) {
        this.getLayerInfos().get(1).getTextInfo().setContent(text);
        this.updateUI();
    }
}
