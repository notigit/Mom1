package com.xiaoaitouch.mom.config;


import android.app.Application;
import android.os.Environment;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.easemob.chat.EMChat;
import com.example.bluetooth.le.BluetoothLeUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.analytics.MobclickAgent;
import com.xiaoaitouch.mom.module.Bluetooth;
import com.xiaoaitouch.mom.module.MainModle;
import com.xiaoaitouch.mom.module.UserModule;
import com.xiaoaitouch.mom.net.SevenDoVolley;
import com.xiaoaitouch.mom.util.CrashHandler;

import java.io.File;

public class MyApplication extends Application {
    private static final int DISK_CACHE_SIZE_BYTES = 50 * 1024 * 1024;
    private static final int MEMORY_CACHE_SIZE_BYTES = 2 * 1024 * 1024;
    public static MyApplication instance;

    public UserModule userModule;
    public static BDLocation mBDLocation = null;
    public static MainModle mainModle = null;
    private LocationClient baiduLocationClient;
    private Float[] sportsYValues = new Float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private Bluetooth bluetoothInfor = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
        SevenDoVolley.init(this);
        //环信SDK
        EMChat.getInstance().init(getApplicationContext());
        EMChat.getInstance().setDebugMode(true);
        // umeng统计
        MobclickAgent.openActivityDurationTrack(false);

        init();
    }


    public void init() {
        // 初始化百度SDK
        SDKInitializer.initialize(this);
        SevenDoVolley.init(this);
        // 网络图片下载设置
        initUniversalImageLoader();
        initBaiduLocateLocation();
    }

    private void initUniversalImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this, Environment
                .getExternalStorageDirectory().getPath() + "/Mom/images");
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(android.graphics.Bitmap.Config.RGB_565).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .defaultDisplayImageOptions(defaultOptions)
                .denyCacheImageMultipleSizesInMemory()// 不缓存同一图片的多种尺寸的内存缓存.threadPoolSize(3)
                .diskCacheSize(DISK_CACHE_SIZE_BYTES)
                .memoryCacheSize(MEMORY_CACHE_SIZE_BYTES)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .memoryCache(new WeakMemoryCache()).build();

        ImageLoader.getInstance().init(config);
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: initBaiduLocateLocation
     * @Description: 获取当前定位信息
     */
    private void initBaiduLocateLocation() {
        this.baiduLocationClient = new LocationClient(this);
        this.baiduLocationClient
                .registerLocationListener(new BDLocationListener() {
                    @Override
                    public void onReceiveLocation(BDLocation location) {
                        if (location != null) {
                            mBDLocation = location;
                        }
                    }
                });
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setScanSpan(2000);
        this.baiduLocationClient.setLocOption(option);
        baiduLocationClient.start();
    }

    public void setUserModule(UserModule userModule) {
        this.userModule = userModule;
    }

    public UserModule getUserModule() {
        return userModule;
    }

    public void setMainModle(MainModle mainModle) {
        this.mainModle = mainModle;
    }

    public MainModle getMainModle() {
        return mainModle;
    }

    public void setSportsYValues(int index, float values) {
        sportsYValues[index] = values;
    }

    public Float[] getSportsYValues() {
        return sportsYValues;
    }

    public void setBlueInfor(Bluetooth bluetooth) {
        this.bluetoothInfor = bluetooth;
    }

    public Bluetooth getBluetooth() {
        return bluetoothInfor;
    }

}
