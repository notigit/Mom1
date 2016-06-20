package com.xiaoaitouch.mom.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.module.ADueCameraModule;
import com.xiaoaitouch.mom.module.DueCameraModule;
import com.xiaoaitouch.mom.util.TimeFormat;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author huxin
 * @ClassName: DueCameraAdapter
 * @Description: 产检相册Adapter
 * @date 2015-12-19 下午5:17:31
 */
public class DueCameraAdapter extends MomAdapter<ADueCameraModule> {
    private int mWith;
    private Activity mActivity;

    public DueCameraAdapter(int with, Activity activity) {
        this.mWith = with;
        this.mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.due_camera_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ADueCameraModule dCameraBean = getItem(position);
        TimeFormat timeFormat = new TimeFormat(mActivity,
                dCameraBean.getCreateTime());
        holder.cardTypeTitleTv.setText(timeFormat.getTime());

        List<DueCameraModule> siList = dCameraBean.getDueCameras();
        int row = siList.size() / 4;
        int number = siList.size() % 4;
        if (number != 0) {
            row = row + 1;
        }
        if (row < 1) {
            row = row + 1;
        }
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, mWith * row);
        holder.dueCameraGridview.setLayoutParams(mLayoutParams);

        DueCameraImageAdapter mAdapter = new DueCameraImageAdapter(mWith,
                dCameraBean.getGroup(), mActivity);
        mAdapter.addAll(siList);
        holder.dueCameraGridview.setAdapter(mAdapter);
        return convertView;
    }

    public class ViewHolder {
        @Bind(R.id.due_camera_date_tv)
        TextView cardTypeTitleTv;
        @Bind(R.id.due_camera_gridview)
        GridView dueCameraGridview;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}