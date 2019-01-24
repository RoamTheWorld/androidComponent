package com.android.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.master.R;
import com.android.utils.Constants;
import com.android.utils.NetWorkUtil;
import com.android.utils.StringUtil;
import com.android.utils.image.ImageListener;
import com.android.utils.image.ImageUtil;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * 
 * 可以加载网络图片、设置圆角的ImageView 
 * 
 * @author wangyang
 * @version 1.0.0
 * @create 2014-11-13
 */
public class NetworkImageView extends ImageView {
	// private String TAG = "NetworkImageView";

	protected Resources mResources;
	protected Context mContext;
	protected LoadMethod loadMethod = LoadMethod.Universal;
	/** 是否是圆角的 */
	protected boolean isRadius = false;
	/** 图片圆角角度 */
	protected int radius;

	protected int width;

	protected int height;
	/** 默认图片资源 */
	protected int defalutImageRes;
	/** 显示图片的方式(圆形、圆角) */
	protected DisplayMethod displayMethod = DisplayMethod.Circle;
	/** 圆角比例 */
	protected float circularScale;
	/** 是否是仅在wifi下加载网络图片 */
	protected boolean isOnlyWifi = false;

	public NetworkImageView(Context context) {
		super(context);
		this.mContext = context;
		this.mResources = context.getResources();
		this.loadMethod = LoadMethod.Universal;
	}

