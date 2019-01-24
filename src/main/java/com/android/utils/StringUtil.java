package com.android.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @description 字符串操作
 * 
 * @author wangyang
 * @create 2014-2-21 上午11:40:27
 * @version 1.0.0
 */
public class StringUtil {

	public static final String TAG = "StringUtil";

	/**
	 * 存放正则表达式
	 * @author Administrator
	 *
	 */
	public interface Regulars {
		/**
		 * 匹配邮箱
		 */
		public static String checkMail = "[\\w_\\-]*+\\@[\\w_\\-]*+\\.[\\w_\\-]*+\\.(com|cn|org|edu|hk)";

		/**
		 * 匹配手机号
		 */
//		public static final String MATCH_PHONE_NUMBER = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
		public static final String MATCH_PHONE_NUMBER = "^((13[0-9])|(15[0-9])|(18[0-9])|(147))\\d{8}$";
		/**
		 * 匹配身份证号
		 */
		public static final String MATCH_IDENTITY_CARD = "^\\d{15}|\\d{18}$";

	}

	public static boolean isEmpty(Object value, int length) {
		if (value == null)
			return true;
		boolean flag = false;
		if ("".equals(value.toString()) || "null".equals(value.toString()) || value.toString().length() < length) {
			flag = true;
		}
		return flag;
	}

	public static boolean isEmpty(Object value) {
		if (value == null)
			return true;
		boolean flag = false;
		if (value == null || "".equals(value) || "null".equals(value)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * MD5 加密
	 * 
	 * @author wangyang 2014-7-16 下午3:56:30
	 * @param str
	 * @return
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString();
	}

	/**
	 * 删除转义符
	 * 
	 * @author 谭杰
	 * @create 2014-6-26 上午9:51:50
	 * @param str
	 * @return
	 */
	public static String deleteEscape(String str) {
		char[] chars = str.toCharArray();
		String newStr = "";
		for (char ch : chars) {
			if (ch != 92) {
				newStr += ch;
			}
		}
		return newStr;
	}

	/**
	 * 转义符
	 * 
	 * @author wangyang 2014-7-30 下午5:01:46
	 * @param content
	 * @return
	 */
	public static String dscapeContent(String content) {
		if (content == null)
			return "";
		if (content.indexOf("'") != -1) {
			content = content.replaceAll("'", "\'");
		}
		if (content.indexOf("\"") != -1) {
			content = content.replaceAll("\"", "\\\\\"");
		}
		return content;
	}

	/**
	 * 字符串正则表达式验证
	 * @param 待验证的字符串
	 * @param 正则表达式
	 * @return
	 */
	public static boolean stringFormatCheck(String str, String regular) {
		return str.matches(regular);
	}

	/**
	 *  校验邮箱
	 *
	 * @author wangyang
	 * 2014-11-13 下午5:07:15
	 * @param str
	 * @return
	 */
	public static boolean checkEmail(String str) {
		return str.matches(Regulars.checkMail);
	}

	/**
	 * 效验手机号
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkPhone(String str) {
		return str.matches(Regulars.MATCH_PHONE_NUMBER);
	}
}
