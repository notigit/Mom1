package com.xiaoaitouch.mom.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.module.UploadParams;
import com.xiaoaitouch.mom.util.StringUtils;
import com.xiaoaitouch.mom.util.UpLoadImage;
import com.xiaoaitouch.mom.util.Utils;

import org.json.JSONException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Desc: 踩一脚
 * User: huxin
 * Date: 2016/3/1
 * Time: 13:43
 * FIXME
 */
public class DazzleSendActivity extends BaseActivity {
    private static final String SAVE_PIC_PATH = Environment
            .getExternalStorageState().equalsIgnoreCase(
                    Environment.MEDIA_MOUNTED) ? Environment
            .getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";// 保存到SD卡

    @Bind(R.id.dazzle_send_content_et)
    EditText dazzleSendContentEt;
    @Bind(R.id.dazzle_send_select_phone_iv)
    ImageView dazzleSendSelectPhoneIv;
    @Bind(R.id.dazzle_send_top_phone_iv)
    ImageView dazzleSendTopPhoneIv;
    @Bind(R.id.dazzle_send_change_tv)
    TextView dazzleSendChangeTv;
    @Bind(R.id.dazzle_send_select_picture_iv)
    ImageView dazzleSendSelectPictureIv;
    @Bind(R.id.dazzle_send_select_delete_iv)
    ImageView dazzleSendSelectDeleteIv;

    private String inputContent = "";
    private Bitmap mBitmap = null;
    private String subForder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dazzle_send_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        setHeader("踩一脚");
        setRightIcon(R.drawable.dazzle_send_top_right_icon);
        dazzleSendContentEt.addTextChangedListener(mTextWatcher);
        dazzleSendSelectPictureIv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dazzleSendSelectDeleteIv.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String inputContent = dazzleSendContentEt.getText().toString().trim();
            if (!TextUtils.isEmpty(inputContent)) {
                int mLength = inputContent.length();
                dazzleSendChangeTv.setText(mLength + "/" + "150");
            }
        }
    };

    @OnClick(R.id.top_right_iv)
    public void onRightActivity() {
        saveDazzleData();
    }

    @OnClick(R.id.dazzle_send_select_phone_iv)
    public void openGalleryTypeActivity() {
        startActivityForResult(GalleryTypeActivity.class, null, 1008);
    }

    @OnClick(R.id.dazzle_send_top_phone_iv)
    public void openCame() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2002);
    }

    @OnClick(R.id.top_title_back_tv)
    public void onBack() {
        onBackBtnClick();
    }

    @OnClick(R.id.dazzle_send_select_delete_iv)
    public void onDeletePic() {
        dazzleSendSelectPictureIv.setVisibility(View.GONE);
        dazzleSendSelectDeleteIv.setVisibility(View.GONE);
        mBitmap = null;
    }

    private void saveDazzleData() {
        inputContent = dazzleSendContentEt.getText().toString().trim();
        if (!TextUtils.isEmpty(inputContent) && inputContent.length() >= 1) {
            if (Utils.isNetworkConnected(mContext)) {
                sendCarImage();
            } else {
                showToast("您处于网络断开状态！");
            }
        } else {
            showToast("请输入内容");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1003) {
            String imagePath = data.getStringExtra("imagePath");
            dazzleSendSelectPictureIv.setVisibility(View.VISIBLE);
            getSelectPic(imagePath);
        } else if (requestCode == 2002 && resultCode == Activity.RESULT_OK) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                return;
            }
            String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data"); // 获取相机返回的数据，并转换为Bitmap图片格式

            FileOutputStream b = null;
            File file = new File("/sdcard/mami/");
            file.mkdirs();// 创建文件夹
            String fileName = "/sdcard/mami/" + name;
            try {
                b = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            dazzleSendSelectPictureIv.setVisibility(View.VISIBLE);
            dazzleSendSelectPictureIv.setImageBitmap(bitmap);// 将图片显示在ImageView里
            mBitmap = bitmap;
        }
    }

    private void getSelectPic(String path) {
        String mPath = "file://" + path;
        ImageLoader.getInstance().displayImage(mPath, dazzleSendSelectPictureIv,
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
                        mBitmap = bitmap;
                    }

                    @Override
                    public void onLoadingCancelled(String arg0, View arg1) {
                    }
                });
    }

    /**
     * 发送有图片的卡片
     */
    private void sendCarImage() {
        UploadParams uploadParams = new UploadParams();
        if (mBitmap != null) {
            String filePath = "";
            try {
                filePath = saveFile(mBitmap, System.currentTimeMillis() + ".jpg",
                        "/DCIM/Camera");
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadParams.filePath = filePath;
        }
        uploadParams.date = StringUtils.getCurrentTimeSs();
        uploadParams.message = inputContent;
        mBlockDialog.show();
        UpLoadImage upLoadImage = new UpLoadImage(uploadParams, "/v3/fc", new UpLoadImage.ResultListener() {
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

    private void submitSuccess() {
        Intent intent1 = new Intent();
        setResult(1004, intent1);
        onBackBtnClick();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }
}
