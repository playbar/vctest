package com.baofeng.mj.videoplugin.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.application.AppConfig;
import com.baofeng.mj.videoplugin.application.UrlConfig;
import com.baofeng.mj.videoplugin.bean.ContentInfo;
import com.baofeng.mj.videoplugin.bean.MainSubContentListBean;
import com.baofeng.mj.videoplugin.bean.ReportClickBean;
import com.baofeng.mj.videoplugin.bean.ReportListPvBean;
import com.baofeng.mj.videoplugin.bean.ResponseBaseBean;
import com.baofeng.mj.videoplugin.download.DownloadUtil;
import com.baofeng.mj.videoplugin.receiver.MassageReceiver;
import com.baofeng.mj.videoplugin.receiver.ReceiverManager;
import com.baofeng.mj.videoplugin.ui.view.AppTitleBackView;
import com.baofeng.mj.videoplugin.ui.view.ProgressBarView;
import com.baofeng.mj.videoplugin.ui.view.VideoH2Adapter;
import com.baofeng.mj.videoplugin.util.GlassesManager;
import com.baofeng.mj.videoplugin.util.application.NetworkUtil;
import com.baofeng.mj.videoplugin.util.net.MainApi;
import com.baofeng.mj.videoplugin.util.okhttp.RequestCallBack;
import com.baofeng.mj.videoplugin.util.report.ReportBusiness;
import com.baofeng.mojing.MojingSDK;
import com.baofeng.mojing.sdk.download.utils.MjDownloadSDK;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.List;

public class MainActivity extends MJBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2<GridView> {

    private AppTitleBackView appTitleBackView;
    private PullToRefreshGridView pullToRefreshGridView;
    private GridView videoH2GridView;
    private VideoH2Adapter adapter;
    private ProgressBarView progressBarView;
    private static int NUM_PER_PAGE = 12;//每页显示12条数据
    private int totalNum = 0;//数据总量
    private int currentPage = 0; //当前页数
    private TextView textview_reset;
    private RelativeLayout layout_no_network;
    private ImageView imageview_no_network_back;
    private Button bottomBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initLib();
        super.onCreate(savedInstanceState);
        DownloadUtil.getInstance(this).init();

        ReceiverManager.getInstance(this).initAllReceiver();
        getIntentDate();

        ReceiverManager.getInstance(this).setMassageReceiverCallBack(receiver);

    }

    private void initLib(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MojingSDK.Init(MainActivity.this);
                String key = GlassesManager.getInstance(MainActivity.this).getKeyFromIds(AppConfig.COMPANY_ID,AppConfig.PRODUCT_ID,AppConfig.LENS_ID);
                if(!TextUtils.isEmpty(key)) {
                    AppConfig.GLASSES_KEY = key;
                } else {
                    AppConfig.GLASSES_KEY = "";
                }
            }
        }).start();
