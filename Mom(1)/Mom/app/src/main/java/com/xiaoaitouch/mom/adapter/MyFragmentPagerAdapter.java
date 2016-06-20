package com.xiaoaitouch.mom.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * <FragmentPagerAdapter>
 *
 * @author huxin
 * @data: 2016/1/6 19:02
 * @version: V1.0
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> list;

    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.list = list;

    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return list.get(arg0);
    }

}