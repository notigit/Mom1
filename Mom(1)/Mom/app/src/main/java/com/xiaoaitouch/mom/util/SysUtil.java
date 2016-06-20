package com.xiaoaitouch.mom.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class SysUtil {
    private static DisplayMetrics metrics = null;

    private static DisplayMetrics getDisplayMetrice() {
        if (null == metrics) {
            metrics = new DisplayMetrics();
            return metrics;
        } else {
            return metrics;
        }
    }

    /***
     * 获取手机屏幕宽度
     *
     * @param activity
     * @return
     */
    public static int getPhoneWidth(Activity activity) {
        DisplayMetrics metrics = getDisplayMetrice();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int phoneWidth = metrics.widthPixels;
        return phoneWidth;
    }

    /***
     * 获取手机屏幕高度
     *
     * @param activity
     * @return
     */
    public static int getPhoneHeight(Activity activity) {
        DisplayMetrics metrics = getDisplayMetrice();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int phoneHeight = metrics.heightPixels;
        return phoneHeight;
    }

    /***
     * 获取状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        if (statusBarHeight == 0) {
            statusBarHeight = 50;
        }
        return statusBarHeight;
    }

    /***
     * bigmap转drawable
     *
     * @param bitmap
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Drawable bitmapToDrawableByBD(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(bitmap);

        return drawable;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * @param @param  dateString
     * @param @return 设定文件
     * @return long 返回类型
     * @throws
     * @Title: getTimeInMillis
     * @Description: 获取指定日期的时间戳
     */
    public static long getTimeInMillis(String dateString) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date;
        try {
            date = df.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0l;
        }
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
