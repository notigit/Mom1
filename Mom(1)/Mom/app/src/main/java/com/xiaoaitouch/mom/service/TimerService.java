package com.xiaoaitouch.mom.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Desc: 计时器
 * User: huxin
 * Date: 2016/3/6
 * Time: 15:04
 * FIXME
 */
public class TimerService extends Service {
    private Handler mHandler;
    private int totalsecond = 0;
    private int second = 0;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private final IBinder mBinder = new TimerBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class TimerBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }


    private void initTime() {
        second++;
        if (second >= totalsecond)
            finishTime();
        else
            mHandler.obtainMessage(1, second).sendToTarget();
    }

    public void startTime() {
        second = 0;
        mHandler.obtainMessage(1, second).sendToTarget();
        startTimers();
    }

    public void stopTime() {
        second = 0;
        mHandler.obtainMessage(2, second).sendToTarget();
        stopTimers();
    }

    public void finishTime() {
        second = 0;
        mHandler.sendEmptyMessage(3);
        stopTimers();
    }

    /**
     * 设置Handler
     */
    public void setParameter(Handler handler, int totalsecond) {
        this.totalsecond = totalsecond;
        this.mHandler = handler;
    }

    private void startTimers() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    initTime();
                }
            };
        }
        if (mTimer != null && mTimerTask != null)
            mTimer.schedule(mTimerTask, 1000, 1000);
    }

    private void stopTimers() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    @Override
    public void onDestroy() {
        stopTimers();
        super.onDestroy();
    }
}
