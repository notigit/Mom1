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
 * Desc: 能不能做
 * User: huxin
 * Date: 2016/2/24
 * Time: 11:18
 * FIXME
 */
public class CanOrNotWorkFragment extends BaseFragment {

    @Bind(R.id.can_or_not_work_gridview)
    MyGridView canOrNotWorkGridview;

    private CanOrNotAdapter canOrNotAdapter;
    private String[] canOrNotWork;
    private int[] canOrNotWorkImage = {R.drawable.can_or_not_work_image1, R.drawable.can_or_not_work_image2, R.drawable.can_or_not_work_image3,
            R.drawable.can_or_not_work_image4, R.drawable.can_or_not_work_image5, R.drawable.can_or_not_work_image6,
            R.drawable.can_or_not_work_image7, R.drawable.can_or_not_work_image8, R.drawable.can_or_not_work_image9};

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.can_or_not_work_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initViewData() {
        canOrNotWork = getActivity().getResources().getStringArray(R.array.can_or_not_work);
        canOrNotAdapter = new CanOrNotAdapter(canOrNotWork, canOrNotWorkImage);
        canOrNotWorkGridview.setAdapter(canOrNotAdapter);
    }


    @OnItemClick(R.id.can_or_not_work_gridview)
    public void OnItemClickGridView(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("title", canOrNotWork[position]);
        bundle.putInt("type", position + 1);
        startIntent(CanOrNotListActivity.class, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
