package com.xiaoaitouch.mom.net.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.xiaoaitouch.mom.config.Configs;
import com.xiaoaitouch.mom.config.MyApplication;
import com.xiaoaitouch.mom.module.UploadParams;
import com.xiaoaitouch.mom.module.UserMessage;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.SevenDoVolley;
import com.xiaoaitouch.mom.net.request.GsonTokenRequest;
import com.xiaoaitouch.mom.net.request.StringRequest;
import com.xiaoaitouch.mom.util.Encode;
import com.xiaoaitouch.mom.util.Utils;

public class HttpApi {
    /**
     * 提交心率
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void submitMeasureHeartData(String method,
                                                  StringRequest request, Object object) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("hrInfos", (String) object);
        getRequest(method, mPostParams, request);
    }

    /**
     * 心率动列表
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void getMeasureHeartData(String method,
                                               GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * 删除心率
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void deleteMeasureHeartModuleData(String method,
                                                        StringRequest request, Object object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * 数胎动列表
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void getFetalMovementData(String method,
                                                GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * 提交记宫缩
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void submitFetalMovementData(String method,
                                                   StringRequest request, Object object) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("fmInfos", (String) object);
        getRequest(method, mPostParams, request);
    }

    /**
     * 删除胎动
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void deleteFetalMovementModuleData(String method,
                                                         StringRequest request, Object object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }


    /**
     * 删除记宫缩
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void deleteRecordContractionsData(String method,
                                                        StringRequest request, Object object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * 提交记宫缩
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void submitRecordContractionsData(String method,
                                                        StringRequest request, Object object) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("ucInfos", (String) object);
        getRequest(method, mPostParams, request);
    }


    /**
     * 记宫缩列表
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void getRecordContractionsData(String method,
                                                     GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }


    /**
     * 能不能列表
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void getDazzleListData(String method,
                                             GsonTokenRequest<T> request, int object) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("index", object + "");
        getRequest(method, mPostParams, request);
    }


    /**
     * 能不能列表
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void getCanOrNotDetails(String method,
                                              GsonTokenRequest<T> request, long object) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("id", object + "");
        getRequest(method, mPostParams, request);
    }


    /**
     * 能不能列表
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void getCanOrNotList(String method,
                                           GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("type", str[0]);
        mPostParams.put("keyWord", str[1]);
        mPostParams.put("index", str[2]);
        getRequest(method, mPostParams, request);
    }

    /**
     * @param method
     * @param request
     * @param object
     */
    public static <T> void getWelfareData(String method,
                                          GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * 获取主页数据
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void getMainData(String method,
                                       GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("date", str[0]);
        mPostParams.put("areaName", str[1]);
        mPostParams.put("pregrenttime", str[2]);
        getRequest(method, mPostParams, request);
    }

    /**
     * 获取收藏列表
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void getCollectList(String method,
                                          GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * 获取收藏地址
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void getCollectAddress(String method,
                                             StringRequest request, String url) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("url", url);
        getRequest(method, mPostParams, request);
    }

    /**
     * 获取收藏状态
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void getClassRoomData(String method,
                                            GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * 获取收藏状态
     *
     * @param method
     * @param request
     * @param <T>
     */
    public static <T> void getCollect(String method,
                                      GsonTokenRequest<T> request, String url) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("url", url);
        getRequest(method, mPostParams, request);
    }

    /**
     * 上传图片
     *
     * @author huxin
     * @data: 2016/1/10 18:25
     * @version: V1.0
     */
    public static <T> void uploadImage(String method,
                                       StringRequest request, Object object) {
        Map<String, String> mPostParams = getParams();
        UploadParams uploadParams = (UploadParams) object;
        mPostParams.put("img", uploadParams.filePath == null ? "" : uploadParams.filePath);
        mPostParams.put("date", uploadParams.date);
        mPostParams.put("message", uploadParams.message == null ? "" : uploadParams.message);
        getRequest(method, mPostParams, request);
    }

