package com.xiaoaitouch.mom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoaitouch.mom.R;


/**
 * 自定义绘制虚线
 *
 * @author huxin
 * @data: 2016/1/9 10:29
 * @version: V1.0
 */
public class DashedLine extends View {
    private Paint p;
    private int width;
    private int height;
    private int dash;

    public DashedLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DashedLine(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.FILL);
        p.setColor(context.getResources().getColor(R.color.textView_color));
        final float scale = context.getResources().getDisplayMetrics().density;
        dash = (int) (4 * scale + 0.5f);
        this.setAlpha(0.4f);
    }

    public void setPaintColor(int color){
        p.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width > 10) {
            for (int i = 0; i < width; i += dash) {
                canvas.drawLine(i, 0, i += dash, 0, p);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        p.setStrokeWidth(height);
        this.postInvalidate();
    }


}
