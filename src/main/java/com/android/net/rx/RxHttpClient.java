package com.android.net.rx;

import com.android.net.parse.NetResponse;
import com.android.net.rx.model.GlobalConst;
import com.android.net.rx.parse.BeanParser;
import com.android.net.rx.parse.GsonConverterFactory;
import com.android.net.rx.parse.GsonUtils;
import com.android.utils.Log;
import com.android.utils.security.SecurityType;
import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import rx.Observable;

/**
 * 处理网络请求
 * Created by wangyang on 2016/3/8.
 */
public enum RxHttpClient {

    instance;//单例对象

    // gson解析用
    private Gson gson;
    private BaseHttpClient httpClient;
    //加密类型
    private SecurityType securityType;

    /**
     * 构造函数
     */
    RxHttpClient() {
        gson = GsonUtils.getGson();
        httpClient = getHttpClient();
    }

    public <T> Observable<String> post(String url, T params) {
        Log.i("当前请求接口=====" + url);
        return httpClient.executeString(generateRequestInfo(url, params, null, false));
    }

    public <T> Observable<String> post(String url, T params, Map<String, ? extends Object> header) {
        Log.i("当前请求接口=====" + url);
        return httpClient.executeString(generateRequestInfo(url, params, header, false));
    }

    public <T, R> Observable<R> post(String url, T params, Class<? extends NetResponse<R>> clazz) {
        return post(url, params, clazz, null);
    }

    public <T, R> Observable<R> post(String url, T params, Class<? extends NetResponse<R>> clazz, Map<String, ? extends Object> header) {
        Log.i("当前请求接口=====" + url);
        return httpClient.execute(generateRequestInfo(url, params, header, false), clazz).flatMap(new BeanParser<R>());
    }

    public <R> Observable<R> postFile(String url, File file, Class<? extends NetResponse<R>> clazz) {
        return postFile(url, null, file, clazz, null);
    }

    public <T, R> Observable<R> postFile(String url, T params, File file, Class<? extends NetResponse<R>> clazz) {
        return postFile(url, params, file, clazz, null);
    }

    public <T, R> Observable<R> postFile(String url, T params, File file, Class<? extends NetResponse<R>> clazz, Map<String, ? extends Object> header) {
        Log.i("当前请求接口=====" + url);
        return httpClient.execute(generateRequestInfo(url, params, header, true).addMultiPart(file), clazz).flatMap(new BeanParser<R>());
    }

    public <R> Observable<R> postFile(String url, List<File> files, Class<? extends NetResponse<R>> clazz) {
        return postFile(url, null, files, clazz, null);
    }

    public <T, R> Observable<R> postFile(String url, T params, List<File> files, Class<? extends NetResponse<R>> clazz) {
        return postFile(url, params, files, clazz, null);
    }

    public <T, R> Observable<R> postFile(String url, T params, List<File> files, Class<? extends NetResponse<R>> clazz, Map<String, ? extends Object> header) {
        Log.i("当前请求接口=====" + url);
        return httpClient.execute(generateRequestInfo(url, params, header, true).addMultiPart(files), clazz).flatMap(new BeanParser<R>());
    }

    private <T> RequestInfo generateRequestInfo(String url, T params, Map<String, ? extends Object> header, boolean isMultiPart) {
        RequestInfo postRequest = new RequestInfo(httpClient, securityType).url(url).method(RequestInfo.Method.POST);

        if (header == null)
            postRequest = generateDefaultHeader(postRequest);
        else
            postRequest.addHeader(header);

        if (params != null) {
            if (params instanceof Map) {
                if (isMultiPart)
                    postRequest.addMultiPart((Map<String, Object>) params);
                else
                    postRequest.addFormParam((Map<String, Object>) params);
            } else {
                postRequest.addHeader("Content-type", "application/json;charset=UTF-8");
                Log.v("-", postRequest.toString());
                String requestString;
                if (isMultiPart)
                    requestString = addPrams(postRequest, params, isMultiPart);
                else
                    requestString = addPrams(postRequest, params, isMultiPart);
                Log.i("request " + requestString);
            }
        }

        if (isMultiPart)
            postRequest.addHeader("Content-type", "multipart/form-data");

        return postRequest;
    }

    private <T> String addPrams(RequestInfo postRequest, T params, boolean isMultiPart) {
        if (params instanceof String) {
            MediaType json = MediaType.parse("application/json;charset=UTF-8");
            postRequest.content(json, params.toString());
        } else {
            if (isMultiPart)
                postRequest.addMultiPart("params", GsonUtils.getGson(gson, params.getClass()).toJson(params));
            else
                postRequest.addFormParam("params", GsonUtils.getGson(gson, params.getClass()).toJson(params));
        }
        return GsonUtils.getGson(gson, params.getClass()).toJson(params);
    }

    private RequestInfo generateDefaultHeader(RequestInfo requestInfo) {
        requestInfo.addHeader("Connection", "Keep-Alive");
        requestInfo.addHeader("User-Agent", "X-Android|1.5.24");
        requestInfo.addHeader("Accept-Encoding", "zh-cn");
        requestInfo.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        Log.v("--", requestInfo.toString());
        return requestInfo;
    }

    public BaseHttpClient getHttpClient() {
        if (null == httpClient) {

            httpClient =
                    new BaseHttpClient.HttpClientBuilder().setConnectTimeout(GlobalConst.TIMEOUT)
                            .setReadTimeout(GlobalConst.TIMEOUT)
                            .setWriteTimeout(GlobalConst.TIMEOUT)
                            .setConverterFactory(new GsonConverterFactory(gson))
                            .build();
        }
        return httpClient;
    }

    /**
     * 保存cookies
     */
    public void saveCookies(HashMap<String, HashMap<String, String>> cookiesMap) {
        httpClient.saveCookies(cookiesMap);
    }

    /**
     * 清楚cookies
     */
    public void clearCookies() {
        httpClient.clearCookies();
    }

    /**
     * 获取cookies
     */
    public CookieStore getCookies() {
        return httpClient.getCookies();
    }

    public RxHttpClient withSecurityType(SecurityType securityType) {
        this.securityType = securityType;
        return instance;
    }
}
