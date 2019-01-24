package com.android.adapter.refresh;

import android.content.Context;
import android.view.View;

public abstract class Up72AdapterView<T> extends BaseAdapterView<T> {

	public Up72AdapterView(Context context) {
		super(context);
	}

	@Override
	public void bindData(T t, int position,boolean isLoadImage) {
		if (tag == null) {
			view = initView();
			setView(view);
			setTag(initViewHolder());
		}
		initData(t, position,isLoadImage);
	}

	protected View initView() {
		return View.inflate(context, getLayoutId(), null);
	}

	protected abstract int getLayoutId();

	protected abstract void initData(T t, int position, boolean isLoadImage);

	protected abstract Object initViewHolder();

}
