package com.xiaoaitouch.mom.app;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 孕妇健康评测结果
 * User: huxin
 * Date: 2016/3/2
 * Time: 11:51
 * FIXME
 */
public class PregnantHealthActivity extends BaseActivity {
    @Bind(R.id.pregnant_health_tv)
    TextView pregnantHealthTv;
    @Bind(R.id.pregnant_health_bmi_tv)
    TextView pregnantHealthBmiTv;
    @Bind(R.id.pregnant_health_weight_tv)
    TextView pregnantHealthWeightTv;

    private float bmi = 0f;
    private int colors = 0;
    private int weight = 0;
    private float height = 0f;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pregant_health_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        setHeader("孕妇健康评测");
        weight = getIntent().getIntExtra("weight", 0);
        height = getIntent().getFloatExtra("height", 0);
        bmi = weight / (height * height);
        if (bmi < 20) {
            pregnantHealthTv.setText("偏瘦");
            pregnantHealthTv.setBackgroundResource(R.drawable.draw_circle_yellow_bg);
            colors = getResources().getColor(R.color.draw_circle_yellow_bg);
        } else if (bmi >= 20 && bmi < 24) {
            pregnantHealthTv.setText("正常");
            pregnantHealthTv.setBackgroundResource(R.drawable.draw_circle_blue_bg);
            colors = getResources().getColor(R.color.draw_circle_blue_bg);
        } else if (bmi >= 24 && bmi < 26.4) {
            pregnantHealthTv.setText("略胖");
            pregnantHealthTv.setBackgroundResource(R.drawable.draw_circle_yellow_bg);
            colors = getResources().getColor(R.color.draw_circle_yellow_bg);
        } else if (bmi >= 26.4) {
            pregnantHealthTv.setText("太胖");
            pregnantHealthTv.setBackgroundResource(R.drawable.draw_circle_red_bg);
            colors = getResources().getColor(R.color.draw_circle_red_bg);
        }
        String bmiStr = "您的体重指数BMI为：<font color = \"" + colors + "\">" + decimalFormat.format(bmi)
                + "</font>";
        float idealWeight = (float) ((bmi * 0.88 + 6.65) * height * height) - weight;
        String weightStr = "整个孕期的理想增中为：<font color = \"" + colors + "\">" + decimalFormat.format(idealWeight) + "公斤" + "</font>";
        pregnantHealthBmiTv.setText(Html.fromHtml(bmiStr));
        pregnantHealthWeightTv.setText(Html.fromHtml(weightStr));
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }

}
