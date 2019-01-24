package com.android.ui.widget.pulltorefresh;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.ui.widget.PullSeparateListView;
import com.android.ui.widget.Up72ListView;

public class PullToRefreshSeparateListView extends PullToRefreshBase<PullSeparateListView> {
	public PullToRefreshSeparateListView(Context context) {
		super(context);
	}

	public PullToRefreshSeparateListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = true;
		if (mPullableView instanceof Up72ListView) {
			View headView = ((Up72ListView) mPullableView).getHeadView();
			if (headView != null) {
				float y = ev.getY();
				float height = headView.getBottom();
				if (y <= height) {
					super.onInterceptTouchEvent(ev);
					result = false;
				}
			}
		}
		if (result)
			result = super.onInterceptTouchEvent(ev);
		return result;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (isOnRefresh)
			return false;
		mPullableView.dispatchTouchEvent(ev);
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

	@SuppressLint("ResourceAsColor")
	@Override
	protected PullSeparateListView createPullableView(Context context) {
		PullSeparateListView lv = new PullSeparateListView(context);
		lv.setFadingEdgeLength(0);
		lv.setCacheColorHint(0);
		lv.setBackgroundColor(R.color.transparent);
		return lv;
	}

}
