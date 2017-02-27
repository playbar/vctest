package com.baofeng.mj.videoplugin.download;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.application.AppConfig;
import com.baofeng.mj.videoplugin.bean.ReportClickBean;
import com.baofeng.mj.videoplugin.bean.ReportDownloadFinishBean;
import com.baofeng.mj.videoplugin.bean.ReportInstallBean;
import com.baofeng.mj.videoplugin.interfaces.ISDPermissionListener;
import com.baofeng.mj.videoplugin.interfaces.IShowNetDialogListener;
import com.baofeng.mj.videoplugin.ui.view.ProgressBarView;
import com.baofeng.mj.videoplugin.util.ApkUtils;
import com.baofeng.mj.videoplugin.util.FileUtils;
import com.baofeng.mj.videoplugin.util.IMjPlayAPI;
import com.baofeng.mj.videoplugin.util.MD5;
import com.baofeng.mj.videoplugin.util.PreferenceUtil;
import com.baofeng.mj.videoplugin.util.application.NetworkUtil;
import com.baofeng.mj.videoplugin.util.permissons.CheckPermission;
import com.baofeng.mj.videoplugin.util.permissons.PermissionListener;
import com.baofeng.mj.videoplugin.util.permissons.PermissionUtil;
import com.baofeng.mj.videoplugin.util.report.ReportBusiness;
import com.baofeng.mojing.MojingDownloader;
import com.baofeng.mojing.sdk.download.entity.NativeCallbackInfo;
import com.baofeng.mojing.sdk.download.utils.MjDownloadSDK;
import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;

import java.io.File;

/**
 * Created by panxin on 2016/11/4.
 */
public class DownloadUtil implements MojingDownloader.DownloadCallback {

    public static DownloadUtil instance;
    public static final String sdDownloadPath = FileStorageDirUtil.getMojingDir() + "download/";
    private final int HANDLER_WHAT_CALLBACK = 0;
    private final int HANDLER_WHAT_START_CALLBACK = 1;
    private DownloadHandler handler;
    private ProgressBarView mProgressBarView;
//    public static boolean isFromDetailPrev = false;//是否从详情页预览图点击

    /**
     * 对应页面上中下按钮报数，
     */
    public static final int CLICK_PREV = 0;//播放上面点击
    public static final int CLICK_MIDDLE = 1; //提升3倍流畅度按钮
    public static final int CLICK_DOWNLOAD = 2; //底部下载按钮
    public static int clickPostion = 2;  //详情页面点击位置
    public static int listClickPos = 0;  //列表页面点击位置
    private int page;//ProgressBarView.PAGE_DETAIL ,ProgressBarView.PAGE_LIST
    public String resId;
    public String title;
    private boolean isCanClick = true;
    private boolean isCanRefreshStatus = false;
    private Context mContext;

    public boolean isCanClick() {
        return isCanClick;
    }

    public void setIsCanClick(boolean isCanClick) {
        this.isCanClick = isCanClick;
    }

