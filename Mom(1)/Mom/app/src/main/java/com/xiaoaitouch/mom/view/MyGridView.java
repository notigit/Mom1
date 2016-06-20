package com.xiaoaitouch.mom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * scrollview嵌套gridview 显示不全的问题
 *
 * @author huxin
 * @data: 2016/1/9 14:39
 * @version: V1.0
 */
public class MyGridView extends GridView {
    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView(Context context) {
        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}  