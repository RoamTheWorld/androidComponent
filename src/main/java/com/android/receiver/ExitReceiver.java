package com.android.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * 退出系统
 * 
 * @author wangyang
 * @version 1.0.0
 * @create date 2013-7-11
 */
public class ExitReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Activity act = (Activity) context;
		act.finish();
	}
}
