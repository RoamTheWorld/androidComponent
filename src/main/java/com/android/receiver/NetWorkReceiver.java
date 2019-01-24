package com.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.eventbus.RxEventBus;
import com.android.receiver.event.NetWorkEvent;
import com.android.ui.widget.Toast;
import com.android.utils.Constants;

/**
 * 网络监听
 *
 * @author wangyang
 * @version 创建时间：2013-9-29 上午09:02:03
 */
public class NetWorkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        NetWorkEvent event = null;
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                Toast.show(context, Constants.CONNECTED);
                event = new NetWorkEvent(true);
            } else {
                Toast.show(context, Constants.NO_CONNECTED);
                event = new NetWorkEvent(false);
            }
        }

        Toast.show(context, Constants.CONNECTED);
        RxEventBus.getInstance().post(event);
    }
}
