package com.baofeng.mj.videoplugin.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofeng.mj.videoplugin.R;
import com.baofeng.mj.videoplugin.bean.ContentInfo;
import com.baofeng.mj.videoplugin.util.ImageLoaderUtils;
import com.baofeng.mj.videoplugin.util.application.PixelsUtil;

import java.util.List;

/**
 * Created by muyu on 2016/11/1.
 */
public class VideoH2Adapter extends BaseAdapter {
    private List<ContentInfo> mjList;
    private Context mContext;
    public int imgWidth;
    public int imgHeight;
    private int screenWidth;
    private int px15;

    public VideoH2Adapter(Context context) {
        this.mContext = context;
        screenWidth = PixelsUtil.getWidthPixels(context);
        px15 = PixelsUtil.dip2px(context, 15);
        imgWidth = screenWidth / 2 - px15;
        imgHeight = (int) (imgWidth / 1.786f);
    }

    @Override
    public int getCount() {
        return mjList == null ? 0 : mjList.size();
    }

    @Override
    public Object getItem(int position) {
        return mjList == null ? null : mjList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (mjList == null || mjList.isEmpty()) ? 0 : mjList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        final MyGridViewItemViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.horizontal_two_item, null);
            viewHolder = new MyGridViewItemViewHolder();
            viewHolder.shorty_two_layout = (RelativeLayout)view.findViewById(R.id.shorty_two_layout);
            viewHolder.ItemText = (TextView) view.findViewById(R.id.shorty_two_name_tv);
            viewHolder.ItemImage = (ImageView) view.findViewById(R.id.shorty_two_image);
            viewHolder.ItemImageTrans = (ImageView) view.findViewById(R.id.shorty_two_image_trans);
            viewHolder.shorty_two_subname = (TextView) view.findViewById(R.id.shorty_two_subname);

//            viewHolder.shorty_two_layout.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth / 2, RelativeLayout.LayoutParams.WRAP_CONTENT));
            viewHolder.ItemImage.setLayoutParams(new RelativeLayout.LayoutParams(imgWidth, imgHeight));
            viewHolder.ItemImageTrans.setLayoutParams(new RelativeLayout.LayoutParams(imgWidth, imgHeight));
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (MyGridViewItemViewHolder) view.getTag();
        }

        //update view holder
        updateViewHolder(viewHolder, (ContentInfo) getItem(position));
        return view;
    }

    private void updateViewHolder(final MyGridViewItemViewHolder viewHolder, ContentInfo data) {
        viewHolder.ItemText.setText(data.getTitle());
//
//        Glide.with(mContext)
//                .load(data.getPic_url())
//                .placeholder(R.mipmap.list_pic)
//                .dontAnimate()
//                .into(viewHolder.ItemImage);
        ImageLoaderUtils.getInstance(mContext).getImageLoader().displayImage(data.getPic_url(), viewHolder.ItemImage, ImageLoaderUtils.getInstance(mContext).getImageOptions());
        viewHolder.shorty_two_subname.setText(data.getSubtitle());
    }

    public void update(List<ContentInfo> data) {
        this.mjList = data;
        notifyDataSetChanged();
    }

    public void add(List<ContentInfo> data) {
        if (this.mjList == null) {
            this.mjList = data;
        } else {
//            mjList.clear();
            this.mjList.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void update() {
        notifyDataSetChanged();
    }

    public boolean hasData() {
        return mjList != null;
    }

    class MyGridViewItemViewHolder {
        private RelativeLayout shorty_two_layout;
        private ImageView ItemImage;
        private ImageView ItemImageTrans;
        private TextView ItemText;
        private TextView shorty_two_subname;
    }

}