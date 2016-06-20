package com.xiaoaitouch.mom.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.app.CollectListActivity;
import com.xiaoaitouch.mom.app.DeviceManageActivity;
import com.xiaoaitouch.mom.app.DueCameraActivity;
import com.xiaoaitouch.mom.app.LoginActivity;
import com.xiaoaitouch.mom.app.MyInformationActivity;
import com.xiaoaitouch.mom.app.SettingActivity;
import com.xiaoaitouch.mom.app.UserInforActivity;
import com.xiaoaitouch.mom.app.WebViewActivity;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;
import com.xiaoaitouch.mom.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <我的>
 *
 * @author huxin
 * @data: 2016/1/7 10:44
 * @version: V1.0
 */
public class PersonalFragment extends BaseFragment {
    @Bind(R.id.my_user_heard_icon)
    ImageView myUserHeardIcon;
    @Bind(R.id.my_user_nick_name_tv)
    TextView myNickNameTv;
    @Bind(R.id.my_collection_ray)
    RelativeLayout myCollectionRay;


    private UserModule userModule;
    private boolean isLogin = false;
    private String mUrl = "";

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.my_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViewData() {
        userModule = MyApplication.instance.getUserModule();
        if (userModule != null) {
            if (!TextUtils.isEmpty(userModule.getUserName())) {
                isLogin = true;
            } else {
                isLogin = false;
            }
            if (!isLogin || TextUtils.isEmpty(userModule.getNeckname())) {
                isLogin = false;
                myNickNameTv.setText("未登录");
            } else {
                myNickNameTv.setText(userModule.getNeckname());
            }
            // 下载用户头像
            if (!TextUtils.isEmpty(userModule.getHeadPic())) {
                if (userModule.getHeadPic().contains("http")) {
                    ImageLoader.getInstance().displayImage(
                            userModule.getHeadPic(),
                            myUserHeardIcon,
                            DisplayImageOptionsUtils
                                    .getUserDisplayImageOptions());
                } else {
                    ImageLoader.getInstance().displayImage(
                            Configs.IMAGE_URL + userModule.getHeadPic(),
                            myUserHeardIcon,
                            DisplayImageOptionsUtils
                                    .getUserDisplayImageOptions());
                }
            }
        } else {
            isLogin = false;
            myNickNameTv.setText("未登录");
        }
        if (TextUtils.isEmpty(mUrl)) {
            getCode();
        }
    }


    @OnClick(R.id.my_messgae_ray)
    public void openUserInfor() {
        if (isLogin) {
            Intent mIntent = new Intent(getActivity(), UserInforActivity.class);
            PersonalFragment.this.startActivityForResult(mIntent, 1002);
        } else {
            Intent mIntent = new Intent(getActivity(), LoginActivity.class);
            PersonalFragment.this.startActivityForResult(mIntent, 1001);
        }
    }

    @OnClick(R.id.my_equipment_ray)
    public void openDeviceManage() {
        startIntent(DeviceManageActivity.class);
    }

    @OnClick(R.id.my_material_ray)
    public void openMaterial() {
        startIntent(MyInformationActivity.class);
    }

    @OnClick(R.id.my_setting_ray)
    public void openSetting() {
        startIntent(SettingActivity.class);
    }

    @OnClick(R.id.my_due_album_ray)
    public void openDueCamera() {
        startIntent(DueCameraActivity.class);
    }

    @OnClick(R.id.my_collection_ray)
    public void openCollection() {
        startIntent(CollectListActivity.class);
    }

    @OnClick(R.id.my_gift_certificate_ray)
    public void openGiftCertificate() {
        Bundle bundle1 = new Bundle();
        bundle1.putString("title", "礼卷");
        bundle1.putString("url", mUrl);
        startIntent(WebViewActivity.class, bundle1);
    }


    private void getCode() {
        if (Utils.isNetworkConnected(getActivity())) {
            StringRequest request = new StringRequest(Request.Method.POST,
                    Configs.SERVER_URL + "/v2/shop", new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    if (!TextUtils.isEmpty(response)) {
                        try {
                            ResultObj result = new ResultObj(response);
                            switch (result.getState()) {
                                case ResultObj.FAIL:
                                    showToast(result.getMessage());
                                    break;
                                case ResultObj.SUCCESS:
                                    JSONObject object = result
                                            .getObjectData();
                                    mUrl = object.getString("url");
                                    break;
                                case ResultObj.UN_USE:
                                    showToast("版本过低请升级新版本");
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {

                }
            });
            HttpApi.getCode("/v2/shop", request, null);
        } else {
            showToast("您处于网络断开状态！");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1001 || requestCode == 1002) {
            initViewData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
