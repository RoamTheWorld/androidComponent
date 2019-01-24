package com.android.net.rx.model;


import com.android.utils.Constants;

/**
 * @author yx
 * @date 2015-11-3
 */
public class GlobalConst {

    /**
     * 设置头信息上送项
     */
    public static String AGENT = "X-Android";// agent: X-ANDR
    // 终端应用标识，基于浏览器的RIA也是一种终端应用；APAD终端对应WEB渠道：APAD
    public static String VERSION = "1.0.0";// versio:终端应用版本，建议格式：“x.y”，其中x为大版本，y为小版本
    public static String DEVICE = "aPhone";// device:设备信息，用来区别终端特性，建议格式：“厂商名,产品名,型号”;如果是基于浏览器的RIA应用，则填写浏览器信息，建议格式：“厂商名,产品名,版本号”
    public static String PLATFORM = "android";// platform:操作系统信息，建议格式：“厂商名,产品名,版本号”
    public static String PLUGINS = "";// plugins:客户端支持的安全控件或插件，逗号分隔的列表，列表项由网银定义
    public static String PAGE = "";// page: 页面标识，用来确定当前请求所在的页面及位置，格式由终端应用提供商定义
    public static String LOCAL = "ZH_cn";// local:国家及语言，标准格式

    public static   int TIMEOUT = Constants.TIME_OUT;

}