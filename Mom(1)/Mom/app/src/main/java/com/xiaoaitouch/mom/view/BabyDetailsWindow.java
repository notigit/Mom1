package com.xiaoaitouch.mom.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.sqlite.GestationTables;
import com.xiaoaitouch.mom.util.FastBlur;

/**
 * 宝贝详情
 */
public class BabyDetailsWindow extends PopupWindow {

    private String TAG = BabyDetailsWindow.class.getSimpleName();
    Activity mContext;
    private int mWidth;
    private int mHeight;
    private int statusBarHeight;
    private Bitmap mBitmap = null;
    private Bitmap overlay = null;
    private int mWeek = 1;
    private int mResID = 0;
    private ImageView babyWeekIconIv;
    private TextView babyInforTv;
    private ImageView imgBg;
    private  GestationTables.GestationInfo gestationInfo;

    public BabyDetailsWindow(Activity context) {
        mContext = context;
    }

    public void init() {
        Rect frame = new Rect();
        mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusBarHeight = frame.top;
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;

        setWidth(mWidth);
        setHeight(mHeight);
    }

    private Bitmap blur() {
        if (null != overlay) {
            return overlay;
        }
        long startMs = System.currentTimeMillis();

        View view = mContext.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        mBitmap = view.getDrawingCache();

        float scaleFactor = 20;//图片缩放比例；
        float radius = 20;//模糊程度
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        overlay = Bitmap.createBitmap((int) (width / scaleFactor), (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(mBitmap, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        return overlay;
    }

    public void showMoreWindow(View anchor, int bottomMargin, int week) {
        mWeek = week;
        final RelativeLayout layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.baby_details_window, null);
        setContentView(layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        babyWeekIconIv = (ImageView) layout.findViewById(R.id.baby_week_icon_iv);
        babyInforTv = (TextView) layout.findViewById(R.id.baby_infor_tv);
        imgBg = (ImageView) layout.findViewById(R.id.baby_imgBg);
        handler.sendEmptyMessage(1);

        setOutsideTouchable(true);
        setFocusable(true);
        showAtLocation(anchor, Gravity.BOTTOM, 0, statusBarHeight);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mResID = mContext.getResources().getIdentifier("week" + mWeek, "drawable",
                            mContext.getApplicationInfo().packageName);
                    GestationTables gestationTables = new GestationTables(mContext);
                    gestationInfo = gestationTables.getWeekDate(mWeek);
                    babyWeekIconIv.setImageResource(mResID);
                    Animation animationBabyIcon = AnimationUtils.loadAnimation(mContext,
                            R.anim.baby_icon);
                    Animation imgAnim = AnimationUtils.loadAnimation(mContext,
                            R.anim.baby_icon_01);
                    babyWeekIconIv.setAnimation(animationBabyIcon);
                    imgBg.startAnimation(imgAnim);
                    imgAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            imgBg.setVisibility(View.VISIBLE);
                            setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blur()));
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    animationBabyIcon.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation arg0) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation arg0) {
                        }

                        @Override
                        public void onAnimationEnd(Animation arg0) {

                            if (gestationInfo != null && gestationInfo.getTips() != null) {
                                babyInforTv.setText(gestationInfo.getTips().replace("=", "\n"));
                            }
                        }
                    });
                    break;
            }
        }
    };

}
