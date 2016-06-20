package com.xiaoaitouch.mom.app;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: B超单解读详情
 * User: huxin
 * Date: 2016/3/4
 * Time: 10:26
 * FIXME
 */
public class BUnscrambleDetailsActivity extends BaseActivity {
    @Bind(R.id.b_unscramble_details_title_tv)
    TextView bUnscrambleDetailsTitleTv;
    @Bind(R.id.b_unscramble_details_content_tv)
    TextView bUnscrambleDetailsContentTv;

    private String title = "";
    private String content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_unscramble_details_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        title = getIntent().getStringExtra("title");
        content=getIntent().getStringExtra("content");

        setHeader(title);
        bUnscrambleDetailsTitleTv.setText(title);
        bUnscrambleDetailsContentTv.setText(Html.fromHtml(content));
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }
}
