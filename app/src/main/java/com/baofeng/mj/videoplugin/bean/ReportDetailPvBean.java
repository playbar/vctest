package com.baofeng.mj.videoplugin.bean;

/**
 * Created by panxin on 2016/11/9.
 */
public class ReportDetailPvBean extends BaseReportBean {

    private String etype;
    private String pagetype;
    private String frompage;
    private String videoid;
    private String title;

    public String getEtype() {
        return etype;
    }

    public void setEtype(String etype) {
        this.etype = etype;
    }

    public String getPagetype() {
        return pagetype;
    }

    public void setPagetype(String pagetype) {
        this.pagetype = pagetype;
    }

    public String getFrompage() {
        return frompage;
    }

    public void setFrompage(String frompage) {
        this.frompage = frompage;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
