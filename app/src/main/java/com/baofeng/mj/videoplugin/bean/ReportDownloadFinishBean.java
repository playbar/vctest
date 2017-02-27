package com.baofeng.mj.videoplugin.bean;

/**
 * Created by panxin on 2016/11/9.
 */
public class ReportDownloadFinishBean extends BaseReportBean{

    private String etype;
    private String MD5_result;


    public String getEtype() {
        return etype;
    }

    public void setEtype(String etype) {
        this.etype = etype;
    }

    public String getMD5_result() {
        return MD5_result;
    }

    public void setMD5_result(String MD5_result) {
        this.MD5_result = MD5_result;
    }
}
