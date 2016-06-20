package com.xiaoaitouch.mom.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.sqlite.UserTables;
import com.xiaoaitouch.mom.util.AppManager;
import com.xiaoaitouch.mom.util.ButtomDialog;
import com.xiaoaitouch.mom.util.DialogUtils;
import com.xiaoaitouch.mom.util.SharedPreferencesUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置界面
 *
 * @author huxin
 * @data: 2016/1/9 18:19
 * @version: V1.0
 */
public class SettingActivity extends BaseActivity {
    @Bind(R.id.setting_official_tv)
    TextView settingOfficialTv;
    @Bind(R.id.setting_user_agreement_ray)
    RelativeLayout settingUserAgreementRay;
    @Bind(R.id.setting_out_login_tv)
    TextView settingOutLoginTv;
    @Bind(R.id.setting_weibo_tv)
    TextView settingWeiboTv;
    @Bind(R.id.setting_weixin_ray)
    RelativeLayout settingWeixinRay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        ButterKnife.bind(this);
        AppManager.getAppManager().addActivity(mActivity);
        initViewData();
    }


    private void initViewData() {
        setHeader("设置");
        settingWeiboTv.setText("@小爱智能母婴");

    }

    @OnClick(R.id.top_title_back_tv)
    public void back() {
        onBackBtnClick();
    }

    @OnClick(R.id.setting_user_feedback_ray)
    public void openUserFeedBak() {
        startIntent(UserFeedBackActivity.class);
    }

    @OnClick(R.id.setting_about_ray)
    public void openAbout() {
        startIntent(AboutActivity.class);
    }

    @OnClick(R.id.setting_clear_cache_ray)
    public void clearCache(){
        DialogUtils.showAlertDialogChoose(mContext, "提示", "清除缓存后会减少磁盘空间使用，但是会增加部分图片下载量。",
                "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogUtils.BUTTON1:
                                dialog.dismiss();
                                break;
                            case DialogUtils.BUTTON2:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
    }

    @OnClick(R.id.setting_tell_ray)
    public void userTell() {
        DialogUtils.showAlertDialogChoose(mContext, "", "400-011-0883", "取消", "呼叫", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogUtils.BUTTON1:
                        dialog.dismiss();
                        break;
                    case DialogUtils.BUTTON2:
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                                + "4000110883"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    @OnClick(R.id.setting_official_ray)
    public void openWebView() {
        Bundle bundle = new Bundle();
        bundle.putString("title", "商城");
        bundle.putString("url", settingOfficialTv.getText().toString().trim());
        startIntent(WebViewActivity.class, bundle);
    }

    @OnClick(R.id.setting_user_agreement_ray)
    public void openXYWebView() {
        Bundle bundle = new Bundle();
        bundle.putString("title", "妈咪神器用户协议");
        bundle.putString("url", Configs.SERVER_URL+"/ios.html");
        startIntent(WebViewActivity.class, bundle);
    }

    @OnClick(R.id.setting_weibo_ray)
    public void openWeiBoWebView() {
        Bundle bundle = new Bundle();
        bundle.putString("title", "微博");
        bundle.putString("url", "http://weibo.com/234235360");
        startIntent(WebViewActivity.class, bundle);
    }

    @OnClick(R.id.setting_out_login_tv)
    public void outLogin() {
        outLoginView();
    }

    private void outLoginView() {
        LayoutInflater inflater = getLayoutInflater();
        final ButtomDialog mChooseDialog = new ButtomDialog(mActivity);
        View rootView = inflater
                .inflate(R.layout.out_login_item, null);
        TextView outLoginTv = (TextView) rootView
                .findViewById(R.id.out_login_tv);
        outLoginTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mChooseDialog.dismiss();
                SharedPreferencesUtil.putString(mContext,
                        "expert_user_info", "");
                UserTables.deleteUser(mActivity);
                startIntent(WelcomeActivity.class);
                AppManager.getAppManager().finishAllActivity();
            }
        });
        TextView cancelTv = (TextView) rootView
                .findViewById(R.id.action_sheet_cancel_tv);
        cancelTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {// 取消
                mChooseDialog.dismiss();
            }
        });
        mChooseDialog.setContentView(rootView);
        mChooseDialog.show();
    }
}
