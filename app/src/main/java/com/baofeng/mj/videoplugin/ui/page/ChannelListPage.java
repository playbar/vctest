package com.baofeng.mj.videoplugin.ui.page;

import android.app.Activity;
import android.content.Context;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.application.AppConfig;
import com.baofeng.mj.videoplugin.application.UrlConfig;
import com.baofeng.mj.videoplugin.bean.ContentInfo;
import com.baofeng.mj.videoplugin.bean.MainSubContentListBean;
import com.baofeng.mj.videoplugin.bean.ResponseBaseBean;
import com.baofeng.mj.videoplugin.receiver.MassageReceiver;
import com.baofeng.mj.videoplugin.receiver.ReceiverManager;
import com.baofeng.mj.videoplugin.ui.activity.GLIndexActivity;
import com.baofeng.mj.videoplugin.ui.adapter.ChannelListMiddleGridAdapter;
import com.baofeng.mj.videoplugin.ui.view.GLErrorTipsView;
import com.baofeng.mj.videoplugin.ui.view.GLToast;
import com.baofeng.mj.videoplugin.ui.view.GLMassageView;
import com.baofeng.mj.videoplugin.util.application.NetworkUtil;
import com.baofeng.mj.videoplugin.util.net.GLVideoApi;
import com.baofeng.mj.videoplugin.util.okhttp.RequestCallBack;
import com.bfmj.viewcore.interfaces.GLOnKeyListener;
import com.bfmj.viewcore.render.GLColor;
import com.bfmj.viewcore.util.GLExtraData;
import com.bfmj.viewcore.util.HeadControl;
import com.bfmj.viewcore.view.GLGridViewPage;
import com.bfmj.viewcore.view.GLImageView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLRelativeView;
import com.bfmj.viewcore.view.GLTextView;
import com.bfmj.viewcore.view.GLViewPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yushaochen on 2017/2/15.
 */
public class ChannelListPage extends GLViewPage {

    private Activity mContext;
    private GLRelativeView indexRootView;//根布局

    private GLGridViewPage middleGridView;

    private ChannelListMiddleGridAdapter middleGridAdapter;

    private GLToast pageTips;//翻页提示

    private GLErrorTipsView errorTipsView;//无网提示

    private GLTextView noResTipsView;//无内容提示

    private String url = "";

    private int total = 0;//当前分类下列表总数
    private int num = 27;//默认每次取3页

    private GLImageView mBackBtn;//返回按钮

    private GLMassageView mMassageView;//按摩模式按钮

    private ArrayList<ContentInfo> datas = new ArrayList<ContentInfo>();

    public ChannelListPage(Context context) {
        super(context);
        mContext = (Activity) context;

        ReceiverManager.getInstance(mContext).setMassageReceiverCallBack(receiver);
    }

    @Override
    protected GLRectView createView(GLExtraData glExtraData) {

        indexRootView = new GLRelativeView(mContext);
        indexRootView.setLayoutParams(GLRectView.MATCH_PARENT,
                GLRectView.MATCH_PARENT);
        indexRootView.setMargin(240f,813f,0,0);
//        indexRootView.setBackground(new GLColor(0xff0000));

        //创建网络错误提示框
        createErrorTipsView();

        //创建GridView列表
        createMiddleView();

        //创建返回按钮
        createBackButton();

        //创建按摩按钮
        createMassageView();

        //加载中部列表数据
        handleUrl(0);

        return indexRootView;
    }

    @Override
    public void onFinish() {
        super.onFinish();
        ReceiverManager.getInstance(mContext).removeMassageReceiverCallBack(receiver);
    }

    private MassageReceiver.ReceiverCallBack receiver = new MassageReceiver.ReceiverCallBack() {
        @Override
        public void onCallBack(int connectStatus, int modeIndex) {
            AppConfig.connectStatus = connectStatus;
            AppConfig.modeIndex = modeIndex;
//            System.out.println("!!!!!!!!!!!!!!------------ChannelListPage---connectStatus:"+connectStatus);
//            System.out.println("!!!!!!!!!!!!!!------------ChannelListPage---modeIndex:"+modeIndex);
            if(null != mMassageView) {
                if(connectStatus == 1) {
                    mMassageView.setVisible(true);
                    mMassageView.setSlected(modeIndex+"");
                } else {
                    mMassageView.setVisible(false);
                    mMassageView.setSlected(GLMassageView.NO_SELECTED);
                }
            }
        }
    };

    private void createMassageView() {
        mMassageView = new GLMassageView(mContext);
        mMassageView.setMargin(1050f,0,0,600f);
        indexRootView.addViewBottom(mMassageView);

        mMassageView.setDatas(AppConfig.modeData);
        mMassageView.setSlected(AppConfig.modeIndex+"");
        if(AppConfig.connectStatus == 0) {
            mMassageView.setVisible(false);
        } else {
            mMassageView.setVisible(true);
        }
    }

