package com.android.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;

/**
 * 日期工具类
 * 
 * @author wangyang 2014-7-16 下午3:12:39
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {

	/**
	 * 根据毫秒值,获得格式化日期字符串
	 * 
	 * @author wangyang 2014-7-16 下午3:14:54
	 * @param milliseconds
	 * @return
	 */
	public static String getChatDate(long milliseconds) {
		// 根据传入毫秒值生成Calendar,并增加6天
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliseconds);
		calendar.add(Calendar.DAY_OF_YEAR, 6);

		// 获得当前时间的Calendar
		Calendar curCalendar = Calendar.getInstance();
		curCalendar.setTimeInMillis(System.currentTimeMillis());

		// 计算两个Calendar的日期差值
		int difference = curCalendar.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);

		// 格式化字符串
		String format = "yyyy-MM-dd";

		// 差值在0-7天内,说明在一周内
		if (difference > 0 && difference < 7)
			format = "E";

		// 如果差值为0,说明在当天
		if (difference == 0)
			format = "hh:mm";
		return getDate(milliseconds, format);
	}

	/**
	 * 根据格式化字符串,与毫秒值得到格式化日期字符串
	 * 
	 * @author wangyang 2014-7-16 下午3:47:44
	 * @param milliseconds
	 * @param format
	 * @return
	 */
	public static String getDate(long milliseconds, String format) {
		return new SimpleDateFormat(format).format(new Date(milliseconds));
	}

	/**
	 * 获得当天零点毫秒值
	 * 
	 * @author wangyang 2014-7-16 下午3:50:00
	 * @return
	 */
	public static long getTodayZeroClock() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 根据当前时间获取相隔为days天的零时,负数为当天之前的日期,反之未来日期
	 *
	 * @author wangyang
	 * 2014-12-27 下午2:52:01
	 * @param time
	 * @param days
	 * @return
	 */
	public static long getDateByDays(long time, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 格式化时间转换成毫秒
	 * 
	 * @author 谭杰
	 * @create 2014-9-23 下午3:48:56
	 * @param formatTime
	 * @param format
	 *            显示的时间的格式
	 * @return
	 */
	public static long dateToTime(String formatTime, String format) {
		try {
			SimpleDateFormat formatObj = new SimpleDateFormat(format, Locale.CHINA);
			return formatObj.parse(formatTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取时间段 凌晨0:00-5:59 |上午6:00-11:59 | 中午12:00-12:59 |下午13:00-17:59
	 * |晚上18:00-23:59
	 * 
	 * @param time
	 * @return
	 */
	public static String getTimePeriod(long milliseconds) {
		Calendar mCalendar = Calendar.getInstance(Locale.CHINA);
		mCalendar.setTimeInMillis(milliseconds);
		int hours = mCalendar.get(Calendar.HOUR);
		int minutes = mCalendar.get(Calendar.MINUTE);
		int seconds = mCalendar.get(Calendar.SECOND);
		long currTime = hours * 3600 + minutes * 60 + seconds;
		long wee = 6 * 3600;// 凌晨0:00-5:59
		long forenoon = 12 * 3600;// 上午6:00-11:59
		long midday = 13 * 3600;// 中午12:00-12:59
		long afternoon = 18 * 3600;// 下午13:00-17:59
		// long evening = 24 * 3600;// 晚上18:00-23:59
		if (currTime < wee) {
			return "凌晨";
		} else if (currTime < forenoon) {
			return "上午";
		} else if (currTime < midday) {
			return "中午";
		} else if (currTime < afternoon) {
			return "下午";
		}
		return "晚上";
	}

	/**
	 * 
	 * 获取某一国家系统当前的时间，将其格式化
	 * 
	 * @author 谭杰
	 * @create 2014-9-23 下午3:50:48
	 * @param format
	 * @return
	 */
	public static String getDateTime(String format) {
		return new SimpleDateFormat(format, Locale.CHINA).format(System.currentTimeMillis());
	}

	/**
	 * 将相应时间以某种格式进行格式化，
	 * 
	 * @author 谭杰
	 * @create 2014-9-23 下午3:50:36
	 * @param milliseconds
	 * @param format
	 *            yyyy-MM-dd HH:mm:ss 格式
	 * @return
	 */
	public static String getDateTime(long milliseconds, String format) {
		return new SimpleDateFormat(format, Locale.CHINA).format(milliseconds);
	}

	/**
	 * 获取星期,格式:星期一,星期二,星期三,星期四,星期五,星期六,星期日
	 * 
	 * @author 谭杰
	 * @create 2014-9-23 下午3:41:39
	 * @param milliseconds
	 *            毫秒
	 * @return
	 */
	public static String getWeekOne(long milliseconds) {
		Calendar mCalendar = Calendar.getInstance(Locale.CHINA);// 未来的某时间
		mCalendar.setTimeInMillis(milliseconds);// 设置毫秒数
		String week = "";
		int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
		case Calendar.SUNDAY:
			week = "星期日";
			break;
		case Calendar.MONDAY:
			week = "星期一";
			break;
		case Calendar.THURSDAY:
			week = "星期二";
			break;
		case Calendar.WEDNESDAY:
			week = "星期三";
			break;
		case Calendar.TUESDAY:
			week = "星期四";
			break;
		case Calendar.FRIDAY:
			week = "星期五";
			break;
		case Calendar.SATURDAY:
			week = "星期六";
			break;
		default:
			break;
		}
		return week;
	}

	/**
	 * 获取星期,格式:周一,周二,周三,周四,周五,周六,周日
	 * 
	 * @author 谭杰
	 * @create 2014-9-23 下午3:41:39
	 * @param milliseconds
	 * @return
	 */
	public static String getWeekTwo(long milliseconds) {
		Calendar mCalendar = Calendar.getInstance(Locale.CHINA);
		mCalendar.setTimeInMillis(milliseconds);
		String week = "";
		int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
		case Calendar.SUNDAY:
			week = "周日";
			break;
		case Calendar.MONDAY:
			week = "周一";
			break;
		case Calendar.THURSDAY:
			week = "周二";
			break;
		case Calendar.WEDNESDAY:
			week = "周三";
			break;
		case Calendar.TUESDAY:
			week = "周四";
			break;
		case Calendar.FRIDAY:
			week = "周五";
			break;
		case Calendar.SATURDAY:
			week = "周六";
			break;
		default:
			break;
		}
		return week;
	}

	/**
	 * @param String
	 *            sourceTime 待转化的时间
	 * @param String
	 *            dataFormat 日期的组织形式
	 * @return long 当前时间的长整型格式,如 1247771400000
	 */
	public static long string2long(String sourceTime, String dataFormat) {
		long longTime = 0L;
		SimpleDateFormat f = new SimpleDateFormat(dataFormat);
		Date d = null;
		try {
			d = f.parse(sourceTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		longTime = d.getTime();
		return longTime;
	}

	/**
	 * 长整型转换为日期类型
	 * 
	 * @param long longTime 长整型时间
	 * @param String
	 *            dataFormat 时间格式
	 * @return String 长整型对应的格式的时间
	 */
	public static String long2String(long longTime, String dataFormat) {
		Date d = new Date(longTime);
		SimpleDateFormat s = new SimpleDateFormat(dataFormat);
		String str = s.format(d);
		return str;
	}

	public static int getCurrentMonthDays() {
		Calendar mCalendar = Calendar.getInstance(Locale.CHINA);
		return mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static int getMonthDays(int showYear, int showMonth) {
		Calendar mCalendar = Calendar.getInstance(Locale.CHINA);
		mCalendar.set(Calendar.YEAR, showYear);
		mCalendar.set(Calendar.MONTH, showMonth);
		mCalendar.add(Calendar.MONTH, -1);
		return mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static int getFirstDayWeek(int showYear, int showMonth) {
		Calendar mCalendar = Calendar.getInstance(Locale.CHINA);
		mCalendar.set(Calendar.YEAR, showYear);
		mCalendar.set(Calendar.MONTH, showMonth);
		mCalendar.set(Calendar.DAY_OF_MONTH, 1);
		return mCalendar.get(Calendar.DAY_OF_WEEK);
	}

	public static int getYear() {
		return Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
	}

	public static int getMonth() {
		return Calendar.getInstance(Locale.CHINA).get(Calendar.MONTH);
	}

	public static int getDay() {
		return Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_MONTH);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SimpleDateFormat")
	public static String getWeek(long time) {
		Date date = new Date(time);
		String week = "";
		int day = date.getDay();
		switch (day) {
		case 0:
			week = "星期天";
			break;
		case 1:
			week = "星期一";
			break;
		case 2:
			week = "星期二";
			break;
		case 3:
			week = "星期三";
			break;
		case 4:
			week = "星期四";
			break;
		case 5:
			week = "星期五";
			break;
		case 6:
			week = "星期六";
			break;
		default:
			break;
		}
		return week;
	}

}
