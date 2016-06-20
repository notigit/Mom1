package com.xiaoaitouch.mom.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.google.gson.reflect.TypeToken;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.module.CollectModule;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.util.MyTextUtils;
import com.xiaoaitouch.mom.util.Utils;

import org.json.JSONException;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 网页
 *
 * @author huxin
 * @data: 2016/1/15 17:57
 * @version: V1.0
 */
public class WebViewActivity extends BaseActivity {

    @Bind(R.id.webview)
    WebView webview;
    @Bind(R.id.webview_progressbar)
    ProgressBar webviewProgressbar;
    @Bind(R.id.top_webview_close_tv)
    TextView webviewCloseTv;
    @Bind(R.id.top_right_iv)
    ImageView topCollectIv;


    private String title;// 标题
    private String url;
    private int type = 0;
    private CollectModule collectModule = null;
    private boolean collectStatic = false;
    private int postion = 0;

    // 起始页url
    private String mStartUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    @OnClick(R.id.top_title_back_tv)
    public void back() {
        if (type == 2) {
            webViewGoBack();
        } else {
            onBackBtnClick();
        }
    }

    @OnClick(R.id.top_webview_close_tv)
    public void closeWebView() {
        onBackBtnClick();
    }

    private void initViewData() {
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        type = getIntent().getIntExtra("type", 0);
        postion = getIntent().getIntExtra("postion", 0);
        if (type == 2) {
            webviewCloseTv.setVisibility(View.VISIBLE);
        }
        this.mStartUrl = url;
        setHeader(title);

        webview.setVisibility(View.VISIBLE); // 隐藏webview
        webview.clearHistory();
        webview.clearView();
        webview.loadUrl(url);


        webview.setHorizontalScrollBarEnabled(false);
        webview.setVerticalScrollbarOverlay(false);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.requestFocus();
        webview.setWebViewClient(new MyWebViewClient());
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                webviewProgressbar.setProgress(newProgress);
                webviewProgressbar.postInvalidate();
            }
        });

    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webviewProgressbar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (type == 1) {
                webviewProgressbar.setVisibility(View.VISIBLE);
                webviewProgressbar.setProgress(0);
            } else {
                if (url.equals("http://app.likemami.com/")) {
                    webview.stopLoading();
                    handler.sendEmptyMessage(4);
                } else {
                    handler.obtainMessage(2, url).sendToTarget();
                }
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view,
                                                final String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            webviewProgressbar.setVisibility(View.GONE);
            view.clearView();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    switch (collectModule.getCollection()) {
                        case -1:
                            topCollectIv.setVisibility(View.INVISIBLE);
                            break;
                        case 0:
                            collectStatic = false;
                            topCollectIv.setImageResource(R.drawable.collect_no_icon);
                            topCollectIv.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            collectStatic = true;
                            topCollectIv.setImageResource(R.drawable.collect_select_icon);
                            topCollectIv.setVisibility(View.VISIBLE);
                            break;
                    }
                    break;

                case 2:
                    webviewProgressbar.setVisibility(View.VISIBLE);
                    webviewProgressbar.setProgress(0);
                    if (type == 2) {
                        getCollect((String) msg.obj);
                    } else {
                        webviewCloseTv.setVisibility(View.INVISIBLE);
                        topCollectIv.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 3:
                    if (collectStatic) {
                        collectStatic = false;
                        topCollectIv.setImageResource(R.drawable.collect_no_icon);
                    } else {
                        collectStatic = true;
                        topCollectIv.setImageResource(R.drawable.collect_select_icon);
                    }
                    break;
                case 4:
                    startIntent(EasemobActivity.class);
                    break;
            }
        }
    };

    private void getCollect(String url) {
        if (Utils.isNetworkConnected(mContext)) {
            GsonTokenRequest<CollectModule> request = new GsonTokenRequest<CollectModule>(
                    Method.POST,
                    Configs.SERVER_URL + "/v3/check",
                    new Listener<JsonResponse<CollectModule>>() {

                        @Override
                        public void onResponse(
                                JsonResponse<CollectModule> response) {
                            switch (response.state) {
                                case Configs.UN_USE:
                                    showToast("版本过低请升级新版本");
                                    break;
                                case Configs.FAIL:
                                    showToast(response.msg);
                                    break;
                                case Configs.SUCCESS:
                                    collectModule = response.data;
                                    handler.sendEmptyMessage(1);
                                    break;
                            }
                        }

                    }, null) {

                @Override
                public Type getType() {
                    Type type = new TypeToken<JsonResponse<CollectModule>>() {
                    }.getType();

                    return type;
                }
            };
            HttpApi.getCollect("/v3/check", request, url);
        }
    }

    @OnClick(R.id.top_right_iv)
    public void collectAddress() {
        if (Utils.isNetworkConnected(mContext)) {
            StringRequest request = new StringRequest(Method.POST,
                    Configs.SERVER_URL + "/v3/collection",
                    new Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                ResultObj result = new ResultObj(response);
                                switch (result.getState()) {
                                    case ResultObj.FAIL:
                                        showToast(result.getMessage());
                                        break;
                                    case ResultObj.SUCCESS:
                                        handler.sendEmptyMessage(3);
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
            HttpApi.getCollectAddress("/v3/collection", request, getCurrentWebViewUrl());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void webViewGoBack() {
        if (!goBack()) {
            onBackBtnClick();
        } else {
            webview.goBack();
        }
    }

    public boolean goBack() {
        boolean canGoBack = true;

        // 如果当前页面为起始页，不再返回
        if (checkCurrentPageIsStartPage()) {
            canGoBack = false;
        }
        return canGoBack;
    }

    private boolean checkCurrentPageIsStartPage() {
        String currentUrl = getCurrentWebViewUrl();
        if (MyTextUtils.isAllNotEmpty(mStartUrl, currentUrl)) {
            Uri startUri = Uri.parse(mStartUrl);
            Uri currentUri = Uri.parse(currentUrl);

            String startPath = startUri.getPath();
            String currentPath = currentUri.getPath();

            return startPath != null && startPath.equals(currentPath);
        } else {
            return false;
        }
    }

    /**
     * 获取当前页面的地址
     *
     * @return
     */
    public String getCurrentWebViewUrl() {
        return webview.getUrl();
    }

    @Override
    public void finish() {
        if (!collectStatic) {
            Intent intent = new Intent();
            intent.putExtra("postion", postion);
            intent.putExtra("isChange", true);
            setResult(1002, intent);
        }
        super.finish();
    }
}
