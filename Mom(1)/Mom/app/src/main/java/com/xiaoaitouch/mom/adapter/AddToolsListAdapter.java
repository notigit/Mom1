package com.xiaoaitouch.mom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.module.AddToolsModule;
import com.xiaoaitouch.mom.sqlite.AddToolsTables;
import com.xiaoaitouch.mom.view.ShSwitchView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Desc: 添加的工具列表
 * User: huxin
 * Date: 2016/2/25
 * Time: 11:34
 * FIXME
 */
public class AddToolsListAdapter extends BaseAdapter {
    private Context mContext;
    private List<AddToolsModule> addToolsModule = null;
    private int[] addToolsImage = {R.drawable.add_tools_image1, R.drawable.add_tools_image2, R.drawable.add_tools_image3
            , R.drawable.add_tools_image4, R.drawable.add_tools_image5, R.drawable.add_tools_image6, R.drawable.add_tools_image7,
            R.drawable.add_tools_image8};

    public AddToolsListAdapter(Context context, List<AddToolsModule> addToolsModuleList) {
        this.mContext = context;
        this.addToolsModule = addToolsModuleList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return addToolsModule != null ? addToolsModule.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return addToolsModule.get(position);
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
                    R.layout.add_tools_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AddToolsModule addtools = addToolsModule.get(position);
        holder.addToosIconIv.setImageResource(addToolsImage[addtools.getIndexs()]);
        holder.addToosDetailsTv.setText(addtools.getInform());
        holder.addToosNameTv.setText(addtools.getName());


        return convertView;
    }



    public class ViewHolder {
        @Bind(R.id.add_toos_icon_iv)
        ImageView addToosIconIv;
        @Bind(R.id.add_toos_name_tv)
        TextView addToosNameTv;
        @Bind(R.id.add_toos_details_tv)
        TextView addToosDetailsTv;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}