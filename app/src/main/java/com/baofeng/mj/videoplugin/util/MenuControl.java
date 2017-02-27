package com.baofeng.mj.videoplugin.util;

import android.content.Context;
import android.opengl.Matrix;

import com.baofeng.mojing.MojingSDK;
import com.bfmj.viewcore.render.GLScreenParams;
import com.bfmj.viewcore.view.GLImageView;

/**
 * Created by huangliang on 2016/9/29.
 */
public class MenuControl extends GLImageView {
    private int width =(int) GLScreenParams.getYDpi();
    private int height = 600;

    public MenuControl(Context context) {
        super(context);
        setCostomHeadView(true);
        setLayoutParams( width, height);
        setX(0);
        setY(GLScreenParams.getYDpi()-(GLScreenParams.getYDpi() / 3));
        setDepth(3.8f);
        // setAlpha(0.2f);
        // setBackground(new GLColor(0xff0000));
    }

    @Override
    public void onBeforeDraw(boolean isLeft) {
        float[] mtx = new float[16];
        Matrix.setRotateM(mtx, 0, 0.0F, 1.0F, 1.0F, 1.0F);
        float[] angles = {0, 0, 0};
        MojingSDK.getLastHeadEulerAngles(angles);
        setyAngle((float) Math.toDegrees(angles[0]));
        Matrix.rotateM(mtx, 0, -(float) Math.toDegrees(angles[2]), 0, 0, 1);
        Matrix.rotateM(mtx, 0, -(float) Math.toDegrees(angles[1]) - 20, 1, 0, 0);

        this.getMatrixState().setVMatrix(mtx);
        super.onBeforeDraw(isLeft);
    }

    public float yAngle = 0;

    public float getyAngle() {
        return yAngle;
    }

    public void setyAngle(float yAngle) {
        this.yAngle = yAngle;
    }
}


