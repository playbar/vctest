package com.baofeng.mj.videoplugin.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by panxin on 2016/11/7.
 */
public class DetailBean implements Serializable {

    public String title;
    public String is_panorama; //1:2d, 2:360, 3:180,  4立方体
    public String video_dimension; //1:2d,  2:3d上下,  3:3d左右
    public String source;
    public String score;
    public String desc;
    public DetailVideoBean video_attrs;



}