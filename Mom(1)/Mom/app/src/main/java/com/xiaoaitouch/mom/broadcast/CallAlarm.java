package com.xiaoaitouch.mom.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.app.DueTimeTableActivity;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.sqlite.UserTables;
import com.xiaoaitouch.mom.util.StringUtils;

/**
 * Desc: 创建闹钟
 * User: huxin
 * Date: 2016/3/10
 * Time: 9:34
 * FIXME
 */
public class CallAlarm extends BroadcastReceiver {
    private int[] dueWeek = {12, 16, 20, 24, 28, 30, 32, 34, 36, 37, 38, 39, 40};
    private String[] dueWeekStr = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三"};
    public static final int NOTICE_ID = 1222;
    private UserModule userModule;
    private String mDueTime = "";//怀孕的日期
    private String mCurrentDate = "";
    private int mCurrentWeek = 1;//当前周
    private String remindContent = "";
    private boolean isflage = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        UserModule userModule = UserTables.queryUser(context);
        MyApplication.instance.setUserModule(userModule);
        setUserData();
        structureTime();

        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher, "产检提醒",
                System.currentTimeMillis());
        Intent mIntents = new Intent(context, DueTimeTableActivity.class);
        mIntents.putExtra("isAlarm", true);
        PendingIntent mIntent = PendingIntent.getActivity(context, 0, mIntents, 0);
        // 设置通知信息
        notification.setLatestEventInfo(context, "产检提醒", remindContent, mIntent);
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        long[] vibrate = {0, 100, 200, 300};
        notification.vibrate = vibrate;
        // 通知
        manager.notify(NOTICE_ID, notification);
        Log.d("CallAlarm","进来了");
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
        String[] startTime = mDueTime.split("-");
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

    private void structureTime() {
        for (int i = 0; i < 13; i++) {
            if (dueWeek[i] < mCurrentWeek) {
            } else if (dueWeek[i] >= mCurrentWeek) {
                if (!isflage) {
                    remindContent = "第" + dueWeekStr[i] + "次产检\n怀孕" + dueWeek[i] + "周";
                    isflage = true;
                }
            }
        }
    }
}
