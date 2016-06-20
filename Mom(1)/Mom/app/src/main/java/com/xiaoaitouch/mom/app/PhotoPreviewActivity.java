package com.xiaoaitouch.mom.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.fragment.ImageDetailFragment;
import com.xiaoaitouch.mom.view.HackyViewPager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Desc: 照片预览
 * User: huxin
 * Date: 2016/3/11
 * Time: 10:37
 * FIXME
 */
public class PhotoPreviewActivity extends FragmentActivity {
    @Bind(R.id.camera_pager)
    HackyViewPager cameraPager;

    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
    private ImagePagerAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_preview_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        mFragments.add(ImageDetailFragment
                .newInstance(getIntent().getStringExtra("url")));
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(),
                mFragments);
        cameraPager.setAdapter(mAdapter);
    }

    private class ImagePagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> mFragments;
        private FragmentManager fm;

        public ImagePagerAdapter(FragmentManager fm,
                                 ArrayList<Fragment> fragments) {
            super(fm);
            this.fm = fm;
            this.mFragments = fragments;
        }

        @Override
        public int getCount() {
            return mFragments == null ? 0 : mFragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        public void setFragments(ArrayList<Fragment> fragments) {
            if (this.mFragments != null) {
                FragmentTransaction ft = fm.beginTransaction();
                for (Fragment f : this.mFragments) {
                    ft.remove(f);
                }
                ft.commit();
                ft = null;
                fm.executePendingTransactions();
            }
            this.mFragments = fragments;
            notifyDataSetChanged();
        }
    }
}
