package com.xiaoaitouch.mom.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.module.ClassRoomImageListModule;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImageAdapters extends BaseAdapter {

    private  List<ClassRoomImageListModule> classRoomTypeTips;
    private DisplayImageOptions displayImageOptions;
    private int mWidth=0;
    private int mHeight=0;

    public void setViewData(List<ClassRoomImageListModule> classRoom,int width,int heigt) {
        this.classRoomTypeTips=classRoom;
        this.mWidth=width;
        this.mHeight=heigt;
        this.displayImageOptions = DisplayImageOptionsUtils.getAdvDisplayImageOptions();
        this.notifyDataSetChanged();
    }

    // 是为了使图片循环显示
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public Object getItem(int position) {
        return classRoomTypeTips.get(position % classRoomTypeTips.size());
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.gallery_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mAdvImageIv.setLayoutParams(new LinearLayout.LayoutParams(
                mWidth, mHeight));

        ClassRoomImageListModule remindIcon= classRoomTypeTips.get(position % classRoomTypeTips.size());
        ImageLoader.getInstance().displayImage(Configs.IMAGE_URL + remindIcon.getImg(),
                holder.mAdvImageIv, displayImageOptions);
        return convertView;
    }

    public class ViewHolder {
        @Bind(R.id.gallery_image)
        ImageView mAdvImageIv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}