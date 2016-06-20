package com.xiaoaitouch.mom.util;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.module.UploadParams;
import com.xiaoaitouch.mom.net.SevenDoVolley;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.MultipartRequest;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.net.response.JsonResponse;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * 上传文件工具类
 *
 * @author huxin
 * @data: 2016/1/10 17:55
 * @version: V1.0
 */
public class UpLoadImage extends
        AsyncTask<String, Integer, String> {

    private UploadParams uploadParams;
    private ResultListener mListener;
    private String action = "";

    @Override
    protected String doInBackground(String... strings) {
        doRichMsgSend();
        return null;
    }


    public interface ResultListener {

        public void onResult(String response);

    }

    public UpLoadImage(UploadParams uploadParams, String action, ResultListener listener) {
        this.uploadParams = uploadParams;
        this.action = action;
        this.mListener = listener;

    }


    private void doRichMsgSend() {
        String filePath = uploadFile();
        uploadParams.filePath = filePath;
        StringRequest request = new StringRequest(Method.POST,
                Configs.SERVER_URL + action,
                new Listener<String>() {

                    @Override
                    public void onResponse(String responses) {
                        mListener.onResult(responses);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        });
        HttpApi.uploadImage(action, request, uploadParams);
    }

    /**
     * 上传文件地址获取文件的临时地址
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private String uploadFile() {
        String resPath = "";
        if (uploadParams.filePath != null && uploadParams.filePath.length() >= 1) {
            RequestFuture<JsonResponse<String>> voiceFuture = RequestFuture
                    .newFuture();
            if (fileExist(uploadParams.filePath)) {
                MultipartRequest voiceRequest = new MultipartRequest(
                        Configs.SERVER_URL + "/file/upload",
                        uploadParams.filePath, voiceFuture, voiceFuture);
                SevenDoVolley.addRequest(voiceRequest);
                JsonResponse<String> res = null;
                try {
                    res = voiceFuture.get();
                    resPath = res.data;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        return resPath;
    }

    private boolean fileExist(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return false;
        }
        return true;
    }

}
