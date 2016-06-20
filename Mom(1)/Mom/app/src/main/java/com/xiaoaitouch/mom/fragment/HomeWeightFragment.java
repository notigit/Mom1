package com.xiaoaitouch.mom.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.MaWeightModle;
import com.xiaoaitouch.mom.module.MainModle;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.util.StringUtils;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.view.HistogramView;
import com.xiaoaitouch.mom.view.WeightView;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * <首页-体重>
 *
 * @author huxin
 * @data: 2016/1/6 17:02
 * @version: V1.0
 */
public class HomeWeightFragment extends BaseFragment {

    @Bind(R.id.home_weight_histogramview)
    HistogramView homeWeightHistogramview;
    @Bind(R.id.home_weight_yesterday_tv)
    TextView homeWeightYesterdayTv;
    @Bind(R.id.home_weight_view)
    WeightView homeWeightView;
    @Bind(R.id.home_weight_report_tv)
    TextView homeWeightReportTv;
    @Bind(R.id.home_weight_imgCache)
    ImageView imgCache;

    private int width = 0;
    private MainModle mainModle = null;
    private int postion = 0;
    private Float[] weightYValues = new Float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private int mLength = 7;
    private String[] weightReportStr = null;
    private int second = 0;
    private float weight = 0;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.home_weight_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViewData() {
        weightReportStr = getActivity().getResources().getStringArray(R.array.home_weight);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int leftMain = Utils.convertDpToPixelInt(
                getActivity(), 40 * 8);
        width = (screenWidth - leftMain) / 7;
        homeWeightView.setOnWeightListener(new WeightView.OnWeightListener() {
            @Override
            public void onWeight(float weight) {
                second = 0;
                handler.removeMessages(2);
                handler.obtainMessage(2, weight).sendToTarget();
            }
        });

        //缓存体重使用，必须设置
        imgCache.setTag(false);
    }

    public void onRelease() {
        if (!(Boolean) imgCache.getTag()) {
            imgCache.setTag(true);

            Bitmap bmpCache = Bitmap.createBitmap(homeWeightView.getWidth()
                    , homeWeightView.getHeight(), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(bmpCache);
            homeWeightView.draw(canvas);

            imgCache.setImageBitmap(bmpCache);
            imgCache.setVisibility(View.VISIBLE);
            homeWeightView.setVisibility(View.INVISIBLE);
        }
    }

    public void onGenerate() {
        homeWeightView.setVisibility(View.VISIBLE);
        imgCache.setVisibility(View.GONE);
        imgCache.setTag(false);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    homeWeightHistogramview.setWeekData(width, weightYValues, null, mLength, true);
                    weightReport();
                    break;
                case 2:
                    if (msg.obj != null) {
                        weight = (Float) msg.obj;
                        weightYValues[postion] = weight;
                        homeWeightHistogramview.setWeekData(width, weightYValues, null, mLength, false);
                    }
                    second++;
                    if (second == 3) {
                        second = 0;
                        handler.removeMessages(2);
                        if (weight != 0) {
                            String[] str = {weight + "", StringUtils.getCurrentTimeSs()};
                            updateWeight(str);
                        }
                    } else {
                        handler.sendEmptyMessageDelayed(2, 1000);
                    }
                    break;
            }
        }
    };

    public void setViewData(int length) {
        if (length == 0) {
            mLength = 7;
        } else {
            mLength = length;
        }
        mainModle = MyApplication.instance.getMainModle();
        if (mainModle != null) {
            texturalData();
        }
        handler.sendEmptyMessage(1);
    }

    private void weightReport() {
        String weightReport = "";
        MainModle mainModle = MyApplication.instance.getMainModle();
        UserModule userModule = MyApplication.instance.getUserModule();
        if (mainModle != null) {
            List<MaWeightModle> mw = mainModle.getMw();
            if (mw != null && mw.size() >= 1) {
                int postion = mw.size() - 1;
                MaWeightModle maWeightModle = mw.get(postion);
                if (maWeightModle != null && !TextUtils.isEmpty(userModule.getHeight())) {
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    float height = Float.valueOf(userModule.getHeight()) / 100;
                    String mValue = decimalFormat.format(height);
                    height = Float.valueOf(mValue);
                    float bmi = Float.valueOf(maWeightModle.getWeight()) / (height * height);
                    if (bmi < 18.5) {
                        weightReport = weightReportStr[0];
                    } else if (18.5 <= bmi && bmi < 23.9) {
                        weightReport = weightReportStr[1];
                    } else if (23.9 <= bmi && bmi < 30) {
                        weightReport = weightReportStr[2];
                    } else if (bmi >= 30) {
                        weightReport = weightReportStr[3];
                    }
                    homeWeightReportTv.setText(weightReport);
                }
            }
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mainModle = MyApplication.instance.getMainModle();
            if (mainModle != null) {
                texturalData();
            }
            handler.sendEmptyMessage(1);
        }
    }

    /**
     * 构造数据
     */
    private void texturalData() {
        List<MaWeightModle> mw = mainModle.getMw();
        if (mw != null && mw.size() <= 7 && mw.size() >= 1) {
            postion = mw.size() - 1;
            for (int i = 0; i < mw.size(); i++) {
                MaWeightModle maWeightModle = mw.get(i);
                weightYValues[i] = Float.valueOf(maWeightModle.getWeight());
            }
            homeWeightView.setSelection(Float.valueOf(mw.get(postion).getWeight()));
            if (postion == 0) {
                homeWeightYesterdayTv.setText("昨天体重：" + mw.get(postion).getWeight() + "KG");
            } else {
                homeWeightYesterdayTv.setText("昨天体重：" + mw.get(postion - 1).getWeight() + "KG");
            }
        }
    }


    /**
     * 修改体重
     *
     * @param str
     */
    public void updateWeight(String[] str) {
        StringRequest request = new StringRequest(Request.Method.POST,
                Configs.SERVER_URL + "/user/mom/weight",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            ResultObj result = new ResultObj(response);
                            switch (result.getState()) {
                                case ResultObj.FAIL:
                                    showToast(result.getMessage());
                                    break;
                                case ResultObj.SUCCESS:
                                    MainModle mainModle = MyApplication.instance.getMainModle();
                                    if (mainModle!=null){
                                        List<MaWeightModle> mw = mainModle.getMw();
                                        if (mw != null) {
                                            MaWeightModle maWeightModle = new MaWeightModle();
                                            maWeightModle.setDate(StringUtils.getCurrentTimeSs());
                                            maWeightModle.setWeight(weightYValues[postion] + "");
                                            mw.set(postion, maWeightModle);
                                            mainModle.setMw(mw);
                                            MyApplication.instance.setMainModle(mainModle);
                                            weightReport();
                                        }
                                    }
                                    break;
                                case ResultObj.UN_USE:
                                    showToast("版本过低请升级新版本");
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
        HttpApi.updateMsWeight("/user/mom/weight", request, str);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
