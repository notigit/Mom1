package com.xiaoaitouch.mom.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.CanOrNotListAdapter;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.module.CanOrNotListBean;
import com.xiaoaitouch.mom.module.CanOrNotListModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.util.ViewUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 能不能列表
 * User: huxin
 * Date: 2016/2/25
 * Time: 14:13
 * FIXME
 */
public class CanOrNotListActivity extends BaseActivity {
    @Bind(R.id.can_or_not_list_view)
    PullToRefreshListView canOrNotListView;
    @Bind(R.id.can_or_not_search_hint_et)
    EditText canOrNotSearchHintTv;
    @Bind(R.id.can_or_not_cancle_tv)
    TextView canOrNotCancleTv;


    private String title;
    private CanOrNotListAdapter canOrNotListAdapter;
    private int type = 0;
    private ListView mListView;
    private int mPage = 0;
    private String[] mStr = new String[3];
    private List<CanOrNotListBean> canOrNotListBeanList = new ArrayList<CanOrNotListBean>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.can_or_not_list_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        title = getIntent().getStringExtra("title");
        type = getIntent().getIntExtra("type", 0);
        setHeader(title);
        mStr[0] = type + "";
        mStr[1] = "";
        if (type <= 9) {
            canOrNotSearchHintTv.setHint("输入行为，看能不能做");
        } else {
            canOrNotSearchHintTv.setHint("输入食物名，看能不能吃");
        }
        canOrNotSearchHintTv.setFocusable(false);
        canOrNotSearchHintTv.addTextChangedListener(mTextWatcher);

        mListView = canOrNotListView.getRefreshableView();
        canOrNotListAdapter = new CanOrNotListAdapter(mContext);
        mListView.setAdapter(canOrNotListAdapter);
        mListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                ViewUtils.hideSoftInput(mActivity, canOrNotSearchHintTv.getWindowToken());
                return false;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CanOrNotListBean canOrNotListBean = (CanOrNotListBean) canOrNotListAdapter.getItem(i - 1);
                Bundle bundle = new Bundle();
                bundle.putLong("id", canOrNotListBean.getId());
                bundle.putInt("type", canOrNotListBean.getType());
                bundle.putString("title", canOrNotListBean.getName());
                startIntent(CanOrNotDetailsActivity.class, bundle);
            }
        });
        canOrNotListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(
                        getApplicationContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                canOrNotListView.setMode(PullToRefreshBase.Mode.BOTH);
                mPage = 0;
                loadInfo();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadInfo();
            }
        });
        loadInfo();
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        ViewUtils.hideSoftInput(mActivity, canOrNotSearchHintTv.getWindowToken());
        onBackBtnClick();
    }

    private void loadInfo() {
        mStr[2] = mPage + "";
        if (Utils.isNetworkConnected(mContext)) {
            GsonTokenRequest<CanOrNotListModule> request = new GsonTokenRequest<CanOrNotListModule>(
                    Request.Method.POST,
                    Configs.SERVER_URL + "/v3/canList",
                    new Response.Listener<JsonResponse<CanOrNotListModule>>() {

                        @Override
                        public void onResponse(
                                JsonResponse<CanOrNotListModule> response) {
                            handler.obtainMessage(response.state, response).sendToTarget();
                        }
                    }, null) {

                @Override
                public Type getType() {
                    Type type = new TypeToken<JsonResponse<CanOrNotListModule>>() {
                    }.getType();
                    return type;
                }
            };
            HttpApi.getCanOrNotList("/v3/canList", request, mStr);
        } else {
            showToast("您处于网络断开状态！");
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            canOrNotListView.onRefreshComplete();
            switch (msg.what) {
                case Configs.UN_USE:
                    showToast("版本过低请升级新版本");
                    break;
                case Configs.FAIL:
                    JsonResponse<CanOrNotListModule> response = (JsonResponse<CanOrNotListModule>) msg.obj;
                    showToast(response.msg);
                    break;
                case Configs.SUCCESS:
                    JsonResponse<CanOrNotListModule> responses = (JsonResponse<CanOrNotListModule>) msg.obj;
                    CanOrNotListModule canOrNotListModule = responses.data;
                    List<CanOrNotListBean> list = canOrNotListModule.getList();
                    if (list != null && list.size() < 20) {
                        if (mPage >= 1) {
                            showToast("数据加载完毕");
                        }
                        canOrNotListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    } else {
                        canOrNotListView.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                    if (list == null || list.size() < 1) {
                        if (mPage == 0) {
                            showToast("暂无数据");
                        }
                    } else {
                        if (mPage == 0) {
                            canOrNotListBeanList.clear();
                        }
                        for (int i = 0; i < list.size(); i++) {
                            canOrNotListBeanList.add(list.get(i));
                        }
                        mPage++;
                        canOrNotListAdapter.setData(canOrNotListBeanList);
                    }
                    break;
            }
        }
    };

    @OnClick(R.id.can_or_not_search_hint_et)
    public void searchListDialog() {
        canOrNotSearchHintTv.setFocusable(true);
        canOrNotSearchHintTv.setFocusableInTouchMode(true);
        canOrNotSearchHintTv.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) canOrNotSearchHintTv
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(canOrNotSearchHintTv, 0);
    }

    @OnClick(R.id.can_or_not_cancle_tv)
    public void cancelSearchContent() {
        canOrNotSearchHintTv.setText("");
        canOrNotSearchHintTv.setFocusable(false);
        ViewUtils.hideSoftInput(mActivity, canOrNotSearchHintTv.getWindowToken());
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String content = canOrNotSearchHintTv.getText().toString().trim();
            if (TextUtils.isEmpty(content) || content.length() < 1) {
                canOrNotCancleTv.setVisibility(View.GONE);
                content = "";
            } else {
                canOrNotCancleTv.setVisibility(View.VISIBLE);
            }
            mPage = 0;
            mStr[1] = content;
            loadInfo();
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    ViewUtils.hideSoftInput(mActivity, canOrNotSearchHintTv.getWindowToken());
                    onBackBtnClick();
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
