package com.xiaoaitouch.mom.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;

/**
 * 广告图片显示
 *
 * @author huxin
 * @data: 2016/1/13 15:31
 * @version: V1.0
 */
public class AdvAdapter extends PagerAdapter {

    /**
     * 装ImageView数组
     */
    private ImageView[] mImageViews;


    public AdvAdapter(ImageView[] mImageView) {
        this.mImageViews = mImageView;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        ((ViewPager) container).removeView((ImageView) object);
    }

    /**
     * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
     */
    @Override
    public Object instantiateItem(View container, int position) {
        try {
            ((ViewPager) container).addView(mImageViews[position
                    % mImageViews.length], 0);
        } catch (Exception e) {
        }
        return mImageViews[position % mImageViews.length];
    }
}
