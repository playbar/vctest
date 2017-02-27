package com.baofeng.mj.videoplugin.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.util.DensityUtil;

/**
 * 按钮进度条样式view
 *
 * @author panxin
 * @data 2016-11-02
 */
public class ProgressBarView extends View {

    private Paint paint;

    private int width;
    private int height;
    private int percent = 0;

    private int oldBackGroundColor;
    private int openOldBackGroundColor;
    private int openNewBackGroundColor;
    private int oldColor;
    private int newColor;
    private int rx = 20;
    private int ry = 20;

    private boolean isPress = false;

    public static final int TYPE_UNDOWNLOAD = 0;
    public static final int TYPE_DOWNLOADING = 1;
    public static final int TYPE_PAUSE = 2;
    public static final int TYPE_UNINSTALL = 3;
    public static final int TYPE_INSTALLED = 4;
    private int type = TYPE_UNDOWNLOAD;
    public static final int PAGE_DETAIL = 0;
    public static final int PAGE_LIST = 1;
    private int page = PAGE_DETAIL;
    private Paint textPaint;
    private String downloadText = "下载";
    private int textWidth;
    private int textHeight;
    private int textColorWhite;
    private int textColorBlack;
    private Context mContext;
    private boolean mIsShowProgress;

    public ProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true); // 消除锯齿
        paint.setStyle(Paint.Style.FILL);
//        paint.setStrokeWidth(4);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(DensityUtil.dip2px(mContext, 16));

        oldBackGroundColor = Color.rgb(231, 234, 238);//灰色
        openOldBackGroundColor = Color.rgb(255, 127, 49);//橙色
        openNewBackGroundColor = Color.rgb(243, 85, 3);//深橙色
        oldColor = Color.rgb(0, 141, 222);//蓝色
        newColor = Color.rgb(29,106, 235);//深蓝色

        textColorWhite = Color.rgb(255, 255, 255);
        textColorBlack = Color.rgb(0, 0, 0);
    }

    public void setProgress(int percent) {
        this.percent = percent;
        downloadText = percent + "%";
        type = TYPE_DOWNLOADING;
        if (percent == 0) {
            setType(page, type);
        }

        invalidate();
    }

    public void setShowProgress(boolean isShowProgress){
        this.mIsShowProgress = isShowProgress;
    }

    /**
     * 荣泰插件需求：首页按钮写死成安装，详情页面写死成下载
     * @param page
     * @param type
     */
    public void setType(int page, int type) {
        this.page = page;
        this.type = type;
        switch (page){
            case PAGE_DETAIL:
                paint.setStyle(Paint.Style.STROKE);
                downloadText = mContext.getResources().getString(R.string.download_app);
                break;
            case PAGE_LIST:
                paint.setStyle(Paint.Style.FILL);
                downloadText = mContext.getResources().getString(R.string.install_app);
                break;
        }
        Rect rect = new Rect();
        textPaint.getTextBounds(downloadText, 0, downloadText.length(), rect);
        textWidth = rect.width();
        textHeight = rect.height();
        invalidate();
    }


    @Override
    public void setPressed(boolean isPress) {
        super.setPressed(isPress);
        this.isPress = isPress;
        postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(page == PAGE_LIST) {
            drawRectList(canvas);
            drawTextList(canvas);
        }else {
            drawRectDetail(canvas);
            drawTextDetail(canvas);
        }
    }

    private void drawRectList(Canvas canvas){
        switch (type) {
            case TYPE_UNDOWNLOAD:
            case TYPE_UNINSTALL:
            case TYPE_INSTALLED:
                drawRectInstalled(canvas);
                break;
            case TYPE_DOWNLOADING:
                //TODO 是否点击过，点击过，正常走loading状态，没点击过，走安装状态
                if(mIsShowProgress) {
                    drawRectDownloading(canvas);
                }else {
                    drawRectInstalled(canvas);
                }
                break;
            case TYPE_PAUSE:
                drawRectDownloading(canvas); //列表页面暂停显示进度
                break;
        }
    }

    private void drawRectDetail(Canvas canvas){
        switch (type) {
            case TYPE_UNDOWNLOAD:
            case TYPE_UNINSTALL:
            case TYPE_INSTALLED:
            case TYPE_DOWNLOADING:
            case TYPE_PAUSE:
                drawRectInstalledDetail(canvas);//详情页面暂停不显示进度
                break;
        }
    }

    private void drawTextList(Canvas canvas){
        if (downloadText == null) {
            return;
        }
        switch (type) {
            case TYPE_UNDOWNLOAD:
            case TYPE_PAUSE:
            case TYPE_UNINSTALL:
            case TYPE_INSTALLED:
                textPaint.setColor(textColorWhite);
                break;
            case TYPE_DOWNLOADING:
                downloadText = mContext.getResources().getString(R.string.install_app);
                textPaint.setColor(textColorWhite);
                break;
        }
        canvas.drawText(downloadText, width / 2f - textWidth / 2f, height / 2 + textHeight / 2 - 10, textPaint);
    }

    private void drawTextDetail(Canvas canvas){
        if (downloadText == null) {
            return;
        }
        switch (type) {
            case TYPE_UNDOWNLOAD:
            case TYPE_PAUSE:
            case TYPE_UNINSTALL:
            case TYPE_INSTALLED:
                textPaint.setColor(oldColor);
                break;
            case TYPE_DOWNLOADING:
                textPaint.setColor(oldColor);
                break;
        }
        canvas.drawText(downloadText, width / 2f - textWidth / 2f, height / 2 + textHeight / 2 - 10, textPaint);
    }


    /**
     * 未下载和未安装
     *
     * @param canvas
     */
    private void drawRectInstalled(Canvas canvas) { //橙色
        if (!isPress) {
            paint.setColor(openOldBackGroundColor);
        } else {
            paint.setColor(openNewBackGroundColor);
        }
        canvas.drawRoundRect(
                new RectF(0, 0, width, height), rx, ry,
                paint);
    }

    private void drawRectInstalledDetail(Canvas canvas) { //蓝色
        if (!isPress) {
            paint.setColor(oldColor);
        } else {
            paint.setColor(newColor);
        }
        canvas.drawRoundRect(
                new RectF(0, 0, width , height), rx, ry,
                paint);
    }

    /**
     * 下载中
     *
     * @param canvas
     */
    private void drawRectDownloading(Canvas canvas) {
        if (!isPress) {
            paint.setColor(oldBackGroundColor);
            canvas.drawRoundRect(new RectF(0, 0, width, height), rx, ry, paint);
//            paint.setColor(oldColor);
            paint.setColor(openOldBackGroundColor);
            canvas.drawRoundRect(
                    new RectF(0, 0, percent / 100f * width, height), rx, ry,
                    paint);
        } else {
            paint.setColor(oldBackGroundColor);
            canvas.drawRoundRect(new RectF(0, 0, width, height), rx, ry, paint);
            paint.setColor(newColor);
            paint.setColor(openNewBackGroundColor);
            canvas.drawRoundRect(
                    new RectF(0, 0, percent / 100f * width, height), rx, ry,
                    paint);
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

}
