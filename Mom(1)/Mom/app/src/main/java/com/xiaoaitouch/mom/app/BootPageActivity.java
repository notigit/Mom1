package com.xiaoaitouch.mom.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.sqlite.UserTables;

/**
 * 启动页
 *
 * @author huxin
 * @data: 2016/1/13 16:44
 * @version: V1.0
 */
public class BootPageActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boot_page_activity);
        initViewData();
    }

    private void initViewData() {
        UserModule userModule = UserTables.queryUser(mActivity);
        if(userModule==null){
            splash(2000, WelcomeActivity.class);
        }else{
            MyApplication.instance.setUserModule(userModule);
            splash(2000, MainActivity.class);
        }
    }

    private void splash(int time, final Class<? extends Activity> activity) {
        if (time <= 200) {
            Intent intent = new Intent(BootPageActivity.this, activity);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
            finish();
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(BootPageActivity.this, activity);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                    finish();
                }
            }, time);
        }
    }
}
