package com.xiaoaitouch.mom.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.bluetooth.le.BluetoothLeService;
import com.example.bluetooth.le.BluetoothLeUtil;
import com.example.bluetooth.le.WristbandsInfo;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.DeviceAdapter;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.info.BeaconInfo;
import com.xiaoaitouch.mom.module.Bluetooth;
import com.xiaoaitouch.mom.service.StepUpdateService;
import com.xiaoaitouch.mom.sqlite.BeaconTables;
import com.xiaoaitouch.mom.util.ShareInfo;
import com.xiaoaitouch.mom.view.MyListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 蓝牙设备管理
 * Created by Administrator on 2016/1/9.
 */
public class DeviceManageActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_SCAN = 10;
    // 8秒后停止查找搜索.
    private static final long SCAN_PERIOD = 1000 * 8;

    @Bind(R.id.devicemanage_imgView)
    ImageView devicemanageImgView;
    @Bind(R.id.devicemanage_btnScan)
    RelativeLayout devicemanageBtnScan;
    @Bind(R.id.devicemanage_btnDetails)
    RelativeLayout devicemanageBtnDetails;
    @Bind(R.id.devicemanage_listView)
    MyListView devicemanageListView;
    @Bind(R.id.device_tips_lay)
    LinearLayout deviceTipsLay;


    private BluetoothLeUtil mBluetoothLeUtil = null;
    private BluetoothAdapter mBluetoothAdapter;
    private AlertDialog dialog;

    private Map<String, BeaconInfo> hashMap;
    private DeviceAdapter adapter;
    private String connectMac = null;
    private ArrayList<BeaconInfo> beaconInfoLiset = new ArrayList<BeaconInfo>();
    private boolean isflage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devicemanage_layout);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        setHeader("设备");
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        devicemanageBtnScan.setOnClickListener(this);
        devicemanageBtnDetails.setOnClickListener(this);
        beaconInfoLiset = BeaconTables.getBeaconList(mContext);
        adapter = new DeviceAdapter(this);
        adapter.setData(beaconInfoLiset);
        devicemanageListView.setAdapter(adapter);
        devicemanageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(DeviceManageActivity.this, DeviceDetailsActivity.class);
                intent.putExtra(BeaconInfo.TAG, ((BeaconInfo) adapter.getItem(position)).getUuid());
                startActivity(intent);
            }
        });
        devicemanageImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice(true);
            }
        });
        if (beaconInfoLiset != null && beaconInfoLiset.size() >= 1) {
            deviceTipsLay.setVisibility(View.VISIBLE);
        } else {
            ShareInfo.saveTagString(DeviceManageActivity.this, ShareInfo.TAG_BLE_MAC, "");
            deviceTipsLay.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }

    @Override
    protected void onResume() {
        // 为确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        getWidget();
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        Bluetooth bluetooth = new Bluetooth();
        bluetooth.bluetoothLeUtil = mBluetoothLeUtil;
        bluetooth.isFlage = isflage;
        MyApplication.instance.setBlueInfor(bluetooth);
        scanLeDevice(false);
        super.onDestroy();
    }


    /**
     * 获取组件
     * <>
     *
     * @author
     */
    public void getWidget() {
        beaconInfoLiset = BeaconTables.getBeaconList(mContext);
        if (beaconInfoLiset != null && beaconInfoLiset.size() >= 1) {
            deviceTipsLay.setVisibility(View.VISIBLE);
        } else {
            ShareInfo.saveTagString(DeviceManageActivity.this, ShareInfo.TAG_BLE_MAC, "");
            if (mBluetoothLeUtil != null) {
                mBluetoothLeUtil.onStopService();
                mBluetoothLeUtil.onDestory();
            }
            deviceTipsLay.setVisibility(View.GONE);
        }
        adapter.setData(beaconInfoLiset);
    }

    /**
     * 搜索蓝牙设备
     * <>
     *
     * @author
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            hashMap = new HashMap<String, BeaconInfo>();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanLeDevice(false);
                    handler.sendEmptyMessage(9);
                }
            }, SCAN_PERIOD);

            showDialog("附近蓝牙搜索中...");
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    /**
     * 开始连接设备
     * <>
     *
     * @author
     */
    private void startBLE() {
        ArrayList<BeaconInfo> list = new ArrayList<BeaconInfo>();
        Iterator it = hashMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            list.add(hashMap.get(key));
        }

        //排序
        BeaconInfo temp; // 记录临时中间值
        int size = list.size(); // 数组大小
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                int sort01 = list.get(i).getRssi();
                int sort02 = list.get(j).getRssi();
                if (sort01 < sort02) { // 交换两数的位置
                    temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                System.out.println(i + ":man!_---------------->" + list.get(i).getUuid());
            }
            BeaconInfo info = list.get(0);
            info.setName("aiShoes " + (BeaconTables.getBeaconList(this).size() + 1));
            BeaconTables.addBeacons(this, info);
            connectMac = info.getUuid();
            //连接蓝牙
            if (!TextUtils.isEmpty(connectMac) && connectMac.length() > 1) {
                bluetoothConnected(connectMac);
            }
        } else {
            handler.sendEmptyMessage(10);
        }
    }

    private void bluetoothConnected(String uuid) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt <= 17) {
            showToast("你手机SDK过低，不支持蓝牙连接功能");
        } else {
            BluetoothAdapter adpter = BluetoothAdapter
                    .getDefaultAdapter();
            adpter.enable();
            mBluetoothLeUtil = BluetoothLeUtil.getBluetoothUtil();
            mBluetoothLeUtil.setDeviceMac(uuid);
            mBluetoothLeUtil.setOnWristbandsDataListener(new BluetoothLeService.OnWristbandsDataListener() {
                @Override
                public void onStepData(WristbandsInfo info) {
                    handler.obtainMessage(2, info).sendToTarget();
                }

                @Override
                public void onConnected(int statues) {
                    handler.sendEmptyMessage(1);
                }

                @Override
                public void onDisconnected(int statues) {
                    handler.sendEmptyMessage(0);
                }

                @Override
                public void onConnectFailed() {
                }
            });
            mBluetoothLeUtil.onStartService();
        }
    }


    /**
     * 蓝牙搜索提示
     * <>
     *
     * @author
     */
    public void showDialog(String status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(status);
        dialog = builder.show();
        dialog.setCancelable(false);
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device.getName() != null) {
                        if (device.getName().contains("aiShose") || device.getName().contains("AS")) {
                            BeaconInfo info = new BeaconInfo();
                            info.setUuid(device.getAddress());
                            info.setRssi(rssi);
                            hashMap.put(device.getAddress(), info);
                        }
                    }
                }
            });
        }
    };

    private Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    isflage = false;
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    showToast("蓝牙连接断开!");
                    break;
                case 1:
                    isflage = true;
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    if (adapter != null) {
                        adapter.updateStatus(connectMac);
                    }
                    showToast("蓝牙连接成功!");
                    break;
                case 2:
                    isflage = true;
                    WristbandsInfo info = (WristbandsInfo) msg.obj;
                    if (info != null) {
                        int stepNumber = 2 * info.getStep();
                        StepUpdateService.setBlueSteps(stepNumber);
                    }
                    break;
                case 9:
                    if (dialog != null) {
                        dialog.cancel();
                        startBLE();
                        if (connectMac != null) {
                            getWidget();
                            ShareInfo.saveTagString(DeviceManageActivity.this, ShareInfo.TAG_BLE_MAC, connectMac);
                            showToast("蓝牙添加成功");
                        }
                    }
                    break;
                case 10:
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    showToast("没有搜索到可用的设备");
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.devicemanage_btnScan:
                intent.setClass(this, MipcaActivityCapture.class);
                startActivityForResult(intent, REQUEST_SCAN);
                break;
            case R.id.devicemanage_btnDetails:
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                bundle.putString("title", "aiShoes");
                bundle.putString("url", "http://www.xiaoaimami.com/index.php?mod=mobile&id=2&name=shopwap&do=detail");
                startIntent(WebViewActivity.class, bundle);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN) {
            if (resultCode == RESULT_OK) {
                scanLeDevice(true);
            }
        } else if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
                return;
            } else {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
