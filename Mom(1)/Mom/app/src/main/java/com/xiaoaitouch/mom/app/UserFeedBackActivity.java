package com.xiaoaitouch.mom.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.util.Utils;

import org.json.JSONException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 意见反馈
 *
 * @author huxin
 * @data: 2016/1/10 12:01
 * @version: V1.0
 */
public class UserFeedBackActivity extends BaseActivity {
    @Bind(R.id.feedback_submit_content_et)
    EditText mFeedBackContentEt;
    @Bind(R.id.feedback_submit_emil_et)
    EditText mFeedBackEmilEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);
        ButterKnife.bind(this);
        setHeader("意见反馈");
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }

    @OnClick(R.id.feedback_submit_tv)
    public void submitFeedBack() {
        if (Utils.isNetworkConnected(mContext)) {
            String content = mFeedBackContentEt.getText().toString().trim();
            String emil = mFeedBackEmilEt.getText().toString().trim();
            if (!TextUtils.isEmpty(content)) {
                emil = emil == null ? "" : emil;
                String str[] = {content, emil};
                mBlockDialog.show();
                StringRequest request = new StringRequest(Request.Method.POST,
                        Configs.SERVER_URL + "/v2/feedback",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                mBlockDialog.cancel();
                                try {
                                    ResultObj result = new ResultObj(response);
                                    switch (result.getState()) {
                                        case ResultObj.FAIL:
                                            showToast(result.getMessage());
                                            break;
                                        case ResultObj.SUCCESS:
                                            onBackBtnClick();
                                            showToast("提交成功");
                                            break;
                                        case ResultObj.UN_USE:
                                            showToast("版本过低请升级新版本");
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        mBlockDialog.cancel();
                        showToast("网络数据加载失败");
                    }
                });
                HttpApi.submitFeedBack("/v2/feedback", request, str);
            } else {
                showToast("请输入要提交的意见反馈内容");
            }
        } else {
            showToast("您处于网络断开状态！");
        }
    }
}
