package com.xiaoaitouch.mom.app;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.MeasureHeartAdapter;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.MeasureHeartBean;
import com.xiaoaitouch.mom.module.MeasureHeartModule;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.sqlite.MeasureHeartTables;
import com.xiaoaitouch.mom.util.ImageProcessing;
import com.xiaoaitouch.mom.util.StringUtils;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.view.RoundProgressBar;
import com.xiaoaitouch.mom.view.swipemenulistview.SwipeMenu;
import com.xiaoaitouch.mom.view.swipemenulistview.SwipeMenuCreator;
import com.xiaoaitouch.mom.view.swipemenulistview.SwipeMenuItem;
import com.xiaoaitouch.mom.view.swipemenulistview.SwipeMenuListView;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 测心率
 * User: huxin
 * Date: 2016/3/6
 * Time: 9:27
 * FIXME
 */
public class MeasureHeartActivity extends BaseActivity {
    private static final AtomicBoolean processing = new AtomicBoolean(false);
    @Bind(R.id.measure_heart_list_view)
    SwipeMenuListView measureHeartListView;
    private TextView measureHeartShowTv;
    private ImageView measureHeartImgAm;
    private RoundProgressBar measureHeartRoundProgressBar;
    private TextView measureHeartEventTv;
    private SurfaceView preview;
    private SurfaceHolder previewHolder = null;


    private int second = 0;
    private Animation heartAnim;
    private MeasureHeartAdapter measureHeartAdapter;
    private static Camera camera = null;
    private static PowerManager.WakeLock wakeLock = null;


    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    public static enum TYPE {
        GREEN, RED
    }

    ;


    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;

