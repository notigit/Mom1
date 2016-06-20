package com.xiaoaitouch.mom.app;

import android.os.Bundle;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 关于
 *
 * @author huxin
 * @data: 2016/1/10 12:39
 * @version: V1.0
 */
public class AboutActivity extends BaseActivity {
    private static final String simpleName = AboutActivity.class.getSimpleName();
    @Bind(R.id.about_version_tv)
    TextView aboutVersionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        ButterKnife.bind(this);
        aboutVersionTv.setText("Version" + Utils.getVersionName());
        setHeader("关于");

    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(simpleName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(simpleName);
    }
}