	public NetworkImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		this.mResources = context.getResources();
		loadAttribute(context, attrs);
	}

	public NetworkImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("Recycle")
	private void loadAttribute(Context context, AttributeSet attrs) {

		// 加载属性
		TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.NetworkImageView);

		// 获取加载方式
		loadMethod = LoadMethod.value(arr.getInt(R.styleable.NetworkImageView_load_method, LoadMethod.Universal.value));

		// 获取显示方式
		displayMethod = DisplayMethod.value(arr.getInt(R.styleable.NetworkImageView_display_method, DisplayMethod.Circle.value));

		if (displayMethod.equals(DisplayMethod.Circle)) {
			circularScale = 1;
		} else {
			circularScale = arr.getFraction(R.styleable.NetworkImageView_circular_scale, 1, 1, 0);
		}

		// Log.i(TAG, "circularScale    :" + circularScale);

	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		if (bm != null) {
			switch (displayMethod) {
			case Circle:
				super.setImageBitmap(ImageUtil.createFramedPhoto(bm, circularScale));
				break;
			case Rectangle:
				super.setImageBitmap(ImageUtil.CreateRoundedCornerBitmap(bm, circularScale));
				break;
			case Square:
				super.setImageBitmap(ImageUtil.createFramedPhoto(bm, circularScale));
				break;
			}
		}
	}

	/**
	 * 设置网络图片的url
	 */
	public void setImageUrl(String url) {
		setImageUrl(url, Config.RGB_565);
	}
	
	public void setImageUrl(String url,ImageListener listener) {
		setImageUrl(url,listener, Config.RGB_565);
	}
	
	public void setImageUrl(String url,int defalutImageRes) {
		setImageUrl(url,defalutImageRes,Config.RGB_565);
	}

	public void setImageUrl(String url, Config config) {
		setImageUrl(url,null,0, 0, 0, config, ImageScaleType.IN_SAMPLE_POWER_OF_2);
	}
	
	public void setImageUrl(String url,ImageListener listener, Config config) {
		setImageUrl(url,listener,0, 0, 0, config, ImageScaleType.IN_SAMPLE_POWER_OF_2);
	}
	
	public void setImageUrl(String url,int defalutImageRes, Config config) {
		setImageUrl(url,null,defalutImageRes, 0, 0, config, ImageScaleType.IN_SAMPLE_POWER_OF_2);
	}

	public void setImageUrl(String url,int defalutImageRes, ImageScaleType imageScaleType) {
		setImageUrl(url,null,defalutImageRes, 0, 0, Config.RGB_565, imageScaleType);
	}

	public void setImageUrl(String url, Config config, ImageScaleType imageScaleType) {
		setImageUrl(url,null,0, 0, 0, config, imageScaleType);
	}
	
	public void setImageUrl(String url, int failureImageRes, int loadingImageRes) {
		setImageUrl(url,null,0, failureImageRes, loadingImageRes, Config.RGB_565, ImageScaleType.IN_SAMPLE_POWER_OF_2);
	}
	
	public void setImageUrl(String url, int defalutImageRes, int failureImageRes, int loadingImageRes) {
		setImageUrl(url,null,defalutImageRes, failureImageRes, loadingImageRes, Config.RGB_565, ImageScaleType.IN_SAMPLE_POWER_OF_2);
	}

	/**
	 * 设置网络图片的url
	 * 
	 * @param url
	 *            图片url
	 * @param failureImageRes
	 *            加载失败时显示的图片
	 * @param loadingImageRes
	 *            加载中显示的图片
	 */
	public void setImageUrl(String url,ImageListener listener, int defalutImageRes,int failureImageRes, int loadingImageRes, Config config, ImageScaleType imageScaleType) {
		setTag(url);
		
		this.defalutImageRes = defalutImageRes;
		if (getTag() != null && !url.equals(getTag().toString())) {
			setImageResource(defalutImageRes);
			return;
		}

		if (isOnlyWifi && !NetWorkUtil.isConnectWifi(mContext)) {
			setImageResource(defalutImageRes);
		} else {
			if (StringUtil.isEmpty(url)) {
				setImageResource(defalutImageRes);
			} else {
				String[] urls = url.split(Constants.SPLIT_REGULAR);
				url = urls[0];
				url = StringUtil.deleteEscape(url);
				if(defalutImageRes>0)
					setImageResource(defalutImageRes);
				ImageUtil.DisplayImage(mContext, this, loadMethod, url,listener, failureImageRes, loadingImageRes, config, imageScaleType);
			}
		}
	}

	@Override
	public void setImageResource(int resId) {
		if (resId > 0) {
			Bitmap bitmap = ImageUtil.decodeResource(mContext, resId);
			setImageBitmap(bitmap);
		}
	}

	public void setLoadMethod(LoadMethod loadMethod) {
		// Log.i(TAG, "loadMethod    :" + loadMethod);
		this.loadMethod = loadMethod;
	}

	/** 设置图片是否是圆角 */
	public void setRadius(boolean isRadius) {
		this.isRadius = isRadius;
	}

	/** 设置图片圆角角度 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/** 设置默认图片资源 */
	public void setDefalutImageRes(int defalutImageRes) {
		this.defalutImageRes = defalutImageRes;
		setImageResource(defalutImageRes);
	}

	/** 设置图片显示方式 */
	public void setDisplayMethod(DisplayMethod displayMethod) {
		if (displayMethod.equals(DisplayMethod.Circle)) {
			circularScale = 1;
		} else {
			circularScale = 0;
		}
		this.displayMethod = displayMethod;
	}

	public void setOnlyWifi(boolean isOnlyWifi) {
		this.isOnlyWifi = isOnlyWifi;
	}

	public void setCircularScale(float circularScale) {
		this.circularScale = circularScale;
	}

	/** 加载图片的方式的枚举 */
	public enum LoadMethod {
		Universal(1), Glide(2);
		private int value;

		private LoadMethod(int value) {
			this.value = value;
		}

		public static LoadMethod value(int value) {
			if (Universal.value == value)
				return Universal;
			else if (Glide.value == value)
				return Glide;
			return null;
		}
	}

	/** 设置图片显示方式的枚举 */
	public enum DisplayMethod {
		/** 圆形 */
		Circle(0),
		/** 保持图片的比例 */
		Rectangle(1),
		/** 画为正方形 */
		Square(2);
		private int value;

		DisplayMethod(int value) {
			this.value = value;
		}

		public static DisplayMethod value(int value) {
			if (Circle.value == value)
				return Circle;
			else if (Rectangle.value == value)
				return Rectangle;
			else if (Square.value == value)
				return Square;
			return null;
		}
	}

}
