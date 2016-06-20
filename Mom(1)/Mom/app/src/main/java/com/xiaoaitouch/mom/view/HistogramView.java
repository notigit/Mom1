package com.xiaoaitouch.mom.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devsmart.android.ui.HorizontalListView;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.HistogramAdapter;
import com.xiaoaitouch.mom.module.HistogramModule;
import com.xiaoaitouch.mom.module.HistogramParams;
import com.xiaoaitouch.mom.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 柱状图显示
 *
 * @author huxin
 * @data: 2016/1/7 17:37
 * @version: V1.0
 */
public class HistogramView extends RelativeLayout {
    private LinearLayout histogramLay;
    private RelativeLayout histogramViewRay;
    private TextView histogramTopLeftTv;
    private TextView histogramTopRightTv;
    private View histogramTopLineView;
    private View histogramButtomLineView;
    public HorizontalListView histogramListview;
    private TextView histogramNoDataTv;

    private int topLineColor;
    private int textColor;
    private int buttomTextColor;
    private int type;//type==1代表是运动；type==2代表体重；type==3代表症状
    private int histogramBg;
    private HistogramAdapter histogramAdapter;
    private String[] weekData;
    private HistogramParams histogramParams;
    private Float[] yValues = new Float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private float maxValues = 0;//计算最大值
    private float averageValue = 0;//计算平均值
    private float totalValues = 0;
    private Context context;
    private DashedLine dashedLine = null;
    private DecimalFormat decimalFormat = new DecimalFormat("0.0");
    private ViewPager classRommViewpager;
    private boolean mIsflage = false;
    private int mPage = 0;


    private List<HistogramModule> list = new ArrayList<HistogramModule>();


    public HistogramView(Context contex) {
        this(contex, null);
    }

    public HistogramView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistogramView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.histogram_ray_item, this, true);
        histogramLay = (LinearLayout) view.findViewById(R.id.histogram_lay);
        histogramViewRay = (RelativeLayout) view.findViewById(R.id.histogram_view_ray);
        histogramTopLeftTv = (TextView) view.findViewById(R.id.histogram_top_left_tv);
        histogramTopRightTv = (TextView) view.findViewById(R.id.histogram_top_right_tv);
        histogramTopLineView = view.findViewById(R.id.histogram_top_line_view);
        histogramListview = (HorizontalListView) view.findViewById(R.id.histogram_listview);
        histogramButtomLineView = view.findViewById(R.id.histogram_buttom_line_view);
        histogramNoDataTv = (TextView) view.findViewById(R.id.histogram_no_data_tv);

        init(attrs, context);
    }

    public void init(AttributeSet attrs, final Context context) {
        this.context = context;
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.histogramView);
        topLineColor = t.getColor(R.styleable.histogramView_line_top_color, 0xffffff);
        textColor = t.getColor(R.styleable.histogramView_text_color, 0xffffff);
        buttomTextColor = t.getColor(R.styleable.histogramView_buttom_text_color, 0xffffff);
        type = t.getInteger(R.styleable.histogramView_type, 0);
        histogramBg = t.getResourceId(R.styleable.histogramView_bg, 0);
        weekData = getResources().getStringArray(R.array.week_name);

        histogramTopLineView.setBackgroundColor(topLineColor);
        histogramButtomLineView.setBackgroundColor(textColor);

        histogramTopLeftTv.setTextColor(textColor);
        histogramTopRightTv.setTextColor(textColor);
        histogramLay.setBackgroundResource(histogramBg);
        if (type == 3) {
            weekData = context.getResources().getStringArray(R.array.symptom_array);
            yValues = new Float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
        }

        histogramAdapter = new HistogramAdapter(context);
        histogramParams = new HistogramParams();
        histogramParams.type = type;
        histogramParams.buttomTextColor = buttomTextColor;
        histogramParams.textColor = textColor;
        histogramParams.topLineColor = topLineColor;
        histogramListview.setAdapter(histogramAdapter);
        histogramListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mIsflage){
                    classRommViewpager.setCurrentItem(mPage);
                }
            }
        });
    }

    public void setViewPagerPage(ViewPager viewPager, int page, boolean isflage) {
        this.classRommViewpager = viewPager;
        this.mPage = page;
        this.mIsflage = isflage;
    }

    /**
     * 更新右边的内容
     *
     * @author huxin
     * @data: 2016/1/8 11:56
     * @version: V1.0
     */
    public void rightTv(String content) {
        histogramTopRightTv.setText(content);
    }


    public void setHistogramBallIcon(int type, boolean isflage) {
        histogramParams.type = type;
        histogramParams.isflage = isflage;
    }

    /**
     * 构造实体类和adapter
     *
     * @param leftMain 左边的距离
     * @param object   柱线图实体类
     */
    public void setWeekData(int leftMain, Object object, String[] symptomXValues, int length, boolean isStartAnimation) {
        list.clear();
        totalValues = 0;
        averageValue = 0;
        if (object != null) {
            yValues = (Float[]) object;
        }
        if (symptomXValues != null && symptomXValues.length >= 1) {
            if (histogramParams.type == 3) {
                weekData = symptomXValues;
            }
        }
        maxValues = yValues[0];
        for (float i : yValues) {
            maxValues = maxValues > i ? maxValues : i;
        }
        for (int j = 0; j < yValues.length; j++) {
            totalValues = yValues[j] + totalValues;
        }
        averageValue = totalValues / length;
        for (int i = 0; i < weekData.length; i++) {
            HistogramModule histogramModule = new HistogramModule();
            histogramModule.setMaxValue(maxValues);
            histogramModule.setxValue(weekData[i]);
            histogramModule.setyValue(yValues[i]);
            histogramModule.setAverageValue(averageValue);
            list.add(histogramModule);
        }
        histogramAdapter.setLeftMain(leftMain, isStartAnimation);
        histogramAdapter.setData(histogramParams, list);
        histogramAdapter.notifyDataSetChanged();
        //生成平均值得划分线
        if (averageValue != 0) {
            histogramNoDataTv.setVisibility(View.GONE);
            if (dashedLine == null) {
                dashedLine = new DashedLine(context);
                dashedLine.setPaintColor(textColor);
                histogramViewRay.addView(dashedLine);
            }
            RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    Utils.convertDpToPixelInt(context, 1));
            //166
            int top = (int) (170 - (averageValue * 170 / maxValues));
            top = Utils.convertDpToPixelInt(
                    context, top);
            mLayoutParams.setMargins(0, top, 0, 0);
            dashedLine.setLayoutParams(mLayoutParams);
        } else {
            histogramNoDataTv.setTextColor(textColor);
            histogramNoDataTv.setVisibility(View.VISIBLE);
        }
        //显示平均值
        initRightValue(averageValue);

    }


    private void initRightValue(float value) {
        if (type == 1) {
            String mValue = decimalFormat.format(value);
            histogramTopLeftTv.setText("本周运动 (卡路里)");
            histogramTopRightTv.setText("日平均值：" + mValue);
        } else if (type == 2) {
            String mValue = decimalFormat.format(value);
            histogramTopLeftTv.setText("本周体重 (千克)");
            histogramTopRightTv.setText("日平均值：" + mValue);
        } else if (type == 3) {
            histogramTopLeftTv.setText("本周症状 (次数)");
            histogramTopRightTv.setText("");
        }
    }
}
