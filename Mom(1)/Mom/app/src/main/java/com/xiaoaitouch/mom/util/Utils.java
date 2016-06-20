package com.xiaoaitouch.mom.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.xiaoaitouch.mom.config.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Utils {
    private static final String UNIQUENESS = "uniqueness";
    private static Toast sToast = null;
    private static String channel;

    public static void showToast(CharSequence text, int duration) {
        if (sToast == null) {
            sToast = Toast.makeText(MyApplication.instance, text, duration);
        } else {
            sToast.setText(text);
        }
        sToast.show();
    }

    public static void hideSoftInput(Activity activity, IBinder windowToken) {
        if (activity == null || windowToken == null) {
            return;
        }

        final InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }

    public static void hideAutoSoftInput(Window window) {
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * This method converts device specific pixels to device independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param px
     * @return 注意db和px的区别
     */
    public static int convertPixelsToDp(Context ctx, float px) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        int dp = (int) (px / (metrics.densityDpi / 160f));
        return dp;

    }

    public static int convertDpToPixelInt(Context context, float dp) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int px = (int) (dp * (metrics.densityDpi / 160f));
        return px;
    }

    public static float convertDpToPixel(Context context, float dp) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = (float) (dp * (metrics.densityDpi / 160f));
        return px;
    }

    public static int getVersionCode() {
        PackageInfo packageInfo = getPackageInfo();

        if (packageInfo != null) {
            return packageInfo.versionCode;
        } else {
            return 0;
        }
    }

    private static PackageInfo getPackageInfo() {
        PackageManager packageManager = MyApplication.instance
                .getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(
                    MyApplication.instance.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return packInfo;
    }

    public static String createUniqueness() {
        String uniqueness = SharedPreferencesUtil.getString(
                MyApplication.instance, UNIQUENESS, "");
        if (!TextUtils.isEmpty(uniqueness)) {
            return uniqueness;
        }
        uniqueness = createUniquenessByPhone();
        SharedPreferencesUtil.putString(MyApplication.instance, UNIQUENESS,
                uniqueness);

        return uniqueness;
    }

    private static String createUniquenessByPhone() {

        TelephonyManager tm = (TelephonyManager) MyApplication.instance
                .getSystemService(Context.TELEPHONY_SERVICE);
        String lineNumber = tm.getLine1Number();
        if (lineNumber != null && !"".equals(lineNumber)) {
            lineNumber = lineNumber.replace("+86", "").replace("-", "");
            if (Validation.isMobile(lineNumber)) {
                return "tel:" + lineNumber;
            }
        }
        String imsi = tm.getSubscriberId();
        if (imsi != null && imsi.length() > 4) {
            return "imsi:" + imsi;
        }
        int type = tm.getPhoneType();
        String diviceId = tm.getDeviceId();
        if (diviceId != null && diviceId.length() > 4) {
            if (type == TelephonyManager.PHONE_TYPE_GSM) {
                return "imei:" + diviceId;
            } else if (type == TelephonyManager.PHONE_TYPE_CDMA) {
                return "meid:" + diviceId;
            }
        }

        WifiManager wifi = (WifiManager) MyApplication.instance
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        if (mac != null && mac.length() > 4) {
            return "mac:" + mac;
        }

        UUID uuid = UUID.randomUUID();
        return "uuid:" + uuid.toString();
    }


    public static String getVersionName() {

        PackageInfo packageInfo = getPackageInfo();
        if (packageInfo != null) {
            return packageInfo.versionName;
        } else {
            return "";
        }

    }

    public static String getChannel() {
        if (TextUtils.isEmpty(channel)) {
            channel = getAppMetaData("UMENG_CHANNEL");
        }
        return channel;
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    private static String getAppMetaData(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = MyApplication.instance
                    .getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager
                        .getApplicationInfo(
                                MyApplication.instance.getPackageName(),
                                PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }

    public static String getAppID() {
        return "com.xiaoaitouch.mom";
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 虚化
     *
     * @param bitmap
     *            原图
     * @param view
     *            需要设置背景的view
     * @param scaleFactor
     *            图片缩放倍数(缩放越大，占资源越小)
     * @param radius
     *            模糊半径(半径越大，占资源越大，模糊效果越好)
     * @param res
     * @return drawable
     */
    public static Drawable blur(Bitmap bitmap, View view, float scaleFactor,
                                int radius, Resources res) {
        // float radius = 50;

        // view.setDrawingCacheEnabled(true);
        // Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() /
        // scaleFactor),
        // (int) (view.getMeasuredHeight() / scaleFactor),
        // Bitmap.Config.ARGB_8888);
        // Canvas canvas = new Canvas(overlay);
        // canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        // Paint paint = new Paint();
        // paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        // canvas.drawBitmap(bitmap, 0, 0, paint);
        // overlay = net.qiujuer.genius.blur.StackBlur.blurNatively(overlay,
        // radius, true);

        // overlay = FastBlur.doBlur(overlay, (int) radius, true);
        // overlay.recycle();

        // return new BitmapDrawable(res, overlay);

        return  Blur.blur(bitmap,view,16);


    }

    /**
     * 虚化
     *
     * @param bitmap
     *            原图
     * @param view
     *            需要设置背景的view
     * @param scaleFactor
     *            图片缩放倍数(缩放越大，占资源越小)
     * @param radius
     *            模糊半径(半径越大，占资源越大，模糊效果越好)
     * @param res
     * @return drawable
     */
    @SuppressWarnings("deprecation")
    public static void setBlurBg(Bitmap bitmap, View view, float scaleFactor,
                                 int radius, Resources res) {
        view.setBackgroundDrawable(blur(bitmap, view, scaleFactor, radius, res));
    }

    public static String getSysDate() {
        return new SimpleDateFormat("HH:mm").format(new Date(
                System.currentTimeMillis()));
    }
}
