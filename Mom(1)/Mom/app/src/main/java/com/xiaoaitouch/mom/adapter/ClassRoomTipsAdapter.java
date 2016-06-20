package com.xiaoaitouch.mom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.xiaoaitouch.mom.R;

import butterknife.ButterKnife;

/**
 * 课堂-贴士
 *
 * @author huxin
 * @data: 2016/1/14 13:45
 * @version: V1.0
 */
public class ClassRoomTipsAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return position;
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
                    R.layout.class_room_tips_view_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }


    public class ViewHolder {


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
