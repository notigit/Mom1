package com.xiaoaitouch.mom.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.MyFragmentPagerAdapter;
import com.xiaoaitouch.mom.fragment.CanOrNotEatFragment;
import com.xiaoaitouch.mom.fragment.CanOrNotWorkFragment;
import com.xiaoaitouch.mom.fragment.FetusGrowthFragment;
import com.xiaoaitouch.mom.fragment.PregnantHealthFragment;
import com.xiaoaitouch.mom.util.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 发育评测
 * User: huxin
 * Date: 2016/3/1
 * Time: 15:45
 * FIXME
 */
public class GrowthEvaluatingActivity extends BaseFragmentActivity {
    @Bind(R.id.pregnant_health_tv)
    TextView pregnantHealthTv;
    @Bind(R.id.fetus_growth_tv)
    TextView fetusGrowthTv;
    @Bind(R.id.growth_evaluating_line_view)
    TextView growthEvaluatingLineView;
    @Bind(R.id.growth_evaluating_viewpager)
    ViewPager growthEvaluatingViewpager;

    private int mPage = 0;
    private int currIndex = 0;//当前页卡编号
    private Map<Integer, TextView> mTextViewMap = new HashMap<Integer, TextView>();
    private ArrayList<Fragment> fragmentList;
    private PregnantHealthFragment pregnantHealthFragment;
    private FetusGrowthFragment fetusGrowthFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.hideAutoSoftInput(getWindow());
        setContentView(R.layout.growth_evaluating_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        setHeader("孕儿健康评测");
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        // 得到显示屏宽度
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        // 1/4屏幕宽度
        int tabLineLength = metrics.widthPixels / 2;

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) growthEvaluatingLineView.getLayoutParams();
        lp.width = tabLineLength;
        growthEvaluatingLineView.setLayoutParams(lp);
        //tab的点击事件
        pregnantHealthTv.setOnClickListener(new TxListener(0));
        fetusGrowthTv.setOnClickListener(new TxListener(1));

        mTextViewMap.put(0, pregnantHealthTv);
        mTextViewMap.put(1, fetusGrowthTv);

        //ViewPager添加fragment
        fragmentList = new ArrayList<Fragment>();
        pregnantHealthFragment = new PregnantHealthFragment();
        fetusGrowthFragment = new FetusGrowthFragment();

        fragmentList.add(pregnantHealthFragment);
        fragmentList.add(fetusGrowthFragment);


        //给ViewPager设置适配器
        growthEvaluatingViewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        growthEvaluatingViewpager.setOffscreenPageLimit(2);
        growthEvaluatingViewpager.setCurrentItem(0);
        growthEvaluatingViewpager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
    }

    public class TxListener implements View.OnClickListener {
        private int index = 0;

        public TxListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            growthEvaluatingViewpager.setCurrentItem(index, true);
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            RelativeLayout.LayoutParams ll = (RelativeLayout.LayoutParams) growthEvaluatingLineView
                    .getLayoutParams();

            if (currIndex == arg0) {
                ll.leftMargin = (int) (currIndex * growthEvaluatingLineView.getWidth() + arg1
                        * growthEvaluatingLineView.getWidth());
            } else if (currIndex > arg0) {
                ll.leftMargin = (int) (currIndex * growthEvaluatingLineView.getWidth() - (1 - arg1) * growthEvaluatingLineView.getWidth());
            }
            growthEvaluatingLineView.setLayoutParams(ll);
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
    public void onBack() {
        onBackBtnClick();
    }
}
