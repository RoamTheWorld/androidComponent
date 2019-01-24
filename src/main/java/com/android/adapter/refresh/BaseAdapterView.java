package com.android.adapter.refresh;

import android.content.Context;
import android.view.View;

/**
 * 自动加载Listview的Item 基类
 *
 * @author wangyang
 * 2014-7-29 上午11:29:19
 * @param <T>
 */
public abstract class BaseAdapterView<T> {

	/**
	 * 上下文
	 */
	protected Context context;

	/**
	 * 实际Item的view
	 */
	protected View view;

	/**
	 * 一般用于存储ViewHolder
	 */
	protected Object tag;

	protected ListDataHolder<T> dataHolder;

	public BaseAdapterView(Context context) {
		this.context = context;
	}

	/**
	 * 根据泛型数据绑定控件(getView方法里的业务逻辑)
	 *
	 * @author wangyang
	 * 2014-7-29 上午11:32:23
	 * @param t
	 * @param position 
	 */
	public abstract void bindData(T t, int position,boolean isLoadImage);

	public View getView() {
		return view;
	}

	public Context getContext() {
		return context;
	}

	public Object getTag() {
		return tag;
	}

	public void setView(View view) {
		this.view = view;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public void setHolder(ListDataHolder<T> holder) {
		this.dataHolder = holder;
	}
}
