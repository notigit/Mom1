package com.xiaoaitouch.mom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.app.DazzleSendAdapter;
import com.xiaoaitouch.mom.module.DazzleListBean;
import com.xiaoaitouch.mom.util.Utility;
import com.xiaoaitouch.mom.view.MyListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Desc: 炫腹足迹
 * User: huxin
 * Date: 2016/2/29
 * Time: 17:06
 * FIXME
 */
public class DazzleAdapter extends BaseAdapter {
    private Context mContext;
    private DazzleSendAdapter dazzleSendAdapter;
    private int width = 0;
    private List<DazzleListBean> dazzleListBeanList;

    public DazzleAdapter(Context context, int screenWidth) {
        this.mContext = context;
        this.width = screenWidth;
    }

    public void setData(List<DazzleListBean> list) {
        this.dazzleListBeanList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dazzleListBeanList == null ? 0 : dazzleListBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return dazzleListBeanList.get(position);
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
                    R.layout.dazzle_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DazzleListBean dazzleListBean = dazzleListBeanList.get(position);
        holder.dazzleDueTimeTv.setText(dazzleListBean.getDate());
        holder.dazzleDueDayTv.setText(dazzleListBean.getGweek());
        dazzleSendAdapter = new DazzleSendAdapter(mContext, width);
        dazzleSendAdapter.setData(dazzleListBean.getData());
        holder.dazzleSendContentLv.setAdapter(dazzleSendAdapter);
        return convertView;
    }


    public class ViewHolder {
        @Bind(R.id.dazzle_due_time_tv)
        TextView dazzleDueTimeTv;
        @Bind(R.id.dazzle_due_day_tv)
        TextView dazzleDueDayTv;
        @Bind(R.id.dazzle_send_content_lv)
        MyListView dazzleSendContentLv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
