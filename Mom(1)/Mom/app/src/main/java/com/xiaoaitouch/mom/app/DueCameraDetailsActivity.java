package com.xiaoaitouch.mom.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.fragment.ImageDetailFragment;
import com.xiaoaitouch.mom.module.ADueCameraModule;
import com.xiaoaitouch.mom.module.DueCameraModule;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.view.HackyViewPager;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 产检详情
 *
 * @author huxin
 * @data: 2016/1/9 20:23
 * @version: V1.0
 */
public class DueCameraDetailsActivity extends BaseFragmentActivity {
    @Bind(R.id.camera_pager)
    HackyViewPager cameraPager;
    @Bind(R.id.indicator)
    TextView indicator;

    private UserModule userModule;
    private long cameraId = 01;
    private boolean isChange = false;
    private int position = 0;
    private int imagePosition = 0;
    private int group = 1;
    private String groupSize = "";
    private ImagePagerAdapter mAdapter = null;
    private DueCameraModule dueCamera = null;
    private List<ADueCameraModule> mCameraBeansLists = null;
    private List<DueCameraModule> mDetailsBeansList = new ArrayList<DueCameraModule>();
    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.due_camera_details_activity);
        ButterKnife.bind(this);
        initViewData();
    }


    private void initViewData() {
        setRightIcon(R.drawable.delete_photo_icon);
        userModule = MyApplication.instance.getUserModule();
        mCameraBeansLists = DueCameraActivity.mCameraBeansList;
        if (mCameraBeansLists != null && mCameraBeansLists.size() >= 1) {
            for (int i = 0; i < mCameraBeansLists.size(); i++) {
                ADueCameraModule dueCameraBean = mCameraBeansLists.get(i);
                if (dueCameraBean != null) {
                    List<DueCameraModule> mDetailsBeans = dueCameraBean
                            .getDueCameras();
                    if (mDetailsBeans != null && mDetailsBeans.size() >= 1) {
                        for (int j = 0; j < mDetailsBeans.size(); j++) {
                            mDetailsBeansList.add(mDetailsBeans.get(j));
                        }
                    }
                }
            }
        }
        dueCamera = mDetailsBeansList.get(0);
        cameraId = dueCamera.getId();
        setHeader(dueCamera.getDate());
        if (mDetailsBeansList != null && mDetailsBeansList.size() >= 1) {
            for (int i = 0; i < mDetailsBeansList.size(); i++) {
                mFragments.add(ImageDetailFragment
                        .newInstance(mDetailsBeansList.get(i).getImg()));
            }
        }
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(),
                mFragments);
        cameraPager.setAdapter(mAdapter);
        cameraPager.setOffscreenPageLimit(0);
        cameraPager.setOnPageChangeListener(new GuidePageChangeListener());

        groupSize = DueCameraActivity.mStr;
        group = getIntent().getIntExtra("group", 1);
        imagePosition = getIntent().getIntExtra("position", 1);
        if (group == 1) {
            position = imagePosition;
        } else {
            if (!TextUtils.isEmpty(groupSize) && groupSize.length() >= 3) {
                String[] mSize = groupSize.split("-");
                position = Integer.valueOf(mSize[group - 2]) + imagePosition;
            }
        }
        cameraPager.setCurrentItem(position);
        CharSequence text = getString(R.string.viewpager_indicator,
                position + 1, cameraPager.getAdapter().getCount());
        indicator.setText(text);
        mAdapter.notifyDataSetChanged();
    }

    private class ImagePagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> mFragments;
        private FragmentManager fm;

        public ImagePagerAdapter(FragmentManager fm,
                                 ArrayList<Fragment> fragments) {
            super(fm);
            this.fm = fm;
            this.mFragments = fragments;
        }

        @Override
        public int getCount() {
            return mFragments == null ? 0 : mFragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        public void setFragments(ArrayList<Fragment> fragments) {
            if (this.mFragments != null) {
                FragmentTransaction ft = fm.beginTransaction();
                for (Fragment f : this.mFragments) {
                    ft.remove(f);
                }
                ft.commit();
                ft = null;
                fm.executePendingTransactions();
            }
            this.mFragments = fragments;
            notifyDataSetChanged();
        }
    }

    private class GuidePageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int posion) {
            position = posion;
            dueCamera = mDetailsBeansList.get(posion);
            cameraId = dueCamera.getId();

            setHeader(dueCamera.getDate());
            CharSequence text = getString(R.string.viewpager_indicator,
                    posion + 1, cameraPager.getAdapter().getCount());
            indicator.setText(text);
        }
    }

    @OnClick(R.id.top_title_back_tv)
    public void back() {
        if (isChange) {
            Intent intent1 = new Intent();
            setResult(1005, intent1);
        }
        onBackBtnClick();
    }

    @OnClick(R.id.top_right_iv)
    public void openCamera() {

    }

    private void deleteCamera() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Configs.SERVER_URL + "/v2/cj/delete", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    ResultObj result = new ResultObj(response);
                    switch (result.getState()) {
                        case ResultObj.FAIL:
                            showToast(result.getMessage());
                            break;
                        case ResultObj.SUCCESS:
                            mHandler.sendEmptyMessage(ResultObj.SUCCESS);
                            break;
                        case ResultObj.UN_USE:
                            showToast("版本过低请升级新版本");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        HttpApi.deleteDueCamera("/v2/cj/delete", request, cameraId + "");
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case ResultObj.SUCCESS:
                    isChange = true;
                    mDetailsBeansList.remove(position);
                    if (mDetailsBeansList.size() == 0) {
                        Intent intent1 = new Intent();
                        setResult(1005, intent1);
                        onBackBtnClick();
                    } else {
                        mFragments.remove(position);
                        mAdapter.setFragments(mFragments);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isChange) {
                Intent intent1 = new Intent();
                setResult(1005, intent1);
            }
            onBackBtnClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
