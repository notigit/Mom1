package com.xiaoaitouch.mom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 修改体重
 *
 * @author huxin
 * @data: 2016/1/9 14:01
 * @version: V1.0
 */
public class WeightView extends LinearLayout {
    private static final int DEFAULT_SELECTION = 10;
    private static final int ITEM_SPACE = 24;
    private static final float DEFAULT_WEIGHT = DEFAULT_SELECTION / 2; // 默认体重
    private static final int MAX_VALUE = 162;
    private int value = 70;

    @Bind(R.id.weight_gallery)
    Gallery gallery;
    @Bind(R.id.weigth_tvLeftNum)
    TextView tvLeftNum;
    @Bind(R.id.weigth_tvCenterNum)
    TextView tvCenterNum;
    @Bind(R.id.weigth_tvRightNum)
    TextView tvRightNum;

    private float curWeight = 0.0f;
    private MyAdapter myAdapter;
    private int mposition = 0;
    private OnWeightListener mOnWeightListener;

    public interface OnWeightListener {
        public void onWeight(float weight);
    }

    public void setOnWeightListener(OnWeightListener l) {
        mOnWeightListener = l;
    }

    public WeightView(Context context) {
        super(context);
        init(context);
    }

    public WeightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context con) {
        View view = LayoutInflater.from(con).inflate(R.layout.weight_layout,
                this, true);
        ButterKnife.bind(this, view);
        myAdapter = new MyAdapter();
        gallery.setAdapter(myAdapter);
        gallery.setSpacing(ITEM_SPACE);
        gallery.setSelection(DEFAULT_SELECTION);
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int position, long arg3) {
                mposition = position;
                curWeight = (float) (value + position) / 2;
                updateWidget(curWeight);
                mOnWeightListener.onWeight(curWeight);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        updateWidget(DEFAULT_WEIGHT);
    }

    public void setSelection(float value) {
        int mValue = (int) value - 35;
        gallery.setSelection(mValue * 2 + 1);
    }

    public void updateWidget(float num) {
        tvLeftNum.setText((num - 5) + "");
        tvCenterNum.setText(num + "KG");
        tvRightNum.setText((num + 5) + "");
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return MAX_VALUE;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                int number = position % 10;
                if (number == 0) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line_d, null);
                } else {
                    int select = position % 2;
                    switch (select) {
                        case 0:
                            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line_m, null);
                            break;
                        case 1:
                            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line_s, null);
                            break;
                    }
                }
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (mposition == position) {
                holder.mWeightTv.setBackgroundColor(getResources().getColor(R.color.white));
            } else {
                holder.mWeightTv.setBackgroundColor(getResources().getColor(R.color.no_select_weight_color));
            }
            return convertView;
        }

        public class ViewHolder {
            @Bind(R.id.weight_line_tv)
            TextView mWeightTv;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
