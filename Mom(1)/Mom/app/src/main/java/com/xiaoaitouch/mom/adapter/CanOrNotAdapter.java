package com.xiaoaitouch.mom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Desc: 能不能
 * User: huxin
 * Date: 2016/2/24
 * Time: 11:35
 * FIXME
 */
public class CanOrNotAdapter extends BaseAdapter {
    private String[] canOrNotWork;
    private int[] canOrNotWorkImage;

    public CanOrNotAdapter(String[] str, int[] image) {
        this.canOrNotWork = str;
        this.canOrNotWorkImage = image;
    }

    @Override
    public int getCount() {
        return canOrNotWork.length;
    }

    @Override
    public Object getItem(int position) {
        return canOrNotWork[position];
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
                    R.layout.can_or_not_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.canOrNotIcon.setImageResource(canOrNotWorkImage[position]);
        holder.canOrNotNameTv.setText(canOrNotWork[position]);
        return convertView;
    }


    public class ViewHolder {
        @Bind(R.id.can_or_not_image_iv)
        ImageView canOrNotIcon;
        @Bind(R.id.can_or_not_name_tv)
        TextView canOrNotNameTv;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
