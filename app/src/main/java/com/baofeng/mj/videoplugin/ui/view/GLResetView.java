package com.baofeng.mj.videoplugin.ui.view;

import android.content.Context;
import android.opengl.Matrix;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.input.base.MojingKeyCode;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLScreenParams;
import com.bfmj.viewcore.view.GLImageView;
import com.bfmj.viewcore.view.GLRectView;

/**
 * Created by lixianke on 2016/7/19.
 */
public class GLResetView extends GLImageView {
    private int width = 124;
    private int height = 124;
    private int padding = 22;

    public GLResetView(Context context) {
        super(context);
        setCostomHeadView(true);
        setLayoutParams( width, height);
        setX((GLScreenParams.getXDpi() - width) / 2);
        setY((GLScreenParams.getYDpi() - height) / 2);
        setPadding(padding, padding, padding, padding);
        setAlpha(0.5f);
        setDepth(3.8f);
        setBackground(R.mipmap.basebar_bg_nom);
        setImage(R.mipmap.icon_reset);
        setFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView glRectView, boolean b) {
                if(b){
                    setAlpha(1f);
                    setBackground(R.mipmap.basebar_bg_high);
                } else {
                    setAlpha(0.5f);
                    setBackground(R.mipmap.basebar_bg_nom);
                }
            }
        });
        setOnKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView glRectView, int keyCode) {
                if (keyCode == MojingKeyCode.KEYCODE_ENTER || keyCode == MojingKeyCode.KEYCODE_DPAD_CENTER){
                    //TODO
                    getRootView().initHeadView();
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

    @Override
    public void onBeforeDraw(boolean isLeft) {
        float[] mtx = new float[16];
        Matrix.setRotateM(mtx, 0, 0.0F, 1.0F, 1.0F, 1.0F);
        float[] angles = {0, 0, 0};
        MojingSDK.getLastHeadEulerAngles(angles);
        Matrix.rotateM(mtx, 0, -(float) Math.toDegrees(angles[2]), 0, 0, 1);
        Matrix.rotateM(mtx, 0, -(float) Math.toDegrees(angles[1]) - 35, 1, 0, 0);

        this.getMatrixState().setVMatrix(mtx);
        super.onBeforeDraw(isLeft);
    }
}
