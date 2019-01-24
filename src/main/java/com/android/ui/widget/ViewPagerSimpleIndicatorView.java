package com.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.android.master.R;

/**
 * @description
 * 
 * @author 谭杰 E-mail: tanjie9012@163.com
 * @create 2014-2-21 上午11:40:27
 * @version 1.0.0
 * @company 北京开拓明天科技有限公司 Copyright: 版权所有 (c) 2014
 */
public class ViewPagerSimpleIndicatorView extends LinearLayout {
	private Context context;

	public ViewPagerSimpleIndicatorView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public ViewPagerSimpleIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {
		setOrientation(LinearLayout.HORIZONTAL);
	}

	/**
	 * 
	 * @param count
	 */
	public void setSize(int count) {
		if (count <= 0) {
			return;
		}
		removeAllViews();
		for (int i = 0; i < count; i++) {
			View v = new View(context);
			int bigSize = (int) context.getResources().getDimension(R.dimen.viewpager_circle_size);
			LayoutParams lp = new LayoutParams(bigSize, bigSize);
			if (i == 0) {
				v.setBackgroundResource(R.drawable.drawable_viewpager_circle_focused);
			} else {
				v.setBackgroundResource(R.drawable.drawable_viewpager_circle_normal);
			}
			lp.setMargins(4, 4, 4, 4);
			v.setLayoutParams(lp);
			addView(v);
		}
		invalidate();
	}

	/**
	 * 
	 * @param curItem
	 */
	public void setCurItem(int curItem) {
		if (curItem < 0) {
			curItem = 0;
		}
		final int childCount = getChildCount();
		if (curItem > childCount - 1) {
			curItem = childCount - 1;
		}
		for (int i = 0; i < childCount; i++) {
			if (curItem == i) {
				getChildAt(i).setBackgroundResource(R.drawable.drawable_viewpager_circle_focused);
			} else {
				getChildAt(i).setBackgroundResource(R.drawable.drawable_viewpager_circle_normal);
			}
		}
	}
}
