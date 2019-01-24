package com.android.net.rx.parse;

import com.google.gson.Gson;

import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;

/**
 * gson转换器
 * Created by XieDu on 2016/4/19.
 */
public class GsonConverterFactory extends Converter.Factory {

    private Gson gson;

    public GsonConverterFactory() {
        gson = new Gson();
    }

    public GsonConverterFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public <D> Converter<D, RequestBody> getRequestConverter(Class<D> clazz) {
        //return new GsonRequestConverter<>(gson.getAdapter(clazz));
        return null;
    }

    @Override
    public <R> Converter<Response, Observable<R>> getConverter(Class<R> clazz) {
        return new GsonResponseConverter<>(gson.getAdapter(clazz));
    }

//    @Override
//    public <D> Converter<Response, Observable<D>> getConverter(TypeToken<D> typeToken) {
//        return new GsonResponseConverter<>(gson,typeToken.getType());
//    }

}