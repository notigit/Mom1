package com.xiaoaitouch.mom.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.FetalMovementAdapter;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.FetalMovementBean;
import com.xiaoaitouch.mom.module.FetalMovementModule;
import com.xiaoaitouch.mom.module.RecordContractionsBean;
import com.xiaoaitouch.mom.module.RecordContractionsModule;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.sqlite.FetalMovementTables;
import com.xiaoaitouch.mom.sqlite.RecordContractionTables;
import com.xiaoaitouch.mom.util.SharedPreferencesUtil;
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
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 数胎动
 * User: huxin
 * Date: 2016/3/6
 * Time: 9:23
 * FIXME
 */
public class FetalMovementActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.fetal_movement_list_view)
    SwipeMenuListView fetalMovementListView;

    private TextView fetalMovementTipsTv;
    private TextView fetalMovementTimeShowTv;
    private TextView fetalMovementClickNumberTv;
    private TextView fetalMovementEventTv;
    private ImageView fetalMovementClickAnimationIv;
    private RelativeLayout fetalMovementRay;

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private FetalMovementAdapter fetalMovementAdapter;
    private List<FetalMovementModule> fetalMovementModuleList = new ArrayList<FetalMovementModule>();
    private String fetalMovementTime = "";
    private int totalsecond = 3600;
    private int currentsecond = 0;
    private int minute = 0;
    private String secondStr = "00";
    private String minuteStr = "00";
    private String eventStr = "60:00";
    private int fetalMovementNumbers = 0;
    private int colors = 0;

    private long userId = 0;
    private UserModule userModule;
    private boolean isChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fetal_movement_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        setHeader("数胎动");
        setRightIcon(R.drawable.help_tools_icon);
        View headView = LayoutInflater.from(mContext).inflate(
                R.layout.fetal_movement_head_view_item, null);
        fetalMovementTipsTv = (TextView) headView.findViewById(R.id.fetal_movement_tips_tv);
        fetalMovementTimeShowTv = (TextView) headView.findViewById(R.id.fetal_movement_time_show_tv);
        fetalMovementEventTv = (TextView) headView.findViewById(R.id.fetal_movement_event_tv);
        fetalMovementClickNumberTv = (TextView) headView.findViewById(R.id.fetal_movement_click_number_tv);
        fetalMovementClickAnimationIv = (ImageView) headView.findViewById(R.id.fetal_movement_click_animation_iv);
        fetalMovementRay = (RelativeLayout) headView.findViewById(R.id.fetal_movement_tips_ray);
        fetalMovementRay.setOnClickListener(this);
        fetalMovementEventTv.setOnClickListener(this);
        fetalMovementListView.addHeaderView(headView);

        userModule = MyApplication.instance.getUserModule();
        if (userModule != null) {
            userId = userModule.getUserId();
        }
        fetalMovementAdapter = new FetalMovementAdapter();
        fetalMovementModuleList = FetalMovementTables.queryFetalMovementModuleList(mContext, userId);
        if (fetalMovementModuleList == null || fetalMovementModuleList.size() <= 0) {
            getLoadData();
        }
        fetalMovementTime = SharedPreferencesUtil.getString(mContext, "fetal_movement_time", "");
        int fetalMovementNumber = SharedPreferencesUtil.getInt(mContext, "fetal_movement_number", 0);
        this.fetalMovementNumbers = fetalMovementNumber;
        if (!TextUtils.isEmpty(fetalMovementTime)) {
            String currentSysTime = StringUtils.getSysDate();
            int minute = StringUtils.getIntervalTime(currentSysTime, fetalMovementTime);
            if (minute >= 3600) {
                resultDispose();
            } else {
                currentsecond = totalsecond - minute;
                minute = currentsecond / 60;
                secondStr = StringUtils.getDataValues(currentsecond % 60);
                minuteStr = StringUtils.getDataValues(minute);
                eventStr = minuteStr + ":" + secondStr;
                fetalMovementTimeShowTv.setText(eventStr);
                fetalMovementEventTv.setText("放弃");
                if (fetalMovementNumber == 0) {
                    fetalMovementClickNumberTv.setText("- -");
                } else {
                    fetalMovementClickNumberTv.setText(fetalMovementNumbers + "次");
                }
                startTimers();
            }
        } else {
            currentsecond = totalsecond;
            fetalMovementTimeShowTv.setText("60:00");
            fetalMovementClickNumberTv.setText("- -");
        }
        fetalMovementAdapter.setData(fetalMovementModuleList);
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
        fetalMovementListView.setMenuCreator(creator);
        fetalMovementListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        fetalMovementListView.mTouchView.closeMenu();
                        mHandler.obtainMessage(2, position).sendToTarget();
                        break;
                }
            }
        });
        fetalMovementListView.setAdapter(fetalMovementAdapter);
    }

    private void getLoadData() {
        if (Utils.isNetworkConnected(mContext)) {
            GsonTokenRequest<FetalMovementBean> request = new GsonTokenRequest<FetalMovementBean>(
                    Request.Method.POST,
                    Configs.SERVER_URL + "/v3/getFm",
                    new Response.Listener<JsonResponse<FetalMovementBean>>() {

                        @Override
                        public void onResponse(
                                JsonResponse<FetalMovementBean> response) {
                            mHandler.obtainMessage(3, response).sendToTarget();
                        }
                    }, null) {

                @Override
                public Type getType() {
                    Type type = new TypeToken<JsonResponse<FetalMovementBean>>() {
                    }.getType();
                    return type;
                }
            };
            HttpApi.getFetalMovementData("/v3/getFm", request, null);
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @OnClick(R.id.top_right_iv)
    public void onRightActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("title", "胎动帮助");
        startIntent(HelpActivity.class, bundle);
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
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
            final List<FetalMovementModule> list = FetalMovementTables.submitFetalMovementModule(mContext, userId, 0);
            if (list != null && list.size() >= 1) {
                Gson gson = new Gson();
                String reslut = gson.toJson(list);
                StringRequest request = new StringRequest(Request.Method.POST,
                        Configs.SERVER_URL + "/v3/fm",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                try {
                                    ResultObj result = new ResultObj(response);
                                    switch (result.getState()) {
                                        case ResultObj.SUCCESS:
                                            for (int i = 0; i < list.size(); i++) {
                                                FetalMovementModule recordContractionsModules = list.get(i);
                                                recordContractionsModules.setType(1);
                                                FetalMovementTables.updateFetalMovementModule(mContext, recordContractionsModules);
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
                HttpApi.submitFetalMovementData("/v3/fm", request, reslut);
            }
        } else {
            onBackBtnClick();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fetal_movement_event_tv:
                String content = fetalMovementEventTv.getText().toString().trim();
                fetalMovementTimeShowTv.setText("60:00");
                fetalMovementClickNumberTv.setText("- -");
                fetalMovementRay.setBackgroundResource(R.drawable.draw_circle_default_bg);
                colors = getResources().getColor(R.color.tips_color);
                String tipsContent = "五分钟内连续记录只算一次有效记录";
                fetalMovementTipsTv.setText(Html.fromHtml("<font color = \"" + colors + "\">" + tipsContent + "</font>"));
                SharedPreferencesUtil.putInt(mContext, "fetal_movement_number", 0);
                if (content.equals("开始")) {
                    currentsecond = 3600;
                    fetalMovementNumbers = 0;
                    SharedPreferencesUtil.putString(mContext, "fetal_movement_time", StringUtils.getSysDate());
                    fetalMovementEventTv.setText("放弃");
                    startTimers();
                } else if (content.equals("放弃")) {
                    SharedPreferencesUtil.putString(mContext, "fetal_movement_time", "");
                    fetalMovementEventTv.setText("开始");
                    stopTimers();
                }
                break;
            case R.id.fetal_movement_tips_ray:
                String contents = fetalMovementEventTv.getText().toString().trim();
                if (contents.equals("放弃")) {
                    fetalMovementClickAnimationIv.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.largen_zoom_anim));
                    String numberStr = SharedPreferencesUtil.getString(mContext, "fetal_movement_number_time", "");
                    if (TextUtils.isEmpty(numberStr)) {
                        fetalMovementNumbers++;
                        SharedPreferencesUtil.putInt(mContext, "fetal_movement_number", fetalMovementNumbers);
                        SharedPreferencesUtil.putString(mContext, "fetal_movement_number_time", StringUtils.getSysDate());
                    } else {
                        int minute = StringUtils.getIntervalTime(StringUtils.getSysDate(), numberStr);
                        if (minute >= 300) {
                            fetalMovementNumbers++;
                            SharedPreferencesUtil.putInt(mContext, "fetal_movement_number", fetalMovementNumbers);
                            SharedPreferencesUtil.putString(mContext, "fetal_movement_number_time", StringUtils.getSysDate());
                        } else {
                            showToast("5分钟内连续多次胎动只算一次~");
                        }
                    }
                    fetalMovementClickNumberTv.setText(fetalMovementNumbers + "次");
                }
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    currentsecond--;
                    if (currentsecond <= 0) {
                        eventStr = "00:00";
                        resultDispose();
                        stopTimers();
                    } else {
                        minute = currentsecond / 60;
                        secondStr = StringUtils.getDataValues(currentsecond % 60);
                        minuteStr = StringUtils.getDataValues(minute);
                        eventStr = minuteStr + ":" + secondStr;
                    }
                    fetalMovementTimeShowTv.setText(eventStr);
                    break;
                case 2:
                    int position = (Integer) msg.obj;
                    FetalMovementModule fetalMovementModule = (FetalMovementModule) fetalMovementAdapter.getItem(position);
                    deleteFetalMovementModule(fetalMovementModule, position);
                    break;
                case 3:
                    JsonResponse<FetalMovementBean> response = (JsonResponse<FetalMovementBean>) msg.obj;
                    FetalMovementBean fetalMovementBean = response.data;
                    if (fetalMovementBean != null) {
                        List<FetalMovementModule> fetalMovementModules = fetalMovementBean.getList();
                        if (fetalMovementModules != null && fetalMovementModules.size() >= 1) {
                            fetalMovementModuleList = fetalMovementModules;
                            fetalMovementAdapter.setData(fetalMovementModuleList);
                            for (int i = 0; i < fetalMovementModules.size(); i++) {
                                FetalMovementModule fetalMovementModuless = fetalMovementModules.get(i);
                                fetalMovementModuless.setType(1);
                                FetalMovementTables.addFetalMovementModule(mContext, fetalMovementModuless);
                            }
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 删除数据
     *
     * @param fetalMovementModule
     * @param position
     */
    private void deleteFetalMovementModule(final FetalMovementModule fetalMovementModule, final int position) {
        if (Utils.isNetworkConnected(mContext)) {
            StringRequest request = new StringRequest(Request.Method.POST,
                    Configs.SERVER_URL + "/v3/deleteFm/" + fetalMovementModule.getCreateTime(),
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                ResultObj result = new ResultObj(response);
                                switch (result.getState()) {
                                    case ResultObj.SUCCESS:
                                        FetalMovementTables.deleteFetalMovementModule(mContext, userId, fetalMovementModule.getStartTime());
                                        fetalMovementModuleList.remove(position);
                                        fetalMovementAdapter.setData(fetalMovementModuleList);
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
            HttpApi.deleteFetalMovementModuleData("/v3/deleteFm/" + fetalMovementModule.getCreateTime(), request, null);
        }
    }

    private void resultDispose() {
        String tipsContent = "";
        if (fetalMovementNumbers >= 3) {
            fetalMovementRay.setBackgroundResource(R.drawable.draw_circle_blue_bg);
            colors = getResources().getColor(R.color.draw_circle_blue_bg);
            tipsContent = "1小时胎动3次表明胎儿很健康，妈妈请放心！";
        } else {
            if (fetalMovementNumbers < 2) {
                tipsContent = "1小时胎动少于2次表明胎儿有危险，在子宫内有缺氧现象，请及时去医院检查！";
                fetalMovementRay.setBackgroundResource(R.drawable.draw_circle_red_bg);
                colors = getResources().getColor(R.color.draw_circle_red_bg);
            } else if (fetalMovementNumbers < 3) {
                tipsContent = "1小时胎动少于3次表明胎儿有异常，建议去医院检查！";
                fetalMovementRay.setBackgroundResource(R.drawable.draw_circle_yellow_bg);
                colors = getResources().getColor(R.color.draw_circle_yellow_bg);
            }
        }
        fetalMovementEventTv.setText("开始");
        fetalMovementTimeShowTv.setText("00:00");
        fetalMovementTipsTv.setText(Html.fromHtml("<font color = \"" + colors + "\">" + tipsContent + "</font>"));

        String startTime = SharedPreferencesUtil.getString(mContext, "fetal_movement_time", "");
        FetalMovementModule fetalMovementModule = new FetalMovementModule();
        fetalMovementModule.setUserId(userId);
        fetalMovementModule.setNumber(fetalMovementNumbers);
        fetalMovementModule.setDate(startTime.substring(0, 10));
        fetalMovementModule.setCreateTime(StringUtils.getDateFromStrss(startTime) + "");
        fetalMovementModule.setType(0);
        fetalMovementModule.setStartTime(startTime.substring(11, startTime.length()));
        FetalMovementTables.addFetalMovementModule(mContext, fetalMovementModule);
        fetalMovementModuleList.add(0, fetalMovementModule);
        fetalMovementAdapter.setData(fetalMovementModuleList);
        isChange = true;

        SharedPreferencesUtil.putString(mContext, "fetal_movement_time", "");
        SharedPreferencesUtil.putString(mContext, "fetal_movement_number_time", "");
        SharedPreferencesUtil.putInt(mContext, "fetal_movement_number", 0);

    }

    private void startTimers() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(1);
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    isSubmitData();
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
