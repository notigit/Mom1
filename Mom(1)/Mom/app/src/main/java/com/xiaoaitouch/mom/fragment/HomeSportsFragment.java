package com.xiaoaitouch.mom.fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.MainModle;
import com.xiaoaitouch.mom.module.SportModle;
import com.xiaoaitouch.mom.module.StepsParams;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.util.StringUtils;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.view.HistogramView;

import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * <首页-运动>
 *
 * @author huxin
 * @data: 2016/1/6 17:02
 * @version: V1.0
 */
public class HomeSportsFragment extends BaseFragment {

    @Bind(R.id.home_sports_histogramview)
    HistogramView homeSportsHistogramview;
    @Bind(R.id.sports_weather_address_tv)
    TextView sportsWeatherAddressTv;
    @Bind(R.id.sports_weather_suggest_tv)
    TextView sportsWeatherSuggestTv;
    @Bind(R.id.sports_weather_aqi_tv)
    TextView sportsWeatherAqiTv;
    @Bind(R.id.sports_weather_date_tv)
    TextView sportsWeatherDateTv;
    @Bind(R.id.sports_weather_due_date_day_tv)
    TextView sportsWeatherDueDateDayTv;
    @Bind(R.id.sports_weather_suggest_sports_tv)
    TextView sportsWeatherSuggestSportsTv;
    @Bind(R.id.sports_message_tv)
    TextView sportsMessageTv;
    @Bind(R.id.sports_weather_left_wen_ddu_tv)
    TextView sportsWeatherLeftWenDduTv;
    @Bind(R.id.sports_weather_left_details_tv)
    TextView sportsWeatherLeftDetailsTv;
    @Bind(R.id.sports_message_tips_tv)
    TextView sportsTipsTv;

    private int width = 0;

    private MainModle mainModle = null;
    private UserModule userModule;
    private String mCurrentDate = "";
    private String mDueTime = "";//怀孕的日期
    private int mCurrentWeek = 1;//当前周
    private int days = 0;//周的余数
    private String weekDate = "";

    private String[] sportsArly;
    private String[] sportsMid;
    private String[] sportsLate;
    private StepsParams stepsParams = null;

