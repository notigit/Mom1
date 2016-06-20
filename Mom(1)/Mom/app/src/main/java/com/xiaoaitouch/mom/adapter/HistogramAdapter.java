package com.xiaoaitouch.mom.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.module.HistogramModule;
import com.xiaoaitouch.mom.module.HistogramParams;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.view.StraightArrowView;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * <柱状图>
 *
 * @author huxin
 * @data: 2016/1/7 17:20
 * @version: V1.0
 */
public class HistogramAdapter extends BaseAdapter {
    private int leftMain = 0;
    private HistogramParams histogramParams;
    private List<HistogramModule> list;
    private boolean isStartAnimation = true;
    private DecimalFormat decimalFormat = new DecimalFormat("0");
    private DecimalFormat decimalFormats = new DecimalFormat("0.0");


    private Context mContext;

    public HistogramAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(HistogramParams histogramParams, List<HistogramModule> list) {
        this.histogramParams = histogramParams;
        this.list = list;
    }

    public void setLeftMain(int leftMain, boolean isStartAnimation) {
        this.leftMain = leftMain;
        this.isStartAnimation = isStartAnimation;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.histogram_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HistogramModule histogramModule = (HistogramModule) getItem(position);
        int type = histogramParams.type;
        LinearLayout.LayoutParams mLayoutParams = null;
        if (type == 3) {
            mLayoutParams = new LinearLayout.LayoutParams(
                    Utils.convertDpToPixelInt(mContext, 30),
                    Utils.convertDpToPixelInt(mContext, 170));
            holder.xValueTv.setTextSize(13f);
        } else {
            mLayoutParams = new LinearLayout.LayoutParams(
                    Utils.convertDpToPixelInt(mContext, 40),
                    Utils.convertDpToPixelInt(mContext, 170));
            holder.xValueTv.setTextSize(16f);
        }
        mLayoutParams.setMargins(leftMain, 0, 0, 0);
        mLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        holder.mLinearLayout.setLayoutParams(mLayoutParams);

        LinearLayout.LayoutParams mLayoutParamsArrView = null;
        if (histogramModule.getMaxValue() == 0) {
            mLayoutParamsArrView = new LinearLayout.LayoutParams(
                    2, 0);

        } else {
            mLayoutParamsArrView = new LinearLayout.LayoutParams(
                    2,
                    Utils.convertDpToPixelInt(
                            mContext,
                            histogramModule.getyValue() * 110
                                    / histogramModule.getMaxValue()));
        }
        if (histogramModule.getyValue() < 0.1) {
            mLayoutParamsArrView = new LinearLayout.LayoutParams(
                    2, 0);
            holder.numberTv.setVisibility(View.GONE);
        } else {
            holder.numberTv.setVisibility(View.VISIBLE);
        }
        holder.straightArrowView.setLayoutParams(mLayoutParamsArrView);
        getLineHeight(holder);
        String mValue = "";
        if (!histogramParams.isflage) {
            if (type == 1) {
                holder.ballIcon.setImageResource(R.drawable.sports_ball_icon);
                holder.straightArrowView.setPaintColor(histogramParams.textColor);
                mValue = decimalFormats.format(histogramModule.getyValue());
            } else if (type == 2) {
                holder.ballIcon.setImageResource(R.drawable.weight_ball_icon);
                holder.straightArrowView.setPaintColor(histogramParams.textColor);
                mValue = decimalFormats.format(histogramModule.getyValue());
            } else if (type == 3) {
                holder.ballIcon.setImageResource(R.drawable.symptom_ball_icon);
                holder.straightArrowView.setPaintColor(histogramParams.textColor);
                mValue = decimalFormat.format(histogramModule.getyValue());
            }
        } else {
            holder.straightArrowView.setPaintColor(mContext.getResources().getColor(R.color.white));
            holder.ballIcon.setImageResource(R.drawable.pandect_ball_icon);
            if (type == 3) {
                mValue = decimalFormat.format(histogramModule.getyValue());
            } else {
                mValue = decimalFormats.format(histogramModule.getyValue());
            }
        }
        holder.numberTv.setText(mValue);
        holder.numberTv.setTextColor(histogramParams.textColor);
        holder.xValueTv.setTextColor(histogramParams.buttomTextColor);
        holder.xValueTv.setText(histogramModule.getxValue());
        return convertView;
    }

    public class ViewHolder {
        @Bind(R.id.histogram_view_ray)
        LinearLayout mLinearLayout;
        @Bind(R.id.histogram_number_tv)
        TextView numberTv;
        @Bind(R.id.histogram_arrow_view)
        StraightArrowView straightArrowView;
        @Bind(R.id.histogram_ball_icon)
        ImageView ballIcon;
        @Bind(R.id.histogram_x_value_tv)
        TextView xValueTv;
        @Bind(R.id.histogram_ball_view_ray)
        LinearLayout mBallLinearLayout;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void getLineHeight(final ViewHolder viewHolder) {
        // 设置卡片的默认高度
        ViewTreeObserver vto2 = viewHolder.straightArrowView.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewHolder.straightArrowView.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);
                viewHolder.straightArrowView.updateLineHeight(viewHolder.straightArrowView.getHeight());
            }
        });
    }
}
