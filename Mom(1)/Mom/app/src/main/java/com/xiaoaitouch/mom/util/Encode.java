package com.xiaoaitouch.mom.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encode {

	/**
	 * MD5全小写32位加密
	 * 
	 * @param sSecret
	 * @return
	 */
	public static String getMd5Value(String sSecret) {
		try {
			MessageDigest bmd5 = MessageDigest.getInstance("MD5");
			bmd5.update(sSecret.getBytes());
			int i;
			StringBuffer buf = new StringBuffer();
			byte[] b = bmd5.digest();
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}
