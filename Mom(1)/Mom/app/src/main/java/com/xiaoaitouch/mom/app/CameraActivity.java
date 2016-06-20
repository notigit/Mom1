package com.xiaoaitouch.mom.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.module.UploadParams;
import com.xiaoaitouch.mom.util.CameraPreView;
import com.xiaoaitouch.mom.util.ShareInfo;
import com.xiaoaitouch.mom.util.StringUtils;
import com.xiaoaitouch.mom.util.UpLoadImage;

import org.json.JSONException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 产检相机
 *
 * @author huxin
 * @data: 2016/1/18 10:52
 * @version: V1.0
 */
public class CameraActivity extends BaseActivity {

    private static final String SAVE_PIC_PATH = Environment
            .getExternalStorageState().equalsIgnoreCase(
                    Environment.MEDIA_MOUNTED) ? Environment
            .getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";// 保存到SD卡
    @Bind(R.id.camera_relativeLayout)
    RelativeLayout layoutCamera;
    @Bind(R.id.release_card_image_iv)
    ImageView gImgView;
    @Bind(R.id.camera_btnFlash)
    ImageView btnFlash; // 闪光
    @Bind(R.id.camera_btnSwitch)
    ImageView btnSwitch; // 摄像头切换
    @Bind(R.id.notes_btnTakePic)
    ImageView btnTakePic; // 拍照
    @Bind(R.id.notes_btnAlbum)
    ImageView btnAblum; // 相册
    @Bind(R.id.retake_tv)
    TextView mRetakeTv;
    @Bind(R.id.sure_tv)
    TextView mSureTv;

