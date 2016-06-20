package com.xiaoaitouch.mom.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.SportTabModule;
import com.xiaoaitouch.mom.module.StepsParams;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.pedometer.StepService;
import com.xiaoaitouch.mom.sqlite.SportTables;
import com.xiaoaitouch.mom.train.BeforTraining;
import com.xiaoaitouch.mom.train.Training;
import com.xiaoaitouch.mom.train.Training.OnTrainingLisener;
import com.xiaoaitouch.mom.train.Utils;
import com.xiaoaitouch.mom.util.StringUtils;


/**
 * @author huxin
 * @ClassName: StepUpdateService
 * @Description: 手机步数
 * @date 2015-12-1 下午3:56:59
 */
public class StepUpdateService extends Service {
    private static final String TAG = "StepUpdateService";
    private static final String BROADCAST_ACTION_UPDATE_CHAT_MSG = "com.xiaoaitouch.mom.service.StepUpdateService.BROADCAST_ACTION_UPDATE_STEP_MSG";
    // TODO 添加判断
    private static final String BROADCAST_EXTRA_MSGS_DATA = "BROADCAST_EXTRA_MSGS_DATA";
    private static Context mContext;
    // ///////////////////////////
    private static BeforTraining mBeforTraining;
    private static Training training;
    private static int totalStepNumber = 1;
    private static float mTallTime = 0.0f;// 当天累计的总时间
    private static StepService mService;
    /***********************
     * 手机计步
     ********************************/
    private static final int STEPS_MSG = 1;
    private static final int TRAIN_MSG = 6;
    private static final int REST_MSG = 7;

    private UserModule userModule = null;
    private int intervalTime = 0;
    private final IBinder mBinder = new StepUpdateBinder();

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mContext = this;
        initStep();
    }

    private void initStep() {
        userModule = MyApplication.instance.getUserModule();
        if (userModule != null) {
            SportTabModule sportTabModule = SportTables.querySportTabModule(mContext, userModule.getUserId(), StringUtils.getCurrentTimeSs());
            if (sportTabModule != null) {
                mTallTime = sportTabModule.getTime();
                totalStepNumber = sportTabModule.getStepNumber();
            } else {
                mTallTime = 0.0f;
                totalStepNumber = 0;
            }
        } else {
            mTallTime = 0.0f;
            totalStepNumber = 0;
        }
        training = new Training(onTrainingLisener);
        mBeforTraining = new BeforTraining();
        mBeforTraining.couldTraining();

        mBeforTraining.getInternalFactor().getPersonInfo().getTrainingInfo()
                .clearInfo();
        mBeforTraining.getInternalFactor().getPersonInfo()
                .getTimeSlotTraining().setTime((long) (mTallTime * 60));
        mBeforTraining.getInternalFactor().getPersonInfo()
                .getTimeSlotTraining().setStepCount(totalStepNumber);
        startStepService();
        bindStepService();
        doWork();
    }

    OnTrainingLisener onTrainingLisener = new OnTrainingLisener() {
        @Override
        public void onTraining(boolean isTraining, float speed, String content) {
            if (isTraining) {
                handler.sendEmptyMessage(TRAIN_MSG);
            } else {
                handler.sendEmptyMessage(REST_MSG);
            }
        }
    };

    Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    totalStepNumber = (int) msg.arg1;
                    intervalTime = 0;
                    handler.removeMessages(2);
                    handler.sendEmptyMessage(2);
                    doWork();
                    break;
                case 2:
                    intervalTime++;
                    if (intervalTime == 20) {
                        intervalTime = 0;
                        handler.removeMessages(2);
                        saveSportData();
                    } else {
                        handler.sendEmptyMessageDelayed(2, 1000);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

        ;
    };

    private static String getStyleString() {
        String runTime = "您已走了<font color = \"#F4426C\">" + getSportTime()
                + "</font>分钟，";
        String wholeStep = "共计<font color = \"#F4426C\">" + getSportStep()
                + "</font>步了哟！";
        return runTime + wholeStep;
    }

    private static float getSportTime() {
        float nowTime = mBeforTraining.getInternalFactor().getPersonInfo()
                .getTrainingInfo().getTrainTime() / 60.0f;
        return nowTime + mTallTime;
    }

    private static int getSportStep() {
        return mBeforTraining.getInternalFactor().getPersonInfo()
                .getStepCount();
    }

    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            handler.sendMessage(handler.obtainMessage(STEPS_MSG, value, 0));
        }

        public void paceChanged(int value) {
        }

        public void distanceChanged(float value) {
        }

        public void speedChanged(float value) {
        }

        public void caloriesChanged(float value) {
        }
    };

    public static void doWork() {
        training.setCouldTrain(mBeforTraining.couldTraining());
        training.trainingLisener(totalStepNumber);
        long dayEndTimeMillis = StringUtils.getEndTime();
        if (StringUtils.currentTimeMillis() >= dayEndTimeMillis) {
            // 清空前天的运动数据
            mTallTime = 0.0f;
            totalStepNumber = 0;
            mBeforTraining.getInternalFactor().getPersonInfo()
                    .getTrainingInfo().clearInfo();
            mBeforTraining.getInternalFactor().getPersonInfo()
                    .getTimeSlotTraining().setTime((long) (mTallTime * 60));
            mBeforTraining.getInternalFactor().getPersonInfo()
                    .getTimeSlotTraining().setStepCount(totalStepNumber);
        }
        Intent i = new Intent(getBroadcastAction());
        StepsParams stepsParams = new StepsParams();
        stepsParams.beforTraining = mBeforTraining;
        stepsParams.stepsMsg = getStyleString();
        stepsParams.stepsNumber = getSportStep();
        i.putExtra(BROADCAST_EXTRA_MSGS_DATA, stepsParams);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(i);
    }

    public static StepsParams getStepMsgs(Intent intent) {
        return intent == null ? null : (StepsParams) intent
                .getSerializableExtra(BROADCAST_EXTRA_MSGS_DATA);
    }

    public static String getBroadcastAction() {
        return BROADCAST_ACTION_UPDATE_CHAT_MSG;
    }

    public static void setBlueSteps(int steps) {
        if (mService != null) {
            mService.resetStepValues(steps);
        }
    }

    // //////////手机计步服务///////////////
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder) service).getService();
            mService.registerCallback(mCallback);
            mService.reloadSettings();
            mService.resetValues();
        }

        public void onServiceDisconnected(ComponentName className) {
            saveSportData();
            mService = null;
        }
    };

    private void startStepService() {
        startService(new Intent(mContext, StepService.class));
    }

    private void bindStepService() {
        bindService(new Intent(mContext, StepService.class), mConnection,
                Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveSportData();
    }

    /**
     * 保存当天的运动信息
     */
    private static void saveSportData() {
        SportTabModule mSportSubmit = new SportTabModule();
        int stepCount = getSportStep();
        mSportSubmit.setCalorie(Utils.comCalorie(stepCount));
        mSportSubmit.setKm(Utils.comDistance(stepCount));
        mSportSubmit.setSportDate(StringUtils.getCurrentTimeSs());
        if (MyApplication.instance.getUserModule() != null) {
            mSportSubmit.setUserId(MyApplication.instance.getUserModule()
                    .getUserId());
        }
        mSportSubmit.setStepNumber(stepCount);
        mSportSubmit.setTime(getSportTime());
        SportTables.addSportTabModule(mContext, mSportSubmit);
    }

    public class StepUpdateBinder extends Binder {
        public StepUpdateService getService() {
            return StepUpdateService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
