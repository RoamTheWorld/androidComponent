package com.android.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

/**
 * 
 * 描述：中文操作工具类 Copyright: 版权所有 (c) 2002 - 2003 Company: 北京开拓明天科技有限公司
 * 
 * @author wangyang
 * @version 1.0.0
 * @create 2014-2-20
 */
public class ChineseUtil {

	/**
	 * 是否包含中文字符
	 * 
	 * @author 谭杰
	 * @create 2014-7-2 上午10:11:17
	 * @param strName
	 * @return
	 */
	public static boolean isChinese(String str) {
		char[] ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (isChinese(ch[i]) == true) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 中文字符判断
	 * 
	 * @author 谭杰
	 * @create 2014-7-2 上午10:11:40
	 * @param c
	 * @return
	 */
	public static boolean isChinese(char ch) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 汉字转换为汉语拼音，英文字符不变
	 * 
	 * @param chines
	 *            汉字
	 * @return 拼音
	 */
	public static String converterToSpell(String chines) {
		chines = chines.replaceAll("\\s*", "");
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				String pinyin = "";
				try {
					pinyin = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0];
				} catch (Exception e) {
					e.printStackTrace();
					pinyin = "";
				}
				pinyinName += pinyin;
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}

	/**
	 * 获取首字母,数据和特殊字符返回#
	 * 
	 * @param chines
	 *            汉字
	 * @return 拼音
	 */
	public static String converterToSpellFirst(String spellName) {
		String firstSpell = "#";
		if (!StringUtil.isEmpty(spellName))
			firstSpell = spellName.substring(0, 1);

		if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(firstSpell) == -1)
			firstSpell = "#";
		return firstSpell;
	}

	/**
	 * 首字母大写
	 * 
	 * @param colName
	 * @return
	 */
	public static String szmUpperCase(String colName) {
		// colName = colName.toLowerCase();
		char szm = colName.charAt(0);
		szm = Character.toUpperCase(szm);
		colName = colName.substring(1, colName.length());
		return new String(szm + colName);
	}
}