    private String mDueTime = "";//怀孕的日期
    private String mCurrentDate = "";
    private int mCurrentWeek = 1;//当前周
    private int days = 0;//周的余数
    private String weekAndDay = "";
    private long userId = 0;
    private UserModule userModule;
    private List<MeasureHeartModule> dueTimeModuleList = new ArrayList<MeasureHeartModule>();
    private int beatsAvg = 0;
    private boolean isChange = false;
    private boolean isStartTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measure_heart_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        setHeader("测心率");
        setRightIcon(R.drawable.help_tools_icon);
        setUserData();
        if (mCurrentWeek > 40) {
            weekAndDay = StringUtils.getSysYyMmDd();
        } else {
            weekAndDay = "孕" + mCurrentWeek + "周+" + days + "天";
        }
        measureHeartAdapter = new MeasureHeartAdapter();
        dueTimeModuleList = MeasureHeartTables.queryMeasureHeartModuleList(mContext, userId);
        if (dueTimeModuleList == null || dueTimeModuleList.size() <= 0) {
            getLoadData();
        }
        heartAnim = AnimationUtils.loadAnimation(this, R.anim.anim_am);
        View headView = LayoutInflater.from(mContext).inflate(
                R.layout.measure_heart_head_view_item, null);
        preview = (SurfaceView) headView.findViewById(R.id.measure_heart_preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        measureHeartShowTv = (TextView) headView.findViewById(R.id.measure_heart_show_tv);
        measureHeartImgAm = (ImageView) headView.findViewById(R.id.measure_heart_img_am);
        measureHeartRoundProgressBar = (RoundProgressBar) headView.findViewById(R.id.measure_heart_roundProgressBar);
        measureHeartEventTv = (TextView) headView.findViewById(R.id.measure_heart_event_tv);
        measureHeartEventTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                measureHeartShowTv.setText("- -");
                mHandler.sendEmptyMessage(1);
            }
        });
        measureHeartListView.addHeaderView(headView);
        measureHeartAdapter.setData(dueTimeModuleList);
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
        measureHeartListView.setMenuCreator(creator);
        measureHeartListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        measureHeartListView.mTouchView.closeMenu();
                        mHandler.obtainMessage(3, position).sendToTarget();
                        break;
                }
            }
        });
        measureHeartListView.setAdapter(measureHeartAdapter);
    }

    private void getLoadData() {
        if (Utils.isNetworkConnected(mContext)) {
            GsonTokenRequest<MeasureHeartBean> request = new GsonTokenRequest<MeasureHeartBean>(
                    Request.Method.POST,
                    Configs.SERVER_URL + "/v3/getHr",
                    new Response.Listener<JsonResponse<MeasureHeartBean>>() {

                        @Override
                        public void onResponse(
                                JsonResponse<MeasureHeartBean> response) {
                            mHandler.obtainMessage(4, response).sendToTarget();
                        }
                    }, null) {

                @Override
                public Type getType() {
                    Type type = new TypeToken<JsonResponse<MeasureHeartBean>>() {
                    }.getType();
                    return type;
                }
            };
            HttpApi.getMeasureHeartData("/v3/getHr", request, null);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    heartControl();
                    break;
                case 2:
                    second++;
                    if (second > 100) {
                        second = 0;
                        isStartTest = false;
                        completeTest();
                        mHandler.removeMessages(2);
                    } else {
                        measureHeartRoundProgressBar.setProgress(second);
                        mHandler.sendEmptyMessageDelayed(2, 300);
                    }
                    break;
                case 3:
                    int position = (Integer) msg.obj;
                    MeasureHeartModule measureHeartModule = (MeasureHeartModule) measureHeartAdapter.getItem(position);
                    deleteMeasureHeartModule(measureHeartModule, position);
                    break;
                case 4:
                    JsonResponse<MeasureHeartBean> response = (JsonResponse<MeasureHeartBean>) msg.obj;
                    MeasureHeartBean measureHeartBean = response.data;
                    if (measureHeartBean != null) {
                        List<MeasureHeartModule> list = measureHeartBean.getList();
                        if (list != null && list.size() >= 1) {
                            dueTimeModuleList = list;
                            measureHeartAdapter.setData(dueTimeModuleList);
                            for (int i = 0; i < list.size(); i++) {
                                MeasureHeartModule measureHeartModules = list.get(i);
                                measureHeartModules.setType(1);
                                MeasureHeartTables.addMeasureHeartModule(mContext, measureHeartModules);
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
     * @param measureHeartModule
     * @param position
     */
    private void deleteMeasureHeartModule(final MeasureHeartModule measureHeartModule, final int position) {
        if (Utils.isNetworkConnected(mContext)) {
            StringRequest request = new StringRequest(Request.Method.POST,
                    Configs.SERVER_URL + "/v3/deleteHr/" + measureHeartModule.getCreateTime(),
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                ResultObj result = new ResultObj(response);
                                switch (result.getState()) {
                                    case ResultObj.SUCCESS:
                                        MeasureHeartTables.deleteMeasureHeartModule(mContext, userId, measureHeartModule.getDate());
                                        dueTimeModuleList.remove(position);
                                        measureHeartAdapter.setData(dueTimeModuleList);
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
            HttpApi.deleteMeasureHeartModuleData("/v3/deleteHr/" + measureHeartModule.getCreateTime(), request, null);
        }
    }

    private void completeTest() {
        second = 0;
        measureHeartEventTv.setText("开始");
        measureHeartRoundProgressBar.setProgress(0);
        turnLightOff(camera);
        String currentDate = StringUtils.getSysDate();
        MeasureHeartModule measureHeartModule = new MeasureHeartModule();
        measureHeartModule.setUserId(userId);
        measureHeartModule.setGweek(weekAndDay);
        measureHeartModule.setNumber(beatsAvg);
        measureHeartModule.setDate(currentDate);
        measureHeartModule.setCreateTime(StringUtils.getDateFromStrss(currentDate) + "");
        measureHeartModule.setType(0);
        MeasureHeartTables.addMeasureHeartModule(mContext, measureHeartModule);
        dueTimeModuleList.add(0, measureHeartModule);
        measureHeartAdapter.setData(dueTimeModuleList);
        isChange = true;
    }


    private void heartControl() {
        String content = measureHeartEventTv.getText().toString().trim();
        if (content.equals("开始")) {
            isStartTest = true;
            measureHeartEventTv.setText("停止");
            turnLightOn(camera);
            mHandler.sendEmptyMessage(2);
        } else {
            isStartTest = false;
            completeTest();
            mHandler.removeMessages(2);
        }
    }

    public static void turnLightOn(Camera mCamera) {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes == null) {
            return;
        }
        String flashMode = parameters.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
            }
        }
    }
    public static void turnLightOff(Camera mCamera) {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        if (flashModes == null) {
            return;
        }
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            } else {
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        camera = Camera.open();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @OnClick(R.id.top_right_iv)
    public void onRightActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 2);
        bundle.putString("title", "心率帮助");
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
            final List<MeasureHeartModule> list = MeasureHeartTables.submitMeasureHeartModule(mContext, userId, 0);
            if (list != null && list.size() >= 1) {
                Gson gson = new Gson();
                String reslut = gson.toJson(list);
                StringRequest request = new StringRequest(Request.Method.POST,
                        Configs.SERVER_URL + "/v3/hr",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                try {
                                    ResultObj result = new ResultObj(response);
                                    switch (result.getState()) {
                                        case ResultObj.SUCCESS:
                                            for (int i = 0; i < list.size(); i++) {
                                                MeasureHeartModule recordContractionsModules = list.get(i);
                                                recordContractionsModules.setType(1);
                                                MeasureHeartTables.updateMeasureHeartModule(mContext, recordContractionsModules);
                                            }
                                            onBackBtnClick();
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, null);
                HttpApi.submitMeasureHeartData("/v3/hr", request, reslut);
            }
        } else {
            onBackBtnClick();
        }
    }


    private PreviewCallback previewCallback = new PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            if (!processing.compareAndSet(false, true)) return;

            int width = size.width;
            int height = size.height;

            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize) averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            if (newType != currentType) {
                currentType = newType;
                if (isStartTest)
                    measureHeartImgAm.startAnimation(heartAnim);
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 10) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    processing.set(false);
                    return;
                }

                if (beatsIndex == beatsArraySize) beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                if (isStartTest)
                    measureHeartShowTv.setText(String.valueOf(beatsAvg));
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    };
    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
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
        int number = StringUtils.getDateSpace(mDueTime, mCurrentDate) + 1;
        if (number >= 7) {
            mCurrentWeek = number / 7;
            days = number % 7;
            if (days != 0) {
                mCurrentWeek = mCurrentWeek + 1;
            }
        } else {
            days = number;
        }
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
