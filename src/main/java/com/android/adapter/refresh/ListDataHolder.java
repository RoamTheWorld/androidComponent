package com.android.adapter.refresh;

import android.content.Context;

import com.android.adapter.refresh.LoadMoreData.OnLoadDataFinishListener;
import com.android.adapter.refresh.LoadMoreData.OnLoadMoreDataListener;
import com.android.adapter.refresh.LoadMoreData.RequestType;
import com.android.master.R;
import com.android.ui.widget.LoadingDialog;
import com.android.utils.Constants;
import com.android.utils.security.SecurityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 自动加载ListView数据池(主要用于存储数据,及计算是否需要加载下一页数据)
 * 
 * @author wangyang 2014-7-29 上午11:35:16
 * @param <T>
 */
public class ListDataHolder<T> implements OnLoadMoreDataListener {

	/**
	 * 数据集合
	 */
	private List<T> datas = new ArrayList<>();
	/**
	 * 页码
	 */
	private int pageIndex = Constants.PAGEINDEX;
	/**
	 * 
	 */
	private Context context;
	/**
	 * 每页的数量
	 */
	private int pageSize = Constants.PAGESIZE;

	/**
	 * 上一次请求结果数量
	 */
	private int lastResultSize;
	/**
	 * 加载更多工具类(用于请求及获取数据)
	 */
	protected LoadMoreData<T> moreData;
	/**
	 * 用当前请求类型为锁,防止并发请求
	 */
	private RequestType lock = null;
	/**
	 * 加载模式(下拉加载,上推加载)
	 */
	private LoadMoreMode mode;
	/**
	 * 是否为刷新操作
	 */
	private boolean isRefresh;

	private boolean isCache;

	/**
	 * 根据泛型的Class 请求Request,参数集合 获取ListDataHolder
	 * 
	 * @author wangyang 2014-7-29 上午11:49:27
	 * @param clazz
	 *            <T> 泛型的Class
	 * @param request
	 *            请求工具类
	 * @param params
	 *            参数集合
	 *
	 * @param mode
	 * @return ListDataHolder<T>
	 *            加载模式
	 */
	public static <T> ListDataHolder<T> fromListDataHolder(Class<T> clazz, RequestStack request, Map<String, Object> params, LoadMoreMode mode) {
		return new ListDataHolder<>(clazz, request, params, mode);
	}

	/**
	 * 根据泛型的Class 请求Request,参数集合 获取ListDataHolder
	 * 
	 * @author wangyang 2014-7-29 上午11:49:27
	 * @param clazz
	 *            <T> 泛型的Class
	 * @param requestType
	 *            请求类型
	 * @param params
	 *            参数集合
	 * @return ListDataHolder<T>
	 * @param mode
	 *            加载模式
	 */
	public static <T> ListDataHolder<T> fromListDataHolder(Class<T> clazz, RequestType requestType, Map<String, Object> params, LoadMoreMode mode) {
		return new ListDataHolder<>(clazz, requestType.getRequest(), params, mode);
	}

	/**
	 * 根据泛型的Class 请求Request 获取ListDataHolder
	 * 
	 * @author wangyang 2014-7-29 上午11:49:32
	 * @param clazz
	 *            <T> 泛型的Class
	 * @param request
	 *            请求工具类
	 * @return ListDataHolder<T>
	 * @param mode
	 *            加载模式
	 */
	public static <T> ListDataHolder<T> fromListDataHolder(Class<T> clazz, RequestStack request, LoadMoreMode mode) {
		return fromListDataHolder(clazz, request, null, mode);
	}

	/**
	 * 根据泛型的Class,参数集合,默认使用网络请求Request 获取ListDataHolder
	 * 
	 * @author wangyang 2014-7-29 上午11:49:36
	 * @param clazz
	 *            <T> 泛型的Class
	 * @param params
	 *            参数集合
	 * @param mode
	 *            加载模式
	 * @return ListDataHolder<T>
	 */
	public static <T> ListDataHolder<T> fromListDataHolder(Class<T> clazz, Map<String, Object> params, LoadMoreMode mode) {
		return fromListDataHolder(clazz, new RxOkRequest(), params, mode);
	}

