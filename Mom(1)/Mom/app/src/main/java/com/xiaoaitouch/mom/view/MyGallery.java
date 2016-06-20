package com.xiaoaitouch.mom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;
import android.widget.GridView;

/**
 * scrollview嵌套Gallery 显示不全的问题
 * @author  huxin 
 * @data:  2016/1/9 14:39
 * @version:  V1.0 
 */
public class MyGallery extends Gallery {

    private boolean mCanScroll = true;

    private float mDownX;

    /**父类宽度 ViewPager**/
    private int mParentWhidth;

    public MyGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGallery(Context context) {
        super(context);
    }

    public MyGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void setParentWhidth(int parentWhidth){
        mParentWhidth = parentWhidth;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = ev.getX();
        }

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            int scrollx = getScrollX();
            if ((scrollx == 0 && mDownX - ev.getX() <= -10)
                    || (getChildAt(0).getMeasuredWidth() <= (scrollx + mParentWhidth) && mDownX - ev.getX() >= 10)) {
                mCanScroll = false;
            }

        }

        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            mCanScroll = true;
        }

        if (this.mCanScroll) {
            // Disallow ViewPager to intercept touch events.
            getParent().requestDisallowInterceptTouchEvent(true);
            return super.onTouchEvent(ev);
        } else {
            // Allow ViewPager to intercept touch events.
            getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        }
    }

}
