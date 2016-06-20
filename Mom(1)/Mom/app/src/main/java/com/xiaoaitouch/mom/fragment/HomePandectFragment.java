package com.xiaoaitouch.mom.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.MaWeightModle;
import com.xiaoaitouch.mom.module.MainModle;
import com.xiaoaitouch.mom.module.SportModle;
import com.xiaoaitouch.mom.module.StepsParams;
import com.xiaoaitouch.mom.module.SymptomCountModle;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.view.HistogramView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <首页-总览>
 *
 * @author huxin
 * @data: 2016/1/6 17:02
 * @version: V1.0
 */
public class HomePandectFragment extends BaseFragment {

    @Bind(R.id.home_pandect_sport_view)
    HistogramView homePandectSportView;
    @Bind(R.id.home_pandect_weight_view)
    HistogramView homePandectWeightView;
    @Bind(R.id.home_pandect_symptom_view)
    HistogramView homePandectSymptomView;

    private int width = 0;
    private int widths = 0;
    private MainModle mainModle = null;

    private Float[] weightYValues = new Float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private Float[] symptomYValues = new Float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private String[] symptomXValues = new String[10];
    private int mLength = 7;
    private ViewPager homeViewpager;
    private StepsParams stepsParams = null;
    private boolean isShow = false;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.home_pandect_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViewData() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int leftMain = Utils.convertDpToPixelInt(
                getActivity(), 40 * 8);
        width = (screenWidth - leftMain) / 7;

        int leftMains = Utils.convertDpToPixelInt(
                getActivity(), 30 * 11);
        widths = (screenWidth - leftMains) / 10;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    homePandectWeightView.setViewPagerPage(homeViewpager, 2, true);
                    homePandectSymptomView.setViewPagerPage(homeViewpager, 3, true);
                    homePandectWeightView.setHistogramBallIcon(2, true);
                    homePandectSymptomView.setHistogramBallIcon(3, true);


                    homePandectWeightView.setWeekData(width, weightYValues, null, mLength, true);
                    homePandectSymptomView.setWeekData(widths, symptomYValues, symptomXValues, 10, true);
                    break;

                case 2:
                    if (stepsParams != null) {
                        homePandectSportView.setWeekData(width, MyApplication.instance.getSportsYValues(), null, stepsParams.days + 1, true);
                    } else {
                        homePandectSportView.setWeekData(width, MyApplication.instance.getSportsYValues(), null, 1, true);
                    }
                    break;
                case 3:
                    homePandectSportView.setViewPagerPage(homeViewpager, 1, true);
                    homePandectSportView.setHistogramBallIcon(1, true);
                    homePandectSportView.setWeekData(width, MyApplication.instance.getSportsYValues(), null, mLength, true);
                    break;

            }
        }
    };

    public void setSportsData(StepsParams stepsParams) {
        this.stepsParams = stepsParams;
        MyApplication.instance.setSportsYValues(stepsParams.days, com.xiaoaitouch.mom.train.Utils.comCalorie(stepsParams.stepsNumber));
        handler.sendEmptyMessage(2);
    }

    public void setViewData(int length) {
        if (length == 0) {
            mLength = 7;
        } else {
            mLength = length;
        }
        mainModle = MyApplication.instance.getMainModle();
        if (mainModle != null) {
            texturalData();
        }
        handler.sendEmptyMessage(1);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mainModle = MyApplication.instance.getMainModle();
            if (mainModle != null) {
                texturalData();
            }
            if (!isShow) {
                handler.sendEmptyMessage(1);
                handler.sendEmptyMessage(3);
                isShow = true;
            } else {
                handler.sendEmptyMessage(1);
                handler.sendEmptyMessage(2);
            }
        }
    }

    /**
     * 构造数据
     */
    private void texturalData() {
        List<SportModle> si = mainModle.getSi();
        if (si != null && si.size() <= 7) {
            for (int i = 0; i < si.size(); i++) {
                SportModle sportModle = si.get(i);
                double calorie = sportModle.getCalorie();
                if (calorie > 0) {
                    MyApplication.instance.setSportsYValues(i, (float) calorie);
                }
            }
        }
        List<MaWeightModle> mw = mainModle.getMw();
        if (mw != null && mw.size() <= 7) {
            for (int i = 0; i < mw.size(); i++) {
                MaWeightModle maWeightModle = mw.get(i);
                weightYValues[i] = Float.valueOf(maWeightModle.getWeight());
            }
        }
        List<SymptomCountModle> zzCount = mainModle.getZzCount();
        if (zzCount != null && zzCount.size() <= 10) {
            for (int i = 0; i < zzCount.size(); i++) {
                SymptomCountModle symptomCountModle = zzCount.get(i);
                symptomYValues[i] = Float.valueOf(symptomCountModle.getCounts());
                symptomXValues[i] = symptomCountModle.getSymptom();
            }
        }
    }

    public void setViewPagerPage(ViewPager viewPager) {
        this.homeViewpager = viewPager;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
