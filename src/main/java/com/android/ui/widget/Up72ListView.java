package com.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class Up72ListView extends ListView {

	private View headView;

	public Up72ListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Up72ListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Up72ListView(Context context) {
		super(context);
	}

	@Override
	public void addHeaderView(View v) {
		this.headView = v;
		super.addHeaderView(v);
	}

	public View getHeadView() {
		return headView;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = true;
		if (headView != null) {
			float y = ev.getY();
			float height = headView.getBottom();
			if (y <= height)
				result = false;
		}
		if (result)
			result = super.onInterceptTouchEvent(ev);
		return result;
	}

}
