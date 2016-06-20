package com.xiaoaitouch.mom.app;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.DueTimeTableAdapter;
import com.xiaoaitouch.mom.broadcast.CallAlarm;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.DueTimeModule;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.sqlite.DueTimeTables;
import com.xiaoaitouch.mom.util.DueTimeUtils;
import com.xiaoaitouch.mom.util.SharedPreferencesUtil;
import com.xiaoaitouch.mom.util.StringUtils;
import com.xiaoaitouch.mom.view.RoundedImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Desc: 产检时间表
 * User: huxin
 * Date: 2016/3/4
 * Time: 11:48
 * FIXME
 */
public class DueTimeTableActivity extends BaseActivity {
    @Bind(R.id.due_time_table_lt)
    ListView dueTimeTableLt;

    private DueTimeTableAdapter dueTimeTableAdapter;
    private View headView;
    private RoundedImageView headerImageIv;
    private UserModule userModule;
    private String mDueTime = "";//怀孕的日期
    private String mCurrentDate = "";
    private int mCurrentWeek = 1;//当前周
    private int[] dueWeek = {12, 16, 20, 24, 28, 30, 32, 34, 36, 37, 38, 39, 40};
    private String[] dueWeekStr = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三"};
    private boolean isDueTimeSetting = false;
    private List<DueTimeModule> dueTimeMoDuleList = new ArrayList<DueTimeModule>();
    private String[] startTime;
    private long userId = 0l;
    private Calendar calendar = Calendar.getInstance();
    private boolean isAlarm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.due_time_table_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        setHeader("产检时间表");
        isAlarm = getIntent().getBooleanExtra("isAlarm", false);
        setUserData();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 取消通知
        manager.cancel(CallAlarm.NOTICE_ID);
        DueTimeModule mDueTimeModule = DueTimeTables.queryDueTimeTableModule(mContext, userId);
        DueTimeUtils.deleteDueTime(mContext, mDueTimeModule, mCurrentWeek);

        isDueTimeSetting = SharedPreferencesUtil.getBoolean(mActivity, "due_time_is_setting", false);
        if (!isDueTimeSetting) {
            structureTime();
            DueTimeTables.deleteDueTimeTableModule(mContext);
            SharedPreferencesUtil.putBoolean(mActivity, "due_time_is_setting", true);
        } else {
            dueTimeMoDuleList = DueTimeTables.queryDueTimeTableModuleList(mContext, userId);
            if (dueTimeMoDuleList == null || dueTimeMoDuleList.size() <= 1) {
                structureTime();
            }
        }
        headView = LayoutInflater.from(mContext).inflate(
                R.layout.due_time_head_view_item, null);
        headerImageIv = (RoundedImageView) headView.findViewById(R.id.user_head_image_iv);
        int mResID = mContext.getResources().getIdentifier("week" + mCurrentWeek, "drawable",
                mContext.getApplicationInfo().packageName);
        headerImageIv.setImageResource(mResID);
        dueTimeTableLt.addHeaderView(headView);
        dueTimeTableAdapter = new DueTimeTableAdapter(mContext);
        dueTimeTableAdapter.setData(dueTimeMoDuleList);
        dueTimeTableLt.setAdapter(dueTimeTableAdapter);
    }

    private void setUserData() {
        userModule = MyApplication.instance.getUserModule();
        mCurrentDate = StringUtils.getCurrentTimeSs();
        if (userModule != null) {
            userId = userModule.getUserId();
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
        startTime = mDueTime.split("-");
        int number = StringUtils.getDateSpace(mDueTime, mCurrentDate) + 1;
        if (number >= 7) {
            mCurrentWeek = number / 7;
            int days = number % 7;
            if (days != 0) {
                mCurrentWeek = mCurrentWeek + 1;
            }
            if (mCurrentWeek >= 39) {
                mCurrentWeek = 39;
            }
        }
    }

    private boolean isflage = false;

    private void structureTime() {
        for (int i = 0; i < 13; i++) {//0代表不在这个区域，1代表之前，2代表当前，3代表之后
            String date = StringUtils.getAddDate(startTime, (dueWeek[i] - 1) * 7);
            DueTimeModule dueTimeModule = new DueTimeModule();
            dueTimeModule.setWeek(dueWeek[i]);
            dueTimeModule.setDueDate(date);
            dueTimeModule.setUserId(userId);
            dueTimeModule.setDay(1);
            dueTimeModule.setRemindTime("8:00");
            dueTimeModule.setIsSelect(0);
            if (dueWeek[i] < mCurrentWeek) {
                dueTimeModule.setType(1);
            } else if (dueWeek[i] >= mCurrentWeek) {
                if (!isflage) {
                    createAlarm(date);
                    dueTimeModule.setType(2);
                    isflage = true;
                } else {
                    dueTimeModule.setType(3);
                }
            }
            dueTimeModule.setTitle("第" + dueWeekStr[i] + "次产检\n怀孕" + dueWeek[i] + "周");
            DueTimeTables.addDueTimeTableModule(mContext, dueTimeModule);
            dueTimeMoDuleList.add(dueTimeModule);
        }
    }

    /**
     * 创建闹钟
     *
     * @param date
     */
    private void createAlarm(String date) {
        String[] dateStr = date.split("-");
        String alarmDate = StringUtils.getAddDate(dateStr, -1) + " " + "08:00:00";
        calendar.setTimeInMillis(StringUtils.getDateFromStrss(alarmDate));
        Intent intent = new Intent(mActivity, CallAlarm.class);
        PendingIntent pdIntent = PendingIntent.getBroadcast(mActivity, 0, intent, 0);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pdIntent);
    }

    @OnItemClick(R.id.due_time_table_lt)
    public void openDueTimeDetailsActivity(int position) {
        if (dueTimeTableAdapter != null && position != 0) {
            DueTimeModule dueTimeModule = (DueTimeModule) dueTimeTableAdapter.getItem(position - 1);
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            bundle.putSerializable("dueTimeModule", dueTimeModule);
            startActivityForResult(DueTimeDetailsActivity.class, bundle, 1008);
        }
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        judgeActivityExist();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1004) {
            dueTimeMoDuleList = DueTimeTables.queryDueTimeTableModuleList(mContext, userId);
            dueTimeTableAdapter.setData(dueTimeMoDuleList);
        }
    }

    private void judgeActivityExist() {
        if (isAlarm) {
            startIntent(MainActivity.class);
        }
        onBackBtnClick();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    judgeActivityExist();
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
