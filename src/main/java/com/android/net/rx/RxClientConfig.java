package com.android.net.rx;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import static com.android.utils.Constants.NET_TIME_OUT;


/**
 * HTTP客户端配置类
 * Created by wangyang on 2016/3/14.
 */
public class RxClientConfig {
    private HostnameVerifier hostnameVerifier;
    private int readTimeout;
    private int writeTimeout;
    private int connectTimeout;

    public RxClientConfig() {
        connectTimeout = NET_TIME_OUT / 3;
        readTimeout = connectTimeout;
        writeTimeout = connectTimeout;
        hostnameVerifier = new DefaultBaseHostnameVerifier();
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    public class DefaultBaseHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


}
