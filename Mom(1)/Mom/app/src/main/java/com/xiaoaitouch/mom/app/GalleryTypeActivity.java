package com.xiaoaitouch.mom.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.adapter.AlbumItemAdapter;
import com.xiaoaitouch.mom.adapter.AlbumsAdapter;
import com.xiaoaitouch.mom.module.PhotoUpImageBucket;
import com.xiaoaitouch.mom.module.PhotoUpImageItem;
import com.xiaoaitouch.mom.util.PhotoUpAlbumHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 相册的分类管理
 *
 * @author huxin
 * @data: 2016/1/18 11:25
 * @version: V1.0
 */
public class GalleryTypeActivity extends BaseActivity implements
        View.OnClickListener {
    @Bind(R.id.gallery_type_header_lay)
    LinearLayout mLinearLayout;
    @Bind(R.id.gallery_type_gridview)
    GridView mGridView;
    @Bind(R.id.gallery_view_one_lay)
    LinearLayout mGalleryTyViewOne;
    @Bind(R.id.gallery_view_two_lay)
    LinearLayout mGalleryTyViewTwo;
    @Bind(R.id.gallery_type_listview)
    ListView mListView;

    // 获取手机相册数据
    private PhotoUpAlbumHelper photoUpAlbumHelper;
    private List<PhotoUpImageBucket> mImageBuckets;
    // 分类下面的图片
    private AlbumItemAdapter mAlbumItemAdapter;
    // 分类listview展示
    private AlbumsAdapter mAlbumsAdapter;
    // 存放相册分类的TextView
    private Map<Integer, TextView> map = new HashMap<Integer, TextView>();

    private List<PhotoUpImageItem> mPhotoUpImageItems;

    private int type = 0;// type==6是从产检相册过来的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_type_activity);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        type = getIntent().getIntExtra("type", 1);
        photoUpAlbumHelper = PhotoUpAlbumHelper.getHelper();
        photoUpAlbumHelper.init(mActivity);
        photoUpAlbumHelper.setGetAlbumList(new PhotoUpAlbumHelper.GetAlbumList() {
            @Override
            public void getAlbumList(List<PhotoUpImageBucket> list) {
                mImageBuckets = getResetData(list);
                if (mImageBuckets != null && mImageBuckets.size() > 0) {
                    setViewData();
                } else {
                    showToast("您手机当前没有照片");
                }
            }
        });
        photoUpAlbumHelper.execute(false);
    }

    private List<PhotoUpImageBucket> getResetData(List<PhotoUpImageBucket> list) {
        if (list != null && list.size() >= 1) {
            for (int i = 0; i < list.size(); i++) {
                PhotoUpImageBucket mBucket = list.get(i);
                if (mBucket.getBucketName().equals("Camera")) {
                    list.set(i, list.get(0));
                    list.set(0, mBucket);
                }
            }
        }
        return list;
    }

    private void setViewData() {
        int size = mImageBuckets.size();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int with = dm.widthPixels / 4;
        for (int i = 0; i < size; i++) {
            View headerView = LayoutInflater.from(this).inflate(
                    R.layout.horizontal_item, null);
            TextView mTextView = (TextView) headerView
                    .findViewById(R.id.horizontal_type_tv);
            if (i == 0) {
                mTextView.setSelected(true);
                mTextView.setFocusable(true);
            }
            map.put(i, mTextView);
            mTextView.setText(mImageBuckets.get(i).getBucketName());
            mTextView.setTag(i);
            mTextView.setOnClickListener(this);
            mLinearLayout.addView(headerView);
        }
        mPhotoUpImageItems = mImageBuckets.get(0).getImageList();
        mAlbumItemAdapter = new AlbumItemAdapter(mPhotoUpImageItems, with,
                mActivity);
        mGridView.setAdapter(mAlbumItemAdapter);
        mAlbumItemAdapter.notifyDataSetChanged();

        mAlbumsAdapter = new AlbumsAdapter(mActivity);
        mAlbumsAdapter.setArrayList(mImageBuckets);
        mListView.setAdapter(mAlbumsAdapter);
    }

    @OnClick({R.id.gallery_type_list_image, R.id.gallery_type_list_images})
    public void showGallery() {
        if (mGalleryTyViewOne.getVisibility() == View.VISIBLE) {
            mGalleryTyViewOne.setVisibility(View.GONE);
            mGalleryTyViewTwo.setVisibility(View.VISIBLE);
        } else {
            mGalleryTyViewTwo.setVisibility(View.GONE);
            mGalleryTyViewOne.setVisibility(View.VISIBLE);
        }
    }

    @OnItemClick(R.id.gallery_type_listview)
    public void OnItemClickListview(AdapterView<?> parent, int position) {
        mGalleryTyViewTwo.setVisibility(View.GONE);
        mGalleryTyViewOne.setVisibility(View.VISIBLE);
        currentView(position);
    }

    @OnItemClick(R.id.gallery_type_gridview)
    public void OnItemClickGridview(AdapterView<?> parent, int position) {
        PhotoUpImageItem mImageItem = (PhotoUpImageItem) parent
                .getAdapter().getItem(position);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", mImageItem.getImagePath());
        intent.putExtras(bundle);
        setResult(1003, intent);
        onBackBtnClick();
    }

    public void currentView(int position) {
        for (int i = 0; i < map.size(); i++) {
            if (position == i) {
                ((TextView) map.get(position)).setSelected(true);
                ((TextView) map.get(position)).setFocusable(true);
            } else {
                ((TextView) map.get(i)).setSelected(false);
                ((TextView) map.get(i)).setFocusable(false);
            }
        }
        if (mAlbumItemAdapter != null) {
            mAlbumItemAdapter.setArrayList(mImageBuckets.get(position)
                    .getImageList());
            mAlbumItemAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.horizontal_type_tv:
                currentView((Integer) v.getTag());
                break;

            default:
                break;
        }
    }

    @OnClick(R.id.back_tv)
    public void onBack() {
        onBackBtnClick();
    }
}
