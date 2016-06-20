package com.xiaoaitouch.mom.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.RecordContractionsAdapter;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.CanOrNotListModule;
import com.xiaoaitouch.mom.module.RecordContractionsBean;
import com.xiaoaitouch.mom.module.RecordContractionsModule;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.service.TimerService;
import com.xiaoaitouch.mom.sqlite.RecordContractionTables;
import com.xiaoaitouch.mom.sqlite.SportTables;
import com.xiaoaitouch.mom.util.DialogUtils;
import com.xiaoaitouch.mom.util.StringUtils;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.view.swipemenulistview.SwipeMenu;
import com.xiaoaitouch.mom.view.swipemenulistview.SwipeMenuCreator;
import com.xiaoaitouch.mom.view.swipemenulistview.SwipeMenuItem;
import com.xiaoaitouch.mom.view.swipemenulistview.SwipeMenuListView;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 记宫缩
 * User: huxin
 * Date: 2016/3/6
 * Time: 9:21
 * FIXME
 */
public class RecordContractionsActivity extends BaseActivity {
    @Bind(R.id.record_contractions_list_view)
    SwipeMenuListView recordContractionsListView;

    private TextView recordContractionsTipsTv;
    private TextView recordContractionsTimeShowTv;
    private TextView recordContractionsEventTv;

    private int second = 0;
    private int minute = 0;
    private String secondStr = "00";
    private String minuteStr = "00";
    private String eventStr = "00:00";
    private String lastStartDate = "";
    private String currentStartDate = "";

    private boolean isChange = false;
    private boolean isStart = false;
    private long userId = 0;
    private UserModule userModule;
    private RecordContractionsAdapter recordContractionsAdapter;
    private List<RecordContractionsModule> recordContractionsModuleList = new ArrayList<RecordContractionsModule>();
    private TimerService mService = null;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((TimerService.TimerBinder) service).getService();
            mService.setParameter(mHandler, 3600);
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_contractions_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        setHeader("记宫缩");
        setRightIcon(R.drawable.help_tools_icon);
        View headView = LayoutInflater.from(mContext).inflate(
                R.layout.record_contractions_head_view_item, null);
        recordContractionsTipsTv = (TextView) headView.findViewById(R.id.record_contractions_tips_tv);
        recordContractionsTimeShowTv = (TextView) headView.findViewById(R.id.record_contractions_time_show_tv);
        recordContractionsEventTv = (TextView) headView.findViewById(R.id.record_contractions_event_tv);
        recordContractionsListView.addHeaderView(headView);

