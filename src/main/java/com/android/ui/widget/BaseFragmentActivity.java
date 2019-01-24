package com.android.ui.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.BaseApplication;
import com.android.master.R;
import com.android.utils.Constants;

import java.util.Set;

/**
 * @description Activity基类,可在引用项目中继承该类并添加相应功能。
 * 
 * @author wangyang
 * @create 2014-2-14 下午03:25:49
 * @version 1.0.0
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements OnClickListener {
	/**
	 * Activity结束时是否滑动退出
	 */
	private boolean isRightOut = true;

	protected SharedPreferences mSp;

	private LinearLayout linearLayout;
	private FrameLayout frameLeft, frameRight;
	private TextView tvLeft, tvContent, tvRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSp = getSharedPreferences(Constants.FILE_CACHE_NAME, Context.MODE_PRIVATE);
		setContentView();
		findViewById();
		init();
		setListeners();
		registerReceivers();
		if (getApplication() instanceof BaseApplication)
			((BaseApplication) getApplication()).getActivities().add(this);
	}

	protected void put(String key, boolean value) {
		Editor editor = mSp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	protected void put(String key, float value) {
		Editor editor = mSp.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	protected void put(String key, String value) {
		Editor editor = mSp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	protected void put(String key, int value) {
		Editor editor = mSp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	protected void put(String key, long value) {
		Editor editor = mSp.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	protected void put(String key, Set<String> value) {
		Editor editor = mSp.edit();
		editor.putStringSet(key, value);
		editor.commit();
	}

	/**
	 * 设置布局文件
	 */
	protected void setContentView() {
		setContentView(getContentViewId());
	}

	/**
	 * 设置布局文件Id
	 */
	protected abstract int getContentViewId();

	/**
	 * 查找控件
	 */
	protected abstract void findViewById();

	/**
	 * 设置事件监听
	 */
	protected void setListeners() {
	}

	/**
	 * 初始化Activity数据
	 */
	protected abstract void init();

	/**
	 * 注册广播接收者
	 */
	protected void registerReceivers() {
	};

	/**
	 * 取消广播接收
	 */
	protected void unRegisterReceivers() {
	};

	/**
	 * 是否滑动退出Activity
	 * 
	 * @param isRightOut
	 */
	public void setRightOut(boolean isRightOut) {
		this.isRightOut = isRightOut;
	}

	@Override
	protected void onDestroy() {
		unRegisterReceivers();
		super.onDestroy();
		if (getApplication() instanceof BaseApplication)
			((BaseApplication) getApplication()).getActivities().remove(this);
	}

	@Override
	public void finish() {
		super.finish();
		if (isRightOut) {
			overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
		}
	}

	@Override
	public void onClick(View v) {
	}

	public void doBack(View view) {
		finish();
	}

	/**
	 * 初始化标题栏
	 * 
	 * @author 李应锋
	 * @create 2015-3-2
	 * 
	 * @param imgLeft
	 *            左侧图片ID
	 * @param imgRight
	 *            右侧图片ID
	 * @param left
	 *            左侧文字
	 * @param content
	 *            中间文字
	 * @param right
	 *            右侧文字
	 */
	protected void initBaseTitle(int imgLeft, int imgRight, String left, String content, String right) {
		this.initBaseTitle(Color.GRAY, imgLeft, imgRight, left, content, right, Color.BLACK, Color.BLACK, Color.BLACK);
	}

	/**
	 * 初始化标题栏
	 * 
	 * @author 李应锋
	 * @create 2015-3-2
	 * 
	 * @param bgColor
	 *            标题栏背景色
	 * @param imgLeft
	 *            左侧图片ID
	 * @param imgRight
	 *            右侧图片ID
	 * @param left
	 *            左侧文字
	 * @param content
	 *            中间文字
	 * @param right
	 *            右侧文字
	 */
	protected void initBaseTitle(int bgColor, int imgLeft, int imgRight, String left, String content, String right) {
		this.initBaseTitle(bgColor, imgLeft, imgRight, left, content, right, Color.BLACK, Color.BLACK, Color.BLACK);
	}

	/**
	 * 初始化标题栏
	 * 
	 * @author 李应锋
	 * @create 2015-3-2
	 * 
	 * @param bgColor
	 *            标题栏背景色
	 * @param imgLeft
	 *            左侧图片ID
	 * @param imgRight
	 *            右侧图片ID
	 * @param left
	 *            左侧文字
	 * @param content
	 *            中间文字
	 * @param right
	 *            右侧文字
	 * @param leftColor
	 *            左侧文字颜色
	 * @param contentColor
	 *            中间文字颜色
	 * @param rightColor
	 *            右侧文字颜色
	 */
	protected void initBaseTitle(int bgColor, int imgLeft, int imgRight, String left, String content, String right, int leftColor, int contentColor, int rightColor) {
		if (linearLayout == null)
			linearLayout = (LinearLayout) findViewById(R.id.title_linear);
		linearLayout.setBackgroundColor(bgColor);
		if (frameLeft == null)
			frameLeft = (FrameLayout) findViewById(R.id.title_frame_left);
		if (imgLeft > 0 || left != null) {
			frameLeft.setVisibility(View.VISIBLE);
			frameLeft.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onClickTitleLeft(v);
				}
			});
		} else
			frameLeft.setVisibility(View.INVISIBLE);

		if (frameRight == null)
			frameRight = (FrameLayout) findViewById(R.id.title_frame_right);
		if (imgRight > 0 || right != null) {
			frameRight.setVisibility(View.VISIBLE);
			frameRight.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onClickTitleRight(v);
				}
			});
		} else
			frameRight.setVisibility(View.INVISIBLE);

		if (tvLeft == null)
			tvLeft = (TextView) findViewById(R.id.title_tv_left);
		if (imgLeft > 0)
			tvLeft.setBackgroundResource(imgLeft);
		if (left != null)
			tvLeft.setText(left);
		tvLeft.setTextColor(leftColor);
		if (tvContent == null)
			tvContent = (TextView) findViewById(R.id.title_tv_content);
		if (content != null)
			tvContent.setText(content);
		tvContent.setTextColor(contentColor);
		if (tvRight == null)
			tvRight = (TextView) findViewById(R.id.title_tv_right);
		if (imgRight > 0)
			tvRight.setBackgroundResource(imgRight);
		if (right != null)
			tvRight.setText(right);
		tvRight.setTextColor(rightColor);
	}

	/** 标题栏左侧点击事件 */
	protected void onClickTitleLeft(View v) {
		finish();
	}

	/** 标题栏右侧点击事件 */
	protected void onClickTitleRight(View v) {
	}
}