	/**
	 * 根据泛型的Class,参数集合,默认使用网络请求Request 获取ListDataHolder
	 * 
	 * @author wangyang 2014-7-29 下午5:49:24
	 * @param clazz
	 * @param params
	 * @return
	 */
	public static <T> ListDataHolder<T> fromListDataHolder(Class<T> clazz, Map<String, Object> params) {
		return fromListDataHolder(clazz, new RxOkRequest(), params, null);
	}

	/**
	 * 根据泛型的Class,网络请求Request 获取ListDataHolder
	 * 
	 * @author wangyang 2014-7-29 下午5:50:36
	 * @param clazz
	 * @param request
	 * @return
	 */
	public static <T> ListDataHolder<T> fromListDataHolder(Class<T> clazz, RequestStack request) {
		return fromListDataHolder(clazz, request, null, null);
	}

	/**
	 * 根据泛型的Class,网络请求Request 获取ListDataHolder
	 * 
	 * @author wangyang 2014-7-29 下午5:50:36
	 * @param clazz
	 * @param requestType
	 * @return
	 */
	public static <T> ListDataHolder<T> fromListDataHolder(Class<T> clazz, RequestType requestType) {
		return fromListDataHolder(clazz, requestType.getRequest(), null, null);
	}

	/**
	 * 根据泛型的Class,默认使用网络请求Request 获取ListDataHolder
	 * 
	 * @author wangyang 2014-7-29 上午11:49:42
	 * @param clazz
	 *            <T> 泛型的Class
	 * @param mode
	 *            加载模式
	 * @return ListDataHolder<T>
	 */
	public static <T> ListDataHolder<T> fromListDataHolder(Class<T> clazz, LoadMoreMode mode) {
		return fromListDataHolder(clazz, RequestType.NET.getRequest(), null, mode);
	}

	/**
	 * 根据泛型的Class,默认使用网络请求Request 获取ListDataHolder
	 * 
	 * @author wangyang 2014-7-29 上午11:49:42
	 * @param clazz
	 *            <T> 泛型的Class
	 * @return ListDataHolder<T>
	 */
	public static <T> ListDataHolder<T> fromListDataHolder(Class<T> clazz) {
		return fromListDataHolder(clazz, RequestType.NET.getRequest(), null, null);
	}

	/**
	 * 
	 * @param clazz
	 *            <T> 泛型的Class
	 * @param request
	 *            请求工具类
	 * @param params
	 *            参数集合
	 * @param mode
	 *            加载模式
	 */
	private ListDataHolder(Class<T> clazz, RequestStack request, Map<String, Object> params, LoadMoreMode mode) {
		if (clazz == null) {
			throw new RuntimeException("clazz can't not be null");
		}

		if (request == null)
			request = RequestType.NET.getRequest();

		if (request == null) {
			throw new RuntimeException("request can't not be null");
		}

		if (mode == null)
			mode = LoadMoreMode.Footer;

		moreData = new LoadMoreData<>(this, clazz, request, params);
		this.mode = mode;

	}

	public enum LoadMoreMode {
		Header, Footer
	}

	/**
	 * 获取数据集的长度
	 * 
	 * @author wangyang 2014-7-29 上午11:55:19
	 * @return
	 */
	public int getDatasCount() {
		if (datas != null) {
			return datas.size();
		}
		return 0;
	}

