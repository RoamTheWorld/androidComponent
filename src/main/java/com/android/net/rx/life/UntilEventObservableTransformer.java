package com.android.net.rx.life;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by wanyang on 2016/5/23.
 */
public class UntilEventObservableTransformer<T, R> implements Observable.Transformer<T, T> {

    final Observable<R> lifecycle;
    final R event;

    public UntilEventObservableTransformer(Observable<R> lifecycle, R event) {
        this.lifecycle = lifecycle;
        this.event = event;
    }

    @Override
    public Observable<T> call(Observable<T> source) {
        return source.takeUntil(takeUntilEvent(lifecycle, event));
    }

    private <T> Observable<T> takeUntilEvent(final Observable<T> lifecycle, final T event) {
        return lifecycle.takeFirst(new Func1<T, Boolean>() {
            @Override
            public Boolean call(T lifecycleEvent) {
                return lifecycleEvent.equals(event);
            }
        });
    }
}
