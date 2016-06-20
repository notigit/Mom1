package com.xiaoaitouch.mom.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.AdvAdapter;
import com.xiaoaitouch.mom.adapter.WelcomeAdapter;
import com.xiaoaitouch.mom.util.DialogUtils;
import com.xiaoaitouch.mom.view.SpringIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/1/25.
 */
public class WelcomeActivity extends BaseActivity {
    @Bind(R.id.welcom_viewpager)
    ViewPager mViewPager;
    @Bind(R.id.viewGroup)
    ViewGroup viewGroup;

    /**
     * 图片资源id
     */
    private int[] imgIdArray;
    /**
     * 装点点的ImageView数组
     */
    private ImageView[] tips;

    /**
     * 装ImageView数组
     */
    private ImageView[] mImageViews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        ButterKnife.bind(this);
        initViewData();
        handler.sendEmptyMessage(1);

    }

    private void initViewData() {
        //载入图片资源ID
        imgIdArray = new int[]{R.drawable.welcome_icon1, R.drawable.welcome_icon2, R.drawable.welcome_icon3};
        //将点点加入到ViewGroup中
        tips = new ImageView[imgIdArray.length];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            viewGroup.addView(imageView, layoutParams);
        }
        //将图片装载到数组中
        mImageViews = new ImageView[imgIdArray.length];
        for (int i = 0; i < mImageViews.length; i++) {
            ImageView imageView = new ImageView(this);
            mImageViews[i] = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundResource(imgIdArray[i]);
        }
        //设置Adapter
        mViewPager.setAdapter(new AdvAdapter(mImageViews));
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
        mViewPager.setCurrentItem((mImageViews.length) * 100);
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageSelected(int arg0) {
            setImageBackground(arg0 % mImageViews.length);
        }
    }

    /**
     * 设置选中的tip的背景
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator);
            }
        }
    }


    private void initUmengUpdate() {
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus,
                                         final UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes:
                        if (updateInfo != null) {
                            DialogUtils.appUpdateAlertDialog(mActivity, updateInfo,
                                    "", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            switch (which) {
                                                case DialogUtils.BUTTON1:
                                                    dialog.dismiss();
                                                    startActivity(new Intent(
                                                            Intent.ACTION_VIEW,
                                                            Uri.parse(updateInfo.path)));
                                                    break;
                                            }
                                        }
                                    });
                        }
                        break;
                }
            }
        });
        UmengUpdateAgent.update(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    initUmengUpdate();
                    break;
            }
        }
    };

    @OnClick(R.id.login_tv)
    public void openLoginActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        startIntent(LoginActivity.class, bundle);
        finish();
    }

    @OnClick(R.id.register_tv)
    public void openGuideActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 2);
        startIntent(LoginActivity.class, bundle);
        finish();
    }
}
