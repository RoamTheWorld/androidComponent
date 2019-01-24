package com.android.net.rx.parse;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;

/**
 * Created by wangyang on 2016/4/13.
 */
public interface Converter<D, R> {
    R convert(D data) throws IOException;

    abstract class Factory {
        public abstract <D> Converter<D, RequestBody> getRequestConverter(Class<D> clazz);

        public abstract <R> Converter<Response, Observable<R>> getConverter(Class<R> clazz);
    }

    class ContentType {
        public static final MediaType FORMURLENCODED = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        public static final MediaType JSON = MediaType.parse("application/json; charset=UTF-8");

    }
}
