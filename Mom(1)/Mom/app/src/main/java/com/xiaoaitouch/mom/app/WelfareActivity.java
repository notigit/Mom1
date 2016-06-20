package com.xiaoaitouch.mom.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.WelfareAdapter;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.module.WelfareBean;
import com.xiaoaitouch.mom.module.WelfareModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.util.Utils;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by Administrator on 2016/1/26.
 */
public class WelfareActivity extends BaseActivity {
    @Bind(R.id.weldare_lv)
    ListView weldareLv;

    private WelfareAdapter welfareAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welfare_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    @OnClick(R.id.top_title_back_tv)
    public void back() {
        onBackBtnClick();
    }

    private void initViewData() {
        setHeader("福利");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        welfareAdapter = new WelfareAdapter();
        welfareAdapter.setScreenWidth(screenWidth);
        weldareLv.setAdapter(welfareAdapter);
        loadData();
    }

    private void loadData() {
        if (Utils.isNetworkConnected(mContext)) {
            mBlockDialog.show();
            GsonTokenRequest<WelfareModule> request = new GsonTokenRequest<WelfareModule>(
                    Request.Method.POST,
                    Configs.SERVER_URL + "/v2/advert",
                    new Response.Listener<JsonResponse<WelfareModule>>() {

                        @Override
                        public void onResponse(
                                JsonResponse<WelfareModule> response) {
                            mBlockDialog.dismiss();
                            switch (response.state) {
                                case Configs.UN_USE:
                                    showToast("版本过低请升级新版本");
                                    break;
                                case Configs.FAIL:
                                    showToast(response.msg);
                                    break;
                                case Configs.SUCCESS:
                                    WelfareModule welfareModule = response.data;
                                    mHandler.obtainMessage(2, welfareModule.getList())
                                            .sendToTarget();

                                    break;
                            }
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError arg0) {
                    mBlockDialog.dismiss();
                    showToast("网络数据加载失败");
                }
            }) {

                @Override
                public Type getType() {
                    Type type = new TypeToken<JsonResponse<WelfareModule>>() {
                    }.getType();

                    return type;
                }
            };
            HttpApi.getWelfareData("/v2/advert", request, null);
        } else {
            showToast("您处于网络断开状态！");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    welfareAdapter.setViewData((List<WelfareBean>) msg.obj);
                    break;
            }
        }
    };

    @OnItemClick(R.id.weldare_lv)
    public void openOnItemClick(int position) {
        WelfareBean welfareModule = (WelfareBean) welfareAdapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putString("title", welfareModule.getTitle());
        bundle.putString("url", welfareModule.getUrl());
        bundle.putInt("type", 1);
        startIntent(WebViewActivity.class, bundle);
    }
}
