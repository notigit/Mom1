package com.xiaoaitouch.mom.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.module.CanOrNotListBean;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Desc: 能不能信息列表
 * User: huxin
 * Date: 2016/2/25
 * Time: 14:27
 * FIXME
 */
public class CanOrNotListAdapter extends BaseAdapter {
    private Context mContext;
    private DisplayImageOptions displayImageOptions;
    private List<CanOrNotListBean> canOrNotListBeanList = new ArrayList<CanOrNotListBean>();

    public CanOrNotListAdapter(Context context) {
        this.mContext = context;
        this.displayImageOptions = DisplayImageOptionsUtils.getDisplayImageOptions();
    }

    public void setData(List<CanOrNotListBean> list) {
        this.canOrNotListBeanList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return canOrNotListBeanList != null ? canOrNotListBeanList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return canOrNotListBeanList.get(position);
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
                    R.layout.can_or_not_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CanOrNotListBean canOrNotListBean = canOrNotListBeanList.get(position);
        ImageLoader.getInstance().displayImage(Configs.IMAGE_URL + canOrNotListBean.getSmallImg(), holder.canOrNotIcon, displayImageOptions);
        holder.canOrNotNameTv.setText(canOrNotListBean.getName());
        holder.canOrNotInformTv.setText(Html.fromHtml(canOrNotListBean.getAbs()));
        int type = canOrNotListBean.getType();
        int can = canOrNotListBean.getCan();
        if (type <= 9) {//0能不能吃/1能不能做
            switch (can) {//0，能做；1不能做；2慎做
                case 0:
                    holder.canOrNotTipsIcon.setImageResource(R.drawable.can_or_not_can_icon);
                    holder.canOrNotTipsTv.setText("能做");
                    holder.canOrNotTipsTv.setTextColor(mContext.getResources().getColor(R.color.can_or_not_can_color));
                    break;
                case 1:
                    holder.canOrNotTipsIcon.setImageResource(R.drawable.can_or_not_donot_icon);
                    holder.canOrNotTipsTv.setText("不能做");
                    holder.canOrNotTipsTv.setTextColor(mContext.getResources().getColor(R.color.can_or_not_donot_color));
                    break;
                case 2:
                    holder.canOrNotTipsIcon.setImageResource(R.drawable.can_or_not_careful_icon);
                    holder.canOrNotTipsTv.setText("慎做");
                    holder.canOrNotTipsTv.setTextColor(mContext.getResources().getColor(R.color.can_or_not_careful_color));
                    break;
            }
        } else {
            switch (can) {
                case 0:
                    holder.canOrNotTipsIcon.setImageResource(R.drawable.can_or_not_can_icon);
                    holder.canOrNotTipsTv.setText("能吃");
                    holder.canOrNotTipsTv.setTextColor(mContext.getResources().getColor(R.color.can_or_not_can_color));
                    break;
                case 1:
                    holder.canOrNotTipsIcon.setImageResource(R.drawable.can_or_not_donot_icon);
                    holder.canOrNotTipsTv.setText("不能吃");
                    holder.canOrNotTipsTv.setTextColor(mContext.getResources().getColor(R.color.can_or_not_donot_color));
                    break;
                case 2:
                    holder.canOrNotTipsIcon.setImageResource(R.drawable.can_or_not_careful_icon);
                    holder.canOrNotTipsTv.setText("慎吃");
                    holder.canOrNotTipsTv.setTextColor(mContext.getResources().getColor(R.color.can_or_not_careful_color));
                    break;
            }
        }

        return convertView;
    }


    public class ViewHolder {
        @Bind(R.id.can_or_not_icon_iv)
        ImageView canOrNotIcon;
        @Bind(R.id.can_or_not_name_tv)
        TextView canOrNotNameTv;
        @Bind(R.id.can_or_not_details_tv)
        TextView canOrNotInformTv;
        @Bind(R.id.can_or_not_tips_iv)
        ImageView canOrNotTipsIcon;
        @Bind(R.id.can_or_not_tips_tv)
        TextView canOrNotTipsTv;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}