package com.xiaoaitouch.mom.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.bluetooth.le.BluetoothLeService;
import com.example.bluetooth.le.BluetoothLeUtil;
import com.example.bluetooth.le.WristbandsInfo;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xiaoaitouch.event.EventBus;
import com.xiaoaitouch.event.bean.MainEvent;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.TabsAdapter;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.fragment.ClassroomFragments;
import com.xiaoaitouch.mom.fragment.FoundFragment;
import com.xiaoaitouch.mom.fragment.HomeFragment;
import com.xiaoaitouch.mom.fragment.PersonalFragment;
import com.xiaoaitouch.mom.module.Bluetooth;
import com.xiaoaitouch.mom.module.StepsParams;
import com.xiaoaitouch.mom.pedometer.StepService;
import com.xiaoaitouch.mom.service.StepUpdateService;
import com.xiaoaitouch.mom.util.AppManager;
import com.xiaoaitouch.mom.util.DialogUtils;
import com.xiaoaitouch.mom.util.ShareInfo;
import com.xiaoaitouch.mom.util.SharedPreferencesUtil;
import com.xiaoaitouch.mom.util.SubmitSportUtils;
import com.xiaoaitouch.mom.util.SysUtil;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.view.ViewPagerNoTouch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * <主界面>
 *
 * @author huxin
 * @data: 2016/1/6 16:57
 * @version: V1.0
 */
