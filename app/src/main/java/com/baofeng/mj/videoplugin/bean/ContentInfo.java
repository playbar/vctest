package com.baofeng.mj.videoplugin.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 具体View中 单个数据
 * Created by muyu on 2016/5/12.
 */
public class ContentInfo extends ContentBaseBean implements Serializable {
    private int has_more;
    private String res_id;//资源ide
    private String pic_url;//
    private String title;//资源名称
    private String subtitle;//副标题
    private int type;
    private int subtype;
    private String operation_type;
    private int headwear;
    private int payment_type;
    private String payment_count;//价格
    private int is_pay;
    private String url;
    private List banner;
    private int index; //用于布局显示位置


    public int getHas_more() {
        return has_more;
    }

    public void setHas_more(int has_more) {
        this.has_more = has_more;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRes_id() {
        return res_id;
    }

    public void setRes_id(String res_id) {
        this.res_id = res_id;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getSubtype() {
        return subtype;
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    public String getOperation_type() {
        return operation_type;
    }

    public void setOperation_type(String operation_type) {
        this.operation_type = operation_type;
    }

    public int getHeadwear() {
        return headwear;
    }

    public void setHeadwear(int headwear) {
        this.headwear = headwear;
    }

    public int getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(int payment_type) {
        this.payment_type = payment_type;
    }

    public String getPayment_count() {
        return payment_count;
    }

    public void setPayment_count(String payment_count) {
        this.payment_count = payment_count;
    }

    public int getIs_pay() {
        return is_pay;
    }

    public void setIs_pay(int is_pay) {
        this.is_pay = is_pay;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List getBanner() {
        return banner;
    }

    public void setBanner(List banner) {
        this.banner = banner;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
