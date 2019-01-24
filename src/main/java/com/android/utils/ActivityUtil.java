package com.android.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.master.R;
import com.android.ui.contact.BaseUser;
import com.android.ui.contact.ChooseUserActivity;

/**
 * @description Activity跳转操作
 * 
 * @author wangyang
 * @create 2014-2-21 上午11:40:27
 * @version 1.0.0
 */
public class ActivityUtil {
	public static final int SUCCESS = 100;
	public static final int ERROR = 99;

	/**
	 * 启动activity-standard
	 * 
	 * @param context
	 * @param clazz
	 */
	public static void startActivity(Context context, Class<?> clazz) {
		Intent intent = new Intent(context, clazz);
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	/**
	 * 启动activity-自定义启动方式
	 * 
	 * @param context
	 * @param clazz
	 */
	public static void startActivity(Context context, Class<?> clazz, int flags) {
		Intent intent = new Intent(context, clazz);
		intent.setFlags(flags);
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	/**
	 * 启动activity并传值
	 * 
	 * @param context
	 * @param clazz
	 * @param data
	 */
	public static void startActivity(Context context, Class<?> clazz, Bundle data, int animIn, int animOut) {
		Intent intent = new Intent(context, clazz);
		intent.putExtras(data);
		context.startActivity(intent);
		if (animIn == 0)
			animIn = R.anim.push_left_in;
		if (animOut == 0)
			animOut = R.anim.push_left_out;
		((Activity) context).overridePendingTransition(animIn, animOut);
	}

	/**
	 * 启动activity并传值
	 * 
	 * @param context
	 * @param clazz
	 * @param data
	 */
	public static void startActivity(Context context, Class<?> clazz, Bundle data) {
		Intent intent = new Intent(context, clazz);
		intent.putExtras(data);
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	/**
	 * 启动activity并传值
	 * 
	 * @param context
	 * @param clazz
	 * @param data
	 */
	public static void startActivityForResult(Activity act, Class<?> clazz, int requestCode) {
		Intent intent = new Intent(act, clazz);
		act.startActivityForResult(intent, requestCode);
		act.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	/**
	 * 启动activity并传值
	 * 
	 * @param context
	 * @param clazz
	 * @param data
	 * @param requestCode
	 */
	public static void startActivityForResult(Activity act, Class<?> clazz, Bundle data, int requestCode) {
		Intent intent = new Intent(act, clazz);
		intent.putExtras(data);
		act.startActivityForResult(intent, requestCode);
		act.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	/**
	 * 启动activity并传值-standard
	 * 
	 * @param context
	 * @param clazz
	 * @param data
	 */
	public static void startActivity(Context context, Class<?> clazz, Bundle data, int flags, int animIn, int animOut) {
		Intent intent = new Intent(context, clazz);
		intent.setFlags(flags);
		intent.putExtras(data);
		context.startActivity(intent);
		if (animIn == 0)
			animIn = R.anim.push_left_in;
		if (animOut == 0)
			animOut = R.anim.push_left_out;
		((Activity) context).overridePendingTransition(animIn, animOut);
	}

	public static void goSingleChooseUserActivity(Activity activity, Class<?> cls, BaseUser selectUser) {
		goChooseUserActivity(activity, cls, ChooseUserActivity.SINGLE_CHOICE, ChooseUserActivity.REQUEST_CODE_CHOICE_USER, null, selectUser, false,
				null, null);
	}

	public static void goSingleChooseUserActivity(Activity activity, Class<?> cls, BaseUser selectUser, int requestCode) {
		goChooseUserActivity(activity, cls, ChooseUserActivity.SINGLE_CHOICE, requestCode, null, selectUser, false, null, null);
	}

	public static void goSingleChooseUserWithCacheActivity(Activity activity, Class<?> cls, BaseUser selectUser, List<? extends BaseUser> allUsers,
			int requestCode) {
		goChooseUserActivity(activity, cls, ChooseUserActivity.SINGLE_CHOICE, requestCode, null, selectUser, true, allUsers, null);
	}

	public static void goSingleChooseUserWithCacheActivity(Activity activity, Class<?> cls, BaseUser selectUser, List<? extends BaseUser> allUsers) {
		goChooseUserActivity(activity, cls, ChooseUserActivity.SINGLE_CHOICE, ChooseUserActivity.REQUEST_CODE_CHOICE_USER, null, selectUser, true,
				allUsers, null);
	}

	public static void goMultiChooseUserActivity(Activity activity, Class<?> cls, List<? extends BaseUser> selectUsers, int requestCode) {
		goChooseUserActivity(activity, cls, ChooseUserActivity.MULTI_CHOICE, requestCode, selectUsers, null, false, null, null);
	}

	public static void goMultiChooseUserActivity(Activity activity, Class<?> cls, List<? extends BaseUser> selectUsers, int requestCode, Bundle data) {
		goChooseUserActivity(activity, cls, ChooseUserActivity.MULTI_CHOICE, requestCode, selectUsers, null, false, null, data);
	}

	public static void goMultiChooseUserActivity(Activity activity, Class<?> cls, List<? extends BaseUser> selectUsers) {
		goChooseUserActivity(activity, cls, ChooseUserActivity.MULTI_CHOICE, ChooseUserActivity.REQUEST_CODE_CHOICE_USER, selectUsers, null, false,
				null, null);
	}

	public static void goMultiChooseUserWithCacheActivity(Activity activity, Class<?> cls, List<? extends BaseUser> selectUsers,
			List<? extends BaseUser> allUsers) {
		goChooseUserActivity(activity, cls, ChooseUserActivity.MULTI_CHOICE, ChooseUserActivity.REQUEST_CODE_CHOICE_USER, selectUsers, null, true,
				allUsers, null);
	}

	public static void goMultiChooseUserWithCacheActivity(Activity activity, Class<?> cls, List<? extends BaseUser> selectUsers,
			List<? extends BaseUser> allUsers, int requestCode) {
		goChooseUserActivity(activity, cls, ChooseUserActivity.MULTI_CHOICE, requestCode, selectUsers, null, true, allUsers, null);
	}

	private static void goChooseUserActivity(Activity activity, Class<?> cls, int mode, int requestCode, List<? extends BaseUser> selectUsers,
			BaseUser selectUser, boolean isUseCache, List<? extends BaseUser> allUsers, Bundle data) {
		Intent intent = new Intent(activity, cls);
		intent.putExtra(Constants.SELECT_MODE, mode);
		if (mode == ChooseUserActivity.SINGLE_CHOICE)
			intent.putExtra(Constants.CHOICE_USER, selectUser);
		else
			intent.putExtra(Constants.CHOICE_USER, (ArrayList<? extends BaseUser>) selectUsers);
		intent.putExtra(Constants.IS_USE_CACHE, isUseCache);
		if (allUsers != null)
			intent.putExtra(Constants.ALL_USER, (ArrayList<? extends BaseUser>) allUsers);
		if (data != null)
			intent.putExtras(data);
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * dp转px
	 * 
	 * @param context
	 * @param dpValue
	 *            dp值
	 * @return px
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 关闭软键盘
	 */
	public static void closeKeyboard(Context context) {
		InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		View view = ((Activity) context).getCurrentFocus();
		if (view != null) {
			IBinder binder = view.getApplicationWindowToken();
			if (binder != null && im != null && view != null) {
				im.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	/**
	 * 给应用评分
	 * 
	 * @author xzj
	 * @2014-3-4 下午5:03:43
	 * @param context
	 */
	public static void setGrade(Context context) {
		Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	// Android获取一个用于打开APK文件的intent
	public static Intent getApkFileIntent(String param) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		return intent;
	}

	/**
	 * 获取全局sharedPreferences
	 * 
	 * @author 谭杰
	 * @create 2014-7-9 下午5:29:07
	 * @param context
	 * @return
	 */
	public static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(Constants.FILE_CACHE_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * 验证是否登录
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkIsLogin(Context context) {
		if (!Constants.IS_LOGIN) {
			Intent intent = new Intent(Constants.Action.LOGIN);
			intent.putExtra("isOtherActivity", true);
			context.startActivity(intent);
		}
		return Constants.IS_LOGIN;
	}

	// Android获取一个用于打开PPT文件的intent
	public static Intent getPptFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	// Android获取一个用于打开Excel文件的intent
	public static Intent getExcelFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	// Android获取一个用于打开Word文件的intent
	public static Intent getWordFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	// Android获取一个用于打开CHM文件的intent
	public static Intent getChmFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	// Android获取一个用于打开文本文件的intent
	public static Intent getTextFileIntent(String param, boolean paramBoolean) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (paramBoolean) {
			Uri uri1 = Uri.parse(param);
			intent.setDataAndType(uri1, "text/plain");
		} else {
			Uri uri2 = Uri.fromFile(new File(param));
			intent.setDataAndType(uri2, "text/plain");
		}
		return intent;
	}

	// Android获取一个用于打开PDF文件的intent
	public static Intent getPdfFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}
}
