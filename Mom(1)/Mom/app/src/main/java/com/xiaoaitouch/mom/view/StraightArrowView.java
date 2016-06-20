package com.xiaoaitouch.mom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * 直线箭头
 *
 * @author huxin
 * @data: 2016/1/8 13:45
 * @version: V1.0
 */
public class StraightArrowView extends View {

    private Paint mPaint;
    private int paintWith = 2;
    private int lineHeight = 0;


    public StraightArrowView(Context context) {
        super(context);
        init();
    }

    public StraightArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StraightArrowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.FILL);//设置填满
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(paintWith);
    }

    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }

    public void setPaintWidth(int with) {
        mPaint.setStrokeWidth(with);
    }

    public void updateLineHeight(int height) {
        this.lineHeight = height;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, 0, 0, lineHeight, mPaint);// 画线
    }
}
