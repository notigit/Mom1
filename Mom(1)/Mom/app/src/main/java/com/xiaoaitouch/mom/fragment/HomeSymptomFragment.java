package com.xiaoaitouch.mom.fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.SymptomAdapter;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.MainModle;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.module.SymptomBean;
import com.xiaoaitouch.mom.module.SymptomCountModle;
import com.xiaoaitouch.mom.module.SymptomModle;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.util.StringUtils;
import com.xiaoaitouch.mom.util.SymptomSelectManager;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.view.HistogramView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * <首页-症状>
 *
 * @author huxin
 * @data: 2016/1/6 17:02
 * @version: V1.0
 */
public class HomeSymptomFragment extends BaseFragment {

    @Bind(R.id.home_symptom_gridview)
    GridView homeSymptomGridview;
    @Bind(R.id.home_symptom_histogramview)
    HistogramView homeSymptomHistogramview;
    @Bind(R.id.symptom_details_ray)
    RelativeLayout symptomDetailsRay;
    @Bind(R.id.symptom_details_tv)
    TextView symptomDetailsTv;

    private int width = 0;
    private SymptomAdapter symptomAdapter;

    private MainModle mainModle = null;
    private List<SymptomModle> zz = null;
    private List<SymptomModle> zzl = null;

    private Float[] symptomYValues = new Float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private String[] symptomXValues = new String[10];
    private int mWeek = 0;
    private int second = 0;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.home_symptom_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViewData() {
        symptomAdapter = new SymptomAdapter(getActivity());
        homeSymptomGridview.setAdapter(symptomAdapter);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int leftMain = Utils.convertDpToPixelInt(
                getActivity(), 30 * 11);

        width = (screenWidth - leftMain) / 10;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    showSymptomDetails();
                    homeSymptomHistogramview.setWeekData(width, symptomYValues, symptomXValues, 10, true);
                    break;
                case 2:
                    second++;
                    if (second == 3) {
                        second = 0;
                        handler.removeMessages(2);
                        submitSymptom();
                    } else {
                        handler.sendEmptyMessageDelayed(2, 1000);
                    }
                    break;
            }
        }
    };


    public void setViewData(int week) {
        SymptomSelectManager.getSymptomSelectManager().removeAllSymptomModle();
        this.mWeek = week;
        mainModle = MyApplication.instance.getMainModle();
        if (mainModle != null) {
            zzl = mainModle.getZz();
            if (zzl != null && zzl.size() >= 1) {
                for (int i = 0; i < zzl.size(); i++) {
                    SymptomModle symptomModle = zzl.get(i);
                    if (symptomModle.getIsOk() == 1) {
                        SymptomSelectManager.getSymptomSelectManager().addSymptomSelectModule(symptomModle);
                        symptomDetailsTv.setVisibility(View.VISIBLE);
                        String title = symptomModle.getSymptom();
                        if (title.length()>=2){
                            symptomDetailsTv.setText(title.substring(0, 2) + "：" + symptomModle.getAbs());
                        }
                    }
                }
            }
            texturalData(mainModle.getZzCount());
            handler.sendEmptyMessage(1);
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mainModle = MyApplication.instance.getMainModle();
            if (mainModle != null) {
                texturalData(mainModle.getZzCount());
            }
            handler.sendEmptyMessage(1);
        }
    }

    /**
     * 构造数据
     */
    private void texturalData(List<SymptomCountModle> zzCount) {
        if (zzCount != null && zzCount.size() <= 10) {
            for (int i = 0; i < zzCount.size(); i++) {
                SymptomCountModle symptomCountModle = zzCount.get(i);
                symptomYValues[i] = Float.valueOf(symptomCountModle.getCounts());
                symptomXValues[i] = symptomCountModle.getSymptom();
            }
        }
        zz = mainModle.getZz();
        if (zz != null && zz.size() <= 10) {
            symptomAdapter.setData(zz);
        }
    }

    @OnItemClick(R.id.home_symptom_gridview)
    public void OnItemClickGridView(int position) {
        SymptomModle symptomModle = (SymptomModle) symptomAdapter.getItem(position);
        if (symptomModle.getIsOk() == 1) {
            symptomModle.setIsOk(0);
            SymptomSelectManager.getSymptomSelectManager().removeSymptomSelectModule(symptomModle);
        } else {
            symptomModle.setIsOk(1);
            SymptomSelectManager.getSymptomSelectManager().addSymptomSelectModule(symptomModle);
        }
        zz.set(position, symptomModle);
        symptomAdapter.setData(zz);
        //更新柱状图ui
        updateViewData(position);
        second = 0;
        handler.removeMessages(2);
        handler.sendEmptyMessage(2);
    }

    private void showSymptomDetails() {
        SymptomModle symptomModle = SymptomSelectManager.getSymptomSelectManager().currentSymptomModle();
        if (symptomModle != null) {
            symptomDetailsRay.setVisibility(View.VISIBLE);
            String title = symptomModle.getSymptom();
            if (title.length()>=2){
                symptomDetailsTv.setText(title.substring(0, 2) + "：" + symptomModle.getAbs());
            }
        } else {
            symptomDetailsRay.setVisibility(View.INVISIBLE);
        }
    }

    private void updateViewData(int postion) {
        List<SymptomModle> symptomModleList = symptomAdapter.getSymptomBeanList();
        MainModle mainModles = MyApplication.instance.getMainModle();
        List<SymptomCountModle> zzCount = mainModles.getZzCount();

        SymptomModle symptomModlel = zzl.get(postion);//老的数据
        SymptomCountModle symptomCountModle = zzCount.get(postion);//老的SymptomCountModle
        SymptomModle symptomModle = symptomModleList.get(postion);
        if (symptomModlel.getIsOk() == 0) {
            if (symptomModle.getIsOk() == 0) {
                symptomCountModle.setCounts(symptomCountModle.getCounts() - 1);
            } else if (symptomModle.getIsOk() == 1) {
            }
        } else if (symptomModlel.getIsOk() == 1) {
            if (symptomModle.getIsOk() == 0) {
            } else if (symptomModle.getIsOk() == 1) {
                symptomCountModle.setCounts(symptomCountModle.getCounts() + 1);
            }
        }
        zzCount.set(postion, symptomCountModle);
        mainModles.setZzCount(zzCount);
        mainModle = mainModles;
        if (mainModle != null) {
            texturalData(zzCount);
        }
        handler.sendEmptyMessage(1);
    }


    private void submitSymptom() {
        List<SymptomModle> symptomModleList = symptomAdapter.getSymptomBeanList();
        List<SymptomBean> sList = new ArrayList<SymptomBean>();
        for (int i = 0; i < symptomModleList.size(); i++) {
            SymptomModle sModel = symptomModleList.get(i);
            SymptomBean sBean = new SymptomBean();
            sBean.setIsOk(sModel.getIsOk());
            sBean.setSymptom(sModel.getSymptom());
            sBean.setSymptomId(sModel.getSymptomId());
            sList.add(sBean);
        }
        Gson gson = new Gson();
        String reslut = gson.toJson(sList);
        String[] str = {reslut, StringUtils.getCurrentTimeSs(), mWeek + ""};
        updateSymptom(str);
    }


    /**
     * 修改症状信息
     *
     * @param str
     */
    public void updateSymptom(String[] str) {
        StringRequest request = new StringRequest(Request.Method.POST,
                Configs.SERVER_URL + "/v3/mom/symptom",
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
                                    MyApplication.instance.setMainModle(mainModle);
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
        HttpApi.updateMsymptom("/v3/mom/symptom", request, str);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
