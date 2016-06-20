package com.xiaoaitouch.mom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.module.FetalMovementModule;
import com.xiaoaitouch.mom.module.MeasureHeartModule;
import com.xiaoaitouch.mom.sqlite.MeasureHeartTables;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Desc: 心率
 * User: huxin
 * Date: 2016/3/9
 * Time: 13:57
 * FIXME
 */
public class MeasureHeartAdapter extends BaseAdapter {
    private List<MeasureHeartModule> measureHeartModuleList;

    public void setData(List<MeasureHeartModule> measureHeartModules) {
        this.measureHeartModuleList = measureHeartModules;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (measureHeartModuleList == null) {
            return 0;
        } else {
            return measureHeartModuleList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return measureHeartModuleList.get(position);
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
                    R.layout.measure_heart_view_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MeasureHeartModule measureHeartModule = measureHeartModuleList.get(position);
        String currentDate = measureHeartModule.getDate();
        String str = currentDate.substring(5, 10) + "\n" + currentDate.substring(11, 16);
        holder.startTimeTv.setText(str);
        holder.continueTimeTv.setText(measureHeartModule.getGweek());
        holder.intervalTv.setText(measureHeartModule.getNumber() + "");
        int number = measureHeartModule.getNumber();
        if (number < 60) {
            holder.resultTv.setText("偏低↓");
            holder.resultTv.setBackgroundResource(R.drawable.attention_bg);
        } else if (number >= 60 && number <= 100) {
            holder.resultTv.setText("正常");
            holder.resultTv.setBackgroundResource(R.drawable.normal_bg);
        } else {
            holder.resultTv.setText("偏高↑");
            holder.resultTv.setBackgroundResource(R.drawable.abnormal_bg);
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
