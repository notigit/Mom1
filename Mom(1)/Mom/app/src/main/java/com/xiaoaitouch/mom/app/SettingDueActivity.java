package com.xiaoaitouch.mom.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.MyFragmentPagerAdapter;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.fragment.SettingDueCalculationFragment;
import com.xiaoaitouch.mom.fragment.SettingDueInputFragment;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置预产期
 *
 * @author huxin
 * @data: 2016/1/13 16:54
 * @version: V1.0
 */
public class SettingDueActivity extends BaseFragmentActivity {
    @Bind(R.id.setting_due_input_date_tv)
    TextView settingDueInputDateTv;
    @Bind(R.id.setting_due_calculation_date_tv)
    TextView settingDueCalculationDateTv;
    @Bind(R.id.setting_due_line_view)
    TextView settingDueLineView;
    @Bind(R.id.setting_due_viewpager)
    ViewPager settingDueViewpager;

    private ArrayList<Fragment> fragmentList;
    private int currIndex;//当前页卡编号
    private Map<Integer, TextView> mTextViewMap = new HashMap<Integer, TextView>();
    private UserModule userModule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_due_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        userModule = MyApplication.instance.getUserModule();
        setHeader(getResources().getString(R.string.setting_due_activity));
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        // 得到显示屏宽度
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        // 1/4屏幕宽度
        int tabLineLength = metrics.widthPixels / 2;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) settingDueLineView.getLayoutParams();
        lp.width = tabLineLength;
        settingDueLineView.setLayoutParams(lp);
        //tab的点击事件
        settingDueInputDateTv.setOnClickListener(new TxListener(0));
        settingDueCalculationDateTv.setOnClickListener(new TxListener(1));

        mTextViewMap.put(0, settingDueInputDateTv);
        mTextViewMap.put(1, settingDueCalculationDateTv);

        //ViewPager添加fragment
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new SettingDueInputFragment());
        fragmentList.add(new SettingDueCalculationFragment());

        //给ViewPager设置适配器
        settingDueViewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        settingDueViewpager.setCurrentItem(0);//设置当前显示标签页为第一页
        settingDueViewpager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
        setViewChange(0);
    }

    public class TxListener implements View.OnClickListener {
        private int index = 0;

        public TxListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            settingDueViewpager.setCurrentItem(index, true);
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) settingDueLineView
                    .getLayoutParams();

            if (currIndex == arg0) {
                ll.leftMargin = (int) (currIndex * settingDueLineView.getWidth() + arg1
                        * settingDueLineView.getWidth());
            } else if (currIndex > arg0) {
                ll.leftMargin = (int) (currIndex * settingDueLineView.getWidth() - (1 - arg1) * settingDueLineView.getWidth());
            }
            settingDueLineView.setLayoutParams(ll);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageSelected(int arg0) {
            currIndex = arg0;
            setViewChange(arg0);
        }
    }

    private void setViewChange(int index) {
        for (int i = 0; i < mTextViewMap.size(); i++) {
            if (index == i) {
                int allChildAt = mTextViewMap.size();
                for (int j = 0; j < allChildAt; j++) {
                    mTextViewMap.get(index).setPressed(true);
                    mTextViewMap.get(index).setFocusable(true);
                    mTextViewMap.get(index).setSelected(true);
                    mTextViewMap.get(index).setTextSize(18f);
                }
            } else {
                int allChildAts = mTextViewMap.size();
                for (int j = 0; j < allChildAts; j++) {
                    mTextViewMap.get(i).setPressed(false);
                    mTextViewMap.get(i).setFocusable(false);
                    mTextViewMap.get(i).setSelected(false);
                    mTextViewMap.get(i).setTextSize(16f);
                }
            }
        }
    }



    @OnClick(R.id.top_title_back_tv)
    public void back() {
        onBackBtnClick();
    }
}
