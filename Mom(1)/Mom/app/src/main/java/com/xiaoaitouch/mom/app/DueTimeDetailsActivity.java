package com.xiaoaitouch.mom.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.broadcast.CallAlarm;
import com.xiaoaitouch.mom.module.DueTimeModule;
import com.xiaoaitouch.mom.sqlite.DueTimeTables;
import com.xiaoaitouch.mom.util.StringUtils;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.view.CommonWheelView;
import com.xiaoaitouch.mom.view.CustomScrollViewss;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 产检详情
 * User: huxin
 * Date: 2016/3/4
 * Time: 14:18
 * FIXME
 */
public class DueTimeDetailsActivity extends BaseActivity {
    @Bind(R.id.due_time_details_webview)
    WebView dueTimeDetailsWebview;
    @Bind(R.id.due_time_details_title_tv)
    TextView dueTimeDetailsTitleTv;
    @Bind(R.id.due_time_details_week_tv)
    TextView dueTimeDetailsWeekTv;
    @Bind(R.id.due_time_details_is_select_tv)
    TextView dueTimeDetailsIsSelectTv;
    @Bind(R.id.due_time_details_time_tv)
    TextView dueTimeDetailsTimeTv;
    @Bind(R.id.due_time_details_remind_time_tv)
    TextView dueTimeDetailsRemindTimeTv;
    @Bind(R.id.due_time_details_due_time_ray)
    RelativeLayout dueTimeDetailsDueTimeRay;
    @Bind(R.id.due_time_details_remind_time_ray)
    RelativeLayout dueTimeDetailsRemindTimeRay;
    @Bind(R.id.due_time_choose_day_view)
    CommonWheelView dueTimeChooseDayView;
    @Bind(R.id.due_time_choose_hour_view)
    CommonWheelView dueTimeChooseHourView;
    @Bind(R.id.due_time_choose_minute_view)
    CommonWheelView dueTimeChooseMinuteView;
    @Bind(R.id.due_time_choose_remind_lay)
    LinearLayout dueTimeChooseRemindLay;
    @Bind(R.id.due_time_details_scrollview)
    ScrollView dueTimeDetailsScrollview;
    @Bind(R.id.due_time_details_due_time_lay)
    RelativeLayout dueTimeDetailsDueTimeLay;
    //产检时间表
    @Bind(R.id.due_time_details_year_view)
    CommonWheelView dueTimeDetailsYearView;
    @Bind(R.id.due_time_details_mouch_view)
    CommonWheelView dueTimeDetailsMouchView;
    @Bind(R.id.due_time_details_day_view)
    CommonWheelView dueTimeDetailsDayView;

    private int position = 1;
    private int week = 1;
    private int type = 1;
    ///////////////////////////////////
    private NumericWheelAdapter remindNumericWheelAdapter;
    private String[] dueWeekStr = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三"};
    private int remindDayNumber = 0;
    private String remindDay = "产检前一天";
    private String remindHour = "8";
    private String remindMinute = "00";
    private String remindStr = "";
    private String[] remindDue = {"产检当天", "产检前一天", "产检前三天"};
    private DueTimeModule dueTimeModule = null;
    ///////////////////////////////////
    private NumericWheelAdapter dueNumericWheelAdapter;
    private String dueYear = "";
    private String duemonch = "";
    private String dueDay = "";
    private String dueTimeStr = "";
    private NumericWheelAdapter dayNumericWheelAdapter;

