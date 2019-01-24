package com.android.adapter.refresh;

import java.util.Map;

import android.content.Context;

import com.android.adapter.refresh.LoadMoreData.RequestType;
/**
 * 发送请求
 *
 * @author wangyang
 * 2014-7-29 下午2:15:26
 */
public interface RequestStack {
	/**
	 * 根据泛型Class与参数集合发送请求,得到结果后回调loadMoreData对应的方法
	 *
	 * @author wangyang
	 * 2014-7-29 下午2:15:44
	 * @param clazz         泛型Class
	 * @param params		参数集合
	 * @param loadMoreData	回调LoadMoreData
	 */
	<T>void requestData(Class<T> clazz, Map<String, Object> params,LoadMoreData<T> loadMoreData);
	/**
	 * 返回请求类型(网络/数据库)
	 *
	 * @author wangyang
	 * 2014-7-29 下午2:19:23
	 * @return
	 */
	RequestType getRequestType();
	/**
	 * 设置上下文
	 *
	 * @author wangyang
	 * 2014-8-27 下午6:18:43
	 * @param context
	 */
	void setContext(Context context);
	
}
