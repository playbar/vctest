package com.baofeng.mj.videoplugin.bean;

/**
 * Created by muyu on 2016/11/9.
 */
public class ReportClickBean extends BaseReportBean{
    private String etype;
    private String pagetype;
    private String clickpos;
    private String clicktype;
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

    public String getClickpos() {
        return clickpos;
    }

    public void setClickpos(String clickpos) {
        this.clickpos = clickpos;
    }

    public String getClicktype() {
        return clicktype;
    }

    public void setClicktype(String clicktype) {
        this.clicktype = clicktype;
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
