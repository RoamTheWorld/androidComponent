package com.android.net.rx.parse;

import com.android.net.rx.exception.HttpException;
import com.android.utils.Log;
import com.google.gson.TypeAdapter;

import java.io.IOException;

import okhttp3.Response;
import rx.Observable;


/**
 * Created by wangyang on 2016/4/19.
 */
public class GsonResponseConverter<T> implements Converter<Response, Observable<T>> {
    private final TypeAdapter<T> adapter;

    GsonResponseConverter(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public Observable<T> convert(Response response) throws IOException {
        try {
            String result = response.body().string();
            Log.i("response:" + result);
            T data = adapter.fromJson(result);
            return Observable.just(data);
        } catch (Exception e) {
            Log.d(e.getMessage());
            HttpException exception = new HttpException(e.getMessage());
            exception.setType(HttpException.ExceptionType.NET_WORK);
            return Observable.error(exception);
        }
    }
}
