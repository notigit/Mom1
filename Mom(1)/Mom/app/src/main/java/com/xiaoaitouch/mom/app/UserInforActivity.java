package com.xiaoaitouch.mom.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoaitouch.mom.R;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.module.UploadParams;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.sqlite.UserTables;
import com.xiaoaitouch.mom.util.ButtomDialog;
import com.xiaoaitouch.mom.util.DisplayImageOptionsUtils;
import com.xiaoaitouch.mom.util.UpLoadImage;
import com.xiaoaitouch.mom.util.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用户信息
 *
 * @author huxin
 * @data: 2016/1/9 17:10
 * @version: V1.0
 */
public class UserInforActivity extends BaseActivity {
    private static final String SAVE_PIC_PATH = Environment
            .getExternalStorageState().equalsIgnoreCase(
                    Environment.MEDIA_MOUNTED) ? Environment
            .getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";// 保存到SD卡
    /**
     * 调用系统相机拍照
     */
    public static final int REQUEST_CODE_TAKE_PHOTO = 20121;
    /**
     * 选择相册
     */
    public static final int REQUEST_CODE_TAKE_PIC = 20122;
    /**
     * 头像剪切
     */
    public static final int REQUEST_CODE_HANDLE_PHOTO = 20123;
    /**
     * 头像文件
     */
    public static final File HEAD_FIEL = new File(
            Environment.getExternalStorageDirectory() + "/temp.jpg");
    /**
     * 头像临时存储Uri
     */
    public static final Uri HEAD_URI = Uri.fromFile(HEAD_FIEL);
    /**
     * 剪切后的头像存储地址
     */
    private String headPath = null;
    private String subForder = null;
    @Bind(R.id.user_head_image)
    ImageView userHeadImage;
    @Bind(R.id.user_nickname_tv)
    TextView userNicknameTv;



    private UserModule userModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_infor_activity);
        ButterKnife.bind(this);
        initViewData();
    }

    private void initViewData() {
        userModule = MyApplication.instance.getUserModule();
        if (userModule != null) {
            setHeader(userModule.getNeckname());
            userNicknameTv.setText(userModule.getNeckname());
            if (!TextUtils.isEmpty(userModule.getHeadPic())) {
                if (userModule.getHeadPic().contains("http")) {
                    ImageLoader.getInstance().displayImage(
                            userModule.getHeadPic(),
                            userHeadImage,
                            DisplayImageOptionsUtils
                                    .getUserDisplayImageOptions());
                } else {
                    ImageLoader.getInstance().displayImage(
                            Configs.IMAGE_URL + userModule.getHeadPic(),
                            userHeadImage,
                            DisplayImageOptionsUtils
                                    .getUserDisplayImageOptions());
                }
            }
        }
    }

    @OnClick(R.id.user_head_ray)
    public void updateHead() {
        LayoutInflater inflater = getLayoutInflater();
        final ButtomDialog mChooseDialog = new ButtomDialog(mActivity);
        View rootView = inflater
                .inflate(R.layout.update_head_sheet, null);
        TextView takePhotoTV = (TextView) rootView
                .findViewById(R.id.image_choose_takephoto_tv);
        takePhotoTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {// 拍照
                mChooseDialog.dismiss();
                // 相机拍照
                takePhoto();
            }
        });
        TextView albumTv = (TextView) rootView
                .findViewById(R.id.image_choose_album_tv);
        albumTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {// 选择照片
                mChooseDialog.dismiss();
                // 系统相册
                takePicture();
            }
        });
        TextView cancelTv = (TextView) rootView
                .findViewById(R.id.action_sheet_cancel_tv);
        cancelTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {// 取消
                mChooseDialog.dismiss();
            }
        });
        mChooseDialog.setContentView(rootView);
        mChooseDialog.show();
    }

    /**
     * 调用系统相机拍照获取图片
     */
    public void takePhoto() {
        // 执行拍照前，应该先判断SD卡是否存在
        if (Validation.checkSDCard(mActivity)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 下面这句指定调用相机拍照后的照片存储的路径
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                    Environment.getExternalStorageDirectory(), "temp.jpg")));
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    /**
     * 选择系统相册
     */
    public void takePicture() {
        // 调用系统相册
        Intent picIntent = new Intent(Intent.ACTION_PICK,
                Media.EXTERNAL_CONTENT_URI);
        picIntent.setType("image/*");// 设置类型
        startActivityForResult(picIntent, REQUEST_CODE_TAKE_PIC);
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    public void trimPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 128);
        intent.putExtra("outputY", 128);
        intent.putExtra("return-data", true);

        intent.putExtra("scale", true);// 黑边
        intent.putExtra("scaleUpIfNeeded", true);// 黑边
        startActivityForResult(intent, REQUEST_CODE_HANDLE_PHOTO);
    }

    @OnClick(R.id.top_title_back_tv)
    public void back() {
        onBackBtnClick();
    }

    @OnClick(R.id.user_nickname_ray)
    public void updateNickName() {
        startActivityForResult(UpdateNickNameActivity.class, null, 1002);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PIC:// 相册选择
                if (data != null) {
                    trimPhoto(data.getData());
                }
                break;
            case REQUEST_CODE_TAKE_PHOTO:// 系统相机
                if (HEAD_URI != null) {
                    trimPhoto(HEAD_URI);
                }
                break;
            case REQUEST_CODE_HANDLE_PHOTO:// 图片剪切后处理
                if (data != null) {
                    // 创建图片剪切后的存储路径
                    submitUpdateHead(data);
                }
                break;
        }
        if (resultCode == 1002) {
            initViewData();
        } else if (resultCode == 1003) {
            initViewData();
        }
    }

    private void submitUpdateHead(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            UploadParams uploadParams = new UploadParams();
            try {
                uploadParams.filePath = saveFile(bitmap, System.currentTimeMillis()
                        + ".jpg", "/DCIM/Camera");
            } catch (IOException e) {
                e.printStackTrace();
            }
            UpLoadImage upLoadImage = new UpLoadImage(uploadParams, "/user/headpic", new UpLoadImage.ResultListener() {
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
                                    JSONObject object = result
                                            .getObjectData();
                                    String headUrl = object
                                            .getString("headPic");
                                    ImageLoader.getInstance().displayImage(
                                            Configs.IMAGE_URL + headUrl,
                                            userHeadImage, DisplayImageOptionsUtils
                                                    .getUserDisplayImageOptions());
                                    saveHead(headUrl);
                                    recycleImage();
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
    }

    private void saveHead(String headUrl) {
        if (userModule != null) {
            userModule.setHeadPic(headUrl);
            MyApplication.instance.setUserModule(userModule);
            UserTables.updateUser(mActivity, userModule);
        }
    }

    /**
     * 保存本地文件
     *
     * @param bm
     * @param fileName
     * @param path
     * @return
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
     * 头像上传成功之后回收图片
     */
    public void recycleImage() {
        try {
            // 回收剪切钱的原始图片
            if (HEAD_FIEL.exists()) {
                HEAD_FIEL.delete();
            }
            // 回收剪切后的图片
            if (headPath != null) {
                File file = new File(headPath);
                if (file.exists()) {
                    file.delete();
                }
            }

            if (subForder != null) {
                File file = new File(subForder);
                if (file.exists()) {
                    file.delete();
                }
            }

        } catch (Exception e) {
        }
    }

}
