package com.android.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

public class Up72ViewPager extends ViewPager {

	private ViewGroup viewGroup;

	public Up72ViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Up72ViewPager(Context context) {
		super(context);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = true;
		if (viewGroup != null ) {
			float y = ev.getY();
			float height = viewGroup.getBottom();
			if (y <= height)
				result = false;
		}
		if (result)
			result = super.onInterceptTouchEvent(ev);
		return result;
	}

	public void setViewGroup(ViewGroup viewGroup) {
		this.viewGroup = viewGroup;
	}
}
