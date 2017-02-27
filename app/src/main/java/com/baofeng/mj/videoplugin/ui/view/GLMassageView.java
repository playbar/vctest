package com.baofeng.mj.videoplugin.ui.view;

import android.content.Context;
import android.content.Intent;

import com.baofeng.mj.videoplugin.R;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.interfaces.GLViewFocusListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.render.GLConstant;
import com.bfmj.viewcore.util.HeadControl;
import com.bfmj.viewcore.view.GLLinearView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLTextView;

/**
 * Created by yushaochen on 2017/2/20.
 */

public class GLMassageView extends GLRelativeView{

    private Context mContext;

    private String[] mModeData;

    private GLLinearView itemView;

    private float mY;

    private String slectedId = NO_SELECTED;

    public static final String NO_SELECTED = "-1";

    private static final String MASSAGE_STATUS_ACTION = "com.baofeng.mj.videoplugin.action.MASSAGE";

    public GLMassageView(Context context) {
        super(context);
        mContext = context;
        //this.setLayoutParams(150f,(mMassageTexts.length+1)*100f+mMassageTexts.length*2f);
//        this.setBackground(new GLColor(0xff0000));
        this.setLayoutParams(150f,100f);
        setListener();
    }

    public void setDatas(String[] modeData) {
        //重置数据
        slectedId = NO_SELECTED;
        removeAllView();
        //添加新数据
        if(null != modeData && modeData.length > 0) {
            mModeData = modeData;
            //创建选项按钮
            createItemView();
            //创建显示控制按钮
            createBottomView();
        }
    }

    public void setSlected(String id) {
        if(null != mModeData && mModeData.length > 0) {
            if (!NO_SELECTED.equals(id)) {
                refreshItemView(id);
            } else {
                clearSlected();
            }
        }
    }

    private void refreshItemView(String id) {
        if(null == itemView) {
            return;
        }
        if(slectedId.equals(id)) {
            itemView.getView(id).setBackground(R.mipmap.hengping_massage_btn_bg);
            slectedId = NO_SELECTED;
        } else {
            clearSlected();
            itemView.getView(id).setBackground(R.mipmap.hengping_massage_selected_btn_bg);
            slectedId = id;
        }
    }

    private void setListener() {
        this.setFocusListener(new GLViewFocusListener() {
            @Override
            public void onFocusChange(GLRectView view, boolean focused) {
                if(null == mModeData) {
                    return;
                }
                if(focused) {
                    mY = getY();
                    setLayoutParams(150f,(mModeData.length+1)*100f+mModeData.length*2f);
                    itemView.setVisible(true);
                    setY(mY-itemView.getHeight());
                } else {
                    setLayoutParams(150f,100f);
                    itemView.setVisible(false);
                    setY(mY);
                }
            }
        });
    }

    private GLTextView bottomView;

    private void createBottomView() {
        bottomView = new GLTextView(mContext);
        bottomView.setLayoutParams(150f,100f);
        bottomView.setTextSize(30);
        bottomView.setTextColor(new GLColor(0xffffff));
        bottomView.setText("按摩模式");
        bottomView.setPadding(15,25,0,0);
        bottomView.setBackground(R.mipmap.hengping_massage_btn_bg);
        this.addViewBottom(bottomView);
    }

    private void createItemView() {
        itemView = new GLLinearView(mContext);
        itemView.setLayoutParams(150f,mModeData.length*100f+mModeData.length*2f);
        itemView.setOrientation(GLConstant.GLOrientation.VERTICAL);
        //添加按摩操作按钮
        createMassageView();
        this.addViewTop(itemView);
        itemView.setVisible(false);
    }

    private void createMassageView() {
        for(int x = 0; x < mModeData.length; x++) {
            GLTextView textView = new GLTextView(mContext);
            textView.setLayoutParams(150f,100f);
            textView.setTextSize(30);
            textView.setTextColor(new GLColor(0xffffff));
            textView.setText(mModeData[x]);
            textView.setMargin(0,0,0,2);
            textView.setPadding(15,25,0,0);
            textView.setBackground(R.mipmap.hengping_massage_btn_bg);
            textView.setId(x+"");
            textView.setOnKeyListener(new GLOnKeyListener() {
                @Override
                public boolean onKeyDown(GLRectView view, int keycode) {
//                    if(slectedId.equals(view.getId())) {
//                        view.setBackground(new GLColor(0x2b3239));
//                        slectedId = "";
//                    } else {
//                        clearSlected();
//                        view.setBackground(new GLColor(0xff355177));
//                        slectedId = view.getId();
//                    }
                    Intent intent = new Intent(MASSAGE_STATUS_ACTION);
                    intent.putExtra("modeIndex",Integer.parseInt(view.getId()));
                    mContext.sendBroadcast(intent);

                    refreshItemView(view.getId());
                    return false;
                }

                @Override
                public boolean onKeyUp(GLRectView view, int keycode) {
                    return false;
                }

                @Override
                public boolean onKeyLongPress(GLRectView view, int keycode) {
                    return false;
                }
            });
            HeadControl.bindView(textView);
            itemView.addView(textView);
        }
    }

    private void clearSlected() {
        slectedId = NO_SELECTED;
        for(GLRectView childView : itemView.getChildView()) {
            childView.setBackground(R.mipmap.hengping_massage_btn_bg);
        }
    }
}
