package com.xiaoaitouch.mom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.module.BcModule;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Desc: B超单解读
 * User: huxin
 * Date: 2016/3/3
 * Time: 16:33
 * FIXME
 */
public class BUnscrambleAdapter extends BaseAdapter {
    private List<BcModule> moduleList;

    public void setData(List<BcModule> bcModules) {
        this.moduleList = bcModules;
        this.notifyDataSetChanged();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.b_unscramble_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BcModule bcModule = moduleList.get(position);
        holder.bUnscrambleTitleTv.setText(bcModule.getName());
        holder.bUnscrambleDataTv.setText(bcModule.getValue());
        return convertView;
    }


    public class ViewHolder {

        @Bind(R.id.b_unscramble_title_tv)
        TextView bUnscrambleTitleTv;
        @Bind(R.id.b_unscramble_data_tv)
        TextView bUnscrambleDataTv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
