package com.android.ui.widget.pulltorefresh;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.ui.widget.SlideListView;
import com.android.ui.widget.pulltorefresh.PullToRefreshBase;

public class PulltoRefreshSlideListView extends PullToRefreshBase<SlideListView> {

	public PulltoRefreshSlideListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PulltoRefreshSlideListView(Context context) {
		super(context);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (isOnRefresh)
			return false;
		return super.dispatchTouchEvent(ev);
	}


	@Override
	protected PullToRefreshBase.CurrentPositionEnum judgeCurrentPosition() {
		Rect r = new Rect();
		View headerView = mPullableView.getChildAt(0);
		if (headerView != null) {
			headerView.getHitRect(r);
		}
		Rect r2 = new Rect();
		if (mPullableView.getLastVisiblePosition() == mPullableView.getCount() - 1) {
			View footerView = mPullableView.getChildAt(mPullableView.getChildCount() - 1);
			if (footerView != null) {
				footerView.getHitRect(r2);
			}
		}
		if (null != mPullableView)
			return (mPullableView.getFirstVisiblePosition() == 0 && r.top >= 0) ? CurrentPositionEnum.TOP : ((mPullableView.getLastVisiblePosition() == mPullableView.getCount() - 1 && r2.bottom <= mPullableView.getHeight()) ? CurrentPositionEnum.BOTTOM : CurrentPositionEnum.MIDDLE);
		return CurrentPositionEnum.MIDDLE;
	}

	@Override
	protected SlideListView createPullableView(Context context) {
		SlideListView lv = new SlideListView(context);
		lv.setFadingEdgeLength(0);
		lv.setCacheColorHint(0);
		// lv.setBackgroundResource(android.R.color.white);
		return lv;
	}
}