    private Camera.Parameters parameters;
    private CameraPreView mPreView;
    private int screenWidth; // 屏幕宽度
    private int screenHeight; // 屏幕高度
    private Bitmap mBitmap = null;
    private String subForder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        ButterKnife.bind(this);
        initViewData();
        initCamera();
    }

    @Override
    protected void onResume() {
        mPreView.onRestart();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mPreView.onStop();
        super.onStop();
    }

    private void initViewData() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        ShareInfo.saveTagInt(this, ShareInfo.TAG_SCREEN_WIDTH, screenWidth);
        ShareInfo.saveTagInt(this, ShareInfo.TAG_SCREEN_HEIGHT, screenHeight);
    }

    /**
     * 初始化摄像头
     */
    public void initCamera() {
        mPreView = new CameraPreView(this);
        mPreView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        mPreView.initCamera();
        mPreView.setOnTakePictureListener(new CameraPreView.OnTakePictureListener() {
            @Override
            public void onPictureTaken(Bitmap bitmap) {
                setImageview(bitmap);
            }
        });
        mPreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreView.focus();
            }
        });

        btnTakePic.setTag(false);
        btnFlash.setTag(0);
        btnFlash.performClick();
        layoutCamera.addView(mPreView);
    }

    // 闪光
    @OnClick(R.id.camera_btnFlash)
    public void flashLight() {
        changeFlash();
    }

    // 摄像头切换
    @OnClick(R.id.camera_btnSwitch)
    public void onchageSwitch() {
        mPreView.switchCamera();
    }

    // 拍照
    @OnClick(R.id.notes_btnTakePic)
    public void takePic() {
        boolean isFlage = (Boolean) btnTakePic.getTag();
        if (isFlage) {
            mPreView.onRestart();
            mPreView.setVisibility(View.VISIBLE);
            btnAblum.setVisibility(View.VISIBLE);
            mRetakeTv.setVisibility(View.INVISIBLE);
            mSureTv.setVisibility(View.INVISIBLE);
            btnTakePic.setTag(false);
        } else {
            mPreView.takePic();
        }
    }

    // 打开相册
    @OnClick(R.id.notes_btnAlbum)
    public void openGalleryTypeActivity() {
        Intent intent = new Intent(mActivity, GalleryTypeActivity.class);
        intent.putExtra("type", 6);
        startActivityForResult(intent, 1002);
        overridePendingTransition(R.anim.action_sheet_dialog_down_in,
                R.anim.calendar_in_out);
    }

    // 发送图片
    @OnClick(R.id.sure_tv)
    public void sendImage() {
        if (mBitmap != null) {
            sendCarImage(mBitmap);
        }
    }

    @OnClick(R.id.activity_left_tv)
    public void onClose() {
        onBackBtnClick();
    }

    /**
     * 发送有图片的卡片
     *
     * @param bitmap
     */
    private void sendCarImage(Bitmap bitmap) {
        UploadParams uploadParams = new UploadParams();
        String filePath = "";
        try {
            filePath = saveFile(bitmap, System.currentTimeMillis() + ".jpg",
                    "/DCIM/Camera");
        } catch (IOException e) {
            e.printStackTrace();
        }
        uploadParams.filePath = filePath;
        uploadParams.date = StringUtils.getCurrentTimeSs();
        mBlockDialog.show();
        UpLoadImage upLoadImage = new UpLoadImage(uploadParams, "/v2/cj/add", new UpLoadImage.ResultListener() {
            @Override
            public void onResult(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        ResultObj result = new ResultObj(response);
                        switch (result.getState()) {
                            case ResultObj.FAIL:
                                showToast(result.getMessage());
                                break;
                            case ResultObj.SUCCESS:
                                recycleImage();
                                submitSuccess();
                                break;
                            case ResultObj.UN_USE:
                                showToast("版本过低请升级新版本");
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    showToast("修改失败");
                }
            }


        });
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            upLoadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            upLoadImage.execute();
        }
    }


    public void recycleImage() {
        try {
            if (subForder != null) {
                File file = new File(subForder);
                if (file.exists()) {
                    file.delete();
                }
            }

        } catch (Exception e) {
        }
    }

    private void submitSuccess() {
        Intent intent1 = new Intent();
        setResult(1004, intent1);
        onBackBtnClick();
    }

    /**
     * 保存本地文件
     *
     * @param bm
     * @param fileName
     * @param path
     * @return
     * @throws IOException
     */
    public String saveFile(Bitmap bm, String fileName, String path)
            throws IOException {
        subForder = SAVE_PIC_PATH + path;
        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(subForder, fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 60, bos);
        bos.flush();
        bos.close();
        return myCaptureFile.getPath();
    }

    /**
     * 闪光模式 ， 默认位置从0 0 自动 ， 1 关闭 ， 2 打开
     */
    private void changeFlash() {
        try {
            Camera.Parameters parameters = mPreView.getParameters();
            int status = (Integer) btnFlash.getTag();
            if (status == 0) {
                btnFlash.setImageResource(R.drawable.jlzp_light_icon_auto);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                btnFlash.setTag(1);
            } else if (status == 1) {
                btnFlash.setImageResource(R.drawable.jlzp_light_icon_stop);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                btnFlash.setTag(2);
            } else if (status == 2) {
                btnFlash.setImageResource(R.drawable.jlzp_light_icon_hand);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                btnFlash.setTag(0);
            }
            mPreView.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1003) {
            String imagePath = data.getStringExtra("imagePath");
            if (!TextUtils.isEmpty(imagePath)) {
                getSelectPic(imagePath);
            }
        }
    }

    private void getSelectPic(String path) {
        ImageLoader.getInstance().displayImage("file://" + path, gImgView,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String arg0, View arg1) {
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1,
                                                FailReason arg2) {
                    }

                    @Override
                    public void onLoadingComplete(String arg0, View arg1,
                                                  Bitmap bitmap) {
                        setImageview(bitmap);
                    }

                    @Override
                    public void onLoadingCancelled(String arg0, View arg1) {
                    }
                });
    }

    private void setImageview(Bitmap bitmap) {
        this.mBitmap = bitmap;
        gImgView.setImageBitmap(bitmap);
        mPreView.onStop();
        mPreView.setVisibility(View.GONE);
        btnAblum.setVisibility(View.INVISIBLE);
        mRetakeTv.setVisibility(View.VISIBLE);
        mSureTv.setVisibility(View.VISIBLE);
        btnTakePic.setTag(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }
}
