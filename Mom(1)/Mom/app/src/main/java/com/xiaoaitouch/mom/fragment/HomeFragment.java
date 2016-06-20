package com.xiaoaitouch.mom.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.MyFragmentPagerAdapter;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.MainModle;
import com.xiaoaitouch.mom.module.StepsParams;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.util.StringUtils;
import com.xiaoaitouch.mom.util.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * <首页>
 *
 * @author huxin
 * @data: 2016/1/6 17:02
 * @version: V1.0
 */
public class HomeFragment extends BaseFragment {
    @Bind(R.id.home_fragment_title_ray)
    RelativeLayout homeFragmentTitleRay;
    @Bind(R.id.home_pandect_tv)
    TextView homePandectTv;
    @Bind(R.id.home_sports_tv)
    TextView homeSportsTv;
    @Bind(R.id.home_weight_tv)
    TextView homeWeightTv;
    @Bind(R.id.home_symptom_tv)
    TextView homeSymptomTv;
    @Bind(R.id.home_line_view)
    TextView homeLineView;
    @Bind(R.id.home_viewpager)
    ViewPager homeViewpager;

    private ArrayList<Fragment> fragmentList;
    private int currIndex;//当前页卡编号
    private Map<Integer, TextView> mTextViewMap = new HashMap<Integer, TextView>();

    private String mCity = "";
    private UserModule userModule;
    private String mDueTime = "";//怀孕的日期
    private String mCurrentDate = "";
    private String mStartDate = "";
    private int mCurrentWeek = 1;//当前周
    private int days = 0;

    private HomePandectFragment homePandectFragment = null;
    private HomeSportsFragment homeSportsFragment = null;
    private HomeWeightFragment homeWeightFragment = null;
    private HomeSymptomFragment homeSymptomFragment = null;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViewData() {
        Display display = getActivity().getWindow().getWindowManager().getDefaultDisplay();
        // 得到显示屏宽度
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        // 1/4屏幕宽度
        int tabLineLength = metrics.widthPixels / 4;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) homeLineView.getLayoutParams();
        lp.width = tabLineLength;
        homeLineView.setLayoutParams(lp);
        //tab的点击事件
        homePandectTv.setOnClickListener(new TxListener(0));
        homeSportsTv.setOnClickListener(new TxListener(1));
        homeWeightTv.setOnClickListener(new TxListener(2));
        homeSymptomTv.setOnClickListener(new TxListener(3));

        mTextViewMap.put(0, homePandectTv);
        mTextViewMap.put(1, homeSportsTv);
        mTextViewMap.put(2, homeWeightTv);
        mTextViewMap.put(3, homeSymptomTv);


        homePandectFragment = new HomePandectFragment();
        homeSportsFragment = new HomeSportsFragment();
        homeWeightFragment = new HomeWeightFragment();
        homeSymptomFragment = new HomeSymptomFragment();
        homePandectFragment.setViewPagerPage(homeViewpager);
        //ViewPager添加fragment
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(homePandectFragment);
        fragmentList.add(homeSportsFragment);
        fragmentList.add(homeWeightFragment);
        fragmentList.add(homeSymptomFragment);

        updateData();

        //给ViewPager设置适配器
        homeViewpager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        homeViewpager.setOffscreenPageLimit(3);
        homeViewpager.setCurrentItem(0);//设置当前显示标签页为第一页
        homeViewpager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
        setViewChange(0);
    }


    public void updateData() {
        setUserData();
        if (MyApplication.mBDLocation != null) {
            mCity = MyApplication.mBDLocation.getProvince();
            if (mCity != null && mCity.equals("重庆市")) {
                mCity = "重庆";
            }
        }
        String[] str = {mStartDate, mCity, (mCurrentWeek - 1) + ""};
        loadData(str);
    }

    public class TxListener implements View.OnClickListener {
        private int index = 0;

        public TxListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            homeViewpager.setCurrentItem(index, true);
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float arg1, int arg2) {
            RelativeLayout.LayoutParams ll = (RelativeLayout.LayoutParams) homeLineView
                    .getLayoutParams();

            //缓冲体重
            if (position == 2 && arg2 == 0){
                homeWeightFragment.onGenerate();
            }else{
                homeWeightFragment.onRelease();
            }


            System.out.println(position + "--------------pager------------->"+arg2);
            if (currIndex == position) {
                ll.leftMargin = (int) (currIndex * homeLineView.getWidth() + arg1
                        * homeLineView.getWidth());
            } else if (currIndex > position) {
                ll.leftMargin = (int) (currIndex * homeLineView.getWidth() - (1 - arg1) * homeLineView.getWidth());
            }

            homeLineView.setLayoutParams(ll);
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
        int number = StringUtils.getDateSpace(mDueTime, mCurrentDate) + 1;
        if (number >= 7) {
            mCurrentWeek = number / 7;
            days = number % 7;
            if (days != 0) {
                mCurrentWeek = mCurrentWeek + 1;
            }
            if (mCurrentWeek >= 40) {
                mCurrentWeek = 40;
            }
        } else {
            days = number;
        }
        mStartDate = StringUtils.getAddDate(mCurrentDate.split("-"), -days);
    }

    private void loadData(String[] str) {
        if (Utils.isNetworkConnected(getActivity())) {
            GsonTokenRequest<MainModle> request = new GsonTokenRequest<MainModle>(
                    Request.Method.POST,
                    Configs.SERVER_URL + "/v3/index",
                    new Response.Listener<JsonResponse<MainModle>>() {

                        @Override
                        public void onResponse(
                                JsonResponse<MainModle> response) {
                            switch (response.state) {
                                case Configs.UN_USE:
                                    showToast("版本过低请升级新版本");
                                    break;
                                case Configs.FAIL:
                                    showToast(response.msg);
                                    break;
                                case Configs.SUCCESS:
                                    handler.obtainMessage(1, response.data)
                                            .sendToTarget();
                                    break;
                            }
                        }
                    }, null) {

                @Override
                public Type getType() {
                    Type type = new TypeToken<JsonResponse<MainModle>>() {
                    }.getType();
                    return type;
                }
            };
            HttpApi.getMainData("/v3/index", request, str);
        } else {
            showToast("您处于网络断开状态！");
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    MyApplication.instance.setMainModle((MainModle) msg.obj);
                    homePandectFragment.setViewData(days);
                    homeSportsFragment.setViewData();
                    homeWeightFragment.setViewData(days);
                    homeSymptomFragment.setViewData(mCurrentWeek - 1);
                    break;
            }
        }
    };

    public void updateSportData(StepsParams stepsParams) {
        if (homePandectFragment != null && homeSportsFragment != null) {
            stepsParams.days = days;
            homePandectFragment.setSportsData(stepsParams);
            homeSportsFragment.setSportsData(stepsParams);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