    private static int START_YEAR = 2015, END_YEAR = 2020;
    private String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
    private String[] months_little = {"4", "6", "9", "11"};
    private final List<String> list_big = Arrays.asList(months_big);
    private final List<String> list_little = Arrays.asList(months_little);
    private int year = 0;
    private int month = 0;
    private int day = 0;
    private int isSelect = 0;
    private Calendar calendar = Calendar.getInstance();
    private boolean isChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.due_time_details_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        setHeader("产检详情");
        position = getIntent().getIntExtra("position", 1);
        dueTimeModule = (DueTimeModule) getIntent().getSerializableExtra("dueTimeModule");
        if (dueTimeModule != null) {
            remindDayNumber = dueTimeModule.getDay();
            week = dueTimeModule.getWeek();
            type = dueTimeModule.getType();
            isSelect = dueTimeModule.getIsSelect();
            String dueStr[] = dueTimeModule.getDueDate().split("-");
            dueTimeDetailsTimeTv.setText(dueTimeModule.getDueDate());
            year = Integer.valueOf(dueStr[0]);
            month = Integer.valueOf(dueStr[1]) - 1;
            day = Integer.valueOf(dueStr[2]);
            dueYear = year + "";
            duemonch = month + "";
            dueDay = day + "";
        }
        //1代表之前，2代表当前，3代表之后
        if (type == 1 || type == 3) {
            dueTimeDetailsRemindTimeRay.setClickable(false);
            dueTimeDetailsDueTimeRay.setClickable(false);
            dueTimeDetailsIsSelectTv.setClickable(false);
            dueTimeDetailsIsSelectTv.setAlpha(0.5f);
            dueTimeDetailsTimeTv.setTextColor(mContext.getResources().getColor(R.color.due_time_befor_color));
            dueTimeDetailsRemindTimeTv.setTextColor(mContext.getResources().getColor(R.color.due_time_befor_color));
        } else {
            dueTimeDetailsIsSelect();
            dueTimeDetailsTimeTv.setTextColor(mContext.getResources().getColor(R.color.app_top_color));
            dueTimeDetailsRemindTimeTv.setTextColor(mContext.getResources().getColor(R.color.app_top_color));
        }

        dueTimeDetailsTitleTv.setText("第" + dueWeekStr[position - 1] + "次产检");
        dueTimeDetailsWeekTv.setText("怀孕" + week + "周");
        dueTimeDetailsWebview.getSettings().setJavaScriptEnabled(true);
        String url = "file:///android_asset/html/pregnancyCareIntroduce" + position + ".html";
        dueTimeDetailsWebview.loadUrl(url);

        dueTimeChooseRemindLay.setOnTouchListener(new DueTimeOnTouchListener());
        dueTimeDetailsDueTimeLay.setOnTouchListener(new DueTimeOnTouchListener());
        dueTimeDetailsRemindTimeTv.setText(remindDue[remindDayNumber] + "\t" + dueTimeModule.getRemindTime());

        //产检时间表
        remindNumericWheelAdapter = new NumericWheelAdapter(mContext, START_YEAR, END_YEAR);
        dueTimeDetailsYearView.setViewAdapter(remindNumericWheelAdapter);// 设置"年"的显示数据
        dueTimeDetailsYearView.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

