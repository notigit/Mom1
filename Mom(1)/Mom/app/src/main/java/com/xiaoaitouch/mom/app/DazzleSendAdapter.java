package com.xiaoaitouch.mom.app;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.module.DazzleListDataBean;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;
import com.xiaoaitouch.mom.util.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DazzleSendAdapter extends BaseAdapter {
    private Context mContext;
    private LinearLayout.LayoutParams mLayoutParams;
    private List<DazzleListDataBean> dazzleListDataBeans;
    private DisplayImageOptions displayImageOptions;

    public DazzleSendAdapter(Context context, int screenWidth) {
        this.mContext = context;
        this.mLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, screenWidth / 2);
        mLayoutParams.topMargin = Utils.convertDpToPixelInt(mContext, 6);
        this.displayImageOptions = DisplayImageOptionsUtils.getAdvDisplayImageOptions();
    }

    public void setData(List<DazzleListDataBean> data) {
        this.dazzleListDataBeans = data;
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return dazzleListDataBeans == null ? 0 : dazzleListDataBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return dazzleListDataBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.dazzle_send_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DazzleListDataBean dazzleListDataBean = dazzleListDataBeans.get(position);
        if (TextUtils.isEmpty(dazzleListDataBean.getMessage())) {
            holder.dazzleSendTitleTv.setVisibility(View.GONE);
        } else {
            holder.dazzleSendTitleTv.setVisibility(View.VISIBLE);
            holder.dazzleSendTitleTv.setText(dazzleListDataBean.getMessage());
        }
        if (dazzleListDataBean.getImg() != null && dazzleListDataBean.getImg().length() >= 1) {
            holder.dazzleSendImageIv.setVisibility(View.VISIBLE);
            lookPhotoImage(holder.dazzleSendImageIv, dazzleListDataBean.getImg());

        } else {
            holder.dazzleSendImageIv.setVisibility(View.GONE);
        }
        holder.dazzleSendImageIv.setLayoutParams(mLayoutParams);
        // 设置卡片的默认高度
        return convertView;
    }

    private void lookPhotoImage(ImageView dazzleSendImageIv, final String url) {
        ImageLoader.getInstance().displayImage(Configs.IMAGE_URL + url, dazzleSendImageIv, displayImageOptions);
        dazzleSendImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PhotoPreviewActivity.class);
                intent.putExtra("url", url);
                mContext.startActivity(intent);
            }
        });
    }


    public class ViewHolder {
        @Bind(R.id.dazzle_send_title_tv)
        TextView dazzleSendTitleTv;
        @Bind(R.id.dazzle_send_image_iv)
        ImageView dazzleSendImageIv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
