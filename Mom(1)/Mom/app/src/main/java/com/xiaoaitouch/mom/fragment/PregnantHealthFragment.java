package com.xiaoaitouch.mom.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.app.PregnantHealthActivity;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.util.ButtomDialog;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 孕妇健康评测
 * User: huxin
 * Date: 2016/3/1
 * Time: 15:53
 * FIXME
 */
public class PregnantHealthFragment extends BaseFragment {

    @Bind(R.id.pregnant_health_height_tv)
    TextView pregnantHealthHeightTv;
    @Bind(R.id.pregnant_health_weight_tv)
    TextView pregnantHealthWeightTv;
    private int height = 160;
    private int weight = 50;

    private UserModule userModule = null;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.pregant_health_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViewData() {
        userModule = MyApplication.instance.getUserModule();
        if (userModule != null) {
            weight = (int) userModule.getWeight();
            pregnantHealthWeightTv.setText(weight + "");
        } else {
            String heights = userModule.getHeight();
            if (TextUtils.isEmpty(heights) && heights.length() >= 1) {
                height = Integer.valueOf(heights);
            }
        }
        pregnantHealthHeightTv.setText(height + "");
    }

    @OnClick(R.id.pregnant_health_height_tv)
    public void setMyHeight() {
        setViewDialog(1, "身高", 130, 70, 30);
    }

    @OnClick(R.id.pregnant_health_weight_tv)
    public void setMyWeight() {
        setViewDialog(2, "体重", 35, 115, 15);
    }

    private void setViewDialog(final int type, String title, final int initValue, int forValue, int currentIndex) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.health_height_item, null);
        final ButtomDialog buttomDialog = new ButtomDialog(getActivity());
        final AbstractWheel mHeightAbstractWheel = (AbstractWheel) view
                .findViewById(R.id.height_choose_view);
        TextView textViewTitle = (TextView) view.findViewById(R.id.dialog_title_tv);
        textViewTitle.setText(title);

        setAbstractWheelData(initValue, forValue, currentIndex, mHeightAbstractWheel);

        TextView mCompleteTv = (TextView) view.findViewById(R.id.dialog_complete_tv);
        mCompleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == 1) {
                    height = mHeightAbstractWheel.getCurrentItem() + initValue;
                    pregnantHealthHeightTv.setText(height + "");
                } else {
                    weight = mHeightAbstractWheel.getCurrentItem() + initValue;
                    pregnantHealthWeightTv.setText(weight + "");
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

    private void setAbstractWheelData(int initValue, int forValue,
                                      int currentIndex, AbstractWheel abstractWheel) {
        String[] mStr = new String[forValue];
        for (int i = 0; i < forValue; i++) {
            mStr[i] = String.valueOf(initValue + i);
        }
        ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(
                getActivity(), mStr);
        ampmAdapter.setItemResource(R.layout.my_infor_common_wheel_item);
        ampmAdapter.setItemTextResource(R.id.common_wheel_item_text);
        abstractWheel.setViewAdapter(ampmAdapter);
        abstractWheel.setCurrentItem(currentIndex, false);
        abstractWheel.setCyclic(false);
    }

    @OnClick(R.id.pregnant_health_sure_tv)
    public void openPregnantHealthActivity() {
        String heights = 1 + "." + (height % 100);
        Bundle bundle = new Bundle();
        bundle.putInt("weight", weight);
        bundle.putFloat("height", Float.valueOf(heights));
        startIntent(PregnantHealthActivity.class, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
