package com.xiaoaitouch.mom.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.app.FetusGrowthActivity;
import com.xiaoaitouch.mom.util.ButtomDialog;

import java.text.DecimalFormat;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 胎儿发育评测
 * User: huxin
 * Date: 2016/3/1
 * Time: 15:55
 * FIXME
 */
public class FetusGrowthFragment extends BaseFragment {
    @Bind(R.id.fetus_growth_cycle_tv)
    TextView fetusGrowthCycleTv;
    @Bind(R.id.fetus_growth_sex_tv)
    TextView fetusGrowthSexTv;
    @Bind(R.id.fetus_growth_bpd_tv)
    EditText fetusGrowthBpdTv;
    @Bind(R.id.fetus_growth_abdominal_tv)
    EditText fetusGrowthAbdominalTv;
    @Bind(R.id.fetus_growth_bone_tv)
    EditText fetusGrowthBoneTv;


    private int isSex = 1;
    private String bpd = "";
    private String abdominal = "";
    private String bone = "";
    private float babyWeight = 0.0f;

    private float bpdValues = 0.0f;
    private float abdominalValues = 0.0f;
    private float boneValues = 0.0f;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fetus_growth_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViewData() {
    }


    @OnClick(R.id.fetus_growth_sure_tv)
    public void openPregnantHealthActivity() {
        bpd = fetusGrowthBpdTv.getText().toString().trim();
        abdominal = fetusGrowthAbdominalTv.getText().toString().trim();
        bone = fetusGrowthBoneTv.getText().toString().trim();
        if (!TextUtils.isEmpty(bpd)) {
            bpdValues = Float.valueOf(bpd);
        }
        if (!TextUtils.isEmpty(abdominal)) {
            abdominalValues = Float.valueOf(abdominal);
        }
        if (!TextUtils.isEmpty(bone)) {
            boneValues = Float.valueOf(bone);
        }

        babyWeight = (float) (1.07 * bpdValues * bpdValues * bpdValues + 0.3 * abdominalValues * abdominalValues * boneValues);

        int week = Integer.valueOf(fetusGrowthCycleTv.getText().toString().trim());
        Bundle bundle = new Bundle();
        bundle.putInt("isSex", isSex);
        bundle.putInt("week", week);
        bundle.putFloat("babyWeight", babyWeight);
        startIntent(FetusGrowthActivity.class, bundle);
    }

    @OnClick(R.id.fetus_growth_cycle_tv)
    public void setMyCycle() {
        setViewDialog(1, "周期", 26, 15, 5);
    }

    @OnClick(R.id.fetus_growth_sex_tv)
    public void setMySex() {
        setViewDialog(2, "性别", 26, 2, 0);
    }

    private void setViewDialog(final int type, String title, final int initValue, int forValue, int currentIndex) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.health_height_item, null);
        final ButtomDialog buttomDialog = new ButtomDialog(getActivity());
        final AbstractWheel mHeightAbstractWheel = (AbstractWheel) view
                .findViewById(R.id.height_choose_view);
        TextView textViewTitle = (TextView) view.findViewById(R.id.dialog_title_tv);
        textViewTitle.setText(title);

        setAbstractWheelData(type, initValue, forValue, currentIndex, mHeightAbstractWheel);

        TextView mCompleteTv = (TextView) view.findViewById(R.id.dialog_complete_tv);
        mCompleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == 1) {
                    int cycleWeek = mHeightAbstractWheel.getCurrentItem() + initValue;
                    fetusGrowthCycleTv.setText(cycleWeek + "");
                } else {
                    if (mHeightAbstractWheel.getCurrentItem() == 0) {
                        fetusGrowthSexTv.setText("男");
                        isSex = 1;
                    } else {
                        fetusGrowthSexTv.setText("女");
                        isSex = 2;
                    }
                }
                buttomDialog.cancel();
            }
        });
        buttomDialog.setContentView(view);
        buttomDialog.show();
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = buttomDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        buttomDialog.getWindow().setAttributes(lp);
    }

    private void setAbstractWheelData(int type, int initValue, int forValue,
                                      int currentIndex, AbstractWheel abstractWheel) {
        String[] mStr = new String[forValue];
        if (type == 1) {
            for (int i = 0; i < forValue; i++) {
                mStr[i] = String.valueOf(initValue + i);
            }
        } else {
            mStr[0] = "男";
            mStr[1] = "女";
        }
        ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(
                getActivity(), mStr);
        ampmAdapter.setItemResource(R.layout.my_infor_common_wheel_item);
        ampmAdapter.setItemTextResource(R.id.common_wheel_item_text);
        abstractWheel.setViewAdapter(ampmAdapter);
        abstractWheel.setCurrentItem(currentIndex, false);
        abstractWheel.setCyclic(false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
