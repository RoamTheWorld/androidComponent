package com.android.adapter.refresh;

import android.content.Context;

import com.android.net.rx.life.RxLifecycleManager;

/**
 * RequestStack实现类的基类
 * 
 * @author wangyang 2014-8-27 下午6:19:50
 */
public abstract class BaseRequestStack implements RequestStack {

	protected Context context;

	protected RxLifecycleManager rxLifecycleManager;

	public void setContext(Context context) {
		this.context = context;
	}

	public BaseRequestStack() {
		rxLifecycleManager = new RxLifecycleManager();
	}
}
