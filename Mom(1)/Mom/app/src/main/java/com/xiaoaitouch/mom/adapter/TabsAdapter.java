package com.xiaoaitouch.mom.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * <FragmentStatePagerAdapter>
 *
 * @author huxin
 * @data: 2016/1/7 15:11
 * @version: V1.0
 */
public class TabsAdapter extends FragmentStatePagerAdapter {
    private final Context mContext;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    public TabsAdapter(Activity activity, FragmentManager fm) {
        super(fm);
        mContext = activity;
    }

    static final class TabInfo {
        private final Class<?> clazz;
        private final Bundle args;
        private Fragment fragment;

        TabInfo(Class<?> clazz, Bundle args) {
            this.clazz = clazz;
            this.args = args;
        }
    }


    public void addTab(Class<?> clss, Bundle args) {
        TabInfo info = new TabInfo(clss, args);
        mTabs.add(info);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }


    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        if (info.fragment == null) {
            info.fragment = Fragment.instantiate(mContext, info.clazz.getName(), info.args);
        }
        return info.fragment;
    }


}