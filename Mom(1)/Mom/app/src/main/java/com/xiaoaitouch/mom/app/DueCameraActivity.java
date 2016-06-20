package com.xiaoaitouch.mom.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.DueCameraAdapter;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.ADueCameraModule;
import com.xiaoaitouch.mom.module.CJListModule;
import com.xiaoaitouch.mom.module.DueCameraModule;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.util.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 产检相册
 *
 * @data: 2016/1/9 20:18
 * @version: V1.0
 */
public class DueCameraActivity extends BaseActivity {
    @Bind(R.id.no_due_camera_lay)
    LinearLayout noDueCameraLay;
    @Bind(R.id.due_camera_listview)
    ListView dueCameraListview;


    private DueCameraAdapter mAdapter;
    private UserModule userModule;
    private List<DueCameraModule> mDueCameras = null;
    private String[] dueDate = null;
    private int jValues = 1;
    private int group = 1;
    public static String mStr = "";
    public static List<ADueCameraModule> mCameraBeansList = new ArrayList<ADueCameraModule>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.due_camera_activity);
        ButterKnife.bind(this);
        initViewData();
    }


    private void initViewData() {
        setHeader("产检相册");
        setRightIcon(R.drawable.photo_icon);
        userModule = MyApplication.instance.getUserModule();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels / 4;
        mAdapter = new DueCameraAdapter(screenWidth, mActivity);
        getDueCameraData();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Configs.SUCCESS:
                    dueCameraListview.setVisibility(View.VISIBLE);
                    noDueCameraLay.setVisibility(View.GONE);
                    mDueCameras = (List<DueCameraModule>) msg.obj;
                    dueCameraDataOrder();
                    break;
                case -1:
                    dueCameraListview.setVisibility(View.GONE);
                    noDueCameraLay.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * @return void 返回类型
     * @throws
     * @Title: dueCameraDataOrder
     * @Description: 对数据进行排序
     */
    private void dueCameraDataOrder() {
        mCameraBeansList.clear();
        mAdapter.clear();
        mStr = "";
        jValues = 1;
        group = 1;
        if (mDueCameras != null && mDueCameras.size() >= 1) {
            dueDate = new String[mDueCameras.size()];
            DueCameraModule dCamera = mDueCameras.get(0);
            dueDate[0] = dCamera.getDate();
            for (int i = 1; i < mDueCameras.size(); i++) {
                boolean isflage = true;
                dCamera = mDueCameras.get(i);
                String date = dCamera.getDate();
                for (int j = 0; j < dueDate.length; j++) {
                    String mDueDate = dueDate[j] == null ? "" : dueDate[j];
                    if (!TextUtils.isEmpty(mDueDate)) {
                        if (mDueDate.equals(date)) {
                            isflage = true;
                        } else {
                            isflage = false;
                        }
                    }
                }
                if (!isflage) {
                    dueDate[jValues] = date;
                    jValues++;
                }
            }
        }
        if (mDueCameras != null && mDueCameras.size() >= 1) {
            for (int j = 0; j < dueDate.length; j++) {
                List<DueCameraModule> mList = new ArrayList<DueCameraModule>();
                for (int i = 0; i < mDueCameras.size(); i++) {
                    DueCameraModule dCamera = mDueCameras.get(i);
                    String date = dCamera.getDate();
                    if (!TextUtils.isEmpty(dueDate[j])
                            && dueDate[j].equals(date)) {
                        mList.add(dCamera);
                    }
                }
                addDueCamera(mList);
            }
        }
        mAdapter.addAll(mCameraBeansList);
        dueCameraListview.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void addDueCamera(List<DueCameraModule> dueCameras) {
        if (dueCameras != null && dueCameras.size() >= 1) {
            mStr = mStr + dueCameras.size() + "-";
            ADueCameraModule mCameraBean = new ADueCameraModule();
            mCameraBean.setCreateTime(dueCameras.get(0).getCreateTime());
            mCameraBean.setGroup(group);
            mCameraBean.setDueCameras(dueCameras);
            mCameraBeansList.add(mCameraBean);
            group++;
        }
    }

    private void getDueCameraData() {
        if (Utils.isNetworkConnected(mContext)) {
            mBlockDialog.show();
            GsonTokenRequest<CJListModule> request = new GsonTokenRequest<CJListModule>(
                    com.android.volley.Request.Method.POST, Configs.SERVER_URL
                    + "/v2/cj/list",
                    new Response.Listener<JsonResponse<CJListModule>>() {

                        @Override
                        public void onResponse(JsonResponse<CJListModule> response) {
                            mBlockDialog.cancel();
                            switch (response.state) {
                                case Configs.UN_USE:
                                    showToast("版本过低请升级新版本");
                                    break;
                                case Configs.FAIL:
                                    showToast(response.msg);
                                    break;
                                case Configs.SUCCESS:
                                    CJListModule cjListBean = response.data;
                                    if (cjListBean != null
                                            && cjListBean.getCjList() != null
                                            && cjListBean.getCjList().size() >= 1) {
                                        mHandler.obtainMessage(Configs.SUCCESS,
                                                cjListBean.getCjList())
                                                .sendToTarget();
                                    } else {
                                        mHandler.sendEmptyMessage(-1);
                                    }
                                    break;
                            }
                        }

                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    mBlockDialog.cancel();
                    showToast("网络数据加载失败");
                }
            }) {

                @Override
                public Type getType() {
                    Type type = new TypeToken<JsonResponse<CJListModule>>() {
                    }.getType();

                    return type;
                }
            };
            HttpApi.getDueCamera("/v2/cj/list", request, null);
        } else {
            showToast("您处于网络断开状态！");
        }
    }

    @OnClick(R.id.top_title_back_tv)
    public void back() {
        onBackBtnClick();
    }

    @OnClick(R.id.top_right_iv)
    public void openCamera() {
        Intent mIntent = new Intent(mActivity, CameraActivity.class);
        startActivityForResult(mIntent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1004) {
            getDueCameraData();
        } else if (resultCode == 1005) {
            getDueCameraData();
        }
    }
}
