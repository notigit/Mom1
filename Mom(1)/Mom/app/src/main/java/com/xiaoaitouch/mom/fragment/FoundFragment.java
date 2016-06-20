package com.xiaoaitouch.mom.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.app.WebViewActivity;
import com.xiaoaitouch.mom.app.WelfareActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <发现>
 *
 * @author huxin
 * @data: 2016/1/7 10:44
 * @version: V1.0
 */
public class FoundFragment extends BaseFragment {

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.found_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViewData() {

    }

    @OnClick(R.id.found_shopping_ray)
    public void openShopping() {
        Bundle bundle = new Bundle();
        bundle.putString("title", "小爱商城");
        bundle.putString("url", "http://www.xiaoaibuy.com");
        bundle.putInt("type", 1);
        startIntent(WebViewActivity.class, bundle);
    }

    @OnClick(R.id.found_fuli_ray)
    public void openFuli() {
        startIntent(WelfareActivity.class);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
