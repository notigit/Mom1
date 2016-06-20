package com.xiaoaitouch.mom.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.view.BabyDetailsWindow;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * baby的每周变化
 *
 * @author huxin
 * @data: 2016/1/16 14:14
 * @version: V1.0
 */
public class BabyFragment extends BaseFragment {
    public static final String QUERY_WEEK_DATA = "week";
    @Bind(R.id.baby_week_icon_iv)
    ImageView babyWeekIconIv;



    private int mWeek = 1;
    private int mResID = 0;
    private BabyDetailsWindow babyDetailsWindow;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.baby_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViewData() {
        final Bundle args = getArguments();
        if (args != null) {
            this.mWeek = args.getInt(QUERY_WEEK_DATA, 1);
            mResID = getResources().getIdentifier("week" + mWeek, "drawable",
                    getActivity().getApplicationInfo().packageName);
            babyWeekIconIv.setImageResource(mResID);
        }
    }

    public static BabyFragment newInstance(int week) {
        final BabyFragment fragment = new BabyFragment();
        final Bundle args = new Bundle();
        args.putInt(QUERY_WEEK_DATA, week);
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.baby_week_icon_iv)
    public void showBabyInfor() {
        showMoreWindow(babyWeekIconIv);
    }

    public void setWeek(int week) {
        this.mWeek = week;
    }

    private void showMoreWindow(View view) {
        if (null == babyDetailsWindow) {
            babyDetailsWindow = new BabyDetailsWindow(getActivity());
            babyDetailsWindow.init();
        }
        babyDetailsWindow.showMoreWindow(view, 100, mWeek);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