    public static DownloadUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DownloadUtil(context);
        }
        return instance;
    }


    public DownloadUtil(Context context) {
        this.mContext = context;
        handler = new DownloadHandler(Looper.getMainLooper());
        MjDownloadSDK.addCallback(this);
    }

    public void init() {
        IMjPlayAPI api = IMjPlayAPI.getInstance(mContext);
        api.requestDownloadUrl(new IMjPlayAPI.RequestCallBack() {
            @Override
            public void isSuccess(boolean status) {
                if(isCanRefreshStatus){
                    isCanRefreshStatus = false;
                    if (!checkAPKMd5()) {
                        mProgressBarView.setType(page, ProgressBarView.TYPE_UNDOWNLOAD);
                    }else{
                        mProgressBarView.setType(page, ProgressBarView.TYPE_UNINSTALL);
                    }
                }
                checkPermission();
            }
        });

        File dir = new File(DownloadUtil.sdDownloadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

    }

    private void checkPermission(){
        CheckPermission.from(mContext)
                .setPermissions(PermissionUtil.ALL_PERMISSIONS)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void permissionGranted() {
                        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(mContext,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        }
                    }

                    @Override
                    public void permissionDenied() {
                        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(mContext,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        }
                    }
                }).check();
    }

    /**
     * 详情页报数需要传递视频id和名字，列表页不需要
     * @param resId
     * @param title
     */
    public void setVideoIDAndTitle(String resId,String title){
        this.resId = resId;
        this.title = title;
    }

    public void addView(final ProgressBarView mProgressBarView, int page) {
        this.mProgressBarView = mProgressBarView;
        this.page = page;

        mProgressBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConfig.AUTO_DOWNLOAD && !isShowProgress) {
                    isShowProgress = true;
                    mProgressBarView.setShowProgress(true);
                    return;
                }
                downLoadShowProgress();
            }
        });
    }

    private boolean isShowProgress = false;//是否显示下载进度，进来直接下载，无需下载进度，用户点击后才能显示进度

    /**
     * 只能下载和安装，不能暂停
     */
    public void onlyDownLoadInstall(boolean isShow){
        isShowProgress = isShow;
        if (isAppInstalled()) {
            isShowProgress = true;
            //open app
            if(isShow) {
                reportDownloadClick("open");
                Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(AppConfig.MJ_PACKAGE_NAME);
                mContext.startActivity(intent);
            }
            return;
        }

        String url = PreferenceUtil.instance(mContext).getDownloadUrl();
        NativeCallbackInfo info = getNativeCallbackInfo(url);
        if (info != null) {
            switch (info.getStatus()) {
                case MjDownloadStatus.DOWNLOADING:
                case MjDownloadStatus.CONNECTING:
//                    PreferenceUtil.instance(BaseApplication.INSTANCE).setClickPause(true);
//                    pauseDownload(info.getJobID());
                    break;
                case MjDownloadStatus.DEFAULT:
                case MjDownloadStatus.ABORT:
                case MjDownloadStatus.PAUSED:
                case MjDownloadStatus.WAITING:
                case MjDownloadStatus.ERROR:
                    PreferenceUtil.instance(mContext).setClickPause(false);
                    startDownload();
                    break;
                case MjDownloadStatus.COMPLETE: //下载完就可以显示进度变化了
                    isShowProgress = true;
                    if (checkAPKMd5()) {
                        //安装
                        install();
                    } else {
                        startDownload();
                    }
                    break;
            }
        } else {
            if (!checkAPKMd5()) {
                reportDownloadClick("download");
                startDownload();
            } else {
                isShowProgress = true;
                install();
            }
        }
    }

    public void install(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApkUtils.installApk(mContext, sdDownloadPath + AppConfig.APK_NAME);
            }
        }).start();
    }

    public boolean isAppInstalled(){
        return FileUtils.isAppInstalled(mContext, AppConfig.MJ_PACKAGE_NAME);
    }

    public void downLoadShowProgress(){
        if (!isCanClick) {
            return;
        }
        isCanRefreshStatus = false;
        if (isAppInstalled()) {
            //open app
            reportDownloadClick("open");
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(AppConfig.MJ_PACKAGE_NAME);
            mContext.startActivity(intent);
            return;
        }

        String url = PreferenceUtil.instance(mContext).getDownloadUrl();
        NativeCallbackInfo info = getNativeCallbackInfo(url);
        if (info != null) {
            switch (info.getStatus()) {
                case MjDownloadStatus.DOWNLOADING:
                case MjDownloadStatus.CONNECTING:
                    isCanClick = false;
                    PreferenceUtil.instance(mContext).setClickPause(true);
                    pauseDownload(info.getJobID());
                    break;
                case MjDownloadStatus.DEFAULT:
                case MjDownloadStatus.ABORT:
                case MjDownloadStatus.PAUSED:
                case MjDownloadStatus.WAITING:
                case MjDownloadStatus.ERROR:
                    isCanClick = false;
                    PreferenceUtil.instance(mContext).setClickPause(false);
                    startDownload();
                    break;
                case MjDownloadStatus.COMPLETE:
                    if (checkAPKMd5()) {
                        //安装
                        install();
                    } else {
                        isCanClick = false;
                        startDownload();
                    }
                    break;
            }
        } else {
            if (!checkAPKMd5()) {
                isCanClick = false;
                reportDownloadClick("download");
                startDownload();
            } else {
                install();
            }
        }
    }

    public void startDownload() {
        if(!NetworkUtil.networkEnable(mContext)){
            Toast.makeText(mContext,mContext.getResources().getString(R.string.mj_string_network_not_connection),Toast.LENGTH_SHORT).show();
            return;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == -1) {
            if(mISDPermissionListener!=null){
                mISDPermissionListener.showSDPermissionDialog();
            }
            isCanClick = true;
            return;
        }
        if(!NetworkUtil.isWIFIConnected(mContext)){
            if(PreferenceUtil.instance(mContext).getNetDownloadStatus() == AppConfig.TYPE_NET_DOWNLOAD_DEFAULT
                    ||PreferenceUtil.instance(mContext).getNetDownloadStatus() == AppConfig.TYPE_NET_DOWNLOAD_WAIT_WIFI){
                if(mIShowNetDialogListener != null){
                    mIShowNetDialogListener.showDialog();
                }
                return;
            }
        }

        File dir = new File(DownloadUtil.sdDownloadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String url = PreferenceUtil.instance(mContext).getDownloadUrl();
        MjDownloadSDK.start(mContext, url, sdDownloadPath + AppConfig.APK_NAME, 2);
    }

    public void pauseDownload(long id) {
        MjDownloadSDK.pause(id);
    }

    public NativeCallbackInfo getNativeCallbackInfo(String url) {
        return MjDownloadSDK.queryInfo(url, sdDownloadPath + AppConfig.APK_NAME, 2);
    }

    public void onPause() {
        MjDownloadSDK.removeCallback(this);
    }

    public void onResume() {
        MjDownloadSDK.addCallback(this);
    }

    public void refreshStatus() {

        if (FileUtils.isAppInstalled(mContext, AppConfig.MJ_PACKAGE_NAME)) {
            mProgressBarView.setType(page, ProgressBarView.TYPE_INSTALLED);
        } else {
            String url = PreferenceUtil.instance(mContext).getDownloadUrl();
            NativeCallbackInfo info = getNativeCallbackInfo(url);

            if (info != null) {

                switch (info.getStatus()) {
                    case MjDownloadStatus.DEFAULT:
                    case MjDownloadStatus.WAITING:
                        mProgressBarView.setType(page, ProgressBarView.TYPE_UNDOWNLOAD);
                        break;
                    case MjDownloadStatus.DOWNLOADING:
                    case MjDownloadStatus.CONNECTING:
                        if(isShowProgress) {
                            mProgressBarView.setProgress((int) (FileUtils.getFileDownloadSize(sdDownloadPath + AppConfig.APK_NAME) / info.getTotalLen() * 100));
                        }
                        mProgressBarView.setType(page, ProgressBarView.TYPE_DOWNLOADING);
                        startDownload();
                        break;
                    case MjDownloadStatus.ABORT:
                    case MjDownloadStatus.PAUSED:
                        if(isShowProgress) {
                            mProgressBarView.setProgress((int) (FileUtils.getFileDownloadSize(sdDownloadPath + AppConfig.APK_NAME) / info.getTotalLen() * 100));
                        }
                        mProgressBarView.setType(page, ProgressBarView.TYPE_PAUSE);
                        break;
                    case MjDownloadStatus.COMPLETE:
                        mProgressBarView.setType(page, ProgressBarView.TYPE_UNINSTALL);
                        break;
                    case MjDownloadStatus.ERROR:
                        mProgressBarView.setType(page, ProgressBarView.TYPE_UNDOWNLOAD);
                        MjDownloadSDK.delete(info.getJobID());

                        break;

                }
            } else {
                if (!checkAPKMd5()) {
                    MjDownloadSDK.cleanCache();
                    mProgressBarView.setType(page, ProgressBarView.TYPE_UNDOWNLOAD);
                }else{
                    mProgressBarView.setType(page, ProgressBarView.TYPE_UNINSTALL);
                }

            }
        }

    }

    @Override
    public void callback(long id, int status, double progress, long errorCode) {
        isCanClick = true;
        if (handler != null) {
            DownloadBean bean = new DownloadBean();
            bean.id = id;
            bean.progress = progress;
            bean.status = status;
            handler.obtainMessage(HANDLER_WHAT_CALLBACK, bean).sendToTarget();
        }
    }

    @Override
    public void startCallback(long id, String url, String path, int status) {
        isCanClick = true;
        if (handler != null) {
            DownloadBean bean = new DownloadBean();
            bean.id = id;
            bean.url = url;
            bean.path = path;
            bean.status = status;
            handler.obtainMessage(HANDLER_WHAT_START_CALLBACK, bean).sendToTarget();
        }
    }

    private class DownloadHandler extends Handler {
        public DownloadHandler(Looper mainLooper) {
            super(mainLooper);
        }

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            DownloadBean bean;
            switch (what) {
                case HANDLER_WHAT_CALLBACK: {
                    bean = (DownloadBean) msg.obj;
                    if(mProgressBarView != null) {
                        if(isShowProgress) {
                            mProgressBarView.setProgress((int) (bean.progress * 100));
                        }
                    }
                    switch (bean.status) {
                        case MjDownloadStatus.CONNECTING:
                            if(mProgressBarView != null) {
                                mProgressBarView.setType(page, ProgressBarView.TYPE_DOWNLOADING);
                            }
                        case MjDownloadStatus.DOWNLOADING:
                            break;
                        case MjDownloadStatus.ABORT:
                        case MjDownloadStatus.PAUSED:
                            if(mProgressBarView != null) {
                                mProgressBarView.setType(page, ProgressBarView.TYPE_PAUSE);
                            }
                            break;
                        case MjDownloadStatus.COMPLETE:
                            if(mProgressBarView != null) {
                                mProgressBarView.setType(page, ProgressBarView.TYPE_UNINSTALL);
                            }
                            if(!checkAPKMd5()){
                                reportDownloadFinish("0");
                                startDownload();
                            }else{
                                reportDownloadFinish("1");
                                reportInstall();
                                if (PreferenceUtil.instance(mContext).getNetDownloadStatus() != AppConfig.TYPE_NET_DOWNLOAD_MOBILE) {
                                    PreferenceUtil.instance(mContext).setNetDownloadStatus(AppConfig.TYPE_NET_DOWNLOAD_DEFAULT);
                                }
                                install();
                            }
                            break;
                        case MjDownloadStatus.ERROR:
                            Toast.makeText(mContext,mContext.getResources().getString(R.string.mj_string_download_error),Toast.LENGTH_SHORT).show();
                            if(mProgressBarView != null) {
                                mProgressBarView.setType(page, ProgressBarView.TYPE_UNDOWNLOAD);
                            }
                            break;
                    }
                }
                break;
                case HANDLER_WHAT_START_CALLBACK: {

                    bean = (DownloadBean) msg.obj;
                    switch (bean.status) {
                        case MjDownloadStatus.CONNECTING:
                        case MjDownloadStatus.DOWNLOADING:
                            if(mProgressBarView != null) {
                                mProgressBarView.setType(page, ProgressBarView.TYPE_DOWNLOADING);
                            }
                            break;
                        case MjDownloadStatus.ABORT:
                        case MjDownloadStatus.PAUSED:
                            if(mProgressBarView != null) {
                                mProgressBarView.setType(page, ProgressBarView.TYPE_PAUSE);
                            }
                            break;
                        case MjDownloadStatus.COMPLETE:
                            if(mProgressBarView != null) {
                                mProgressBarView.setType(page, ProgressBarView.TYPE_UNINSTALL);
                            }
                            break;
                        case MjDownloadStatus.ERROR:
                            Toast.makeText(mContext,mContext.getResources().getString(R.string.mj_string_download_error),Toast.LENGTH_SHORT).show();
                            if(mProgressBarView != null) {
                                mProgressBarView.setType(page, ProgressBarView.TYPE_UNDOWNLOAD);
                            }
                            break;
                    }
                }
                break;
                default:
                    break;
            }
        }
    }

    private boolean checkAPKMd5() {
        File file = new File(sdDownloadPath + AppConfig.APK_NAME);
        if (file.exists()) {

            String md5 = PreferenceUtil.instance(mContext).getDownloadMD5();
            String tempMd5 = MD5.getFileMD5(file);
            if (md5.equals(tempMd5)) {
                return true;
            } else {
                if(!md5.equals("")) {
                    file.delete();
                }else{
                    isCanRefreshStatus = true;
                }
                return false;
            }
        } else {
            return false;
        }

    }

    private ISDPermissionListener mISDPermissionListener;
    public void setISDPermissionListener(ISDPermissionListener listener){
        this.mISDPermissionListener = listener;
    }


    public IShowNetDialogListener mIShowNetDialogListener;
    public void setIShowNetDialogListener(IShowNetDialogListener listener){
        this.mIShowNetDialogListener = listener;
    }


    private void reportDownloadClick(String clickType){
        ReportClickBean bean = new ReportClickBean();
        bean.setEtype("click");
        if(page == ProgressBarView.PAGE_LIST){
            bean.setPagetype("bf_vrlist");
            switch (listClickPos){
                case CLICK_DOWNLOAD:
                    bean.setClickpos("vrlist_root");
                    break;
                default:
                    bean.setClickpos("vrlist_app");
                    break;
            }
        } else{
            bean.setPagetype("bf_detail");
            switch (clickPostion){
                case CLICK_PREV:
                    bean.setClickpos("detailprev_app");
                    break;
                case CLICK_MIDDLE:
                    bean.setClickpos("detailbutton_app");
                    break;
                case CLICK_DOWNLOAD:
                    bean.setClickpos("detaildownload_app");
                    break;
            }
            if(resId!=null) {
                bean.setVideoid(resId);
                bean.setTitle(title);
            }
        }

        bean.setClicktype(clickType);
        ReportBusiness.getInstance().reportClick(bean);
    }

    private void reportDownloadFinish(String result){
        ReportDownloadFinishBean bean = new ReportDownloadFinishBean();
        bean.setEtype("finish");
        bean.setMD5_result(result);
        ReportBusiness.getInstance().reportClick(bean);
    }

    private void reportInstall(){
        ReportInstallBean bean = new ReportInstallBean();
        bean.setEtype("install");
        ReportBusiness.getInstance().reportClick(bean);
    }


}
