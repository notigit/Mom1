package com.xiaoaitouch.mom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.module.ClassRoomImageListModule;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 课堂推荐
 *
 * @author huxin
 * @data: 2016/1/14 13:46
 * @version: V1.0
 */
public class ClassRoomAdapter extends BaseAdapter {
    private int width = 0;
    private int height = 0;
    private List<ClassRoomImageListModule> classRoomTypeRecommend = null;
    private DisplayImageOptions displayImageOptions;

    public ClassRoomAdapter(int width, int height) {
        this.width = width;
        this.height = height;
        this.displayImageOptions = DisplayImageOptionsUtils.getAdvDisplayImageOptions();
    }

    public void setData(List<ClassRoomImageListModule> classRoomTypeRecommend) {
        this.classRoomTypeRecommend = classRoomTypeRecommend;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return classRoomTypeRecommend == null ? 0 : classRoomTypeRecommend.size();
    }

    @Override
    public Object getItem(int position) {
        return classRoomTypeRecommend.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.class_room_view_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ClassRoomImageListModule classRoomImageListModules = classRoomTypeRecommend.get(position);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                width - 5, height);
        holder.relativeLayout.setLayoutParams(mLayoutParams);
        if (classRoomImageListModules.getIsNew() == 1) {
            holder.remindNewIcon.setVisibility(View.VISIBLE);
        } else {
            holder.remindNewIcon.setVisibility(View.INVISIBLE);
        }
        holder.remindTitleTv.setText(classRoomImageListModules.getTitle1());
        holder.remindContentTv.setText(classRoomImageListModules.getTitle2());
        ImageLoader.getInstance().displayImage(Configs.IMAGE_URL + classRoomImageListModules.getImg(),
                holder.remindIcon, displayImageOptions);
        return convertView;
    }


    public class ViewHolder {
        @Bind(R.id.class_room_remind_ray)
        RelativeLayout relativeLayout;
        @Bind(R.id.class_room_remind_bg_icon)
        ImageView remindIcon;
        @Bind(R.id.class_room_remind_new_icon)
        ImageView remindNewIcon;
        @Bind(R.id.class_room_remind_title_tv)
        TextView remindTitleTv;
        @Bind(R.id.class_room_remind_content_tv)
        TextView remindContentTv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
