package com.android.net.rx;


import com.android.net.rx.parse.Converter;
import com.android.utils.Constants;
import com.android.utils.security.SecurityType;

import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 设置请求参数
 */
public class RequestInfo {
    private HttpUrl url;
    private Method method;
    /**
     * 上传的内容，可以是普通文本，也可以是json转成的文本
     */
    private String content;
    /**
     * 上传的MIME类型
     */
    private MediaType contentType;
    private HttpUrl.Builder urlBuilder;
    private MultipartBody.Builder multipartBuilder;
    private FormBody.Builder formBuilder;
    private Request.Builder requestBuilder;
    private RequestBody body;
    private SecurityType securityType;

    private Converter.Factory converterFactory;


    public RequestInfo(BaseHttpClient baseHttpClient, SecurityType securityType) {
        requestBuilder = new Request.Builder();
        converterFactory = baseHttpClient.getConverterFactory();
        this.securityType = securityType;
        if (securityType != null)
            requestBuilder.addHeader(Constants.HEADER_ENCRYPT_KEY, securityType.getEncryptKey());
    }

    public RequestInfo url(String url) {
        if (url == null || (this.url = HttpUrl.parse(url)) == null)
            throw new IllegalArgumentException("Illegal URL: " + url);
        urlBuilder = this.url.newBuilder();
        return this;
    }


    public RequestInfo method(Method method) {
        this.method = method;
        return this;
    }


    public RequestInfo addHeader(String key, String value) {
        if (securityType != null) {
            try {
                value = securityType.getSecurityString(value);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        requestBuilder.addHeader(key, value);
        return this;
    }

    public RequestInfo addHeader(Map<String, ? extends Object> header) {
        for (Map.Entry<String, ? extends Object> entry : header.entrySet())
            addHeader(entry.getKey(), entry.getValue().toString());
        return this;
    }

    public RequestInfo content(MediaType contentType, String content) {
        this.contentType = contentType;
        this.content = content;
        body = RequestBody.create(contentType, content);
        return this;
    }

    public RequestInfo addQueryParam(String name, String value) {
        if (securityType != null) {
            try {
                value = securityType.getSecurityString(value);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        urlBuilder.addQueryParameter(name, value);
        return this;
    }

    /**
     * @param name
     * @param value
     * @param encoded 参数是否已经预编码过了
     * @return
     */
    public RequestInfo addFormParam(String name, String value, boolean encoded) {
        if (securityType != null) {
            try {
                value = securityType.getSecurityString(value);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }

        if (formBuilder == null)
            formBuilder = new FormBody.Builder();
        if (encoded)
            formBuilder.addEncoded(name, value);
        else
            formBuilder.add(name, value);
        return this;
    }

    /**
     * @param name
     * @param value
     * @return
     */
    public RequestInfo addFormParam(String name, String value) {
        return addFormParam(name, value, false);
    }

    public RequestInfo addFormParam(Map<String, ? extends Object> header) {
        for (Map.Entry<String, ? extends Object> entry : header.entrySet())
            addFormParam(entry.getKey(), entry.getValue().toString());
        return this;
    }

    /**
     * 添加要上传的文件
     *
     * @param file
     * @return
     */
    public RequestInfo addMultiPart(File file) {
        if (file == null || !file.exists() || file.length() == 0) {
            throw new IllegalArgumentException("Illegal File:" + file);
        }
        getMultipartBuilder();
        RequestBody requestBody = RequestBody.create(MediaType.parse(RxUtils.guessMimeType(file.getPath())), file);
        multipartBuilder.addFormDataPart("file", file.getName(), requestBody);
        return this;
    }

    /**
     * 添加要上传的文件列表
     *
     * @param files
     * @return
     */
    public RequestInfo addMultiPart(List<File> files) {
        for (File file : files) {
            addMultiPart(file);
        }
        return this;
    }

    public RequestInfo addMultiPart(String name, String value) {
        if (securityType != null) {
            try {
                value = securityType.getSecurityString(value);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }

        getMultipartBuilder();
        multipartBuilder.addFormDataPart(name, value);
        return this;
    }

    public RequestInfo addMultiPart(Map<String, ? extends Object> params) {
        for (Map.Entry<String, ? extends Object> entry : params.entrySet())
            addMultiPart(entry.getKey(), entry.getValue().toString());
        return this;
    }

    private void getMultipartBuilder() {
        if (multipartBuilder == null) {
            multipartBuilder = new MultipartBody.Builder();
            multipartBuilder.setType(MultipartBody.FORM);
        }
    }


    /**
     * 根据不同的请求方式和参数,生成Request对象
     */
    public Request generateRequest() {
        if (url == null)
            throw new IllegalArgumentException("Illegal URL: " + url);
        if (method == null) {
            method = Method.GET;
        }
        switch (method) {
            case GET:
            case DELETE:
            case HEAD:
                url = urlBuilder.build();
                break;
            case POST:
            case PUT:
            case PATCH:
                if (body == null) {
                    if (formBuilder != null) {
                        body = formBuilder.build();
                    } else if (multipartBuilder != null) {
                        body = multipartBuilder.build();
                    }
                }
                break;
        }
        return requestBuilder
                .url(url)
                .method(method.name(), body)
                .build();
    }

    public RequestInfo setConverterFactory(Converter.Factory converterFactory) {
        this.converterFactory = converterFactory;
        return this;
    }

    public enum Method {
        GET, POST, PUT, DELETE, HEAD, PATCH,
    }

    @Override
    public String toString() {
        return "RequestInfo{" +
                "url=" + url +
                ", method=" + method +
                ", content='" + content + '\'' +
                ", contentType=" + contentType +
                ", urlBuilder=" + urlBuilder +
                ", multipartBuilder=" + multipartBuilder +
                ", formBuilder=" + formBuilder +
                ", requestBuilder=" + requestBuilder +
                ", body=" + body +
                ", securityType=" + securityType +
                ", converterFactory=" + converterFactory +
                '}';
    }
}