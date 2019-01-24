package com.android.net.rx.mvp;

/**
 * 作者：wangyang
 * 创建时间：2016/9/23 17:21
 * 描述：
 */
public interface ViewWithPresenter<P extends BasePresenter> {

    P getPresenter();

    P initPresenter();

    void startPresenter();

    void stopPresenter();
}
