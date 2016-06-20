package com.xiaoaitouch.mom.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.BUnscrambleAdapter;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.BcModule;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.sqlite.BabayGrowTables;
import com.xiaoaitouch.mom.util.ButtomDialog;
import com.xiaoaitouch.mom.util.StringUtils;

import java.util.List;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import butterknife.OnItemSelected;

/**
 * Desc: B超单解读
 * User: huxin
 * Date: 2016/3/3
 * Time: 11:02
 * FIXME
 */
public class BUnscrambleActivity extends BaseActivity {
    @Bind(R.id.b_unscramble_pregnancy_week_tv)
    TextView bUnscramblePregnancyWeekTv;
    @Bind(R.id.b_unscramble_data_list_lv)
    ListView bUnscrambleDataListLv;

    private BUnscrambleAdapter bUnscrambleAdapter;
    private BabayGrowTables babayGrowTables;
    private List<BcModule> moduleList;
    private UserModule userModule;
    private String mDueTime = "";//怀孕的日期
    private String mCurrentDate = "";
    private int mCurrentWeek = 1;//当前周

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_unscramble_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        setHeader("B超单解读");
        setUserData();
        bUnscrambleAdapter = new BUnscrambleAdapter();
        bUnscrambleDataListLv.setAdapter(bUnscrambleAdapter);
        babayGrowTables = new BabayGrowTables(mActivity);
        if (mCurrentWeek <= 6) {
            moduleList = babayGrowTables.getWeekBcData(6);
            bUnscramblePregnancyWeekTv.setText("6");
        } else {
            bUnscramblePregnancyWeekTv.setText(mCurrentWeek + "");
            moduleList = babayGrowTables.getWeekBcData(mCurrentWeek);
        }
        if (moduleList != null && moduleList.size() >= 1) {
            bUnscrambleAdapter.setData(moduleList);
        }
    }

    private void setUserData() {
        userModule = MyApplication.instance.getUserModule();
        mCurrentDate = StringUtils.getCurrentTimeSs();
        if (userModule != null) {
            if (!TextUtils.isEmpty(userModule.getLastMensesTime())
                    && !userModule.getLastMensesTime().equals("0")) {// 末次月经
                setGestationData(userModule.getLastMensesTime());
            } else if (!TextUtils.isEmpty(userModule.getDueTime())
                    && !userModule.getDueTime().equals("0")) {// 预产期
                String[] mDueTime = StringUtils.getStringFromDate(
                        Long.valueOf(userModule.getDueTime())).split("-");
                setGestationData(StringUtils.getDateFromStr(StringUtils
                        .getAddDate(mDueTime, -280)));
            }
        }
    }

    /**
     * 计算用户怀孕的周数和天数
     *
     * @param time
     */
    private void setGestationData(String time) {
        mDueTime = StringUtils.getStringFromDate(Long.valueOf(time));
        int number = StringUtils.getDateSpace(mDueTime, mCurrentDate) + 1;
        if (number >= 7) {
            mCurrentWeek = number / 7;
            int days = number % 7;
            if (days != 0) {
                mCurrentWeek = mCurrentWeek + 1;
            }
            if (mCurrentWeek >= 39) {
                mCurrentWeek = 39;
            }
        }
    }

    @OnItemClick(R.id.b_unscramble_data_list_lv)
    public void openBUnscrambleDetailsActivity(int postion) {
        BcModule bcModule = (BcModule) bUnscrambleAdapter.getItem(postion);
        Bundle bundle = new Bundle();
        bundle.putString("title", bcModule.getName());
        bundle.putString("content", bcModule.getDetail());
        startIntent(BUnscrambleDetailsActivity.class, bundle);
    }

    @OnClick(R.id.b_unscramble_pregnancy_week_tv)
    public void openDueWeekData() {
        setViewDialog("周期", 6, 34, 1);
    }

    private void setViewDialog(String title, final int initValue, int forValue, int currentIndex) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.health_height_item, null);
        final ButtomDialog buttomDialog = new ButtomDialog(mActivity);
        final AbstractWheel mHeightAbstractWheel = (AbstractWheel) view
                .findViewById(R.id.height_choose_view);
        TextView textViewTitle = (TextView) view.findViewById(R.id.dialog_title_tv);
        textViewTitle.setText(title);

        setAbstractWheelData(initValue, forValue, currentIndex, mHeightAbstractWheel);

        TextView mCompleteTv = (TextView) view.findViewById(R.id.dialog_complete_tv);
        mCompleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int values = mHeightAbstractWheel.getCurrentItem() + initValue;
                bUnscramblePregnancyWeekTv.setText(values + "");
                queryBData(values);
                buttomDialog.cancel();
            }
        });
        buttomDialog.setContentView(view);
        buttomDialog.show();
        WindowManager windowManager = mActivity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = buttomDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        buttomDialog.getWindow().setAttributes(lp);
    }

    private void setAbstractWheelData(int initValue, int forValue,
                                      int currentIndex, AbstractWheel abstractWheel) {
        String[] mStr = new String[forValue];
        for (int i = 0; i < forValue; i++) {
            mStr[i] = String.valueOf(initValue + i);
        }
        ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(
                mActivity, mStr);
        ampmAdapter.setItemResource(R.layout.my_infor_common_wheel_item);
        ampmAdapter.setItemTextResource(R.id.common_wheel_item_text);
        abstractWheel.setViewAdapter(ampmAdapter);
        abstractWheel.setCurrentItem(currentIndex, false);
        abstractWheel.setCyclic(false);
    }

    private void queryBData(int week) {
        moduleList = babayGrowTables.getWeekBcData(week);
        bUnscrambleAdapter.setData(moduleList);
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }
}
