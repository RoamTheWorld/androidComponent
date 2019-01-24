package com.android.ui.widget.pulltorefresh;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.android.ui.widget.pulltorefresh.FlipLoadingLayout.FlipLoadingLayoutMode;

public abstract class PullToRefreshBase<T extends View> extends LinearLayout {

	public PullToRefreshBase(Context context) {
		super(context);
		init(context);
	}

	public PullToRefreshBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public enum RefreshStatusEnum {
		/** 点击刷新 **/
		CLICK_TO_REFRESH,
		/** 拖拽刷新 **/
		PULL_TO_REFRESH,
		/** 释放刷新 **/
		RELEASE_TO_REFRESH,
		/** 刷新中 **/
		REFRESHING;
	}

	public enum PullModeEnum {
		PULL_DOWN_TO_REFRESH, PULL_UP_TO_REFRESH, BOTH, BOTH_NONE;
		boolean canPullDown() {
			return this == PULL_DOWN_TO_REFRESH || this == BOTH;
		}

		boolean canPullUp() {
			return this == PULL_UP_TO_REFRESH || this == BOTH;
		}
	}

	public enum CurrentPositionEnum {
		TOP, BOTTOM, MIDDLE;
	}

	protected final float DEFAULT_RESISTANCE = 2f;
	protected final int DEFAULT_HEIGHT_LIMIT = 10;
	protected final int SMOOTH_SCROLL_DURATION_MS = 200;
	protected final int SMOOTH_SCROLL_LONG_DURATION_MS = 325;
	protected final PullModeEnum DEFAULT_MODE = PullModeEnum.BOTH_NONE;
	protected final RefreshStatusEnum DEFAULT_STATE = RefreshStatusEnum.CLICK_TO_REFRESH;

	protected float mResistance = DEFAULT_RESISTANCE;
	protected int mHeightLimit = DEFAULT_HEIGHT_LIMIT;
	protected PullModeEnum mCurrentMode = DEFAULT_MODE;
	protected RefreshStatusEnum mCurrentState = DEFAULT_STATE;

	boolean isReadyForPullDown = false;
	boolean isReadyForPullUp = false;
	protected boolean isOnRefresh = false;

	private OnRefreshHeaderListener mOnRefreshHeaderListener;
	private OnRefreshFooterListener mOnRefreshFooterListener;

	protected CurrentPositionEnum mCurrentPosition;
	protected FlipLoadingLayout header;
	protected FlipLoadingLayout footer;
	protected float mActionDownPointY;
	protected T mPullableView;
	protected Rect initPadding;

	private Interpolator mScrollAnimationInterpolator;
	private SmoothScrollRunnable mCurrentSmoothScrollRunnable;

	public CurrentPositionEnum getmCurrentPosition() {
		return mCurrentPosition;
	}

	public void setmCurrentPosition(CurrentPositionEnum mCurrentPosition) {
		this.mCurrentPosition = mCurrentPosition;
	}

	public final float getResistance() {
		return mResistance;
	}

	public final void setResistance(float mResistance) {
		this.mResistance = mResistance;
	}

	public final int getHeightLimit() {
		return mHeightLimit;
	}

	public final void setHeightLimit(int mHeightLimit) {
		this.mHeightLimit = mHeightLimit;
	}

	public final PullModeEnum getCurrentMode() {
		return mCurrentMode;
	}

	public final void setCurrentMode(PullModeEnum mCurrentMode) {
		this.mCurrentMode = mCurrentMode;
		updateUIForMode(getContext());
	}

	public final RefreshStatusEnum getCurrentState() {
		return mCurrentState;
	}

	public final void setCurrentState(RefreshStatusEnum mCurrentState) {
		this.mCurrentState = mCurrentState;
	}

	public interface OnRefreshHeaderListener {
		public void onRefreshHeader();
	}

	public interface OnRefreshFooterListener {
		public void onRefreshFooter();
	}

	public final FlipLoadingLayout getHeader() {
		if (null == header)
			return null;
		return header;
	}

	public final int getHeaderOriginalHeight() {
		if (null == header)
			return 0;
		return header.mOriginalHeight;
	}

