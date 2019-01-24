package com.android.utils;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;

/**
 * @description 注册以及发送广播的管理类
 * @author wangyang
 * @create 2014-12-5 下午5:44:42
 * @version 1.0.0
 */
public class BroadCastSenderUtil {
	
	public static Intent getIntent(int value) {
		Intent intent = new Intent();
		intent.setAction(Constants.Action.BASE_RECEIVER_ACTION);
		intent.putExtra(Constants.Action.BROADCAST_FIRST_DATA, value);
		return intent;
	}

	/**
	 * 发送只携带what的广播
	 * @param context
	 * @param value
	 */
	public static void send(Context context, int value) {
		Intent intent = getIntent(value).putExtra(Constants.Action.BROADCAST_SECODE_DATA, "");
		context.sendBroadcast(intent);
	}
	
	/**
	 * 发送带有消息的广播
	 * @param context
	 * @param value
	 * @param obj
	 */
	public static void send(Context context, int value,Object obj){
		if (obj == null) {
			send(context, value);
			return;
		} else {
			Intent intent = getIntent(value);
			String name = obj.getClass().getName();
			intent.putExtra(Constants.Action.BROADCAST_SECODE_DATA, name);
			if (name.equals("java.lang.String")) {
				intent.putExtra(Constants.Action.BROADCAST_THIRD_DATA, (String) obj);
			} else if (name.equals("java.lang.Integer")) {
				intent.putExtra(Constants.Action.BROADCAST_THIRD_DATA, (Integer) obj);
			} else {
				intent.putExtra(Constants.Action.BROADCAST_THIRD_DATA, (Serializable) obj);
			}
			context.sendBroadcast(intent);
		}
	}

}
