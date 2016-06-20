package com.xiaoaitouch.mom.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.CanOrNotListAdapter;
import com.xiaoaitouch.mom.adapter.MyFragmentPagerAdapter;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.fragment.CanOrNotEatFragment;
import com.xiaoaitouch.mom.fragment.CanOrNotWorkFragment;
import com.xiaoaitouch.mom.module.CanOrNotListBean;
import com.xiaoaitouch.mom.module.CanOrNotListModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.util.SearchDialog;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.util.ViewUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 能不能
 * User: huxin
 * Date: 2016/2/24
 * Time: 10:43
 * FIXME
 */
public class CanOrNotActivity extends BaseFragmentActivity {
    @Bind(R.id.can_or_not_eat_tv)
    TextView canOrNotEatTv;
    @Bind(R.id.can_or_not_work_tv)
    TextView canOrNotWorkTv;
    @Bind(R.id.can_or_not_line_view)
    TextView canOrNotLineView;
    @Bind(R.id.can_or_not_viewpager)
    ViewPager canOrNotViewpager;
    @Bind(R.id.can_or_not_search_hint_tv)
    TextView canOrNotSearchHintTv;


    private int currIndex = 0;//当前页卡编号
    private Map<Integer, TextView> mTextViewMap = new HashMap<Integer, TextView>();
    private ArrayList<Fragment> fragmentList;
    private CanOrNotEatFragment canOrNotEatFragment;
    private CanOrNotWorkFragment canOrNotWorkFragment;
    private SearchDialog searchDialog;
    private EditText searchContentEt;
    private PullToRefreshListView canOrNotListView;
    private int mPage = 0;
    private String[] mStr = new String[3];
    private CanOrNotListAdapter canOrNotListAdapter;
    private List<CanOrNotListBean> canOrNotListBeanList = new ArrayList<CanOrNotListBean>();
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.can_or_not_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }

    private void initViewData() {
        setHeader("能不能");
        searchDialog = new SearchDialog(mContext);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        // 得到显示屏宽度
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        // 1/4屏幕宽度
        int tabLineLength = metrics.widthPixels / 2;

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) canOrNotLineView.getLayoutParams();
        lp.width = tabLineLength;
        canOrNotLineView.setLayoutParams(lp);
        //tab的点击事件
        canOrNotEatTv.setOnClickListener(new TxListener(0));
        canOrNotWorkTv.setOnClickListener(new TxListener(1));

        mTextViewMap.put(0, canOrNotEatTv);
        mTextViewMap.put(1, canOrNotWorkTv);

        //ViewPager添加fragment
        fragmentList = new ArrayList<Fragment>();
        canOrNotEatFragment = new CanOrNotEatFragment();
        canOrNotWorkFragment = new CanOrNotWorkFragment();

        fragmentList.add(canOrNotEatFragment);
        fragmentList.add(canOrNotWorkFragment);


        //给ViewPager设置适配器
        canOrNotViewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        canOrNotViewpager.setOffscreenPageLimit(2);
        canOrNotViewpager.setCurrentItem(0);
        canOrNotViewpager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
        setSearchHintTv(0);

    }

    public class TxListener implements View.OnClickListener {
        private int index = 0;

        public TxListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            canOrNotViewpager.setCurrentItem(index, true);
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            RelativeLayout.LayoutParams ll = (RelativeLayout.LayoutParams) canOrNotLineView
                    .getLayoutParams();

            if (currIndex == arg0) {
                ll.leftMargin = (int) (currIndex * canOrNotLineView.getWidth() + arg1
                        * canOrNotLineView.getWidth());
            } else if (currIndex > arg0) {
                ll.leftMargin = (int) (currIndex * canOrNotLineView.getWidth() - (1 - arg1) * canOrNotLineView.getWidth());
            }
            canOrNotLineView.setLayoutParams(ll);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageSelected(int arg0) {
            currIndex = arg0;
            setViewChange(arg0);
            setSearchHintTv(arg0);
        }
    }

    private void setSearchHintTv(int currentItem) {
        if (currentItem == 1) {
            canOrNotSearchHintTv.setHint("输入行为，看能不能做");
        } else {
            canOrNotSearchHintTv.setHint("输入食物名，看能不能吃");
        }
    }

    private void setViewChange(int index) {
        for (int i = 0; i < mTextViewMap.size(); i++) {
            if (index == i) {
                int allChildAt = mTextViewMap.size();
                for (int j = 0; j < allChildAt; j++) {
                    mTextViewMap.get(index).setPressed(true);
                    mTextViewMap.get(index).setFocusable(true);
                    mTextViewMap.get(index).setSelected(true);
                    mTextViewMap.get(index).setTextSize(18f);
                }
            } else {
                int allChildAts = mTextViewMap.size();
                for (int j = 0; j < allChildAts; j++) {
                    mTextViewMap.get(i).setPressed(false);
                    mTextViewMap.get(i).setFocusable(false);
                    mTextViewMap.get(i).setSelected(false);
                    mTextViewMap.get(i).setTextSize(16f);
                }
            }
        }
    }

    @OnClick(R.id.can_or_not_search_ray)
    public void SearchListDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View rootView = inflater
                .inflate(R.layout.search_list_view_item, null);
        searchContentEt = (EditText) rootView.findViewById(R.id.can_or_not_search_hint_et);
        searchContentEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    searchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        searchContentEt.addTextChangedListener(mTextWatcher);
        rootView.findViewById(R.id.can_or_not_cancle_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtils.hideSoftInput(mActivity, searchContentEt.getWindowToken());
                searchDialog.cancel();
            }
        });

        canOrNotListView = (PullToRefreshListView) rootView.findViewById(R.id.search_list_view);
        mListView = canOrNotListView.getRefreshableView();
        canOrNotListAdapter = new CanOrNotListAdapter(mContext);
        mListView.setAdapter(canOrNotListAdapter);
        mListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                ViewUtils.hideSoftInput(mActivity, searchContentEt.getWindowToken());
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
        if (currIndex == 0) {
            searchContentEt.setHint("输入食物名，看能不能吃");
        } else {
            searchContentEt.setHint("输入行为，看能不能做");
        }
        searchDialog.setContentView(rootView);
        searchDialog.show();

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = searchDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        lp.height = (int) (display.getHeight()); //设置宽度
        searchDialog.getWindow().setAttributes(lp);
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
            mPage = 0;
            String content = searchContentEt.getText().toString().trim();
            if (!TextUtils.isEmpty(content) && content.length() >= 1) {
                mStr[1] = content;
                loadInfo();
            } else {
                canOrNotListBeanList.clear();
                canOrNotListAdapter.notifyDataSetChanged();
            }
        }
    };

    private void loadInfo() {
        if (currIndex == 0) {
            mStr[0] = "-2";
        } else {
            mStr[0] = "-1";
        }
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
}
