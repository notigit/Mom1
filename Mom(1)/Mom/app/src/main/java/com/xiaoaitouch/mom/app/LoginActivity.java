package com.xiaoaitouch.mom.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.MineInfoModule;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.sqlite.UserTables;
import com.xiaoaitouch.mom.util.SharedPreferencesUtil;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.util.ViewUtils;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录
 *
 * @author huxin
 * @data: 2016/1/10 14:27
 * @version: V1.0
 */
public class LoginActivity extends BaseActivity {
    @Bind(R.id.login_input_content_et)
    EditText loginInputContentEt;
    @Bind(R.id.login_get_captchas_tv)
    TextView loginGetCaptchasTv;
    @Bind(R.id.login_input_captchas_et)
    EditText loginInputCaptchasEt;
    @Bind(R.id.login_tips_tv)
    TextView loginTips;
    @Bind(R.id.login_tips_one_tv)
    TextView loginTipsTv;
    @Bind(R.id.login_tv)
    TextView submitTv;


    private Timer mTimer = new Timer();
    private int mDelayTime = 60;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        initViewData();
        mHandler.sendEmptyMessageDelayed(3, 1000);
    }


    private void initViewData() {
        type = getIntent().getIntExtra("type", 0);
        if (type == 1) {
            setHeader("登录");
            loginTipsTv.setText("*登录即表示你同意");
            loginTips.setText(" <<妈咪神器用户协议>>");
            submitTv.setText("登录");
        } else {
            setHeader("注册");
            loginTipsTv.setText("*注册即表示你同意");
            loginTips.setText(" <<妈咪神器用户协议>>");
            submitTv.setText("注册");
        }
    }

    @OnClick(R.id.top_title_back_tv)
    public void back() {
        onBackBtnClick();
    }


    @OnClick(R.id.login_get_captchas_tv)
    public void sendCode() {
        ViewUtils.hideSoftInput(mActivity, loginInputContentEt.getWindowToken());
        if (Utils.isNetworkConnected(mContext)) {
            String mTell = loginInputContentEt.getText().toString().trim();
            if (!TextUtils.isEmpty(mTell)) {
                mBlockDialog.show();
                StringRequest request = new StringRequest(Method.POST,
                        Configs.SERVER_URL + "/send/code",
                        new Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                mBlockDialog.dismiss();
                                try {
                                    ResultObj result = new ResultObj(response);
                                    switch (result.getState()) {
                                        case ResultObj.FAIL:
                                            showToast(result.getMessage());
                                            break;
                                        case ResultObj.SUCCESS:
                                            mTimer.schedule(task, 1000, 1000);
                                            break;
                                        case ResultObj.UN_USE:
                                            showToast("版本过低请升级新版本");
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        mBlockDialog.dismiss();
                        showToast("网络数据加载失败");
                    }
                });
                HttpApi.sendCode("/send/code", request, mTell);
            } else {
                showToast("请输入手机号码");
            }
        } else {
            showToast("您处于网络断开状态！");
        }
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            mDelayTime--;
            Message message = new Message();
            message.what = 1;
            mHandler.sendMessage(message);
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    loginGetCaptchasTv.setTextColor(getResources().getColor(
                            R.color.textView_color));
                    loginGetCaptchasTv.setText(mDelayTime + "秒");
                    loginGetCaptchasTv.setClickable(false);
                    if (mDelayTime < 0) {
                        mTimer.cancel();
                        loginGetCaptchasTv.setTextColor(getResources().getColor(
                                R.color.app_top_color));
                        loginGetCaptchasTv.setText(getResources().getString(
                                R.string.mine_register_captchas_tv));
                        loginGetCaptchasTv.setClickable(true);
                    }
                    break;
                case 2:
                    MineInfoModule mineInfo = (MineInfoModule) msg.obj;
                    MyApplication.instance.setUserModule(mineInfo.getUserInfo());
                    if (type == 1) {
                        int age = Integer.valueOf(mineInfo.getUserInfo()
                                .getHeight());
                      String lastTime=  mineInfo.getUserInfo().getLastMensesTime();
                      String dueTime=  mineInfo.getUserInfo().getDueTime();
                        if (age <= 1||Integer.valueOf(lastTime)==0&&Integer.valueOf(dueTime)==0) {
                            startIntent(GuideActivity.class);
                        } else {
                            UserTables.addUser(mActivity, mineInfo.getUserInfo());
                            startIntent(MainActivity.class);
                        }
                    } else if (type == 2) {
                        startIntent(GuideActivity.class);
                    }
                    onBackBtnClick();
                    break;

                case 3:
                    loginInputContentEt.setFocusable(true);
                    loginInputContentEt.setFocusableInTouchMode(true);
                    loginInputContentEt.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                    break;

                default:
                    break;
            }
        }
    };


    @OnClick(R.id.login_tv)
    public void login() {
        if (Utils.isNetworkConnected(mContext)) {
            String tell = loginInputContentEt.getText().toString().trim();
            String captchas = loginInputCaptchasEt.getText().toString().trim();
            if (TextUtils.isEmpty(tell)) {
                showToast("请输入手机号码");
            } else if (TextUtils.isEmpty(captchas)) {
                showToast("请输入验证码");
            } else {
                String[] str = {tell, captchas};
                mBlockDialog.show();
                GsonTokenRequest<MineInfoModule> request = new GsonTokenRequest<MineInfoModule>(
                        Method.POST,
                        Configs.SERVER_URL + "/user/login",
                        new Listener<JsonResponse<MineInfoModule>>() {

                            @Override
                            public void onResponse(
                                    JsonResponse<MineInfoModule> response) {
                                mBlockDialog.dismiss();
                                switch (response.state) {
                                    case Configs.UN_USE:
                                        showToast("版本过低请升级新版本");
                                        break;
                                    case Configs.FAIL:
                                        showToast(response.msg);
                                        break;
                                    case Configs.SUCCESS:
                                        mHandler.obtainMessage(2, response.data)
                                                .sendToTarget();
                                        break;
                                }
                            }

                        }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        mBlockDialog.dismiss();
                        showToast("网络数据加载失败");
                    }
                }) {

                    @Override
                    public Type getType() {
                        Type type = new TypeToken<JsonResponse<MineInfoModule>>() {
                        }.getType();

                        return type;
                    }
                };
                HttpApi.getUserLogin("/user/login", request, str);
            }
        } else {
            showToast("您处于网络断开状态！");
        }
    }

    @OnClick(R.id.login_tips_tv)
    public void lookProtocol() {
        Bundle bundle = new Bundle();
        bundle.putString("title", "妈咪神器用户协议");
        bundle.putString("url", Configs.SERVER_URL + "/ios.html");
        startIntent(WebViewActivity.class, bundle);
    }
}
