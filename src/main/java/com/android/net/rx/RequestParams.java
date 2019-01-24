package com.android.net.rx;



import com.android.utils.StringUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangyang on 2016/3/7.
 */
public class RequestParams {
    protected ConcurrentHashMap<String, String> headers;

    protected ConcurrentHashMap<String, String> urlParams;


    public RequestParams() {
        headers = new ConcurrentHashMap<>();
        urlParams = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(ConcurrentHashMap<String, String> headers) {
        this.headers = headers;
    }

    public ConcurrentHashMap<String, String> getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(ConcurrentHashMap<String, String> urlParams) {
        this.urlParams = urlParams;
    }

    public void putHeader(String key, String value) {
        if (value == null) {
            value = "";
        }

        if (!StringUtil.isEmpty(key)) {
            headers.put(key, value);
        }
    }

    public void putAllHeaders(Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            urlParams.putAll(headers);
        }
    }

    public void putUrlParam(String key, String value) {
        if (value == null) {
            value = "";
        }

        if (!StringUtil.isEmpty(key)) {
            urlParams.put(key, value);
        }
    }

    public void putAllUrlParams(Map<String, String> urlParams) {
        if (urlParams != null && !urlParams.isEmpty()) {
            urlParams.putAll(urlParams);
        }
    }

    public void clear() {
        headers.clear();
        urlParams.clear();
    }

}
