package com.android.ui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.android.master.R;
import com.android.ui.widget.NetworkImageView.DisplayMethod;

/**
 * 广告组件
 * 
 * 布局文件： <com.up72.ui.widget.BannerView android:id="@+id/group"
 * android:layout_width="match_parent" android:layout_height="100dp" />
 * 
 * JAVA类中： BannerView bannerView = (BannerView)
 * headView.findViewById(R.id.group); for (int i = 0; i < 5; i++) {
 * mImages.add(TestData.getNetImageUrl()); } bannerView.setData(mImages,
 * R.drawable.home_viewpager_default); bannerView.setOnBannerClickListener(new
 * OnBannerClickListener() { // @Override public void onClick(int position) {
 * Toast.show(getActivity(), "你点了一下:" + position); } });
 * 
 * @author wangyang
 * @create 2015-2-27
 */
public class BannerView extends LinearLayout {

	private int time = 5;// 广告切换时间间隔，单位秒
	private Context mContext;
	private View childView;
	private Up72ViewPager viewPager;
	private LinearLayout linearLayout;

	private List<? extends Object> ImageData;
	private ImageView[] disksArray;// 放置圆点的数组
	private int oldPosition = 0;// 上一个页面位置
	private int currentPosition = 0; // 当前页面位置
	private ScheduledExecutorService scheduledExecutor;

	private OnBannerClickListener onClickListener;
	private float x, y;
	private int defaultBanner;
	private int disksSelected = R.drawable.screenview_seekpoint_selected;
	private int disksNormal = R.drawable.screenview_seekpoint_normal;
	private int wh = 16;

	public BannerView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	public BannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public BannerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	private void init() {
		childView = LayoutInflater.from(mContext).inflate(R.layout.layout_banner, null);
		viewPager = (Up72ViewPager) childView.findViewById(R.id.viewpager);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		viewPager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						x = event.getX();
						y = event.getY();
						break;
					case MotionEvent.ACTION_UP :
						if (Math.abs(event.getX() - x) < 10 && Math.abs(event.getY() - y) < 10)
							if (onClickListener != null && ImageData != null && ImageData.size() > 0)
								onClickListener.onClick(currentPosition % ImageData.size());
						break;
				}
				return false;
			}
		});
		linearLayout = (LinearLayout) childView.findViewById(R.id.linearlayout);
		linearLayout.getBackground().setAlpha(30);
		addView(childView);
	}

	/**
	 * 设置数据(请将此方法放在最后面)
	 * 
	 * @param Images
	 *            图片数据，例如：List<"网络图片路径">,List<"Bitmap">,List<"资源图片ID">
	 */
	public void bindData(List<? extends Object> Images) {
		linearLayout.removeAllViews();
		ImageData = Images;
		viewPager.setAdapter(new ViewPagerAdapter());
		int size = ImageData.size();
		disksArray = new ImageView[size];
		for (int i = 0; i < size; i++) {
			ImageView imag = new ImageView(mContext);
			imag.setScaleType(ScaleType.CENTER);
			imag.setPadding(5, 5, 5, 5);
			imag.setLayoutParams(new LinearLayout.LayoutParams(wh, wh));
			if (i == 0) {
				imag.setImageResource(disksSelected);
			} else {
				imag.setImageResource(disksNormal);
			}
			disksArray[i] = imag;
			linearLayout.addView(imag);
		}

		if (size > 3 && time > 0) {
			currentPosition = 0;
			oldPosition = 0;
			startAnim();
		}
	}

	/**
	 * 默认图片
	 * 
	 * @param defaultBanner
	 */
	public void setDefaultImg(int defaultBanner) {
		if (defaultBanner > 0)
			this.defaultBanner = defaultBanner;
	}

	/**
	 * @param resSelected
	 *            小圆点选中的图片资源
	 * @param resNormal
	 *            小圆点没选择的图片资源
	 */
	public void setDotsRes(int resSelected, int resNormal) {
		if (resSelected > 0)
			this.disksSelected = resSelected;
		if (resNormal > 0)
			this.disksNormal = resNormal;
	}

	/**
	 * 图片切换时间间隔（单位：秒）
	 * 
	 * @param spaceTime
	 */
	public void setSpaceTime(int spaceTime) {
		if (spaceTime > 0)
			time = spaceTime;
	}

	/**
	 * 小圆点的宽（高）
	 * 
	 * @param wh
	 */
	public void setDotsWH(int wh) {
		if (wh > 0)
			this.wh = wh;
	}

	public void setDotsGravity(int dotsGravity) {
		if (dotsGravity > 0) {
			FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) linearLayout.getLayoutParams();
			lp.gravity = dotsGravity;
		}
	}

	public void startAnim() {
		if (scheduledExecutor != null) {
			scheduledExecutor.shutdown();
		}
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		/**
		 * 参数一： 指定执行的任务 参数2： 第一次启动延迟启动时间 参数3： 下一次启动间隔时间 参数4: 时间单位
		 */
		scheduledExecutor.scheduleAtFixedRate(new PagerTask(), time, time, TimeUnit.SECONDS);
	}

	public void stopAnim() {
		if (scheduledExecutor != null) {
			scheduledExecutor.shutdown();
		}
	}

	// 完成切换页面
	private class PagerTask implements Runnable {
		@Override
		public void run() {
			currentPosition++;
			// if (currentPosition >= imageViews.size())
			// currentPosition = 0;
			mHandler.sendEmptyMessage(0);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0 :
					if (viewPager != null)
						viewPager.setCurrentItem(currentPosition);
					break;
			}
		}
	};

	class ViewPagerAdapter extends PagerAdapter {
		private ArrayList<NetworkImageView> imageViews = new ArrayList<NetworkImageView>();// 放置图片的列表

		@Override
		public int getCount() {
			return ImageData.size() > 3 ? Integer.MAX_VALUE : ImageData.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup viewpager, int position, Object object) {
			viewpager.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup viewpager, int position) {
			NetworkImageView imgone;
			position = position % ImageData.size();
			if (imageViews.size() > position)
				imgone = imageViews.get(position);
			else {
				imgone = new NetworkImageView(mContext);
				imgone.setDisplayMethod(DisplayMethod.Rectangle);
				imgone.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				imgone.setScaleType(ScaleType.FIT_XY);
				if (ImageData.get(position) instanceof String) {
					if (defaultBanner > 0)
						imgone.setImageUrl((String) ImageData.get(position), defaultBanner, defaultBanner);
					else
						imgone.setImageUrl((String) ImageData.get(position));
				} else if (ImageData.get(position) instanceof Integer) {
					imgone.setImageResource((Integer) ImageData.get(position));
				} else if (ImageData.get(position) instanceof Bitmap)
					imgone.setImageBitmap((Bitmap) ImageData.get(position));
				imageViews.add(position, imgone);
			}
			viewpager.addView(imgone);
			return imgone;
		}
	}

	class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			currentPosition = arg0;
			disksArray[currentPosition % ImageData.size()].setImageResource(disksSelected);
			disksArray[oldPosition % ImageData.size()].setImageResource(disksNormal);
			oldPosition = currentPosition;
		}
	}

	/**
	 * 设置广告点击事件监听
	 * 
	 * @param listener
	 */
	public void setOnBannerClickListener(OnBannerClickListener listener) {
		this.onClickListener = listener;
	}

	public interface OnBannerClickListener {
		/**
		 * @param position
		 *            点击时的位置，从0开始
		 */
		public void onClick(int position);
	}
}