    private void createBackButton() {
        mBackBtn = new GLImageView(mContext);
        mBackBtn.setLayoutParams(100f,100f);
        mBackBtn.setBackground(R.mipmap.hengping_back);
        mBackBtn.setMargin(870f,0,0,600f);
        mBackBtn.setOnKeyListener(new GLOnKeyListener() {
            @Override
            public boolean onKeyDown(GLRectView view, int keycode) {
                ((GLIndexActivity) getContext()).finish();
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
        HeadControl.bindView(mBackBtn);
        indexRootView.addViewBottom(mBackBtn);
    }

    private void createErrorTipsView() {
        errorTipsView = new GLErrorTipsView(mContext);
        errorTipsView.setMargin(700f,120f,0,0);
        errorTipsView.setDepth(3.6f);
        errorTipsView.setVisible(false);
        errorTipsView.setTipsOnKeyListener(new GLErrorTipsView.TipsOnKeyListener(){
            @Override
            public void onOk() {
                ((GLIndexActivity) getContext()).finish();
            }
        });
        indexRootView.addView(errorTipsView);
    }

    private void createMiddleView() {

        createMiddleNoResTipsView();

        middleGridView = new GLGridViewPage(mContext);
        middleGridView.setLayoutParams(368f*3+15f*2,705f);
        middleGridView.setNumColumns(3);
        middleGridView.setNumRows(3);
        middleGridView.setVerticalSpacing(15f);
        middleGridView.setHorizontalSpacing(15f);
        middleGridView.setMargin(230f,0,0,0);

        middleGridView.setBottomSpaceing(80f);//设置翻页和gridview之间的距离
        middleGridView.setNumDefaultColor(R.mipmap.flip_page_bg);
        middleGridView.setNumSelectedColor(R.mipmap.flip_page_bg_highlight);
        middleGridView.setFlipLeftIcon(R.mipmap.flip_leftarrow);
        middleGridView.setFlipRightIcon(R.mipmap.flip_rightarrow);
        middleGridView.setHeadControl(true);
//        middleGridView.setBackground(new GLColor(0x00ff00));

        middleGridAdapter = new ChannelListMiddleGridAdapter(mContext,this);

        middleGridView.setAdapter(middleGridAdapter);

        indexRootView.addView(middleGridView);

        createMiddleGridTipsView();

        setMiddleGridViewListener();
    }

    public void createMiddleNoResTipsView(){
        noResTipsView = new GLTextView(mContext);
        noResTipsView.setLayoutParams(368f*3+15f*2,705f);
        noResTipsView.setAlignment(GLTextView.ALIGN_CENTER);
        noResTipsView.setTextSize(32);
        noResTipsView.setTextColor(new GLColor(0xffffff));
        noResTipsView.setMargin(460f,0,0,0);
        noResTipsView.setVisible(false);
        noResTipsView.setPadding(0,300f,0,0);
        indexRootView.addView(noResTipsView);
    }

    private void setMiddleGridViewListener() {
        middleGridView.setPrvPageChange(new GLGridViewPage.PageChangeListener() {
            @Override
            public void onPageChange() {
                moveToLeft();
            }
        });
        middleGridView.setNextPageChange(new GLGridViewPage.PageChangeListener() {
            @Override
            public void onPageChange() {
                moveToRight();
            }
        });
    }

    public void createMiddleGridTipsView(){
        pageTips = new GLToast(mContext);
        pageTips.setMargin(800f,180f,0,0);
        indexRootView.addViewOf(pageTips,null,null,null,middleGridView);
    }

    public void handleUrl(int start){
        //右侧筛选加载中部数据
            String reqUrl = UrlConfig.getListUrl(start, num);;
            loadingMiddleData(reqUrl,start);
    }

    public void loadingMiddleData(String reqUrl,final int start){

        new GLVideoApi().getMiddleList(reqUrl, new RequestCallBack<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
            @Override
            public void onSuccess(ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> result) {
                super.onSuccess(result);
                errorTipsView.setVisible(false);
                errorTipsView.isUseCallback(false);
                if (result.getStatus() == 0) {
                    setMiddleData(result, start);
                }
                lock = false;
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                lock = false;
                if(!NetworkUtil.isNetworkConnected(mContext)) {
                    errorTipsView.setVisible(true);
                    errorTipsView.isUseCallback(true);
                }
            }
        });

    }

    public void setMiddleData(ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> response,int start){
        if(null != response && null != response.getData()) {
            if(start <= 0) {//取第一页数据
                middleGridView.resetPage();
                datas.clear();

                if(null != response.getData().getList() && response.getData().getList().size() > 0) {
                    noResTipsView.setText("");
                    noResTipsView.setVisible(false);
                } else {
                    noResTipsView.setVisible(true);
                    noResTipsView.setText("暂无资源!");
                }
            }
            if(null != response.getData().getList()) {
                datas.addAll(response.getData().getList());
            }

            total = response.getData().getTotal();
            middleGridView.setTotalCount(total);//没有翻到服务器最后一页数据时，向右翻页箭头继续显示

            middleGridAdapter.setData(datas);

            //当是翻页追加数据时，追加回来后自动向下翻一页
            if(start > 0){
                //判断是否翻到已有数据的最后一页
                if(!middleGridView.isLastPage()) {
                    pageTips.setVisible(false);
                    //执行翻页
                    middleGridView.nextPage();
                }
            }
        }
    }

    //向左翻一页
    public void moveToLeft(){
        if(null == datas || datas.size() == 0) {
            return;
        }

        if(!middleGridView.isFirstPage()) {
            pageTips.setVisible(false);
            //执行翻页
            middleGridView.previousPage();
        } else {
            pageTips.showToast("已是第一页啦");
        }

    }

    private boolean lock = false;//翻页锁，防止多次连续执行，导致重复添加数据

    //向右翻一页
    public void moveToRight(){

        if(null == datas || datas.size() == 0) {
            return;
        }

        //判断是否翻到已有数据的最后一页
        if(!middleGridView.isLastPage()) {
            pageTips.setVisible(false);
            //执行翻页
            middleGridView.nextPage();
        } else {
            if(total <= datas.size()) {//服务器没有数据了
                pageTips.showToast("已是最后一页啦");
                //tips.setVisible(true);

            } else {//去服务器数据
                if(NetworkUtil.isNetworkConnected(mContext)) {
                    if(!lock) {
                        lock = true;
                        handleUrl(datas.size());
                    }
                } else {
                    if(!errorTipsView.isVisible()) {
                        errorTipsView.setVisible(true);
                        errorTipsView.isUseCallback(false);
                    }
                }
            }
        }

    }

}