    /**
     * @param @param method
     * @param @param request
     * @param @param object 设定文件
     * @return void 返回类型
     * @throws
     * @Title: deleteDueCamera
     * @Description: 删除产检照片
     */
    public static <T> void deleteDueCamera(String method,
                                           StringRequest request, String str) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("id", str);
        getRequest(method, mPostParams, request);
    }

    /**
     * @param @param method
     * @param @param request
     * @param @param object 设定文件
     * @return void 返回类型
     * @throws
     * @Title: getDueCamera
     * @Description: 产检相册
     */
    public static <T> void getDueCamera(String method,
                                        GsonTokenRequest<T> request, String object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * @param @param method
     * @param @param request
     * @param @param object 设定文件
     * @return void 返回类型
     * @throws
     * @Title: getCode
     * @Description: 获取登录code
     */
    public static <T> void getCode(String method, StringRequest request,
                                   Object object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * @param @param method
     * @param @param request
     * @param @param object 设定文件
     * @return void 返回类型
     * @throws
     * @Title: getExpertConsultation
     * @Description: 专家咨询
     */
    public static <T> void getExpertConsultation(String method,
                                                 GsonTokenRequest<T> request) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * @param @param method
     * @param @param request
     * @param @param object 设定文件
     * @return void 返回类型
     * @throws
     * @Title: submitFeedBack
     * @Description: 意见反馈
     */
    public static <T> void submitFeedBack(String method, StringRequest request,
                                          Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("message", str[0]);
        mPostParams.put("email", str[1]);
        mPostParams.put("version", "android");
        mPostParams.put("verNum", Utils.getVersionName());
        getRequest(method, mPostParams, request);
    }

    /**
     * @param @param method
     * @param @param request
     * @param @param object 设定文件
     * @return void 返回类型
     * @throws
     * @Title: submitSport
     * @Description: 提交运动数据
     */
    public static <T> void submitSport(String method, StringRequest request,
                                       String object) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("sportInfos", object);
        getRequest(method, mPostParams, request);
    }

    /**
     * @param @param method
     * @param @param request
     * @param @param object 设定文件
     * @return void 返回类型
     * @throws
     * @Title: updateTemperature
     * @Description: 修改体温
     */
    public static <T> void updateTemperature(String method,
                                             StringRequest request, Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("temp", str[0]);
        mPostParams.put("date", str[1]);
        getRequest(method, mPostParams, request);
    }

    /**
     * @param @param method
     * @param @param request
     * @param @param object 设定文件
     * @return void 返回类型
     * @throws
     * @Title: getSymptom
     * @Description: 获取症状
     */
    public static <T> void getSymptom(String method,
                                      GsonTokenRequest<T> request, String object) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("pregrenttime", object);
        getRequest(method, mPostParams, request);
    }

    /**
     * 提交运动信息
     *
     * @param method
     * @param request
     */
    public static <T> void getBigThing(String method,
                                       GsonTokenRequest<T> request) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * 提交运动信息
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void submitSportInfo(String method,
                                           StringRequest request, String object) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("sportInfos", object);
        getRequest(method, mPostParams, request);
    }

    /**
     * 修改自查
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void updateSelf(String method, StringRequest request,
                                      Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("checks", str[0]);
        mPostParams.put("date", str[1]);
        getRequest(method, mPostParams, request);
    }

    /**
     * 修改妈咪症状
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void updateMsymptom(String method, StringRequest request,
                                          Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("symptoms", str[0]);
        mPostParams.put("date", str[1]);
        mPostParams.put("pregrenttime", str[2]);
        getRequest(method, mPostParams, request);
    }

    /**
     * 修改妈咪体重
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void updateMsWeight(String method, StringRequest request,
                                          Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("weight", str[0]);
        mPostParams.put("date", str[1]);
        getRequest(method, mPostParams, request);
    }

    /**
     * 天气和pm2.5接口
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void getPmOrWeather(String method,
                                          GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("areaName", str[0]);
        mPostParams.put("date", str[1]);
        getRequest(method, mPostParams, request);
    }

    /**
     * 删除卡片
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void deleteCard(String method, StringRequest request,
                                      Object object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * 删除用户
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void deleteUser(String method, StringRequest request,
                                      Object object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * 发送卡片(没有图片的)
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void sendCard(String method, GsonTokenRequest<T> request,
                                    Object object) {
        Map<String, String> mPostParams = getParams();
//		SendCardParams mainParams = (SendCardParams) object;
//		mPostParams.put("date", mainParams.getDate());
//		mPostParams.put("message", mainParams.getMessage());
//		mPostParams.put("lat", String.valueOf(mainParams.getLat()));
//		mPostParams.put("lng", String.valueOf(mainParams.getLng()));
//		mPostParams.put("location", mainParams.getLocation());
//		mPostParams.put("type", String.valueOf(mainParams.getType()));
//		mPostParams.put("createTime",
//				String.valueOf(mainParams.getCreateTime()));
        getRequest(method, mPostParams, request);
    }


    /**
     * 当前日历数据
     *
     * @param method
     * @param request
     * @param string
     */
    public static <T> void getCalendar(String method,
                                       GsonTokenRequest<T> request, String string) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * 修改昵称
     *
     * @param method
     * @param request
     * @param string
     */
    public static <T> void getUpdateNickName(String method,
                                             GsonTokenRequest<T> request, String string) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("neckname", string);
        getRequest(method, mPostParams, request);
    }

    /**
     * 忘记密码
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void getUserForgetPwd(String method,
                                            GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("phone", str[0]);
        mPostParams.put("pwd", str[1]);
        mPostParams.put("code", str[2]);
        getRequest(method, mPostParams, request);
    }

    /**
     * 登录
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void getUserLogin(String method,
                                        GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("phone", str[0]);
        mPostParams.put("code", str[1]);
        getRequest(method, mPostParams, request);
    }

    /**
     * 注册
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void getRegister(String method,
                                       GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("phone", str[0]);
        mPostParams.put("pwd", str[1]);
        mPostParams.put("code", str[2]);
        getRequest(method, mPostParams, request);
    }

    /**
     * 发送验证码
     *
     * @param method
     * @param request
     * @param phone
     */
    public static <T> void sendCode(String method, StringRequest request,
                                    String phone) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("phone", phone);
        getRequest(method, mPostParams, request);
    }

    /**
     * 第三方登录
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void getUserSocialLogin(String method,
                                              GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
//		SocialLogin sLogin = (SocialLogin) object;
//		mPostParams.put("socailUnid", sLogin.getSocailUnid());
//		mPostParams.put("userLoginName", sLogin.getUserLoginName());
//		mPostParams.put("userName", sLogin.getUserName());
//		mPostParams.put("head_pic", sLogin.getHead_pic());
//		mPostParams.put("source", String.valueOf(sLogin.getSource()));
        getRequest(method, mPostParams, request);
    }

    /**
     * 修改预产期信息
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void getUpdateDueInfo(String method,
                                            GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("dueTime", str[0]);
        mPostParams.put("lastMensesTime", str[1]);
        mPostParams.put("mensesCircle", str[2]);
        getRequest(method, mPostParams, request);
    }

    /**
     * @param @param method
     * @param @param request
     * @param @param object 设定文件
     * @return void 返回类型
     * @throws
     * @Title: getUpdateAgeAndHeight
     * @Description: 修改年龄和身高
     */
    public static <T> void getUpdateAgeAndHeight(String method,
                                                 GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("height", str[0]);
        mPostParams.put("age", str[1]);
        getRequest(method, mPostParams, request);
    }

    /**
     * 修改个人信息
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void getUpdateUserInfo(String method,
                                             GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        String[] str = (String[]) object;
        mPostParams.put("age", str[0]);
        mPostParams.put("height", str[1]);
        getRequest(method, mPostParams, request);
    }

    /**
     * 获取主页信息
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void getHomeData(String method,
                                       GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        mPostParams.put("lastMensesTime", (String) object);
        getRequest(method, mPostParams, request);
    }

    /**
     * 基础信息绑定
     *
     * @param method
     * @param request
     * @param object
     */
    public static <T> void getSubmitInfoParams(String method,
                                               GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        if (object != null) {
            UserMessage userInfo = (UserMessage) object;
            mPostParams.put("age", userInfo.getAge());
            mPostParams.put("height", userInfo.getHeight());
            mPostParams.put("neckname", userInfo.getNickName());
            mPostParams.put("dueTime", userInfo.getDueTime());
            mPostParams.put("weight", userInfo.getWeight());
            mPostParams.put("mensesCircle", String.valueOf(userInfo.getDays()));
            mPostParams.put("lastMensesTime", userInfo.getmLastPeriod());
        }
        getRequest(method, mPostParams, request);
    }

    /**
     * 获取个人信息
     *
     * @param <T>
     * @return
     */
    public static <T> void getUserInfo(String method,
                                       GsonTokenRequest<T> request, Object object) {
        Map<String, String> mPostParams = getParams();
        getRequest(method, mPostParams, request);
    }

    /**
     * 带参数请求公共方法
     *
     * @param method
     * @param map
     * @param request
     */
    private static <T> void getRequest(String method, Map<String, String> map,
                                       GsonTokenRequest<T> request) {
        Map<String, String> mPostParams = new HashMap<String, String>();
        RequestQueue requestQueue = SevenDoVolley.getRequestQueue();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if ((String) entry.getValue() != null) {
                request.addPostParam((String) entry.getKey(),
                        (String) entry.getValue());
                mPostParams.put((String) entry.getKey(),
                        (String) entry.getValue());
            }

        }
        request.addPostParam("key", getKeyValues(method, mPostParams));
        requestQueue.add(request);
    }

    /**
     * 带参数请求公共方法
     *
     * @param method
     * @param map
     * @param request
     */
    private static void getRequest(String method, Map<String, String> map,
                                   StringRequest request) {
        Map<String, String> mPostParams = new HashMap<String, String>();
        RequestQueue requestQueue = SevenDoVolley.getRequestQueue();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if ((String) entry.getValue() != null) {
                request.addPostParam((String) entry.getKey(),
                        (String) entry.getValue());
                mPostParams.put((String) entry.getKey(),
                        (String) entry.getValue());
            }

        }
        request.addPostParam("key", getKeyValues(method, mPostParams));
        requestQueue.add(request);
    }

    public static Map<String, String> getParams() {
        Map<String, String> mPostParams = new HashMap<String, String>();
        UserModule userInfo = MyApplication.instance.getUserModule();
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getUserName())) {
            mPostParams.put("userName", userInfo.getUserName());
        } else {
            mPostParams.put("uniqueness", Utils.createUniqueness());
        }
        mPostParams
                .put("timestemp", String.valueOf(System.currentTimeMillis()));
        return mPostParams;
    }

    /**
     * 参数名排序得到key值
     *
     * @param method
     * @param map
     * @return
     */
    public static String getKeyValues(String method, Map<String, String> map) {
        String params = Configs.SERVER_URL + method + "?";
        String valueParams = setArraySort(map);
        params += valueParams.substring(1, valueParams.length());
        params += "&appId=com.xiaoaitouch.mom&com=xiaoaitouch";
        return Encode.getMd5Value(Encode.getMd5Value(params)).toString()
                .toUpperCase();// 字母大写
    }

    /**
     * 请求参数
     *
     * @param method
     * @param map
     * @return
     */
    public static String getValues(String method, Map<String, String> map) {
        String params = Configs.SERVER_URL + method + "?";
        String valueParams = setArraySort(map);
        params += valueParams.substring(1, valueParams.length());
        params += "&appId=com.xiaoaitouch.mom&com=xiaoaitouch";
        return params;
    }

    /**
     * 对参数名字进行排序
     *
     * @param map
     * @return
     */
    private static String setArraySort(Map<String, String> map) {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        String keys[] = new String[map.size()];
        Set<String> keySet = map.keySet();
        Iterator<String> iter = keySet.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            keys[i] = "&" + key + "=" + map.get(key);
            i++;
        }
        Arrays.sort(keys);
        for (String s : keys) {
            sb.append(s);
        }
        return sb.toString();
    }

}
