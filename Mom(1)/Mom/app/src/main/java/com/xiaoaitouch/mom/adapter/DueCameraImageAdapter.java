package com.xiaoaitouch.mom.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.app.DueCameraDetailsActivity;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.module.DueCameraModule;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author huxin
 * @ClassName: DueCameraImageAdapter
 * @Description: TODO
 * @date 2015-12-19 下午5:35:29
 */
public class DueCameraImageAdapter extends MomAdapter<DueCameraModule> {
    private DisplayImageOptions mCardOptions;
    private int mWith;
    private int mGroup;
    private Activity mActivity;

    public DueCameraImageAdapter(int with, int group, Activity activity) {
        this.mWith = with;
        this.mActivity = activity;
        this.mGroup = group;
        this.mCardOptions = DisplayImageOptionsUtils
                .getDueCameraDisplayImageOptions();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.due_camera_image_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final DueCameraModule dueCamera = getItem(position);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                mWith, mWith);
        holder.imageIv.setLayoutParams(mLayoutParams);
        if (!TextUtils.isEmpty(dueCamera.getImg())) {
            ImageLoader.getInstance().displayImage(
                    Configs.IMAGE_URL + dueCamera.getImg(), holder.imageIv,
                    mCardOptions);
        }
        holder.imageIv.setTag(position);
        holder.imageIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View mView) {
                int mPosition = (Integer) mView.getTag();
                Intent mIntent = new Intent(mActivity,
                        DueCameraDetailsActivity.class);
                mIntent.putExtra("group", mGroup);
                mIntent.putExtra("position", mPosition);
                mActivity.startActivityForResult(mIntent, 1003);
            }
        });
        return convertView;
    }

    public class ViewHolder {
        @Bind(R.id.due_camera_image_iv)
        ImageView imageIv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
