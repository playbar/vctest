package com.baofeng.mj.videoplugin.download;

import com.baofeng.mojing.sdk.download.utils.MjDownloadStatus;

/**
 * Created by panxin on 2016/11/4.
 */
public class DownloadBean {

    public long id;

    public String url;//视频url

    /**文件绝对路径*/
    public String path;

    public double progress;

    public int status = MjDownloadStatus.DEFAULT;
}
