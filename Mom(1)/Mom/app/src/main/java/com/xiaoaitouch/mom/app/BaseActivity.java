package com.xiaoaitouch.mom.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.util.BlockDialog;
import com.xiaoaitouch.mom.util.Utils;

/**
 * Created by Administrator on 2016/1/6.
 */
public abstract class BaseActivity extends Activity {
    private static final String simpleName = BaseActivity.class.getSimpleName();
    protected Activity mActivity;
    protected Context mContext;
    protected BlockDialog mBlockDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this;
        mBlockDialog = new BlockDialog(mContext);
    }


    protected void startIntent(Class<?> activity) {
        Intent intent = new Intent(mActivity, activity);
        startActivity(intent);
    }

    protected void startIntent(Class<?> activity, Bundle bundle) {
        Intent mIntent = new Intent(mActivity, activity);
        mIntent.putExtras(bundle);
        startActivity(mIntent);
    }

    protected void startActivityForResult(Class<?> activity, Bundle bundle, int requestCode) {
        Intent mIntent = new Intent(mActivity, activity);
        if (bundle != null) {
            mIntent.putExtras(bundle);
        }
        startActivityForResult(mIntent, requestCode);
    }

    protected void onBackBtnClick() {
        finish();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    /**
     * 设置头部title
     *
     * @param title 标题
     */
    protected void setHeader(CharSequence title) {
        TextView headerTv = (TextView) findViewById(R.id.top_title_tv);
        headerTv.setText(title);
    }

    /**
     * 设置头部title
     *
     * @param resId 资源ID
     */
    protected final void setHeader(int resId) {
        setHeader(getString(resId));
    }

    protected void setRightIcon(int resId) {
        ImageView imageView = (ImageView) findViewById(R.id.top_right_iv);
        imageView.setImageResource(resId);
        imageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    onBackBtnClick();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(simpleName);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(simpleName);
        MobclickAgent.onPause(this);
    }

    protected void showToast(CharSequence text) {
        Utils.showToast(text, Toast.LENGTH_SHORT);
    }

    protected void showToast(int resId) {
        Utils.showToast(getString(resId), Toast.LENGTH_SHORT);
    }

    protected void showToast(int resId, int duration) {
        Utils.showToast(getString(resId), duration);
    }
}
