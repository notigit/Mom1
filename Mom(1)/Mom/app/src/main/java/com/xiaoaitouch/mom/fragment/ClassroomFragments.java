package com.xiaoaitouch.mom.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.AddToolsListAdapter;
import com.xiaoaitouch.mom.adapter.MyFragmentPagerAdapter;
import com.xiaoaitouch.mom.app.AddToolsActivity;
import com.xiaoaitouch.mom.app.BUnscrambleActivity;
import com.xiaoaitouch.mom.app.CanOrNotActivity;
import com.xiaoaitouch.mom.app.DazzleActivity;
import com.xiaoaitouch.mom.app.DueTimeTableActivity;
import com.xiaoaitouch.mom.app.EasemobActivity;
import com.xiaoaitouch.mom.app.FetalMovementActivity;
import com.xiaoaitouch.mom.app.GrowthEvaluatingActivity;
import com.xiaoaitouch.mom.app.MeasureHeartActivity;
import com.xiaoaitouch.mom.app.RecordContractionsActivity;
import com.xiaoaitouch.mom.app.WebViewActivity;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.AddToolsModule;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.sqlite.AddToolsTables;
import com.xiaoaitouch.mom.sqlite.GestationTables;
import com.xiaoaitouch.mom.util.StringUtils;
import com.xiaoaitouch.mom.util.Utility;
import com.xiaoaitouch.mom.view.ArcMenu;
import com.xiaoaitouch.mom.view.ArcMenu.Status;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * <课堂>
 *
 * @author huxin
 * @data: 2016/1/7 10:44
 * @version: V1.0
 */
public class ClassroomFragments extends BaseFragment {


    @Bind(R.id.baby_weight_tv)
    TextView babyWeightTv;
    @Bind(R.id.baby_height_tv)
    TextView babyHeightTv;
    @Bind(R.id.baby_due_progressbar)
    ProgressBar babyDueProgressbar;
    @Bind(R.id.baby_viewPager)
    ViewPager babyViewPager;
    @Bind(R.id.baby_due_day_tv)
    TextView babyDueDayTv;
    @Bind(R.id.due_selected_week_tv)
    TextView dueSelectedWeekTv;
    @Bind(R.id.baby_due_date_tv)
    TextView babyDueDateTv;
    @Bind(R.id.class_room_add_tools_ray)
    RelativeLayout classRoomAddToolsRay;
    @Bind(R.id.class_room_add_tools_view)
    View classRoomAddToolsView;
    @Bind(R.id.class_room_tips_tv)
    TextView classRoomTipsTv;
    @Bind(R.id.class_room_symtom_tv)
    TextView classRoomSymtomTv;
    @Bind(R.id.class_room_recipe_tv)
    TextView classRoomRecipeTv;
    @Bind(R.id.add_toos_details_lv)
    ListView addToosDetailsLv;
    @Bind(R.id.early_tv)
    TextView earlyTv;
    @Bind(R.id.during_tv)
    TextView duringTv;
    @Bind(R.id.late_tv)
    TextView lateTv;
    @Bind(R.id.big_menu)
    ArcMenu bigMenu;

    private long userId = 0;
    private UserModule userModule;
    private List<AddToolsModule> addToolsModule = null;
    private AddToolsListAdapter addToolsListAdapter;
    private int dueTypeTime = 1;
    private String mDueTime = "";//怀孕的日期
    private String mCurrentDate = "";
    private int mCurrentWeek = 1;//当前周
    private int days = 0;//周的余数
    private String weekAndDay = "";
    private String weekDate = "";
    private String mDueDayStr = "";

