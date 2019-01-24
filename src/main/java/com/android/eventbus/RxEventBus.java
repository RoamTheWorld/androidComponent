package com.android.eventbus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * RX java事件总线
 * Created by wangyang on 2016/10/24 0024.
 */
public class RxEventBus {

    private static RxEventBus instance;

    public static RxEventBus getInstance() {
        if (instance == null) {
            synchronized (RxEventBus.class) {
                if (instance == null)
                    instance = new RxEventBus();
            }
        }
        return instance;
    }

    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public void post(Object o) {
        bus.onNext(o);
    }

    public Observable<Object> getBusObservable() {
        return bus;
    }

    public <T> Observable<T> getBusObservable(Class<T> clazz) {
        return bus.ofType(clazz);
    }


}
