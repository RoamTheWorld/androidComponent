package com.android.adapter.refresh;

import android.content.Context;

import com.android.adapter.refresh.ListDataHolder.LoadMoreMode;
import com.android.utils.Constants;
import com.android.utils.security.SecurityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加载更多数据工具类
 * 
 * @author wangyang 2014-7-29 下午1:43:10
 * @param <T>
 */
public class LoadMoreData<T> {

	/**
	 * 数据池
	 */
	private ListDataHolder<T> holder;
	/**
	 * 请求
	 */
	private RequestStack request;
	/**
	 * 参数集合
	 */
	private Map<String, Object> params;

	protected SecurityType security;
	/**
	 * 泛型的Class
	 */
	private Class<T> clazz;
	/**
	 * 加载一次结束后的监听
	 */
	private List<OnLoadDataFinishListener> onLoadDataFinishListeners;
	/**
	 * 当需要请求更多数据前的监听(一般用于设置参数)
	 */
	private List<OnLoadMoreDataListener> onLoadMoreDataListeners;
	/**
	 * 数据变化后的监听
	 */
	protected OnDataChangeListener dataChangeListener;

	/**
	 * 
	 * @param holder
	 *            数据池
	 * @param clazz
	 *            泛型的Class
	 * @param request
	 *            请求
	 * @param params
	 *            参数集合
	 */
	public LoadMoreData(ListDataHolder<T> holder, Class<T> clazz, RequestStack request, Map<String, Object> params) {
		super();
		this.holder = holder;
		this.request = request;
		this.params = params;
		this.clazz = clazz;
		addOnLoadMoreDataListener(this.holder);
	}

	public void putAll(Map<String, Object> map) {
		if (map == null || map.size() <= 0)
			return;

		if (params == null)
			params = new HashMap<>();

		params.putAll(map);
	}

	public void put(String key, Object value) {
		if (key == null)
			return;

		if (value == null)
			value = "";

		if (params == null)
			params = new HashMap<>();

		params.put(key, value);
	}

	public void remove(String key) {
		if (params != null)
			params.remove(key);
	}

	public void clearParams() {
		if (params == null || params.size() <= 0)
			return;
		params.clear();
	}

	public Object removeHeader(String key) {
		return getBaseNetRequest().removeHeader(key);
	}

	public void removeAllHeader() {
		getBaseNetRequest().removeAllHeader();
	}

	public void addAllHeader(Map<String, String> map) {
		getBaseNetRequest().addAllHeader(map);
	}

	public void addHeader(String key, String value) {
		getBaseNetRequest().addHeader(key, value);
	}

	private BaseNetRequest getBaseNetRequest() {
		BaseNetRequest netRequest = (BaseNetRequest) request;
		netRequest.setSecurity(getSecurity());
		return netRequest;
	}

	public enum RequestType {
		SQL_LITE, NET;
		private RequestStack request;

		public RequestStack getRequest() {
			if (request == null) {
				switch (this) {
				case SQL_LITE:
					request = new SqlLiteRequest();
					break;
				case NET:
					request = new RxOkRequest();
					break;
				}
			}
			return request;
		}
	}

	/**
	 * 发送请求
	 * 
	 * @author wangyang 2014-7-29 下午2:07:04
	 * @return 返回请求类型(网络/数据库)
	 */
	public RequestType requestData() {
		if(request.getRequestType()==RequestType.NET && (params==null || params.get(Constants.REFRESH_NET_RESPONSE_KEY)==null)){
			throw new RuntimeException("please set params "+Constants.REFRESH_NET_RESPONSE_KEY);
		}

		if (dataChangeListener != null && holder.isRefresh())
			dataChangeListener.setDefaultState();
		request.requestData(clazz, params, this);
		return request.getRequestType();
	}

	/**
	 * 处理返回结果
	 * 
	 * @author wangyang 2014-7-29 下午2:07:35
	 * @param list
	 */
	@SuppressWarnings("unchecked")
	public void handleRequestSuccess(List<T> list) {
		// 得到泛型集合,添加到数据池
		holder.setLastResultSize(-1);
		
		if (holder.isRefresh() && !holder.isCache())
			holder.getDatas().clear();
		
		if (list != null && list.size() > 0) {
			// 设置本次请求结果数量
			holder.setLastResultSize(list.size());

			// 如果为刷新则清空缓存
			if (holder.isRefresh())
				holder.getDatas().clear();

			if (holder.getMode() == LoadMoreMode.Footer) {
				holder.getDatas().addAll(list);
			} else {
				Collections.reverse(list);
				holder.getDatas().addAll(0, list);
			}
		}

		for (int i = 0; i < onLoadDataFinishListeners.size(); i++)
			onLoadDataFinishListeners.get(i).OnLoadDataFinish(holder);

		// 调用监听
		if (dataChangeListener != null)
			dataChangeListener.onDataChange();

		// 解锁
		holder.setLock(null);
	}

