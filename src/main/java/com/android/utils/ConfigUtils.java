package com.android.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 配置缓存
 */
public class ConfigUtils {

	private ConfigUtils() {
	}

	/**
	 * 写入配置文件
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void writeConfig(Context context, String key, String value) {
		SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = spf.edit();
		edit.putString(key, value);
		edit.commit();
	}

	public static void writeConfig(Context context, String key, boolean value) {
		SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = spf.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	public static void writeConfig(Context context, String key, float value) {
		SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = spf.edit();
		edit.putFloat(key, value);
		edit.commit();
	}

	public static void writeConfig(Context context, String key, int value) {
		SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = spf.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	public static void writeConfig(Context context, String key, long value) {
		SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = spf.edit();
		edit.putLong(key, value);
		edit.commit();
	}

	/**
	 * 读取配置文件
	 * 
	 * @param context
	 * @param key
	 * @param defaultValue
	 *            读取失败后返回的值
	 * @return value
	 */
	public static String readConfig(Context context, String key, String defaultValue) {
		SharedPreferences spf1 = PreferenceManager.getDefaultSharedPreferences(context);
		return spf1.getString(key, defaultValue);
	}

	public static boolean readConfig(Context context, String key, boolean defaultValue) {
		SharedPreferences spf1 = PreferenceManager.getDefaultSharedPreferences(context);
		return spf1.getBoolean(key, defaultValue);
	}

	public static float readConfig(Context context, String key, float defaultValue) {
		SharedPreferences spf1 = PreferenceManager.getDefaultSharedPreferences(context);
		return spf1.getFloat(key, defaultValue);
	}

	public static int readConfig(Context context, String key, int defaultValue) {
		SharedPreferences spf1 = PreferenceManager.getDefaultSharedPreferences(context);
		return spf1.getInt(key, defaultValue);
	}

	public static long readConfig(Context context, String key, long defaultValue) {
		SharedPreferences spf1 = PreferenceManager.getDefaultSharedPreferences(context);
		return spf1.getLong(key, defaultValue);
	}
	
	/**
	 * 
	 * 读取本地缓存文件
	 * 
	 * @param fileName
	 *            : 文件名（不带任何的 “/ ”和后缀名）
	 * @param expirationTime
	 *            : 缓存文件过期时间（毫秒）
	 * @return Object: 当缓存过期或读取异常时返回null
	 */
	public static Object readCache(Context context,String fileName, long expirationTime) {
		Object returnObj = null;
		try {
			File file = new File(FileUtil.getFileCacheDir(context),fileName + ".cache");
			if (System.currentTimeMillis() - file.lastModified() < expirationTime) {
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				returnObj = ois.readObject();
				fis.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnObj;
	}

	/**
	 * 将对象写入文件
	 * 
	 * @param fileName
	 *            文件名（不带任何的 “/ ”和后缀名）
	 * @param object
	 *            写入的对象
	 */
	public static void writeObject(Context context,String fileName, Object obj) {
		try {
			File file = new File(FileUtil.getFileCacheDir(context), fileName + ".cache");
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取文件
	 * 
	 * @param fileName
	 *            文件名（不带任何的 “/ ”和后缀名）
	 * @return object 读取失败时，返回null
	 */
	public static Object readObject(Context context,String fileName) {
		Object returnObj = null;
		try {
			File file = new File(FileUtil.getFileCacheDir(context), fileName + ".cache");
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			returnObj = ois.readObject();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnObj;
	}

	/**
	 * 删除缓存目录下指定的缓存文件
	 * 
	 * @param fileName
	 * @return boolean 当返回值为ture的时候，表示该缓存文件已删除或不存在
	 */
	public static boolean deleteCacheFile(Context context,String fileName) {
		boolean b = false;
		try {
			File file = new File(FileUtil.getFileCacheDir(context), fileName + ".cache");
			if (file.exists()) // 文件存在则删除
				b = file.delete();
			else
				b = true;// 文件不存在也认为文件删除成功
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
}
