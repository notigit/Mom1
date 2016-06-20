package com.xiaoaitouch.mom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.module.DueTimeModule;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Desc: 产检时间表
 * User: huxin
 * Date: 2016/3/4
 * Time: 13:33
 * FIXME
 */
public class DueTimeTableAdapter extends BaseAdapter {
    private List<DueTimeModule> dueTimeMoDuleList = null;
    private Context mContext;

    public DueTimeTableAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<DueTimeModule> dueTimeModules) {
        this.dueTimeMoDuleList = dueTimeModules;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (dueTimeMoDuleList == null) {
            return 0;
        } else {
            return dueTimeMoDuleList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return dueTimeMoDuleList.get(position);
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
                    R.layout.due_time_table_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DueTimeModule dueTimeModule = dueTimeMoDuleList.get(position);
        holder.dueTimeDateTv.setText(dueTimeModule.getDueDate());
        holder.dueTimeTitleTv.setText(dueTimeModule.getTitle());
        int type = dueTimeModule.getType();
        if (type == 1) {
            holder.dueTimeIsCurrentView.setVisibility(View.VISIBLE);
            holder.dueTimeIsCurrentRay.setVisibility(View.GONE);
            holder.dueTimeDateTv.setTextColor(mContext.getResources().getColor(R.color.due_time_befor_color));
            holder.dueTimeTitleTv.setTextColor(mContext.getResources().getColor(R.color.due_time_befor_color));
        } else if (type == 2) {
            holder.dueTimeIsCurrentView.setVisibility(View.GONE);
            holder.dueTimeIsCurrentRay.setVisibility(View.VISIBLE);
            holder.dueTimeDateTv.setTextColor(mContext.getResources().getColor(R.color.app_top_color));
            holder.dueTimeTitleTv.setTextColor(mContext.getResources().getColor(R.color.app_top_color));
        } else if (type == 3) {
            holder.dueTimeIsCurrentView.setVisibility(View.VISIBLE);
            holder.dueTimeIsCurrentRay.setVisibility(View.GONE);
            holder.dueTimeDateTv.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.dueTimeTitleTv.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        return convertView;
    }


    public class ViewHolder {
        @Bind(R.id.due_time_is_current_view)
        View dueTimeIsCurrentView;
        @Bind(R.id.due_time_is_current_ray)
        RelativeLayout dueTimeIsCurrentRay;
        @Bind(R.id.due_time_date_tv)
        TextView dueTimeDateTv;
        @Bind(R.id.due_time_title_tv)
        TextView dueTimeTitleTv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
