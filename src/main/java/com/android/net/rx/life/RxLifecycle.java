package com.android.net.rx.life;

import rx.Observable;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Created by wangyang on 2016/5/21.
 */
public class RxLifecycle {

    public enum Event {
        START,
        DESTROY,
        BG_TASK_DESTROY//后台任务destroy
    }

    public static <T, R> Observable.Transformer<T, T> bindUntilEvent(final Observable<R> lifecycle, final R event) {
        checkNotNull(lifecycle, "lifecycle == null");
        checkNotNull(event, "event == null");
        return new UntilEventObservableTransformer<>(lifecycle, event);
    }
}
