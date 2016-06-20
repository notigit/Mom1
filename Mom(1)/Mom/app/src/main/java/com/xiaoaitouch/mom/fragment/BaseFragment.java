package com.xiaoaitouch.mom.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xiaoaitouch.mom.util.BlockDialog;
import com.xiaoaitouch.mom.util.Utils;

/**
 * Created by Administrator on 2016/1/6.
 */
public abstract class BaseFragment extends Fragment {

    protected BlockDialog mBlockDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBlockDialog = new BlockDialog(getActivity());
        initViewData();
    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup container);

    protected abstract void initViewData();

    /**
     * 启动下一个Activity
     *
     * @param activity
     */
    protected void startIntent(Class<?> activity) {
        Intent mIntent = new Intent(getActivity(), activity);
        getActivity().startActivity(mIntent);
    }

    protected void startIntent(Class<?> activity, Bundle bundle) {
        Intent mIntent = new Intent(getActivity(), activity);
        mIntent.putExtras(bundle);
        BaseFragment.this.startActivity(mIntent);
    }

    protected void startActivityForResult(Class<?> activity, Bundle bundle, int requestCode) {
        Intent mIntent = new Intent(getActivity(), activity);
        if (bundle != null) {
            mIntent.putExtras(bundle);
        }
        getActivity().startActivityForResult(mIntent, requestCode);
    }

    protected void showToast(CharSequence text, int duration) {
        Utils.showToast(text, duration);
    }

    protected void showToast(CharSequence text) {
        Utils.showToast(text, Toast.LENGTH_SHORT);
    }

    protected void showToast(int resId, int duration) {
        showToast(getString(resId), duration);
    }

}
