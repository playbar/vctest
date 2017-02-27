package com.baofeng.mj.videoplugin.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baofeng.mj.videoplugin.R;

/**
 * Created by muyu on 2016/11/3.
 */
public class NetworkDialog  extends Dialog implements View.OnClickListener {

    private Button downloadNowBtn;
    private Button cancelBtn;

    private DialogCallBack mDialogCallBack;

    public NetworkDialog(Context context, DialogCallBack dialogCallBack) {
        super(context, R.style.alertdialog);
        this.mDialogCallBack = dialogCallBack;
        initView();
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(
                R.layout.network_dialog, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.width = PixelsUtil.getWidthPixels() - PixelsUtil.dip2px(40);
        setContentView(v, params);
        setCancelable(false);
        downloadNowBtn = (Button) v.findViewById(R.id.dialog_download);
        cancelBtn = (Button) v.findViewById(R.id.dialog_cancal);
        downloadNowBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_download) {
            if (mDialogCallBack != null) {
                mDialogCallBack.downLoad();
            }
        }else if(id == R.id.dialog_cancal){
            if (mDialogCallBack != null) {
                mDialogCallBack.cancal();
            }
        }
        dismiss();
    }

    public interface DialogCallBack {
        void downLoad();
        void cancal();
    }
}