package com.xiaoaitouch.mom.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.DazzleAdapter;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.DazzleListBean;
import com.xiaoaitouch.mom.module.DazzleListModule;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;
import com.xiaoaitouch.mom.util.Utils;
import com.xiaoaitouch.mom.view.RoundedImageView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 炫腹足迹
 * User: huxin
 * Date: 2016/2/29
 * Time: 16:38
 * FIXME
 */
public class DazzleActivity extends BaseActivity {
    @Bind(R.id.dazzle_content_lv)
    PullToRefreshListView dazzleContentLv;
    @Bind(R.id.top_right_iv)
    ImageView topRightIv;

    private ListView mListView;
    private DazzleAdapter dazzleAdapter;
    private int mPage = 0;
    private RoundedImageView headerImageIv;
    private List<DazzleListBean> dazzleListBeanList = new ArrayList<DazzleListBean>();
    private View headView;
    private UserModule userModule;
    private RelativeLayout headerNoDataTipsTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dazzle_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        setHeader("炫腹足迹");
        setRightIcon(R.drawable.dazzle_right_icon);
        userModule = MyApplication.instance.getUserModule();
        mListView = dazzleContentLv.getRefreshableView();
        headView = LayoutInflater.from(mContext).inflate(
                R.layout.dazzle_head_view_item, null);
        headerImageIv = (RoundedImageView) headView.findViewById(R.id.user_head_image_iv);
        headerNoDataTipsTv = (RelativeLayout) headView.findViewById(R.id.dazzle_list_top_tips_ray);
        if (userModule != null) {
            // 下载用户头像
            if (!TextUtils.isEmpty(userModule.getHeadPic())) {
                if (userModule.getHeadPic().contains("http")) {
                    ImageLoader.getInstance().displayImage(
                            userModule.getHeadPic(),
                            headerImageIv,
                            DisplayImageOptionsUtils
                                    .getUserHeadImageOptions());
                } else {
                    ImageLoader.getInstance().displayImage(
                            Configs.IMAGE_URL + userModule.getHeadPic(),
                            headerImageIv,
                            DisplayImageOptionsUtils
                                    .getUserHeadImageOptions());
                }
            }
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        mListView.setDividerHeight(0);
        dazzleAdapter = new DazzleAdapter(mContext, screenWidth);
        mListView.setAdapter(dazzleAdapter);
        mListView.addHeaderView(headView);
        dazzleContentLv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData();
            }
        });
        loadData();
    }

    private void playHeartbeatAnimation() {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f));
        animationSet.addAnimation(new AlphaAnimation(1.0f, 0.4f));

        animationSet.setDuration(200);
        animationSet.setInterpolator(new AccelerateInterpolator());
        animationSet.setFillAfter(true);

        animationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(new ScaleAnimation(0.5f, 1.0f, 0.5f,
                        1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f));
                animationSet.addAnimation(new AlphaAnimation(0.4f, 1.0f));

                animationSet.setDuration(600);
                animationSet.setInterpolator(new DecelerateInterpolator());
                animationSet.setFillAfter(false);

                topRightIv.startAnimation(animationSet);
            }
        });
        // 实现心跳的View
        topRightIv.startAnimation(animationSet);
    }

    private void loadData() {
        if (Utils.isNetworkConnected(mContext)) {
            GsonTokenRequest<DazzleListModule> request = new GsonTokenRequest<DazzleListModule>(
                    Request.Method.POST,
                    Configs.SERVER_URL + "/v3/fcList",
                    new Response.Listener<JsonResponse<DazzleListModule>>() {

                        @Override
                        public void onResponse(
                                JsonResponse<DazzleListModule> response) {
                            handler.obtainMessage(response.state, response).sendToTarget();
                        }
                    }, null) {

                @Override
                public Type getType() {
                    Type type = new TypeToken<JsonResponse<DazzleListModule>>() {
                    }.getType();
                    return type;
                }
            };
            HttpApi.getDazzleListData("/v3/fcList", request, mPage);
        } else {
            showToast("您处于网络断开状态！");
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dazzleContentLv.onRefreshComplete();
            super.handleMessage(msg);
            switch (msg.what) {
                case Configs.UN_USE:
                    showToast("版本过低请升级新版本");
                    break;
                case Configs.FAIL:
                    JsonResponse<DazzleListModule> response = (JsonResponse<DazzleListModule>) msg.obj;
                    showToast(response.msg);
                    break;
                case Configs.SUCCESS:
                    JsonResponse<DazzleListModule> responses = (JsonResponse<DazzleListModule>) msg.obj;
                    DazzleListModule dazzleListModule = responses.data;
                    if (dazzleListModule != null) {
                        List<DazzleListBean> list = dazzleListModule.getList();
                        if (list != null && list.size() >= 1) {
                            headerNoDataTipsTv.setVisibility(View.GONE);
                            handler.removeMessages(2);
                            for (int i = 0; i < list.size(); i++) {
                                dazzleListBeanList.add(list.get(i));
                            }
                            if (list != null && list.size() < 10) {
                                showToast("数据加载完毕");
                                dazzleContentLv.setPullToRefreshOverScrollEnabled(false);
                                dazzleContentLv.setMode(PullToRefreshBase.Mode.DISABLED);
                            }
                            mPage++;
                            dazzleAdapter.setData(list);
                        } else {
                            if (mPage >= 1) {
                                showToast("数据加载完毕");
                            }
                            dazzleContentLv.setPullToRefreshOverScrollEnabled(false);
                            dazzleContentLv.setMode(PullToRefreshBase.Mode.DISABLED);
                            if (mPage == 0) {
                                headerNoDataTipsTv.setVisibility(View.VISIBLE);
                                handler.sendEmptyMessage(2);
                            }
                        }
                    } else {
                        if (mPage == 0) {
                            dazzleContentLv.setPullToRefreshOverScrollEnabled(false);
                            dazzleContentLv.setMode(PullToRefreshBase.Mode.DISABLED);
                            headerNoDataTipsTv.setVisibility(View.VISIBLE);
                            handler.sendEmptyMessage(2);
                        }
                    }
                    break;
                case 2:
                    playHeartbeatAnimation();
                    handler.sendEmptyMessageDelayed(2, 1000);
                    break;
            }
        }
    };


    @OnClick(R.id.top_right_iv)
    public void onRightActivity() {
        startActivityForResult(DazzleSendActivity.class, null, 1008);
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1004) {
            mPage = 0;
            dazzleContentLv.setPullToRefreshOverScrollEnabled(true);
            dazzleContentLv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
            loadData();
        }
    }
}
