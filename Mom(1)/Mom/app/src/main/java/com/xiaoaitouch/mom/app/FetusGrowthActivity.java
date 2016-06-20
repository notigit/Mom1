package com.xiaoaitouch.mom.app;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.module.BabayGrow;
import com.xiaoaitouch.mom.module.BabayWeight;
import com.xiaoaitouch.mom.sqlite.BabayGrowTables;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 胎儿发育评测结果
 * User: huxin
 * Date: 2016/3/2
 * Time: 17:31
 * FIXME
 */
public class FetusGrowthActivity extends BaseActivity {
    @Bind(R.id.fetus_growth_tv)
    TextView fetusGrowthTv;
    @Bind(R.id.fetus_growth_baby_weight_tv)
    TextView fetusGrowthBabyWeightTv;
    @Bind(R.id.fetus_growth_range_values_tv)
    TextView fetusGrowthRangeValuesTv;
    @Bind(R.id.fetus_growth_bpd_tv)
    TextView fetusGrowthBpdTv;
    @Bind(R.id.fetus_growth_abdominal_tv)
    TextView fetusGrowthAbdominalTv;
    @Bind(R.id.fetus_growth_bone_tv)
    TextView fetusGrowthBoneTv;
    private BabayGrowTables babayGrowTables;
    private BabayGrow babayGrow = null;

    private int isSex = 1;
    private float babyWeight = 0.0f;
    private int week = 0;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private BabayWeight babayWeightData = null;
    private int colors = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fetus_growth_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        setHeader("胎儿健康评测");
        isSex = getIntent().getIntExtra("isSex", 1);
        babyWeight = getIntent().getFloatExtra("babyWeight", 0.0f);
        week = getIntent().getIntExtra("week", 26);

        babayGrowTables = new BabayGrowTables(mActivity);
        babayGrow = babayGrowTables.getWeekDate(week);
        if (babayGrow != null) {
            fetusGrowthBpdTv.setText(babayGrow.getSdj());
            fetusGrowthAbdominalTv.setText(babayGrow.getFw());
            fetusGrowthBoneTv.setText(babayGrow.getGgc());
        }

        babayWeightData = babayGrowTables.getBabayWeightWeekDate(week, isSex);
        if (babayWeightData != null) {
            if (babyWeight < babayWeightData.getTen()) {
                fetusGrowthTv.setText("过轻");
                fetusGrowthTv.setBackgroundResource(R.drawable.draw_circle_yellow_bg);
                colors = getResources().getColor(R.color.draw_circle_yellow_bg);
            } else if (babyWeight > babayWeightData.getNinety()) {
                fetusGrowthTv.setText("过重");
                fetusGrowthTv.setBackgroundResource(R.drawable.draw_circle_red_bg);
                colors = getResources().getColor(R.color.draw_circle_red_bg);
            } else {
                fetusGrowthTv.setText("正常");
                fetusGrowthTv.setBackgroundResource(R.drawable.draw_circle_blue_bg);
                colors = getResources().getColor(R.color.draw_circle_blue_bg);
            }
            String weightStr = "本次体重参考范围：<font color = \"" + colors + "\">" + babayWeightData.getTen() + "g" + "-" + babayWeightData.getNinety() + "g"
                    + "</font>";
            fetusGrowthRangeValuesTv.setText(Html.fromHtml(weightStr));
        }
        String babyWeightStr = "胎儿体重为：<font color = \"" + colors + "\">" + decimalFormat.format(babyWeight) + "g"
                + "</font>";
        fetusGrowthBabyWeightTv.setText(Html.fromHtml(babyWeightStr));
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }
}