	public final FlipLoadingLayout getFooter() {
		if (null == footer)
			return null;
		return footer;
	}

	public final int getFooterOriginalHeight() {
		if (null == footer)
			return 0;
		return footer.mOriginalHeight;
	}

	/**
	 * 点击刷新View时调用<br/>
	 * 主要在list仅有少量items，无法下拉刷新只能手动点击刷新View时调用
	 */
	protected final class OnClickFlipLoadingLayoutRefreshListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v instanceof FlipLoadingLayout) {
				FlipLoadingLayout flip = (FlipLoadingLayout) v;
				if (FlipLoadingLayoutMode.HEADER == flip.getMode() && RefreshStatusEnum.REFRESHING != mCurrentState) {
					onRefreshHeader();
				}
				if (FlipLoadingLayoutMode.FOOTER == flip.getMode() && RefreshStatusEnum.REFRESHING != mCurrentState) {
					onRefreshFooter();
				}
			}
		}
	}

	VelocityTracker vt = VelocityTracker.obtain();

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		vt.addMovement(ev);
		vt.computeCurrentVelocity(1, (float) 100.0);
		mCurrentPosition = judgeCurrentPosition();
		gestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean handle = false;
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mActionDownPointY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			switch (mCurrentPosition) {
			case TOP:
				isReadyForPullDown = true;
				isReadyForPullUp = false;
				if (gesture == GestureSlideExt.GESTURE_DOWN && vt.getYVelocity() > 0) {
					handle = true;
				} else {
					handle = false;
				}
				break;
			case MIDDLE:
				isReadyForPullDown = false;
				isReadyForPullUp = false;
				handle = false;
				break;
			case BOTTOM:
				isReadyForPullUp = true;
				isReadyForPullDown = false;
				if (gesture == GestureSlideExt.GESTURE_UP && vt.getYVelocity() < 0) {
					handle = true;
				} else {
					handle = false;
				}
			default:
				break;
			}
			break;
		case MotionEvent.ACTION_UP:
			handle = false;
			break;
		default:
			handle = false;
		}
		return handle;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			if (mCurrentState != RefreshStatusEnum.REFRESHING) {
				if ((gesture == GestureSlideExt.GESTURE_DOWN) && mCurrentMode.canPullDown() && isReadyForPullDown) {
					adjustHeader(event);
				} else if ((gesture == GestureSlideExt.GESTURE_UP) && mCurrentMode.canPullUp() && isReadyForPullUp) {
					adjustFooter(event);
				} else {
					setStatusClickToRefresh();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			gesture = 0;
			// vt.recycle();
			if (mCurrentState != RefreshStatusEnum.REFRESHING) {
				switch (mCurrentState) {
				case CLICK_TO_REFRESH:
					setStatusClickToRefresh();
					break;
				case RELEASE_TO_REFRESH:
					if (mCurrentMode.canPullDown() && mCurrentPosition == CurrentPositionEnum.TOP) {
						onRefreshHeader();
					} else if (mCurrentMode.canPullUp() && mCurrentPosition == CurrentPositionEnum.BOTTOM) {
						onRefreshFooter();
					}
					break;
				case PULL_TO_REFRESH:
					setStatusClickToRefresh();
					break;
				default:
					break;
				}
			}
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 准备刷新
	 */
	private void onRefreshBegin() {
		setStatusRefreshing();
	}

	/**
	 * 刷新
	 */
	public final void onRefreshHeader() {
		if (mOnRefreshHeaderListener != null) {
			onRefreshBegin();
			isOnRefresh = true;
			mOnRefreshHeaderListener.onRefreshHeader();
		}
	}

	public final void onRefreshFooter() {
		if (mOnRefreshFooterListener != null) {
			onRefreshBegin();
			isOnRefresh = true;
			mOnRefreshFooterListener.onRefreshFooter();
		}
	}

	/**
	 * 刷新结束
	 * 
	 * @param lastUpdatedText
	 *            上次更新信息，若为null，不显示
	 */
	public final void onRefreshComplete(CharSequence lastUpdatedText) {
		setLastUpdatedText(lastUpdatedText);
		onRefreshComplete();
	}

	/**
	 * 刷新结束，恢复View状态
	 */
	public final void onRefreshComplete() {
		setStatusClickToRefresh();
		isOnRefresh = false;
		// reSetScroll();
	}

	public T getPullableView() {
		return mPullableView;
	}

	protected void adjustHeader(MotionEvent event) {
		int mHistorySize = event.getHistorySize();
		if (mHistorySize == 0) {
			int y = Math.round(Math.min(mActionDownPointY - event.getY(), 0) / mResistance);
			if (mCurrentState != RefreshStatusEnum.PULL_TO_REFRESH && header.mOriginalHeight >= Math.abs(y)) {
				setStatusPullToRefresh();
			} else if (mCurrentState == RefreshStatusEnum.PULL_TO_REFRESH && header.mOriginalHeight < Math.abs(y)) {
				setStatusReleaseToRefresh();
			}
			scrollTo(0, y);
		} else {
			for (int i = 0; i < mHistorySize; i++) {
				int y = Math.round(Math.min(mActionDownPointY - event.getHistoricalY(i), 0) / mResistance);
				if (mCurrentState != RefreshStatusEnum.PULL_TO_REFRESH && header.mOriginalHeight >= Math.abs(y)) {
					setStatusPullToRefresh();
				} else if (mCurrentState == RefreshStatusEnum.PULL_TO_REFRESH && header.mOriginalHeight < Math.abs(y)) {
					setStatusReleaseToRefresh();
				}
				scrollTo(0, y);
			}
		}
	}

	protected void adjustFooter(MotionEvent event) {
		int mHistorySize = event.getHistorySize();
		if (mHistorySize == 0) {
			int y = Math.round(Math.max(mActionDownPointY - event.getY(), 0) / mResistance);
			if (mCurrentState != RefreshStatusEnum.PULL_TO_REFRESH && footer.mOriginalHeight >= Math.abs(y)) {
				setStatusPullToRefresh();
			} else if (mCurrentState == RefreshStatusEnum.PULL_TO_REFRESH && footer.mOriginalHeight < Math.abs(y)) {
				setStatusReleaseToRefresh();
			}
			scrollTo(0, y);
		} else {
			for (int i = 0; i < mHistorySize; i++) {
				int y = Math.round(Math.max(mActionDownPointY - event.getHistoricalY(i), 0) / mResistance);
				if (mCurrentState != RefreshStatusEnum.PULL_TO_REFRESH && footer.mOriginalHeight >= Math.abs(y)) {
					setStatusPullToRefresh();
				} else if (mCurrentState == RefreshStatusEnum.PULL_TO_REFRESH && footer.mOriginalHeight < Math.abs(y)) {
					setStatusReleaseToRefresh();
				}
				scrollTo(0, y);
			}
		}
	}

	/**
	 * 设置上次更新信息
	 * 
	 * @param lastUpdatedText
	 *            上次更新信息，若为null，不显示
	 */
	public final void setLastUpdatedText(CharSequence lastUpdatedText) {
		if (null != header)
			header.setLastUpdatedText(lastUpdatedText);
	}

	/**
	 * 设置刷新事件器
	 * 
	 * @param onRefreshListener
	 */
	public final void setOnRefreshHeaderListener(OnRefreshHeaderListener onRefreshHeaderListener) {
		mOnRefreshHeaderListener = onRefreshHeaderListener;
	}

	public final void setOnRefreshFooterListener(OnRefreshFooterListener onRefreshFooterListener) {
		mOnRefreshFooterListener = onRefreshFooterListener;
	}

	protected FlipLoadingLayout createLoadingLayout(Context context, FlipLoadingLayoutMode mode) {
		FlipLoadingLayout flip = new FlipLoadingLayout(context, mode);
		measureView(flip);
		flip.initOriginalProperties();
		return flip;
	}

	private void addRefreshableView(Context context, T refreshableView) {
		FrameLayout frameLayout = new FrameLayout(context);
		frameLayout.addView(refreshableView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addViewInternal(frameLayout, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
	}

	private int gesture;
	private GestureDetector gestureDetector;

	protected void init(Context context) {
		setOrientation(LinearLayout.VERTICAL);
		mPullableView = createPullableView(context);
		addRefreshableView(context, mPullableView);
		header = createLoadingLayout(context, FlipLoadingLayoutMode.HEADER);
		footer = createLoadingLayout(context, FlipLoadingLayoutMode.FOOTER);
		initPadding = new Rect();
		initPadding.top = getPaddingTop();
		initPadding.bottom = getPaddingBottom();
		initPadding.left = getPaddingLeft();
		initPadding.right = getPaddingRight();
		updateUIForMode(context);
		gestureDetector = new GestureSlideExt(context, new GestureSlideExt.OnGestureResult() {
			@Override
			public void onGestureResult(int direction) {
				gesture = direction;
				// 业务逻辑处理
				// switch (direction) {
				// case GestureSlideExt.GESTURE_UP:
				// show("滑屏手势方向：GESTURE_UP(向上)");
				// break;
				// case GestureSlideExt.GESTURE_RIGHT:
				// show("滑屏手势方向：GESTURE_RIGHT(向右)");
				// break;
				// case GestureSlideExt.GESTURE_DOWN:
				// show("滑屏手势方向：GESTURE_DOWN(向下)");
				// break;
				// case GestureSlideExt.GESTURE_LEFT:
				// show("滑屏手势方向：GESTURE_LEFT(向左)");
				// break;
				// }
			}
		}).Buile();

	}

	/**
	 * 测量View的宽度和高度
	 * 
	 * @param child
	 */
	protected void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	protected void setStatusClickToRefresh() {
		// reSetScroll();
		smoothScrollTo(0, SMOOTH_SCROLL_LONG_DURATION_MS);
		if (mCurrentState != RefreshStatusEnum.CLICK_TO_REFRESH) {
			switch (mCurrentPosition) {
			case TOP:
				header.setStatusClickToRefresh();
				break;
			case BOTTOM:
				footer.setStatusClickToRefresh();
				break;
			default:
				break;
			}
			mCurrentState = RefreshStatusEnum.CLICK_TO_REFRESH;
		}
	}

	protected void setStatusPullToRefresh() {
		if (mCurrentState != RefreshStatusEnum.PULL_TO_REFRESH) {
			switch (mCurrentPosition) {
			case TOP:
				header.setStatusPullToRefresh();
				break;
			case BOTTOM:
				footer.setStatusPullToRefresh();
				break;
			default:
				break;
			}
			mCurrentState = RefreshStatusEnum.PULL_TO_REFRESH;
		}
	}

	protected void setStatusReleaseToRefresh() {
		if (mCurrentState != RefreshStatusEnum.RELEASE_TO_REFRESH) {
			switch (mCurrentPosition) {
			case TOP:
				header.setStatusReleaseToRefresh();
				break;
			case BOTTOM:
				footer.setStatusReleaseToRefresh();
				break;
			default:
				break;
			}
			mCurrentState = RefreshStatusEnum.RELEASE_TO_REFRESH;
		}
	}

	public void refreshByHeader() {
		isOnRefresh = true;
		setmCurrentPosition(CurrentPositionEnum.TOP);
		setStatusRefreshing();
	}

	protected void setStatusRefreshing() {
		// reSetScroll();
		if (mCurrentState != RefreshStatusEnum.REFRESHING) {
			switch (mCurrentPosition) {
			case TOP:
				header.setStatusRefreshing();
				break;
			case BOTTOM:
				footer.setStatusRefreshing();
				break;
			default:
				break;
			}
			mCurrentState = RefreshStatusEnum.REFRESHING;
		}
		smoothScrollTo(mCurrentPosition == CurrentPositionEnum.TOP ? -header.mOriginalHeight : footer.mOriginalHeight, SMOOTH_SCROLL_LONG_DURATION_MS);
	}

	/**
	 * Used internally for adding view. Need because we override addView to
	 * pass-through to the Refreshable View
	 */
	protected final void addViewInternal(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
	}

	/**
	 * Used internally for adding view. Need because we override addView to
	 * pass-through to the Refreshable View
	 */
	protected final void addViewInternal(View child, ViewGroup.LayoutParams params) {
		super.addView(child, -1, params);
	}

	protected void updateUIForMode(Context context) {
		if (null != header && this == header.getParent()) {
			removeView(header);
		}
		if (mCurrentMode.canPullDown()) {
			header = createLoadingLayout(context, FlipLoadingLayoutMode.HEADER);
			addViewInternal(header, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		}

		// Remove Footer, and then add Footer Loading View again if needed
		if (null != footer && mPullableView == footer.getParent()) {
			removeView(footer);
		}
		if (mCurrentMode.canPullUp()) {
			footer = createLoadingLayout(context, FlipLoadingLayoutMode.FOOTER);
			addViewInternal(footer, -1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		}
		refreshLoadingViewsHeight();
	}

	private void refreshLoadingViewsHeight() {
		// Hide Loading Views
		switch (mCurrentMode) {
		case BOTH_NONE:
			setPadding(initPadding.left, initPadding.top, initPadding.right, initPadding.bottom);
			break;
		case BOTH:
			setPadding(initPadding.left, -header.mOriginalHeight, initPadding.right, -footer.mOriginalHeight);
			break;
		case PULL_UP_TO_REFRESH:
			setPadding(initPadding.left, initPadding.top, initPadding.right, -footer.mOriginalHeight);
			break;
		case PULL_DOWN_TO_REFRESH:
			setPadding(initPadding.left, -header.mOriginalHeight, initPadding.right, initPadding.bottom);
			break;
		default:

			break;
		}
	}

	/**
	 * Smooth Scroll to Y position using the specific duration
	 * 
	 * @param y
	 *            - Y position to scroll to
	 * @param duration
	 *            - Duration of animation in milliseconds
	 */
	private final void smoothScrollTo(int y, long duration) {
		if (null != mCurrentSmoothScrollRunnable) {
			mCurrentSmoothScrollRunnable.stop();
		}

		if (getScrollY() != y) {
			if (null == mScrollAnimationInterpolator) {
				// Default interpolator is a Decelerate Interpolator
				mScrollAnimationInterpolator = new DecelerateInterpolator();
			}
			mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(getScrollY(), y, duration);
			post(mCurrentSmoothScrollRunnable);
		}
	}

	final class SmoothScrollRunnable implements Runnable {

		static final int ANIMATION_DELAY = 10;

		private final Interpolator mInterpolator;
		private final int mScrollToY;
		private final int mScrollFromY;
		private final long mDuration;

		private boolean mContinueRunning = true;
		private long mStartTime = -1;
		private int mCurrentY = -1;

		public SmoothScrollRunnable(int fromY, int toY, long duration) {
			mScrollFromY = fromY;
			mScrollToY = toY;
			mInterpolator = mScrollAnimationInterpolator;
			mDuration = duration;
		}

		@Override
		public void run() {

			/**
			 * Only set mStartTime if this is the first time we're starting,
			 * else actually calculate the Y delta
			 */
			if (mStartTime == -1) {
				mStartTime = System.currentTimeMillis();
			} else {

				/**
				 * We do do all calculations in long to reduce software float
				 * calculations. We use 1000 as it gives us good accuracy and
				 * small rounding errors
				 */
				long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / mDuration;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

				final int deltaY = Math.round((mScrollFromY - mScrollToY) * mInterpolator.getInterpolation(normalizedTime / 1000f));
				mCurrentY = mScrollFromY - deltaY;
				scrollTo(0, mCurrentY);
			}

			// If we're not at the target Y, keep going...
			if (mContinueRunning && mScrollToY != mCurrentY) {
				// if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
				// SDK16.postOnAnimation(PullToRefreshBase.this, this);
				// } else {
				postDelayed(this, ANIMATION_DELAY);
				// }
			}
		}

		public void stop() {
			mContinueRunning = false;
			removeCallbacks(this);
		}
	}

	protected abstract CurrentPositionEnum judgeCurrentPosition();

	protected abstract T createPullableView(Context context);
}