package com.android.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

/**
 * 设备属性获取
 * @author wangyang
 *
 */
public class DevicePropertyUtil {

	/**
	 * 获取手机分辨率
	 * 
	 * @return DisplayMetrics
	 */
	public static DisplayMetrics getScreenPixel(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		Log.e("DisplayMetrics", "分辨率：" + dm.widthPixels + "x" + dm.heightPixels + ",精度：" + dm.density + ",densityDpi=" + dm.densityDpi);
		return dm;
	}

	/**
	 * 获取手机系统版本
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getAndroidSDKVersion() {
		int version;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			return 0;
		}
		return version;
	}

	/**
	 * 获取设备token
	 * 
	 * @param tm
	 * @return
	 */
	public static String getDeviceToken(Activity activity) {
		TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		String tokenId = tm.getDeviceId();
		if (StringUtil.isEmpty(tokenId)) {
			tokenId = tm.getSubscriberId();
		}
		if (StringUtil.isEmpty(tokenId)) {
			tokenId = UUID.randomUUID().toString().replace("-", "");
		}
		return tokenId;
	}
	
	/**
	 * 
	 * @author wangyang
	 * @2013-10-25 上午10:25:29
	 * @param context
	 * @return 获取本机的Mac地址
	 */
	public static String getMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();

		return info.getMacAddress();
	}
}
