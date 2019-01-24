package com.android.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.master.R;
import com.android.ui.contact.BaseUser;
import com.android.utils.StringUtil;
import com.android.utils.image.ImageUtil;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * @description 头像
 * 
 * @author wangyang
 * @create 2014-7-11 下午1:36:34
 * @version 1.0.0
 */
public class UniversalHeadImageView extends ImageView {

	private Context mContext;

	private Resources mResources;

	private int radius;

	private int sex = BaseUser.MAN;

	private boolean isRadius = true;

	private int defalutImageRes;

	private boolean isBitmap = false;

	/**
	 * 头像显示级别,0代表大图，1代表中图，2代表小图
	 */
	private int level = 2;

	public UniversalHeadImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public UniversalHeadImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();

	}

	public UniversalHeadImageView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	@Override
	public void setImageBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			if (isRadius) {
				isBitmap = true;
				super.setImageBitmap(ImageUtil.createFramedPhoto(bitmap, radius));
			} else {
				super.setImageBitmap(bitmap);
			}
		} else {
			setImageResource(defalutImageRes);
		}
	}

	@Override
	public void setImageResource(int resId) {
		Bitmap bitmap = BitmapFactory.decodeResource(mResources, resId);
		setImageBitmap(bitmap);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		if (isRadius && !isBitmap) {
			BitmapDrawable bd = (BitmapDrawable) drawable;
			Bitmap bitmap = bd.getBitmap();
			setImageBitmap(bitmap);
		} else {
			super.setImageDrawable(drawable);
			isBitmap = false;
		}
	}

	/**
	 * 设置图片
	 * 
	 * @author 谭杰
	 * @create 2014-7-11 下午2:11:06
	 * @param url
	 */
	public void setImageUrl(String imageUrl) {
		if (StringUtil.isEmpty(imageUrl)) {
			setImageBitmap(null);
		} else {
			String[] urls = imageUrl.split(",");
			if (urls.length > level) {
				imageUrl = urls[level];
			} else {
				imageUrl = urls[0];
			}
			imageUrl = StringUtil.deleteEscape(imageUrl);
			ImageUtil.getUniversalImageLoader(mContext).displayImage(imageUrl, this, ImageUtil.getOptions(defalutImageRes, 0,Config.RGB_565,ImageScaleType.IN_SAMPLE_POWER_OF_2));
		}
	}

	public void setRadius(boolean isRadius) {
		this.isRadius = isRadius;
	}

	/**
	 * 设置图片显示等级
	 * 
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * 设置性别
	 * 
	 * @param sex
	 */
	public void setSex(Integer sex) {
		this.sex = sex == null ? 0 : sex;
		init();
	}

	private void init() {
		mResources = mContext.getResources();
		radius = (int) mResources.getDimension(R.dimen.head_radius);
		if (sex == BaseUser.MAN) {
			defalutImageRes = R.drawable.man;
		} else {
			defalutImageRes = R.drawable.woman;
		}
	}
}
