package com.xiaoaitouch.mom.app;

import android.os.Bundle;
import android.webkit.WebView;

import com.xiaoaitouch.mom.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 帮助
 * User: huxin
 * Date: 2016/3/6
 * Time: 9:25
 * FIXME
 */
public class HelpActivity extends BaseActivity {
    @Bind(R.id.help_webview)
    WebView helpWebview;
    private int type = 0;
    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        title = getIntent().getStringExtra("title");
        type = getIntent().getIntExtra("type", 0);
        setHeader(title);
        helpWebview.getSettings().setJavaScriptEnabled(true);
        String url = "";
        if (type == 1) {
            url = "file:///android_asset/html/fetal_movement.html";
        } else if (type == 2) {
            url = "file:///android_asset/html/pregant_health.html";
        } else if (type == 3) {
            url = "file:///android_asset/html/record_contractions.html";
        }
        helpWebview.loadUrl(url);
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }
}
