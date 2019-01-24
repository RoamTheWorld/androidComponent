package com.android.net.rx.mvp;


import com.android.net.rx.life.RxLifecycle;
import com.android.net.rx.life.RxLifecycleManager;
import com.android.ui.MvpActivity;

import rx.Observable;

/**
 * Created by wangyang on 2016/6/24.
 */
public class RxPresenter implements BasePresenter {
    public MvpActivity activity;
    private RxLifecycleManager mRxLifecycleManager = new RxLifecycleManager();

    protected <T> Observable.Transformer<T, T> bindToLifecycle() {
        return mRxLifecycleManager.bindToLifecycle();
    }

    protected <T> Observable.Transformer<T, T> bindUntilEvent(RxLifecycle.Event event) {
        return mRxLifecycleManager.bindUntilEvent(event);
    }

    @Override
    public void subscribe() {
        mRxLifecycleManager.onStart();
    }

    @Override
    public void unSubscribe() {
        mRxLifecycleManager.onDestroy();
    }

    public void destroyEvent(RxLifecycle.Event event) {
        mRxLifecycleManager.onDestroyEvent(event);
    }

    public void destroyBgTask() {
        destroyEvent(RxLifecycle.Event.BG_TASK_DESTROY);
    }
}