	/**
	 * 处理错误
	 * 
	 * @author wangyang 2014-7-29 下午2:15:02
	 * @param error
	 */
	public void handleReuqestError(String error) {
		dataChangeListener.onError(error);
		for (int i = 0; i < onLoadDataFinishListeners.size(); i++)
			onLoadDataFinishListeners.get(i).OnLoadDataFinish(holder);
		holder.setLock(null);
	}

	/**
	 * 请求结束后的监听,一般用于消隐加载框
	 * 
	 * Copyright: 版权所有 (c) 2014 Company: 北京开拓明天科技有限公司
	 * 
	 * @author wangyang 2014-8-25 下午2:03:28
	 */
	public interface SetHolderDataListListener {
		void setHolderDataList(Object datas);
	}

	/**
	 * 请求结束后的监听,一般用于消隐加载框
	 * 
	 * Copyright: 版权所有 (c) 2014 Company: 北京开拓明天科技有限公司
	 * 
	 * @author wangyang 2014-8-25 下午2:03:28
	 */
	public interface OnLoadDataFinishListener {
		<T> void OnLoadDataFinish(ListDataHolder<T> dataHolder);
	}

	/**
	 * 当数据变化时候的监听
	 * 
	 * Copyright: 版权所有 (c) 2014 Company: 北京开拓明天科技有限公司
	 * 
	 * @author wangyang 2014-7-29 下午1:42:20
	 */
	protected interface OnDataChangeListener {
		public void onDataChange();

		public void onError(String error);

		public void setDefaultState();
	}

	/**
	 * 请求更多数据前的监听,一般用于设置参数,显示加载框
	 * 
	 * Copyright: 版权所有 (c) 2014 Company: 北京开拓明天科技有限公司
	 * 
	 * @author wangyang 2014-7-29 下午1:42:38
	 */
	public interface OnLoadMoreDataListener {
		<T> void onLoadMoreData(ListDataHolder<T> holder);
	}

	public void setContext(Context context) {
		request.setContext(context);
	}

	public void addOnLoadDataFinishListener(OnLoadDataFinishListener listener) {
		addOnLoadDataFinishListener(listener, 0);
	}

	public void addOnLoadDataFinishListener(OnLoadDataFinishListener listener, int position) {
		if (onLoadDataFinishListeners == null)
			onLoadDataFinishListeners = new ArrayList<>();
		onLoadDataFinishListeners.add(position, listener);
	}

	public void setOnLoadDataFinishListener(OnLoadDataFinishListener listener) {
		if (onLoadDataFinishListeners == null)
			onLoadDataFinishListeners = new ArrayList<>();
		onLoadDataFinishListeners.set(0, listener);
	}

	public void setOnLoadMoreDataListener(OnLoadMoreDataListener listener) {
		if (onLoadMoreDataListeners == null)
			onLoadMoreDataListeners = new ArrayList<>();
		onLoadMoreDataListeners.set(0, listener);
	}

	public void addOnLoadMoreDataListener(OnLoadMoreDataListener listener) {
		addOnLoadMoreDataListener(listener, 0);
	}

	public void addOnLoadMoreDataListener(OnLoadMoreDataListener listener, int position) {
		if (onLoadMoreDataListeners == null)
			onLoadMoreDataListeners = new ArrayList<>();
		onLoadMoreDataListeners.add(position, listener);
	}

	protected List<OnLoadMoreDataListener> getOnLoadMoreDataListeners() {
		return onLoadMoreDataListeners;
	}

	protected void setDataChangeListener(OnDataChangeListener dataChangeListener) {
		this.dataChangeListener = dataChangeListener;
	}

	public SecurityType getSecurity() {
		if (security == null)
			security = SecurityType.DES;
		return security;
	}

	public void setSecurity(SecurityType security) {
		this.security = security;
	}
}