package com.xiaoaitouch.mom.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;
import com.xiaoaitouch.event.EventBus;
import com.xiaoaitouch.event.bean.MainEvent;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.MineInfoModule;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;
import com.xiaoaitouch.mom.sqlite.UserTables;
import com.xiaoaitouch.mom.util.ButtomDialog;
import com.xiaoaitouch.mom.util.SharedPreferencesUtil;
import com.xiaoaitouch.mom.util.StringUtils;

import java.lang.reflect.Type;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的资料
 *
 * @author huxin
 * @data: 2016/1/9 18:13
 * @version: V1.0
 */
public class MyInformationActivity extends BaseActivity {
    @Bind(R.id.my_information_due_tv)
    TextView myInformationDueTv;
    @Bind(R.id.my_information_hight_tv)
    TextView myInformationHightTv;
    @Bind(R.id.my_information_age_tv)
    TextView myInformationAgeTv;


    private UserModule userModule;
    private String mHeight;
    private String mAge;
    private boolean isflage = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_information_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        setHeader("资料");
        userModule = MyApplication.instance.getUserModule();
        if (userModule != null) {
            if (!TextUtils.isEmpty(userModule.getLastMensesTime())
                    && !userModule.getLastMensesTime().equals("0")) {// 末次月经
                String mEndDueTime = StringUtils.getStringFromDate(Long
                        .valueOf(userModule.getLastMensesTime()));

                String[] mDueTimes = mEndDueTime.split("-");
                String mDueTime = StringUtils.getAddDate(mDueTimes, 280);
                myInformationDueTv.setText(mDueTime);

            } else if (!TextUtils.isEmpty(userModule.getDueTime())
                    && !userModule.getDueTime().equals("0")) {// 预产期
                String mDueTime = StringUtils.getStringFromDate(Long
                        .valueOf(userModule.getDueTime()));
                myInformationDueTv.setText(mDueTime);
            }
            mHeight = userModule.getHeight();
            mAge = userModule.getAge();
            myInformationAgeTv.setText(mAge + "岁");
            myInformationHightTv.setText(mHeight + "CM");
        }
    }

    @OnClick(R.id.top_title_back_tv)
    public void back() {
        if (isflage) {
            String height = myInformationHightTv.getText().toString().trim();
            String age = myInformationAgeTv.getText().toString().trim();

            String mHeight = height.substring(0, height.length() - 2);
            String mAge = age.substring(0, age.length() - 1);

            String[] mStr = {mHeight, mAge};
            submitDueInfo(mStr);
        } else {
            onBackBtnClick();
        }
    }

    @OnClick(R.id.my_information_due_ray)
    public void openSettingDue() {
        startActivityForResult(SettingDueActivity.class, null, 1003);
    }

    @OnClick(R.id.my_information_hight_ray)
    public void setMyHeight() {
        final int mHeight = 1;
        View view = LayoutInflater.from(mActivity).inflate(R.layout.update_height_item, null);
        final ButtomDialog buttomDialog = new ButtomDialog(mActivity);
        final AbstractWheel mHeightAbstractWheel = (AbstractWheel) view
                .findViewById(R.id.height_choose_view);
        final TextView mHeightLeftTv = (TextView) view
                .findViewById(R.id.height_left_tv);
        // 选择身高
        mHeightLeftTv.setText("1");
        setAbstractWheelData(mHeight, 99, 59, 2, mHeightAbstractWheel, mHeightLeftTv);

        TextView mCompleteTv = (TextView) view.findViewById(R.id.dialog_complete_tv);
        mCompleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String height = mHeightLeftTv.getText().toString().trim() + (mHeightAbstractWheel.getCurrentItem() + mHeight);
                if (!userModule.getHeight().equals(height)) {
                    isflage = true;
                } else {
                    isflage = false;
                }
                myInformationHightTv.setText(height + "CM");
                buttomDialog.cancel();
            }
        });
        buttomDialog.setContentView(view);
        buttomDialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = buttomDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        buttomDialog.getWindow().setAttributes(lp);
    }

    @OnClick(R.id.my_information_age_ray)
    public void setMyAge() {
        final int mAge = 16;
        View view = LayoutInflater.from(mActivity).inflate(R.layout.update_age_item, null);
        final ButtomDialog buttomDialog = new ButtomDialog(mActivity);
        final AbstractWheel mAgeAbstractWheel = (AbstractWheel) view
                .findViewById(R.id.age_choose_view);
        setAbstractWheelData(mAge, 35, 9, 1, mAgeAbstractWheel, null);
        TextView mCompleteTv = (TextView) view.findViewById(R.id.dialog_complete_tv);
        mCompleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String age = (mAgeAbstractWheel.getCurrentItem() + mAge) + "";
                if (!userModule.getAge().equals(age)) {
                    isflage = true;
                } else {
                    isflage = false;
                }
                myInformationAgeTv.setText(age + "岁");
                buttomDialog.cancel();
            }
        });
        buttomDialog.setContentView(view);
        buttomDialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = buttomDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        buttomDialog.getWindow().setAttributes(lp);
    }

    private void setAbstractWheelData(int initValue, int forValue,
                                      int currentIndex, final int type,
                                      final AbstractWheel abstractWheel,
                                      final TextView mHeightLeftTv) {
        String[] mStr = new String[forValue];
        for (int i = 0; i < forValue; i++) {
            if (type == 2) {
                mStr[i] = String.valueOf(i + 1);
            } else {
                mStr[i] = String.valueOf(initValue + i);
            }
        }
        final ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(
                mActivity, mStr);
        ampmAdapter.setItemResource(R.layout.my_infor_common_wheel_item);
        ampmAdapter.setItemTextResource(R.id.common_wheel_item_text);
        abstractWheel.setViewAdapter(ampmAdapter);
        abstractWheel.setCurrentItem(currentIndex, false);
        abstractWheel.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(AbstractWheel wheel, int oldValue,
                                  int newValue) {
                if (type == 2) {
                    String valueUp = ampmAdapter.getItemText(oldValue)
                            .toString();
                    String valueCurrent = ampmAdapter.getItemText(newValue)
                            .toString();
                    if (!TextUtils.isEmpty(valueUp)
                            && !TextUtils.isEmpty(valueCurrent)) {
                        if (Integer.valueOf(valueUp) == 99
                                && Integer.valueOf(valueCurrent) == 1) {
                            mHeightLeftTv.setText("2");
                        } else if (Integer.valueOf(valueUp) == 1
                                && Integer.valueOf(valueCurrent) == 99) {
                            mHeightLeftTv.setText("1");
                        }
                    }
                }
            }
        });
        abstractWheel.setCyclic(true);
    }

    private void submitDueInfo(String[] str) {
        GsonTokenRequest<MineInfoModule> request = new GsonTokenRequest<MineInfoModule>(
                com.android.volley.Request.Method.POST, Configs.SERVER_URL
                + "/v2/user/modify/info",
                new Response.Listener<JsonResponse<MineInfoModule>>() {

                    @Override
                    public void onResponse(JsonResponse<MineInfoModule> response) {
                        switch (response.state) {
                            case Configs.UN_USE:
                                showToast("版本过低请升级新版本");
                                break;
                            case Configs.FAIL:
                                showToast(response.msg);
                                break;
                            case Configs.SUCCESS:
                                MineInfoModule mineInfo = response.data;
                                userModule = mineInfo.getUserInfo();
                                MyApplication.instance.setUserModule(userModule);
                                UserTables.updateUser(mActivity, userModule);
                                onBackBtnClick();
                                break;
                        }
                    }

                }, null) {

            @Override
            public Type getType() {
                Type type = new TypeToken<JsonResponse<MineInfoModule>>() {
                }.getType();

                return type;
            }
        };
        HttpApi.getUpdateAgeAndHeight("/v2/user/modify/info", request, str);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1003) {
            initViewData();
            boolean isFlage = SharedPreferencesUtil.getBoolean(mContext, "is_update_data", false);
            if (isFlage) {
                EventBus.getDefault().post(new MainEvent());
                SharedPreferencesUtil.putBoolean(mContext, "is_update_data", false);
            }
        }
    }
}