        dueNumericWheelAdapter = new NumericWheelAdapter(mContext, 1, 12);
        dueTimeDetailsMouchView.setViewAdapter(dueNumericWheelAdapter);// 设置"年"的显示数据
        dueTimeDetailsMouchView.setCurrentItem(month);


        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big.contains(String.valueOf(month + 1))) {
            dayNumericWheelAdapter = new NumericWheelAdapter(mContext, 1, 31);
            dueTimeDetailsDayView.setViewAdapter(dayNumericWheelAdapter);
        } else if (list_little.contains(String.valueOf(month + 1))) {
            dayNumericWheelAdapter = new NumericWheelAdapter(mContext, 1, 30);
            dueTimeDetailsDayView.setViewAdapter(dayNumericWheelAdapter);
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                dayNumericWheelAdapter = new NumericWheelAdapter(mContext, 1, 29);
            } else {
                dayNumericWheelAdapter = new NumericWheelAdapter(mContext, 1, 28);
            }
            dueTimeDetailsDayView.setViewAdapter(dayNumericWheelAdapter);
        }
        dueTimeDetailsDayView.setCurrentItem(day - 1);

        OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                int year_num = newValue + START_YEAR;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String
                        .valueOf(dueTimeDetailsMouchView.getCurrentItem() + 1))) {
                    dayNumericWheelAdapter = new NumericWheelAdapter(mContext, 1, 31);
                    dueTimeDetailsDayView.setViewAdapter(dayNumericWheelAdapter);
                } else if (list_little.contains(String.valueOf(dueTimeDetailsMouchView
                        .getCurrentItem() + 1))) {
                    dayNumericWheelAdapter = new NumericWheelAdapter(mContext, 1, 30);
                    dueTimeDetailsDayView.setViewAdapter(dayNumericWheelAdapter);
                } else {
                    if ((year_num % 4 == 0 && year_num % 100 != 0)
                            || year_num % 400 == 0) {
                        dayNumericWheelAdapter = new NumericWheelAdapter(mContext, 1, 29);
                    } else {
                        dayNumericWheelAdapter = new NumericWheelAdapter(mContext, 1, 28);
                    }
                    dueTimeDetailsDayView.setViewAdapter(dayNumericWheelAdapter);
                }
                isChange = true;
                CharSequence charSequence = remindNumericWheelAdapter.getItemText(newValue);
                dueYear = charSequence.toString();
                //year
                if (Integer.valueOf(dueYear) < year) {
                    dueTimeDetailsYearView.setCurrentItem(year - START_YEAR);
                } else if (Integer.valueOf(dueYear) >= (year + 2)) {
                    dueTimeDetailsYearView.setCurrentItem(year - START_YEAR);
                }
                dueTimeStr = dueYear + "-" + duemonch + "-" + dueDay;
                dueTimeDetailsTimeTv.setText(dueTimeStr);
            }
        };
        // 添加"月"监听
        OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                int month_num = newValue + 1;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(month_num))) {
                    dayNumericWheelAdapter = new NumericWheelAdapter(mContext, 1, 31);
                    dueTimeDetailsDayView.setViewAdapter(dayNumericWheelAdapter);
                } else if (list_little.contains(String.valueOf(month_num))) {
                    dayNumericWheelAdapter = new NumericWheelAdapter(mContext, 1, 30);
                    dueTimeDetailsDayView.setViewAdapter(dayNumericWheelAdapter);
                } else {
                    if (((dueTimeDetailsYearView.getCurrentItem() + START_YEAR) % 4 == 0 && (dueTimeDetailsYearView
                            .getCurrentItem() + START_YEAR) % 100 != 0)
                            || (dueTimeDetailsYearView.getCurrentItem() + START_YEAR) % 400 == 0) {
                        dayNumericWheelAdapter = new NumericWheelAdapter(mContext, 1, 29);
                        dueTimeDetailsDayView.setViewAdapter(dayNumericWheelAdapter);
                    } else {
                        dayNumericWheelAdapter = new NumericWheelAdapter(mContext, 1, 28);
                        dueTimeDetailsDayView.setViewAdapter(dayNumericWheelAdapter);
                    }
                }
                isChange = true;
                CharSequence charSequence = dueNumericWheelAdapter.getItemText(newValue);
                duemonch = StringUtils.getDataValues(Integer.valueOf(charSequence.toString()));
                dueTimeStr = dueYear + "-" + duemonch + "-" + dueDay;
                dueTimeDetailsTimeTv.setText(dueTimeStr);
            }
        };
        dueTimeDetailsYearView.addChangingListener(wheelListener_year);
        dueTimeDetailsMouchView.addChangingListener(wheelListener_month);
        dueTimeDetailsDayView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                isChange = true;
                CharSequence charSequence = dayNumericWheelAdapter.getItemText(newValue);
                dueDay = StringUtils.getDataValues(Integer.valueOf(charSequence.toString()));
                dueTimeStr = dueYear + "-" + duemonch + "-" + dueDay;
                dueTimeDetailsTimeTv.setText(dueTimeStr);
            }
        });
        setAbstractWheelData(1, 1, 3, remindDayNumber, dueTimeChooseDayView);
        setAbstractWheelData(2, 1, 23, 7, dueTimeChooseHourView);
        setAbstractWheelData(3, 0, 60, 0, dueTimeChooseMinuteView);
    }


    private class DueTimeOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                dueTimeDetailsScrollview.requestDisallowInterceptTouchEvent(true);
            } else {
                dueTimeDetailsScrollview.requestDisallowInterceptTouchEvent(false);
            }
            return false;
        }

    }

    @OnClick(R.id.due_time_details_is_select_tv)
    public void dueSign() {
        isChange = true;
        if (isSelect == 1) {
            isSelect = 0;
            dueTimeDetailsIsSelectTv.setBackgroundResource(R.drawable.due_time_details_no_select_icon);
            dueTimeDetailsTimeTv.setTextColor(mContext.getResources().getColor(R.color.app_top_color));
            dueTimeDetailsRemindTimeTv.setTextColor(mContext.getResources().getColor(R.color.app_top_color));
        } else {
            isSelect = 1;
            dueTimeDetailsIsSelectTv.setBackgroundResource(R.drawable.due_time_details_select_icon);
            dueTimeDetailsTimeTv.setTextColor(mContext.getResources().getColor(R.color.due_time_befor_color));
            dueTimeDetailsRemindTimeTv.setTextColor(mContext.getResources().getColor(R.color.due_time_befor_color));
        }
    }

    private void dueTimeDetailsIsSelect() {
        if (isSelect == 1) {
            dueTimeDetailsIsSelectTv.setBackgroundResource(R.drawable.due_time_details_select_icon);
        } else {
            dueTimeDetailsIsSelectTv.setBackgroundResource(R.drawable.due_time_details_no_select_icon);
        }
    }

    @OnClick(R.id.due_time_details_remind_time_ray)
    public void chooseRemindTime() {
        if (dueTimeDetailsDueTimeLay.getVisibility() == View.VISIBLE) {
            dueTimeDetailsDueTimeLay.setVisibility(View.GONE);
        }
        if (dueTimeChooseRemindLay.getVisibility() == View.VISIBLE) {
            dueTimeChooseRemindLay.setVisibility(View.GONE);
        } else {
            dueTimeChooseRemindLay.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.due_time_details_due_time_ray)
    public void chooseDueTime() {
        if (dueTimeChooseRemindLay.getVisibility() == View.VISIBLE) {
            dueTimeChooseRemindLay.setVisibility(View.GONE);
        }
        if (dueTimeDetailsDueTimeLay.getVisibility() == View.VISIBLE) {
            dueTimeDetailsDueTimeLay.setVisibility(View.GONE);
        } else {
            dueTimeDetailsDueTimeLay.setVisibility(View.VISIBLE);
        }
    }

    private void setAbstractWheelData(final int types, int initValue, int forValue,
                                      int currentIndex, AbstractWheel abstractWheel) {
        String[] mStr = new String[forValue];
        if (types != 1) {
            for (int i = 0; i < forValue; i++) {
                if (types == 3) {
                    mStr[i] = String.valueOf(StringUtils.getDataValues(initValue + i));
                } else {
                    mStr[i] = String.valueOf(initValue + i);
                }
            }
        } else {
            mStr = remindDue;
        }
        final ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(
                mActivity, mStr);
        ampmAdapter.setItemResource(R.layout.my_infor_common_wheel_item);
        ampmAdapter.setItemTextResource(R.id.common_wheel_item_text);
        abstractWheel.setViewAdapter(ampmAdapter);
        abstractWheel.setCurrentItem(currentIndex, false);
        abstractWheel.setCyclic(false);
        abstractWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                CharSequence charSequence = ampmAdapter.getItemText(newValue);
                if (types == 1) {
                    remindDayNumber = newValue;
                    remindDay = charSequence.toString();
                } else if (types == 2) {
                    remindHour = charSequence.toString();
                } else if (types == 3) {
                    remindMinute = charSequence.toString();
                }
                remindStr = remindDay + "\t" + remindHour + ":" + remindMinute;
                dueTimeDetailsRemindTimeTv.setText(remindStr);
                isChange = true;
            }
        });
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        isUpdateData();

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    isUpdateData();
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void isUpdateData() {
        if (isChange) {
            String dueDate = dueTimeDetailsTimeTv.getText().toString().trim();
            createAlarm(dueDate);
            dueTimeModule.setIsSelect(isSelect);
            dueTimeModule.setDueDate(dueDate);
            dueTimeModule.setDay(remindDayNumber);
            dueTimeModule.setRemindTime(remindHour + ":" + remindMinute);
            DueTimeTables.updatequeryDueTimeTableModule(mContext, dueTimeModule);
            Intent intent1 = new Intent();
            setResult(1004, intent1);
            onBackBtnClick();
        } else {
            onBackBtnClick();
        }
    }

    /**
     * 创建闹钟
     *
     * @param date
     */
    private void createAlarm(String date) {
        String[] dateStr = date.split("-");
        String alarmDate = "";
        if (remindDayNumber == 2) {
            alarmDate = StringUtils.getAddDate(dateStr, -3) + " " + StringUtils.getDataValues(Integer.valueOf(remindHour)) + ":" + StringUtils.getDataValues(Integer.valueOf(remindMinute)) + ":00";
        } else {
            alarmDate = StringUtils.getAddDate(dateStr, -remindDayNumber) + " " + StringUtils.getDataValues(Integer.valueOf(remindHour)) + ":" + StringUtils.getDataValues(Integer.valueOf(remindMinute)) + ":00";
        }
        calendar.setTimeInMillis(StringUtils.getDateFromStrss(alarmDate));
        Intent intent = new Intent(mActivity, CallAlarm.class);
        PendingIntent pdIntent = PendingIntent.getBroadcast(mActivity, 0, intent, 0);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        /**
         * AlarmManager.RTC_WAKEUP 在系统休眠的时候同样运行
         * 以set()设置的PendingIntent只会运行一次
         */
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pdIntent);
    }
}
