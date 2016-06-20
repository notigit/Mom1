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
import com.xiaoaitouch.mom.module.CollectListModule;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 收藏
 */
public class CollectListAdapter extends BaseAdapter {
    private List<CollectListModule> list = null;
    private DisplayImageOptions displayImageOptions;

    public CollectListAdapter() {
        this.displayImageOptions = DisplayImageOptionsUtils.getAdvDisplayImageOptions();
    }

    public void setData(List<CollectListModule> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
                    R.layout.collect_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CollectListModule collectListModule = list.get(position);
        if (collectListModule.getType() == 3) {
            holder.tipsLay.setVisibility(View.VISIBLE);
            holder.itemRay.setVisibility(View.GONE);
            holder.tipsTitleTv.setText(collectListModule.getTitle());
            holder.tipsContentTv.setText(collectListModule.getAbs());
        } else {
            holder.itemRay.setVisibility(View.VISIBLE);
            holder.tipsLay.setVisibility(View.GONE);
            holder.itemTitleTv.setText(collectListModule.getTitle());
            holder.itemContentTv.setText(collectListModule.getAbs());
            ImageLoader.getInstance().displayImage(Configs.IMAGE_URL + collectListModule.getImg(), holder.itemImageIv, displayImageOptions);
        }

        return convertView;
    }

    public class ViewHolder {
        @Bind(R.id.my_collection_tips_lay)
        LinearLayout tipsLay;
        @Bind(R.id.my_collection_tips_title_tv)
        TextView tipsTitleTv;
        @Bind(R.id.my_collection_content_tv)
        TextView tipsContentTv;
        @Bind(R.id.my_collection_item_ray)
        RelativeLayout itemRay;
        @Bind(R.id.my_collection_image_iv)
        ImageView itemImageIv;
        @Bind(R.id.my_collection_item_title_tv)
        TextView itemTitleTv;
        @Bind(R.id.my_collection_item_content_tv)
        TextView itemContentTv;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
