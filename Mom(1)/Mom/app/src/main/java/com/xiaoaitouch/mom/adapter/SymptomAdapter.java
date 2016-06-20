package com.xiaoaitouch.mom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.module.SymptomModle;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 症状选择
 *
 * @author huxin
 * @data: 2016/1/8 11:05
 * @version: V1.0
 */
public class SymptomAdapter extends BaseAdapter {
    private List<SymptomModle> list;
    private Context mContext;

    public SymptomAdapter(Context context) {
        this.mContext = context;

    }

    public void setData(List<SymptomModle> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
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
                    R.layout.symptom_view_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final SymptomModle mSymptomBean = list.get(position);
        if (mSymptomBean.getIsOk() == 1) {
            holder.symptomTv
                    .setBackgroundResource(R.drawable.symptom_select_tv);
            holder.symptomTv.setTextColor(mContext.getResources().getColor(
                    R.color.symptom_histogram_tv_color));
        } else {
            holder.symptomTv
                    .setBackgroundResource(R.drawable.symptom_normal_tv);
            holder.symptomTv.setTextColor(mContext.getResources().getColor(
                    R.color.white));
        }
        holder.symptomTv.setText(mSymptomBean.getSymptom());
        return convertView;
    }

    public class ViewHolder {
        @Bind(R.id.symptom_tv)
        TextView symptomTv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public List<SymptomModle> getSymptomBeanList() {
        return list;
    }
}