        userModule = MyApplication.instance.getUserModule();
        if (userModule != null) {
            userId = userModule.getUserId();
        }
        recordContractionsModuleList = RecordContractionTables.queryRecordContractionModuleList(mContext, userId);
        if (recordContractionsModuleList != null && recordContractionsModuleList.size() >= 1) {
            lastStartDate = ((RecordContractionsModule) recordContractionsModuleList.get(0)).getDate();
        } else {
            getLoadData();
        }
        recordContractionsAdapter = new RecordContractionsAdapter();
        recordContractionsAdapter.setData(recordContractionsModuleList);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                openItem.setBackground(R.color.app_top_color);
                openItem.setWidth(dp2px(80));
                openItem.setTitle("删除");
                openItem.setTitleSize(16);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        };
        recordContractionsListView.setMenuCreator(creator);
        recordContractionsListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        recordContractionsListView.mTouchView.closeMenu();
                        mHandler.obtainMessage(4, position).sendToTarget();
                        break;
                }
            }
        });
        recordContractionsListView.setAdapter(recordContractionsAdapter);
        bindService();
        recordContractionsEventTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendEmptyMessage(5);
            }
        });
    }

    private void getLoadData() {
        if (Utils.isNetworkConnected(mContext)) {
            GsonTokenRequest<RecordContractionsBean> request = new GsonTokenRequest<RecordContractionsBean>(
                    Request.Method.POST,
                    Configs.SERVER_URL + "/v3/getUc",
                    new Response.Listener<JsonResponse<RecordContractionsBean>>() {

                        @Override
                        public void onResponse(
                                JsonResponse<RecordContractionsBean> response) {
                            mHandler.obtainMessage(7, response).sendToTarget();
                        }
                    }, null) {

                @Override
                public Type getType() {
                    Type type = new TypeToken<JsonResponse<RecordContractionsBean>>() {
                    }.getType();
                    return type;
                }
            };
            HttpApi.getRecordContractionsData("/v3/getUc", request, null);
        }
    }

    private void bindService() {
        Intent intent = new Intent(mActivity, TimerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    /**
     * 保存数据
     */
    private void saveRecordData() {
        int min = 0;
        RecordContractionsModule recordContractionsModule = new RecordContractionsModule();
        currentStartDate = StringUtils.getSysDate();
        recordContractionsModule.setStartTime(StringUtils.getSysHHMMSS());
        recordContractionsModule.setCxTime(second);
        recordContractionsModule.setUserId(userId);
        recordContractionsModule.setCreateTime(StringUtils.getDateFromStrss(currentStartDate) + "");
        recordContractionsModule.setType(0);
        if (TextUtils.isEmpty(lastStartDate)) {
            lastStartDate = currentStartDate;
            recordContractionsTipsTv.setText("宫缩间隔较长，还需等待，注意记录宫缩哦~");
            recordContractionsTipsTv.setTextColor(getResources().getColor(R.color.black));
        } else {
            min = StringUtils.getIntervalTime(currentStartDate, lastStartDate) / 60;
            if (min <= 5) {
                recordContractionsTipsTv.setText("与上次宫缩间隙小于5分钟，可能是临产征兆，需要注意哦~");
                recordContractionsTipsTv.setTextColor(getResources().getColor(R.color.draw_circle_yellow_bg));
            } else {
                if (min >= 60) {
                    recordContractionsTipsTv.setText("宫缩间隔较长，还需等待，注意记录宫缩哦~");
                    recordContractionsTipsTv.setTextColor(getResources().getColor(R.color.black));
                }
            }
        }
        recordContractionsModule.setJgTime(StringUtils.getIntervalTime(currentStartDate, lastStartDate));
        lastStartDate = currentStartDate;
        recordContractionsModule.setDate(currentStartDate);
        RecordContractionTables.addRecordContractionModule(mContext, recordContractionsModule);
        recordContractionsModuleList.add(0, recordContractionsModule);
        recordContractionsAdapter.setData(recordContractionsModuleList);
        isChange = true;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://开始
                    second = (Integer) msg.obj;
                    minute = second / 60;
                    secondStr = StringUtils.getDataValues(second % 60);
                    minuteStr = StringUtils.getDataValues(minute);
                    eventStr = minuteStr + ":" + secondStr;
                    recordContractionsTimeShowTv.setText(eventStr);
                    break;
                case 2://停止
                    second = (Integer) msg.obj;
                    recordContractionsTimeShowTv.setText("00:00");
                    recordContractionsTimeShowTv.setBackgroundResource(R.drawable.draw_circle_default_bg);
                    break;
                case 3://完成
                    recordContractionsEventTv.setText("开始");
                    recordContractionsTimeShowTv.setText("00:00");
                    recordContractionsTimeShowTv.setBackgroundResource(R.drawable.draw_circle_default_bg);
                    break;
                case 4:
                    int position = (Integer) msg.obj;
                    RecordContractionsModule recordContractionsModule = (RecordContractionsModule) recordContractionsAdapter.getItem(position);
                    deleteRecordContractionsModule(recordContractionsModule, position);
                    break;
                case 5:
                    String str = recordContractionsEventTv.getText().toString().trim();
                    if (!TextUtils.isEmpty(lastStartDate)) {
                        int time = StringUtils.getIntervalTime(StringUtils.getSysDate(), lastStartDate);
                        if (time < 5) {
                            showToast("记录宫缩的时间间隔必须大于5秒");
                        } else {
                            if (str.equals("开始")) {
                                mService.startTime();
                                recordContractionsEventTv.setText("结束");
                            } else {
                                recordContractionsEventTv.setText("开始");
                                mService.stopTime();
                                saveRecordData();
                            }
                        }
                    } else {
                        if (str.equals("开始")) {
                            mService.startTime();
                            recordContractionsEventTv.setText("结束");
                        } else {
                            recordContractionsEventTv.setText("开始");
                            mService.stopTime();
                            saveRecordData();
                        }
                    }
                    break;
                case 6:
                    if (mConnection != null) {
                        unbindService(mConnection);
                        mConnection = null;
                    }
                    isSubmitData();
                    break;
                case 7:
                    JsonResponse<RecordContractionsBean> response = (JsonResponse<RecordContractionsBean>) msg.obj;
                    RecordContractionsBean recordContractionsBean = response.data;
                    if (recordContractionsBean != null) {
                        List<RecordContractionsModule> recordContractions = recordContractionsBean.getList();
                        if (recordContractions != null && recordContractions.size() >= 1) {
                            recordContractionsModuleList = recordContractions;
                            recordContractionsAdapter.setData(recordContractionsModuleList);
                            lastStartDate = ((RecordContractionsModule) recordContractionsModuleList.get(0)).getDate();
                            for (int i = 0; i < recordContractions.size(); i++) {
                                RecordContractionsModule recordContractionsModules = recordContractions.get(i);
                                recordContractionsModules.setType(1);
                                RecordContractionTables.addRecordContractionModule(mContext, recordContractionsModules);
                            }
                        }
                    }
                    break;
            }
        }
    };

    @OnClick(R.id.top_right_iv)
    public void onRightActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 3);
        bundle.putString("title", "宫缩帮助");
        startIntent(HelpActivity.class, bundle);
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        abandonRecord();
    }

    private void abandonRecord() {
        String str = recordContractionsEventTv.getText().toString().trim();
        if (str.equals("结束")) {
            isStart = true;
        } else {
            isStart = false;
        }
        if (isStart) {
            DialogUtils.showAlertDialogChoose(mContext, "提示", "退出后本次记录将丢失，确定要放弃么？", "取消", "确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogUtils.BUTTON1:
                            dialog.dismiss();
                            break;
                        case DialogUtils.BUTTON2:
                            mHandler.sendEmptyMessage(6);
                            dialog.dismiss();
                            break;

                        default:
                            break;
                    }
                }
            });
        } else
            isSubmitData();
    }

    private void isSubmitData() {
        if (isChange) {
            submitData();
        } else {
            onBackBtnClick();
        }
    }

    private void submitData() {
        if (Utils.isNetworkConnected(mContext)) {
            final List<RecordContractionsModule> list = RecordContractionTables.submitRecordContractionModule(mContext, userId, 0);
            if (list != null && list.size() >= 1) {
                Gson gson = new Gson();
                String reslut = gson.toJson(list);
                StringRequest request = new StringRequest(Request.Method.POST,
                        Configs.SERVER_URL + "/v3/uc",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                try {
                                    ResultObj result = new ResultObj(response);
                                    switch (result.getState()) {
                                        case ResultObj.SUCCESS:
                                            for (int i = 0; i < list.size(); i++) {
                                                RecordContractionsModule recordContractionsModules = list.get(i);
                                                recordContractionsModules.setType(1);
                                                RecordContractionTables.updateRecordContractionModule(mContext, recordContractionsModules);
                                            }
                                            onBackBtnClick();
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {

                    }
                });
                HttpApi.submitRecordContractionsData("/v3/uc", request, reslut);
            }
        } else {
            onBackBtnClick();
        }
    }

    /**
     * 删除数据
     *
     * @param recordContractionsModule
     * @param position
     */
    private void deleteRecordContractionsModule(final RecordContractionsModule recordContractionsModule, final int position) {
        if (Utils.isNetworkConnected(mContext)) {
            StringRequest request = new StringRequest(Request.Method.POST,
                    Configs.SERVER_URL + "/v3/deleteUc/" + recordContractionsModule.getCreateTime(),
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                ResultObj result = new ResultObj(response);
                                switch (result.getState()) {
                                    case ResultObj.SUCCESS:
                                        RecordContractionTables.deleteRecordContractionModule(mContext, userId, recordContractionsModule.getStartTime());
                                        lastStartDate = recordContractionsModule.getDate();
                                        recordContractionsModuleList.remove(position);
                                        recordContractionsAdapter.setData(recordContractionsModuleList);
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {

                }
            });
            HttpApi.deleteRecordContractionsData("/v3/deleteUc/" + recordContractionsModule.getCreateTime(), request, null);
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    abandonRecord();
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
