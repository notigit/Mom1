package com.xiaoaitouch.mom.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.CanOrNotAdapter;
import com.xiaoaitouch.mom.app.CanOrNotListActivity;
import com.xiaoaitouch.mom.view.MyGridView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

/**
 * Desc: 能不能吃
 * User: huxin
 * Date: 2016/2/24
 * Time: 11:18
 * FIXME
 */
public class CanOrNotEatFragment extends BaseFragment {

    @Bind(R.id.can_or_not_eat_gridview)
    MyGridView canOrNotEatGridview;

    private CanOrNotAdapter canOrNotAdapter;
    private String[] canOrNotEat;
    private int[] canOrNotEatImage = {R.drawable.can_or_not_eat_image1, R.drawable.can_or_not_eat_image2, R.drawable.can_or_not_eat_image3,
            R.drawable.can_or_not_eat_image4, R.drawable.can_or_not_eat_image5, R.drawable.can_or_not_eat_image6,
            R.drawable.can_or_not_eat_image7, R.drawable.can_or_not_eat_image8, R.drawable.can_or_not_eat_image9
            , R.drawable.can_or_not_eat_image10, R.drawable.can_or_not_eat_image11, R.drawable.can_or_not_eat_image12};

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.can_or_not_eat_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViewData() {
        canOrNotEat = getActivity().getResources().getStringArray(R.array.can_or_not_eat);
        canOrNotAdapter = new CanOrNotAdapter(canOrNotEat, canOrNotEatImage);
        canOrNotEatGridview.setAdapter(canOrNotAdapter);
    }

    @OnItemClick(R.id.can_or_not_eat_gridview)
    public void OnItemClickGridView(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("title", canOrNotEat[position]);
        bundle.putInt("type", 10 + position);
        startIntent(CanOrNotListActivity.class, bundle);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
