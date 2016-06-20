package com.xiaoaitouch.mom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.module.RecordContractionsModule;
import com.xiaoaitouch.mom.util.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Desc: 记宫缩
 * User: huxin
 * Date: 2016/3/6
 * Time: 11:24
 * FIXME
 */
public class RecordContractionsAdapter extends BaseAdapter {
    private List<RecordContractionsModule> recordContractionsModuleList;

    public void setData(List<RecordContractionsModule> recordContractionsModules) {
        this.recordContractionsModuleList = recordContractionsModules;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (recordContractionsModuleList == null) {
            return 0;
        } else {
            return recordContractionsModuleList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return recordContractionsModuleList.get(position);
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
                    R.layout.record_contractions_view_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RecordContractionsModule recordContractionsModule = recordContractionsModuleList.get(position);
        holder.startTimeTv.setText(recordContractionsModule.getStartTime());
        holder.continueTimeTv.setText(StringUtils.calculateTime(recordContractionsModule.getCxTime()));
        int min = recordContractionsModule.getJgTime() / 60;
        if (min > 60) {
            holder.intervalTv.setText("- -");
            holder.resultTv.setText("正常");
            holder.resultTv.setBackgroundResource(R.drawable.normal_bg);
        } else {
            holder.intervalTv.setText(StringUtils.calculateTime(recordContractionsModule.getJgTime()));
            if (min < 5) {
                holder.resultTv.setText("注意");
                holder.resultTv.setBackgroundResource(R.drawable.attention_bg);
            } else {
                holder.resultTv.setText("正常");
                holder.resultTv.setBackgroundResource(R.drawable.normal_bg);
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
