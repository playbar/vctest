package com.baofeng.mj.videoplugin.ui.adapter;

import android.content.Context;

import com.baofeng.mj.videoplugin.bean.ContentInfo;
import com.baofeng.mj.videoplugin.ui.view.ChannelMiddleItemView;
import com.bfmj.viewcore.adapter.GLBaseAdapter;
import com.bfmj.viewcore.view.GLGroupView;
import com.bfmj.viewcore.view.GLRectView;
import com.bfmj.viewcore.view.GLViewPage;

import java.util.ArrayList;

/**
 * Created by yushaochen on 2016/7/18.
 */
public class ChannelListMiddleGridAdapter extends GLBaseAdapter {

    private Context mContext;
    private GLViewPage mPage;
    private ArrayList<ContentInfo> datas = new ArrayList<ContentInfo>();

    public ChannelListMiddleGridAdapter(Context context, GLViewPage page) {
        mContext = context;
        mPage = page;
    }

    public void setData(ArrayList<ContentInfo> datas) {
        this.datas.clear();
        if(null == datas) {
            return;
        }

        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public void addIndex(int i, GLRectView glRectView) {

    }

    @Override
    public void removeIndex(int i) {

    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public GLRectView getGLView(int position, GLRectView convertView, GLGroupView parent) {
        if (position >= datas.size()) {
            return null;
        }
        ChannelMiddleItemView itemView = (ChannelMiddleItemView) convertView;
        if (itemView == null) {
            itemView = new ChannelMiddleItemView(mContext,mPage);
            if (parent != null) {
                itemView.setDepth(parent.getDepth());
            }
        }

        itemView.setData(datas.get(position));

        return itemView;
    }

    public ArrayList<ContentInfo> getData(){
        return datas;
    }

}
