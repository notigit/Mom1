package com.xiaoaitouch.mom.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.google.gson.reflect.TypeToken;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.NickNameModule;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.sqlite.UserTables;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.util.ViewUtils;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 修改昵称
 *
 * @author huxin
 * @data: 2016/1/9 17:45
 * @version: V1.0
 */
public class UpdateNickNameActivity extends BaseActivity {
    @Bind(R.id.nickname_input_et)
    EditText nicknameInputEt;

    private UserModule userModule;
    private String mNickName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_nickname_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        setHeader("昵称");
        userModule = MyApplication.instance.getUserModule();
        if (userModule != null && userModule.getNeckname() != null) {
            mNickName = userModule.getNeckname();
            nicknameInputEt.setText(mNickName);
        }
    }

    @OnClick(R.id.top_title_back_tv)
    public void back() {
        ViewUtils.hideSoftInput(mActivity, nicknameInputEt.getWindowToken());
        onBackBtnClick();
    }

    @OnClick(R.id.nickname_save_tv)
    public void submitData() {
        if (Utils.isNetworkConnected(mContext)) {
            ViewUtils.hideSoftInput(mActivity, nicknameInputEt.getWindowToken());
            String nickName = nicknameInputEt.getText().toString().trim();
            if (TextUtils.isEmpty(nickName)) {
                showToast("请输入新的昵称");
            } else if (mNickName.equals(nickName)) {
                showToast("您输入的昵称与之前一致");
            } else {
                GsonTokenRequest<NickNameModule> request = new GsonTokenRequest<NickNameModule>(Method.POST,
                        Configs.SERVER_URL + "/user/neckname",
                        new Listener<JsonResponse<NickNameModule>>() {

                            @Override
                            public void onResponse(
                                    JsonResponse<NickNameModule> response) {
                                switch (response.state) {
                                    case Configs.UN_USE:
                                        showToast("版本过低请升级新版本");
                                        break;
                                    case Configs.FAIL:
                                        showToast(response.msg);
                                        break;
                                    case Configs.SUCCESS:
                                        updateUserData(response.data);
                                        break;
                                }
                            }
                        }, null) {

                    @Override
                    public Type getType() {
                        Type type = new TypeToken<JsonResponse<NickNameModule>>() {
                        }.getType();
                        return type;
                    }
                };
                HttpApi.getUpdateNickName("/user/neckname", request, nickName);
            }
        } else {
            showToast("您处于网络断开状态！");
        }
    }

    private void updateUserData(NickNameModule nickName) {
        if (userModule != null) {
            userModule.setNeckname(nickName.getNeckname());
            // TODO: 2016/1/10
            MyApplication.instance.setUserModule(userModule);
            UserTables.updateUser(mActivity, userModule);
        }
        Intent intent = new Intent();
        intent.putExtra("nickName", nickName.getNeckname());
        setResult(1002, intent);
        onBackBtnClick();
    }
}
