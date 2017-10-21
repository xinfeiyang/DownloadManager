package com.security.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 对数据进行加密的工具类;
 */
public class MD5Util {

	/**
	 * 对数据进行MD5加密;
	 * @param url :要加密的数据;
	 * @return :以字符串的方式返回加密后的结果
	 */
	public static String DatEncryption(String url) {
        String cacheKey;
        try {
            MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
	
}
