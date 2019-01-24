package com.android.utils.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * 
 * @author wangyang
 * @2013-10-23 下午4:24:29 加密类
 */
public class EncryptUtils {

	public static void main(String[] args) throws Exception { // 添加新安全算法,如果用JCE就要把它添加进去
		String key = "3b38e11ffd65698aedeb5ffc";
		String value = "4b5e6992";
		System.out.println("加密前的字符串:" + value);
		String encoded = encryptThreeDESECB(value, key);
		System.out.println("加密后的字符串:" + encoded);
		String srcBytes = decryptThreeDESECB(encoded, key);
		System.out.println("解密后的字符串:" + srcBytes);
	}

	public static String encryptThreeDESECB(String src, String key) throws Exception {
		DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey securekey = keyFactory.generateSecret(dks);

		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, securekey);
		byte[] b = cipher.doFinal(src.getBytes());

		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(b).replaceAll("\r", "").replaceAll("\n", "");

	}

	// 3DESECB解密,key必须是长度大于等于 3*8 = 24 位
	public static String decryptThreeDESECB(String src, String key) throws Exception {
		// --通过base64,将字符串转成byte数组
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] bytesrc = decoder.decodeBuffer(src);
		// --解密的key
		DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey securekey = keyFactory.generateSecret(dks);

		// --Chipher对象解密
		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, securekey);
		byte[] retByte = cipher.doFinal(bytesrc);

		return new String(retByte);
	}

}
