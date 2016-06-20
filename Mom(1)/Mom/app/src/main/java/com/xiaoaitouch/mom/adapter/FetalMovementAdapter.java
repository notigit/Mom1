package com.xiaoaitouch.mom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.module.FetalMovementModule;
import com.xiaoaitouch.mom.module.RecordContractionsModule;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Desc: 数胎动
 * User: huxin
 * Date: 2016/3/8
 * Time: 17:14
 * FIXME
 */
public class FetalMovementAdapter extends BaseAdapter {
    private List<FetalMovementModule> fetalMovementModuleList;

    public void setData(List<FetalMovementModule> fetalMovementModules) {
        this.fetalMovementModuleList = fetalMovementModules;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (fetalMovementModuleList == null && fetalMovementModuleList.size() == 0) {
            return 0;
        } else {
            return fetalMovementModuleList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return fetalMovementModuleList.get(position);
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
                    R.layout.fetal_movement_view_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FetalMovementModule fetalMovementModules = fetalMovementModuleList.get(position);
        holder.startTimeTv.setText(fetalMovementModules.getDate());
        holder.continueTimeTv.setText(fetalMovementModules.getStartTime());
        holder.intervalTv.setText(fetalMovementModules.getNumber() + "次");
        int result = fetalMovementModules.getNumber();
        if (result >= 3) {
            holder.resultTv.setText("正常");
            holder.resultTv.setBackgroundResource(R.drawable.normal_bg);
        } else {
            if (result < 2) {
                holder.resultTv.setText("异常");
                holder.resultTv.setBackgroundResource(R.drawable.abnormal_bg);
            } else if (result < 3) {
                holder.resultTv.setText("注意");
                holder.resultTv.setBackgroundResource(R.drawable.attention_bg);
            }
        }
        return convertView;
    }


    public class ViewHolder {
        @Bind(R.id.start_time_tv)
        TextView startTimeTv;
        @Bind(R.id.continue_time_tv)
        TextView continueTimeTv;
        @Bind(R.id.interval_tv)
        TextView intervalTv;
        @Bind(R.id.result_tv)
        TextView resultTv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
