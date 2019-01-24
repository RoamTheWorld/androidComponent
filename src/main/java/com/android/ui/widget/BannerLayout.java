package com.android.ui.widget;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.android.master.R;

/**
 * Banner广告组件 View,用例如 优酷首页，爱奇艺首页，乐视首页 等等可滑动可自动 广告图片新闻组件
 * 
 * 
 * 使用 在布局文件中定义 <RelativeLayout android:id="@+id/spy_relative"
 * android:layout_width="fill_parent" android:layout_height="140dp" >
 * 
 * <com.anjoyo.diebao.baseview.BannerLayout android:id="@+id/group"
 * android:layout_width="fill_parent" android:layout_height="140dp" >
 * 
 * </com.anjoyo.diebao.baseview.BannerLayout>
 * 
 * <LinearLayout android:id="@+id/bottomSeekPoint"
 * android:layout_width="fill_parent" android:layout_height="wrap_content"
 * android:layout_alignParentBottom="true" android:layout_marginBottom="10dp"
 * android:gravity="right" android:orientation="horizontal" > </LinearLayout>
 * </RelativeLayout>
 * 
 * 
 * 
 * 
 * BannerLayout BannerLayout group = (BannerLayout)
 * context.findViewById(R.id.group); List<Object> mImages = new
 * ArrayList<Object>(); LinearLayout mLinearLayout = (LinearLayout)
 * context.findViewById(R.id.bottomSeekPoint); for (int i = 0; i <
 * pictures.length; i++) { mImages.add(pictures[i].getPicUrl()); }
 * group.setImagViews(mImages, mLinearLayout, R.drawable.default_banner);
 * 
 * @author wangyang
 * @2014-2-26 上午10:44:49
 */
@SuppressLint("HandlerLeak")
public class BannerLayout extends ViewGroup {

	private Scroller scroller;
	private float mLastMotionX;
	private int currentScreenIndex = 0;

	private boolean autoScroll = true;// 判断是否 自动跳转

	private int scrollTime = 3 * 1000;

	private int currentWhat = 0;
	private List<ImageView> mImageViews;

	private Context context;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (autoScroll && currentWhat == msg.what) {
				if (getChildCount() > 0) {
					currentScreenIndex = (currentScreenIndex + 1) % getChildCount();
					scrollToScreen(currentScreenIndex);
				}
				if (autoScroll)
					handler.sendEmptyMessageDelayed(currentWhat, scrollTime);
			}
		}
	};

	public BannerLayout(Context context) {
		super(context);
		this.context = context;
		initView(context);

	}

	public BannerLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initView(context);
	}

	public BannerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView(context);
	}

	private void initView(final Context context) {
		this.scroller = new Scroller(context, new DecelerateInterpolator(2));// OvershootInterpolator(1.1f)
		handler.sendEmptyMessageDelayed(currentWhat, scrollTime);
	}

	// 设置 图片的 宽和高
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int maxHeight = -1;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
			maxHeight = Math.max(maxHeight, getChildAt(i).getMeasuredHeight());
		}
		maxHeight = Math.min(maxHeight, MeasureSpec.getSize(heightMeasureSpec));
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), maxHeight);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		final int count = getChildCount();
		int cLeft = 0;

		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() == View.GONE)
				continue;
			final int childWidth = child.getMeasuredWidth();
			child.layout(cLeft, 0, cLeft + childWidth, child.getMeasuredHeight());

			cLeft += childWidth;
		}
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), 0);
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (getChildCount() == 0)
			return false;
		final int action = ev.getAction();
		final float x = ev.getX();
		switch (action) {
			case MotionEvent.ACTION_DOWN :
				autoScroll = false;
				currentWhat++;
				mLastMotionX = x;
				if (!scroller.isFinished()) {
					scroller.abortAnimation();
				}
				return true;

			case MotionEvent.ACTION_MOVE :
				final int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;

				if ((0 == currentScreenIndex && deltaX < 0) || (getChildCount() - 1 == currentScreenIndex && deltaX > 0))
					scrollBy(deltaX / 4, 0);
				else
					scrollBy(deltaX, 0);
				final int screenWidth = getWidth();
				currentScreenIndex = (getScrollX() + (screenWidth / 2)) / screenWidth;
				return true;
			case MotionEvent.ACTION_UP :
				snapToDestination();

				if (!autoScroll) {
					autoScroll = true;
					handler.sendEmptyMessageDelayed(currentWhat, scrollTime);
				}
				break;
			case MotionEvent.ACTION_CANCEL :
				snapToDestination();
				if (!autoScroll) {
					autoScroll = true;
					handler.sendEmptyMessageDelayed(currentWhat, scrollTime);
				}

		}
		return false;
	}

	// 图片 移动
	private void scrollToScreen(int whichScreen) {
		int delta = 0;
		delta = whichScreen * getWidth() - getScrollX();
		for (int i = 0; i < getChildCount(); i++) {
			ImageView imag = mImageViews.get(i);
			if (i == whichScreen) {
				imag.setImageResource(R.drawable.screenview_seekpoint_normal);
			} else {
				imag.setImageResource(R.drawable.screenview_seekpoint_selected);
			}
		}
		scroller.startScroll(getScrollX(), 0, delta, 0, 1500);
		invalidate();
		currentScreenIndex = whichScreen;
	}

	private void snapToDestination() {
		final int x = getScrollX();
		final int screenWidth = getWidth();
		scrollToScreen((x + (screenWidth / 2)) / screenWidth);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	public void setImagViews(List<Object> Images, LinearLayout linearLayout, int default_banner) {
		for (int i = 0; i < Images.size(); i++) {
			NetworkImageView imgone = new NetworkImageView(context);
			imgone.setScaleType(ScaleType.FIT_XY);
			if (Images.get(i) instanceof String) {
				imgone.setImageUrl((String) Images.get(i), default_banner, default_banner);
			} else if (Images.get(i) instanceof Integer) {
				imgone.setImageResource((Integer) Images.get(i));
			} else if (Images.get(i) instanceof Bitmap)
				imgone.setImageBitmap((Bitmap) Images.get(i));
			addView(imgone);
		}
		mImageViews = new ArrayList<ImageView>();

		for (int i = 0; i < getChildCount(); i++) {
			ImageView imag = new ImageView(context);
			imag.setLayoutParams(new LinearLayout.LayoutParams(15, 15));
			if (i == 0) {
				imag.setImageResource(R.drawable.screenview_seekpoint_normal);
			} else {
				imag.setImageResource(R.drawable.screenview_seekpoint_selected);
			}
			mImageViews.add(imag);
			linearLayout.addView(imag);
		}
	}
}
