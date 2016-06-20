package com.xiaoaitouch.mom.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.CollectListAdapter;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.module.CollectListBean;
import com.xiaoaitouch.mom.module.CollectListModule;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.view.swipemenulistview.SwipeMenu;
import com.xiaoaitouch.mom.view.swipemenulistview.SwipeMenuCreator;
import com.xiaoaitouch.mom.view.swipemenulistview.SwipeMenuItem;
import com.xiaoaitouch.mom.view.swipemenulistview.SwipeMenuListView;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 收藏列表
 *
 * @author huxin
 * @data: 2016/1/20 16:33
 * @version: V1.0
 */
public class CollectListActivity extends BaseActivity {
    @Bind(R.id.my_collection_list_view)
    SwipeMenuListView menuListView;

    private CollectListAdapter collectListAdapter;
    private int positions = 0;
    private List<CollectListModule> moduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_list_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }

    private void initViewData() {
        setHeader("收藏");
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                openItem.setBackground(R.color.app_top_color);
                openItem.setWidth(dp2px(90));
                openItem.setTitle("删除");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        };
        menuListView.setMenuCreator(creator);
        menuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        CollectListModule collectListModule = (CollectListModule) collectListAdapter.getItem(position);
                        if (collectListModule != null) {
                            positions = position;
                            handler.obtainMessage(2, collectListModule.getUrl()).sendToTarget();
                        }
                        break;
                }
            }
        });
        getCollectList();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public void getCollectList() {
        if (Utils.isNetworkConnected(mContext)) {
            GsonTokenRequest<CollectListBean> request = new GsonTokenRequest<CollectListBean>(
                    Request.Method.POST,
                    Configs.SERVER_URL + "/v3/getCollections",
                    new Response.Listener<JsonResponse<CollectListBean>>() {

                        @Override
                        public void onResponse(
                                JsonResponse<CollectListBean> response) {
                            switch (response.state) {
                                case Configs.UN_USE:
                                    showToast("版本过低请升级新版本");
                                    break;
                                case Configs.FAIL:
                                    showToast(response.msg);
                                    break;
                                case Configs.SUCCESS:
                                    handler.obtainMessage(1, response.data.getList()).sendToTarget();
                                    break;
                            }
                        }
                    }, null) {

                @Override
                public Type getType() {
                    Type type = new TypeToken<JsonResponse<CollectListBean>>() {
                    }.getType();
                    return type;
                }
            };
            HttpApi.getCollectList("/v3/getCollections", request, null);
        } else {
            showToast("您处于网络断开状态！");
        }
    }

    public void collectAddress(String url) {
        if (Utils.isNetworkConnected(mContext)) {
            StringRequest request = new StringRequest(Request.Method.POST,
                    Configs.SERVER_URL + "/v3/collection",
                    new Response.Listener<String>() {

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
            HttpApi.getCollectAddress("/v3/collection", request, url);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    collectListAdapter = new CollectListAdapter();
                    moduleList = (List<CollectListModule>) msg.obj;
                    collectListAdapter.setData(moduleList);
                    menuListView.setAdapter(collectListAdapter);
                    break;
                case 2:
                    collectAddress((String) msg.obj);
                    break;
                case 3:
                    moduleList.remove(positions);
                    collectListAdapter.setData(moduleList);
                    break;
            }
        }
    };

    @OnItemClick(R.id.my_collection_list_view)
    public void listViewOnItemClick(int postion) {
        CollectListModule collectListModule = (CollectListModule) collectListAdapter.getItem(postion);
        if (collectListModule != null) {
            Bundle bundle = new Bundle();
            bundle.putString("title", collectListModule.getTitle());
            bundle.putString("url", collectListModule.getUrl());
            bundle.putInt("postion", postion);
            bundle.putInt("type", 2);
            startActivityForResult(WebViewActivity.class, bundle, 1001);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1002) {
            boolean isChange = data.getBooleanExtra("isChange", false);
            if (isChange) {
                int postion = data.getIntExtra("postion", 0);
                moduleList.remove(postion);
                collectListAdapter.setData(moduleList);
            }
        }
    }


}
