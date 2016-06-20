package com.xiaoaitouch.mom.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * <禁用ViewPager的滑动事件>
 * @author  huxin
 * @data:  2016/1/7 14:50
 * @version:  V1.0
 */
public final class ViewPagerNoTouch extends ViewPager {

    public ViewPagerNoTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPagerNoTouch(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }


}