//        MojingSDKReport.openActivityDurationTrack(false);
        /**
         * 初始化下载sdk
         */
        MjDownloadSDK.init(this, AppConfig.memoryMaxSize,
                AppConfig.fileMaxSize,
                AppConfig.singleMemoryMaxSize,
                AppConfig.singleFileMaxSize,
                AppConfig.maxTask,
                getCacheDir().getPath());
    }

    private void getIntentDate(){
        if(getIntent() != null) {
            int status = getIntent().getIntExtra("connectStatus", 0);
            AppConfig.connectStatus = status;

            String[] mode = getIntent().getStringArrayExtra("modeData");
            AppConfig.modeData = mode;

            int modeIndex = getIntent().getIntExtra("modeIndex", -1);
            AppConfig.modeIndex = modeIndex;

            refreshConnnectStatues(status);
        }
    }

    private MassageReceiver.ReceiverCallBack receiver = new MassageReceiver.ReceiverCallBack() {
        @Override
        public void onCallBack(int connectStatus, int modeIndex) {
            AppConfig.connectStatus = connectStatus;
            AppConfig.modeIndex = modeIndex;
            refreshConnnectStatues(connectStatus);
        }
    };

    //更新页面状态
    private void refreshConnnectStatues(int connectStatus){
        if(connectStatus == 1){
            appTitleBackView.connectZkey();
        } else {
            appTitleBackView.unConnectZkey();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DownloadUtil.getInstance(this).addView(progressBarView, ProgressBarView.PAGE_LIST);
        DownloadUtil.getInstance(this).refreshStatus();
    }

    @Override
    public void initView(){
        setContentView(R.layout.activity_video_list);
        appTitleBackView = (AppTitleBackView) findViewById(R.id.main_title_layout);
        appTitleBackView.getNameTV().setText(getResources().getString(R.string.panorama));
        pullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.app_list_grid);

        videoH2GridView = pullToRefreshGridView.getRefreshableView();
        videoH2GridView.setNumColumns(2);
        videoH2GridView.setGravity(Gravity.CENTER);
//        videoH2GridView.setVerticalSpacing(30);
//        videoH2GridView.setHorizontalSpacing(18);
        progressBarView = (ProgressBarView) findViewById(R.id.main_progressbar);
        adapter = new VideoH2Adapter(MainActivity.this);
        videoH2GridView.setAdapter(adapter);
        layout_no_network = (RelativeLayout)findViewById(R.id.layout_no_network);
        textview_reset = (TextView)findViewById(R.id.textview_reset);
        imageview_no_network_back = (ImageView)findViewById(R.id.imageview_no_network_back);

        bottomBtn = (Button) findViewById(R.id.app_list_bottom_btn);
        bottomBtn.setOnClickListener(this);
    }


    @Override
    public void initData(){
        initData(0);
    }

    public void initData(int pageNum){
        currentPage = pageNum;
        if (pageNum != 0 && pageNum * NUM_PER_PAGE >= totalNum) { //首页面没有
            Toast.makeText(this, "没有更多", Toast.LENGTH_SHORT).show();
            refreshComplete();
            return;
        }
        if(!NetworkUtil.isNetworkConnected(this)){
            layout_no_network.setVisibility(View.VISIBLE);
            return;
        }
        refreshData();
    }

    @Override
    public void initListener() {
        videoH2GridView.setOnItemClickListener(this);
        pullToRefreshGridView.setOnRefreshListener(this);
        layout_no_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        textview_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetworkConnected(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, getString(R.string.mj_string_network_not_connection), Toast.LENGTH_SHORT).show();
                    return;
                }
                layout_no_network.setVisibility(View.GONE);
                refreshData();
                DownloadUtil.getInstance(MainActivity.this).refreshStatus();
            }
        });

        imageview_no_network_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void refreshData(){
        reportListPv();
        String listUrl = UrlConfig.getListUrl(currentPage * 12, 12);
        new MainApi().mainList(listUrl, new RequestCallBack<ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>>>() {
            @Override
            public void onSuccess(ResponseBaseBean<MainSubContentListBean<List<ContentInfo>>> result) {
                super.onSuccess(result);
                if (result.getStatus() == 0) {
                    totalNum = result.getData().getTotal();
                    adapter.add(result.getData().getList());
                    refreshComplete();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                refreshComplete();
            }
        });
    }

    public void refreshComplete(){
        pullToRefreshGridView.postDelayed(new Runnable() {

            @Override
            public void run() {
                pullToRefreshGridView.onRefreshComplete();
            }
        }, 200);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ContentInfo info = (ContentInfo) adapterView.getAdapter().getItem(i);
        reportItemClick(info.getRes_id(), info.getTitle());
        Intent intent = new Intent(this,MJVideoDetailActivity.class);
        Bundle bd = new Bundle();
        bd.putString(AppConfig.KEY_INTENT_URL,info.getUrl());
        bd.putString(AppConfig.KEY_INTENT_RESID, info.getRes_id());
        bd.putString(AppConfig.KEY_INTENT_TITLE, info.getTitle());
        bd.putString(AppConfig.KEY_INTENT_FROMPAGE, "bf_vrlist");
        intent.putExtra(AppConfig.KEY_INTENT_BUNDLE, bd);
        startActivity(intent);
        //打开Mojing应用
//        Intent intent = getPackageManager().getLaunchIntentForPackage("com.baofeng.mj");
//        startActivity(intent);

        //打开指定页面
//        try {
//            String MY_ACTION = "com.baofeng.mj.videoplugin.action";
//            Intent intent = new Intent();
//            intent.setAction(MY_ACTION);
//            intent.putExtra("next_url", contentInfos.get(i).getUrl());
//            intent.putExtra("next_type", contentInfos.get(i).getType());
//            intent.putExtra("next_subType", 0);
//            startActivity(intent);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        //拼接数据跳到播放页面
//        Intent intent = new Intent();
//        ComponentName componentName = new ComponentName("com.baofeng.mj.unity","com.baofeng.mj.unity.UnityActivity");
//        intent.setComponent(componentName);
//        List<HierarchyBean> list = new ArrayList<HierarchyBean>();
//        HierarchyBean hierarchyBean = new HierarchyBean();
//        hierarchyBean.setType(contentInfos.get(i).getType() + "");
//        hierarchyBean.setSubType(0 + "");
//        list.add(hierarchyBean);
//        intent.putExtra("hierarchy",  JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect););

    }

    private void reportItemClick(String videoId,String title){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        bean.setPagetype("bf_vrlist");
        bean.setClickpos("vrlist_res");
        bean.setClicktype("jump");
        bean.setVideoid(videoId);
        bean.setTitle(title);
        ReportBusiness.getInstance().reportClick(bean);
    }

    private void reportListPv(){
        ReportListPvBean bean = new ReportListPvBean();
        bean.setEtype("pv");
        bean.setPagetype("bf_vrlist");
        ReportBusiness.getInstance().reportClick(bean);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
        currentPage = currentPage + 1;
        initData(currentPage);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.app_list_bottom_btn){
            DownloadUtil.listClickPos = DownloadUtil.CLICK_DOWNLOAD;
            DownloadUtil.getInstance(this).onlyDownLoadInstall(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppConfig.connectStatus = -1;
        AppConfig.modeData = null;
        AppConfig.modeIndex = -1;
        ReceiverManager.getInstance(this).removeMassageReceiverCallBack(receiver);
        ReceiverManager.getInstance(this).clearAllReceiver();
    }


}