	/**
	 * 根据当前位置获取对应Item对象
	 * 
	 * @author wangyang 2014-7-29 上午11:56:07
	 * @param index
	 *            当前序列
	 * @return 当前序列对应的item数据对象
	 */
	public final T obtainItem(int index) {
		if (index < 0) {
			throw new IllegalArgumentException("index不能小于0");
		}
		T currentItem = null;

		// 跳过当前index大于等于数据集合长度
		if (index < getDatasCount()) {
			// 获取当前对应Item
			currentItem = datas.get(index);

			// 判断当前index是否为设置的需要自动加载的位置
			int keyIndex = 0;
			if (mode == LoadMoreMode.Footer) {
				if (getDatasCount() - index <= Constants.AUTO_LOAD_MORE_AT_POSITION) {
					keyIndex = getDatasCount();
				}
			} else {
				if (index <= Constants.AUTO_LOAD_MORE_AT_POSITION) {
					keyIndex = getDatasCount();
				}
			}
			// 如果当前数据为空,则视为没有更多数据,直接返回
			if (currentItem == null) {
				keyIndex = -1;
			}

			// 如果keyIndex大于0,则请求下一页数据
			if (keyIndex > 0) {
				askforNextPageRequest(keyIndex);
			}
		}
		return currentItem;
	}

	/**
	 * 根据keyIndex判断是否申请进行下一页的请求
	 * 
	 * @author wangyang 2014-7-29 下午1:37:14
	 * @param keyIndex
	 */
	private void askforNextPageRequest(int keyIndex) {
		if (pageSize == 0) {
			throw new RuntimeException("pageSize can not be 0!");
		}
		if (lock != null || keyIndex % pageSize != 0 || lastResultSize < pageSize) {
			return;
		}
		loadMore();
	}

	/**
	 * 发送请求
	 * 
	 * @author wangyang 2014-7-29 下午1:37:24
	 */
	private void sendRequest() {
		// 计算pageIndex
		calcIndex();

		// 调用请求前的监听
		if (moreData.getOnLoadMoreDataListeners() != null && moreData.getOnLoadMoreDataListeners().size() > 0) {
			for (OnLoadMoreDataListener listener : moreData.getOnLoadMoreDataListeners())
				listener.onLoadMoreData(this);
		}

		// 设置参数
		moreData.put(Constants.PAGE_SIZE, pageSize);
		moreData.put(Constants.PAGE_INDEX, pageIndex);

		// 发送请求
		lock = moreData.requestData();
	}

	public void putAll(Map<String, Object> map) {
		moreData.putAll(map);
	}

	public void put(String key, Object value) {
		moreData.put(key, value);
	}

	public void remove(String key) {
		moreData.remove(key);
	}

	public void clearParams() {
		moreData.clearParams();
	}

	public Object removeHeader(String key) {
		return moreData.removeHeader(key);
	}

	public void removeAllHeader() {
		moreData.removeAllHeader();
	}

	public void addAllHeader(Map<String, String> map) {
		moreData.addAllHeader(map);
	}

	public void addHeader(String key, String value) {
		moreData.addHeader(key, value);
	}

	/**
	 * 获取数据
	 * 
	 * @author wangyang 2014-7-29 下午1:39:54
	 */
	public void loadMore() {
		this.isRefresh = false;
		sendRequest();
	}

	/**
	 * 清空已经数据,重新请求
	 * 
	 * @author wangyang 2014-7-29 下午1:39:51
	 */
	public void refresh(boolean isCache) {
		this.isCache = isCache;
		this.isRefresh = true;
		pageIndex = Constants.PAGEINDEX;
		sendRequest();
	}

	/**
	 * 清空已经数据,重新请求
	 * 
	 * @author wangyang 2014-7-29 下午1:39:51
	 */
	public void refresh() {
		refresh(false);
	}

	/**
	 * 计算pageIndex
	 * 
	 * @author wangyang 2014-7-29 下午1:40:19
	 */
	private void calcIndex() {
		if (datas != null && datas.size() > 0 && !isRefresh) {
			pageIndex = datas.size() / pageSize + 1;
		}
	}

