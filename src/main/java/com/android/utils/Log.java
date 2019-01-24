package com.android.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import android.text.TextUtils;

import com.android.master.BuildConfig;

/**
 * @description 日志打印操作
 * 
 * @author wangyang
 * @create 2014-2-21 上午09:32:30
 * @version 1.0.0
 */
public class Log {
	/** 是否启用日志 ,默认为启用 */
	private static boolean ENABLE = true;
	/** 是否打印日志到手机 ,默认为关闭 */
	private static boolean SAVELOCAL = true;
	/**
	 * 日志打印级别,默认为INFO
	 */
	private static int LEVEL = Level.DEBUG.getLevel();

	public static void v(String s, String map) {

	}

    /**
	 * 
	 * 日志常用级别枚举类
	 */
	public enum Level {
		DEBUG(3), INFO(2), WARN(1), ERROR(0);

		private int level;

		Level(int level) {
			this.level = level;
		}

		public int getLevel() {
			return level;
		}
	}

	/**
	 * 记录日志
	 * 
	 * @param level
	 *            Level.DEBUG,Level.INFO,Level.WARN,Level.ERROR
	 * 
	 * @param text
	 */
	public static void log(Level level, String tag, String text) {
		if (StringUtil.isEmpty(tag))
			tag = "LOG";
		if (ENABLE) {
			if (!TextUtils.isEmpty(text)) {
				switch (level) {
				case DEBUG:
					if (Log.LEVEL >= Level.DEBUG.getLevel()) {
						android.util.Log.d(tag, text);
					}
					break;
				case INFO:
					if (Log.LEVEL >= Level.INFO.getLevel()) {
						android.util.Log.i(tag, text);
					}
					break;
				case WARN:
					if (Log.LEVEL >= Level.WARN.getLevel()) {
						android.util.Log.w(tag, text);
					}
					break;
				case ERROR:
					if (Log.LEVEL >= Level.ERROR.getLevel()) {
						android.util.Log.e(tag, text);
					}
					break;
				}
			}
		}
	}

	/**
	 * 打印debug信息
	 * 
	 * @param text
	 */
	public static void d(String text) {
		log(Level.DEBUG, null, text);
	}

	/**
	 * 根据tag打印debug信息
	 * 
	 * @param tag
	 * @param text
	 */
	public static void d(String tag, String text) {
		log(Level.DEBUG, tag, text);
	}

	/**
	 * 根据tag打印info信息
	 * 
	 * @param tag
	 * @param text
	 */
	public static void i(String tag, String text) {
		log(Level.INFO, tag, text);
	}

	/**
	 * 打印info信息
	 * 
	 * @param text
	 */
	public static void i(String text) {
		log(Level.INFO, null, text);
	}

	/**
	 * 根据tag打印warn信息
	 * 
	 * @param tag
	 * @param text
	 */
	public static void w(String tag, String text) {
		log(Level.WARN, tag, text);
	}

	/**
	 * 打印warn信息
	 * 
	 * @param text
	 */
	public static void w(String text) {
		log(Level.WARN, null, text);
	}

	/**
	 * 打印error信息
	 * 
	 * @param text
	 */
	public static void e(String text) {
		log(Level.ERROR, null, text);
	}

	/**
	 * 根据tag打印error信息
	 * 
	 * @param tag
	 * @param text
	 */
	public static void e(String tag, String text) {
		log(Level.ERROR, tag, text);
	}

	/**
	 * 设置是否启用日志
	 * 
	 * @param enable
	 */
	public static void setEnable(boolean enable) {
		Log.ENABLE = enable;
	}

	/**
	 * 是否启用打印日志到手机
	 * 
	 * @param saveLocal
	 */
	public static void setSaveLocal(boolean saveLocal) {
		SAVELOCAL = saveLocal;
	}

	/**
	 * 设置日志启动级别
	 * 
	 * @param level
	 *            Level.DEBUG;Level.INFO; Level.WARN;Level.ERROR; 级别由低到高
	 * 
	 */
	public static void setLevel(Level level) {
		Log.ENABLE = true;
		Log.LEVEL = level.getLevel();
	}

	/**
	 * 将LOG打印到手机
	 * 
	 * @param e
	 */
	public static void saveLogToLocal(String log) {
		File saveDir = FileUtil.getSDCard();
		if (saveDir != null && SAVELOCAL) {
			FileWriter fos = null;
			File saveFile = new File(saveDir, "log.txt");
			try {
				fos = new FileWriter(saveFile, true);
				fos.write(log + "\n");
				fos.flush();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				if (fos != null)
					try {
						fos.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			}
		}
	}

}
