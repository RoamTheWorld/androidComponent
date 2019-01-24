package com.android.receiver.event;

/**
 * @author wangyang
 *         2017/9/21 15:28
 */
public class NetWorkEvent {

    private boolean connectState;

    public NetWorkEvent(boolean connectState) {
        this.connectState = connectState;
    }

    public boolean isConnectState() {
        return connectState;
    }
}