	@Override
	public <E> void onLoadMoreData(ListDataHolder<E> holder) {
		LoadingDialog.showLoadingDialog(context, context.getString(R.string.adapter_loading));
	}

	public void setContext(Context context) {
		moreData.setContext(context);
		this.context = context;
	}

	public void addOnLoadDataFinishListener(OnLoadDataFinishListener listener) {
		moreData.addOnLoadDataFinishListener(listener);
	}

	public void addOnLoadDataFinishListener(OnLoadDataFinishListener listener, int position) {
		moreData.addOnLoadDataFinishListener(listener, position);
	}

	public void setOnLoadDataFinishListener(OnLoadDataFinishListener listener) {
		moreData.setOnLoadDataFinishListener(listener);
	}

	public void setOnLoadMoreDataListener(OnLoadMoreDataListener listener) {
		moreData.setOnLoadMoreDataListener(listener);
	}

	public void addOnLoadMoreDataListener(OnLoadMoreDataListener listener) {
		moreData.addOnLoadMoreDataListener(listener);
	}

	public void addOnLoadMoreDataListener(OnLoadMoreDataListener listener, int position) {
		moreData.addOnLoadMoreDataListener(listener, position);
	}

	public int getPageSize() {
		return pageSize;
	}

	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		if (datas == null)
			return;
		this.datas = datas;

		if (this.datas.size() > 0 && getMode() == LoadMoreMode.Header)
			Collections.reverse(this.datas);

		calcLastResultSize(datas);
	}

	public void setDatasNotify(List<T> datas) {
		setDatas(datas);
		if (datas != null)
			notifyDataSetChanged();
	}

	private void onDataChange() {
		if (moreData.dataChangeListener != null) {
			moreData.dataChangeListener.setDefaultState();
			moreData.dataChangeListener.onDataChange();
		}
	}

	private void calcLastResultSize(List<T> datas) {
		this.lastResultSize = -1;
		if (datas.size() > 0) {
			pageIndex = datas.size() / pageSize;
			this.lastResultSize = pageSize;
			if (datas.size() % pageSize > 0) {
				pageIndex += 1;
				this.lastResultSize = datas.size() % pageSize;
			}
		}
	}

	public void addDataNotify(T t) {
		datas.remove(t);
		if (mode == LoadMoreMode.Footer)
			datas.add(0, t);
		else
			datas.add(t);
		notifyDataSetChanged();
	}

	public void addData(T t) {
		datas.remove(t);
		if (mode == LoadMoreMode.Footer)
			datas.add(0, t);
		else
			datas.add(t);
		calcLastResultSize(datas);
	}

	public void setData(T t) {
		int index = datas.indexOf(t);
		if (index < 0)
			return;
		datas.set(index, t);
	}

	public void setDataNotify(T t) {
		int index = datas.indexOf(t);
		if (index < 0)
			return;
		datas.set(index, t);
		notifyDataSetChanged();
	}

	public void removeDataNotify(T t) {
		datas.remove(t);
		notifyDataSetChanged();
	}

	public void removeData(T t) {
		datas.remove(t);
		calcLastResultSize(datas);
	}

	public void notifyDataSetChanged() {
		calcLastResultSize(datas);
		onDataChange();
	}

	public void setLock(RequestType lock) {
		this.lock = lock;
	}

	public LoadMoreMode getMode() {
		return mode;
	}

	public void setMode(LoadMoreMode mode) {
		this.mode = mode;
	}

	public boolean isRefresh() {
		return isRefresh;
	}

	public int getLastResultSize() {
		return lastResultSize;
	}

	public void setLastResultSize(int lastResultSize) {
		this.lastResultSize = lastResultSize;
	}

	public boolean isCache() {
		return isCache;
	}

	public ListDataHolder<T> withSecurity(SecurityType securityType){
		moreData.setSecurity(securityType);
		return this;
	}
}