    private ArrayList<Fragment> fragmentList;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private int stageCode = 1;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.class_room_fragments, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViewData() {
        setUserData();
        addBaByDataView();
        addListDataRefresh();
        setClassRoomMidleView(mCurrentWeek);
        bigMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                int stageCode = (Integer) view.getTag();
                changeStageCode(stageCode);
                setClassRoomViewData(stageCode);
            }
        });
        addToosDetailsLv.setFocusable(false);
    }

    /***
     * 数据更新
     */
    public void setUpdateData() {
        setUserData();
        initBabyData(mCurrentWeek);
        setClassRoomMidleView(mCurrentWeek);
        setCurrentItems(mCurrentWeek - 1);
    }

    private void setClassRoomViewData(int stageCode) {
        if (stageCode == 1) {
            dueTypeTime = 1;
            setCurrentItems(1);
        } else if (stageCode == 2) {
            dueTypeTime = 2;
            setCurrentItems(13);
        } else if (stageCode == 3) {
            dueTypeTime = 3;
            setCurrentItems(28);
        }
    }

    private void changeStageCode(int stageCode) {
        if (stageCode == 1) {
            earlyTv.setText("早");
            earlyTv.setTag(1);
            duringTv.setText("中");
            duringTv.setTag(2);
            lateTv.setText("晚");
            lateTv.setTag(3);
        } else if (stageCode == 2) {
            earlyTv.setText("中");
            earlyTv.setTag(2);
            duringTv.setText("早");
            duringTv.setTag(1);
            lateTv.setText("晚");
            lateTv.setTag(3);
        } else if (stageCode == 3) {
            earlyTv.setText("晚");
            earlyTv.setTag(3);
            duringTv.setText("早");
            duringTv.setTag(1);
            lateTv.setText("中");
            lateTv.setTag(2);
        }
    }

    private void addBaByDataView() {
        fragmentList = new ArrayList<Fragment>();
        for (int i = 1; i <= 40; i++) {
            fragmentList.add(BabyFragment.newInstance(i));
        }
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList);
        babyViewPager.setAdapter(myFragmentPagerAdapter);
        babyViewPager.setOffscreenPageLimit(3);
        babyViewPager.setCurrentItem(mCurrentWeek - 1);
        babyViewPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
        initBabyData(mCurrentWeek);
    }

    public void setCurrentItems(int page) {
        babyViewPager.setCurrentItem(page, false);
    }


    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }

        @Override
        public void onPageSelected(int position) {
            initBabyData(position + 1);
        }
    }

    private void initBabyData(int defaultValues) {
        if (bigMenu.isOpen()) {
            Status status = Status.CLOSE;
            bigMenu.toggleMenu(300);
        }
        BabyFragment babyFragment = (BabyFragment) fragmentList.get(defaultValues - 1);
        babyFragment.setWeek(defaultValues);
        GestationTables gestationTables = new GestationTables(getActivity());
        GestationTables.GestationInfo gestationInfo = gestationTables.getWeekDate(defaultValues);
        if (gestationInfo != null) {
            String height = gestationInfo.getHeight();
            String weight = gestationInfo.getWeight();
            babyHeightTv.setText(height.substring(0, 2) + "\n"
                    + height.substring(2, height.length()));
            babyWeightTv.setText(weight.substring(0, 2) + "\n"
                    + weight.substring(2, weight.length()));
        }
        int progress = ((defaultValues) * 100) / 40;
        babyDueProgressbar.setProgress(progress);
        setDueData(defaultValues);
        if (defaultValues <= 12) {
            dueTypeTime = 1;
        } else if (defaultValues >= 13 && defaultValues <= 27) {
            dueTypeTime = 2;
        } else {
            dueTypeTime = 3;
        }
        setClassRoomMidleView(defaultValues);
    }

    private void setDueData(int week) {
        if (mCurrentWeek == week) {
            weekAndDay = mCurrentWeek + "周+" + days + "天";
            if (mCurrentWeek == 1) {
                weekDate = StringUtils.getAddDate(mDueTime.split("-"), days);
            } else {
                weekDate = StringUtils.getAddDate(mDueTime.split("-"), (mCurrentWeek - 1) * 7 + days);
            }
        } else {
            weekDate = StringUtils.getAddDate(mDueTime.split("-"), week * 7);
            weekAndDay = week + "周+7天";
        }
        if (week >= 40) {
            if (!TextUtils.isEmpty(weekDate)) {
                String str[] = weekDate.split("-");
                babyDueDateTv.setText(str[1] + "月" + str[2] + "日");
            }
            babyDueDayTv.setText("宝宝还有0天出生");
        } else {
            if (!TextUtils.isEmpty(weekDate)) {
                String str[] = weekDate.split("-");
                babyDueDateTv.setText(str[1] + "月" + str[2] + "日" + " (" + weekAndDay + ")");
            }
            int mCurrentWeekDay = StringUtils.getDateSpace(mDueTime, weekDate);
            int dueDay = 280 - mCurrentWeekDay;
            this.mDueDayStr = "宝宝还有" + dueDay + "天出生";
            babyDueDayTv.setText(mDueDayStr);
        }
    }


    private void setUserData() {
        userModule = MyApplication.instance.getUserModule();
        if (userModule != null) {
            userId = userModule.getUserId();
        }
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
            days = number;
        }
    }

    private void addListDataRefresh() {
        addToolsModule = AddToolsTables.queryAddTools(getActivity(), userId);
        if (addToolsModule.size() >= 1) {
            classRoomAddToolsRay.setVisibility(View.GONE);
            classRoomAddToolsView.setVisibility(View.VISIBLE);
            addToosDetailsLv.setDividerHeight(0);
            addToolsListAdapter = new AddToolsListAdapter(getActivity(), addToolsModule);
            addToosDetailsLv.setAdapter(addToolsListAdapter);
            Utility.setListViewHeightBasedOnChildren(addToosDetailsLv);
        } else {
            classRoomAddToolsRay.setVisibility(View.VISIBLE);
            classRoomAddToolsView.setVisibility(View.GONE);
        }
    }

    private void setClassRoomMidleView(int week) {
        String title = "";
        if (dueTypeTime == 1) {
            title = "早期";
        } else if (dueTypeTime == 2) {
            title = "中期";
        } else if (dueTypeTime == 3) {
            title = "晚期";
        }
        classRoomTipsTv.setText(title + "贴士");
        classRoomSymtomTv.setText(title + "症状");
        classRoomRecipeTv.setText(title + "食谱");
        if (week <= 12) {
            stageCode = 1;
            earlyTv.setText("早");
            earlyTv.setTag(1);
            duringTv.setText("中");
            duringTv.setTag(2);
            lateTv.setText("晚");
            lateTv.setTag(3);
        } else if (week >= 13 && week <= 27) {
            stageCode = 2;
            earlyTv.setText("中");
            earlyTv.setTag(2);
            duringTv.setText("早");
            duringTv.setTag(1);
            lateTv.setText("晚");
            lateTv.setTag(3);
        } else {
            stageCode = 3;
            earlyTv.setText("晚");
            earlyTv.setTag(3);
            duringTv.setText("早");
            duringTv.setTag(1);
            lateTv.setText("中");
            lateTv.setTag(2);
        }
    }

    @OnClick(R.id.due_selected_week_tv)
    public void currentWeek() {
        setCurrentItems(mCurrentWeek - 1);
    }

    @OnClick({R.id.class_room_add_tools_ray, R.id.add_toos_details_tv})
    public void addTools() {
        Intent intent = new Intent(getActivity(), AddToolsActivity.class);
        ClassroomFragments.this.startActivityForResult(intent, 2008);
    }

    @OnClick(R.id.class_room_consult_lay)
    public void openEasemobActivity() {
        startIntent(EasemobActivity.class);
    }

    @OnItemClick(R.id.add_toos_details_lv)
    public void OnItemClickAddToolsLt(int position) {
        AddToolsModule addToolsModule = (AddToolsModule) addToolsListAdapter.getItem(position);
        int type = addToolsModule.getIndexs();
        switch (type) {
            case 0://产检时间表
                startIntent(DueTimeTableActivity.class);
                break;
            case 1://B超单解读
                startIntent(BUnscrambleActivity.class);
                break;
            case 2://数胎动
                startIntent(FetalMovementActivity.class);
                break;
            case 3://记宫缩
                startIntent(RecordContractionsActivity.class);
                break;
            case 4://炫腹足迹
                startIntent(DazzleActivity.class);
                break;
            case 5://测心率
                startIntent(MeasureHeartActivity.class);
                break;
            case 6://能不能
                startIntent(CanOrNotActivity.class);
                break;
            case 7://发育评测
                startIntent(GrowthEvaluatingActivity.class);
                break;
        }
    }

    @OnClick(R.id.class_room_recipe_lay)
    public void openWebView() {
        Bundle bundle = new Bundle();
        bundle.putString("title", classRoomRecipeTv.getText().toString());
        bundle.putString("url", Configs.SERVER_URL + "/html/pregnancy/cp/cpztList.html?stageCode=" + dueTypeTime);
        bundle.putInt("type", 2);
        startIntent(WebViewActivity.class, bundle);
    }

    @OnClick(R.id.class_room_symtom_lay)
    public void openSymtomWebView() {
        Bundle bundle = new Bundle();
        bundle.putString("title", classRoomSymtomTv.getText().toString());
        bundle.putString("url", Configs.SERVER_URL + "/html/pregnancy/zz/zzSearch.html?stageCode=" + dueTypeTime + "&userId=" + userId);
        bundle.putInt("type", 2);
        startIntent(WebViewActivity.class, bundle);
    }

    @OnClick(R.id.class_room_tips_lay)
    public void openTipsWebView() {
        Bundle bundle = new Bundle();
        bundle.putString("title", classRoomTipsTv.getText().toString());
        bundle.putString("url", Configs.SERVER_URL + "/html/pregnancy/ts/tsList.html?stageCode=" + dueTypeTime);
        bundle.putInt("type", 2);
        startIntent(WebViewActivity.class, bundle);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2008) {
            addListDataRefresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
