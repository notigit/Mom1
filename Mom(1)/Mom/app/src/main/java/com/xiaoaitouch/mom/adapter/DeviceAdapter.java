package com.xiaoaitouch.mom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.info.BeaconInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/12.
 */
public class DeviceAdapter extends BaseAdapter {
    private ArrayList<BeaconInfo> list = null;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int tagPosition = 1000;

    public DeviceAdapter(Context con) {
        this.mContext = con;
        this.mLayoutInflater = LayoutInflater.from(con);
    }

    public void setData(ArrayList<BeaconInfo> arrayList) {
        this.list = arrayList;
        this.notifyDataSetChanged();
    }

    public void updateStatus(String mac) {
        for (int i = 0; i < list.size(); i++) {
            BeaconInfo info = list.get(i);
            if (info.getUuid().equals(mac)) {
                tagPosition = i;
            }
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        } else
            return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.list_item_device_layout, null);
            holder.tvName = (TextView) view.findViewById(R.id.list_device_tvName);
            holder.tvStatus = (TextView) view.findViewById(R.id.list_device_tvStatus);
            holder.imgBattery = (ImageView) view.findViewById(R.id.list_device_imgBattery);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        BeaconInfo info = list.get(position);
        holder.tvName.setText(info.getName());

        holder.tvStatus.setText(info.getUuid());
//        if (position == tagPosition){
//            holder.tvStatus.setText("已连接");
//        }


        return view;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvStatus;
        ImageView imgBattery;
    }

}
