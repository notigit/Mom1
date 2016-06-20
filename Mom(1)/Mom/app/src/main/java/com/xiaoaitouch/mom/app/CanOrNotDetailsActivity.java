package com.xiaoaitouch.mom.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.module.CanOrNotDetailsBean;
import com.xiaoaitouch.mom.module.CanOrNotDetailsModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;
import com.xiaoaitouch.mom.util.Utils;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 能不能详情
 * User: huxin
 * Date: 2016/2/26
 * Time: 10:10
 * FIXME
 */
public class CanOrNotDetailsActivity extends BaseActivity {

    @Bind(R.id.can_or_not_details_iv)
    ImageView canOrNotDetailsIv;
    @Bind(R.id.can_or_not_details_abs_tv)
    TextView canOrNotDetailsAbsTv;
    @Bind(R.id.can_or_not_details_icon_tv)
    TextView canOrNotDetailsIconTv;
    @Bind(R.id.can_or_not_details_content_tv)
    TextView canOrNotDetailsContentTv;
    @Bind(R.id.can_or_not_details_buytips_tv)
    TextView canOrNotDetailsBuytipsTv;
    @Bind(R.id.can_or_not_details_lay)
    LinearLayout canOrNotDetailsLay;
    @Bind(R.id.can_or_not_details_view_lay)
    LinearLayout canOrNotDetailsViewLay;

    private String title;
    private int type = 0;
    private long id = 0l;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.can_or_not_details_activity);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        title = getIntent().getStringExtra("title");
        type = getIntent().getIntExtra("type", 0);
        id = getIntent().getLongExtra("id", 0l);
        setHeader(title);

        loadData();
    }

    private void loadData() {
        if (Utils.isNetworkConnected(mContext)) {
            GsonTokenRequest<CanOrNotDetailsModule> request = new GsonTokenRequest<CanOrNotDetailsModule>(
                    Request.Method.POST,
                    Configs.SERVER_URL + "/v3/caneat",
                    new Response.Listener<JsonResponse<CanOrNotDetailsModule>>() {

                        @Override
                        public void onResponse(
                                JsonResponse<CanOrNotDetailsModule> response) {
                            handler.obtainMessage(response.state, response).sendToTarget();
                        }
                    }, null) {

                @Override
                public Type getType() {
                    Type type = new TypeToken<JsonResponse<CanOrNotDetailsModule>>() {
                    }.getType();
                    return type;
                }
            };
            HttpApi.getCanOrNotDetails("/v3/caneat", request, id);
        } else {
            showToast("您处于网络断开状态！");
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Configs.UN_USE:
                    showToast("版本过低请升级新版本");
                    break;
                case Configs.FAIL:
                    JsonResponse<CanOrNotDetailsModule> response = (JsonResponse<CanOrNotDetailsModule>) msg.obj;
                    showToast(response.msg);
                    break;
                case Configs.SUCCESS:
                    JsonResponse<CanOrNotDetailsModule> responses = (JsonResponse<CanOrNotDetailsModule>) msg.obj;
                    CanOrNotDetailsModule canOrNotDetailsModule = responses.data;
                    if (canOrNotDetailsModule != null && canOrNotDetailsModule.getObj() != null) {
                        initViewData(canOrNotDetailsModule.getObj());
                    }
                    break;
            }
        }
    };

    private void initViewData(CanOrNotDetailsBean canOrNotDetailsBean) {
        canOrNotDetailsViewLay.setVisibility(View.VISIBLE);
        ImageLoader.getInstance().displayImage(Configs.IMAGE_URL + canOrNotDetailsBean.getBigImg(), canOrNotDetailsIv,
                DisplayImageOptionsUtils.getDisplayImageOptions());
        canOrNotDetailsAbsTv.setText("功能与功效：" + Html.fromHtml(canOrNotDetailsBean.getAbs()));
        canOrNotDetailsContentTv.setText(Html.fromHtml(canOrNotDetailsBean.getContent()));
        int can = canOrNotDetailsBean.getCan();
        Drawable drawable = null;
        switch (can) {
            case 0:
                drawable = getResources().getDrawable(R.drawable.can_or_not_can_icon);
                break;
            case 1:
                drawable = getResources().getDrawable(R.drawable.can_or_not_donot_icon);
                break;
            case 2:
                drawable = getResources().getDrawable(R.drawable.can_or_not_careful_icon);
                break;
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        canOrNotDetailsIconTv.setCompoundDrawables(drawable, null, null, null);
        if (type <= 9) {
            canOrNotDetailsLay.setVisibility(View.GONE);
        } else {
            canOrNotDetailsLay.setVisibility(View.VISIBLE);
            canOrNotDetailsBuytipsTv.setText(Html.fromHtml(canOrNotDetailsBean.getBuyTips()));
        }
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }

}
