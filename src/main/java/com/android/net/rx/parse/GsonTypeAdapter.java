package com.android.net.rx.parse;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * 作者：wangyang
 * 创建时间：2016/10/9 19:54
 * 描述：使得子类兼具JsonSerializer、JsonDeserializer两接口功能。
 */
public abstract class GsonTypeAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
}
