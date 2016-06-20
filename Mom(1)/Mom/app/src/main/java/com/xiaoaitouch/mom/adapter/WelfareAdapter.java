package com.xiaoaitouch.mom.adapter;

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
import com.xiaoaitouch.mom.module.SymptomModle;
import com.xiaoaitouch.mom.module.WelfareBean;
import com.xiaoaitouch.mom.module.WelfareModule;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/1/26.
 */
public class WelfareAdapter extends BaseAdapter {
    private List<WelfareBean> moduleList;
    private int screenWidth = 0;

    private DisplayImageOptions mCardOptions;

    public WelfareAdapter() {
        this.mCardOptions = DisplayImageOptionsUtils
                .getCardDisplayImageOptions();
    }

    public void setViewData(List<WelfareBean> welfareModuleList) {
        this.moduleList = welfareModuleList;
        this.notifyDataSetChanged();
    }

    public void setScreenWidth(int with) {
        this.screenWidth = with;
    }

    @Override
    public int getCount() {
        return moduleList == null ? 0 : moduleList.size();
    }

    @Override
    public Object getItem(int position) {
        return moduleList.get(position);
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
                    R.layout.welfare_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WelfareBean welfareModule = moduleList.get(position);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, screenWidth / 2);
        mLayoutParams.setMargins(16, 16, 16, 16);
        holder.mAdvImageIv.setLayoutParams(mLayoutParams);
        ImageLoader.getInstance().displayImage(
                Configs.IMAGE_URL + welfareModule.getImg(), holder.mAdvImageIv,
                mCardOptions);
        return convertView;
    }

    public class ViewHolder {
        @Bind(R.id.welfare_image_iv)
        ImageView mAdvImageIv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
