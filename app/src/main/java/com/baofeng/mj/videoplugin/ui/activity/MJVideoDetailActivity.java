package com.baofeng.mj.videoplugin.ui.activity;


import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.application.AppConfig;
import com.baofeng.mj.videoplugin.bean.DetailBean;
import com.baofeng.mj.videoplugin.bean.ReportDetailPvBean;
import com.baofeng.mj.videoplugin.download.DownloadUtil;
import com.baofeng.mj.videoplugin.interfaces.IPlayStopListener;
import com.baofeng.mj.videoplugin.ui.view.PanoramVideoPlayerView;
import com.baofeng.mj.videoplugin.ui.view.PlayerBottomView;
import com.baofeng.mj.videoplugin.ui.view.ProgressBarView;
import com.baofeng.mj.videoplugin.util.FileUtils;
import com.baofeng.mj.videoplugin.util.IMjPlayAPI;
import com.baofeng.mj.videoplugin.util.application.NetworkUtil;
import com.baofeng.mj.videoplugin.util.application.PixelsUtil;
import com.baofeng.mj.videoplugin.util.report.ReportBusiness;

/**
 * Created by panxin on 2016/11/1.
 */
public class MJVideoDetailActivity extends MJBaseActivity implements View.OnClickListener{

//    private ProgressBarView view_progressbar;
    private PanoramVideoPlayerView view_playerview;
    private PlayerBottomView view_bottomview;
    private ImageView imageview_back;
    private TextView detail_name_textview;
    private TextView detail_grade_textview;
//    private TextView textview_size;
    private TextView detail_source_textview;
    private TextView detail_desc_textview;
    private LinearLayout layout_play_view;
    private ImageView imageview_play;
    private LinearLayout layout_play_download;
    private LinearLayout layout_play_open;
    private TextView textview_play_download;
    private TextView textview_play_open;
    private String url;
    private String resid,title,frompage;
    private String videoUrl;
    private TextView textview_reset;
    private RelativeLayout layout_no_network;
    private ImageView imageview_no_network_back;
    private ImageView openMojingAd;
    private ProgressBarView progressBarView;

    private DetailBean mBean;

    @Override
    public void initView() {
        setContentView(R.layout.activity_video_detail);
//        view_progressbar = (ProgressBarView) findViewById(R.id.view_progressbar);
        view_playerview = (PanoramVideoPlayerView) findViewById(R.id.view_playerview);
        view_bottomview = (PlayerBottomView) findViewById(R.id.view_bottomview);
        imageview_back = (ImageView) findViewById(R.id.imageview_back);
//        textview_instructions_message = (TextView) findViewById(R.id.textview_instructions_message);
        detail_name_textview = (TextView) findViewById(R.id.detail_name_textview);
        detail_grade_textview = (TextView) findViewById(R.id.detail_grade_textview);
//        textview_size = (TextView) findViewById(R.id.textview_size);
        detail_source_textview = (TextView) findViewById(R.id.detail_source_textview);
        detail_desc_textview = (TextView) findViewById(R.id.detail_desc_textview);
        layout_play_view = (LinearLayout) findViewById(R.id.layout_play_view);
        imageview_play = (ImageView) findViewById(R.id.imageview_play);
        layout_play_download = (LinearLayout) findViewById(R.id.layout_play_download);
        layout_play_open = (LinearLayout) findViewById(R.id.layout_play_open);
        textview_play_download = (TextView) findViewById(R.id.textview_play_download);
        textview_play_download.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        textview_play_open = (TextView) findViewById(R.id.textview_play_open);
        textview_play_open.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        view_playerview.setBottomView(view_bottomview);
        view_bottomview.setParentView(view_playerview);
        view_playerview.setDoubleScreen(false);
        layout_no_network = (RelativeLayout)findViewById(R.id.layout_no_network);
        textview_reset = (TextView)findViewById(R.id.textview_reset);
        imageview_no_network_back = (ImageView)findViewById(R.id.imageview_no_network_back);

        RelativeLayout playLayout = (RelativeLayout) findViewById(R.id.layout_player);
        int width = PixelsUtil.getWidthPixels(this);
        int height = (int) (width /10f * 9f);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
        playLayout.setLayoutParams(params);

        openMojingAd = (ImageView) findViewById(R.id.detail_open_mojing_ad);
        openMojingAd.setOnClickListener(this);

        progressBarView = (ProgressBarView) findViewById(R.id.detail_download_btn);

    }

    @Override
    public void initData() {
        Intent in = getIntent();
        Bundle bd = in.getBundleExtra(AppConfig.KEY_INTENT_BUNDLE);
        url = bd.getString(AppConfig.KEY_INTENT_URL);
        resid = bd.getString(AppConfig.KEY_INTENT_RESID);
        title = bd.getString(AppConfig.KEY_INTENT_TITLE);
        frompage = bd.getString(AppConfig.KEY_INTENT_FROMPAGE);
        view_bottomview.setUrl(url);
        DownloadUtil.getInstance(this).setVideoIDAndTitle(resid, title);
        if(!NetworkUtil.isNetworkConnected(this)){
            layout_no_network.setVisibility(View.VISIBLE);
            return;
        }
        reportDetailPv(resid, title, frompage);


        IMjPlayAPI.getInstance(this).requestDetail(url, new IMjPlayAPI.DetailRequestCallBack() {
            @Override
            public void isSuccess(boolean status, DetailBean bean) {
                refreshData(bean);
            }
        });


    }


