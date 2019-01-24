package com.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.eventbus.RxEventBus;
import com.android.receiver.event.GoHomeEvent;

/**
 * @author wangyang
 * @version 创建时间：2013-12-4 上午10:51:20
 */
public class GoHomeReceiver extends BroadcastReceiver {
	final String SYSTEM_DIALOG_REASON_KEY = "reason";
	final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";


	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
			String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
			if (reason != null && reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
				RxEventBus.getInstance().post(new GoHomeEvent(true));
			}
		}
	}
}
