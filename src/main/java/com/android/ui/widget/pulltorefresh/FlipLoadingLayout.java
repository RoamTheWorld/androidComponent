package com.android.ui.widget.pulltorefresh;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.master.R;

public class FlipLoadingLayout extends LinearLayout {
	private ImageView mFlipLoadingImage;
	private ProgressBar mFlipLoadingProgress;
	private TextView mFlipLoadingText;
	private TextView mFlipLoadingSubText;
	private RotateAnimation mForwardFlipAnimation;
	private RotateAnimation mReverseFlipAnimation;
	private FlipLoadingLayoutMode mode;

	public int mOriginalHeight;
	public int mOriginalPaddingTop;
	public int mOriginalPaddingBottom;
	public int mOriginalPaddingLeft;
	public int mOriginalPaddingRight;

	private Interpolator mInterpolator = new LinearInterpolator();
	private int mDuration = 250;

	public enum FlipLoadingLayoutMode {
		HEADER, FOOTER
	}

	// public FlipLoadingLayout(Context context, FlipLoadingLayoutMode mode,
	// AttributeSet attrs, int defStyle) {
	// super(context, attrs, defStyle);
	// this.mode = mode;
	// initFlipLoadingLayout(context);
	// }

	public FlipLoadingLayout(Context context, FlipLoadingLayoutMode mode, AttributeSet attrs) {
		super(context, attrs);
		this.mode = mode;
		initFlipLoadingLayout(context);
	}

	public FlipLoadingLayout(Context context, FlipLoadingLayoutMode mode) {
		super(context);
		this.mode = mode;
		initFlipLoadingLayout(context);
	}

	public void adjustPadding(MotionEvent event, float mActionDownPointY, float resistance) {
		int mHistorySize = event.getHistorySize();
		switch (mode) {
		case HEADER:
			for (int i = 0; i < mHistorySize; i++)
				setPadding(mOriginalPaddingLeft, Math.abs(Math.round(Math.min(mActionDownPointY - event.getHistoricalY(i), 0) / resistance)), mOriginalPaddingRight, mOriginalPaddingBottom);
			break;
		case FOOTER:
			for (int i = 0; i < mHistorySize; i++)
				setPadding(mOriginalPaddingLeft, mOriginalPaddingTop, mOriginalPaddingRight, Math.round(Math.max(mActionDownPointY - event.getHistoricalY(i), 0) / resistance));
			break;
		default:
			break;
		}
	}

	// public void resetPadding() {
	// this.setPadding(mOriginalPaddingLeft, mOriginalPaddingTop,
	// mOriginalPaddingRight, mOriginalPaddingBottom);
	// }

	public final void setStatusClickToRefresh() {
		// resetPadding();
		switch (mode) {
		case HEADER:
			mFlipLoadingImage.setImageResource(R.drawable.fliploading_up_arrow);
			break;
		case FOOTER:
			mFlipLoadingImage.setImageResource(R.drawable.fliploading_up_arrow);
			break;
		default:
			break;
		}
		mFlipLoadingImage.clearAnimation();
		mFlipLoadingImage.setVisibility(View.GONE);
		mFlipLoadingProgress.setVisibility(View.GONE);
		mFlipLoadingText.setText(R.string.pull_to_refresh_click_tips);
	}

	public final void setStatusPullToRefresh() {
		mFlipLoadingImage.setVisibility(View.VISIBLE);
		mFlipLoadingProgress.setVisibility(View.GONE);
		switch (mode) {
		case HEADER:
			mFlipLoadingImage.clearAnimation();
			mFlipLoadingImage.startAnimation(mForwardFlipAnimation);
			mFlipLoadingText.setText(R.string.pull_to_refresh_pull_down_tips);
			break;
		case FOOTER:
			mFlipLoadingImage.clearAnimation();
			mFlipLoadingImage.startAnimation(mReverseFlipAnimation);
			mFlipLoadingText.setText(R.string.pull_to_refresh_pull_up_tips);
			break;
		default:
			break;
		}
	}

