package com.android.ui.widget.pulltorefresh;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

public class PullToRefreshGridView extends PullToRefreshBase<GridView>{

	public PullToRefreshGridView(Context context) {
		super(context);
	}

	public PullToRefreshGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
	protected GridView createPullableView(Context context) {
		GridView gv = new GridView(context);
		return gv;
	}
}
