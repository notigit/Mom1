package com.xiaoaitouch.mom.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.ResultObj;
import com.xiaoaitouch.mom.module.SportTabModule;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.api.HttpApi;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.sqlite.SportTables;

import org.json.JSONException;

import java.util.List;

/**
 * Created by Administrator on 2016/1/26.
 */
public class SubmitSportUtils {
    private static Context mContext;
    private static SubmitSportUtils instance;
    private static UserModule userModule;

    private SubmitSportUtils() {
    }

    public static SubmitSportUtils getInstance(Context context) {
        mContext = context;
        userModule = MyApplication.instance.getUserModule();
        if (instance == null) {
            instance = new SubmitSportUtils();
        }
        return instance;
    }

    /**
     * 运动数据的提交
     */
    public void submitSportData() {
        if (userModule != null) {
            List<SportTabModule> sportTabModule = SportTables.queryAllSportTabModule(mContext, userModule.getUserId(), StringUtils.getCurrentTimeSs());
            if (sportTabModule != null && sportTabModule.size() >= 1) {
                Gson gson = new Gson();
                String reslut = gson.toJson(sportTabModule);
                StringRequest request = new StringRequest(Request.Method.POST,
                        Configs.SERVER_URL + "/v2/submit/sportInfo",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                try {
                                    ResultObj result = new ResultObj(response);
                                    switch (result.getState()) {
                                        case ResultObj.SUCCESS:
                                            SportTables.deletesportTabModule(mContext, userModule.getUserId(), StringUtils.getCurrentTimeSs());
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
                HttpApi.submitSport("/v2/submit/sportInfo", request, reslut);
            }
        }
    }
}
