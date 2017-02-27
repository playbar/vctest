package com.baofeng.mj.videoplugin.bean;

import java.io.Serializable;

/**
 * Created by yushaochen on 2017/2/21.
 */
public class GLDetailVideoBean implements Serializable {

    private String play_url;
    private String size;

    public String getPlay_url() {
        return play_url;
    }

    public void setPlay_url(String play_url) {
        this.play_url = play_url;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
