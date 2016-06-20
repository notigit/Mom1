package com.xiaoaitouch.mom.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;
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
import com.xiaoaitouch.mom.util.Utils;

import java.lang.reflect.Type;
import java.util.Calendar;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 输入预产期
 *
 * @author huxin
 * @data: 2016/1/13 17:41
 * @version: V1.0
 */
public class SettingDueInputFragment extends BaseFragment {
    @Bind(R.id.setting_due_input_tv)
    TextView settingDueInputTv;


    private AbstractWheel abstractWheelYear;
    private AbstractWheel abstractWheelMonth;
    private AbstractWheel abstractWheelDay;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private String mEndDueTime;
    private Calendar mCalendar;
    private int mDayIndex = 0;
    private int mSelectYear = 0;
    private int mSelectMonth = 0;
    private int mSelectDay = 0;
    private int indexDay = 0;

    private UserModule userModule;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.setting_due_input_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViewData() {
        userModule = MyApplication.instance.getUserModule();
        if (userModule != null) {
            if (!TextUtils.isEmpty(userModule.getLastMensesTime())
                    && !userModule.getLastMensesTime().equals("0")) {// 末次月经
                String mEndDueTime = StringUtils.getStringFromDate(Long
                        .valueOf(userModule.getLastMensesTime()));

                String[] mDueTimes = mEndDueTime.split("-");
                String mDueTime = StringUtils.getAddDate(mDueTimes, 280);
                settingDueInputTv.setText(mDueTime);
            } else if (!TextUtils.isEmpty(userModule.getDueTime())
                    && !userModule.getDueTime().equals("0")) {// 预产期
                String mDueTime = StringUtils.getStringFromDate(Long
                        .valueOf(userModule.getDueTime()));
                settingDueInputTv.setText(mDueTime);
            }
        }
        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDayIndex = mCalendar.get(Calendar.DATE);
        mDay = StringUtils.chooseTime(mYear, mMonth);
    }

    @OnClick(R.id.setting_due_input_tv)
    public void userInputDue() {
        showDateDialog("预产期", false);
    }

    @OnClick(R.id.setting_due_save_tv)
    public void saveDue() {
        String mDueStrss = settingDueInputTv.getText().toString().trim();
        String mDueStr = "";
        if (!TextUtils.isEmpty(mDueStrss)) {
            mDueStr = StringUtils.getDateFromStr(mDueStrss);
        }
        mEndDueTime = StringUtils.getAddDate(
                mDueStrss.split("-"), -280);
        String mEndDueStr = "";
        if (!TextUtils.isEmpty(mEndDueTime)) {
            mEndDueStr = StringUtils.getDateFromStr(mEndDueTime);
        }
        String[] mStr = {mDueStr, mEndDueStr, userModule.getMensesCircle() + ""};
        submitDueInfo(mStr);
    }