public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {
    private static final String BC_ACTION = "com.xiaoaitouch.mom.action.STEP_ACTION";
    @Bind(R.id.main_content_view_page)
    ViewPagerNoTouch mainContentViewPage;
    @Bind(R.id.main_home_ray)
    RelativeLayout mainHomeRay;
    @Bind(R.id.main_class_room_ray)
    RelativeLayout mainClassRoomRay;
    @Bind(R.id.main_found_ray)
    RelativeLayout mainFoundRay;
    @Bind(R.id.main_my_ray)
    RelativeLayout mainMyRay;

    // ///////////蓝牙连接
    private boolean mConnectResult = false;
    public String mDeviceAddress = null;
    public BluetoothLeUtil mBluetoothLeUtil = null;

    // 定义数组来存放Fragment界面
    private Class fragmentArray[] = {HomeFragment.class, ClassroomFragments.class, FoundFragment.class, PersonalFragment.class};
    // 定义数组来存放按钮图片
    private static final int DEFAULT_OFFSCREEN_PAGES = 4;
    private TabsAdapter mTabsAdapter;
    private Map<Integer, RelativeLayout> mRelativeLayoutMap = new HashMap<Integer, RelativeLayout>();
    private long firstTime = 0; // 记录按返回键时间
    private static final int OFF_APP_SERVICE = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothLeUtil.init(this);

        EventBus.getDefault().register(this);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        AppManager.getAppManager().addActivity(mActivity);
        initViewData();
    }

    public void initViewData() {
        mRelativeLayoutMap.put(0, mainHomeRay);
        mRelativeLayoutMap.put(1, mainClassRoomRay);
        mRelativeLayoutMap.put(2, mainFoundRay);
        mRelativeLayoutMap.put(3, mainMyRay);

        mainHomeRay.setOnClickListener(this);
        mainClassRoomRay.setOnClickListener(this);
        mainFoundRay.setOnClickListener(this);
        mainMyRay.setOnClickListener(this);

        mainContentViewPage.setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGES);
        mTabsAdapter = new TabsAdapter(this, getSupportFragmentManager());
        mainContentViewPage.setAdapter(mTabsAdapter);

        for (int i = 0; i < fragmentArray.length; i++) {
            mTabsAdapter.addTab(
                    fragmentArray[i], null);
        }
        mainContentViewPage.setCurrentItem(0, false);
        setViewChange(0);

        mHandler.sendEmptyMessage(6);
    }

    public void onEvent(MainEvent event) {
        List<Fragment> mainFragments = getSupportFragmentManager()
                .getFragments();
        if (mainFragments != null && mainFragments.size() >= 1) {
            HomeFragment mainFragment = (HomeFragment) mainFragments.get(0);
            ClassroomFragments classroomFragment = (ClassroomFragments) mainFragments.get(1);
            if (mainFragment != null) {
                mainFragment.updateData();
            }
            if (classroomFragment != null) {
                classroomFragment.setUpdateData();
            }
        }
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: initBleService
     * @Description: 初始蓝牙
     */
    private void initBleService() {
        mDeviceAddress = ShareInfo.getTagString(mActivity, ShareInfo.TAG_BLE_MAC);
        mDeviceAddress = mDeviceAddress == null ? "" : mDeviceAddress;
        if (mDeviceAddress.length() > 1) {
            mBluetoothLeUtil = BluetoothLeUtil.getBluetoothUtil();
            mBluetoothLeUtil.setDeviceMac(mDeviceAddress);
            mBluetoothLeUtil.setOnWristbandsDataListener(new BluetoothLeService.OnWristbandsDataListener() {
                @Override
                public void onStepData(WristbandsInfo info) {
                    mHandler.obtainMessage(2, info).sendToTarget();
                }

                @Override
                public void onDisconnected(int arg0) {
                    mHandler.obtainMessage(3).sendToTarget();
                }

                @Override
                public void onConnected(int arg0) {
                    mHandler.obtainMessage(1).sendToTarget();
                }

                @Override
                public void onConnectFailed() {
                    mHandler.obtainMessage(3).sendToTarget();
                }
            });
            mBluetoothLeUtil.onStartService();
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    mConnectResult = true;
                    if (mBluetoothLeUtil != null) {
                        showToast("蓝牙连接成功");
                        SharedPreferencesUtil.putBoolean(mContext, "blue_connect",
                                mConnectResult);
                        int battery = mBluetoothLeUtil.getBattery();
                        SharedPreferencesUtil.putInt(mContext, "shoes_battery",
                                battery);
                    }
                    Bluetooth bluetooth = new Bluetooth();
                    bluetooth.bluetoothLeUtil = mBluetoothLeUtil;
                    bluetooth.isFlage = mConnectResult;
                    MyApplication.instance.setBlueInfor(bluetooth);
                    break;
                case 2:
                    mConnectResult = true;
                    WristbandsInfo info = (WristbandsInfo) msg.obj;
                    if (info != null) {
                        int stepNumber = 2 * info.getStep();
                        StepUpdateService.setBlueSteps(stepNumber);
                    }
                    Bluetooth bluetooth1 = new Bluetooth();
                    bluetooth1.bluetoothLeUtil = mBluetoothLeUtil;
                    bluetooth1.isFlage = mConnectResult;
                    MyApplication.instance.setBlueInfor(bluetooth1);
                    break;
                case 3:
                    mConnectResult = false;
                    SharedPreferencesUtil.putBoolean(mContext, "blue_connect",
                            mConnectResult);
                    Bluetooth bluetooth2 = new Bluetooth();
                    bluetooth2.bluetoothLeUtil = mBluetoothLeUtil;
                    bluetooth2.isFlage = mConnectResult;
                    MyApplication.instance.setBlueInfor(bluetooth2);
                    break;
                case OFF_APP_SERVICE:
                    stopService(new Intent(mContext, StepService.class));
                    stopService(new Intent(mContext, StepUpdateService.class));
                    AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    PendingIntent pi = createAlarmIntent();
                    alarmMgr.cancel(pi);
                    break;
                case 6:
                    initUmengUpdate();
                    stepBroadcast();
                    bluetoothConnected();
                    SubmitSportUtils.getInstance(mContext).submitSportData();
                    break;

                default:
                    break;
            }
        }
    };

    private void bluetoothConnected() {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt <= 17) {
            showToast("你手机SDK过低，不支持蓝牙连接功能");
        } else {
            BluetoothAdapter adpter = BluetoothAdapter
                    .getDefaultAdapter();
            adpter.enable();
            initBleService();
        }
    }

    private void initUmengUpdate() {
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus,
                                         final UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes:
                        if (updateInfo != null) {
                            DialogUtils.appUpdateAlertDialog(mActivity, updateInfo,
                                    "", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            switch (which) {
                                                case DialogUtils.BUTTON1:
                                                    dialog.dismiss();
                                                    startActivity(new Intent(
                                                            Intent.ACTION_VIEW,
                                                            Uri.parse(updateInfo.path)));
                                                    break;
                                            }
                                        }
                                    });
                        }
                        break;
                }
            }
        });
        UmengUpdateAgent.update(this);
    }

    /**
     * 手机计步数据来源
     */
    private final BroadcastReceiver mStepBroadcastReceiverListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                final String action = intent.getAction();
                if (StepUpdateService.getBroadcastAction().equals(action)) {
                    StepsParams stepsParams = StepUpdateService
                            .getStepMsgs(intent);
                    if (stepsParams != null) {
                        List<Fragment> mainFragments = getSupportFragmentManager()
                                .getFragments();
                        if (mainFragments != null && mainFragments.size() >= 1) {
                            HomeFragment mainFragment = (HomeFragment) mainFragments.get(0);
                            if (mainFragment != null) {
                                mainFragment.updateSportData(stepsParams);
                            }
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    };

    private void setStepReceiver() {
        final IntentFilter f = new IntentFilter();
        f.addAction(StepUpdateService.getBroadcastAction());
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
                mStepBroadcastReceiverListener, new IntentFilter(f));
    }

    private void stopGetStepMsgs() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(
                mStepBroadcastReceiverListener);
    }

    private void stepBroadcast() {
        // 获得AlarmManager实例
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // 实例化PendingIntent
        PendingIntent pi = createAlarmIntent();
        // 获得系统时间
        long time = System.currentTimeMillis();
        // 重复提示，从当前时间开始，间隔6秒
        am.setRepeating(AlarmManager.RTC_WAKEUP, time, 30 * 1000, pi);

        boolean isOnOff = SysUtil.isServiceWork(mContext,
                "com.xiaoaitouch.mom.service.StepUpdateService");
        boolean stepService = SysUtil.isServiceWork(mContext,
                "com.xiaoaitouch.mom.pedometer.StepService");
        if (isOnOff && stepService) {
            StepUpdateService.doWork();
        }

    }

    /**
     * 创建一个用于设置Alarm的Intent
     */
    private PendingIntent createAlarmIntent() {
        Intent intent = new Intent();
        intent.setAction(BC_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        return pi;
    }

    public void cancel() {
        mHandler.sendEmptyMessage(OFF_APP_SERVICE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_home_ray:
                setViewChange(0);
                break;
            case R.id.main_class_room_ray:
                setViewChange(1);
                break;
            case R.id.main_found_ray:
                setViewChange(2);
                break;
            case R.id.main_my_ray:
                setViewChange(3);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bluetooth bluetoothInfor = MyApplication.instance.getBluetooth();
        if (bluetoothInfor == null) {
            Bluetooth bluetooth = new Bluetooth();
            bluetooth.bluetoothLeUtil = mBluetoothLeUtil;
            bluetooth.isFlage = mConnectResult;
            bluetoothInfor = bluetooth;
        }
        if (bluetoothInfor != null) {
            if (!bluetoothInfor.isFlage) {
                bluetoothConnected();
            }
        }
        setStepReceiver();

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopGetStepMsgs();

    }


    @Override
    protected void onDestroy() {
        Bluetooth bluetoothInfor = MyApplication.instance.getBluetooth();
        if (bluetoothInfor != null) {
            if (bluetoothInfor.bluetoothLeUtil != null) {
                bluetoothInfor.bluetoothLeUtil.onStopService();
                bluetoothInfor.bluetoothLeUtil.onDestory();
            }
        }
        BluetoothAdapter adpter = BluetoothAdapter
                .getDefaultAdapter();
        adpter.disable();
        AppManager.getAppManager().finishAllActivity();
        super.onDestroy();
    }

    private void setViewChange(int index) {
        mainContentViewPage.setCurrentItem(index, false);
        for (int i = 0; i < mRelativeLayoutMap.size(); i++) {
            if (index == i) {
                int allChildAt = mRelativeLayoutMap.get(index).getChildCount();
                for (int j = 0; j < allChildAt; j++) {
                    mRelativeLayoutMap.get(index).getChildAt(j).setPressed(true);
                    mRelativeLayoutMap.get(index).getChildAt(j).setFocusable(true);
                    mRelativeLayoutMap.get(index).getChildAt(j).setSelected(true);
                }
            } else {
                int allChildAts = mRelativeLayoutMap.get(i).getChildCount();
                for (int j = 0; j < allChildAts; j++) {
                    mRelativeLayoutMap.get(i).getChildAt(j).setPressed(false);
                    mRelativeLayoutMap.get(i).getChildAt(j).setFocusable(false);
                    mRelativeLayoutMap.get(i).getChildAt(j).setSelected(false);
                }
            }
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    long secondTime = System.currentTimeMillis();
                    if ((secondTime - firstTime) > 1000) {
                        Utils.showToast(getString(R.string.app_exit), Toast.LENGTH_SHORT);
                        firstTime = secondTime;
                        return false;
                    } else {
                        finish();
                        return true;
                    }
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
