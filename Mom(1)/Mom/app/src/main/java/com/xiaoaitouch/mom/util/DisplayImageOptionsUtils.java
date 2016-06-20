package com.xiaoaitouch.mom.util;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xiaoaitouch.mom.R;

public class DisplayImageOptionsUtils {
    /**
     *
     * @Title: getUserDisplayImageOptions
     * @Description: 设置头像的下载背景
     * @param @return 设定文件
     * @return DisplayImageOptions 返回类型
     * @throws
     */
    public static DisplayImageOptions getUserDisplayImageOptions() {
        DisplayImageOptions sLoadImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .showImageOnLoading(R.drawable.user_default_icon)
                .showImageForEmptyUri(R.drawable.user_default_icon)
                .showImageOnFail(R.drawable.user_default_icon)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .displayer(new RoundedBitmapDisplayer(20))
                .resetViewBeforeLoading(true).build();
        return sLoadImageOptions;
    }

    /**
     *
     * @Title: getUserDisplayImageOptions
     * @Description: 设置头像的下载背景
     * @param @return 设定文件
     * @return DisplayImageOptions 返回类型
     * @throws
     */
    public static DisplayImageOptions getUserHeadImageOptions() {
        DisplayImageOptions sLoadImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .showImageOnLoading(R.drawable.user_default_icon)
                .showImageForEmptyUri(R.drawable.user_default_icon)
                .showImageOnFail(R.drawable.user_default_icon)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .resetViewBeforeLoading(true).build();
        return sLoadImageOptions;
    }
    /**
     * 聊天界面用户的头像
     * @return
     */
    public static DisplayImageOptions getChatDisplayImageOptions() {
        DisplayImageOptions sLoadImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .showImageOnLoading(R.drawable.grzx_head_icon)
                .showImageForEmptyUri(R.drawable.grzx_head_icon)
                .showImageOnFail(R.drawable.grzx_head_icon)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .displayer(new RoundedBitmapDisplayer(90))
                .resetViewBeforeLoading(true).build();
        return sLoadImageOptions;
    }

    /**
     * 广告位图片下载
     * @return
     */
    public static DisplayImageOptions getAdvDisplayImageOptions() {
        DisplayImageOptions sLoadImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .showImageOnLoading(R.drawable.adv_default_icon)
                .showImageForEmptyUri(R.drawable.adv_default_icon)
                .showImageOnFail(R.drawable.adv_default_icon)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .displayer(new RoundedBitmapDisplayer(5))
                .resetViewBeforeLoading(true).build();
        return sLoadImageOptions;
    }


    public static DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions sLoadImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .showImageOnLoading(R.drawable.adv_default_icon)
                .showImageForEmptyUri(R.drawable.adv_default_icon)
                .showImageOnFail(R.drawable.adv_default_icon)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .resetViewBeforeLoading(true).build();
        return sLoadImageOptions;
    }

    public static DisplayImageOptions getDueCameraDisplayImageOptions() {
        DisplayImageOptions sLoadImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .showImageOnLoading(R.drawable.test_filter)
                .showImageForEmptyUri(R.drawable.test_filter)
                .showImageOnFail(R.drawable.test_filter)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .resetViewBeforeLoading(true).build();
        return sLoadImageOptions;
    }


    /**
     *
     * @Title: getDisplayImageOptions
     * @Description: 卡片下载设置背景
     * @param @return 设定文件
     * @return DisplayImageOptions 返回类型
     * @throws
     */
    public static DisplayImageOptions getCardDisplayImageOptions() {
        DisplayImageOptions sLoadImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .showImageOnLoading(R.drawable.test_filter)
                .showImageForEmptyUri(R.drawable.test_filter)
                .showImageOnFail(R.drawable.test_filter)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .resetViewBeforeLoading(true).build();
        return sLoadImageOptions;
    }
}
