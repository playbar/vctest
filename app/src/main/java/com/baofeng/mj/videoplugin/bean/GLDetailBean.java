package com.baofeng.mj.videoplugin.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yushaochen on 2017/2/21.
 */
public class GLDetailBean implements Serializable {

    private String title;
    private String is_panorama; //1:2d, 2:360, 3:180,  4立方体
    private String video_dimension; //1:2d,  2:3d上下,  3:3d左右
    private String source;
    private String score;
    private String desc;
    private ArrayList<GLDetailVideoBean> video_attrs;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIs_panorama() {
        return is_panorama;
    }

    public void setIs_panorama(String is_panorama) {
        this.is_panorama = is_panorama;
    }

    public String getVideo_dimension() {
        return video_dimension;
    }

    public void setVideo_dimension(String video_dimension) {
        this.video_dimension = video_dimension;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArrayList<GLDetailVideoBean> getVideo_attrs() {
        return video_attrs;
    }

    public void setVideo_attrs(ArrayList<GLDetailVideoBean> video_attrs) {
        this.video_attrs = video_attrs;
    }
}