	public final void setStatusReleaseToRefresh() {
		mFlipLoadingImage.setVisibility(View.VISIBLE);
		mFlipLoadingProgress.setVisibility(View.GONE);
		mFlipLoadingText.setText(R.string.pull_to_refresh_release_tips);
		switch (mode) {
		case HEADER:
			mFlipLoadingImage.clearAnimation();
			mFlipLoadingImage.startAnimation(mReverseFlipAnimation);
			break;
		case FOOTER:
			mFlipLoadingImage.clearAnimation();
			mFlipLoadingImage.startAnimation(mForwardFlipAnimation);
		default:
			break;
		}
	}

	public final void setStatusRefreshing() {
		// resetPadding();
		mFlipLoadingImage.setVisibility(View.GONE);
		mFlipLoadingImage.setImageDrawable(null);
		mFlipLoadingProgress.setVisibility(View.VISIBLE);
		mFlipLoadingText.setText(R.string.pull_to_refresh_refreshing_tips);
	}

	public final void setLastUpdatedText(CharSequence lastUpdatedText) {
		if (lastUpdatedText == null)
			mFlipLoadingSubText.setVisibility(View.GONE);
		mFlipLoadingSubText.setVisibility(View.VISIBLE);
		mFlipLoadingSubText.setText(lastUpdatedText);
	}

	public void setmFlipLoadingImage(int mFlipLoadingImage) {
		this.mFlipLoadingImage.setImageResource(mFlipLoadingImage);
	}

	public void setFlipLoadingImage(Drawable mFlipLoadingDrawable) {
		this.mFlipLoadingImage.setImageDrawable(mFlipLoadingDrawable);
	}

	public void setmFlipLoadingProgress(ProgressBar mFlipLoadingProgress) {
		this.mFlipLoadingProgress = mFlipLoadingProgress;
	}

	public void setmFlipLoadingText(CharSequence mFlipLoadingText) {
		this.mFlipLoadingText.setText(mFlipLoadingText);
	}

	public void setmInterpolator(Interpolator mInterpolator) {
		this.mInterpolator = mInterpolator;
	}

	public void setmDuration(int mDuration) {
		this.mDuration = mDuration;
	}

	public FlipLoadingLayoutMode getMode() {
		return mode;
	}

	private void initFlipLoadingLayout(Context context) {
		mForwardFlipAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mForwardFlipAnimation.setInterpolator(mInterpolator);
		mForwardFlipAnimation.setDuration(mDuration);
		mForwardFlipAnimation.setFillAfter(true);
		mReverseFlipAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(mInterpolator);
		mReverseFlipAnimation.setDuration(mDuration);
		mReverseFlipAnimation.setFillAfter(true);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout flipLoading = (RelativeLayout) inflater.inflate(R.layout.layout_fliploading, this, false);
		mFlipLoadingImage = (ImageView) flipLoading.findViewById(R.id.fliploading_image);
		mFlipLoadingProgress = (ProgressBar) flipLoading.findViewById(R.id.fliploading_progress);
		mFlipLoadingText = (TextView) flipLoading.findViewById(R.id.fliploading_text);
		mFlipLoadingSubText = (TextView) flipLoading.findViewById(R.id.fliploading_subtext);

		switch (mode) {
		case HEADER:
			mFlipLoadingImage.setImageResource(R.drawable.fliploading_up_arrow);
			mFlipLoadingText.setText(R.string.pull_to_refresh_pull_down_tips);
			break;
		case FOOTER:
			mFlipLoadingImage.setImageResource(R.drawable.fliploading_up_arrow);
			mFlipLoadingText.setText(R.string.pull_to_refresh_pull_up_tips);
			break;
		default:
			break;
		}

		addView(flipLoading);
	}

	public void initOriginalProperties() {
		mOriginalHeight = getMeasuredHeight();
		mOriginalPaddingBottom = getPaddingBottom();
		mOriginalPaddingTop = getPaddingTop();
		mOriginalPaddingLeft = getPaddingLeft();
		mOriginalPaddingRight = getPaddingRight();

	}

}