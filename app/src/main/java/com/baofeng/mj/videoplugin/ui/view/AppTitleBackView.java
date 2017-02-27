package com.baofeng.mj.videoplugin.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.application.AppConfig;
import com.baofeng.mj.videoplugin.ui.activity.GLIndexActivity;

/**
 * Created by muyu on 2016/11/1.
 */
public class AppTitleBackView extends FrameLayout implements View.OnClickListener{
    private Context mContext;
    private View rootView;
    private ImageView backImgBtn;
    private TextView nameTV;
    private ImageButton goVrImageBtn;
    private TextView connectStatusTV;
    private ImageView statusLightIV;

    public AppTitleBackView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public AppTitleBackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView(){
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_app_back_title, this);
        backImgBtn = (ImageView) rootView.findViewById(R.id.app_title_back_imagebtn);
        nameTV = (TextView) rootView.findViewById(R.id.app_title_name);
        backImgBtn.setOnClickListener(this);

        goVrImageBtn = (ImageButton) findViewById(R.id.app_title_go_vr);
        goVrImageBtn.setOnClickListener(this);
        if(AppConfig.CAN_GOVR){
            goVrImageBtn.setVisibility(View.VISIBLE);
        }
        connectStatusTV = (TextView) findViewById(R.id.app_connect_status);
        statusLightIV = (ImageView) findViewById(R.id.app_connect_status_light);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.app_title_back_imagebtn) {
            ((Activity)mContext).finish();
        } else if(i == R.id.app_title_go_vr){
            Intent intent = new Intent(mContext,GLIndexActivity.class);
            intent.putExtra("toPage",0);
            mContext.startActivity(intent);
        }
    }

    public TextView getNameTV() {
        return nameTV;
    }

    public void setNameTV(TextView nameTV) {
        this.nameTV = nameTV;
    }

    public ImageView getBackImgBtn(){
        return backImgBtn;
    }

    //连接绿色，未连接红色
    public void unConnectZkey() {
        statusLightIV.setImageDrawable(getResources().getDrawable(R.mipmap.handle_red));
        connectStatusTV.setText("按摩椅未连接");
    }

    public void connectZkey() {
        statusLightIV.setImageDrawable(getResources().getDrawable(R.mipmap.handle_green));
        connectStatusTV.setText("按摩椅已连接");
    }

}