    @Override
    public void initListener() {

        imageview_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageview_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoUrl != null) {
                    view_playerview.setVideoPath(videoUrl);
                    layout_play_view.setVisibility(View.GONE);
                }

            }
        });

        view_bottomview.setIPlayStopListener(new IPlayStopListener() {
            @Override
            public void stopPlay() {
                view_playerview.releasePlay();
                view_bottomview.setVisibility(View.GONE);
                refreshPlayLayout();
            }
        });

        textview_play_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadUtil.clickPostion = DownloadUtil.CLICK_PREV;
//                view_progressbar.performClick();
            }
        });

        textview_play_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadUtil.clickPostion = DownloadUtil.CLICK_PREV;
                progressBarView.performClick();
            }
        });

        layout_no_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        textview_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetworkConnected(MJVideoDetailActivity.this)) {
                    Toast.makeText(MJVideoDetailActivity.this, getString(R.string.mj_string_network_not_connection), Toast.LENGTH_SHORT).show();
                    return;
                }
                layout_no_network.setVisibility(View.GONE);
                reportDetailPv(resid, title, frompage);

                IMjPlayAPI.getInstance(MJVideoDetailActivity.this).requestDetail(url, new IMjPlayAPI.DetailRequestCallBack() {
                    @Override
                    public void isSuccess(boolean status, DetailBean bean) {
                        refreshData(bean);
                    }
                });
                DownloadUtil.getInstance(MJVideoDetailActivity.this).refreshStatus();
            }
        });

        imageview_no_network_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void refreshData(DetailBean bean) {
//        refreshInstructions();
        this.mBean = bean;
        if (bean != null) {
            videoUrl = bean.video_attrs.play_url;
            detail_name_textview.setText(bean.title);
            detail_grade_textview.setText(bean.score+"分");
//            textview_size.setText(getString(R.string.mj_string_detail_panorama_video) + bean.video_attrs.size);
            detail_source_textview.setText(getString(R.string.mj_string_detail_video_source) + bean.source);
            detail_desc_textview.setText(bean.desc);
            view_playerview.setSenceAndMode(Integer.parseInt(bean.is_panorama), Integer.parseInt(bean.video_dimension));
            if (NetworkUtil.isWIFIConnected(this)) {
                //底部play显示 wifi情况下自动播放，非wifi情况下，显示播放状态
                view_playerview.setVideoPath(bean.video_attrs.play_url);
                view_bottomview.setPlayBtnStatus(false);
            } else {
                layout_play_view.setVisibility(View.VISIBLE);
                view_bottomview.setPlayBtnStatus(true);
            }

        } else {
            layout_play_view.setVisibility(View.VISIBLE);
            detail_name_textview.setText(getString(R.string.mj_string_detail_no_know));
//            textview_size.setText(getString(R.string.mj_string_detail_panorama_video) + getString(R.string.mj_string_detail_no_know));
            detail_source_textview.setText(getString(R.string.mj_string_detail_video_source) + getString(R.string.mj_string_detail_no_know));
            detail_desc_textview.setText(getString(R.string.mj_string_detail_no_know));
        }


    }

    private void refreshPlayLayout(){
        if (FileUtils.isAppInstalled(MJVideoDetailActivity.this, AppConfig.MJ_PACKAGE_NAME)) {
            layout_play_open.setVisibility(View.VISIBLE);
            layout_play_download.setVisibility(View.GONE);
        } else {
            layout_play_download.setVisibility(View.VISIBLE);
            layout_play_open.setVisibility(View.GONE);
        }
    }

//    private void refreshInstructions(){
//        if (FileUtils.isAppInstalled(this, AppConfig.MJ_PACKAGE_NAME)) {
//            textview_instructions_message.setText(getString(R.string.mj_string_detail_instructions_installed));
//        } else {
//            textview_instructions_message.setText(getString(R.string.mj_string_detail_instructions_no_install));
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if(view_bottomview.getVisibility() == View.GONE){
            refreshPlayLayout();
        }
        view_playerview.resumeView();
//        DownloadUtil.getInstance().addView(view_progressbar, ProgressBarView.PAGE_DETAIL);
        DownloadUtil.getInstance(MJVideoDetailActivity.this).refreshStatus();
//        refreshInstructions();
        DownloadUtil.getInstance(MJVideoDetailActivity.this).addView(progressBarView, ProgressBarView.PAGE_DETAIL);
        DownloadUtil.getInstance(MJVideoDetailActivity.this).refreshStatus();
    }


    @Override
    protected void onPause() {
        super.onPause();
        view_playerview.pauseView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view_playerview.destroyView();
    }


    private void reportDetailPv(String resId, String title, String frompage) {
        ReportDetailPvBean bean = new ReportDetailPvBean();
        bean.setEtype("pv");
        bean.setPagetype("bf_detail");
        if (frompage != null && "bf_vrlist".equals(frompage)) {
            bean.setFrompage("bf_vrlist");
        }
        bean.setVideoid(resId);
        bean.setTitle(title);
        ReportBusiness.getInstance().reportClick(bean);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.detail_open_mojing_ad){ //下载打开 竖屏详情页-中部按钮按钮引导
            DownloadUtil.clickPostion = DownloadUtil.CLICK_MIDDLE;
            DownloadUtil.getInstance(MJVideoDetailActivity.this).onlyDownLoadInstall(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppConfig.OPEN_VR_CODE){
            if (mBean != null && mBean.video_attrs.play_url != null) {
                this.videoUrl = mBean.video_attrs.play_url;
                view_playerview.initPlayer(this);
                layout_play_view.setVisibility(View.GONE);
                layout_play_download.setVisibility(View.GONE);
                view_bottomview.setVisibility(View.VISIBLE);
                view_playerview.setDoubleScreen(false);
                view_bottomview.clearCurrentTime();
            }
            refreshData(mBean);
        }
    }
}