    private void submitDueInfo(String[] str) {
        if (Utils.isNetworkConnected(getActivity())) {
            mBlockDialog.show();
            GsonTokenRequest<MineInfoModule> request = new GsonTokenRequest<MineInfoModule>(
                    com.android.volley.Request.Method.POST, Configs.SERVER_URL
                    + "/user/modify/due",
                    new Response.Listener<JsonResponse<MineInfoModule>>() {

                        @Override
                        public void onResponse(JsonResponse<MineInfoModule> response) {
                            mBlockDialog.cancel();
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
                                    UserTables.updateUser(getActivity(), userModule);
                                    SharedPreferencesUtil.putBoolean(getActivity(), "due_time_is_setting", false);
                                    SharedPreferencesUtil.putBoolean(getActivity(), "is_update_data", true);
                                    getActivity().finish();
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
            HttpApi.getUpdateDueInfo("/user/modify/due", request, str);
        } else {
            showToast("您处于网络断开状态！");
        }
    }

    private void showDateDialog(String title, boolean isflage) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.update_due_item, null);
        final ButtomDialog buttomDialog = new ButtomDialog(getActivity());
        TextView mTitle = (TextView) view.findViewById(R.id.dialog_title_tv);
        mTitle.setText(title);
        abstractWheelYear = (AbstractWheel) view
                .findViewById(R.id.due_yser_choose_view);
        abstractWheelMonth = (AbstractWheel) view
                .findViewById(R.id.due_month_choose_view);
        abstractWheelDay = (AbstractWheel) view
                .findViewById(R.id.due_date_choose_view);

        view.findViewById(R.id.dialog_complete_tv).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        String dueTime = mSelectYear + "-"
                                + getDataValues(mSelectMonth) + "-"
                                + getDataValues(mSelectDay);
                        settingDueInputTv.setText(dueTime);
                        buttomDialog.dismiss();
                    }
                });
        setAbstractWheelYear(mYear - 5, 100, 5, isflage, "年");
        setAbstractWheelMonth(1, 12, mMonth - 1, isflage, "月");

        buttomDialog.setContentView(view);
        buttomDialog.show();
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = buttomDialog.getWindow()
                .getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        buttomDialog.getWindow().setAttributes(lp);
    }

    private String getDataValues(int values) {
        String result = "";
        if (values <= 9) {
            result = "0" + values;
        } else {
            result = String.valueOf(values);
        }
        return result;
    }

    /**
     * 年份
     *
     * @param initValue
     * @param forValue
     * @param currentIndex
     * @param isflage
     * @param content
     */
    private void setAbstractWheelYear(int initValue, int forValue,
                                      final int currentIndex, final boolean isflage, String content) {
        String[] mStr = new String[forValue];
        for (int i = 0; i < forValue; i++) {
            mStr[i] = String.valueOf(initValue + i) + content;
        }
        final ArrayWheelAdapter<String> ampmAdapterYear = new ArrayWheelAdapter<String>(
                getActivity(), mStr);
        ampmAdapterYear.setItemResource(R.layout.common_wheel_items);
        ampmAdapterYear.setItemTextResource(R.id.common_wheel_item_text);
        abstractWheelYear.setViewAdapter(ampmAdapterYear);
        abstractWheelYear.setCurrentItem(currentIndex, false);
        String str = ampmAdapterYear.getItemText(currentIndex) + "";
        str = (String) str.subSequence(0, str.length() - 1);
        mYear = Integer.valueOf(str);
        mSelectYear = mYear;
        abstractWheelYear.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(AbstractWheel wheel, int oldValue,
                                  int newValue) {
                CharSequence charSequence = ampmAdapterYear
                        .getItemText(newValue);
                if (charSequence != null) {
                    String str = charSequence.toString();
                    str = (String) str.subSequence(0, str.length() - 1);
                    if (!TextUtils.isEmpty(str)) {
                        mSelectYear = Integer.valueOf(str);
                        if (isflage) {
                            int minYear = mYear - 2;
                            if (mSelectYear > mYear || mSelectYear <= minYear) {
                                abstractWheelYear.setCurrentItem(currentIndex,
                                        true);
                            } else {
                                int day = StringUtils.chooseTime(mSelectYear,
                                        mSelectMonth);
                                setAbstractWheelDay(1, day, 0, isflage, "日");
                            }
                        } else {
                            int maxYear = mYear + 2;
                            if (mSelectYear < mYear || mSelectYear >= maxYear) {
                                abstractWheelYear.setCurrentItem(currentIndex,
                                        true);
                            } else {
                                int day = StringUtils.chooseTime(mSelectYear,
                                        mSelectMonth);
                                setAbstractWheelDay(1, day, 0, isflage, "日");
                            }
                        }
                    }
                }
            }
        });
        abstractWheelYear.setCyclic(true);
        setAbstractWheelDay(1, mDay, mDayIndex - 1, isflage, "日");
    }

    /**
     * 月份
     *
     * @param initValue
     * @param forValue
     * @param currentIndex
     * @param isflage
     * @param content
     */
    private void setAbstractWheelMonth(int initValue, int forValue,
                                       final int currentIndex, final boolean isflage, String content) {
        String[] mStr = new String[forValue];
        for (int i = 0; i < forValue; i++) {
            mStr[i] = String.valueOf(initValue + i) + content;
        }
        final ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(
                getActivity(), mStr);
        ampmAdapter.setItemResource(R.layout.common_wheel_items);
        ampmAdapter.setItemTextResource(R.id.common_wheel_item_text);
        abstractWheelMonth.setViewAdapter(ampmAdapter);
        abstractWheelMonth.setCurrentItem(currentIndex, false);
        String str = ampmAdapter.getItemText(currentIndex) + "";
        str = (String) str.subSequence(0, str.length() - 1);
        mMonth = Integer.valueOf(str);
        mSelectMonth = mMonth;
        abstractWheelMonth.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(AbstractWheel wheel, int oldValue,
                                  int newValue) {
                CharSequence charSequence = ampmAdapter.getItemText(newValue);
                if (charSequence != null) {
                    String str = charSequence.toString();
                    str = (String) str.subSequence(0, str.length() - 1);
                    if (!TextUtils.isEmpty(str)) {
                        mSelectMonth = Integer.valueOf(str);
                        if (isflage) {
                            if (mSelectYear == mYear) {
                                if (mMonth < mSelectMonth) {
                                    abstractWheelMonth.setCurrentItem(
                                            currentIndex, true);
                                } else {
                                    int day = StringUtils.chooseTime(
                                            mSelectYear, mSelectMonth);
                                    setAbstractWheelDay(1, day, 0, isflage, "日");
                                }
                            } else {
                                int day = StringUtils.chooseTime(mSelectYear,
                                        mSelectMonth);
                                setAbstractWheelDay(1, day, 0, isflage, "日");
                            }
                        } else {
                            if (mSelectYear == mYear) {
                                if (mMonth >= mSelectMonth) {
                                    abstractWheelMonth.setCurrentItem(
                                            currentIndex, true);
                                } else {
                                    int day = StringUtils.chooseTime(
                                            mSelectYear, mSelectMonth);
                                    setAbstractWheelDay(1, day, 0, isflage, "日");
                                }
                            } else {
                                int day = StringUtils.chooseTime(mSelectYear,
                                        mSelectMonth);
                                setAbstractWheelDay(1, day, 0, isflage, "日");
                            }
                        }
                    }
                }
            }
        });
        abstractWheelMonth.setCyclic(true);
    }

    /**
     * 显示这个月的天数
     *
     * @param initValue
     * @param forValue
     * @param currentIndex
     * @param isflage
     * @param content
     */
    private void setAbstractWheelDay(int initValue, int forValue,
                                     int currentIndex, final boolean isflage, String content) {
        String[] mStr = new String[forValue];
        for (int i = 0; i < forValue; i++) {
            if (mDayIndex == (initValue + i)) {
                indexDay = i;
            }
            mStr[i] = String.valueOf(initValue + i) + content;
        }
        final ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(
                getActivity(), mStr);
        ampmAdapter.setItemResource(R.layout.common_wheel_items);
        ampmAdapter.setItemTextResource(R.id.common_wheel_item_text);
        abstractWheelDay.setViewAdapter(ampmAdapter);
        abstractWheelDay.setCurrentItem(currentIndex, false);
        String str = ampmAdapter.getItemText(currentIndex) + "";
        String mDayStr = (String) str.subSequence(0, str.length() - 1);
        mSelectDay = Integer.valueOf(mDayStr);
        abstractWheelDay.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(AbstractWheel wheel, int oldValue,
                                  int newValue) {
                CharSequence charSequence = ampmAdapter.getItemText(newValue);
                if (charSequence != null) {
                    String str = charSequence.toString();
                    String mDayStr = (String) str.subSequence(0,
                            str.length() - 1);
                    mSelectDay = Integer.valueOf(mDayStr);
                    if (isflage) {
                        if (mSelectYear == mYear) {
                            if (mSelectMonth == mMonth) {
                                if (mSelectDay > mDayIndex) {
                                    abstractWheelDay.setCurrentItem(indexDay,
                                            true);
                                }
                            }
                        }
                    } else {
                        if (mSelectYear == mYear) {
                            if (mSelectMonth == mMonth) {
                                if (mSelectDay < mDayIndex) {
                                    abstractWheelDay.setCurrentItem(indexDay,
                                            true);
                                }
                            }
                        }
                    }
                }
            }
        });
        abstractWheelDay.setCyclic(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