    private String sportStr = "您已走了<font color = \"#F4426C\">" + 0
            + "</font>分钟，共计<font color = \"#F4426C\">" + 0
            + "</font>步了哟！";

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.home_sports_fragment, container, false);
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

        sportsArly = getActivity().getResources().getStringArray(R.array.home_sports_arly);
        sportsMid = getActivity().getResources().getStringArray(R.array.home_sports_mid);
        sportsLate = getActivity().getResources().getStringArray(R.array.home_sports_late);
        sportsMessageTv.setText(Html.fromHtml(sportStr));

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (stepsParams != null) {
                        homeSportsHistogramview.setWeekData(width, MyApplication.instance.getSportsYValues(), null, stepsParams.days + 1, true);
                    } else {
                        homeSportsHistogramview.setWeekData(width, MyApplication.instance.getSportsYValues(), null, 1, true);
                    }
                    break;
            }
        }
    };

    public void setSportsData(StepsParams stepsParams) {
        this.stepsParams = stepsParams;
        String content = stepsParams.stepsMsg + consumptionView(stepsParams.stepsNumber);
        sportsMessageTv.setText(Html.fromHtml(content));
        MyApplication.instance.setSportsYValues(stepsParams.days, com.xiaoaitouch.mom.train.Utils.comCalorie(stepsParams.stepsNumber));
        handler.sendEmptyMessage(1);
    }


    /**
     * 显示的消耗标识
     *
     * @param mStepCount
     * @return
     */
    private String consumptionView(int mStepCount) {
        float calorie = com.xiaoaitouch.mom.train.Utils.comCalorie(mStepCount);
        String content = "";
        if (calorie > 0 && calorie <= 50) {
            content = "接近消耗了<font color = \"#F4426C\">" + 1 + "</font>个苹果";
        } else if (calorie > 50 && calorie <= 100) {
            content = "接近消耗了<font color = \"#F4426C\">" + 2 + "</font>个苹果";
        } else if (calorie > 100 && calorie <= 200) {
            content = "接近消耗了<font color = \"#F4426C\">" + 1 + "</font>杯咖啡";
        } else if (calorie > 200 && calorie <= 300) {
            content = "接近消耗了<font color = \"#F4426C\">" + 1 + "</font>杯咖啡";
        } else if (calorie > 300 && calorie <= 400) {
            content = "接近消耗了<font color = \"#F4426C\">" + 2 + "</font>杯咖啡";
        } else if (calorie > 400 && calorie <= 600) {
            content = "接近消耗了<font color = \"#F4426C\">" + 1 + "</font>个汉堡";
        } else if (calorie > 600 && calorie <= 800) {
            content = "接近消耗了<font color = \"#F4426C\">" + 2 + "</font>个汉堡";
        } else if (calorie > 800 && calorie <= 1000) {
            content = "接近消耗了<font color = \"#F4426C\">" + 3 + "</font>个汉堡";
        }
        return content;
    }

    public void setViewData() {
        mainModle = MyApplication.instance.getMainModle();
        if (mainModle != null) {
            sportsWeatherAddressTv.setVisibility(View.VISIBLE);
            texturalData();
            viewData();
        } else {
            sportsWeatherAddressTv.setVisibility(View.INVISIBLE);
        }
        handler.sendEmptyMessage(1);
    }

    private void viewData() {
        setUserData();
        BDLocation mBDLocation = MyApplication.instance.mBDLocation;
        if (mBDLocation != null) {
            sportsWeatherAddressTv.setText(mBDLocation.getProvince() + " " + mBDLocation.getDistrict());
        }
        sportsWeatherSuggestTv.setText("体感温度 " + mainModle.getTq().getFl() + "°");
        sportsWeatherAqiTv.setText("AQI：" + mainModle.getTq().getQlty());
        sportsWeatherLeftWenDduTv.setText(mainModle.getTq().getTmp() + "°");
        sportsWeatherLeftDetailsTv.setText(mainModle.getTq().getTxt());

        Random random = new Random();
        String tips = "";
        if (mCurrentWeek <= 12) {
            int s = random.nextInt(sportsArly.length);
            sportsWeatherSuggestSportsTv.setText("不建议运动");
            tips = sportsArly[s].replace("PS", "\n\nPS:");
        } else if (mCurrentWeek >= 13 && mCurrentWeek < 27) {
            int s = random.nextInt(sportsMid.length);
            sportsWeatherSuggestSportsTv.setText("建议适当运动");
            tips = sportsMid[s].replace("PS", "\n\nPS:");
        } else if (mCurrentWeek >= 27) {
            int s = random.nextInt(sportsLate.length);
            sportsWeatherSuggestSportsTv.setText("不建议运动");
            tips = sportsLate[s].replace("PS", "\n\nPS:");
        }
        sportsTipsTv.setText(tips);
    }

    private void setUserData() {
        userModule = MyApplication.instance.getUserModule();
        mCurrentDate = StringUtils.getCurrentTimeSs();
        if (userModule != null) {
            if (!TextUtils.isEmpty(userModule.getLastMensesTime())
                    && !userModule.getLastMensesTime().equals("0")) {// 末次月经
                setGestationData(userModule.getLastMensesTime());
            } else if (!TextUtils.isEmpty(userModule.getDueTime())
                    && !userModule.getDueTime().equals("0")) {// 预产期
                String[] mDueTime = StringUtils.getStringFromDate(
                        Long.valueOf(userModule.getDueTime())).split("-");
                setGestationData(StringUtils.getDateFromStr(StringUtils
                        .getAddDate(mDueTime, -280)));
            }
        }
    }

    /**
     * 计算用户怀孕的周数和天数
     *
     * @param time
     */
    private void setGestationData(String time) {
        mDueTime = StringUtils.getStringFromDate(Long.valueOf(time));
        int number = StringUtils.getDateSpace(mDueTime, mCurrentDate);
        if (number >= 7) {
            mCurrentWeek = (number / 7) + 1;
            days = number % 7;
            if (mCurrentWeek >= 40) {
                mCurrentWeek = 40;
            }
        } else {
            this.days = number;
        }
        setDueData();
    }

    private void setDueData() {
        String weekAndDay = "";
        if (mCurrentWeek == 1) {
            weekDate = StringUtils.getAddDate(mDueTime.split("-"), days);
        } else {
            weekDate = StringUtils.getAddDate(mDueTime.split("-"), (mCurrentWeek - 1) * 7 + days);
        }
        String str[] = weekDate.split("-");
        if (mCurrentWeek >= 40) {
            sportsWeatherDateTv.setText(str[1] + "月" + str[2] + "日");
            sportsWeatherDueDateDayTv.setText("距离预产期0天");
        } else {
            int mCurrentWeekDay = StringUtils.getDateSpace(mDueTime, weekDate);
            int dueDay = 280 - mCurrentWeekDay;
            weekAndDay = mCurrentWeek + "周+" + days + "天";
            sportsWeatherDateTv.setText(str[1] + "月" + str[2] + "日" + " (" + weekAndDay + ")");
            sportsWeatherDueDateDayTv.setText("距离预产期" + dueDay + "天");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mainModle = MyApplication.instance.getMainModle();
            if (mainModle != null) {
                texturalData();
            }
            handler.sendEmptyMessage(1);
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
    }
}
