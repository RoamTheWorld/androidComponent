package com.android.net.rx.parse;

import com.google.gson.TypeAdapter;

/**
 * Created by XieDu on 2016/4/13.
 */
public class GsonRequestConverter<D> {
    private TypeAdapter<D> adapter;

    public GsonRequestConverter(TypeAdapter<D> adapter) {
        this.adapter = adapter;
    }

//    @Override
//    public RequestBody convert(D data) throws IOException {
//        String json = adapter.toJson(data);
//        return RequestBody.create(ContentType.JSON, json);
//    }
}
