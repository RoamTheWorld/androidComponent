package com.android.receiver.event;

/**
 * @author wangyang
 *         2017/9/21 15:27
 */
public class GoHomeEvent {

    private boolean isHome;

    public GoHomeEvent(boolean isHome) {
        this.isHome = isHome;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }
}
