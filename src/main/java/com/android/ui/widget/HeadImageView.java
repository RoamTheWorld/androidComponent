package com.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.android.master.R;
import com.android.ui.contact.BaseUser;

/**
 * 
 * 头像图片 Copyright: 版权所有 (c) 2002 - 2003 Company: 北京开拓明天科技有限公司
 * 
 * @author wangyang
 * @version 1.0.0
 * @create 2014-11-13
 */
public class HeadImageView extends NetworkImageView {
	private int sex = BaseUser.MAN;

	public HeadImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public HeadImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HeadImageView(Context context) {
		super(context);
	}

	/**
	 * 根据性别设置图片加载失败显示
	 */
	private void setErrorImage() {
		if (sex == BaseUser.MAN) {
			setDefalutImageRes(R.drawable.man);
		} else {
			setDefalutImageRes(R.drawable.woman);
		}
	}

	/**
	 * 设置性别 1男 0女
	 * 
	 * @param sex
	 */
	public void setSex(int sex) {
		this.sex = sex;
		if (getDrawable() == null)
			setErrorImage();
	}

}
