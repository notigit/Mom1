package com.xiaoaitouch.mom.config;

import com.xiaoaitouch.mom.util.Utils;

public class Configs {

	public final static boolean DEBUG = true;

	/**
	 * 成功.
	 */
	public static final int SUCCESS = 1;

	/**
	 * 失败.
	 */
	public static final int FAIL = -1;

	/**
	 * 版本不可用
	 */
	public static final int UN_USE = -2;
	/**
	 * 服务器地址
	 */
//	public final static String SERVER_URL = "http://192.168.1.100:8080/XiaoAiMom_Web";
//
//	public final static String IMAGE_URL = "http://192.168.1.100:8080/360DoctorFile";
    //线上地址
	 public final static String SERVER_URL = "http://app.likemami.com";

	 public final static String IMAGE_URL = "http://file.likemami.com";

	public static final int APP_VERSION = Utils.getVersionCode();
}
